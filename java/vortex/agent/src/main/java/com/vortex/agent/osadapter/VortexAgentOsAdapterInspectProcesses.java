package com.vortex.agent.osadapter;

import java.util.List;

import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterInspectProcesses {

	List<VortexAgentProcess> inspectProcesses(VortexAgentOsAdapterState state);
}
