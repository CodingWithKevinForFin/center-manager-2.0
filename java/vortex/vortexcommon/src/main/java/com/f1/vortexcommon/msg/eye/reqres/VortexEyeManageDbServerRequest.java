package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;

@VID("F1.VE.MDBSQ")
public interface VortexEyeManageDbServerRequest extends VortexEyeRequest {

	byte PID_DB_SERVER = 10;
	@PID(PID_DB_SERVER)
	public VortexAgentDbServer getDbServer();
	public void setDbServer(VortexAgentDbServer dbserver);

}
