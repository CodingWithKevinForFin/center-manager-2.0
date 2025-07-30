package com.f1.vortexcommon.msg.agent;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.audit.F1AppAuditTrailEventList;
import com.f1.vortexcommon.msg.VortexEntity;

@VID("F1.VA.TD")
public interface TestTrackDeltas extends PartialMessage {

	byte PID_REVISIONS = 1;
	byte PID_NEW_SNAPSHOTS = 2;
	byte PID_REMOVED_SNAPSHOTS = 3;
	byte PID_F1_SNAPSHOTS = 6;
	byte PID_SEQUENCE_NUMBER = 4;
	byte PID_AUDIT_TRAIL_EVENT_LISTS = 7;

	@PID(PID_REVISIONS)
	public List<VortexEntity> getRevisions();
	public void setRevisions(List<VortexEntity> revisions);

	@PID(PID_NEW_SNAPSHOTS)
	public List<VortexAgentSnapshot> getNewSnapshots();
	public void setNewSnapshots(List<VortexAgentSnapshot> snapshot);

	@PID(PID_REMOVED_SNAPSHOTS)
	public void setRemovedSnapshots(long[] longs);
	public long[] getRemovedSnapshots();

	@PID(PID_F1_SNAPSHOTS)
	public List<F1AppInstance> getF1Snapshots();
	public void setF1Snapshots(List<F1AppInstance> snapshots);

	@PID(PID_SEQUENCE_NUMBER)
	public long getSequenceNumber();
	public void setSequenceNumber(long sequenceNumber);

	@PID(PID_AUDIT_TRAIL_EVENT_LISTS)
	public void setAuditTrailEventLists(List<F1AppAuditTrailEventList> deltas);
	public List<F1AppAuditTrailEventList> getAuditTrailEventLists();

}
