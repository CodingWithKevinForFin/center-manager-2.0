package com.vortex.agent.osadapter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.f1.utils.EH;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public class VortexAgentNoopOsAdapter extends VortexAgentAbstractOsAdapter {

	@Override
	public List<VortexAgentCron> inspectCron(VortexAgentOsAdapterState state) {
		return Collections.emptyList();
	}

	@Override
	public List<VortexAgentMachineEventStats> inspectMachineEvents(VortexAgentOsAdapterState state, long onwards, byte level) {
		return Collections.emptyList();
	}

	@Override
	public List<VortexAgentNetAddress> inspectNetAddresses(VortexAgentOsAdapterState state) {
		return Collections.emptyList();
	}

	@Override
	public List<VortexAgentNetLink> inspectNetLinks(VortexAgentOsAdapterState state) {
		return Collections.emptyList();
	}

	@Override
	public List<VortexAgentNetConnection> inspectNetConnections(VortexAgentOsAdapterState state) {
		return Collections.emptyList();
	}

	@Override
	public List<VortexAgentProcess> inspectProcesses(VortexAgentOsAdapterState state) {
		return Collections.emptyList();
	}

	@Override
	public long runLastReboot(VortexAgentOsAdapterState state) {
		return -1;
	}

	@Override
	public void runFree(VortexAgentOsAdapterState state, VortexAgentMachine sink) {
	}

	@Override
	public String getMachineUid(VortexAgentOsAdapterState state) throws IOException {
		return "UID_" + EH.getLocalHost();
	}

	@Override
	public String getHostName(VortexAgentOsAdapterState state) throws IOException {
		return EH.getLocalHost();
	}

}
