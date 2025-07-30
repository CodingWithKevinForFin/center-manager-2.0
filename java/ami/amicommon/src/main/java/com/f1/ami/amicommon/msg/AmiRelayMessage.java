package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.AMIAM")
public interface AmiRelayMessage extends SingleParamMessage {
	byte TRANSFORM_NONE = 0;
	byte TRANSFORM_FIRST = 1;
	byte TRANSFORM_DUP = 2;

	@PID(10)
	public int getConnectionId();
	public void setConnectionId(int connectionId);

	@PID(44)
	public short getAppIdStringKey();
	public void setAppIdStringKey(short connectionId);

	@PID(45)
	public long getOrigSeqNum();
	public void setOrigSeqNum(long seqnum);

	@PID(46)
	byte getTransformState();
	public void setTransformState(byte miid);
}
