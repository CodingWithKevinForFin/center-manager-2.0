package com.vortex.agent.osadapter;

import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteResponse;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterFileDeleter {

	VortexAgentFileDeleteResponse deleteFiles(VortexAgentFileDeleteRequest requestMessage, VortexAgentOsAdapterState state);

}
