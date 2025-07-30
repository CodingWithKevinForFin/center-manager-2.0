package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;

@VID("F1.VA.IDR")
public interface VortexAgentInspectDbResponse extends VortexAgentResponse {

	byte PID_START_TIME = 1;
	byte PID_END_TIME = 2;
	byte PID_DB_SERVER = 3;
	byte PID_MACHINE_UID = 4;

	@PID(PID_START_TIME)
	public long getStartTime();
	public void setStartTime(long startTime);

	@PID(PID_END_TIME)
	public long getEndTime();
	public void setEndTime(long endTime);

	@PID(PID_DB_SERVER)
	public VortexAgentDbServer getDbServer();
	public void setDbServer(VortexAgentDbServer databases);

	@PID(PID_MACHINE_UID)
	public String getMachineUid();
	public void setMachineUid(String machineUid);

}
