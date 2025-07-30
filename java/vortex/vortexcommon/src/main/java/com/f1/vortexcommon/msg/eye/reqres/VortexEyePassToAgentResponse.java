package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentResponse;

@VID("F1.VE.PTAR")
public interface VortexEyePassToAgentResponse extends VortexEyeResponse {

	@PID(1)
	public void setAgentResponse(VortexAgentResponse agentResponse);
	public VortexAgentResponse getAgentResponse();

}
