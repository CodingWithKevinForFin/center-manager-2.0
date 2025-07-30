package com.f1.vortexcommon.msg.agent;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.DT")
public interface VortexAgentDbTable extends VortexAgentDbEntity {

	byte PID_DATABASE_ID = 2;
	byte PID_NAME = 3;
	byte PID_COMMENTS = 4;
	byte PID_DESCRIPTION = 5;
	byte PID_COLUMNS = 6;
	byte PID_CREATE_TIME = 7;

	@PID(PID_DATABASE_ID)
	public long getDatabaseId();
	public void setDatabaseId(long databaseId);

	@PID(PID_NAME)
	public String getName();
	public void setName(String time);

	@PID(PID_COMMENTS)
	public String getComments();
	public void setComments(String comments);

	@PID(PID_DESCRIPTION)
	public String getDescription();
	public void setDescription(String description);

	@PID(PID_COLUMNS)
	public Map<String, VortexAgentDbColumn> getColumns();
	public void setColumns(Map<String, VortexAgentDbColumn> tables);

	@PID(PID_CREATE_TIME)
	public long getCreateTime();
	public void setCreateTime(long createTime);

}
