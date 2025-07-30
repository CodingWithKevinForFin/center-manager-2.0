package com.f1.ami.amicommon.msg;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.AMISR")
public interface AmiCenterSchemaRecord extends Message {

	@PID(2)
	byte getValueType();
	public void setValueType(byte type);

	@PID(6)
	short getObjectType();
	public void setObjectType(short type);

	@PID(4)
	public short getParam();
	public void setParam(short param);

	@PID(5)
	public int getAppId();
	public void setAppId(int param);

	@PID(11)
	public long getCount();
	public void setCount(long count);
}
