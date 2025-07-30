package com.vortex.client;

import java.util.List;

import com.f1.povo.f1app.F1AppEvent;

public interface VortexClientAuditEventsListener {
	public void onAgentAuditEvents(List<F1AppEvent> list);
}
