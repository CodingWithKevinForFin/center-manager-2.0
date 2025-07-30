package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentChanges;

@VID("F1.VA.SR")
public interface VortexAgentSnapshotResponse extends VortexAgentResponse {

	@PID(1)
	public VortexAgentChanges getSnapshot();
	public void setSnapshot(VortexAgentChanges changes);

}
