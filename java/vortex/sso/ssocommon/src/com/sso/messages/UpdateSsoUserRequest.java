package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.UQ")
public interface UpdateSsoUserRequest extends SsoRequest {

	
	byte PID_SSO_USER_ID=2;
	byte PID_SSO_USER=3;
	
	@PID(PID_SSO_USER_ID)
	public long getSsoUserId();
	public void setSsoUserId(long ssoUserId);

	@PID(PID_SSO_USER)
	public SsoUser getSsoUser();
	public void setSsoUser(SsoUser user);

}
