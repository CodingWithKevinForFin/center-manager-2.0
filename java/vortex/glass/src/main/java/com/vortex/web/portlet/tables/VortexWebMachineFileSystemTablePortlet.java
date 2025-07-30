package com.vortex.web.portlet.tables;

import java.util.Date;
import java.util.List;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.chart.SeriesChartPortlet;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.utils.agg.DoubleAggregator;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataResponse;
import com.vortex.client.VortexClientFileSystem;

public class VortexWebMachineFileSystemTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentFileSystem, VortexClientFileSystem> {

	public VortexWebMachineFileSystemTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_FILE_SYSTEM);
		String[] ids = { HOST, FSID, REV, "total", "free", "name", NOW, MIID, "used", "pctused", "type", "data", "exps" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("File Systems");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(false, "Revision", REV, service.getNumberFormatter());
		table.addColumn(false, "Machine ID", MIID, service.getIdFormatter("MA-"));
		table.addColumn(false, "File System ID", FSID, service.getIdFormatter("FS-"));
		table.addColumn(true, "Name", "name", service.getBasicFormatter()).setWidth(200);
		table.addColumn(true, "FS Type", "type", service.getBasicFormatter()).setWidth(50);
		table.addColumn(false, "Update Time", NOW, service.getDateTimeWebCellFormatter());

		table.addColumn(true, "Used %", "pctused", service.getPercentFormatter());
		table.addColumn(true, "Free", "free", service.getMemoryFormatter());
		table.addColumn(true, "Used", "used", service.getMemoryFormatter());
		table.addColumn(true, "Tot. Space", "total", service.getMemoryFormatter());
		table.addColumn(true, "Expectation", "exps", service.getExpectationsStateFormatter());

		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebMachineFileSystemTablePortlet> {

		public Builder() {
			super(VortexWebMachineFileSystemTablePortlet.class);
		}

		public static final String ID = "fileSystemTablePortlet";

		@Override
		public VortexWebMachineFileSystemTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebMachineFileSystemTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "File Systems";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	protected Row createAndAddRow(VortexClientFileSystem node) {
		VortexAgentFileSystem a = node.getData();
		return addRow(node.getHostName(), a.getId(), a.getRevision(), a.getTotalSpace(), a.getFreeSpace(), a.getName(), a.getNow(), a.getMachineInstanceId(), calcUsed(a),
				calcPctUsed(a), a.getType(), node, node.getExpectationState());
	}

	@Override
	protected void updateRow(Row row, VortexClientFileSystem node) {
		VortexAgentFileSystem a = node.getData();
		row.put(HOST, node.getHostName());
		row.put(FSID, a.getId());
		row.put(REV, a.getRevision());
		row.put("total", a.getTotalSpace());
		row.put("free", a.getFreeSpace());
		row.put("name", a.getName());
		row.put(NOW, a.getNow());
		row.put(MIID, a.getMachineInstanceId());
		row.put("type", a.getType());
		row.put("used", calcUsed(a));
		row.put("pctused", calcPctUsed(a));
		row.put("exps", node.getExpectationState());
	}

	private double calcPctUsed(VortexAgentFileSystem a) {
		return 1d * calcUsed(a) / a.getTotalSpace();
	}
	private long calcUsed(VortexAgentFileSystem a) {
		return a.getTotalSpace() - a.getFreeSpace();
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		List<Row> selectedRows = table.getSelectedRows();
		if ("history".equals(action)) {
			LongSet ids = new LongSet();
			for (Row row : selectedRows) {
				ids.add(row.get("data", VortexClientFileSystem.class).getId());
			}
			VortexEyeQueryDataRequest request = nw(VortexEyeQueryDataRequest.class);
			request.setIds(ids.toLongArray());
			request.setType(VortexAgentEntity.TYPE_FILE_SYSTEM);
			service.sendRequestToBackend(getPortletId(), request);
		}
		super.onContextMenu(table, action);
	}
	@Override
	public BasicWebMenu createMenu(WebTable table) {
		BasicWebMenu menu = (BasicWebMenu) super.createMenu(table);
		menu.addChild(new BasicWebMenuLink("Show History", true, "history"));
		return menu;
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getAction();
		if (action instanceof VortexEyeQueryDataResponse) {
			VortexEyeQueryDataResponse qdr = (VortexEyeQueryDataResponse) action;
			SeriesChartPortlet usedchart = new SeriesChartPortlet(generateConfig());
			usedchart.setTitle("Disk Usage (%) ");
			//SeriesChartPortlet memchart = new SeriesChartPortlet(generateConfig());
			//memchart.setTitle("Memory Usage");
			//memchart.setStyle(SeriesChartPortlet.STYLE_LINE);
			usedchart.addOption(SeriesChartPortlet.OPTION_KEY_POSITION, SeriesChartPortlet.POSITION_BELOW);
			VH.sort(((VortexEyeQueryDataResponse) action).getData(), VortexAgentProcess.PID_NOW);
			DoubleAggregator da = new DoubleAggregator();
			for (VortexEntity row : ((VortexEyeQueryDataResponse) action).getData()) {
				VortexAgentFileSystem vbr = (VortexAgentFileSystem) row;
				String date = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL).format(new Date(vbr.getNow()));
				String id = SH.toString(vbr.getId());
				if (!usedchart.getSeries().contains(id)) {
					String label = vbr.getName() + "@" + service.getAgentManager().getAgentMachine(vbr.getMachineInstanceId()).getHostName();
					usedchart.setSeriesLabel(id, label);
					//memchart.setSeriesLabel(id, label);
				}
				double value = calcPctUsed(vbr) * 100;
				//da.add(vbr.getTotalSpace() / 1024 / 1024);
				//da.add(value);
				usedchart.addPoint(id, date, value);
			}
			usedchart.setStyle(usedchart.getSeries().size() == 1 ? SeriesChartPortlet.STYLE_AREA : SeriesChartPortlet.STYLE_LINE);
			usedchart.addOption(SeriesChartPortlet.OPTION_Y_MIN, 0);
			usedchart.addOption(SeriesChartPortlet.OPTION_Y_MAX, 100);
			//DividerPortlet div = new DividerPortlet(generateConfig(), false);
			//div.addChild(usedchart);
			//div.addChild(memchart);
			getManager().showDialog("Disk Usage (%)", usedchart);
		} else
			super.onBackendResponse(result);
	}
}
