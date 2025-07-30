package com.vortex.agent.processors.f1app;

import com.f1.container.ThreadScope;
import com.f1.povo.f1app.audit.F1AppAuditTrailEventList;
import com.vortex.agent.processors.VortexAgentBasicProcessor;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentF1AppAuditTrailProcessor extends VortexAgentBasicProcessor<F1AppAuditTrailEventList> {

	public VortexAgentF1AppAuditTrailProcessor() {
		super(F1AppAuditTrailEventList.class);
	}

	@Override
	public void processAction(F1AppAuditTrailEventList action, VortexAgentState state, ThreadScope threadScope) throws Exception {
		sendToEye(action);
	}

}
