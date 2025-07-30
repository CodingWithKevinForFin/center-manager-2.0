package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.base.Row;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapEntryWebCellFormatter;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageAuditTrailRuleRequest;
import com.vortex.client.VortexClientAuditTrailRule;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.portlet.forms.VortexWebAuditTrailRuleFormPortlet;

public class VortexWebAuditRulesTablePortlet extends VortexWebTablePortlet implements VortexClientMachineListener, WebContextMenuFactory, WebContextMenuListener {

	private LongKeyMap<Row> rows = new LongKeyMap<Row>();

	public VortexWebAuditRulesTablePortlet(PortletConfig config) {
		super(config, null);
		String[] ids = { "id", "name", "now", "type", "rules", "rules_map" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Audit Rules");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Name", "name", service.getBasicFormatter()).setWidth(150).addCssClass("bold");
		table.addColumn(true, "Type", "type", service.getRuleTypeFormatter());
		table.addColumn(true, "App Name mask", "rules_map", new MapEntryWebCellFormatter(F1AppAuditTrailRule.RULE_PROCESS_APPNAME_MASK).setNullValue("*")).setCssColumn("italic");
		table.addColumn(true, "Host mask", "rules_map", new MapEntryWebCellFormatter(F1AppAuditTrailRule.RULE_PROCESS_HOSTMACHINE_MASK).setNullValue("*")).setCssColumn("italic");
		table.addColumn(true, "Owner mask", "rules_map", new MapEntryWebCellFormatter(F1AppAuditTrailRule.RULE_PROCESS_USER_MASK).setNullValue("*")).setCssColumn("italic");
		table.addColumn(false, "Updated", "now", service.getDateTimeWebCellFormatter());
		table.addColumn(false, "All Rules", "rules", service.getBasicFormatter()).setWidth(250);
		table.addColumn(false, "Logger Min Level Mask", "rules_map", new MapEntryWebCellFormatter(F1AppAuditTrailRule.RULE_LOGGER_LOG_LEVEL)).setCssColumn("italic");
		table.addColumn(false, "Logger Id Mask", "rules_map", new MapEntryWebCellFormatter(F1AppAuditTrailRule.RULE_LOGGER_LOGGER_ID)).setCssColumn("italic");
		table.addColumn(false, "Message Type Mask", "rules_map", new MapEntryWebCellFormatter(F1AppAuditTrailRule.RULE_MSG_CLASS_MASK)).setCssColumn("italic");
		table.addColumn(false, "Message fields Mask", "rules_map", new MapEntryWebCellFormatter(F1AppAuditTrailRule.RULE_MSG_FIELDS_MASK)).setCssColumn("italic");
		table.addColumn(false, "Message Topic Mask", "rules_map", new MapEntryWebCellFormatter(F1AppAuditTrailRule.RULE_MSG_TOPIC_MASK)).setCssColumn("italic");
		table.addColumn(false, "Database Url Mask", "rules_map", new MapEntryWebCellFormatter(F1AppAuditTrailRule.RULE_SQL_DATABASE_URL_MASK)).setCssColumn("italic");
		table.addColumn(false, "Database Statement Mask", "rules_map", new MapEntryWebCellFormatter(F1AppAuditTrailRule.RULE_SQL_STATEMENT_MASK)).setCssColumn("italic");
		table.addColumn(false, "Id", "id", service.getIdFormatter("AI-"));
		table.setMenuFactory(this);
		//table.addMenuListener(this);
		agentManager.addMachineListener(this);
		setTable(table);
		for (VortexClientAuditTrailRule rule : service.getAgentManager().getAuditTrailRules())
			onMachineEntityAdded(rule);
	}
	@Override
	public void close() {
		agentManager.removeMachineListener(this);
		super.close();
	}

	@Override
	public void onMachineAdded(VortexClientMachine machine) {
	}

	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_AUDIT_EVENT_RULE) {
			VortexClientAuditTrailRule rule = (VortexClientAuditTrailRule) node;
			addAuditRuleRow(rule);
		}
	}

	private void addAuditRuleRow(VortexClientAuditTrailRule node) {
		VortexEyeAuditTrailRule rule = node.getData();
		Row existing = rows.get(node.getId());
		String name = rule.getName();
		long now = rule.getNow();
		byte type = rule.getRuleType();
		String rules = node.getRules();
		Map<Short, String> rulesMap = node.getRulesMap();
		if (existing == null) {
			rows.put(node.getId(), addRow(node.getId(), name, now, type, rules, rulesMap));
		} else {
			existing.put("name", name);
			existing.put("now", now);
			existing.put("type", type);
			existing.put("rules", rules);
			existing.put("rules_map", rulesMap);
		}
	}
	private void removeAuditRuleRow(VortexClientAuditTrailRule node) {
		Row existing = rows.remove(node.getId());
		if (existing != null)
			removeRow(existing);
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_AUDIT_EVENT_RULE) {
			VortexClientAuditTrailRule rule = (VortexClientAuditTrailRule) node;
			removeAuditRuleRow(rule);
		}
	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
		rows.clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebAuditRulesTablePortlet> {

		public static final String ID = "AgentF1AuditRulesTablePortlet";

		public Builder() {
			super(VortexWebAuditRulesTablePortlet.class);
		}

		@Override
		public VortexWebAuditRulesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebAuditRulesTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 Audit Rules Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("create".equals(action))
			getManager().showDialog("Add Rule", new VortexWebAuditTrailRuleFormPortlet(generateConfig()));
		else if ("delete".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				VortexEyeManageAuditTrailRuleRequest request = nw(VortexEyeManageAuditTrailRuleRequest.class);
				VortexEyeAuditTrailRule rule = nw(VortexEyeAuditTrailRule.class);
				rule.setId(row.get("id", Long.class));
				rule.setRevision(VortexAgentEntity.REVISION_DONE);
				request.setRule(rule);
				service.sendRequestToBackend(getPortletId(), request);
			}
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
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> items = new ArrayList<WebMenuItem>();
		items.add(new BasicWebMenuLink("Create Rule", true, "create"));
		items.add(new BasicWebMenuLink("Delete Rule", true, "delete"));
		items.add(new BasicWebMenuLink("Update Rule", true, "update"));
		return new BasicWebMenu("", true, items);
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEyeSnapshotProcessed() {
		// TODO Auto-generated method stub

	}

}
