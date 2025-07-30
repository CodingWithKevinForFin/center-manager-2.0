package com.f1.console.impl;

import java.util.Map;

import com.f1.utils.SH;

public class ConsoleAuthenticatorResponse {

	/**
	 * the password was incorrect for the supplied user name
	 */
	public static final byte STATUS_BAD_PASSWORD = 1;

	/**
	 * The user name was not found
	 */
	public static final byte STATUS_BAD_USERNAME = 2;

	/**
	 * The user name and password are valid for login and a user object is available via {@link #getUser()}
	 */
	public static final byte STATUS_OKAY = 3;

	/**
	 * the user name's account is locked for some reason
	 */
	public static final byte STATUS_ACCOUNT_LOCKED = 4;

	/**
	 * Authentication failed do to some external reason, not due to the user/password issue. Eg: the connection to the single sign on server is down
	 */
	public static final byte STATUS_GENERAL_ERROR = 5;

	/**
	 * Authentication failed do to some user count exceeded for this jvm. Eg: the connection to the single sign on server is down
	 */
	public static final byte STATUS_USER_COUNT_EXCEEDED = 6;

	/**
	 * The user name & password combination was not found
	 */
	public static final byte STATUS_BAD_CREDENTIALS = 7;
	public static final byte STATUS_AUTH_SERVICE_NOT_STARTED = 8;
	public static final byte STATUS_SERVICE_DISABLED = 9;

	private final byte status;
	private final String message;
	private final String username;
	private final Map<String, Object> attributes;

	public byte getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}
	public String getUsername() {
		return username;
	}
	public ConsoleAuthenticatorResponse(byte status, String message, String username, Map<String, Object> map) {
		this.status = status;
		this.message = message;
		this.username = username;
		this.attributes = map;
	}

	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	public static String toStringForStatus(byte status) {
		switch (status) {
			case STATUS_BAD_PASSWORD:
				return "BAD_PASSWORD";
			case STATUS_BAD_USERNAME:
				return "BAD_USERNAME";
			case STATUS_ACCOUNT_LOCKED:
				return "ACCOUNT_LOCKED";
			case STATUS_GENERAL_ERROR:
				return "INTERNAL_ERROR";
			case STATUS_USER_COUNT_EXCEEDED:
				return "USER_COUNT_EXCEEDED";
			case STATUS_OKAY:
				return "OKAY";
			case STATUS_BAD_CREDENTIALS:
				return "BAD_CREDENTIALS";
			case STATUS_SERVICE_DISABLED:
				return "SERVICE_DISABLED";
			case STATUS_AUTH_SERVICE_NOT_STARTED:
				return "AUTH_SERVICE_NOT_STARTED";
			default:
				return "AUTH_STATUS_" + SH.toString(status);
		}
	}
}
