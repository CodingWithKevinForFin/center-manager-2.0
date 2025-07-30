package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.chart.SeriesChartPortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetTuple2Filter;
import com.f1.utils.CH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataResponse;
import com.vortex.client.VortexClientProcess;
import com.vortex.web.messages.VortexPidInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebSendSignalToProcessFormPortlet;

public class VortexWebMachineProcessesTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentProcess, VortexClientProcess> {
	private static final Logger log = Logger.getLogger(VortexWebMachineProcessesTablePortlet.class.getName());
	final private BasicPortletSocket pidSocket;
	private Object sendMiidSocket;

	public VortexWebMachineProcessesTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_PROCESS);
		// TODO Auto-generated constructor stub
		Class[] clazz = { Long.class, Long.class, String.class, String.class, Integer.class, Long.class, Double.class, Long.class, Long.class, String.class, Long.class,
				Long.class, Boolean.class, String.class, String.class, Object.class, Byte.class };
		String[] ids = { PTID, PID, "ppid", USER, REV, "mem", "cpu", START, END, "process", NOW, MIID, "shown", HOST, "fullprocess", "data", "exps" };
		BasicTable inner = new BasicTable(clazz, ids);
		inner.setTitle("Processes");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Hostname", HOST, service.getHostnameFormatter());
		table.addColumn(true, "Process ID", PID, service.getBasicFormatter());
		table.addColumn(true, "User", USER, service.getBasicFormatter());
		table.addColumn(true, "Memory", "mem", service.getMemoryFormatter());
		table.addColumn(true, "CPU Usage", "cpu", service.getPercentFormatter());
		table.addColumn(true, "Start Time", START, service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Command", "process", service.getBasicFormatter()).setWidth(300);
		table.addColumn(false, "Event Time", NOW, service.getDateTimeWebCellFormatter());
		table.addColumn(false, "Revision", REV, service.getNumberFormatter());
		table.addColumn(false, "Parent Process ID #", "ppid", service.getBasicFormatter());
		table.addColumn(false, "End Time", END, service.getDateTimeWebCellFormatter());
		table.addColumn(false, "Id", PTID, service.getIdFormatter("PR-"));
		table.addColumn(false, "Machine Id", MIID, service.getIdFormatter("m"));
		table.addColumn(false, "Full Process Name", "fullprocess", service.getBasicFormatter()).setWidth(500);
		table.addColumn(true, "Expectation", "exps", service.getExpectationsStateFormatter());

		super.setTable(table);
		table.setMenuFactory(this);
		//table.addMenuListener(this);
		//service.addPortlet(this);
		this.pidSocket = addSocket(false, "pid", "Pid", false, null, CH.s(VortexPidInterPortletMessage.class));
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		//	if ("Select".equals(col.getColumnName())) {
		//			boolean show = !row.get("shown", Boolean.class);
		//			row.put("shown", show);
		//		String pid = (String) row.get("pid");
		//		if (!connectionsSocket.getRemoteConnections().isEmpty()) {
		//				List<String> orders = CH.l(pid);
		//				if (show) {
		//				connectionsSocket.sendMessage(new NodeSelectionInterPortletMessage(orders, null));
		//			} else {
		//.sendMessage(new NodeSelectionInterPortletMessage(null, orders));
		//				}
		///			}
		//	}
	}

	//@Override
	//public void onDisconnect(PortletSocket localSocket, PortletSocket remoteSocket) {
	//List<String> orders = new ArrayList<String>();
	//for (Row row : rows) {
	//if (!row.get("shown", Boolean.class)) {
	//String order = (String) row.getValues()[2];
	//orders.add(order);
	//}
	//}
	//remoteSocket.sendMessage(new ShowConnectionsFromProcessesSocket(null, orders));
	//}

	//@Override
	//public void onConnect(PortletSocket localSocket, PortletSocket remoteSocket) {
	//List<String> orders = new ArrayList<String>();
	//for (Row row : rows) {
	//if (row.get("shown", Boolean.class)) {
	//String order = (String) row.getValues()[2];
	//orders.add(order);
	//}
	//}
	//localSocket.sendMessage(new ShowConnectionsFromProcessesSocket(orders, null));
	//}

	@Override
	public void onContextMenu(WebTable table, String action) {
		List<Row> selectedRows = table.getSelectedRows();
		if ("sig".equals(action)) {
			List<VortexClientProcess> processes = new ArrayList<VortexClientProcess>(selectedRows.size());
			for (Row row : selectedRows) {
				processes.add(row.get("data", VortexClientProcess.class));
			}
			VortexWebSendSignalToProcessFormPortlet form = new VortexWebSendSignalToProcessFormPortlet(generateConfig());
			form.setProcesses(processes);
			getManager().showDialog("Kill " + selectedRows.size() + " Processes", form);
		} else if ("history".equals(action)) {
			LongSet ids = new LongSet();
			for (Row row : selectedRows) {
				ids.add(row.get("data", VortexClientProcess.class).getId());
			}
			VortexEyeQueryDataRequest request = nw(VortexEyeQueryDataRequest.class);
			request.setIds(ids.toLongArray());
			request.setType(VortexAgentEntity.TYPE_PROCESS);
			service.sendRequestToBackend(getPortletId(), request);
		}
		super.onContextMenu(table, action);
	}
	@Override
	public BasicWebMenu createMenu(WebTable table) {
		BasicWebMenu menu = (BasicWebMenu) super.createMenu(table);
		menu.addChild(new BasicWebMenuLink("Send Signal", true, "sig"));
		menu.addChild(new BasicWebMenuLink("Show History", true, "history"));
		return menu;
		//List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		//children.add(new BasicWebMenuLink("Show Connections", true, "showc"));
		//children.add(new BasicWebMenuLink("Hide Connections", true, "hidec"));
		//children.add(new BasicWebMenuLink("Show History", true, "history"));
		//children.add(new BasicWebMenuLink("Show Net Connection History", true, "nchistory"));
		//WebMenu r = new BasicWebMenu("test", true, children);
		//return r;
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebMachineProcessesTablePortlet> {
		public Builder() {
			super(VortexWebMachineProcessesTablePortlet.class);
		}

		public static final String ID = "processesTablePortlet";

		@Override
		public VortexWebMachineProcessesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebMachineProcessesTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Processes";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);

	}

	@Override
	protected Row createAndAddRow(VortexClientProcess t) {
		VortexClientProcess process = (VortexClientProcess) t;
		VortexAgentProcess a = process.getData();
		return addRow(process.getId(), a.getPid(), a.getParentPid(), a.getUser(), a.getRevision(), a.getMemory(), a.getCpuPercent(), a.getStartTime(), a.getEndTime(),
				process.getName(), a.getNow(), a.getMachineInstanceId(), false, process.getHostName(), a.getCommand(), t, t.getExpectationState());
	}

	@Override
	protected void updateRow(Row row, VortexClientProcess t) {
		VortexClientProcess process = (VortexClientProcess) t;
		VortexAgentProcess a = process.getData();
		row.put(PID, a.getPid());
		row.put("ppid", a.getParentPid());
		row.put(USER, a.getUser());
		row.put(REV, a.getRevision());
		row.put("mem", a.getMemory());
		row.put("cpu", a.getCpuPercent());
		row.put(START, a.getStartTime());
		row.put(END, a.getEndTime());
		row.put(HOST, process.getHostName());
		row.put("process", process.getName());
		row.put("fullprocess", a.getCommand());
		row.put("exps", t.getExpectationState());
		row.put(NOW, a.getNow());
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == pidSocket) {
			VortexPidInterPortletMessage msg = (VortexPidInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetTuple2Filter(getTable().getColumn(HOST), getTable().getColumn(PID), (Set) msg.getHostAndPids()));
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getAction();
		if (action instanceof VortexEyeQueryDataResponse) {
			VortexEyeQueryDataResponse qdr = (VortexEyeQueryDataResponse) action;
			SeriesChartPortlet cpuchart = new SeriesChartPortlet(generateConfig());
			cpuchart.setStyle(SeriesChartPortlet.STYLE_LINE);
			cpuchart.setTitle("CPU Usage (%)");
			SeriesChartPortlet memchart = new SeriesChartPortlet(generateConfig());
			memchart.setTitle("Memory Usage (MB)");
			memchart.setStyle(SeriesChartPortlet.STYLE_LINE);
			memchart.addOption(SeriesChartPortlet.OPTION_KEY_POSITION, SeriesChartPortlet.POSITION_BELOW);
			VH.sort(((VortexEyeQueryDataResponse) action).getData(), VortexAgentProcess.PID_NOW);
			for (VortexEntity row : ((VortexEyeQueryDataResponse) action).getData()) {
				VortexAgentProcess vbr = (VortexAgentProcess) row;
				String date = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL).format(new Date(vbr.getNow()));
				String id = SH.toString(vbr.getId());
				if (!cpuchart.getSeries().contains(id)) {
					String label = vbr.getPid() + "@" + service.getAgentManager().getAgentMachine(vbr.getMachineInstanceId()).getHostName() + " - "
							+ VortexClientProcess.parseCommand(vbr.getCommand());
					cpuchart.setSeriesLabel(id, label);
					memchart.setSeriesLabel(id, label);
				}
				cpuchart.addPoint(id, date, vbr.getCpuPercent() * 100);
				memchart.addPoint(id, date, vbr.getMemory() / 1024 / 1024);
			}
			cpuchart.setStyle(cpuchart.getSeries().size() < 4 ? SeriesChartPortlet.STYLE_AREA : SeriesChartPortlet.STYLE_LINE);
			memchart.setStyle(memchart.getSeries().size() < 4 ? SeriesChartPortlet.STYLE_AREA : SeriesChartPortlet.STYLE_LINE);
			DividerPortlet div = new DividerPortlet(generateConfig(), false);
			div.addChild(cpuchart);
			div.addChild(memchart);
			getManager().showDialog("Cpu And Memory Usage", div);
		} else
			super.onBackendResponse(result);
	}
}
