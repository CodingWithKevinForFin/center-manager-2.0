package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.AMICN")
public interface AmiRelayConnectionMessage extends AmiRelayMessage {

	@PID(9)
	String getRemoteIp();
	void setRemoteIp(String remoteIp);

	@PID(12)
	int getRemotePort();
	void setRemotePort(int remoteIp);

	@PID(11)
	public long getConnectionTime();
	public void setConnectionTime(long connectionId);

}
