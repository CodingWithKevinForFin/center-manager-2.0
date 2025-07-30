package com.vortex.web.portlet.tables;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.vortex.client.VortexClientCron;

public class VortexWebCronTabTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentCron, VortexClientCron> {

	public VortexWebCronTabTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_CRON);
		// TODO Auto-generated constructor stub
		String[] ids = { "id", "agentid", "rev", "sec", "min", "hr", "dotm", "mon", "dotw", "tz", "cmd", "now", "miid", "data", HOST, "sch", "exps" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Cron Tabs");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(true, "User", "agentid", service.getUserFormatter());
		table.addColumn(true, "Second", "sec", service.getBasicFormatter()).setWidth(50);
		table.addColumn(true, "Minute", "min", service.getBasicFormatter()).setWidth(50);
		table.addColumn(true, "Hour", "hr", service.getBasicFormatter()).setWidth(50);
		table.addColumn(true, "Month", "mon", service.getBasicFormatter()).setWidth(50);
		table.addColumn(true, "Day of Month", "dotm", service.getBasicFormatter()).setWidth(80);
		table.addColumn(true, "Day of Week", "dotw", service.getBasicFormatter()).setWidth(80);
		//table.addColumn(true, "Timezone", "tz", service.getBasicFormatter()).setWidth(50);
		table.addColumn(true, "Command", "cmd", service.getBasicFormatter()).setWidth(250).addCssClass("bold");
		table.addColumn(false, "Update Time", "now", service.getDateTimeWebCellFormatter());
		table.addColumn(false, "Revision #", "rev", service.getNumberFormatter());
		table.addColumn(false, "Id", "id", service.getIdFormatter("CR-"));
		table.addColumn(false, "Machine Id", "miid", service.getBasicFormatter());
		table.addColumn(false, "Schedule", "sch", service.getBasicFormatter());
		table.addColumn(true, "Expectation", "exps", service.getExpectationsStateFormatter());

		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		WebMenu r = super.createMenu(table);
		//List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		//children.add(new BasicWebMenuLink("Show History", true, "history"));
		//WebMenu r = new BasicWebMenu("test", true, children);
		return r;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		super.onContextMenu(table, action);
		//action = parseContext(action);
		//List<Row> selectedRows = table.getSelectedRows();
		//if ("history".equals(action)) {
		//List<Long> ids = new ArrayList<Long>();
		//AgentCron a = null;
		//for (Row row : selectedRows) {
		//a = (AgentCron) getIdToRevision().get(row.get("id"));
		//ids.add(a.getId());
		//}
		//if (null != a)
		//service.getHistory(AgentHistoryRequest.TYPE_CRON, ids, getPortletId(), false, false);
		//}

	}

	public static class Builder extends AbstractPortletBuilder<VortexWebCronTabTablePortlet> {

		public static final String ID = "cronTabTablePortlet";

		public Builder() {
			super(VortexWebCronTabTablePortlet.class);
		}

		@Override
		public VortexWebCronTabTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebCronTabTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Cron Tabs";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {

	}

	@Override
	protected Row createAndAddRow(VortexClientCron node) {
		// TODO Auto-generated method stub
		VortexAgentCron c = node.getData();
		return addRow(c.getId(), c.getUser(), c.getRevision(), c.getSecond(), c.getMinute(), c.getHour(), c.getDayOfMonth(), c.getMonth(), c.getDayOfWeek(), c.getTimeZone(),
				c.getCommand(), c.getNow(), c.getMachineInstanceId(), node, node.getHostName(), node.getScheduleText(), node.getExpectationState());
	}

	@Override
	protected void updateRow(Row row, VortexClientCron node) {
		VortexAgentCron c = node.getData();
		row.put("id", c.getId());
		row.put("agentid", c.getUser());
		row.put("rev", c.getRevision());
		row.put("sec", c.getSecond());
		row.put("min", c.getMinute());
		row.put("hr", c.getHour());
		row.put("dotm", c.getDayOfMonth());
		row.put("mon", c.getMonth());
		row.put("dotw", c.getDayOfWeek());
		row.put("tz", c.getTimeZone());
		row.put("cmd", c.getCommand());
		row.put("now", c.getNow());
		row.put("sch", node.getScheduleText());
		row.put("miid", c.getMachineInstanceId());
		row.put("exps", node.getExpectationState());

	}

}
