package com.vortex.agent.osadapter;

import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchResponse;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterFileSearcher {

	VortexAgentFileSearchResponse searchFiles(VortexAgentFileSearchRequest action, VortexAgentOsAdapterState state);
}
