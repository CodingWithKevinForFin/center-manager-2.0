package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;

@VID("F1.VE.MDBSR")
public interface VortexEyeManageDbServerResponse extends VortexEyeResponse {

	@PID(10)
	public VortexAgentDbServer getDbServer();
	public void setDbServer(VortexAgentDbServer dbserver);

}
