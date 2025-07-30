package com.f1.vortexcommon.msg.agent;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppEvent;

@VID("F1.VA.CBC")
public interface VortexAgentChanges extends PartialMessage {

	//////////////////
	//agent entities
	//////////////////
	@PID(1)
	public void setAgentEntitiesAdded(List<VortexAgentEntity> snapshot);
	public List<VortexAgentEntity> getAgentEntitiesAdded();

	@PID(2)
	public void setAgentEntitiesUpdated(byte[] updated);
	public byte[] getAgentEntitiesUpdated();

	@PID(3)
	public void setAgentEntitiesRemoved(long[] removed);
	public long[] getAgentEntitiesRemoved();

	//////////////////
	//f1app entities
	//////////////////
	@PID(4)
	public List<F1AppEntity> getF1AppEntitiesAdded();
	public void setF1AppEntitiesAdded(List<F1AppEntity> f1Apps);

	@PID(5)
	public void setF1AppEntitiesUpdated(byte[] updated);
	public byte[] getF1AppEntitiesUpdated();

	@PID(6)
	public void setF1AppEntitiesRemoved(long[] removed);
	public long[] getF1AppEntitiesRemoved();

	@PID(7)
	public void setF1AppEvents(List<F1AppEvent> f1AppEvents);
	public List<F1AppEvent> getF1AppEvents();

	//////////////////
	//message vitals
	//////////////////

	@PID(9)
	public String getAgentProcessUid();
	public void setAgentProcessUid(String agentProcessUid);

	@PID(8)
	public long getSeqNum();
	public void setSeqNum(long seqNum);

}
