package com.f1.povo.f1app.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.ISPQ")
public interface F1AppInspectPartitionRequest extends F1AppRequest {

	byte PID_PARTITION_ID = 11;
	@PID(PID_PARTITION_ID)
	public long getPartitionId();
	public void setPartitionId(long id);

	byte PID_TIMEOUT_MS = 10;
	@PID(PID_TIMEOUT_MS)
	public long getTimeoutMs();
	public void setTimeoutMs(long timeout);
}
