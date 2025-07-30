package com.vortex.agent.osadapter;

import java.io.IOException;

import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public interface VortexAgentOsAdapterInspectMachine {

	VortexAgentMachine inspectMachine(VortexAgentOsAdapterState state) throws IOException;
}
