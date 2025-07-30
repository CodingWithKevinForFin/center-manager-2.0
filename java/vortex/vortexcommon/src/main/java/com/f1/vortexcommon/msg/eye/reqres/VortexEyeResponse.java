package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VE.R")
public interface VortexEyeResponse extends PartialMessage {

	@PID(51)
	public String getMessage();
	public void setMessage(String message);

	@PID(52)
	public boolean getOk();
	public void setOk(boolean message);

	@PID(53)
	public double getProgress();
	public void setProgress(double progress);
}
