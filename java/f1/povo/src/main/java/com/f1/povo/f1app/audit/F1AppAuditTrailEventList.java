package com.f1.povo.f1app.audit;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.ATEL")
public interface F1AppAuditTrailEventList extends Message {

	byte PID_AGENT_AUDIT_TRAIL_EVENTS = 1;
	byte PID_PROCESS_UID = 2;

	@PID(PID_AGENT_AUDIT_TRAIL_EVENTS)
	public List<F1AppAuditTrailEvent> getAgentAuditTrailEvents();
	public void setAgentAuditTrailEvents(List<F1AppAuditTrailEvent> events);

	@PID(PID_PROCESS_UID)
	public String getProcessUid();
	public void setProcessUid(String uid);
}
