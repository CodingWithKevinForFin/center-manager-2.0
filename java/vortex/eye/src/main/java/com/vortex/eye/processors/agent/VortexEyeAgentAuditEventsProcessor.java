package com.vortex.eye.processors.agent;

import com.f1.container.ThreadScope;
import com.f1.povo.f1app.audit.F1AppAuditTrailEventList;
import com.f1.utils.CH;
import com.f1.vortexcommon.msg.agent.TestTrackDeltas;
import com.vortex.eye.processors.VortexEyeBasicProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeAgentAuditEventsProcessor extends VortexEyeBasicProcessor<F1AppAuditTrailEventList> {

	public VortexEyeAgentAuditEventsProcessor() {
		super(F1AppAuditTrailEventList.class);
	}

	@Override
	public void processAction(F1AppAuditTrailEventList action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		TestTrackDeltas deltas = nw(TestTrackDeltas.class);
		deltas.setAuditTrailEventLists(CH.l(action));
		sendToClients(deltas);
	}

}
