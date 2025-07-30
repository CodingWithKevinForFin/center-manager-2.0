package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;

@VID("F1.VA.FSR")
public interface VortexAgentFileSearchResponse extends VortexAgentResponse {

	byte PID_START_TIME = 1;
	byte PID_END_TIME = 2;
	byte PID_FILES = 3;
	byte PID_MACHINE_UID = 4;
	byte PID_JOB_ID = 5;

	@PID(PID_START_TIME)
	public long getStartTime();
	public void setStartTime(long startTime);

	@PID(PID_END_TIME)
	public long getEndTime();
	public void setEndTime(long endTime);

	@PID(PID_FILES)
	public List<VortexAgentFile> getFiles();
	public void setFiles(List<VortexAgentFile> files);

	@PID(PID_MACHINE_UID)
	public String getMachineUid();
	public void setMachineUid(String machineUid);

	@PID(PID_JOB_ID)
	public long getJobId();
	public void setJobId(long jobId);

}
