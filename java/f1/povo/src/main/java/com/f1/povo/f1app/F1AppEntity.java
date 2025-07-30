package com.f1.povo.f1app;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.base.ValuedListenable;

@VID("F1.FA.E")
public interface F1AppEntity extends Message, ValuedListenable {

	byte TYPE_CLASS = 1;
	byte TYPE_CONTAINER_SCOPE = 2;
	byte TYPE_CONTAINER_STATE = 3;
	byte TYPE_CONTAINER_PARTITION = 4;
	byte TYPE_CONTAINER_THREADSCOPE = 5;
	byte TYPE_LOGGER = 6;
	byte TYPE_LOGGER_SINK = 7;
	byte TYPE_DATABASE = 8;
	byte TYPE_MSG_TOPIC = 9;
	byte TYPE_PROPERTY = 10;
	byte TYPE_APP_INSTANCE = 11;

	byte PID_OBJECT_TYPE = 100;
	byte PID_CLASS_ID = 101;
	byte PID_STARTED_MS = 102;
	byte PID_AUDIT_RULES_COUNT = 103;
	byte PID_ID = 105;
	byte PID_F1_APP_INSTANCE_ID = 106;

	@PID(PID_OBJECT_TYPE)
	public byte getObjectType();
	public void setObjectType(byte type);

	@PID(PID_CLASS_ID)
	public long getClassId();
	public void setClassId(long id);

	@PID(PID_STARTED_MS)
	public long getStartedMs();
	public void setStartedMs(long startedMs);

	@PID(PID_ID)
	public long getId();
	public void setId(long id);

	@PID(PID_AUDIT_RULES_COUNT)
	public byte getAuditRulesCount();
	public void setAuditRulesCount(byte type);

	@PID(PID_F1_APP_INSTANCE_ID)
	public long getF1AppInstanceId();
	public void setF1AppInstanceId(long f1AppInstanceId);

	public F1AppEntity clone();

}
