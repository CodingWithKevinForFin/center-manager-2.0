package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.AQ")
public interface CreateSsoUserRequest extends CreateSsoGroupRequest {

	byte PID_USER = 12;

	@PID(PID_USER)
	public SsoUser getUser();
	public void setUser(SsoUser user);

}
