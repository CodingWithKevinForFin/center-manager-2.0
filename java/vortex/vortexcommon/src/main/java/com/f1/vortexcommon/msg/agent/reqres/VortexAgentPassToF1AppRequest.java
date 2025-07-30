package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.povo.f1app.reqres.F1AppRequest;

@VID("F1.VA.PTFAQ")
public interface VortexAgentPassToF1AppRequest extends VortexAgentRequest {

	@PID(1)
	public void setF1AppRequest(F1AppRequest agentRequest);
	public F1AppRequest getF1AppRequest();

	@PID(3)
	public long getF1AppId();
	public void setF1AppId(long agentProcessUid);
}
