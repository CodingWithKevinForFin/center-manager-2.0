package com.f1.ami.web.auth;

import java.util.Map;

import com.f1.utils.SH;

public class BasicAmiAuthResponse implements AmiAuthResponse {

	final private byte status;
	final private String message;
	final private AmiAuthUser user;

	public static class Okay extends BasicAmiAuthResponse {
		public Okay(String username, Map<String, Object> attributes) {
			super(STATUS_OKAY, null, new BasicAmiAuthUser(username, attributes));
		}
	}

	public static class BadCredentials extends BasicAmiAuthResponse {
		public BadCredentials() {
			this(null);
		}
		public BadCredentials(String message) {
			super(STATUS_BAD_CREDENTIALS, message, null);
		}
	}

	public static class BadUsername extends BasicAmiAuthResponse {
		public BadUsername() {
			this(null);
		}
		public BadUsername(String message) {
			super(STATUS_BAD_USERNAME, message, null);
		}
	}

	public static class BadPassword extends BasicAmiAuthResponse {
		public BadPassword() {
			this(null);
		}
		public BadPassword(String message) {
			super(STATUS_BAD_PASSWORD, message, null);
		}
	}

	public static class AccountLocked extends BasicAmiAuthResponse {
		public AccountLocked() {
			this(null);
		}
		public AccountLocked(String message) {
			super(STATUS_ACCOUNT_LOCKED, message, null);
		}
	}

	public static class GeneralError extends BasicAmiAuthResponse {
		public GeneralError() {
			this(null);
		}
		public GeneralError(String message) {
			super(STATUS_GENERAL_ERROR, message, null);
		}
	}

	public BasicAmiAuthResponse(byte status, String message, AmiAuthUser user) {
		super();
		this.status = status;
		this.message = message;
		this.user = user;
	}

	@Override
	public byte getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public AmiAuthUser getUser() {
		return user;
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
			default:
				return "AUTH_STATUS_" + SH.toString(status);
		}
	}

}
