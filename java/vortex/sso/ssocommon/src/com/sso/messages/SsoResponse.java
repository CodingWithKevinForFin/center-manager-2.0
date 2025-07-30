package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.SS.SR")
public interface SsoResponse extends PartialMessage {

	
	byte PID_MESSAGE=1;
	byte PID_OK=2;
	
	@PID(PID_MESSAGE)
	public String getMessage();
	public void setMessage(String message);

	@PID(PID_OK)
	public boolean getOk();
	public void setOk(boolean ok);
}
