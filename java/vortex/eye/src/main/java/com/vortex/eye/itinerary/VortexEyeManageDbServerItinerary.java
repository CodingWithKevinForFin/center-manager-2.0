package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.OH;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDbServerRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDbServerResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDbInspectionRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDbInspectionResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageDbServerItinerary extends AbstractVortexEyeItinerary<VortexEyeManageDbServerRequest> {

	private VortexAgentDbServer old, nuw;
	private VortexEyeManageDbServerResponse r;
	private boolean isDelete;
	private int step;
	private static final int STEP1_UPDATE_VORTEX_EYE_DB = 1;
	private static final int STEP2_INSPECT_DB = 2;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageDbServerResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getDbServer();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (nuw.getId() != 0) {
			old = state.getDbServer(nuw.getId());
			if (old == null) {
				r.setMessage("BuildProcedure not found for update / delete: " + nuw.getId());
				return STATUS_COMPLETE;
			}
			if (isDelete) {
				nuw = old.clone();
				nuw.setRevision(VortexEyeUtils.REVISION_DONE);
			} else {
				nuw.setRevision(old.getRevision() + 1);
				if (!validateUnique(nuw, getState(), r))
					return STATUS_COMPLETE;
				if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
					return STATUS_COMPLETE;
			}
			if (OH.ne(old.getMachineUid(), nuw.getMachineUid()) || OH.ne(old.getUrl(), nuw.getUrl()) || OH.ne(old.getPassword(), nuw.getPassword()))
				nuw.setStatus(VortexAgentDbServer.STATUS_MODIFIED);
			else
				nuw.setStatus(old.getStatus());
		} else {
			if (!validateUnique(nuw, getState(), r))
				return STATUS_COMPLETE;
			if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
				return STATUS_COMPLETE;
			nuw.setId(getState().createNextId());
			nuw.setStatus(VortexAgentDbServer.STATUS_CREATED);
			nuw.setRevision(0);
			nuw.setDatabases(new HashMap<String, VortexAgentDbDatabase>());
		}
		nuw.setNow(now);
		nuw.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
		//nuw.setMessage(isDelete ? "Deleted" : (old == null ? "Created" : "Modified"));
		nuw.setDatabases(old == null ? new HashMap<String, VortexAgentDbDatabase>() : old.getDatabases());
		nuw.lock();
		sendToDb(nuw, worker);
		step = STEP1_UPDATE_VORTEX_EYE_DB;
		return STATUS_ACTIVE;
	}
	private boolean validateUnique(VortexAgentDbServer dbs, VortexEyeState state, VortexEyeManageDbServerResponse r2) {
		for (VortexAgentDbServer existing : state.getDbServers()) {
			if (existing.getId() == dbs.getId())
				continue;
			//if (OH.eq(existing.getUrl(), dbs.getUrl()) && OH.eq(existing.getMachineUid(), dbs.getMachineUid())) {
			//r.setMessage("Could not process db server request, duplicate URL: " + existing.getUrl());
			//return false;
			//}
			if (OH.eq(existing.getDescription(), dbs.getDescription())) {
				r.setMessage("Could not process db server request, duplicate description: " + existing.getDescription());
				return false;
			}
		}
		return true;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		switch (step) {
			case STEP1_UPDATE_VORTEX_EYE_DB: {
				DbResultMessage dbResult = (DbResultMessage) result.getAction();
				if (!dbResult.getOk()) {
					r.setOk(false);
					r.setMessage(dbResult.getMessage());
					return STATUS_COMPLETE;
				} else {
					VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
					cmb.writeTransition(old, nuw);
					worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
					r.setOk(true);
					if (isDelete) {
						getState().removeDbServer(nuw.getId());
						return STATUS_COMPLETE;
					} else {
						getState().addDbServer(nuw);
						//is this a new dbserver, or did something changed requiring that we re-inspect the db?
						if (old == null || OH.ne(old.getUrl(), nuw.getUrl()) || OH.ne(old.getPassword(), nuw.getPassword()) || OH.ne(old.getMachineUid(), nuw.getMachineUid())
								|| OH.ne(old.getDbType(), nuw.getDbType())) {
							VortexEyeRunDbInspectionRequest d = getState().nw(VortexEyeRunDbInspectionRequest.class);
							d.setDbServerId(nuw.getId());
							step = STEP2_INSPECT_DB;
							worker.startItinerary(this, new VortexEyeInspectDbSchemaItinerary(), d);
							return STATUS_ACTIVE;
						} else {

							r.setOk(true);
							return STATUS_COMPLETE;
						}
					}
				}
			}
			case STEP2_INSPECT_DB: {
				VortexEyeRunDbInspectionResponse inspectResult = (VortexEyeRunDbInspectionResponse) result.getAction();
				if (!inspectResult.getOk()) {
					r.setOk(false);
					r.setMessage("DB Config saved but error inspecting database: " + inspectResult.getMessage());
				}
				return STATUS_COMPLETE;
			}
			default:
				throw new RuntimeException("unknown step: " + step);
		}
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setDbServer(nuw);
			r.setOk(true);
		}
		return r;
	}

	public void sendToDb(VortexAgentDbServer db, VortexEyeItineraryWorker worker) {
		boolean active = db.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("description", db.getDescription());
		params.put("id", db.getId());
		params.put("revision", db.getRevision());
		params.put("machine_uid", db.getMachineUid());
		params.put("now", db.getNow());
		params.put("url", db.getUrl());
		params.put("db_type", db.getDbType());
		params.put("psw", db.getPassword());
		params.put("status", db.getStatus());
		params.put("message", db.getMessage());
		params.put("inspected_time", db.getInspectedTime());
		params.put("hints", db.getHints());
		params.put("metadata", db.getMetadata() == null ? null : VortexEyeUtils.joinMap(db.getMetadata()));
		params.put("server_port", db.getServerPort());
		DbRequestMessage msg = getTools().nw(DbRequestMessage.class);
		msg.setId("insert_db_server");
		msg.setParams(params);
		if (active == false) {
			DbRequestMessage msg2 = getTools().nw(DbRequestMessage.class);
			final Map<Object, Object> params2 = new HashMap<Object, Object>();
			params2.put("db_server_id", db.getId());
			msg2.setId("deactivate_dbserver");
			msg2.setParams(params2);
			msg.setNextRequest(msg2);
		}
		worker.sendToDb(this, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageDbServerRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_DEPLOYMENT);
		auditEntity(sink, "DPID", action.getDbServer());

	}

}
