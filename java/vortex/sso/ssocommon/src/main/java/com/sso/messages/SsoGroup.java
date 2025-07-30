package com.sso.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.SS.SG")
public interface SsoGroup extends PartialMessage, Lockable {

	short GROUP_TYPE_GENERIC = 0;
	short GROUP_TYPE_HOST = 1;
	short GROUP_TYPE_ENVIRONMENT = 2;
	short GROUP_TYPE_USER = 3;
	short GROUP_TYPE_REGION = 4;
	short GROUP_TYPE_PROCESS = 5;
	short GROUP_TYPE_ACCOUNT = 6;
	short GROUP_TYPE_EXPECTATION = 7;
	short GROUP_TYPE_DEPLOYMENT = 8;

	byte PID_ID = 1;
	byte PID_REVISION = 2;
	byte PID_NOW = 3;
	byte PID_NAME = 5;
	byte PID_TYPE = 6;

	@PID(PID_ID)
	public long getId();
	public void setId(long id);

	@PID(PID_REVISION)
	public int getRevision();
	public void setRevision(int revision);

	@PID(PID_NOW)
	public long getNow();
	public void setNow(long now);

	@PID(PID_NAME)
	public String getName();
	public void setName(String name);

	@PID(PID_TYPE)
	public short getType();
	public void setType(short type);

	SsoGroup clone();

}
