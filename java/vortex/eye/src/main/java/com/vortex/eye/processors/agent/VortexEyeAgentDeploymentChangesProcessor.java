package com.vortex.eye.processors.agent;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Valued;
import com.f1.base.ValuedSchema;
import com.f1.container.ContainerTools;
import com.f1.container.ThreadScope;
import com.f1.povo.db.DbRequestMessage;
import com.f1.utils.LH;
import com.f1.utils.VH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexUpdateDeploymentStatusesFromAgent;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.processors.VortexEyeBasicProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeAgentDeploymentChangesProcessor extends VortexEyeBasicProcessor<VortexUpdateDeploymentStatusesFromAgent> {

	public VortexEyeAgentDeploymentChangesProcessor() {
		super(VortexUpdateDeploymentStatusesFromAgent.class);
	}

	@Override
	public void processAction(VortexUpdateDeploymentStatusesFromAgent action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		VortexEyeChangesMessageBuilder cmb = state.getChangesMessageBuilder();
		for (VortexDeployment dep : action.getUpdated()) {
			VortexDeployment existing = state.getDeployment(dep.getId());
			if (existing == null) {
				LH.warning(log, "update status for unknown deployment: ", dep);
			} else {
				VortexDeployment nuw = existing.clone();
				ValuedSchema<Valued> schema = nuw.askSchema();
				boolean dbChange = false;
				for (byte pid : new byte[] { VortexDeployment.PID_CURRENT_BUILD_INVOKED_BY, VortexDeployment.PID_CURRENT_BUILD_RESULT_ID,
						VortexDeployment.PID_DEPLOYED_INSTANCE_ID, VortexDeployment.PID_MESSAGE, VortexDeployment.PID_RUNNING_PID, VortexDeployment.PID_RUNNING_PROCESS_UID,
						VortexDeployment.PID_STATUS }) {
					if (!schema.askValuedParam(pid).areEqual(dep, nuw)) {
						dbChange = true;
						break;
					}
				}
				VH.copyPartialFields(dep, nuw);
				state.addDeployment(nuw);
				if (dbChange)
					sendToDb(insertDeploymentStatus(nuw, this.getTools()));
				cmb.writeUpdate(existing, nuw);
			}
		}
		//Send to guis
		sendToClients(cmb.popToChangesMsg(state.nextSequenceNumber()));
	}

	static public DbRequestMessage insertDeploymentStatus(VortexDeployment deploymentStatus, ContainerTools tools) {
		boolean active = deploymentStatus.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("revision", deploymentStatus.getRevision());
		params.put("now", deploymentStatus.getNow());
		params.put("deployment_id", deploymentStatus.getId());
		params.put("status", deploymentStatus.getStatus());
		params.put("running_pid", deploymentStatus.getRunningPid());
		params.put("running_process_uid", deploymentStatus.getRunningProcessUid());
		params.put("message", deploymentStatus.getMessage());
		params.put("build_result_id", deploymentStatus.getCurrentBuildResultId());
		params.put("build_invoked_by", deploymentStatus.getCurrentBuildInvokedBy());
		params.put("deployed_instance_id", deploymentStatus.getDeployedInstanceId());
		DbRequestMessage dbRequest = tools.nw(DbRequestMessage.class);
		dbRequest.setId("insert_deployment_status");
		dbRequest.setParams(params);
		return dbRequest;
	}

}
