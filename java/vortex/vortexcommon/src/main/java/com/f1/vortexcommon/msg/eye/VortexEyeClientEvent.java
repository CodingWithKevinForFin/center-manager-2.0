package com.f1.vortexcommon.msg.eye;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.VECE")
public interface VortexEyeClientEvent extends VortexEyeEntity {

	byte TYPE_RUN_SCHEDULED_TASK = 1;
	byte TYPE_RUN_DEPLOYMENT = 2;
	byte TYPE_RUN_BUILD_PROCEDURE = 3;
	byte TYPE_RUN_BACKUP = 4;
	byte TYPE_QUERY_DATA = 5;
	byte TYPE_MANAGE_SCHEDULED_TASK = 6;
	byte TYPE_MANAGE_METADATA_FIELD = 7;
	byte TYPE_MANAGE_MACHINE = 8;
	byte TYPE_MANAGE_EXPECTATION = 9;
	byte TYPE_MANAGE_DEPLOYMENT_SET = 10;
	byte TYPE_MANAGE_DEPLOYMENT = 11;
	byte TYPE_MANAGE_BUILD_PROCEDURE = 12;
	byte TYPE_MANAGE_BUILD_RESULT = 13;
	byte TYPE_MANAGE_BACKUP = 14;
	byte TYPE_MANAGE_BACKUP_DESTINATION = 15;
	byte TYPE_MANAGE_AUDIT_RULE = 16;
	byte TYPE_RUN_INSPECT_DATASERVER = 17;
	byte TYPE_CREATE_DEPLOYMENT_ENVIRONMENT = 18;
	byte TYPE_RUN_SIGNAL_ON_PROCESS = 19;
	byte TYPE_DEPLOYMENT_DELETE_ALL_FILES = 20;
	byte TYPE_DEPLOYMENT_DEPLOY = 21;
	byte TYPE_DEPLOYMENT_GET_FILE = 22;
	byte TYPE_DEPLOYMENT_GET_FILE_STRUCTURE = 23;
	byte TYPE_DEPLOYMENT_RUN_SCRIPT = 24;
	byte TYPE_DEPLOYMENT_START_SCRIPT = 25;
	byte TYPE_DEPLOYMENT_STOP_SCRIPT = 26;
	byte TYPE_GET_FILES = 27;
	byte TYPE_APP_CHANGE_LOG_LEVEL = 28;
	byte TYPE_APP_INSPECT_PARTITION = 29;
	byte TYPE_APP_INTERRUPT_THREAD = 30;
	byte TYPE_DEPLOYMENT_VERIFY = 31;
	byte TYPE_DEPLOYMENT_CONFIG = 32;
	byte TYPE_NETWORK_SCAN = 33;
	byte TYPE_RUN_SHELL_COMMAND = 34;
	byte TYPE_INSTALL_AGENT = 35;
	byte TYPE_MANAGE_CLOUD_INTERFACE = 36;
	byte TYPE_GET_EYE_INFO = 37;
	byte TYPE_RUN_OS_COMMAND = 38;
	byte TYPE_DELETE_FILES = 43;
	byte TYPE_CI_COMMAND = 44;

	//byte TYPE_RUN_AMI_COMMAND = 39;
	//byte TYPE_MANAGE_AMI_ALERT = 40;
	//byte TYPE_GET_AMI_SCHEMA = 41;
	//byte TYPE_MANAGE_AMI_DATA = 42;

	@PID(10)
	public String getComment();
	public void setComment(String comment);

	@PID(11)
	public String getMessage();
	public void setMessage(String comment);

	@PID(13)
	public byte getEventType();
	public void setEventType(byte eventType);

	@PID(14)
	public String getTargetMachineUid();
	public void setTargetMachineUid(String eventType);

	@PID(15)
	public void setInvokedBy(String invokedBy);
	public String getInvokedBy();

	@PID(16)
	public Map<String, String> getParams();
	public void setParams(Map<String, String> eventParams);
}
