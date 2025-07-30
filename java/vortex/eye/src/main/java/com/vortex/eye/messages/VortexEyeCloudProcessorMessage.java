package com.vortex.eye.messages;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;

public interface VortexEyeCloudProcessorMessage extends Message {
	@PID(1)
	public Iterable<VortexEyeCloudInterface> getCloudInterfaces();
	public void setCloudInterfaces(Iterable<VortexEyeCloudInterface> cis);
}
