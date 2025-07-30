package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.PR")
public interface F1AppProcessor extends F1AppContainerScope {

	byte PID_PROCESS_STATS = 5;
	byte PID_QUEUE_PUSH_STATS = 6;
	byte PID_THROWN_STATS = 7;
	byte PID_QUEUE_POP_STATS = 8;

	byte PID_STATE_TYPE_CLASS_ID = 10;
	byte PID_ACTION_TYPE_CLASS_ID = 11;
	byte PID_RESPONSE_TYPE_CLASS_ID = 12;
	byte PID_PARTITION_RESOLVER_ID = 13;

	@PID(PID_STATE_TYPE_CLASS_ID)
	public long getStateTypeClassId();
	public void setStateTypeClassId(long stateType);

	@PID(PID_ACTION_TYPE_CLASS_ID)
	public long getActionTypeClassId();
	public void setActionTypeClassId(long actionType);

	@PID(PID_RESPONSE_TYPE_CLASS_ID)
	public long getResponseTypeClassId();
	public void setResponseTypeClassId(long responseType);

	@PID(PID_PARTITION_RESOLVER_ID)
	public long getPartitionResolverId();
	public void setPartitionResolverId(long partitionResolverId);

	@PID(PID_PROCESS_STATS)
	public long getProcessStats();
	public void setProcessStats(long processStats);

	@PID(PID_QUEUE_PUSH_STATS)
	public long getQueuePushStats();
	public void setQueuePushStats(long dispatchStats);

	@PID(PID_THROWN_STATS)
	public long getThrownStats();
	public void setThrownStats(long thrownStats);

	@PID(PID_QUEUE_POP_STATS)
	public long getQueuePopStats();
	public void setQueuePopStats(long processStats);

}
