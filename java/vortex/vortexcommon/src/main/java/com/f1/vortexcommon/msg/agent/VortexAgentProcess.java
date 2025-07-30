package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.P")
public interface VortexAgentProcess extends PartialMessage, VortexAgentEntity {

	byte PID_USER = 1;
	byte PID_PID = 2;
	byte PID_PARENT_PID = 3;
	byte PID_START_TIME = 4;
	byte PID_END_TIME = 5;
	byte PID_COMMAND = 6;
	byte PID_MEMORY = 7;
	byte PID_CPU_PERCENT = 8;

	@PID(PID_USER)
	public String getUser();
	public void setUser(String user);

	@PID(PID_PID)
	public String getPid();
	public void setPid(String pid);

	@PID(PID_PARENT_PID)
	public String getParentPid();
	public void setParentPid(String ParentPid);

	@PID(PID_START_TIME)
	public long getStartTime();
	public void setStartTime(long startTime);

	@PID(PID_END_TIME)
	public long getEndTime();
	public void setEndTime(long startTime);

	@PID(PID_COMMAND)
	public String getCommand();
	public void setCommand(String command);

	@PID(PID_MEMORY)
	public void setMemory(long memory);
	public long getMemory();

	@PID(PID_CPU_PERCENT)
	public void setCpuPercent(double cpuPercent);
	public double getCpuPercent();

	public VortexAgentProcess clone();
}
