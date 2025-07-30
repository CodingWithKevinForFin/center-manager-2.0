package com.vortex.agent.osadapter;

import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessResponse;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterSendSignal {

	public VortexAgentRunSignalProcessResponse sendSignal(VortexAgentRunSignalProcessRequest request, VortexAgentOsAdapterState state);
}
