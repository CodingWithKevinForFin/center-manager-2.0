package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.R")
public interface VortexAgentResponse extends PartialMessage {

	@PID(42)
	public void setOk(boolean b);
	public boolean getOk();

	@PID(43)
	public void setMessage(String message);
	public String getMessage();

}
