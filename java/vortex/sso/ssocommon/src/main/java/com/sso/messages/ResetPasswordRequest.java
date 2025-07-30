package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.EQ")
public interface ResetPasswordRequest extends SsoRequest {

	byte PID_USER_NAME = 2;
	byte PID_EMAIL = 3;
	byte PID_RESET_ANSWER = 4;
	byte PID_ENCODING_ALGORITHM = 5;

	@PID(PID_USER_NAME)
	public String getUserName();
	public void setUserName(String user);

	@PID(PID_EMAIL)
	public String getEmail();
	public void setEmail(String email);

	@PID(PID_RESET_ANSWER)
	public String getResetAnswer();
	public void setResetAnswer(String resetAnswer);

	@PID(PID_ENCODING_ALGORITHM)
	public byte getEncodingAlgorithm();
	public void setEncodingAlgorithm(byte encodingAlgorithm);
}
