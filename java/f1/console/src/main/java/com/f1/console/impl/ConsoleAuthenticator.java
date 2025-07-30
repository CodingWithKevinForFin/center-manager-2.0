package com.f1.console.impl;

import com.f1.base.Password;

/**
 * 
 * A single instance will be loaded at startup of the web server and used for authenticating users for login verification.
 * 
 */
public interface ConsoleAuthenticator {

	public ConsoleAuthenticatorResponse authenticate(String namespace, String location, String user, Password password);

}
