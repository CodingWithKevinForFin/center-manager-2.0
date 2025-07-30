package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.povo.f1app.reqres.F1AppResponse;

@VID("F1.VA.PTFAR")
public interface VortexAgentPassToF1AppResponse extends VortexAgentResponse {

	@PID(1)
	public void setF1AppResponse(F1AppResponse agentResponse);
	public F1AppResponse getF1AppResponse();

}
