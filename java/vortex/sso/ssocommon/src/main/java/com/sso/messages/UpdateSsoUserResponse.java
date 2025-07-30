package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.AR")
public interface UpdateSsoUserResponse extends SsoResponse {

	
	byte PID_SSO_USER=3;
	
	@PID(PID_SSO_USER)
	public SsoUser getSsoUser();
	public void setSsoUser(SsoUser user);

}
