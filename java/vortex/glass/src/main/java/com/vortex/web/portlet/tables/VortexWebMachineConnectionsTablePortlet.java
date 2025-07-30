package com.vortex.web.portlet.tables;

//import org.jfree.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetTuple2Filter;
import com.f1.utils.CH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.vortex.client.VortexClientNetConnection;
import com.vortex.web.messages.VortexPidInterPortletMessage;

public class VortexWebMachineConnectionsTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentNetConnection, VortexClientNetConnection> {

	//private BasicPortletSocket processesSocket;

	final private BasicPortletSocket pidSocket;
	final private BasicPortletSocket sendPidSocket;

	public VortexWebMachineConnectionsTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_NET_CONNECTION);

		String[] ids = { CNID, REV, "fip", "fp", "ip", "lp", PID, "lapp", "state", NOW, MIID, HOST, "data", "exps", "rpid", "rhost" };
		BasicTable inner = new BasicTable(ids);
		String Title = "Net Connections";
		inner.setTitle(Title);
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(false, "Revision", REV, service.getNumberFormatter());
		table.addColumn(false, "Machine ID", MIID, service.getIdFormatter("MA-"));
		table.addColumn(false, "Connection Id", CNID, service.getIdFormatter("CN-"));
		table.addColumn(false, "Update Time", NOW, service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Pid", PID, service.getBasicFormatter()).setWidth(45);
		table.addColumn(true, "Remote Host", "rhost", service.getHostnameFormatter());
		table.addColumn(true, "Remote Pid", "rpid", service.getBasicFormatter()).setWidth(45);

		table.addColumn(true, "IP", "ip", service.getBasicFormatter()).setWidth(80);
		table.addColumn(true, "Port", "lp", service.getBasicFormatter()).setWidth(45);
		table.addColumn(true, "Remote IP", "fip", service.getBasicFormatter()).setWidth(80);
		table.addColumn(true, "Remote Port", "fp", service.getBasicFormatter()).setWidth(45);
		table.addColumn(true, "State", "state", service.getConnectionStateFormatter());
		table.addColumn(true, "Process", "lapp", service.getBasicFormatter());
		table.addColumn(true, "Expectation", "exps", service.getExpectationsStateFormatter());

		this.pidSocket = addSocket(false, "pid", "Pid", false, null, CH.s(VortexPidInterPortletMessage.class));
		this.sendPidSocket = addSocket(true, "sendPid", "Pid", true, CH.s(VortexPidInterPortletMessage.class), null);
		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);

		//		this.processesSocket = addSocket(true, "netConnections", "Show selected Connections", true, null, CH.s(ShowConnectionsFromProcessesSocket.class));
		//	this.masksendSocket = addSocket(true, "selection2", "Send Node Selection", true, CH.s(NodeSelectionInterPortletMessage.class), null);

	}
	//@Override
	//public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
	/*
		//map = service.getConnections();
		//pidMap = service.getPidToConnection();
		ShowConnectionsFromProcessesSocket showMessage = (ShowConnectionsFromProcessesSocket) message;
		List<String> show = showMessage.getShowOrders();

		if (show != null)
			for (String o : show) {
				Set<AgentNetConnection> s = pidMap.getValues(o);
				if (null != s)
					for (AgentNetConnection r : s) {
						AgentNetConnection a = (AgentNetConnection) r;
						if (o.equals(a.getLocalPid())) {
							//addSelectedId(a.getId());
							add(r);
						}
					}
			}
		List<String> hide = showMessage.getHideOrders();
		if (hide != null)
			for (String o : hide) {
				Set<AgentNetConnection> s = pidMap.getValues(o);
				if (null != s)
					for (AgentNetConnection a : s) {
						remove(a);
						//removeSelectedId(a.getId());
					}
			}
			*/
	//}

	//@Override
	//public void onDisconnect(PortletSocket localSocket, PortletSocket remoteSocket) {

	//		service.addPortlet(this);
	//super.onDisconnect(localSocket, remoteSocket);
	//}

	//@Override
	//public void onConnect(PortletSocket localSocket, PortletSocket remoteSocket) {

	//super.onConnect(localSocket, remoteSocket);
	//
	//rows.clear();
	//getTable().refresh();
	//log.info("ConnectionsTablePortlet " + getPortletId() + " connected to ProcessesTablePortlet");
	//}
	//
	//public void onRemoved() {
	//service.removePortlet(this);
	//}

	@Override
	protected Row createAndAddRow(VortexClientNetConnection connection) {
		VortexAgentNetConnection a = connection.getData();
		VortexClientNetConnection remoteConnection = connection.getRemoteConnection();

		String rHost = "";
		String rPid = "";
		if (remoteConnection != null) {
			rHost = remoteConnection.getHostName();
			rPid = remoteConnection.getData().getLocalPid();
		}
		Row r = addRow(a.getId(), a.getRevision(), a.getForeignHost(), a.getForeignPort(), a.getLocalHost(), a.getLocalPort(), a.getLocalPid(), a.getLocalAppName(), a.getState(),
				a.getNow(), a.getMachineInstanceId(), connection.getHostName(), connection, connection.getExpectationState(), rPid, rHost);
		return r;
	}
	@Override
	protected void updateRow(Row row, VortexClientNetConnection node) {
		VortexAgentNetConnection a = node.getData();
		VortexClientNetConnection remoteConnection = node.getRemoteConnection();

		String rHost = "";
		String rPid = "";
		if (remoteConnection != null) {
			rHost = remoteConnection.getHostName();
			rPid = remoteConnection.getData().getLocalPid();
		}
		row.put(HOST, node.getHostName());
		row.put(CNID, a.getId());
		row.put(REV, a.getRevision());
		row.put("fip", a.getForeignHost());
		row.put("fp", a.getForeignPort());
		row.put("ip", a.getLocalHost());
		row.put("lp", a.getLocalPort());
		row.put(PID, a.getLocalPid());
		row.put("lapp", a.getLocalAppName());
		row.put("state", a.getState());
		row.put(NOW, a.getNow());
		row.put(MIID, a.getMachineInstanceId());
		row.put("exps", node.getExpectationState());
		row.put("rhost", rHost);
		row.put("rpid", rPid);
	}

	//public void remove(AgentNetConnection a) {
	//if (!connected || (connected && getSelectedIds().contains(a.getId()))) {
	//deleteRow(a);
	//}
	//}

	@Override
	public WebMenu createMenu(WebTable table) {
		return super.createMenu(table);
		//List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		//children.add(new BasicWebMenuLink("Show History", true, "history"));
		//WebMenu r = new BasicWebMenu("test", true, children);
		//return r;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		super.onContextMenu(table, action);
		//action = parseContext(action);
		//List<Row> selectedRows = table.getSelectedRows();
		//if ("history".equals(action)) {
		//List<Long> ids = new ArrayList<Long>();
		//for (Row row : selectedRows) {
		//ids.add((Long) row.get("id"));
		//}
		//if (ids.size() > 0)
		//service.getHistory(AgentHistoryRequest.TYPE_CONNECTION, ids, getPortletId(), false, false);
		//}
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebMachineConnectionsTablePortlet> {

		public static final String ID = "connectionsTablePortlet";

		public Builder() {
			super(VortexWebMachineConnectionsTablePortlet.class);
		}

		@Override
		public VortexWebMachineConnectionsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebMachineConnectionsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Net Connections";
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == pidSocket) {
			VortexPidInterPortletMessage msg = (VortexPidInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetTuple2Filter(getTable().getColumn(HOST), getTable().getColumn(PID), (Set) msg.getHostAndPids()));
			onVortexRowsChanged();
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}

	@Override
	public void onVortexRowsChanged() {
		if (!sendPidSocket.hasConnections())
			return;
		FastWebTable t = getTable();
		List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
		if (sendPidSocket.hasConnections()) {
			Set<Tuple2<String, String>> selections = new HashSet<Tuple2<String, String>>();
			for (Row addRow : sel)
				selections.add(new Tuple2<String, String>(addRow.get("rhost", String.class), addRow.get("rpid", String.class)));
			sendPidSocket.sendMessage(new VortexPidInterPortletMessage(selections));
		}
	}

}
