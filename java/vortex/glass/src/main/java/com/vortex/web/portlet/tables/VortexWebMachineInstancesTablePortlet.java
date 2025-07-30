package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.chart.SeriesChartPortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.Formatter;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.utils.agg.DoubleAggregator;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedTable;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMachineRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientNetAddress;
import com.vortex.web.messages.VortexMachineIdInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebMachineFormPortlet;
import com.vortex.web.portlet.grids.VortexWebAddMachinesDialog;
import com.vortex.web.portlet.grids.VortexWebMachineFilesDialog;
import com.vortex.web.portlet.visuals.VortexTerminalPortlet;

public class VortexWebMachineInstancesTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentMachine, VortexClientMachine> {

	//private BasicPortletSocket masksendSocket;

	public class OverloadFormatter extends NumberWebCellFormatter {

		public OverloadFormatter(Formatter formatter) {
			super(formatter);
		}

		@Override
		public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
			Double val = (Double) value;
			if (val == null || val == 0)
				return;
			super.formatCellToHtml(value, sb, cellStyle);
			sb.append(" x");
			if (val > 1)
				cellStyle.append("_cna=red");

		}
	}
	final private BasicPortletSocket sendMiidSocket;

	public VortexWebMachineInstancesTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_MACHINE);
		// define the document schema
		Class[] clazz = { Long.class, Integer.class, String.class, String.class, Long.class, String.class, String.class, String.class, Integer.class, Double.class, Long.class,
				Long.class, Long.class, Long.class, Long.class, Object.class, Byte.class, Boolean.class, String.class, String.class, Map.class, Map.class };
		String[] ids = { MIID, REV, HOST, MUID, START, "osv", "osn", "osa", "cpu", "rawsys", "total", "used", "totals", "useds", NOW, "data", "exps", "running", "processUid",
				"aversion", "adetails", "mdata" };
		DerivedTable inner = new DerivedTable(new BasicDerivedCellParser(new JavaExpressionParser()), clazz, ids);
		inner.setTitle("Machines");

		inner.addDerivedColumn("pcttotal", "1d * used/total", null);
		inner.addDerivedColumn("swaptotal", "1d * used/totals", null);
		inner.addDerivedColumn("sys", "(1d * rawsys/cpu) > 1d ? 1d : (1d * rawsys/cpu)", null);
		inner.addDerivedColumn("syso", "(1d * rawsys/cpu) < 0d ? 0d : (1d * rawsys/cpu) ", null);
		//inner.addDerivedColumn("sys", "min(1d * rawsys/cpu,1d)");
		//inner.addDerivedColumn("syso", "max((1d * rawsys/cpu),0d)");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		MapWebCellFormatter<Boolean> isRunningFormatter = new MapWebCellFormatter<Boolean>(getManager().getTextFormatter());
		isRunningFormatter.addEntry(true, "Running", "_cna=", "Running");
		isRunningFormatter.addEntry(false, "Stale", "_cna=red", "Stale");
		isRunningFormatter.setDefaultWidth(70).lockFormatter();

		//define the view

		table.addColumn(true, "Expectation", "exps", service.getExpectationsStateFormatter());
		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(true, "Agent State", "running", isRunningFormatter);
		table.addColumn(false, "Revision", REV, service.getNumberFormatter());
		table.addColumn(false, "Machine ID", MIID, service.getIdFormatter("MA-"));
		table.addColumn(false, "Machine UID", MUID, service.getBasicFormatter());
		table.addColumn(false, "Update Time", NOW, service.getDateTimeWebCellFormatter());
		table.addColumn(false, "Agent ProcessUid", "processUid", service.getBasicFormatter());

		table.addColumn(true, "Start Time", START, service.getDateTimeWebCellFormatter());
		table.addColumn(true, "System Usage", "sys", service.getPercentFormatter());
		table.addColumn(true, "System Overload", "syso", new OverloadFormatter(getManager().getLocaleFormatter().getNumberFormatter(2)));
		table.addColumn(true, "Tot. Memory", "total", service.getMemoryFormatter());
		table.addColumn(true, "Used Memory", "pcttotal", service.getPercentFormatter());
		table.addColumn(true, "Tot. Swap Memory", "totals", service.getMemoryFormatter());
		table.addColumn(true, "Swap Memory", "swaptotal", service.getPercentFormatter());
		table.addColumn(true, "OS", "osn", service.getBasicFormatter()).setWidth(50);
		table.addColumn(true, "Architecture", "osa", service.getBasicFormatter()).setWidth(80);
		table.addColumn(true, "Cpu", "cpu", service.getNumberFormatter()).setWidth(35);
		table.addColumn(true, "OS Version", "osv", service.getBasicFormatter()).setWidth(250);
		table.addColumn(true, "Used Swap Memory", "useds", service.getMemoryFormatter());
		table.addColumn(true, "Used", "used", service.getMemoryFormatter());
		table.addColumn(false, "MetaData", "mdata", service.getBasicFormatter());
		table.addColumn(false, "Agent Version", "aversion", service.getBasicFormatter());
		table.addColumn(false, "Agent Details", "adetails", service.getBasicFormatter());
		table.addColumn(true, "System Load", "rawsys", service.getPercentFormatter());

		this.sendMiidSocket = addSocket(true, "sendMiid", "Send Machine ID", true, CH.s(VortexMachineIdInterPortletMessage.class), null);
		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);
		metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_MACHINE, "mdata");

	}
	public void onClosed() {
		super.onClosed();
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		super.onContextMenu(table, action);
		if ("delete".equals(action)) {
			List<VortexEyeRequest> requests = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexEyeManageMachineRequest request = nw(VortexEyeManageMachineRequest.class);
				VortexAgentMachine exp = nw(VortexAgentMachine.class);
				exp.setId(row.get(MIID, Long.class));
				exp.setMachineUid(row.get(MUID, String.class));
				exp.setRevision(VortexAgentEntity.REVISION_DONE);
				request.setMachine(exp);
				requests.add(request);
			}
			getManager().showDialog("Delete dataserver",
					new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), requests, "Delete Machine", "host.jpg").setIconToDelete());
		} else if ("metadata".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				VortexEyeManageMachineRequest request = nw(VortexEyeManageMachineRequest.class);
				VortexAgentMachine exp = nw(VortexAgentMachine.class);
				VortexClientMachine data = row.get("data", VortexClientMachine.class);
				getManager().showDialog("Edit Metadata", new VortexWebMachineFormPortlet(generateConfig(), data));
			}
		} else if ("history".equals(action)) {
			LongSet ids = new LongSet();
			for (Row row : table.getSelectedRows()) {
				ids.add(row.get("data", VortexClientMachine.class).getId());
			}
			VortexEyeQueryDataRequest request = nw(VortexEyeQueryDataRequest.class);
			request.setIds(ids.toLongArray());
			request.setType(VortexAgentEntity.TYPE_MACHINE);
			service.sendRequestToBackend(getPortletId(), request);
		} else if ("terminal".equals(action)) {
			for (Row row : getTable().getSelectedRows()) {
				String muid = row.get(MUID, String.class);
				String name = row.get(HOST, String.class);
				VortexTerminalPortlet vtp = new VortexTerminalPortlet(generateConfig(), getManager().getState().getWebState().getUser().getUserName(), muid, "/");
				vtp.setAllowExit(true);
				getManager().showDialog("Terminal", vtp);
				vtp.logLocation("Opened Terminal at ");
				return;
			}
		} else if ("files".equals(action)) {
			List<Tuple3<String, VortexAgentFile, String>> files = new ArrayList<Tuple3<String, VortexAgentFile, String>>();
			for (Row row : getTable().getSelectedRows()) {
				String muid = row.get(MUID, String.class);
				String name = row.get(HOST, String.class);
				files.add(new Tuple3<String, VortexAgentFile, String>(muid, null, name));
			}
			getManager().showDialog("Files", new VortexWebMachineFilesDialog(generateConfig(), files));
		} else if ("add".equals(action)) {
			getManager().showDialog("Add Machines", new VortexWebAddMachinesDialog(generateConfig()));
		} else if ("update".equals(action)) {
			Set<String> addresses = new HashSet<String>();
			for (Row row : table.getSelectedRows()) {
				VortexClientMachine machine = (VortexClientMachine) row.get("data");
				for (VortexClientNetAddress i : machine.getNetAddresses()) {
					if (!i.isLoopback() && i.getData().getType() == VortexAgentNetAddress.TYPE_INET) {
						addresses.add(i.getData().getAddress());
					}
				}
			}
			System.out.println(addresses);
			VortexWebAddMachinesDialog dialog = new VortexWebAddMachinesDialog(generateConfig());
			dialog.setIps(addresses);
			getManager().showDialog("Add Machines", dialog);
		}
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu r = (BasicWebMenu) super.createMenu(table);
		Boolean showDeleteOption = false;
		for (Row row : table.getSelectedRows()) {
			VortexClientMachine machine = (VortexClientMachine) row.get("data");
			if (!machine.getIsRunning()) {
				showDeleteOption = true;
			} else {
				showDeleteOption = false;
				break;
			}
		}
		r.addChild(new BasicWebMenuLink("Edit Meta Data", true, "metadata"));
		r.addChild(new BasicWebMenuLink("Show History", true, "history"));
		if (table.getSelectedRows().size() > 0)
			r.addChild(new BasicWebMenuLink("Browse Files", true, "files"));
		if (table.getSelectedRows().size() == 1)
			r.addChild(new BasicWebMenuLink("Open Terminal", true, "terminal"));

		if (showDeleteOption)
			r.addChild(new BasicWebMenuLink("Remove Stale Machines", true, "delete"));
		r.addChild(new BasicWebMenuLink("Install agents on new machines", true, "add"));
		if (table.hasSelectedRows())
			r.addChild(new BasicWebMenuLink("Update agent on Selected Machine(s)", true, "update"));
		return r;
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebMachineInstancesTablePortlet> {

		public static final String ID = "machineTablePortlet";

		public Builder() {
			super(VortexWebMachineInstancesTablePortlet.class);
		}

		@Override
		public VortexWebMachineInstancesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebMachineInstancesTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Machines";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);

	}
	@Override
	protected Row createAndAddRow(VortexClientMachine node) {

		//add row to document
		VortexClientMachine machine = (VortexClientMachine) node;
		VortexAgentMachine b = machine.getData();
		return addRow(b.getId(), b.getRevision(), b.getHostName(), b.getMachineUid(), b.getSystemStartTime(), b.getOsVersion(), b.getOsName(), b.getOsArchitecture(),
				b.getCpuCount(), b.getSystemLoadAverage(), b.getTotalMemory(), b.getUsedMemory(), b.getTotalSwapMemory(), b.getUsedSwapMemory(), b.getNow(), node,
				node.getExpectationState(), machine.getIsRunning(), machine.getProcessUid(), machine.getAgentDetail("version"), b.getAgentDetails(), b.getMetadata());
	}

	@Override
	protected void updateRow(Row row, VortexClientMachine node) {

		//update row in document
		VortexClientMachine machine = (VortexClientMachine) node;
		VortexAgentMachine a = machine.getData();
		row.put(MIID, a.getId());
		row.put(REV, a.getRevision());
		row.put(HOST, a.getHostName());
		row.put(MUID, a.getMachineUid());
		row.put(START, a.getSystemStartTime());
		row.put("osv", a.getOsVersion());
		row.put("osn", a.getOsName());
		row.put("osa", a.getOsArchitecture());
		row.put("cpu", a.getCpuCount());
		row.put("rawsys", a.getSystemLoadAverage());
		row.put("total", a.getTotalMemory());
		row.put("used", a.getUsedMemory());
		row.put("totals", a.getTotalSwapMemory());
		row.put("useds", a.getUsedSwapMemory());
		row.put("exps", node.getExpectationState());
		row.put("running", machine.getIsRunning());
		row.put("processUid", machine.getProcessUid());
		row.put("aversion", machine.getAgentDetail("version"));
		row.put("adetails", a.getAgentDetails());
		row.put("mdata", a.getMetadata());
		row.put(NOW, a.getNow());
	}

	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
		boolean exists = this.getRow(machine.getId()) != null;
		if (!exists && machine.getIsRunning())
			add(machine);
		else if (exists) {
			//if (!machine.getIsRunning())
			//remove(machine);
			//else
			update(machine);
		}

		//TODO:onMachineEntityUpdated(machine);
	}
	@Override
	public void onMachineAdded(VortexClientMachine machine) {
		add(machine);
	}

	public String[] getPreferredCol() {
		String[] cols = { MIID };
		return cols;
	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
		//nothing to do, let the natural update do the work
	}
	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		remove(machine);
	}

	@Override
	protected void onVortexRowsChanged() {
		if (!sendMiidSocket.hasConnections())
			return;
		FastWebTable t = getTable();
		List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
		if (sendMiidSocket.hasConnections()) {
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add((Long) addRow.get(MIID));
			sendMiidSocket.sendMessage(new VortexMachineIdInterPortletMessage(selections));
		}
	}

	final private VortexWebMetadataColumnsManager metadataColumnManager;
	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		metadataColumnManager.onMachineEntityAdded(node);
		super.onMachineEntityAdded(node);
	}
	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		metadataColumnManager.onMachineEntityUpdated(node);
		super.onMachineEntityUpdated(node);
	}
	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		metadataColumnManager.onMachineEntityRemoved(node);
		super.onMachineEntityRemoved(node);
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		metadataColumnManager.init(configuration, origToNewIdMapping, sb);
		super.init(configuration, origToNewIdMapping, sb);
	}
	@Override
	public Map<String, Object> getConfiguration() {
		return metadataColumnManager.getConfiguration(super.getConfiguration());
	}
	@Override
	public void onEyeSnapshotProcessed() {
		metadataColumnManager.onEyeSnapshotProcessed(service.getAgentManager());
		super.onEyeSnapshotProcessed();
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getAction();
		//if (action instanceof VortexEyePassToAgentResponse) {
		//VortexEyePassToAgentResponse resp = (VortexEyePassToAgentResponse) action;
		//VortexAgentResponse agentResponse = resp.getAgentResponse();
		//if (agentResponse instanceof VortexAgentFileSearchResponse) {
		//List<VortexAgentFile> files = ((VortexAgentFileSearchResponse) agentResponse).getFiles();
		//String machineUid = (((VortexEyePassToAgentRequest) result.getRequestMessage().getAction()).getAgentMachineUid());
		////getManager().showDialog("Files", new VortexWebMachineFilesDialog(generateConfig(), machineUid, files));
		//}

		//} else 
		if (action instanceof VortexEyeQueryDataResponse) {
			VortexEyeQueryDataResponse qdr = (VortexEyeQueryDataResponse) action;
			SeriesChartPortlet usedchart = new SeriesChartPortlet(generateConfig());
			usedchart.setTitle("System Load (%)");
			SeriesChartPortlet memchart = new SeriesChartPortlet(generateConfig());
			memchart.setTitle("Used Memory (%)");
			memchart.setStyle(SeriesChartPortlet.STYLE_LINE);

			//SeriesChartPortlet swapchart = new SeriesChartPortlet(generateConfig());
			//swapchart.setTitle("Total Memory (mb)");
			//swapchart.setStyle(SeriesChartPortlet.STYLE_LINE);

			memchart.addOption(SeriesChartPortlet.OPTION_KEY_POSITION, SeriesChartPortlet.POSITION_BELOW);
			VH.sort(((VortexEyeQueryDataResponse) action).getData(), VortexAgentMachine.PID_NOW);
			DoubleAggregator da = new DoubleAggregator();
			for (VortexEntity row : ((VortexEyeQueryDataResponse) action).getData()) {
				VortexAgentMachine vbr = (VortexAgentMachine) row;
				String date = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL).format(new Date(vbr.getNow()));
				String id = SH.toString(vbr.getId());
				if (!usedchart.getSeries().contains(id)) {
					String label = vbr.getHostName();
					usedchart.setSeriesLabel(id, label);
					memchart.setSeriesLabel(id, label);
					//swapchart.setSeriesLabel(id, label);
				}
				//Double value = vbr.getSystemLoadAverage();
				//da.add(vbr.getTotalSpace() / 1024 / 1024);
				//da.add(value);
				usedchart.addPoint(id, date, vbr.getSystemLoadAverage() * 100);
				memchart.addPoint(id, date, vbr.getUsedMemory() * 100D / vbr.getTotalMemory());
				//swapchart.addPoint(id, date, vbr.getTotalMemory() / 1024d / 1024d);
			}
			usedchart.setStyle(usedchart.getSeries().size() < 4 ? SeriesChartPortlet.STYLE_AREA : SeriesChartPortlet.STYLE_LINE);
			memchart.setStyle(memchart.getSeries().size() < 4 ? SeriesChartPortlet.STYLE_AREA : SeriesChartPortlet.STYLE_LINE);
			//swapchart.setStyle(swapchart.getSeries().size() == 1 ? SeriesChartPortlet.STYLE_AREA : SeriesChartPortlet.STYLE_LINE);
			usedchart.addOption(SeriesChartPortlet.OPTION_Y_MIN, 0);
			//usedchart.addOption(SeriesChartPortlet.OPTION_Y_MAX, 100);
			//swapchart.addOption(SeriesChartPortlet.OPTION_Y_MIN, 0);
			memchart.addOption(SeriesChartPortlet.OPTION_Y_MIN, 0);
			memchart.addOption(SeriesChartPortlet.OPTION_Y_MAX, 100);
			DividerPortlet div = new DividerPortlet(generateConfig(), false);
			div.addChild(usedchart);
			div.addChild(memchart);
			//DividerPortlet div2 = new DividerPortlet(generateConfig(), false);
			//div2.addChild(div);
			//div2.addChild(swapchart);
			getManager().showDialog("System Usage", div);
		} else
			super.onBackendResponse(result);
	}
}
