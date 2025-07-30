package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.IDQ")
public interface VortexAgentInspectDbRequest extends VortexAgentRequest {

	byte PID_URL = 1;
	byte PID_PASSWORD = 2;
	byte PID_DB_TYPE = 3;
	byte PID_SERVER_ID = 4;

	@PID(PID_URL)
	public String getUrl();
	public void setUrl(String searchExpression);

	@PID(PID_PASSWORD)
	public String getPassword();
	public void setPassword(String password);

	@PID(PID_DB_TYPE)
	public byte getDbType();
	public void setDbType(byte type);

	@PID(PID_SERVER_ID)
	public long getServerId();
	public void setServerId(long serverId);

}
