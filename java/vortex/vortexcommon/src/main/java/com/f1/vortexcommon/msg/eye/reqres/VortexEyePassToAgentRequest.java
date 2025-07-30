package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;

@VID("F1.VE.PTAQ")
public interface VortexEyePassToAgentRequest extends VortexEyeRequest {

	@PID(1)
	public void setAgentRequest(VortexAgentRequest agentRequest);
	public VortexAgentRequest getAgentRequest();

	@PID(2)
	public String getAgentMachineUid();
	public void setAgentMachineUid(String agentMachineUid);

	@PID(3)
	public String getAgentProcessUid();
	public void setAgentProcessUid(String agentProcessUid);
}
