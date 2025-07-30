package com.vortex.agent.processors.eye;

import java.util.HashMap;
import java.util.Map;

import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.structs.LongSet;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentUpdateDeploymentRequest;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.vortex.agent.messages.VortexAgentDeploymentUpdateMessage;
import com.vortex.agent.processors.VortexAgentBasicProcessor;
import com.vortex.agent.state.VortexAgentDeploymentWrapper;
import com.vortex.agent.state.VortexAgentF1AppState;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentEyeUpdateDeploymentsProcessor extends VortexAgentBasicProcessor<VortexAgentUpdateDeploymentRequest> {

	public final OutputPort<VortexAgentDeploymentUpdateMessage> toDeployments = newOutputPort(VortexAgentDeploymentUpdateMessage.class);
	public VortexAgentEyeUpdateDeploymentsProcessor() {
		super(VortexAgentUpdateDeploymentRequest.class);
	}

	@Override
	public void processAction(VortexAgentUpdateDeploymentRequest action, VortexAgentState state, ThreadScope threadScope) throws Exception {
		LongSet toRemoveDueToSnapshot = new LongSet();
		if (action.getIsSnapshot())
			for (VortexAgentDeploymentWrapper i : state.getDeployments())
				toRemoveDueToSnapshot.add(i.getId());
		if (CH.isntEmpty(action.getUpdated())) {
			Map<String, String> puidToDiids = new HashMap<String, String>();
			for (VortexAgentF1AppState app : state.getApps())
				puidToDiids.put(app.getPuid(), app.getDiid());
			for (VortexDeployment deployment : action.getUpdated()) {
				deployment.lock();
				toRemoveDueToSnapshot.remove(deployment.getId());
				state.addDeployment(new VortexAgentDeploymentWrapper(deployment));
				String partitionId = "DP_" + deployment.getId();
				VortexAgentDeploymentUpdateMessage updateMsg = nw(VortexAgentDeploymentUpdateMessage.class);
				updateMsg.setPartitionId(partitionId);
				updateMsg.setDeployment(deployment);
				updateMsg.setAddedPuidToDiids(puidToDiids);
				toDeployments.send(updateMsg, threadScope);
			}
		}

		for (long id : AH.i(action.getRemoved()))
			toRemoveDueToSnapshot.add(id);

		for (long id : toRemoveDueToSnapshot.toLongArray()) {
			if (state.removeDeployment(id) != null) {
				String partitionId = "DP_" + id;
				VortexAgentDeploymentUpdateMessage updateMsg = nw(VortexAgentDeploymentUpdateMessage.class);
				updateMsg.setPartitionId(partitionId);
				VortexDeployment deployment = nw(VortexDeployment.class);
				deployment.setRevision(VortexAgentEntity.REVISION_DONE);
				deployment.setId(id);
				updateMsg.setDeployment(deployment);
				toDeployments.send(updateMsg, threadScope);
			}
		}
	}

}
