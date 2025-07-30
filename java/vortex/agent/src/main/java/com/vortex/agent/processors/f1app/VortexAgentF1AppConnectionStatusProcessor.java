package com.vortex.agent.processors.f1app;

import java.util.HashSet;
import java.util.Set;

import com.f1.bootstrap.appmonitor.AppMonitorUtils;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.reqres.F1AppSnapshotRequest;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.agent.VortexAgentChanges;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;
import com.vortex.agent.messages.VortexAgentDeploymentUpdateMessage;
import com.vortex.agent.processors.VortexAgentBasicProcessor;
import com.vortex.agent.state.VortexAgentDeploymentWrapper;
import com.vortex.agent.state.VortexAgentF1AppState;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentF1AppConnectionStatusProcessor extends VortexAgentBasicProcessor<MsgStatusMessage> {

	public final OutputPort<VortexAgentDeploymentUpdateMessage> toDeployments = newOutputPort(VortexAgentDeploymentUpdateMessage.class);
	public VortexAgentF1AppConnectionStatusProcessor() {
		super(MsgStatusMessage.class);
	}

	@Override
	public void processAction(MsgStatusMessage action, VortexAgentState state, ThreadScope threadScope) throws Exception {
		String f1AppProcessUid = action.getSuffix();
		if ("f1.agent.to.app".equals(action.getTopic()) && f1AppProcessUid != null) {
			if (!action.getIsConnected()) {
				final VortexAgentF1AppState app = state.removeF1AppNoThrow(f1AppProcessUid);
				if (app != null) {
					publishRemoveToDeployments(app, state, threadScope);

					for (VortexEyeAuditTrailRule rule : state.getAuditTrailRules()) {
						Set<String> agentsForRule = state.getAgentsForRule(rule.getId());
						if (agentsForRule == null)
							continue;
						agentsForRule.remove(f1AppProcessUid);
					}

					LH.info(log, "Disconnect from: ", AppMonitorUtils.describe(app.getF1AppInstance()));
					if (state.getIsEyeConnected() && state.getIsSnapshotSentToEye()) {
						VortexAgentChanges update = nw(VortexAgentChanges.class);
						F1AppInstance instance = app.getF1AppInstance();
						update.setF1AppEntitiesRemoved(new long[] { instance.getF1AppInstanceId(), instance.getId() });
						update.setSeqNum(state.nextSequenceNumber());
						update.setAgentProcessUid(EH.getProcessUid());
						sendToEye(update);
					}
				} else
					LH.warning(log, "Disconnect from unknown f1app: ", f1AppProcessUid);

			} else {
				F1AppSnapshotRequest req = nw(F1AppSnapshotRequest.class);
				req.setAgentProcessUid(EH.getProcessUid());
				req.setTargetF1AppProcessUid(f1AppProcessUid);
				sendRequestToF1App(req, f1AppProcessUid, getResponseRoutingPort());
			}
		}
	}

	private void publishRemoveToDeployments(VortexAgentF1AppState appState, VortexAgentState state, ThreadScope ts) {
		final VortexAgentDeploymentUpdateMessage msg = nw(VortexAgentDeploymentUpdateMessage.class);
		final Set<String> puids = new HashSet<String>();
		puids.add(appState.getPuid());
		msg.setRemovedPuids(puids);
		for (VortexAgentDeploymentWrapper i : state.getDeployments()) {
			long id = i.getId();
			final String partitionId = "DP_" + id;
			toDeployments.send(msg, partitionId, ts);
		}
	}
}
