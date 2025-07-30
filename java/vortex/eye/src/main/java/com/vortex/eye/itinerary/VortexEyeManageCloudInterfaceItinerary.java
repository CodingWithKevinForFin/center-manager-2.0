package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.utils.OH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageCloudInterfaceRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageCloudInterfaceResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.cloud.CloudRunnable;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageCloudInterfaceItinerary extends AbstractVortexEyeItinerary<VortexEyeManageCloudInterfaceRequest> {

	private static final byte STEP_TEST_ON_CLOUD = 1;
	private static final byte STEP_INSERT_TO_DB = 2;

	private VortexEyeCloudInterface old, nuw;
	private VortexEyeManageCloudInterfaceResponse r;
	private boolean isDelete;
	private byte step;
	private CloudRunnable cloudRunner;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageCloudInterfaceResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getCloudInterface();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (!getInitialRequest().getAction().getOnlyTest()) {
			if (nuw.getId() != 0) {
				old = state.getCloudInterface(nuw.getId());
				if (old == null) {
					r.setMessage("Deployment not found for update / delete: " + nuw.getId());
					return STATUS_COMPLETE;
				}
				if (isDelete) {
					nuw = old.clone();
					nuw.setRevision(VortexAgentEntity.REVISION_DONE);
				} else {
					//if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
					//return STATUS_COMPLETE;
					if (!validateUnique(nuw, state.getCloudInterfaces(), r))
						return STATUS_COMPLETE;
					nuw.setRevision(old.getRevision() + 1);
				}
			} else {
				//if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
				//return STATUS_COMPLETE;
				nuw.setId(getState().createNextId());
				nuw.setRevision(0);
				if (!validateUnique(nuw, state.getCloudInterfaces(), r))
					return STATUS_COMPLETE;
			}
			nuw.setNow(now);
			nuw.lock();
		}
		if (isDelete) {
			sendToDb(nuw, worker, this);
			this.step = STEP_INSERT_TO_DB;
		} else {
			this.step = STEP_TEST_ON_CLOUD;
			this.cloudRunner = new CloudRunnable(VortexEyeRunCloudInterfaceActionItinerary.getCloudAdapter(nuw.getCloudVendorType()), nuw,
					CloudRunnable.ACTION_GET_MACHINES_IN_CLOUD);
			RunnableRequestMessage rm = getState().nw(RunnableRequestMessage.class);
			rm.setPartitionId("CI-" + nuw.getId());
			rm.setRunnable(this.cloudRunner);
			rm.setTimeoutMs(30000);
			worker.sendRunnable(this, rm);
		}
		return STATUS_ACTIVE;
	}

	public static boolean validateUnique(VortexEyeCloudInterface nuw, Iterable<VortexEyeCloudInterface> existings, VortexEyeResponse r) {
		for (VortexEyeCloudInterface existing : existings) {
			if (existing.getId() == nuw.getId())
				continue;
			if (OH.eq(existing.getDescription(), nuw.getDescription())) {
				r.setMessage("Duplicate cloud interface description: " + existing.getDescription() + "::" + existing.getDescription());
				return false;
			}
		}
		return true;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		switch (step) {
			case STEP_TEST_ON_CLOUD: {
				if (getInitialRequest().getAction().getOnlyTest()) {
					r.setOk(false);
					if (this.cloudRunner.getSuccess()) {
						r.setMessage("Success!\n\nFound " + ((List<String>) cloudRunner.getResults()).size() + " machine(s) for this interface");
					} else {
						r.setMessage("Failed:\n" + cloudRunner.getMessage());
					}
					return STATUS_COMPLETE;
				}
				if (this.cloudRunner.getSuccess()) {
					sendToDb(nuw, worker, this);
					this.step = STEP_INSERT_TO_DB;
					return STATUS_ACTIVE;
				} else {
					r.setOk(false);
					r.setMessage("Could not verify cloud configuration:\n" + cloudRunner.getMessage());
					return STATUS_COMPLETE;
				}
			}
			case STEP_INSERT_TO_DB: {
				DbResultMessage dbResult = (DbResultMessage) result.getAction();
				if (!dbResult.getOk()) {
					r.setMessage(dbResult.getMessage());
				} else {
					if (isDelete)
						getState().removeCloudInterface(nuw.getId());
					else
						getState().addCloudInterface(nuw);
					r.setOk(true);
				}
				return STATUS_COMPLETE;
			}
			default:
				throw new RuntimeException("unknown step: " + this.step);
		}
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setCloudInterface(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(old, nuw);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
		}
		return r;
	}

	public static void sendToDb(VortexEyeCloudInterface nw, VortexEyeItineraryWorker worker, VortexEyeItinerary<?> source) {
		boolean active = nw.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", nw.getId());
		params.put("revision", nw.getRevision());
		params.put("now", nw.getNow());
		params.put("description", nw.getDescription());
		params.put("user_name", nw.getUserName());
		params.put("password", nw.getPassword());
		params.put("key_contents", nw.getKeyContents());
		params.put("key_type", nw.getKeyType());
		params.put("cloud_vendor_type", nw.getCloudVendorType());
		params.put("parameters", nw.getParameters() == null ? null : VortexEyeUtils.joinMap(nw.getParameters()));
		//params.put("metadata", nw.getMetadata() == null ? null : SH.joinMap('|', '=', nw.getMetadata()));
		DbRequestMessage msg = source.getState().nw(DbRequestMessage.class);
		msg.setId("insert_cloud_interface");
		msg.setParams(params);
		worker.sendToDb(source, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageCloudInterfaceRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_CLOUD_INTERFACE);
		auditEntity(sink, "CIID", action.getCloudInterface());
	}

}
