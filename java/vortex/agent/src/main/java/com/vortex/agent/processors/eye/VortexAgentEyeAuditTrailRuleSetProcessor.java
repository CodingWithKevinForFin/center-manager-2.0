package com.vortex.agent.processors.eye;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.container.ThreadScope;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.LongSet;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRuleSet;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.agent.processors.VortexAgentBasicProcessor;
import com.vortex.agent.state.VortexAgentF1AppState;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentEyeAuditTrailRuleSetProcessor extends VortexAgentBasicProcessor<VortexEyeAuditTrailRuleSet> {

	public VortexAgentEyeAuditTrailRuleSetProcessor() {
		super(VortexEyeAuditTrailRuleSet.class);
	}

	@Override
	public void processAction(VortexEyeAuditTrailRuleSet action, VortexAgentState state, ThreadScope threadScope) throws Exception {
		BasicMultiMap.List<String, VortexEyeAuditTrailRule> toAgents = new BasicMultiMap.List<String, VortexEyeAuditTrailRule>();
		for (VortexEyeAuditTrailRule rule : action.getRules()) {
			if (rule.getRevision() == VortexAgentEntity.REVISION_DONE) {//delete
				Set<String> agentUids = state.removeAgentsForRule(rule.getId());
				for (String processUid : CH.i(agentUids))
					toAgents.putMulti(processUid, rule);
				state.removeAuditTrailRule(rule.getId());
			} else {
				VortexEyeAuditTrailRule existing = state.getAuditTrailRule(rule.getId());
				if (existing != null) {//update
					state.addAuditTrailRule(rule);
					for (VortexAgentF1AppState app : state.getApps()) {
						F1AppInstance ss = app.getF1AppInstance();
						if (auditRuleAppliesToApp(rule, ss)) {
							state.addRuleToAgentMapping(rule.getId(), ss.getProcessUid());
							toAgents.putMulti(app.getF1AppInstance().getProcessUid(), rule);
						} else if (state.removeAgentRuleMapping(rule.getId(), ss.getProcessUid())) {//the update caused this rule to no longer apply, send a delete
							VortexEyeAuditTrailRule rule2 = nw(VortexEyeAuditTrailRule.class);
							rule2.setId(rule.getId());
							rule.setRevision(VortexAgentUtils.REVISION_DONE);
							toAgents.putMulti(app.getF1AppInstance().getProcessUid(), rule2);
						}
					}
				} else {//insert
					state.addAuditTrailRule(rule);
					for (VortexAgentF1AppState app : state.getApps()) {
						if (auditRuleAppliesToApp(rule, app.getF1AppInstance())) {
							state.addRuleToAgentMapping(rule.getId(), app.getF1AppInstance().getProcessUid());
							toAgents.putMulti(app.getF1AppInstance().getProcessUid(), rule);
						}
					}
				}
			}
		}
		if (action.getIsSnapshot()) {
			final LongSet toRemove = new LongSet();
			for (VortexEyeAuditTrailRule i : state.getAuditTrailRules())
				toRemove.add(i.getId());
			for (VortexEyeAuditTrailRule i : action.getRules())
				toRemove.remove(i.getId());
			for (long id : toRemove) {
				VortexEyeAuditTrailRule rule = state.removeAuditTrailRule(id);
				rule.setRevision(VortexAgentUtils.REVISION_DONE);
				Set<String> agentUids = state.removeAgentsForRule(rule.getId());
				for (String processUid : CH.i(agentUids))
					toAgents.putMulti(processUid, rule);
			}
		}
		for (Entry<String, List<VortexEyeAuditTrailRule>> e : toAgents.entrySet()) {
			final VortexEyeAuditTrailRuleSet batch = nw(VortexEyeAuditTrailRuleSet.class);
			batch.setRules(e.getValue());
			sendToF1App(batch, e.getKey());
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
		for (Entry<Short, String> e : rule.getRules().entrySet()) {
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
