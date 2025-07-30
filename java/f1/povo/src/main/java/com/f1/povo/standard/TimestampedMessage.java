package com.f1.povo.standard;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.ST.TS")
public interface TimestampedMessage extends Message {

	
	byte PID_ACTION=1;
	byte PID_TIMESTAMP_NANOS=2;
	byte PID_PARTITION_ID=3;
	byte PID_PROCESS_UID=4;
	byte PID_NOTES=5;
	
	@PID(PID_ACTION)
	public Action getAction();
	public void setAction(Action action);

	@PID(PID_TIMESTAMP_NANOS)
	public long getTimestampNanos();
	public void setTimestampNanos(long timestampNanos);

	@PID(PID_PARTITION_ID)
	public Object getPartitionId();
	public void setPartitionId(Object partitionId);

	@PID(PID_PROCESS_UID)
	public String getProcessUid();
	public void setProcessUid(String processUid);

	@PID(PID_NOTES)
	public void setNotes(String backendServiceId);
	public String getNotes();
}
