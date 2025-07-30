package com.f1.povo.f1app.reqres;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.FA.R")
public interface F1AppResponse extends PartialMessage {

	@PID(21)
	public boolean getOk();
	public void setOk(boolean ok);

	@PID(22)
	public String getMessage();
	public void setMessage(String message);

}
