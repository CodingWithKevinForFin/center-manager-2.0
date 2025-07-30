package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VE.Q")
public interface VortexEyeRequest extends PartialMessage {

	@PID(51)
	public String getInvokedBy();
	public void setInvokedBy(String invokedBy);

	@PID(52)
	public String getComment();
	public void setComment(String comment);

}
