package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.SS.SQ")
public interface SsoRequest extends PartialMessage {

	byte PID_SESSION = 0;
	byte PID_NAMESPACE = 1;

	@PID(PID_SESSION)
	public String getSession();
	public void setSession(String session);

	@PID(PID_NAMESPACE)
	public String getNamespace();
	public void setNamespace(String namespace);
}
