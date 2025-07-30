package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.FS")
public interface VortexAgentFileSystem extends PartialMessage, VortexAgentEntity {

	byte PID_FREE_SPACE = 1;
	byte PID_USABLE_SPACE = 2;
	byte PID_TOTAL_SPACE = 3;
	byte PID_NAME = 4;
	byte PID_TYPE = 5;

	@PID(PID_FREE_SPACE)
	public void setFreeSpace(long freeSpace);
	public long getFreeSpace();

	@PID(PID_USABLE_SPACE)
	public void setUsableSpace(long usableSpace);
	public long getUsableSpace();

	@PID(PID_TOTAL_SPACE)
	public void setTotalSpace(long totalSpace);
	public long getTotalSpace();

	@PID(PID_NAME)
	public void setName(String name);
	public String getName();

	@PID(PID_TYPE)
	public void setType(String type);
	public String getType();
}
