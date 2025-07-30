package com.f1.ami.amicommon.msg;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.Password;
import com.f1.base.VID;

@VID("F1.WM.LIQ")
public interface AmiWebLoginRequest extends Message {

	@PID(1)
	public String getUserName();
	public void setUserName(String name);

	@PID(3)
	public Password getPassword();
	public void setPassword(Password name);

	@PID(7)
	public void setClientAgent(String userAgent);
	public String getClientAgent();

	@PID(8)
	public void setClientLocation(String clientLocation);
	public String getClientLocation();

}
