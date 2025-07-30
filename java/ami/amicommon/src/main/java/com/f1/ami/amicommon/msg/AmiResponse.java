package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.AMIR")
public interface AmiResponse extends PartialMessage {

	@PID(42)
	public void setOk(boolean b);
	public boolean getOk();

	@PID(43)
	public void setMessage(String message);
	public String getMessage();

	@PID(44)
	public void setException(Exception message);
	public Exception getException();
}
