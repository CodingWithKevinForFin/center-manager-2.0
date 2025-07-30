package com.f1.ami.web.auth;

import com.f1.ami.amicommon.AmiPlugin;

/**
 * 
 * A single instance will be loaded at startup of the web server and used for authenticating users for login verification.
 * 
 */
public interface AmiAuthenticatorPlugin extends AmiPlugin {

	public static final String NAMESPACE_AMIADMIN_CLI = "AMIADMIN_CLI";
	public static final String NAMESPACE_AMIDB_JDBC = "AMIDB_JDBC";
	public static final String NAMESPACE_AMIDB_CLI = "AMIDB_CLI";
	public static final String NAMESPACE_AMIWEB_GUI = "AMIWEB_GUI";
	public static final String NAMESPACE_AMIWEB_REST = "AMIWEB_REST";

	/**
	 * 
	 * @param namespace
	 *            this is the value specified using the f1.appname property and is useful when there are multiple web servers that should have distinct login permissions.
	 * @param location
	 *            the ip address of the remote user (as gathered by using the http connection's remote ip)
	 * @param user
	 *            name passed into the user field on the login page
	 * @param password
	 *            password passed into the user field on the login page
	 * @return The result of doing the authentication, should never be null
	 */
	public AmiAuthResponse authenticate(String namespace, String location, String user, String password);

}
