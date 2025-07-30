package com.f1.console.impl;

import java.util.Collections;

import com.f1.base.Password;

public class BasicConsoleAuthenticator implements ConsoleAuthenticator {

	@Override
	public ConsoleAuthenticatorResponse authenticate(String namespace, String location, String user, Password password) {
		return new ConsoleAuthenticatorResponse(ConsoleAuthenticatorResponse.STATUS_OKAY, "Dumy authenticator passwed", user, Collections.EMPTY_MAP);
	}

}
