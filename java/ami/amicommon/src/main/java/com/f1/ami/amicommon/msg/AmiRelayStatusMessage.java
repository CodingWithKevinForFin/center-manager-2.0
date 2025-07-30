package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.AMIAS")
public interface AmiRelayStatusMessage extends AmiRelayMessage {

	@PID(5)
	public String getMessage();
	public void setMessage(String message);

	@PID(7)
	void setStatus(int status);
	public int getStatus();

}
