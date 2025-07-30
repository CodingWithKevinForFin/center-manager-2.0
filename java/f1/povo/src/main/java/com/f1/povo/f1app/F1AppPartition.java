package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.P")
public interface F1AppPartition extends F1AppEntity {

	byte PID_PARTITION_ID = 1;
	byte PID_THREAD_POOL_KEY = 2;
	byte PID_THROWN_STATS = 4;
	byte PID_QUEUE_PUSH_STATS = 5;
	byte PID_PROCESS_STATS = 6;
	byte PID_QUEUE_POP_STATS = 7;
	byte PID_PARTITION_CONTROLLER_ID = 8;
	byte PID_CONTAINER_SCOPE_ID = 10;

	@PID(PID_PARTITION_ID)
	public Object getPartitionId();
	public void setPartitionId(Object partitionId);

	@PID(PID_THREAD_POOL_KEY)
	public Object getThreadPoolKey();
	public void setThreadPoolKey(Object threadPoolKey);

	@PID(PID_THROWN_STATS)
	public long getThrownStats();
	public void setThrownStats(long thrownStats);

	@PID(PID_QUEUE_PUSH_STATS)
	public long getQueuePushStats();
	public void setQueuePushStats(long queuePushStats);

	@PID(PID_PROCESS_STATS)
	public long getProcessStats();
	public void setProcessStats(long processStats);

	@PID(PID_QUEUE_POP_STATS)
	public long getQueuePopStats();
	public void setQueuePopStats(long queuePopStats);

	@PID(PID_PARTITION_CONTROLLER_ID)
	public long getPartitionControllerId();
	public void setPartitionControllerId(long containerId);

	@PID(PID_CONTAINER_SCOPE_ID)
	public long getContainerScopeId();
	public void setContainerScopeId(long partitionId);
}
