package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.CR")
public interface CreateSsoUserResponse extends CreateSsoGroupResponse {

	byte PID_USER = 13;

	@PID(PID_USER)
	public SsoUser getUser();
	public void setUser(SsoUser user);

}
