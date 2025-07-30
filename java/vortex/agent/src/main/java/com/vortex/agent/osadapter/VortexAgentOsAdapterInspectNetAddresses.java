package com.vortex.agent.osadapter;

import java.util.List;

import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterInspectNetAddresses {

	List<VortexAgentNetAddress> inspectNetAddresses(VortexAgentOsAdapterState state);
}
