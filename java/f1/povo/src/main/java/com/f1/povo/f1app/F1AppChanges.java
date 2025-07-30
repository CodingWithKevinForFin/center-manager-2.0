package com.f1.povo.f1app;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.FA.CH")
public interface F1AppChanges extends PartialMessage {

	@PID(1)
	public void setF1AppEntitiesAdded(List<F1AppEntity> added);
	public List<F1AppEntity> getF1AppEntitiesAdded();

	@PID(2)
	public void setF1AppEntitiesRemoved(long[] removed);
	public long[] getF1AppEntitiesRemoved();

	@PID(3)
	public void setF1AppEntitiesUpdated(byte[] updated);
	public byte[] getF1AppEntitiesUpdated();

	@PID(4)
	public void setF1AppEvents(List<F1AppEvent> f1AppEvents);
	public List<F1AppEvent> getF1AppEvents();

	@PID(5)
	public long getSeqNum();
	public void setSeqNum(long seqNum);

	@PID(6)
	public String getF1AppProcessUid();
	public void setF1AppProcessUid(String processUid);
}
