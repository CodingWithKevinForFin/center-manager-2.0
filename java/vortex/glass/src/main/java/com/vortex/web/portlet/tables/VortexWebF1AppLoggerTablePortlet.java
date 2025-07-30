package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Row;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppLogger;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebLogger;
import com.vortex.web.portlet.forms.VortexWebAuditTrailRuleFormPortlet;
import com.vortex.web.portlet.forms.VortexWebChangeLoggerLevelFormPortlet;

public class VortexWebF1AppLoggerTablePortlet extends VortexWebF1AppAbstractTablePortlet<VortexClientF1AppState.AgentWebLogger> implements WebContextMenuFactory {

	public VortexWebF1AppLoggerTablePortlet(PortletConfig config) {
		super(config, null, VortexClientF1AppState.AgentWebLogger.class);
		String[] ids = { HOST, USER, MAIN, "class", "name", "fullname", "events", "warnings", "errors", "ignored", "exceptions", "bytes", APPNAME, "data", "id", "apid", "level" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("F1 Loggers");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(true, "User", USER, service.getUserFormatter());
		table.addColumn(true, "App Name", APPNAME, service.getAppnameFormatter());
		table.addColumn(true, "Name", "name", service.getBasicFormatter()).setWidth(200).setCssColumn("bold");
		table.addColumn(true, "Log Events", "events", service.getNumberFormatter()).setWidth(70);
		table.addColumn(true, "Exceptions", "exceptions", service.getWarningNumberFormatter()).setWidth(70);
		table.addColumn(true, "Ignored", "ignored", service.getNumberFormatter()).setWidth(70);
		table.addColumn(true, "Bytes", "bytes", service.getMemoryFormatter()).setWidth(70);
		table.addColumn(true, "Warnings", "warnings", service.getWarningNumberFormatter()).setWidth(70);
		table.addColumn(true, "Errors", "errors", service.getWarningNumberFormatter()).setWidth(70);
		table.addColumn(false, "Main", MAIN, service.getClassNameFormatter()).setWidth(150);
		table.addColumn(false, "Full Name", "fullname", service.getBasicFormatter()).setWidth(300);
		table.addColumn(false, "Class", "class", service.getBasicFormatter()).setWidth(250);
		table.addColumn(false, "Id", "id", service.getIdFormatter("APLG-"));
		table.addColumn(false, "AppId", "apid", service.getIdFormatter("AP-"));
		table.addColumn(false, "Level", "level", service.getLogLevelFormatter());
		setTable(table);
		table.setMenuFactory(this);
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebF1AppLoggerTablePortlet> {

		public static final String ID = "AgentF1LoggerTablePortlet";

		public Builder() {
			super(VortexWebF1AppLoggerTablePortlet.class);
		}

		@Override
		public VortexWebF1AppLoggerTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebF1AppLoggerTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 Loggers Table";
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
				AgentWebLogger awl = r.get("data", AgentWebLogger.class);
				VortexWebAuditTrailRuleFormPortlet form = new VortexWebAuditTrailRuleFormPortlet(generateConfig());
				form.setTemplate(awl);
				getManager().showDialog("Audit Logger", form);
			}
		} else if ("adjust".equals(action)) {
			List<Row> sel = table.getSelectedRows();
			List<AgentWebLogger> loggers = new ArrayList<AgentWebLogger>();
			for (Row row : sel)
				loggers.add(row.get("data", AgentWebLogger.class));
			getManager().showDialog("Adjust Logger Levels", new VortexWebChangeLoggerLevelFormPortlet(generateConfig(), loggers));
		} else
			super.onContextMenu(table, action);
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		return new BasicWebMenu(new BasicWebMenuLink("Start auditing", true, "audit"), new BasicWebMenuLink("Change log level", true, "adjust"));
	}
	@Override
	protected Row createAndAddRow(AgentWebLogger node) {
		F1AppInstance snapshot = node.getSnapshot();
		String host = snapshot.getHostName();
		String user = snapshot.getUserName();
		String appName = snapshot.getAppName();
		String mainClassName = SH.afterLast(snapshot.getMainClassName(), '.');
		F1AppLogger o = node.getObject();
		String fullname = o.getLoggerId();
		String name = SH.afterLast(fullname, '.');
		long events = o.getTotalEventsCount();
		long warning = o.getWarningOrHigherCount();
		long error = o.getErrorOrHigherCount();
		long ignored = o.getDroppedCount();
		long exceptions = o.getExceptionsCount();
		long bytes = o.getBytesLoggedCount();
		String className = node.getClassName();
		return addRow(host, user, mainClassName, className, name, fullname, events, warning, error, ignored, exceptions, bytes, appName, node, o.getId(), o.getF1AppInstanceId(),
				o.getMinLogLevel());
	}
	@Override
	protected void updateRow(Row row, AgentWebLogger node) {
		F1AppLogger o = node.getObject();
		long events = o.getTotalEventsCount();
		long warning = o.getWarningOrHigherCount();
		long error = o.getErrorOrHigherCount();
		long ignored = o.getDroppedCount();
		long exceptions = o.getExceptionsCount();
		long bytes = o.getBytesLoggedCount();
		row.put("events", events);
		row.put("warnings", warning);
		row.put("errors", error);
		row.put("ignored", ignored);
		row.put("exceptions", exceptions);
		row.put("bytes", bytes);
		row.put("level", o.getMinLogLevel());
	}

	@Override
	protected Iterable<AgentWebLogger> getEntitiesForSnapshot(VortexClientF1AppState f1AppState) {
		return f1AppState.getLoggersById().values();
	}
}
