package com.vortex.eye.processors.agent;

import com.f1.container.ThreadScope;
import com.f1.povo.f1app.F1AppEvent;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.agent.VortexAgentChanges;
import com.f1.vortexcommon.msg.eye.VortexEyeChanges;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.processors.VortexEyeBasicProcessor;
import com.vortex.eye.state.VortexEyeAgentState;
import com.vortex.eye.state.VortexEyeState;
import com.vortex.eye.state.VortexEyeStateUtils;

public class VortexEyeAgentF1ChangesProcessor extends VortexEyeBasicProcessor<VortexAgentChanges> {

	public VortexEyeAgentF1ChangesProcessor() {
		super(VortexAgentChanges.class);
	}

	@Override
	public void processAction(VortexAgentChanges action, VortexEyeState state, ThreadScope threadScope) throws Exception {

		VortexEyeAgentState agentState = state.getAgentByPuid(action.getAgentProcessUid());
		if (agentState == null) {
			LH.warning(log, "Ignoring changes prior to snapshot from agent: ", action.getAgentProcessUid());
			return;
		}
		if (agentState.getCurrentSeqNum() == -1L) {
			LH.info(log, "Received update, prior to  snapshot from: ", action.getAgentProcessUid());
			return;
		}
		if (agentState.getCurrentSeqNum() + 1 != action.getSeqNum()) {
			LH.warning(log, "SeqNum mismatch at ", agentState.getCurrentSeqNum(), ", received: ", action.getSeqNum(), " Updating seqnum, but data may be inconsistent");
		}
		agentState.setCurrentSeqNum(action.getSeqNum());

		final long now = EH.currentTimeMillis();
		VortexEyeChangesMessageBuilder msgBuilder = state.getChangesMessageBuilder();
		VortexEyeStateUtils.processF1AppEntityAdds(agentState, action.getF1AppEntitiesAdded(), this, now, msgBuilder);
		VortexEyeStateUtils.processF1AppEntityUpdates(agentState, action.getF1AppEntitiesUpdated(), this, now, msgBuilder);
		VortexEyeStateUtils.processF1AppEntityRemoves(agentState, action.getF1AppEntitiesRemoved(), this, now, msgBuilder);
		VortexEyeStateUtils.processAgentEntityAdds(agentState, action.getAgentEntitiesAdded(), this, now, msgBuilder);
		VortexEyeStateUtils.processAgentEntityUpdates(agentState, action.getAgentEntitiesUpdated(), this, now, msgBuilder);
		VortexEyeStateUtils.processAgentEntityRemoves(agentState, action.getAgentEntitiesRemoved(), this, now, msgBuilder);
		//VortexEyeAmiUtils.processAgentAmiEvents(state, agentState, action.getAmiEvents(), action.getAmiStringPoolMap(), this, now, msgBuilder);

		if (msgBuilder.hasChanges()) {
			VortexEyeChanges toClient = msgBuilder.popToChangesMsg(state.nextSequenceNumber());
			//toClient.setAmiKeysStringPoolMap(state.popPendingNewAmiKeysSink());
			//toClient.setAmiValuesStringPoolMap(state.popPendingAmiValuesStringPool());
			if (CH.isntEmpty(action.getF1AppEvents())) {

				//mapping IDs
				for (F1AppEvent event : action.getF1AppEvents())
					event.setF1AppInstanceId(agentState.getF1AppByOrigAiid(event.getF1AppInstanceId()).getF1AppInstance().getId());

				toClient.setF1AppEvents(action.getF1AppEvents());
			}
			sendToClients(toClient);
		}

		//TODO: 
	}
}
