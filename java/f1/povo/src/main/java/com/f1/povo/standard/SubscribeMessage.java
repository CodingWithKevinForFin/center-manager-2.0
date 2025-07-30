package com.f1.povo.standard;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.ST.SU")
public interface SubscribeMessage extends Message {

	byte PID_PARTITION_ID = 1;
	
	@PID(PID_PARTITION_ID)
	public Object getPartitionId();
	public void setPartitionId(Object partitionId);
}
