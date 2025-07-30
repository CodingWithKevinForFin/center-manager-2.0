package com.f1.vortexcommon.msg.agent;

import java.util.List;
import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.DD")
public interface VortexAgentDbDatabase extends VortexAgentDbEntity {

	byte PID_NAME = 3;
	byte PID_TABLES = 6;
	byte PID_OBJECTS = 7;
	byte PID_PRIVILEGES = 8;
	byte PID_DB_SERVER_ID = 11;

	@PID(PID_NAME)
	public String getName();
	public void setName(String time);

	@PID(PID_TABLES)
	public Map<String, VortexAgentDbTable> getTables();
	public void setTables(Map<String, VortexAgentDbTable> tables);

	@PID(PID_OBJECTS)
	public List<VortexAgentDbObject> getObjects();
	public void setObjects(List<VortexAgentDbObject> triggers);

	@PID(PID_PRIVILEGES)
	public List<VortexAgentDbPrivilege> getPrivileges();
	public void setPrivileges(List<VortexAgentDbPrivilege> privileges);

	@Override
	@PID(PID_DB_SERVER_ID)
	public long getDbServerId();
	public void setDbServerId(long dbServerId);
}
