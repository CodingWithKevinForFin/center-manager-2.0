package com.f1.povo.f1app.audit;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.ATFE")
public interface F1AppAuditTrailF1Event extends F1AppAuditTrailEvent {

	byte PID_PARTITION_ID = 12;
	byte PID_MESSAGE_CLASS_NAME = 13;
	byte PID_STATE_CLASS_NAME = 14;

	@PID(PID_PARTITION_ID)
	public void setPartitionId(Object type);
	public Object getPartitionId();

	@PID(PID_MESSAGE_CLASS_NAME)
	public void setMessageClassName(String messageName);
	public String getMessageClassName();

	@PID(PID_STATE_CLASS_NAME)
	public void setStateClassName(String messageName);
	public String getStateClassName();
}
