package com.vortex.agent.osadapter;

import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandResponse;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterCommandRunner {

	public VortexAgentRunOsCommandResponse runCommand(VortexAgentRunOsCommandRequest request, VortexAgentOsAdapterState state);
}
