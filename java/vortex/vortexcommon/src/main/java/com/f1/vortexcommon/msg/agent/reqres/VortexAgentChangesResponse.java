package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.VID;

@VID("F1.VA.CR")
public interface VortexAgentChangesResponse extends VortexAgentResponse {

	//@PID(1)
	//public VortexAgentChanges getChanges();
	//public void setChanges(VortexAgentChanges changes);

	//byte PID_CHANGES = 1;
	//@PID(PID_CHANGES)
	//public VortexAgentSnapshot getChanges();
	//public void setChanges(VortexAgentSnapshot snapshot);

	//@PID(1)
	//public void setVortexAgentEntitiesAdded(List<VortexAgentEntity> snapshot);
	//public List<VortexAgentEntity> getVortexAgentEntitiesAdded();

	//byte PID_REMOVED = 2;
	//@PID(PID_REMOVED)
	//public void setRemoved(long[] removed);
	//public long[] getRemoved();

	//byte PID_UPDATED = 3;
	//@PID(PID_UPDATED)
	//public void setUpdated(byte[] updated);
	//public byte[] getUpdated();

}
