package com.f1.ami.amicommon;

import com.f1.ami.web.auth.AmiAuthResponse;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.base.Password;
import com.f1.console.impl.ConsoleAuthenticator;
import com.f1.console.impl.ConsoleAuthenticatorResponse;

public class AmiConsoleAuthenticator implements ConsoleAuthenticator {

	final private AmiAuthenticatorPlugin inner;

	@Override
	public ConsoleAuthenticatorResponse authenticate(String namespace, String location, String username, Password password) {
		final AmiAuthResponse result = inner.authenticate(AmiAuthenticatorPlugin.NAMESPACE_AMIADMIN_CLI, location, username, password.getPasswordString());
		if (result == null)
			return null;
		final AmiAuthUser u = result.getUser();
		byte status = mapStatus(result.getStatus());
		return new ConsoleAuthenticatorResponse(status, result.getMessage(), u == null ? null : u.getUserName(), u == null ? null : u.getAuthAttributes());
	}

	private byte mapStatus(byte status) {
		switch (status) {
			case AmiAuthResponse.STATUS_BAD_PASSWORD:
				return ConsoleAuthenticatorResponse.STATUS_BAD_PASSWORD;
			case AmiAuthResponse.STATUS_BAD_USERNAME:
				return ConsoleAuthenticatorResponse.STATUS_BAD_USERNAME;
			case AmiAuthResponse.STATUS_ACCOUNT_LOCKED:
				return ConsoleAuthenticatorResponse.STATUS_ACCOUNT_LOCKED;
			case AmiAuthResponse.STATUS_GENERAL_ERROR:
				return ConsoleAuthenticatorResponse.STATUS_GENERAL_ERROR;
			case AmiAuthResponse.STATUS_USER_COUNT_EXCEEDED:
				return ConsoleAuthenticatorResponse.STATUS_USER_COUNT_EXCEEDED;
			case AmiAuthResponse.STATUS_OKAY:
				return ConsoleAuthenticatorResponse.STATUS_OKAY;
			case AmiAuthResponse.STATUS_BAD_CREDENTIALS:
				return ConsoleAuthenticatorResponse.STATUS_BAD_CREDENTIALS;
			case AmiAuthResponse.STATUS_SERVICE_DISABLED:
				return ConsoleAuthenticatorResponse.STATUS_SERVICE_DISABLED;
			default:
				return ConsoleAuthenticatorResponse.STATUS_GENERAL_ERROR;
		}
	}

	public AmiConsoleAuthenticator(AmiAuthenticatorPlugin inner) {
		this.inner = inner;
	}

}
