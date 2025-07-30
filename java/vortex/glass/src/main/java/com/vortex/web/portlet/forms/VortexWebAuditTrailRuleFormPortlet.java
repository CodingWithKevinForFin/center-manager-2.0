package com.vortex.web.portlet.forms;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.Message;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageAuditTrailRuleRequest;
import com.sso.messages.SsoGroupAttribute;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebLogger;
import com.vortex.client.VortexClientF1AppState.AgentWebObject;
import com.vortex.client.VortexClientF1AppState.AgentWebProcessor;
import com.vortex.web.VortexWebEyeService;

public class VortexWebAuditTrailRuleFormPortlet extends FormPortlet {
	private VortexWebEyeService service;
	private static final String sName = "ssog.Name";

	private FormPortletTextField nameField;
	private FormPortletButton submitButton;
	private FormPortletSelectField<Byte> ruleType;
	private FormPortletSelectField<Integer> logLevelType;
	private FormPortletTextField appNameField;
	private FormPortletTextField hostNameField;
	private FormPortletTextField userNameField;
	private FormPortletTextField logIdField;
	private FormPortletTextField dbStatementField;
	private FormPortletSelectField<String> msgClassField;
	private FormPortletTextField msgTopicField;
	private FormPortletTextField msgFieldsField;
	private FormPortletTextField dbUrlField;
	private FormPortletTextField f1MessageTypeField;
	private FormPortletTextField f1ProcessorField;
	private FormPortletTextField f1ProcessorNameField;
	private FormPortletTextField f1StateTypeField;

	public VortexWebAuditTrailRuleFormPortlet(PortletConfig config) {
		super(config);
		addButton(this.submitButton = new FormPortletButton("Create Rule"));

		this.logLevelType = new FormPortletSelectField<Integer>(Integer.class, "Min log level");
		for (Entry<Integer, String> e : SpeedLoggerLevels.LEVELS_2_LABEL_SORTED.entrySet())
			logLevelType.addOption(e.getKey(), e.getValue());
		logLevelType.setValue(SpeedLoggerLevels.WARNING);
		this.logIdField = new FormPortletTextField("Logger Id").setValue("*");
		this.appNameField = new FormPortletTextField("App Name").setValue("*");
		this.dbUrlField = new FormPortletTextField("Database URL").setValue("*");
		this.dbStatementField = new FormPortletTextField("Database Statement").setValue("*");
		this.msgClassField = new FormPortletSelectField<String>(String.class, "Type").addOption("FixMsgConnection$", "Fix");
		this.msgFieldsField = new FormPortletTextField("Fields").setValue("*");
		this.msgTopicField = new FormPortletTextField("Topic").setValue("*");
		this.f1MessageTypeField = new FormPortletTextField("Message class Name").setValue(Message.class.getName()).setWidth(300);
		this.f1StateTypeField = new FormPortletTextField("State Class Name").setValue(State.class.getName()).setWidth(300);
		this.f1ProcessorField = new FormPortletTextField("Processor Class Name").setValue(Processor.class.getName()).setWidth(300);
		this.f1ProcessorNameField = new FormPortletTextField("Processor Name Mask").setValue("*").setWidth(300);

		ruleType = new FormPortletSelectField<Byte>(Byte.class, "Rule Type");
		ruleType.addOption(F1AppAuditTrailRule.EVENT_TYPE_F1, "F1 Event");
		ruleType.addOption(F1AppAuditTrailRule.EVENT_TYPE_MSG, "Inbound / Outbound Message");
		ruleType.addOption(F1AppAuditTrailRule.EVENT_TYPE_LOG, "Logger Event");
		ruleType.addOption(F1AppAuditTrailRule.EVENT_TYPE_SQL, "Sql Command");
		addField(ruleType);
		addField(this.nameField = new FormPortletTextField("Audit Rule Name").setValue(""));
		addField(this.hostNameField = new FormPortletTextField("Host Name")).setValue("*");
		addField(this.userNameField = new FormPortletTextField("User Name")).setValue("*");
		//addField(logLevelType);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		updateRuleType();
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			VortexEyeManageAuditTrailRuleRequest request = nw(VortexEyeManageAuditTrailRuleRequest.class);
			VortexEyeAuditTrailRule rule = nw(VortexEyeAuditTrailRule.class);
			request.setRule(rule);
			rule.setRules(new HashMap<Short, String>());
			rule.setRuleType(ruleType.getValue());//TODO:populate
			switch (ruleType.getValue()) {
				case F1AppAuditTrailRule.EVENT_TYPE_LOG:
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_LOGGER_LOG_LEVEL, logLevelType);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_LOGGER_LOGGER_ID, logIdField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_PROCESS_APPNAME_MASK, appNameField);
					break;
				case F1AppAuditTrailRule.EVENT_TYPE_SQL:
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_SQL_DATABASE_URL_MASK, dbUrlField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_SQL_STATEMENT_MASK, dbStatementField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_PROCESS_APPNAME_MASK, appNameField);
					break;
				case F1AppAuditTrailRule.EVENT_TYPE_MSG:
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_MSG_CLASS_MASK, msgClassField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_MSG_TOPIC_MASK, msgTopicField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_MSG_FIELDS_MASK, msgFieldsField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_MSG_TOPIC_MASK, appNameField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_PROCESS_APPNAME_MASK, appNameField);
					break;
				case F1AppAuditTrailRule.EVENT_TYPE_F1:
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_F1_MESSAGE_CLASSNAME, f1MessageTypeField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_F1_STATE_CLASSNAME, f1StateTypeField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_F1_PROCESSOR_CLASSNAME, f1ProcessorField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_F1_PROCESSOR_MASK, f1ProcessorNameField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_MSG_TOPIC_MASK, appNameField);
					populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_PROCESS_APPNAME_MASK, appNameField);
					break;
			}
			populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_PROCESS_HOSTMACHINE_MASK, hostNameField);
			populateMap(rule.getRules(), F1AppAuditTrailRule.RULE_PROCESS_USER_MASK, userNameField);
			rule.setName(nameField.getValue());
			service.sendRequestToBackend(getPortletId(), request);
			close();
		}

		super.onUserPressedButton(button);
	}

	private void populateMap(Map<Short, String> sink, short key, FormPortletField<?> field) {
		Object value = field.getValue();
		if (SH.isnt(value) || "*".equals(value))
			return;
		sink.put(key, SH.toString(value));
	}

	private SsoGroupAttribute newAttribute(String key, String value, byte type) {
		SsoGroupAttribute r = nw(SsoGroupAttribute.class);
		r.setKey(key);
		r.setType(type);
		r.setValue(value);
		return r;
	}
	@Override
	protected boolean onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		super.onUserChangedValue(field, attributes);
		if (field == ruleType) {
			updateRuleType();
		}
		return true;
	}

	private void updateRuleType() {
		removeFieldNoThrow(logLevelType);
		removeFieldNoThrow(logIdField);
		removeFieldNoThrow(appNameField);
		removeFieldNoThrow(dbStatementField);
		removeFieldNoThrow(dbUrlField);
		removeFieldNoThrow(msgClassField);
		removeFieldNoThrow(msgFieldsField);
		removeFieldNoThrow(msgTopicField);
		removeFieldNoThrow(f1MessageTypeField);
		removeFieldNoThrow(f1ProcessorField);
		removeFieldNoThrow(f1ProcessorNameField);
		removeFieldNoThrow(f1StateTypeField);
		switch (ruleType.getValue()) {
			case F1AppAuditTrailRule.EVENT_TYPE_LOG:
				addField(appNameField);
				addField(logLevelType);
				addField(logIdField);
				break;
			case F1AppAuditTrailRule.EVENT_TYPE_SQL:
				addField(appNameField);
				addField(dbUrlField);
				addField(dbStatementField);
				break;
			case F1AppAuditTrailRule.EVENT_TYPE_MSG:
				addField(appNameField);
				addField(msgClassField);
				addField(msgTopicField);
				addField(msgFieldsField);
				break;
			case F1AppAuditTrailRule.EVENT_TYPE_F1:
				addField(appNameField);
				addField(f1MessageTypeField);
				addField(f1ProcessorField);
				addField(f1ProcessorNameField);
				addField(f1StateTypeField);
				break;
			default:
				break;
		}

	}

	public static class Builder extends AbstractPortletBuilder<VortexWebAuditTrailRuleFormPortlet> {

		public static final String ID = "AuditTrailRuleFormPortlet";

		public Builder() {
			super(VortexWebAuditTrailRuleFormPortlet.class);
			setUserCreatable(false);
		}

		@Override
		public VortexWebAuditTrailRuleFormPortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebAuditTrailRuleFormPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Audit Trail Rule Form Portlet";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 250;
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 450;
	}
	public void setTemplate(AgentWebObject awo) {
		VortexClientF1AppState appState = awo.getAppState();
		if (awo instanceof AgentWebLogger) {
			AgentWebLogger logger = (AgentWebLogger) awo;
			this.ruleType.setValue((F1AppAuditTrailRule.EVENT_TYPE_LOG));
			this.nameField.setValue(SH.afterLast(logger.getObject().getLoggerId(), '.'));
			this.hostNameField.setValue(logger.getAppState().getSnapshot().getHostName());
			this.userNameField.setValue(logger.getAppState().getSnapshot().getUserName());
			this.appNameField.setValue(logger.getAppState().getSnapshot().getAppName());
			this.logIdField.setValue(logger.getObject().getLoggerId());
			this.logLevelType.setValue((SpeedLoggerLevels.INFO));
		} else if (awo instanceof AgentWebProcessor) {
			AgentWebProcessor logger = (AgentWebProcessor) awo;
			this.ruleType.setValue((F1AppAuditTrailRule.EVENT_TYPE_F1));
			this.nameField.setValue(SH.afterLast(logger.getObject().getName(), '.'));
			this.hostNameField.setValue(logger.getAppState().getSnapshot().getHostName());
			this.userNameField.setValue(logger.getAppState().getSnapshot().getUserName());
			this.appNameField.setValue(logger.getAppState().getSnapshot().getAppName());
			this.f1ProcessorField.setValue(appState.getClassName(logger.getObject().getClassId()));
			this.f1ProcessorNameField.setValue(logger.getObject().getName());
			this.f1StateTypeField.setValue(appState.getClassName(logger.getObject().getStateTypeClassId()));
			this.f1MessageTypeField.setValue(appState.getClassName(logger.getObject().getActionTypeClassId()));
		}
		updateRuleType();
	}

}
