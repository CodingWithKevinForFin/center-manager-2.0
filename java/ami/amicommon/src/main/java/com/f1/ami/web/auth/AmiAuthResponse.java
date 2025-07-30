package com.f1.ami.web.auth;

/**
 * 
 * Represents the resulting status of an auth (login) attempt. The status should always be populated. The user be populated only on a successful status. The message should be
 * populated on an unsuccessful status
 * <P>
 * See {@link BasicAmiAuthResponse} for a default implementation
 * 
 */
public interface AmiAuthResponse {

	/**
	 * The password was incorrect for the supplied user name
	 */
	byte STATUS_BAD_PASSWORD = 1;

	/**
	 * The user name was not found
	 */
	byte STATUS_BAD_USERNAME = 2;

	/**
	 * The user name and password are valid for login and a user object is available via {@link #getUser()}
	 */
	byte STATUS_OKAY = 3;

	/**
	 * the user name's account is locked for some reason
	 */
	byte STATUS_ACCOUNT_LOCKED = 4;

	/**
	 * Authentication failed due to some external reason, not due to the user/password issue. Eg: the connection to the single sign on server is down
	 */
	byte STATUS_GENERAL_ERROR = 5;

	/**
	 * Authentication failed due to some user count exceeded for this jvm. Eg: the connection to the single sign on server is down
	 */
	byte STATUS_USER_COUNT_EXCEEDED = 6;

	/**
	 * The user name & password combination was not found
	 */
	byte STATUS_BAD_CREDENTIALS = 7;

	/**
	 * The service has been disabled and no logins are allowed
	 */
	byte STATUS_SERVICE_DISABLED = 9;

	/**
	 * @return either {@link #STATUS_BAD_PASSWORD}, {@link #STATUS_BAD_USERNAME},{@link #STATUS_GENERAL_ERROR}, {@link #STATUS_GENERAL_ERROR} or {@link #STATUS_OKAY}
	 */
	public byte getStatus();

	/**
	 * @return a short message to be displayed to the user
	 */
	public String getMessage();

	/**
	 * @return the user profile for this successful authentication
	 */
	public AmiAuthUser getUser();

}
