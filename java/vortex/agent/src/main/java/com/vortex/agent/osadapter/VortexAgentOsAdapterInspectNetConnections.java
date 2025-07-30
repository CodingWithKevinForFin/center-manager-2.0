package com.vortex.agent.osadapter;

import java.util.List;

import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterInspectNetConnections {

	List<VortexAgentNetConnection> inspectNetConnections(VortexAgentOsAdapterState state);
}
