package com.f1.ami.amicommon.msg;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.AMITR")
public interface AmiCenterSchemaTypeRecord extends Message {

	@PID(6)
	short getObjectType();
	public void setObjectType(short type);

	@PID(11)
	public long getCount();
	public void setCount(long count);
}
