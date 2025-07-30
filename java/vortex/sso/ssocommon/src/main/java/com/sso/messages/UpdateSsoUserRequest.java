package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.UQ")
public interface UpdateSsoUserRequest extends SsoRequest {

	byte PID_SSO_USER_ID = 2;
	byte PID_SSO_USER = 3;
	byte PID_USER_SUPPLIED_PASSWORD = 4;
	byte PID_USER_SUPPLIED_ANSWER = 5;
	byte PID_ENCODING_ALGORITHM = 6;

	@PID(PID_SSO_USER_ID)
	public long getSsoUserId();
	public void setSsoUserId(long ssoUserId);

	@PID(PID_SSO_USER)
	public SsoUser getSsoUser();
	public void setSsoUser(SsoUser user);

	@PID(PID_USER_SUPPLIED_PASSWORD)
	public String getUserSuppliedPassword();
	public void setUserSuppliedPassword(String userSuppliedPassword);

	@PID(PID_USER_SUPPLIED_ANSWER)
	public String getUserSuppliedAnswer();
	public void setUserSuppliedAnswer(String userSuppliedAnswer);

	@PID(PID_ENCODING_ALGORITHM)
	public byte getEncodingAlgorithm();
	public void setEncodingAlgorithm(byte encodingAlgorithm);

}
