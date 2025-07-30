package com.f1.vortexcommon.msg.agent;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;

@VID("F1.VA.DS")
public interface VortexAgentDbServer extends VortexEyeEntity, VortexMetadatable {

	byte STATUS_CREATED = 1;
	byte STATUS_CONNECTION_ERROR = 2;
	//byte STATUS_AGENT_NOT_RUNNING = 3;
	byte STATUS_INSPECTING = 4;
	byte STATUS_OKAY = 5;
	byte STATUS_MODIFIED = 6;
	byte STATUS_GENERAL_ERROR = 7;

	byte TYPE_MYSQL = 1;
	byte TYPE_SYBASE = 2;
	byte TYPE_ORACLE = 3;
	byte TYPE_SQLSERVER = 4;

	byte PID_MACHINE_UID = 4;
	byte PID_URL = 5;
	byte PID_DATABASES = 6;
	byte PID_PASSWORD = 9;
	byte PID_DB_TYPE = 11;
	byte PID_DESCRIPTION = 12;

	@PID(PID_MACHINE_UID)
	public String getMachineUid();
	public void setMachineUid(String processUid);

	@PID(PID_URL)
	public String getUrl();
	public void setUrl(String url);

	@PID(PID_DATABASES)
	public Map<String, VortexAgentDbDatabase> getDatabases();
	public void setDatabases(Map<String, VortexAgentDbDatabase> tables);

	@PID(PID_PASSWORD)
	public String getPassword();
	public void setPassword(String password);

	@PID(PID_DB_TYPE)
	public byte getDbType();
	public void setDbType(byte type);

	@PID(PID_DESCRIPTION)
	public String getDescription();
	public void setDescription(String description);

	byte PID_STATUS = 31;
	@PID(PID_STATUS)
	public byte getStatus();
	public void setStatus(byte status);

	byte PID_MESSAGE = 35;
	@PID(PID_MESSAGE)
	public String getMessage();
	public void setMessage(String message);

	byte PID_INVOKED_BY = 36;
	@PID(PID_INVOKED_BY)
	public String getInvokedBy();
	public void setInvokedBy(String message);

	byte PID_INSPECTED_TIME = 37;
	@PID(PID_INSPECTED_TIME)
	public void setInspectedTime(long now);
	public long getInspectedTime();

	byte PID_SERVER_PORT = 38;
	@PID(PID_SERVER_PORT)
	public void setServerPort(int now);
	public int getServerPort();

	byte PID_HINTS = 40;
	@PID(PID_HINTS)
	public void setHints(String now);
	public String getHints();

	public VortexAgentDbServer clone();

}
