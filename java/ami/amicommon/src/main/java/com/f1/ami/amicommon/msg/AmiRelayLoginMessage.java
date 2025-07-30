package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.AMIAL")
public interface AmiRelayLoginMessage extends AmiRelayMessage {

	@PID(29)
	String getAppId();
	void setAppId(String appName);

	@PID(30)
	public String getOptions();
	public void setOptions(String options);

	@PID(31)
	public String getPlugin();
	public void setPlugin(String plugin);

}
