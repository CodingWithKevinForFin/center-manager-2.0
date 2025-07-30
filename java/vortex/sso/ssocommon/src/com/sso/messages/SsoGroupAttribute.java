package com.sso.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.SS.GA")
public interface SsoGroupAttribute extends PartialMessage, Lockable {

	byte TYPE_BINARY = 1;
	byte TYPE_TEXT = 2;
	byte TYPE_JSON = 3;

	byte PID_ID=1;
	byte PID_KEY=3;
	byte PID_VALUE=4;
	byte PID_NOW=5;
	byte PID_REVISION=6;
	byte PID_GROUP_ID=7;
	byte PID_TYPE=8;
	
	@PID(PID_ID)
	public long getId();
	public void setId(long id);

	@PID(PID_KEY)
	public String getKey();
	public void setKey(String key);

	@PID(PID_VALUE)
	public String getValue();
	public void setValue(String value);

	@PID(PID_NOW)
	public long getNow();
	public void setNow(long lastUpdateTime);

	@PID(PID_REVISION)
	public int getRevision();
	public void setRevision(int revision);

	@PID(PID_GROUP_ID)
	public long getGroupId();
	public void setGroupId(long revision);

	@PID(PID_TYPE)
	public byte getType();
	public void setType(byte type);

	public SsoGroupAttribute clone();
}
