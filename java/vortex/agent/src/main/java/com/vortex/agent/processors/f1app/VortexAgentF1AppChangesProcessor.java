package com.vortex.agent.processors.f1app;

import java.util.ArrayList;

import com.f1.container.ThreadScope;
import com.f1.povo.f1app.F1AppChanges;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppEvent;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.agent.VortexAgentChanges;
import com.vortex.agent.processors.VortexAgentBasicProcessor;
import com.vortex.agent.state.VortexAgentF1AppState;
import com.vortex.agent.state.VortexAgentState;
import com.vortex.agent.state.VortexAgentStateUtils;

public class VortexAgentF1AppChangesProcessor extends VortexAgentBasicProcessor<F1AppChanges> {

	public VortexAgentF1AppChangesProcessor() {
		super(F1AppChanges.class);
	}

	@Override
	public void processAction(F1AppChanges action, VortexAgentState state, ThreadScope threadScope) throws Exception {

		final F1AppChanges changes = action;
		final String f1AppProcessUid = changes.getF1AppProcessUid();
		VortexAgentF1AppState appState = state.getF1AppByProcessUidNoThrow(f1AppProcessUid);
		if (appState == null) {
			LH.info(log, "Received changes from unknown f1 app: ", changes.getF1AppProcessUid(), " seqnum: ", changes.getSeqNum());
			return;
		}

		//sequence numbers
		if (appState.getCurrentSeqNum() + 1 != changes.getSeqNum())
			throw new IllegalStateException("Bad sequence number at " + appState.getCurrentSeqNum() + ", received: " + changes.getSeqNum());
		appState.setCurrentSeqNum(changes.getSeqNum());

		final long aid = appState.getF1AppInstance().getId();

		//process addeds
		final ArrayList<F1AppEntity> addedToEye = VortexAgentStateUtils.processAdds(appState, changes.getF1AppEntitiesAdded());
		final long[] removedToEye = VortexAgentStateUtils.processRemoves(appState, changes.getF1AppEntitiesRemoved());
		final byte[] updatedToEye = VortexAgentStateUtils.processUpdates(appState, changes.getF1AppEntitiesUpdated());

		//send new app to eye
		if (state.getIsEyeConnected() && state.getIsSnapshotSentToEye()) {
			final VortexAgentChanges toEye = nw(VortexAgentChanges.class);
			toEye.setAgentProcessUid(EH.getProcessUid());
			toEye.setF1AppEntitiesAdded(addedToEye);
			toEye.setF1AppEntitiesRemoved(removedToEye);
			toEye.setF1AppEntitiesUpdated(updatedToEye);
			if (CH.isntEmpty(action.getF1AppEvents()))
				for (F1AppEvent event : action.getF1AppEvents())
					event.setF1AppInstanceId(aid);
			toEye.setF1AppEvents(action.getF1AppEvents());
			toEye.setSeqNum(state.nextSequenceNumber());
			sendToEye(toEye);
		} else {
			//TODO: we need to store audit events
		}

	}

}
