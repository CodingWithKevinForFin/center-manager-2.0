package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.LQ")
public interface LoginSsoUserRequest extends SsoRequest {

	byte PID_USER_NAME = 2;
	byte PID_EMAIL = 3;
	byte PID_PASSWORD = 4;
	byte PID_ENCODING_ALGORITHM = 5;
	byte PID_NAMESPACE = 6;

	@PID(PID_USER_NAME)
	public String getUserName();
	public void setUserName(String name);

	@PID(PID_EMAIL)
	public String getEmail();
	public void setEmail(String name);

	@PID(PID_PASSWORD)
	public String getPassword();
	public void setPassword(String name);

	@PID(PID_ENCODING_ALGORITHM)
	public byte getEncodingAlgorithm();
	public void setEncodingAlgorithm(byte encodingAlgorithm);

	@PID(PID_NAMESPACE)
	public void setNamespace(String namespace);
	public String getNamespace();

}
