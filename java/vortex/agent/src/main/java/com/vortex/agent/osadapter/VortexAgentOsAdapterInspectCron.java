package com.vortex.agent.osadapter;

import java.util.List;

import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterInspectCron {

	List<VortexAgentCron> inspectCron(VortexAgentOsAdapterState state);
}
