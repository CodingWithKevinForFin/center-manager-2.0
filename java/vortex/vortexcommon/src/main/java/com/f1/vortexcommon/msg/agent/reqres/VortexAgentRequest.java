package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.Q")
public interface VortexAgentRequest extends PartialMessage {

	@PID(40)
	public String getTargetAgentProcessUid();
	public void setTargetAgentProcessUid(String processUid);

	@PID(41)
	public String getInvokedBy();
	public void setInvokedBy(String buildProcedureId);

}
