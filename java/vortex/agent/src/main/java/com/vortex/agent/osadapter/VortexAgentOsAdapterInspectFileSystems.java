package com.vortex.agent.osadapter;

import java.util.List;

import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterInspectFileSystems {

	List<VortexAgentFileSystem> inspectFileSystems(VortexAgentOsAdapterState state);
}
