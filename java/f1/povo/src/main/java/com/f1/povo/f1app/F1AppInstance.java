package com.f1.povo.f1app;

import java.util.List;
import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.SS")
public interface F1AppInstance extends F1AppEntity {

	byte STATUS_RUNNING = 1;

	byte PID_MAIN_CLASS_NAME = 2;
	byte PID_START_TIME_MS = 3;
	byte PID_HOST_NAME = 4;
	byte PID_USER_NAME = 5;
	byte PID_PROCESS_UID = 6;
	byte PID_PID = 7;
	byte PID_PWD = 8;
	byte PID_JVM_ARGUMENTS = 10;
	byte PID_MAIN_CLASS_ARGUMENTS = 11;
	byte PID_JAVA_HOME = 12;
	byte PID_JAVA_VERSION = 13;
	byte PID_JAVA_VENDOR = 14;
	byte PID_JAVA_EXTERNAL_DIRS = 15;
	byte PID_MEMORY_POOLS = 17;
	byte PID_CLASSPATH = 18;
	byte PID_BOOT_CLASSPATH = 19;
	byte PID_NOW_MS = 21;
	byte PID_CLOCK_NOW_MS = 22;
	byte PID_MONITOR_TIME_SPENT_MS = 23;
	byte PID_THREADS_NEW_COUNT = 27;
	byte PID_THREADS_BLOCKED_COUNT = 28;
	byte PID_THREADS_RUNNABLE_COUNT = 29;
	byte PID_THREADS_TERMINATED_COUNT = 30;
	byte PID_THREADS_TIMED_WAITING_COUNT = 31;
	byte PID_THREADS_WAITING_COUNT = 32;
	byte PID_FREE_MEMORY = 33;
	byte PID_MAX_MEMORY = 34;
	byte PID_TOTAL_MEMORY = 35;
	byte PID_AVAILABLE_PROCESSORS_COUNT = 36;
	byte PID_AGENT_DISCONNECT_TIME = 37;
	byte PID_AGENT_CONNECT_TIME = 38;
	byte PID_IS_DEBUG = 40;
	byte PID_APP_NAME = 42;
	byte PID_AGENT_VERSION = 43;
	byte PID_F1_LICENSE_END_DATE = 44;
	byte PID_AGENT_PROCESS_UID = 45;
	byte PID_AGENT_MACHINE_UID = 46;

	@PID(PID_MAIN_CLASS_NAME)
	public String getMainClassName();
	public void setMainClassName(String appName);

	@PID(PID_START_TIME_MS)
	public long getStartTimeMs();
	public void setStartTimeMs(long startTime);

	@PID(PID_HOST_NAME)
	public String getHostName();
	public void setHostName(String hostName);

	@PID(PID_USER_NAME)
	public String getUserName();
	public void setUserName(String userName);

	@PID(PID_PROCESS_UID)
	public String getProcessUid();
	public void setProcessUid(String uid);

	@PID(PID_PID)
	public String getPid();
	public void setPid(String pid);

	@PID(PID_PWD)
	public String getPwd();
	public void setPwd(String pwd);

	@PID(PID_JVM_ARGUMENTS)
	public List<String> getJvmArguments();
	public void setJvmArguments(List<String> jvmArguments);

	@PID(PID_MAIN_CLASS_ARGUMENTS)
	public List<String> getMainClassArguments();
	public void setMainClassArguments(List<String> jvmArguments);

	@PID(PID_JAVA_HOME)
	public String getJavaHome();
	public void setJavaHome(String javaHome);

	@PID(PID_JAVA_VERSION)
	public String getJavaVersion();
	public void setJavaVersion(String javaHome);

	@PID(PID_JAVA_VENDOR)
	public String getJavaVendor();
	public void setJavaVendor(String javaHome);

	@PID(PID_JAVA_EXTERNAL_DIRS)
	public List<String> getJavaExternalDirs();
	public void setJavaExternalDirs(List<String> javaHome);

	@PID(PID_MEMORY_POOLS)
	public Map<String, Long> getMemoryPools();
	public void setMemoryPools(Map<String, Long> maxMemory);

	@PID(PID_CLASSPATH)
	public List<String> getClasspath();
	public void setClasspath(List<String> javaHome);

	@PID(PID_BOOT_CLASSPATH)
	public List<String> getBootClasspath();
	public void setBootClasspath(List<String> javaHome);

	@PID(PID_NOW_MS)
	public long getNowMs();
	public void setNowMs(long time);

	@PID(PID_CLOCK_NOW_MS)
	public long getClockNowMs();
	public void setClockNowMs(long time);

	@PID(PID_MONITOR_TIME_SPENT_MS)
	public int getMonitorTimeSpentMs();
	public void setMonitorTimeSpentMs(int time);

	@PID(PID_THREADS_NEW_COUNT)
	public short getThreadsNewCount();
	public void setThreadsNewCount(short count);

	@PID(PID_THREADS_BLOCKED_COUNT)
	public short getThreadsBlockedCount();
	public void setThreadsBlockedCount(short count);

	@PID(PID_THREADS_RUNNABLE_COUNT)
	public short getThreadsRunnableCount();
	public void setThreadsRunnableCount(short count);

	@PID(PID_THREADS_TERMINATED_COUNT)
	public short getThreadsTerminatedCount();
	public void setThreadsTerminatedCount(short count);

	@PID(PID_THREADS_TIMED_WAITING_COUNT)
	public short getThreadsTimedWaitingCount();
	public void setThreadsTimedWaitingCount(short count);

	@PID(PID_THREADS_WAITING_COUNT)
	public short getThreadsWaitingCount();
	public void setThreadsWaitingCount(short count);

	@PID(PID_FREE_MEMORY)
	public long getFreeMemory();
	public void setFreeMemory(long memoryAvailable);

	@PID(PID_MAX_MEMORY)
	public long getMaxMemory();
	public void setMaxMemory(long memoryAvailable);

	@PID(PID_TOTAL_MEMORY)
	public long getTotalMemory();
	public void setTotalMemory(long totalAvailable);

	@PID(PID_AVAILABLE_PROCESSORS_COUNT)
	public short getAvailableProcessorsCount();
	public void setAvailableProcessorsCount(short totalAvailable);

	@PID(PID_AGENT_DISCONNECT_TIME)
	public long getAgentDisconnectTime();
	public void setAgentDisconnectTime(long totalAvailable);

	@PID(PID_AGENT_CONNECT_TIME)
	public long getAgentConnectTime();
	public void setAgentConnectTime(long totalAvailable);

	@PID(PID_IS_DEBUG)
	public boolean getIsDebug();
	public void setIsDebug(boolean isDebug);

	@PID(PID_APP_NAME)
	public String getAppName();
	public void setAppName(String appName);

	@PID(PID_AGENT_VERSION)
	public String getAgentVersion();
	public void setAgentVersion(String agentVersion);

	@PID(PID_F1_LICENSE_END_DATE)
	public void setF1LicenseEndDate(String licenseEndDate);
	public String getF1LicenseEndDate();

	@PID(PID_AGENT_PROCESS_UID)
	public String getAgentProcessUid();
	public void setAgentProcessUid(String agentProcessUid);

	@PID(PID_AGENT_MACHINE_UID)
	public String getAgentMachineUid();
	public void setAgentMachineUid(String agentMachineUid);

	public F1AppInstance clone();
}
