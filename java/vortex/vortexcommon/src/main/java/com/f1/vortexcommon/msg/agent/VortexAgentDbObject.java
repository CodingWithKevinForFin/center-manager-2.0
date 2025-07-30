package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.DO")
public interface VortexAgentDbObject extends VortexAgentDbEntity {

	byte PROCEDURE = 1;
	byte TRIGGER = 2;
	byte CONSTRAINT = 3;
	byte INDEX = 4;

	byte PID_DATABASE_ID = 2;
	byte PID_NAME = 3;
	byte PID_DEFINITION = 4;
	byte PID_TYPE = 5;

	@PID(PID_DATABASE_ID)
	public long getDatabaseId();
	public void setDatabaseId(long databaseId);

	@PID(PID_NAME)
	public String getName();
	public void setName(String time);

	@PID(PID_DEFINITION)
	public String getDefinition();
	public void setDefinition(String condition);

	@PID(PID_TYPE)
	public byte getType();
	public void setType(byte type);

}
