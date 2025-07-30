package com.f1.povo.f1app.audit;

import java.util.Map;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.povo.f1app.F1AppEntity;

@VID("F1.FA.FATR")
public interface F1AppAuditTrailRule extends F1AppEntity, Lockable {

	//general
	short RULE_PROCESS_HOSTMACHINE_MASK = 2;//STRING(text pattern)
	short RULE_PROCESS_USER_MASK = 3;//STRING(text pattern)
	short RULE_PROCESS_APPNAME_MASK = 4;//STRING(text pattern)

	//logging
	short RULE_LOGGER_LOG_LEVEL = 10;//INTEGER
	short RULE_LOGGER_LOGGER_ID = 11;//STRING(text pattern)
	short RULE_LOGGER_SINK_ID = 12;//STRING(text pattern)

	//sql
	short RULE_SQL_DATABASE_URL_MASK = 20;//STRING(text pattern)
	short RULE_SQL_STATEMENT_MASK = 21;//STRING(text pattern)

	//msg
	short RULE_MSG_TOPIC_MASK = 30;//STRING(text pattern)
	short RULE_MSG_FIELDS_MASK = 31;//STRING(text pattern)
	short RULE_MSG_CLASS_MASK = 32;//STRING(text pattern)

	short RULE_F1_PROCESSOR_MASK = 40;//STRING(text pattern)
	short RULE_F1_PROCESSOR_CLASSNAME = 41;//STRING(class name)
	short RULE_F1_MESSAGE_CLASSNAME = 42;//STRING(class name)
	short RULE_F1_STATE_CLASSNAME = 43;//STRING(class name)

	byte EVENT_TYPE_LOG = 1;
	byte EVENT_TYPE_SQL = 2;
	byte EVENT_TYPE_MSG = 3;
	byte EVENT_TYPE_F1 = 4;

	byte PID_RULE_TYPE = 1;
	byte PID_RULES = 2;
	byte PID_NAME = 3;
	byte PID_REVISION = 21;

	@PID(PID_RULE_TYPE)
	public byte getRuleType();
	public void setRuleType(byte eventType);

	@PID(PID_RULES)
	public Map<Short, String> getRules();
	public void setRules(Map<Short, String> masks);

	@PID(PID_NAME)
	public String getName();
	public void setName(String name);

	public F1AppAuditTrailRule clone();

	@PID(PID_REVISION)
	public int getRevision();
	public void setRevision(int revision);
}
