package com.vortex.agent.messages;

import java.util.Map;
import java.util.Set;

import com.f1.base.Message;
import com.f1.vortexcommon.msg.eye.VortexDeployment;

public interface VortexAgentDeploymentUpdateMessage extends Message {

	public VortexDeployment getDeployment();
	public void setDeployment(VortexDeployment backup);

	public String getPartitionId();
	public void setPartitionId(String partitionId);

	public Map<String, String> getAddedPuidToDiids();
	public void setAddedPuidToDiids(Map<String, String> puidToDiids);

	public Set<String> getRemovedPuids();
	public void setRemovedPuids(Set<String> removed);

	public void setStatusBitsToSet(int statusBitsToSet);
	public int getStatusBitsToSet();

	public void setStatusBitsToClear(int statusBitsToClear);
	public int getStatusBitsToClear();

	public void setMessage(String msg);
	public String getMessage();

}
