package com.vortex.web.portlet.tables;

import com.f1.base.Row;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppProperty;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.utils.CH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebProperty;

public class VortexWebF1AppPropertiesTablePortlet extends VortexWebF1AppAbstractTablePortlet<VortexClientF1AppState.AgentWebProperty> {

	public VortexWebF1AppPropertiesTablePortlet(PortletConfig config) {
		super(config, null, VortexClientF1AppState.AgentWebProperty.class);
		String[] ids = { "processUid", USER, HOST, "class", "key", "value", "pos", "type", "src", "line", APPNAME, "apid" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("F1 Properties");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(true, "User", USER, service.getUserFormatter());
		table.addColumn(true, "App Name", APPNAME, service.getAppnameFormatter());
		table.addColumn(true, "Property", new Object[] { "key", "pos" }, new StrikeThroughFormatter()).setWidth(250);
		table.addColumn(true, "Value", new Object[] { "value", "pos" }, new StrikeThroughFormatter()).setWidth(500);
		BasicWebColumn pos = table.addColumn(false, "Position", "pos", service.getNumberFormatter()).setWidth(50);
		table.addColumn(false, "Type", "type", service.getPropertyTypeFormatter());
		table.addColumn(false, "src", "src", service.getBasicFormatter()).setWidth(90);
		table.addColumn(false, "Line", "line", service.getNumberFormatter()).setWidth(40);
		table.addColumn(false, "class", "class", service.getClassNameFormatter());
		table.addColumn(false, "AppId", "apid", service.getIdFormatter("AP-"));

		table.setFilteredIn(pos.getColumnId(), CH.s("0"));
		setTable(table);

	}

	public static class Builder extends AbstractPortletBuilder<VortexWebF1AppPropertiesTablePortlet> {

		public Builder() {
			super(VortexWebF1AppPropertiesTablePortlet.class);
		}

		public static final String ID = "AgentF1PropertiesTablePortlet";

		@Override
		public VortexWebF1AppPropertiesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebF1AppPropertiesTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 Properties";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}
	}

	private static class StrikeThroughFormatter extends BasicWebCellFormatter {

		@Override
		public StringBuilder formatCellToText(Object o, StringBuilder sb) {
			return sb.append(((Object[]) o)[0]);
		}
		public String formatCellToText(Object o) {
			return (String) ((Object[]) o)[0];
		}
		@Override
		public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
			Object[] array = (Object[]) value;
			if (array[0] == null) {
				sb.append("[password not available]");
				cellStyle.append("style.font-style=italic|style.color=red");
				return;
			}
			Integer i = (Integer) array[1];
			if (i != null && i != 0)
				cellStyle.append("style.textDecoration=line-through");
			super.formatCellToHtml(array[0], sb, cellStyle);
		}

		@Override
		public Comparable getOrdinalValue(Object value) {
			return (Comparable) ((Object[]) value)[0];
		}

	}

	@Override
	protected Row createAndAddRow(AgentWebProperty node) {
		F1AppInstance ss = node.getSnapshot();
		String processUid = ss.getProcessUid();
		String hostName = ss.getHostName();
		String user = ss.getUserName();
		String appName = ss.getAppName();
		String className = ss.getMainClassName();
		F1AppProperty i = node.getObject();
		return addRow(processUid, user, hostName, className, i.getKey(), i.getIsSecure() ? null : i.getValue(), (int) i.getPosition(), i.getSourceType(), i.getSource(),
				i.getSourceLineNumber() == F1AppProperty.NO_LINE_NUMBER ? null : i.getSourceLineNumber(), appName, ss.getF1AppInstanceId());
	}

	@Override
	protected void updateRow(Row row, AgentWebProperty node) {
		throw new UnsupportedOperationException("updating properties not supported at this time!");
	}

	@Override
	protected Iterable<AgentWebProperty> getEntitiesForSnapshot(VortexClientF1AppState f1AppState) {
		return f1AppState.getProperties().values();
	}
}
