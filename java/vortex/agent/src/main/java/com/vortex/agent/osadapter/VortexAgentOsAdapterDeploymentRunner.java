package com.vortex.agent.osadapter;

import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentResponse;
import com.vortex.agent.messages.VortexAgentOsAdapterDeploymentRequest;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterDeploymentRunner {

	public VortexAgentRunDeploymentResponse runDeployment(VortexAgentOsAdapterDeploymentRequest request, VortexAgentOsAdapterState state);
}
