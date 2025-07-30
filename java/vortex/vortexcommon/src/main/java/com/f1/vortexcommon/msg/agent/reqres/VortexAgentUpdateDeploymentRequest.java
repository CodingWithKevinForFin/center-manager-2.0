package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexDeployment;

@VID("F1.VA.UDQ")
public interface VortexAgentUpdateDeploymentRequest extends VortexAgentRequest {

	@PID(1)
	public List<VortexDeployment> getUpdated();
	public void setUpdated(List<VortexDeployment> updated);

	@PID(3)
	public long[] getRemoved();
	public void setRemoved(long[] removed);

	@PID(4)
	public boolean getIsSnapshot();
	public void setIsSnapshot(boolean removed);
}
