package com.vortex.agent.osadapter;

import java.util.List;

import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterInspectNetLinks {

	List<VortexAgentNetLink> inspectNetLinks(VortexAgentOsAdapterState state);
}
