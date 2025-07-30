package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.EV")
public interface VortexAgentEvent extends VortexAgentEntity {

	byte SEVERITY_NORMAL = 1;
	byte SEVERITY_WARNING = 2;
	byte SEVERITY_SEVERE = 3;

	int STATUS_UNKNOWN = 1;
	int STATUS_OK = 2;
	int STATUS_NOT_OK = 3;
	int STATUS_BAD = 4;

	byte PID_SEVERITY = 1;
	byte PID_SUB_TYPE = 2;
	byte PID_MESSAGE = 3;
	byte PID_STATUS = 5;
	byte PID_AGENT_ID = 6;

	@PID(PID_SEVERITY)
	public byte getSeverity();
	public void setSeverity(byte severity);

	@PID(PID_SUB_TYPE)
	public int getSubType();
	public void setSubType(int subType);

	@PID(PID_MESSAGE)
	public String getMessage();
	public void setMessage(String message);

	@PID(PID_STATUS)
	public int getStatus();
	public void setStatus(int status);

	//TODO: remove
	@PID(PID_AGENT_ID)
	public long getAgentId();
	public void setAgentId(long agentId);

}
