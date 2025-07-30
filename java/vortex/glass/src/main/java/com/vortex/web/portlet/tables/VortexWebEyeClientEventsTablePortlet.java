package com.vortex.web.portlet.tables;

import java.util.Map.Entry;

import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.vortex.client.VortexClientEventListener;
import com.vortex.client.VortexClientMachine;

public class VortexWebEyeClientEventsTablePortlet extends VortexWebTablePortlet implements VortexClientEventListener {

	private MapWebCellFormatter<Byte> typeFormatter;

	public VortexWebEyeClientEventsTablePortlet(PortletConfig config) {
		super(config, null);
		String[] ids = { "id", "type", "invoked_by", "comment", "now", "message", "muid", "params", "data" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Client Events");
		SmartTable st = new BasicSmartTable(inner);
		typeFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_APP_CHANGE_LOG_LEVEL, "Change App Log Level");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_APP_INSPECT_PARTITION, "Inspect App Partition");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_APP_INTERRUPT_THREAD, "Interrupt App Thread");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_CREATE_DEPLOYMENT_ENVIRONMENT, "Create Deployment");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_DEPLOYMENT_DELETE_ALL_FILES, "Undeploy");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_DEPLOYMENT_DEPLOY, "Deploy");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_DEPLOYMENT_VERIFY, "Verify Deployment");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_DEPLOYMENT_CONFIG, "Deploy Config");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_DEPLOYMENT_GET_FILE, "Get Deployment File");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_DEPLOYMENT_GET_FILE_STRUCTURE, "Show Deployment Files");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_DEPLOYMENT_RUN_SCRIPT, "Run Deployment Custom Script");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_DEPLOYMENT_START_SCRIPT, "Run Deployment Start Script");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_DEPLOYMENT_STOP_SCRIPT, "run Deployment Stop Script");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_GET_FILES, "Get Files");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_DELETE_FILES, "Delete Files");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_AUDIT_RULE, "Manage Audit Rules");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_BACKUP, "Manage Backup");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_BACKUP_DESTINATION, "Manage Backup Destination");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_BUILD_PROCEDURE, "Manage Build Procedure");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_BUILD_RESULT, "Manage Build Result");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_DEPLOYMENT, "Manage Deployment");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_DEPLOYMENT_SET, "Manage Deployment Set");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_EXPECTATION, "Manage Expectation");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_MACHINE, "Manage Machine");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_METADATA_FIELD, "Manage Metadata Field");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_SCHEDULED_TASK, "Manage Scheduled Task");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_RUN_BACKUP, "Run Backup");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_RUN_BUILD_PROCEDURE, "Run Build Procedure");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_RUN_DEPLOYMENT, "Run Deployment");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_RUN_INSPECT_DATASERVER, "Run Inspect Dataserver");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_RUN_SCHEDULED_TASK, "Run Scheduled Task");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_RUN_SIGNAL_ON_PROCESS, "Send Signal to Process");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_QUERY_DATA, "Query Historical Data");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_NETWORK_SCAN, "Network Scan");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_RUN_SHELL_COMMAND, "Run Shell Command");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_INSTALL_AGENT, "Install Agent");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_RUN_OS_COMMAND, "Os Command");
		typeFormatter.addEntry(VortexEyeClientEvent.TYPE_CI_COMMAND, "Cloud Interface Command");
		//typeFormatter.addEntry(VortexEyeClientEvent.TYPE_RUN_AMI_COMMAND, "AMI Command");
		//typeFormatter.addEntry(VortexEyeClientEvent.TYPE_MANAGE_AMI_ALERT, "Manage AMI Alert");
		typeFormatter.setDefaultWidth(70).lockFormatter();
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Time", "now", service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Type", "type", typeFormatter).setCssColumn("bold blue clickable").setIsClickable(true);
		table.addColumn(true, "User", "invoked_by", service.getUserFormatter());
		table.addColumn(true, "Host", "muid", service.getHostnameFormatter());
		table.addColumn(false, "Comment", "comment", service.getBasicFormatter());
		table.addColumn(false, "Message", "message", service.getBasicFormatter());
		table.addColumn(false, "Params", "params", service.getBasicFormatter()).setWidth(75);
		table.addColumn(false, "Id", "id", service.getIdFormatter("CE"));
		agentManager.addClientEventlistener(this);
		setTable(table);
		for (VortexEyeClientEvent event : service.getAgentManager().getClientEvents())
			onClientEvent(event);
		getTable().sortRows("now", false, true, false);
	}

	@Override
	public void close() {
		service.getAgentManager().removeClientEventListener(this);
		super.close();
	}

	@Override
	public void onEyeDisconnected() {
		getTable().clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebEyeClientEventsTablePortlet> {

		public static final String ID = "ClientEventsTablePortlet";

		public Builder() {
			super(VortexWebEyeClientEventsTablePortlet.class);
		}

		@Override
		public VortexWebEyeClientEventsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebEyeClientEventsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Client Events";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onEyeSnapshotProcessed() {
		// TODO Auto-generated method stub

	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		VortexEyeClientEvent event = row.get("data", VortexEyeClientEvent.class);
		HtmlPortlet details = new HtmlPortlet(generateConfig());
		StringBuilder html = new StringBuilder();
		html.append("Client Event: <B>CE-").append(event.getId()).append("</B><P>");
		html.append("Action: <B>").append(typeFormatter.formatCellToHtml(event.getEventType())).append("</B><P>");
		html.append("Time: <B>").append(service.getDateTimeWebCellFormatter().formatCellToHtml(event.getNow())).append("</B><P>");
		html.append("User: <B>").append(event.getInvokedBy()).append("</B><P>");
		Object muid = row.get("muid");
		if (muid != null)
			html.append("Host: <B>").append(muid).append("</B><P>");

		if (SH.is(event.getComment()))
			html.append("User Comment: <B><i><pre>").append(event.getComment()).append("</pre></i><P>");

		if (event.getParams() != null) {
			for (Entry<String, String> e : event.getParams().entrySet())
				html.append(e.getKey()).append(": <pre>").append(e.getValue()).append("</pre><P>");

		}

		details.setHtml(html.toString());
		getManager().showDialog("Client Event Details", details);
	}
	@Override
	public void onClientEvent(VortexEyeClientEvent entity) {
		String muid = entity.getTargetMachineUid();
		if (muid != null) {
			final VortexClientMachine am = service.getAgentManager().getAgentMachineByUid(muid);
			if (am != null)
				muid = am.getHostName();
		}
		addRow(entity.getId(), entity.getEventType(), entity.getInvokedBy(), entity.getComment(), entity.getNow(), entity.getMessage(), muid, entity.getParams(), entity);
	}
}
