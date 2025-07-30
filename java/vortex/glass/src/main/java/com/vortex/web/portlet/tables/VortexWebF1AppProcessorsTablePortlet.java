package com.vortex.web.portlet.tables;

import com.f1.base.Row;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppProcessor;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebProcessor;
import com.vortex.web.portlet.forms.VortexWebAuditTrailRuleFormPortlet;

public class VortexWebF1AppProcessorsTablePortlet extends VortexWebF1AppAbstractTablePortlet<VortexClientF1AppState.AgentWebProcessor> implements WebContextMenuFactory {

	public VortexWebF1AppProcessorsTablePortlet(PortletConfig config) {
		super(config, null, VortexClientF1AppState.AgentWebProcessor.class);
		String[] ids = { HOST, USER, MAIN, "procname", "class", "proc", "exceptions", "pop", "push", "queue", "started", APPNAME, "data", "audit", "id", "apid", "ac" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("F1 Processors");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Host", HOST, service.getHostnameFormatter()).setWidth(150);
		table.addColumn(true, "User", USER, service.getUserFormatter()).setWidth(100);
		table.addColumn(true, "Main", MAIN, service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "Processor Id", "procname", service.getBasicFormatter()).setWidth(250).setCssColumn("bold");
		table.addColumn(true, "Queued", "queue", service.getNumberFormatter()).setWidth(70).addCssClass("blue");
		table.addColumn(true, "Processed", "proc", service.getNumberFormatter()).setWidth(70);
		table.addColumn(true, "Exceptions", "exceptions", service.getWarningNumberFormatter()).setWidth(70);
		table.addColumn(true, "AppName", APPNAME, service.getBasicFormatter()).addCssClass("bold");

		table.addColumn(false, "Class", "class", service.getClassNameFormatter());
		table.addColumn(false, "Pop", "pop", service.getNumberFormatter()).setWidth(70);
		table.addColumn(false, "Push", "push", service.getNumberFormatter()).setWidth(70);
		table.addColumn(true, "Is Audited", "audit", service.getNumberFormatter());
		table.addColumn(false, "Id", "id", service.getIdFormatter("APPC-"));
		table.addColumn(false, "AppId", "apid", service.getIdFormatter("AP-"));
		table.addColumn(false, "Action Class", "ac", service.getClassNameFormatter());

		setTable(table);
		table.setMenuFactory(this);
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebF1AppProcessorsTablePortlet> {

		public static final String ID = "AgentF1ProcessorsTablePortlet";

		public Builder() {
			super(VortexWebF1AppProcessorsTablePortlet.class);
		}

		@Override
		public VortexWebF1AppProcessorsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebF1AppProcessorsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 Processors Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("audit".equals(action)) {
			for (Row r : table.getSelectedRows()) {
				AgentWebProcessor awp = r.get("data", AgentWebProcessor.class);
				VortexWebAuditTrailRuleFormPortlet form = new VortexWebAuditTrailRuleFormPortlet(generateConfig());
				form.setTemplate(awp);
				getManager().showDialog("Audit Logger", form);
			}
		} else
			super.onContextMenu(table, action);
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		return new BasicWebMenu(new BasicWebMenuLink("Start auditing.", true, "audit"));
	}

	@Override
	protected Row createAndAddRow(AgentWebProcessor node) {
		final F1AppProcessor o = node.getObject();
		final long processStats = o.getProcessStats();
		final long thrownStats = o.getThrownStats();
		final long queuePopStats = o.getQueuePopStats();
		final long queuePushStats = o.getQueuePushStats();
		final long queueSize = queuePushStats - queuePopStats;
		final F1AppInstance snapshot = node.getSnapshot();
		final long startedMs = o.getStartedMs();
		final String host = snapshot.getHostName();
		final String user = snapshot.getUserName();
		final String appName = snapshot.getAppName();
		final String mainClassName = SH.afterLast(snapshot.getMainClassName(), '.');
		final String procname = OH.toString(o.getName());
		final String className = node.getClassName();
		final String actionClassName = node.getActionTypeClassName();
		return addRow(host, user, mainClassName, procname, className, processStats, thrownStats, queuePopStats, queuePushStats, queueSize, startedMs, appName, node,
				o.getAuditRulesCount(), o.getId(), o.getF1AppInstanceId(), actionClassName);
	}
	@Override
	protected void updateRow(Row row, AgentWebProcessor i) {
		F1AppProcessor o = i.getObject();
		long processStats = o.getProcessStats();
		long thrownStats = o.getThrownStats();
		long queuePopStats = o.getQueuePopStats();
		long queuePushStats = o.getQueuePushStats();
		long queueSize = queuePushStats - queuePopStats;
		row.put("proc", processStats);
		row.put("exceptions", thrownStats);
		row.put("pop", queuePopStats);
		row.put("push", queuePushStats);
		row.put("queue", queueSize);
		row.put("audit", o.getAuditRulesCount());

	}

	@Override
	protected Iterable<AgentWebProcessor> getEntitiesForSnapshot(VortexClientF1AppState f1AppState) {
		return f1AppState.getProcessorsByContainerScopeId().values();
	}

}
