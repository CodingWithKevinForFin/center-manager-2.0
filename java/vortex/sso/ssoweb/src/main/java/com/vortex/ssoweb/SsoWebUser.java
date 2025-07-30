package com.vortex.ssoweb;

import com.f1.suite.web.WebUser;
import com.sso.messages.SsoUser;

public class SsoWebUser implements WebUser {

	private SsoUser ssoUser;

	public SsoWebUser(SsoUser ssoUser) {
		this.ssoUser = ssoUser;
	}

	public SsoUser getSsoUser() {
		return ssoUser;
	}

	@Override
	public String getUserName() {
		return ssoUser.getUserName();
	}

}
