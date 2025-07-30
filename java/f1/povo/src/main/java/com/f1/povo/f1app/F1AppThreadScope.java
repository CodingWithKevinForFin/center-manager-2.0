package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.TS")
public interface F1AppThreadScope extends F1AppEntity {

	byte PID_PROCESS_STATS = 2;
	byte PID_DISPATCH_STATS = 3;
	byte PID_THROWN_STATS = 4;
	byte PID_FORWARD_STATS = 5;
	byte PID_CURRENT_PROCESSOR_ID = 6;
	byte PID_CURRENT_PARTITION_ID = 7;
	byte PID_CONTAINER_SCOPE_ID = 8;
	byte PID_THREAD_SCOPE_CONTROLLER_ID = 9;
	byte PID_THREAD_POOL_KEY = 10;
	byte PID_THREAD_NAME = 11;

	@PID(PID_THREAD_POOL_KEY)
	public String getThreadPoolKey();
	public void setThreadPoolKey(String threadPoolKey);

	@PID(PID_THREAD_NAME)
	public String getThreadName();
	public void setThreadName(String threadName);

	@PID(PID_PROCESS_STATS)
	public long getProcessStats();
	public void setProcessStats(long processStats);

	@PID(PID_DISPATCH_STATS)
	public long getDispatchStats();
	public void setDispatchStats(long dispatchStats);

	@PID(PID_THROWN_STATS)
	public long getThrownStats();
	public void setThrownStats(long thrownStats);

	@PID(PID_FORWARD_STATS)
	public long getForwardStats();
	public void setForwardStats(long processStats);

	@PID(PID_CURRENT_PROCESSOR_ID)
	public long getCurrentProcessorId();
	public void setCurrentProcessorId(long processStats);

	@PID(PID_CURRENT_PARTITION_ID)
	public long getCurrentPartitionId();
	public void setCurrentPartitionId(long partitionId);

	@PID(PID_CONTAINER_SCOPE_ID)
	public long getContainerScopeId();
	public void setContainerScopeId(long containerScopeId);

	@PID(PID_THREAD_SCOPE_CONTROLLER_ID)
	public long getThreadScopeControllerId();
	public void setThreadScopeControllerId(long containerId);
}
