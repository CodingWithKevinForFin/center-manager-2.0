package com.vortex.eye.itinerary;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.container.ContainerTools;
import com.f1.container.RequestMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public abstract class AbstractVortexEyeItinerary<M extends VortexEyeRequest> implements VortexEyeItinerary<M> {

	private RequestMessage<M> initialRequest;
	private long itineraryId = 0;
	private VortexEyeState vortexEyeState;
	final IdentityHashSet<RequestMessage<?>> pendingRequests = new IdentityHashSet<RequestMessage<?>>();

	@Override
	final public void init(long itineraryId, RequestMessage<M> requestMessage, VortexEyeState vortexEyeState) {
		if (this.itineraryId != 0)
			throw new IllegalStateException("init already called");
		if (itineraryId == 0)
			throw new IllegalArgumentException("intineraryId is zero");
		this.itineraryId = itineraryId;
		this.initialRequest = requestMessage;
		this.vortexEyeState = vortexEyeState;
	}

	@Override
	public RequestMessage<M> getInitialRequest() {
		return initialRequest;
	}

	@Override
	public long getItineraryId() {
		return itineraryId;
	}

	@Override
	public void removePendingRequest(RequestMessage<?> requestMessage) {
		if (!pendingRequests.remove(requestMessage))
			throw new RuntimeException("not registered: " + requestMessage);
	}
	@Override
	public void addPendingRequest(RequestMessage<?> requestMessage) {
		if (!pendingRequests.add(requestMessage))
			throw new RuntimeException("already registered: " + requestMessage);
	}

	@Override
	public VortexEyeState getState() {
		return vortexEyeState;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + itineraryId + ")";
	}

	@Override
	public Collection<RequestMessage<?>> getPendingRequests() {
		return pendingRequests;
	}

	protected ContainerTools getTools() {
		return getState().getPartition().getContainer().getTools();
	}

	@Override
	public final void auditClientEvent(VortexEyeItineraryProcessor worker) {
		M action = getInitialRequest().getAction();
		VortexEyeClientEvent event = getState().nw(VortexEyeClientEvent.class);
		event.setId(getState().createNextId());
		event.setInvokedBy(action.getInvokedBy());
		event.setComment(action.getComment());
		event.setParams(new HashMap<String, String>());
		event.setNow(getTools().getNow());
		populateAuditEvent(action, worker, event);
		if (event.getEventType() == 0)
			throw new RuntimeException("Must populate type");
		if (event.getParams().isEmpty())
			event.setParams(null);
		getState().addClientEvent(event);
		sendAuditToDb(event, worker);
		VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
		cmb.writeAdd(event);
		worker.sendToClients(cmb.popToChangesMsg(getState().nextSequenceNumber()));

	}

	abstract protected void populateAuditEvent(M action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink);

	private void sendAuditToDb(VortexEyeClientEvent event, VortexEyeItineraryProcessor worker) {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("id", event.getId());
		params.put("now", event.getNow());
		params.put("event_type", event.getEventType());
		params.put("invoked_by", event.getInvokedBy());
		params.put("comment", event.getComment());
		params.put("message", event.getMessage());
		params.put("target_machine_uid", event.getTargetMachineUid());
		params.put("params", event.getParams() == null ? null : VortexEyeUtils.joinMap(event.getParams()));
		DbRequestMessage msg = getState().nw(DbRequestMessage.class);
		msg.setId("insert_client_event");
		msg.setParams(params);
		worker.sendToDb(msg);
	}

	public static void auditMap(VortexEyeClientEvent sink, String key, Map<String, String> vars) {
		if (CH.isEmpty(vars))
			return;
		final StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (Entry<String, String> e : vars.entrySet()) {
			SH.quote(e.getKey(), sb);
			sb.append(':');
			SH.quote(e.getValue(), sb);
		}
		sb.append('}');
		sink.getParams().put(key, sb.toString());
	}
	protected static void auditList(VortexEyeClientEvent sink, String key, long[] ids) {
		if (ids == null)
			return;
		if (ids.length == 0)
			sink.getParams().put(key, "[]");
		else if (ids.length == 1)
			sink.getParams().put(key, "[" + ids[0] + "]");
		else
			sink.getParams().put(key, SH.join(',', ids, new StringBuilder('[')).append(']').toString());
	}
	protected static void auditList(VortexEyeClientEvent sink, String key, int[] ids) {
		if (ids == null)
			return;
		if (ids.length == 0)
			sink.getParams().put(key, "[]");
		else if (ids.length == 1)
			sink.getParams().put(key, "[" + ids[0] + "]");
		else
			sink.getParams().put(key, SH.join(',', ids, new StringBuilder('[')).append(']').toString());
	}
	protected static void auditList(VortexEyeClientEvent sink, String key, List<?> ids) {
		if (ids == null)
			return;
		if (ids.size() == 0)
			sink.getParams().put(key, "[]");
		else
			sink.getParams().put(key, SH.join(',', ids, new StringBuilder("[")).append(']').toString());
	}
	protected static boolean auditEntity(VortexEyeClientEvent sink, String idkey, VortexEntity entity) {
		if (entity == null)
			return false;
		if (entity.getId() == 0)
			sink.getParams().put("ACTN", "ADD");
		else if (entity.getRevision() == VortexEyeEntity.REVISION_DONE) {
			sink.getParams().put("ACTN", "DEL");
			sink.getParams().put(idkey, SH.toString(entity.getId()));
		} else {
			sink.getParams().put("ACTN", "UPD");
			sink.getParams().put(idkey, SH.toString(entity.getId()));
		}
		return true;
	}
}
