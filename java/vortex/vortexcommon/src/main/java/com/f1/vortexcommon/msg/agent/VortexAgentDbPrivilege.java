package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.DP")
public interface VortexAgentDbPrivilege extends VortexAgentDbEntity {

	int SELECT = 1;
	int INSERT = 2;
	int UPDATE = 4;
	int DELETE = 8;
	int CREATE = 16;
	int DROP = 32;
	int REFERENCES = 64;
	int INDEX = 128;
	int ALTER = 256;
	int CREATE_TEMP_TABLES = 512;
	int CREATE_VIEW = 1024;
	int SHOW_VIEW = 2048;
	int CREATE_ROUTINE = 4096;
	int EVENT = 8192;
	int TRIGGER = 16384;
	int LOCK_TABLES = 32768;
	int EXECUTE = 32768 * 2;
	int ALTER_ROUTINE = 32768 * 4;
	int OTHER = 32768 * 8;

	byte PID_DATABASE_ID = 2;
	byte PID_USER = 3;
	byte PID_TABLE_NAME = 4;
	byte PID_TYPE = 5;
	byte PID_DESCRIPTION = 7;

	@PID(PID_DATABASE_ID)
	public long getDatabaseId();
	public void setDatabaseId(long databaseId);

	@PID(PID_USER)
	public String getUser();
	public void setUser(String time);

	@PID(PID_TABLE_NAME)
	public String getTableName();
	public void setTableName(String name);

	@PID(PID_TYPE)
	public int getType();
	public void setType(int type);

	@PID(PID_DESCRIPTION)
	public String getDescription();
	public void setDescription(String description);

}
