package com.vortex.agent.osadapter;

import java.util.List;

import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterInspectMachineEvents {

	List<VortexAgentMachineEventStats> inspectMachineEvents(VortexAgentOsAdapterState state, long onwards, byte level);
}
