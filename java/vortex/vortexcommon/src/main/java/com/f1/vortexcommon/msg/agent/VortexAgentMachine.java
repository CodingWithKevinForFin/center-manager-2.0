package com.f1.vortexcommon.msg.agent;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.M")
public interface VortexAgentMachine extends PartialMessage, VortexAgentEntity, VortexMetadatable {

	byte PID_CPU_COUNT = 1;
	byte PID_OS_VERSION = 3;
	byte PID_OS_NAME = 4;
	byte PID_OS_ARCHITECTURE = 5;
	byte PID_SYSTEM_START_TIME = 7;
	byte PID_MACHINE_UID = 8;
	byte PID_START_TIME = 11;
	byte PID_HOST_NAME = 13;
	byte PID_SYSTEM_LOAD_AVERAGE = 42;
	byte PID_TOTAL_MEMORY = 43;
	byte PID_USED_MEMORY = 44;
	byte PID_TOTAL_SWAP_MEMORY = 45;
	byte PID_USED_SWAP_MEMORY = 46;
	byte PID_AGENT_PROCESS_UID = 47;
	byte PID_AGENT_DETAILS = 49;

	@PID(PID_CPU_COUNT)
	public int getCpuCount();
	public void setCpuCount(int count);

	@PID(PID_OS_VERSION)
	public String getOsVersion();
	public void setOsVersion(String osVersion);

	@PID(PID_OS_NAME)
	public String getOsName();
	public void setOsName(String osVersion);

	@PID(PID_OS_ARCHITECTURE)
	public String getOsArchitecture();
	public void setOsArchitecture(String osArchitecture);

	@PID(PID_SYSTEM_START_TIME)
	public void setSystemStartTime(long time);
	public long getSystemStartTime();

	@PID(PID_MACHINE_UID)
	public void setMachineUid(String machineUid);
	public String getMachineUid();

	@PID(PID_START_TIME)
	public long getStartTime();
	public void setStartTime(long startTime);

	@PID(PID_HOST_NAME)
	public String getHostName();
	public void setHostName(String hostName);

	@PID(PID_SYSTEM_LOAD_AVERAGE)
	public void setSystemLoadAverage(Double systemLoadAvg);
	public Double getSystemLoadAverage();

	@PID(PID_TOTAL_MEMORY)
	public Long getTotalMemory();
	public void setTotalMemory(Long totalMemory);

	@PID(PID_USED_MEMORY)
	public Long getUsedMemory();
	public void setUsedMemory(Long usedMemory);

	@PID(PID_TOTAL_SWAP_MEMORY)
	public Long getTotalSwapMemory();
	public void setTotalSwapMemory(Long totalSwapMemory);

	@PID(PID_USED_SWAP_MEMORY)
	public Long getUsedSwapMemory();
	public void setUsedSwapMemory(Long usedSwapMemory);

	@PID(PID_AGENT_PROCESS_UID)
	public void setAgentProcessUid(String machineUid);
	public String getAgentProcessUid();

	@PID(PID_AGENT_DETAILS)
	public void setAgentDetails(Map<String, String> agentPath);
	public Map<String, String> getAgentDetails();

	public VortexAgentMachine clone();

}
