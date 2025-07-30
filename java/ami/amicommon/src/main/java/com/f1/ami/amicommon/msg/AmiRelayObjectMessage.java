package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.AMIAO")
public interface AmiRelayObjectMessage extends AmiRelayMessage {

	@PID(2)
	String getType();
	public void setType(String type);

	@PID(3)
	public String getId();
	public void setId(String id);

	@PID(11)
	long getExpires();
	public void setExpires(long miid);
}
