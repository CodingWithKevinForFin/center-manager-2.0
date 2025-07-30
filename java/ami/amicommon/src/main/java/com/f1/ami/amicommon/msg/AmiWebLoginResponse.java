package com.f1.ami.amicommon.msg;

import java.util.Map;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.WM.LIR")
public interface AmiWebLoginResponse extends Message {

	//See AmiAuthResponse
	@PID(2)
	public byte getStatus();
	public void setStatus(byte status);

	@PID(3)
	public int getFailedLoginAttempts();
	public void setFailedLoginAttempts(int status);

	@PID(4)
	public void setMessage(String message);
	public String getMessage();

	@PID(5)
	public void setAttributes(Map<String, Object> attributes);
	public Map<String, Object> getAttributes();

	@PID(6)
	public void setUsername(String username);
	public String getUsername();

}
