package com.vortex.web.portlet.tables;

import com.f1.base.Row;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppMsgTopic;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebMsgTopic;

public class VortexWebF1AppConnectionsTablePortlet extends VortexWebF1AppAbstractTablePortlet<VortexClientF1AppState.AgentWebMsgTopic> {

	public VortexWebF1AppConnectionsTablePortlet(PortletConfig config) {
		super(config, null, VortexClientF1AppState.AgentWebMsgTopic.class);
		String[] ids = { HOST, USER, MAIN, "name", "suffix", "class", START, APPNAME, "in", "out", "inb", "outb", "conn", "ports", "apid" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("F1 Connections");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Host", HOST, service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "User", USER, service.getBasicFormatter()).setWidth(100);
		table.addColumn(true, "Main", MAIN, service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "Name", "name", service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "Started", START, service.getDateTimeWebCellFormatter());
		table.addColumn(true, "In Count", "in", service.getNumberFormatter()).setWidth(100);
		table.addColumn(true, "Out Count", "out", service.getNumberFormatter()).setWidth(100);
		table.addColumn(true, "In Size", "inb", service.getMemoryFormatter());
		table.addColumn(true, "Out Size", "outb", service.getMemoryFormatter());
		table.addColumn(true, "App Name", APPNAME, service.getBasicFormatter()).addCssClass("bold");
		table.addColumn(false, "Class", "class", service.getBasicFormatter()).setWidth(250);
		table.addColumn(false, "Connections", "conn", service.getNumberFormatter()).setWidth(70);
		table.addColumn(false, "Ports", "ports", service.getBasicFormatter()).setWidth(150);
		table.addColumn(false, "Suffix", "suffix", service.getBasicFormatter()).setWidth(150);
		table.addColumn(false, "AppId", "apid", service.getIdFormatter("AP-"));
		setTable(table);
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebF1AppConnectionsTablePortlet> {

		public static final String ID = "AgentF1ConnectionsTablePortlet";

		public Builder() {
			super(VortexWebF1AppConnectionsTablePortlet.class);
		}

		@Override
		public VortexWebF1AppConnectionsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebF1AppConnectionsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 Connections Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	protected Row createAndAddRow(AgentWebMsgTopic i) {
		F1AppInstance snapshot = i.getSnapshot();
		F1AppMsgTopic o = i.getObject();
		long in = o.getMessagesReceivedCount();
		long out = o.getMessagesSentCount();
		long connections = o.getConnectionsCount();
		long inb = o.getBytesReceived();
		long outb = o.getBytesSent();
		String ports = o.getLocalPorts() == null ? "" : SH.join(',', o.getLocalPorts());
		long startedMs = o.getStartedMs();
		String host = snapshot.getHostName();
		String user = snapshot.getUserName();
		String appName = snapshot.getAppName();
		String mainClassName = SH.afterLast(snapshot.getMainClassName(), '.');
		String name = SH.beforeFirst(o.getTopicName(), "$");
		String suffix = SH.afterFirst(o.getTopicName(), "$", null);
		//String threadPoolKey = SH.toString(o.getThreadPoolKey());
		String className = i.getClassName();
		return addRow(host, user, mainClassName, name, suffix, className, startedMs, appName, in, out, inb, outb, connections, ports, o.getF1AppInstanceId());
	}

	@Override
	protected void updateRow(Row row, AgentWebMsgTopic i) {
		F1AppMsgTopic o = i.getObject();
		long in = o.getMessagesReceivedCount();
		long out = o.getMessagesSentCount();
		long connections = o.getConnectionsCount();
		long inb = o.getBytesReceived();
		long outb = o.getBytesSent();
		String ports = o.getLocalPorts() == null ? "" : SH.join(',', o.getLocalPorts());
		row.put("in", in);
		row.put("out", out);
		row.put("inb", inb);
		row.put("outb", outb);
		row.put("conn", connections);
		row.put("ports", ports);
	}
	@Override
	protected Iterable<AgentWebMsgTopic> getEntitiesForSnapshot(VortexClientF1AppState f1AppState) {
		return f1AppState.getTopics().values();
	}

}
