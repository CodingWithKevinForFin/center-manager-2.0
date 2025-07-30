/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.InputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.Port;
import com.f1.container.ProcessActionListener;
import com.f1.container.Processor;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.ThrowableHandler;
import com.f1.utils.AH;

public abstract class BasicProcessor<A extends Action, S extends State> extends AbstractConnectable implements Processor<A, S> {

	private PartitionResolver<? super A> partitionResolver;
	protected InputPort<A> inputPort;
	private Class<S> stateType;
	private Class<A> actionType;

	@Override
	public void setPartitionResolver(PartitionResolver<? super A> resolver) {
		assertNotStarted();
		if (getChildContainerScopes().contains(this.partitionResolver))
			removeChildContainerScope(this.partitionResolver);
		this.partitionResolver = resolver;
		if (resolver != null && resolver.getParentContainerScope() == null && resolver != this)
			addChildContainerScope(resolver);
	}

	public BasicProcessor(Class<A> actionType, Class<S> stateType) {
		this(actionType, stateType, null);
	}

	public BasicProcessor(Class<A> actionType, Class<S> stateType, PartitionResolver<? super A> resolver) {
		this.stateType = stateType;
		this.actionType = actionType;
		inputPort = createInputPort();
		if (resolver != null)
			setPartitionResolver(resolver);
		else
			setPartitionResolver(new BasicPartitionResolver<A>(actionType, null));

	}

	protected void reply(RequestMessage<?> request, ResultMessage<?> result, ThreadScope threadScope) {
		getContainer().getDispatchController().reply(this.getInputPort(), request, result, threadScope);
	}

	protected InputPort<A> createInputPort() {
		InputPort<A> r;
		addInputPort(r = new BasicInputPort<A>(getActionType(), this));
		return r;
	}

	public Port<A> newOutputPort() {
		return newOutputPort(getActionType());
	}

	@Override
	public PartitionResolver<? super A> getPartitionResolver() {
		return partitionResolver;
	}

	@Override
	public abstract void processAction(A action, S state, ThreadScope threadScope) throws Exception;

	@Override
	public InputPort<A> getInputPort() {
		return inputPort;
	}

	private ThrowableHandler<? super A, ? super S> throwableHandler = null;

	@Override
	public void handleThrowable(A action, S state, ThreadScope thread, Throwable thrown) throws Throwable {
		if (throwableHandler == null)
			getContainer().getServices().getDefaultThrowableHandler().handleThrowable(this, action, state, thread, thrown);
		else
			throwableHandler.handleThrowable(this, action, state, thread, thrown);
	}

	public void setThrowableHandler(ThrowableHandler<? super A, ? super S> throwableHandler) {
		assertNotStarted();
		this.throwableHandler = throwableHandler;
	}

	public ThrowableHandler<? super A, ? super S> getThrowableHandler() {
		return throwableHandler;
	}

	@Override
	public Class<S> getStateType() {
		return stateType;
	}

	@Override
	public Class<A> getActionType() {
		return actionType;
	}

	@Override
	public void bindToPartition(Object partitionId) {
		setPartitionResolver(new BasicPartitionResolver<A>(getActionType(), partitionId));
	}

	public void commit(State state) {
		getContainer().getPersistenceController().commitState(state);
	}

	private ProcessActionListener[] processListeners = ContainerHelper.EMPTY_PROCESS_EVENT_LISTENER_ARRAY;

	@Override
	public void addProcessActionListener(ProcessActionListener listener) {
		processListeners = AH.insert(processListeners, processListeners.length, listener);
	}

	@Override
	public ProcessActionListener[] getProcessActionListeners() {
		return processListeners;
	}
}
