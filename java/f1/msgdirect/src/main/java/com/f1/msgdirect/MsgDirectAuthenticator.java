package com.f1.msgdirect;

public interface MsgDirectAuthenticator {

	/**
	 * true if allow
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean authenticate(String user, String password);
}
