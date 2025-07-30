package com.sso.messages;

import com.f1.base.PID;

public interface SendEmailToSsoUserResponse extends SsoResponse {

	@PID(10)
	public SsoUser getUser();
	public void setUser(SsoUser user);

}
