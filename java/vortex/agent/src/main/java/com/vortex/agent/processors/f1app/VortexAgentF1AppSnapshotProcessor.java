package com.vortex.agent.processors.f1app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.f1.bootstrap.appmonitor.AppMonitorUtils;
import com.f1.container.OutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.povo.f1app.F1AppChanges;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.povo.f1app.reqres.F1AppSnapshotResponse;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentChanges;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.agent.messages.VortexAgentDeploymentUpdateMessage;
import com.vortex.agent.processors.VortexAgentResultProcessor;
import com.vortex.agent.state.VortexAgentDeploymentWrapper;
import com.vortex.agent.state.VortexAgentF1AppState;
import com.vortex.agent.state.VortexAgentState;
import com.vortex.agent.state.VortexAgentStateUtils;

public class VortexAgentF1AppSnapshotProcessor extends VortexAgentResultProcessor<F1AppSnapshotResponse> {

	public final OutputPort<VortexAgentDeploymentUpdateMessage> toDeployments = newOutputPort(VortexAgentDeploymentUpdateMessage.class);

	public VortexAgentF1AppSnapshotProcessor() {
		super(F1AppSnapshotResponse.class);
	}

	@Override
	public void processAction(ResultMessage<F1AppSnapshotResponse> result, VortexAgentState state, ThreadScope threadScope) throws Exception {

		if (!VortexAgentUtils.verifyOk(log, result))
			return;

		final F1AppChanges changes = result.getAction().getSnapshot();
		final List<F1AppEntity> added = changes.getF1AppEntitiesAdded();
		final String f1AppProcessUid = changes.getF1AppProcessUid();
		final F1AppEntity first = CH.firstOr(added, null);

		//validation
		if (!(first instanceof F1AppInstance))
			throw new RuntimeException("first element in snapshot add is not an F1 App Instance: " + first + ", from: " + changes.getF1AppProcessUid());
		if (changes.getF1AppEvents() != null)
			throw new RuntimeException("snapshot should not have events");
		if (changes.getF1AppEntitiesUpdated() != null)
			throw new RuntimeException("snapshot should not have changes");
		if (changes.getF1AppEntitiesRemoved() != null)
			throw new RuntimeException("snapshot should not have removes");
		if (OH.ne(f1AppProcessUid, getF1AppProcessUid(result)))
			throw new RuntimeException("bad processUid: " + f1AppProcessUid + ", expecting: " + getF1AppProcessUid(result));
		if (state.getF1AppByProcessUidNoThrow(f1AppProcessUid) != null)
			throw new IllegalStateException("already added: " + changes.getF1AppProcessUid());

		final F1AppInstance appInstance = (F1AppInstance) first;
		if (log.isLoggable(Level.INFO))
			LH.info(log, "Received f1App snapshot", AppMonitorUtils.describe(appInstance), " with ", CH.size(changes.getF1AppEntitiesAdded()) + " entities");

		//Build f1 app state
		final long now = getTools().getNow();
		final long appOrigId = appInstance.getId();
		appInstance.setId(state.createNextId());
		appInstance.setF1AppInstanceId(appInstance.getId());
		VortexAgentF1AppState appState = new VortexAgentF1AppState(state, appOrigId, appInstance, now);
		state.addF1App(appState);
		appState.setCurrentSeqNum(changes.getSeqNum());

		//process addeds
		List<F1AppEntity> addedToEye = new ArrayList<F1AppEntity>(added.size());
		addedToEye.add(appInstance);
		addedToEye.addAll(VortexAgentStateUtils.processAdds(appState, CH.sublistStartingAt(added, 1)));

		//send new app to eye
		if (state.getIsEyeConnected() && state.getIsSnapshotSentToEye()) {
			VortexAgentChanges toEye = nw(VortexAgentChanges.class);
			toEye.setAgentProcessUid(EH.getProcessUid());
			toEye.setF1AppEntitiesAdded(addedToEye);
			toEye.setSeqNum(state.nextSequenceNumber());
			sendToEye(toEye);
		}

		//Handle Rules
		//		final List<VortexEyeAuditTrailRule> rules = new ArrayList<VortexEyeAuditTrailRule>();
		//		for (VortexEyeAuditTrailRule rule : state.getAuditTrailRules()) {
		//			if (auditRuleAppliesToApp(rule, appInstance)) {
		//				state.addRuleToAgentMapping(rule.getId(), f1AppProcessUid);
		//				rules.add(rule);
		//			}
		//		}
		//		VortexEyeAuditTrailRuleSet ruleSet = nw(VortexEyeAuditTrailRuleSet.class);
		//		ruleSet.setRules(rules);
		//		sendToF1App(ruleSet, f1AppProcessUid);
		publishToDeployments(appState, state, threadScope);

	}

	private void publishToDeployments(VortexAgentF1AppState appState, VortexAgentState state, ThreadScope ts) {
		final VortexAgentDeploymentUpdateMessage msg = nw(VortexAgentDeploymentUpdateMessage.class);
		final Map<String, String> puidToDiids = new HashMap<String, String>();
		puidToDiids.put(appState.getPuid(), appState.getDiid());
		msg.setAddedPuidToDiids(puidToDiids);
		for (VortexAgentDeploymentWrapper i : state.getDeployments()) {
			long id = i.getId();
			final String partitionId = "DP_" + id;
			toDeployments.send(msg, partitionId, ts);
		}
	}

	public static boolean auditRuleAppliesToApp(VortexEyeAuditTrailRule rule, F1AppInstance app) {
		switch (rule.getRuleType()) {
			case F1AppAuditTrailRule.EVENT_TYPE_F1:
			case F1AppAuditTrailRule.EVENT_TYPE_SQL:
			case F1AppAuditTrailRule.EVENT_TYPE_MSG:
			case F1AppAuditTrailRule.EVENT_TYPE_LOG:
				break;
			default:
				return false;
		}
		for (Map.Entry<Short, String> e : rule.getRules().entrySet()) {
			switch (e.getKey().shortValue()) {
				case F1AppAuditTrailRule.RULE_PROCESS_APPNAME_MASK:
					if (!SH.m(e.getValue()).matches(app.getAppName()))
						return false;
					break;
				case F1AppAuditTrailRule.RULE_PROCESS_HOSTMACHINE_MASK:
					if (!SH.m(e.getValue()).matches(app.getHostName()))
						return false;
					break;
				case F1AppAuditTrailRule.RULE_PROCESS_USER_MASK:
					if (!SH.m(e.getValue()).matches(app.getUserName()))
						return false;
					break;
			}

		}
		return true;
	}

}
