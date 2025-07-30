package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.SQ")
public interface VortexEyeSnapshotRequest extends VortexEyeRequest {

	@PID(1)
	boolean getSupportsIntermediate();
	public void setSupportsIntermediate(boolean supportsIntermediate);

	@PID(2)
	int getMaxBatchSize();
	public void setMaxBatchSize(int maxBatchSize);

	//@PID(3)
	//public Set<String> getAmiObjectTypesToSend();
	//public void setAmiObjectTypesToSend(Set<String> types);

}
