package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.ST")
public interface F1AppState extends F1AppEntity {

	byte PID_STATE_TYPE_CLASS_ID=10;
	byte PID_PARTITION_MONITOR_ID=13;
	
	@PID(PID_STATE_TYPE_CLASS_ID)
	public long getStateTypeClassId();
	public void setStateTypeClassId(long stateType);

	@PID(PID_PARTITION_MONITOR_ID)
	public long getPartitionMonitorId();
	public void setPartitionMonitorId(long parentId);
}
