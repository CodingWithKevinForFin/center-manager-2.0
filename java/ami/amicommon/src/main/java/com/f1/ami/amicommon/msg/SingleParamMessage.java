package com.f1.ami.amicommon.msg;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.AMISPM")
public interface SingleParamMessage extends Message {
	@PID(4)
	public byte[] getParams();
	public void setParams(byte[] params);

}
