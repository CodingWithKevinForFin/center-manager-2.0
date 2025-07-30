package com.f1.ami.center;

import java.util.Collection;

import com.f1.base.Message;
import com.f1.container.ContainerTools;
import com.f1.container.RequestMessage;
import com.f1.utils.concurrent.IdentityHashSet;

public abstract class AbstractAmiCenterItinerary<M extends Message> implements AmiCenterItinerary<M> {

	private RequestMessage<M> initialRequest;
	private AmiCenterState amiCenterState;
	final IdentityHashSet<RequestMessage<?>> pendingRequests = new IdentityHashSet<RequestMessage<?>>();
	private boolean isReadonly;

	@Override
	final public void init(RequestMessage<M> requestMessage, AmiCenterState amiCenterState, boolean isReadonly) {
		this.isReadonly = isReadonly;
		this.initialRequest = requestMessage;
		this.amiCenterState = amiCenterState;
		onInit();
	}

	final protected void onInit() {
	}

	@Override
	public RequestMessage<M> getInitialRequest() {
		return initialRequest;
	}

	public boolean isReadonly() {
		return this.isReadonly;
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
	public AmiCenterState getState() {
		return amiCenterState;
	}

	@Override
	public Collection<RequestMessage<?>> getPendingRequests() {
		return pendingRequests;
	}

	protected ContainerTools getTools() {
		return getState().getPartition().getContainer().getTools();
	}

	@Override
	public int getTimeoutMs() {
		return getState().getDefaultDatasourceTimeout();
	}
}
