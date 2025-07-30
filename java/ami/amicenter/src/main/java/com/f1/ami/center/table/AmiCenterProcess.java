package com.f1.ami.center.table;

public interface AmiCenterProcess {
	String PROCESS_IDLE = "IDLE";
	String PROCESS_RUN = "RUN_QUERY";
	String PROCESS_RUN_RT = "RUN_RTSTREAM";
	String PROCESS_RUN_MONITOR = "RUN_MONITOR";
	String PROCESS_WAIT_SUBPROCESS = "WAIT_SUBPROCESS";
	String PROCESS_WAIT_DB = "WAIT_DB";
	String PROCESS_WAIT_RELAY = "WAIT_RELAY";
	String PROCESS_WAIT_NAME_SERVICE = "WAIT_NAME_SERVICE";
	String PROCESS_RUN_REPLICATION = "RUN_REPLICATION";

	public long getSessionId();
	public long getProcessId();
	public long getStartTime();
	public String getQuery();
	public String getDsName();
	public String getDsRelayId();
	public String getProcessStatus();
	public long getParentProcessId();
}
