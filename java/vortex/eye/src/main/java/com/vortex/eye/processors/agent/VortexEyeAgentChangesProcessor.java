package com.vortex.eye.processors.agent;

import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.utils.MH;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentChangesRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentChangesResponse;
import com.vortex.eye.processors.VortexEyeResultProcessor;
import com.vortex.eye.state.VortexEyeAgentState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeAgentChangesProcessor extends VortexEyeResultProcessor<VortexAgentChangesResponse> {

	private static final long DEFAULT_CHECK = 5000;
	//public final OutputPort<MsgMessage> toAgentPort = newOutputPort(MsgMessage.class);
	//public final OutputPort<TestTrackDeltas> agentRevisionPort = newOutputPort(TestTrackDeltas.class);

	private long processCheckMs;
	private long netLinkCheckMs;
	private long netConnCheckMs;
	private long netAddrCheckMs;
	private long machineCheckMs;
	private long filesysCheckMs;
	private long chrntabCheckMs;
	private long mchnevtCheckMs;

	private long minCheckMs;

	public VortexEyeAgentChangesProcessor() {
		super(VortexAgentChangesResponse.class);
	}

	public void init() {
		super.init();
		processCheckMs = getTools().getOptional("agent.process.checkms", DEFAULT_CHECK);
		netLinkCheckMs = getTools().getOptional("agent.netlink.checkms", DEFAULT_CHECK);
		netConnCheckMs = getTools().getOptional("agent.netconnection.checkms", DEFAULT_CHECK);
		netAddrCheckMs = getTools().getOptional("agent.netaddress.checkms", DEFAULT_CHECK);
		machineCheckMs = getTools().getOptional("agent.machine.checkms", DEFAULT_CHECK);
		filesysCheckMs = getTools().getOptional("agent.filesystem.checkms", DEFAULT_CHECK);
		chrntabCheckMs = getTools().getOptional("agent.chrontab.checkms", DEFAULT_CHECK);
		mchnevtCheckMs = getTools().getOptional("agent.machineevent.checkms", DEFAULT_CHECK);
		this.minCheckMs = MH.minl(processCheckMs, netLinkCheckMs, netConnCheckMs, netAddrCheckMs, machineCheckMs, filesysCheckMs, chrntabCheckMs, mchnevtCheckMs);
	}

	@Override
	public void processAction(ResultMessage<VortexAgentChangesResponse> resultMessage, VortexEyeState state, ThreadScope threadScope) throws Exception {
		long now = getTools().getNow();
		String processUid = getProcessUidFromAgentResponse(resultMessage);
		VortexEyeAgentState agent = state.getAgentByPuid(processUid);

		short mask = 0;

		if (agent.getLastProcessCheckMs() + processCheckMs <= now) {
			agent.setLastProcessCheckMs(now);
			mask |= VortexAgentChangesRequest.PROCESSES;
		}
		if (agent.getLastNetLinkCheckMs() + netLinkCheckMs <= now) {
			agent.setLastNetLinkCheckMs(now);
			mask |= VortexAgentChangesRequest.NET_LINKS;
		}
		if (agent.getLastNetConnCheckMs() + netConnCheckMs <= now) {
			agent.setLastNetConnCheckMs(now);
			mask |= VortexAgentChangesRequest.NET_CONNECTIONS;
		}
		if (agent.getLastNetAddrCheckMs() + netAddrCheckMs <= now) {
			agent.setLastNetAddrCheckMs(now);
			mask |= VortexAgentChangesRequest.NET_ADDRESSES;
		}
		if (agent.getLastMachineCheckMs() + machineCheckMs <= now) {
			agent.setLastMachineCheckMs(now);
			mask |= VortexAgentChangesRequest.MACHINE;
		}
		if (agent.getLastFilesysCheckMs() + filesysCheckMs <= now) {
			agent.setLastFilesysCheckMs(now);
			mask |= VortexAgentChangesRequest.FILESYSTEMS;
		}
		if (agent.getLastChrntabCheckMs() + chrntabCheckMs <= now) {
			agent.setLastChrntabCheckMs(now);
			mask |= VortexAgentChangesRequest.CRON;
		}
		if (agent.getLastMchnevtCheckMs() + mchnevtCheckMs <= now) {
			agent.setLastMchnevtCheckMs(now);
			mask |= VortexAgentChangesRequest.AGENT_MACHINE_EVENTS;
		}

		VortexAgentChangesRequest req = nw(VortexAgentChangesRequest.class);
		req.setMask(mask);
		log.fine("Sending changes request to agent: " + processUid + ",  mask: " + mask);
		sendRequestToAgent(req, processUid, getLoopbackPort(), minCheckMs);

	}
}
