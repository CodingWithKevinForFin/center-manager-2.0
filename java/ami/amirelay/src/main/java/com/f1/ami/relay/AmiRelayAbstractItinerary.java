package com.f1.ami.relay;

import java.util.Collection;

import com.f1.base.Message;
import com.f1.container.ContainerTools;
import com.f1.container.RequestMessage;
import com.f1.utils.concurrent.IdentityHashSet;

public abstract class AmiRelayAbstractItinerary<M extends Message> implements AmiRelayItinerary<M> {

	private RequestMessage<M> initialRequest;
	private long itineraryId = 0;
	private AmiRelayState amiRelayState;
	final IdentityHashSet<RequestMessage<?>> pendingRequests = new IdentityHashSet<RequestMessage<?>>();

	@Override
	public void init(long itineraryId, RequestMessage<M> requestMessage, AmiRelayState amiRelayState) {
		if (this.itineraryId != 0)
			throw new IllegalStateException("init already called");
		if (itineraryId == 0)
			throw new IllegalArgumentException("intineraryId is zero");
		this.itineraryId = itineraryId;
		this.initialRequest = requestMessage;
		this.amiRelayState = amiRelayState;
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
	public AmiRelayState getState() {
		return amiRelayState;
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

	public <T> T nw(Class<T> clazz) {
		return getState().nw(clazz);
	}

	@Override
	public byte continueItinerary() {
		return STATUS_ERROR;
	}

}
