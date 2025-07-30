package com.f1.vortexcommon.msg.eye;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppEvent;
import com.f1.vortexcommon.msg.agent.TestTrackDeltas;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

@VID("F1.VE.CH")
public interface VortexEyeChanges extends TestTrackDeltas {

	////////////////////////////////////
	// Eye 
	////////////////////////////////////
	@PID(17)
	public void setEyeEntitiesAdded(List<VortexEyeEntity> snapshot);
	public List<VortexEyeEntity> getEyeEntitiesAdded();

	@PID(18)
	public void setEyeEntitiesUpdated(byte[] updated);
	public byte[] getEyeEntitiesUpdated();

	@PID(19)
	public void setEyeEntitiesRemoved(long[] removed);
	public long[] getEyeEntitiesRemoved();

	////////////////////////////////////
	// Agent (machine)
	////////////////////////////////////
	@PID(11)
	public void setAgentEntitiesAdded(List<VortexAgentEntity> snapshot);
	public List<VortexAgentEntity> getAgentEntitiesAdded();

	@PID(12)
	public void setAgentEntitiesUpdated(byte[] updated);
	public byte[] getAgentEntitiesUpdated();

	@PID(13)
	public void setAgentEntitiesRemoved(long[] removed);
	public long[] getAgentEntitiesRemoved();

	////////////////////////////////////
	// F1 Application 
	////////////////////////////////////
	@PID(14)
	public List<F1AppEntity> getF1AppEntitiesAdded();
	public void setF1AppEntitiesAdded(List<F1AppEntity> f1Apps);

	@PID(15)
	public void setF1AppEntitiesUpdated(byte[] updated);
	public byte[] getF1AppEntitiesUpdated();

	@PID(16)
	public void setF1AppEntitiesRemoved(long[] removed);
	public long[] getF1AppEntitiesRemoved();

	@PID(22)
	public void setF1AppEvents(List<F1AppEvent> f1AppEvents);
	public List<F1AppEvent> getF1AppEvents();

	////////////////////////////////////
	// Message vitals
	////////////////////////////////////
	@PID(21)
	public String getEyeProcessUid();
	public void setEyeProcessUid(String agentProcessUid);

	@PID(20)
	public long getSeqNum();
	public void setSeqNum(long seqNum);

}
