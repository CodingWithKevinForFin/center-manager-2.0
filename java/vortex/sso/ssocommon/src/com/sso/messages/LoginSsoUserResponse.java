package com.sso.messages;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.LR")
public interface LoginSsoUserResponse extends SsoResponse {

	byte STATUS_OK = 1;
	byte STATUS_USER_NOT_FOUND = 2;
	byte STATUS_PASSWORD_INVALID = 3;
	byte STATUS_INTERNAL_ERROR = 4;
	byte STATUS_INVALID_ENCODING = 5;
	byte STATUS_ACCOUNT_INACTIVE = 6;

	byte PID_USER=3;
	byte PID_STATUS=4;
	byte PID_FAILED_LOGIN_ATTEMPTS=5;
	byte PID_GROUP=6;
	byte PID_GROUP_ATTRIBUTES=7;
	
	@PID(PID_USER)
	public SsoUser getUser();
	public void setUser(SsoUser user);

	@PID(PID_STATUS)
	public byte getStatus();
	public void setStatus(byte status);

	@PID(PID_FAILED_LOGIN_ATTEMPTS)
	public int getFailedLoginAttempts();
	public void setFailedLoginAttempts(int status);

	@PID(PID_GROUP)
	public SsoGroup getGroup();
	public void setGroup(SsoGroup user);

	@PID(PID_GROUP_ATTRIBUTES)
	public Map<String, SsoGroupAttribute> getGroupAttributes();
	public void setGroupAttributes(Map<String, SsoGroupAttribute> attributes);

}
