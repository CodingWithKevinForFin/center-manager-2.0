package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.CH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRuleSet;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageAuditTrailRuleRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageAuditTrailRuleResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageAuditRulesItinerary extends AbstractVortexEyeItinerary<VortexEyeManageAuditTrailRuleRequest> {

	private VortexEyeAuditTrailRule old;
	private VortexEyeAuditTrailRule nuw;
	private VortexEyeManageAuditTrailRuleResponse r;
	private boolean isDelete;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageAuditTrailRuleResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getRule();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (nuw.getId() != 0) {
			old = state.getAuditTrailRule(nuw.getId());
			if (old == null) {
				r.setMessage("AuditTrailRule not found for update / delete: " + nuw.getId());
				return STATUS_COMPLETE;
			}
			if (isDelete) {
				nuw = old.clone();
				nuw.setRevision(VortexAgentEntity.REVISION_DONE);
			} else
				nuw.setRevision(old.getRevision() + 1);
		} else {
			nuw.setId(getState().createNextId());
			nuw.setRevision(0);
		}
		nuw.setNow(now);
		sendToDb(nuw, worker);
		return STATUS_ACTIVE;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		DbResultMessage dbResult = (DbResultMessage) result.getAction();
		if (!dbResult.getOk()) {
			r.setMessage(dbResult.getMessage());
		} else {
			if (isDelete)
				getState().removeAuditTrailRule(nuw.getId());
			else
				getState().addAuditTrailRule(nuw);
			r.setOk(true);
		}
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setRule(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(old, nuw);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));

			final VortexEyeAuditTrailRuleSet ruleSet = getTools().nw(VortexEyeAuditTrailRuleSet.class);
			ruleSet.setRules(CH.l(nuw));
			worker.sendToAgent(this, ruleSet, null);
		}
		return r;
	}

	public void sendToDb(VortexEyeAuditTrailRule rule, VortexEyeItineraryWorker worker) {
		boolean active = rule.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("now", rule.getNow());
		params.put("active", active);
		params.put("id", rule.getId());
		params.put("revision", rule.getRevision());
		params.put("rule_type", rule.getRuleType());
		params.put("rules", getTools().getServices().getJsonConverter().objectToString(rule.getRules()));
		DbRequestMessage msg = getTools().nw(DbRequestMessage.class);
		msg.setId("insert_audit_trail_rule");
		msg.setParams(params);
		worker.sendToDb(this, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageAuditTrailRuleRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_AUDIT_RULE);
		auditEntity(sink, "AIID", action.getRule());
	}

}
