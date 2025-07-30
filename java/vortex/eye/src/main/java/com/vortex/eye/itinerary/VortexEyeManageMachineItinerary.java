package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMachineRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMachineResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageMachineItinerary extends AbstractVortexEyeItinerary<VortexEyeManageMachineRequest> {
	private static final Logger log = LH.get(VortexEyeManageMachineItinerary.class);

	private VortexAgentMachine old, nuw;
	private VortexEyeManageMachineResponse r;
	private boolean isDelete;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageMachineResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getMachine();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		String muid = nuw.getMachineUid();
		VortexEyeMachineState mstate = state.getMachineByMuidNoThrow(muid);
		if (mstate == null) {
			r.setMessage("Machine not found for update / delete: " + muid);
			return STATUS_COMPLETE;
		}
		old = mstate.getMachine();
		if (isDelete) {
			//if (mstate.getAmiApplications().iterator().hasNext()) {
			//r.setMessage("Can not delete machines with active ami applications: " + muid);
			//return STATUS_COMPLETE;
			//}
			if (old.getAgentProcessUid() != null) {
				r.setMessage("Can not delete machines that are running : " + muid);
				return STATUS_COMPLETE;
			}
			if (CH.isntEmpty(state.getDeploymentsByMachineUid(muid))) {
				r.setMessage("Can not delete machines with active deployments: " + muid + " --> " + state.getDeploymentsByMachineUid(muid).get(0).getDescription());
				return STATUS_COMPLETE;
			}
			for (VortexEyeBackup backup : state.getBackups()) {
				if (muid.equals(backup.getSourceMachineUid())) {
					r.setMessage("Can not delete machines with active backups: " + muid + " --> " + backup.getSourcePath());
					return STATUS_COMPLETE;
				}
			}
			for (VortexEyeBackupDestination dest : state.getBackupDestinations()) {
				if (muid.equals(dest.getDestinationMachineUid())) {
					r.setMessage("Can not delete machines with active backup destinations: " + muid + " --> " + dest.getName());
					return STATUS_COMPLETE;
				}
			}
			for (VortexAgentDbServer db : state.getDbServers()) {
				if (muid.equals(db.getMachineUid())) {
					r.setMessage("Can not delete machines with declared databases: " + muid + " --> " + db.getDescription());
					return STATUS_COMPLETE;
				}
			}
			//for (VortexEyeScheduledTask backup : state.getScheduledTasks()) {
			//TODO: check for schedules that are dependent on this machine
			//}
			nuw = old.clone();
			nuw.setRevision(VortexEyeUtils.REVISION_DONE);
			nuw.setNow(now);
			nuw.lock();
			sendToDb(nuw, worker);
			return STATUS_ACTIVE;
		} else {
			VortexAgentMachine clone = mstate.getMachine().clone();
			if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, getState(), r))
				return STATUS_COMPLETE;
			clone.setMetadata(nuw.getMetadata());
			clone.setRevision(clone.getRevision() + 1);
			sendToDb(clone, worker);
			return STATUS_ACTIVE;
		}
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		DbResultMessage dbResult = (DbResultMessage) result.getAction();
		if (!dbResult.getOk()) {
			r.setMessage(dbResult.getMessage());
		} else {
			if (isDelete) {
				LH.info(log, "Deactivated Machine: ", nuw.getHostName() + " (" + nuw.getMachineUid() + ") ");
				VortexEyeMachineState mstate = getState().getMachineByMuidNoThrow(nuw.getMachineUid());
				getState().removeMachine(mstate);
			} else {
				VortexEyeMachineState mstate = getState().getMachineByMuidNoThrow(nuw.getMachineUid());
				mstate.getMachine().setMetadata(nuw.getMetadata());
			}
			r.setOk(true);
		}
		return STATUS_COMPLETE;
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setMachine(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			if (isDelete) {
				cmb.writeRemove(nuw);
			} else {
				cmb.writeUpdate(nuw, VortexAgentMachine.PID_REVISION, VortexAgentMachine.PID_METADATA);
			}
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
		}
		return r;
	}

	public void sendToDb(VortexAgentMachine agent, VortexEyeItineraryWorker worker) {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		boolean active = agent.getRevision() < VortexAgentEntity.REVISION_DONE;
		params.put("id", agent.getId());
		params.put("active", active);
		params.put("revision", agent.getRevision());
		params.put("hostname", agent.getHostName());
		params.put("now", agent.getNow());
		params.put("machine_uid", agent.getMachineUid());
		params.put("machine_start_time", agent.getSystemStartTime());
		params.put("os_version", agent.getOsVersion());
		params.put("os_name", agent.getOsName());
		params.put("os_architecture", agent.getOsArchitecture());
		params.put("cpu_count", agent.getCpuCount());
		params.put("metadata", agent.getMetadata() == null ? null : VortexEyeUtils.joinMap(agent.getMetadata()));

		DbRequestMessage msg = getTools().nw(DbRequestMessage.class);
		msg.setId("insert_machine_instance");
		msg.setParams(params);
		if (active == false) {
			LH.info(log, "Deactivating Machine: ", agent.getHostName() + " (" + agent.getMachineUid() + ") ");
			DbRequestMessage msg2 = getTools().nw(DbRequestMessage.class);
			final Map<Object, Object> params2 = new HashMap<Object, Object>();
			params2.put("machine_instance_id", agent.getId());
			msg2.setId("deactivate_machine");
			msg2.setParams(params2);
			msg.setNextRequest(msg2);
		}
		worker.sendToDb(this, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageMachineRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_MACHINE);
		auditEntity(sink, "MDID", action.getMachine());
	}

}
