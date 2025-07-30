package com.f1.vortexcommon.msg;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VO.ENTITY")
public interface VortexEntity extends Message {

	int REVISION_DONE = 2000 * 1000 * 1000;
	int REVISION_NEW = 0;

	byte PID_REVISION = 21;
	byte PID_ID = 22;
	byte PID_NOW = 23;

	@PID(PID_REVISION)
	public int getRevision();
	public void setRevision(int revision);

	@PID(PID_ID)
	public long getId();
	public void setId(long id);

	@PID(PID_NOW)
	public long getNow();
	public void setNow(long now);

}
