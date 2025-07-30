/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.concurrent.TimeUnit;

import com.f1.base.Action;
import com.f1.container.Connectable;
import com.f1.container.ContainerConstants;
import com.f1.container.ContainerScope;
import com.f1.container.DispatchController;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ContainerException;
import com.f1.utils.OH;

public class BasicOutputPort<A extends Action> extends AbstractContainerScope implements OutputPort<A> {

	final private Class<A> actionType;
	final private Connectable parent;
	private Port<? super A> destination;
	private Processor<? super A, ?> processor;
	private final byte NOTSET = 0;
	private final byte DISPATCH = 1;
	private final byte FORWARD = 2;

	private byte isDispatch = NOTSET;

	protected void initName() {
		setName(actionType.getSimpleName() + "OutputPort");
	}

	public BasicOutputPort(Class<A> actionType, Connectable parent) {
		OH.assertNotNull(actionType);
		OH.assertNotNull(parent);
		this.actionType = actionType;
		this.parent = parent;
		super.setParentContainerScope(parent);
		this.destination = null;
		initName();
	}

	@Override
	public void setParentContainerScope(ContainerScope parent) {
		if (parent != this.parent)
			throw new ContainerException(this, "not correct parent").setContainerScope(this).setTargetContainerScope(parent);
		super.setParentContainerScope(parent);
	}

	@Override
	public Connectable getParentContainerScope() {
		return parent;
	}

	@Override
	public boolean isInput() {
		return false;
	}

	@Override
	public Processor<? super A, ?> getProcessor() {
		if (processor == null && destination != null)
			processor = destination.getProcessor();
		return processor;
	}

	@Override
	public void wire(InputPort<? super A> inputPort, boolean isDispatch) {
		assertNotStarted();
		if (this.destination != null)
			throw new ContainerException(this, "already connected").set("existing port", destination).set("attempted to wire port", inputPort);
		this.destination = inputPort;
		this.isDispatch = isDispatch ? DISPATCH : FORWARD;
		this.processor = inputPort.getProcessor();
	}

	@Override
	public void rewire(InputPort<? super A> inputPort, boolean isDispatch) {
		assertNotStarted();
		this.destination = null;
		this.hasConnected = false;
		this.processor = null;
		this.isDispatch = NOTSET;
		wire(inputPort, isDispatch);
	}

	@Override
	public boolean isDispatch() {
		respolveDispatchMode();
		return isDispatch == DISPATCH;
	}

	private void respolveDispatchMode() {
		if (isDispatch == NOTSET && destination instanceof OutputPort) {
			OutputPort<?> o = (OutputPort<?>) destination;
			if (o.isDispatch())
				isDispatch = DISPATCH;
			else if (o.isForward())
				isDispatch = FORWARD;
		}
	}

	@Override
	public boolean isForward() {
		respolveDispatchMode();
		return isDispatch == FORWARD;
	}

	private boolean hasConnected = false;
	private DispatchController dispatcher = null;

	@Override
	public void send(Action a, ThreadScope threadScope) {
		if (!prepareForSend()) {
			if (!isConnectionOptional())
				throw new ContainerException("could not send action on unwired port").setAction(a).set("port", this);
		} else if (isDispatch == DISPATCH)
			dispatcher.dispatch(this, processor, a, null, threadScope);
		else
			dispatcher.forward(this, processor, a, threadScope);
	}

	@Override
	public void sendDelayed(Action a, ThreadScope threadScope, long delay, TimeUnit timeUnit) {
		sendDelayed(a, null, threadScope, delay, timeUnit);
	}

	@Override
	public void sendDelayed(Action a, Object partitionId, ThreadScope threadScope, long delay, TimeUnit timeUnit) {
		if (!prepareForSend()) {
			if (isConnectionOptional())
				return;
			throw new ContainerException("could not send action on unwired port").setAction(a).set("port", this);
		}
		if (!isDispatch())
			throw new ContainerException(this, "delay sends only on ports wired for dispatch").setAction(a);
		dispatcher.dispatch(this, processor, a, partitionId, timeUnit.toMillis(delay), threadScope);
	}

	@Override
	public void send(Action a, Object partitionId, ThreadScope threadScope) {
		if (!prepareForSend()) {
			if (isConnectionOptional())
				return;
			throw new ContainerException("could not send action on unwired port").setAction(a).set("port", this);
		} else if (isDispatch == DISPATCH)
			dispatcher.dispatch(this, processor, a, partitionId, threadScope);
		else
			dispatcher.forward(this, processor, a, threadScope);

	}

	private boolean prepareForSend() {
		if (hasConnected)
			return true;
		respolveDispatchMode();
		assertInit();
		dispatcher = getContainer().getDispatchController();
		if (dispatcher != null && isDispatch != NOTSET && getProcessor() != null) {
			hasConnected = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean isConnected() {
		return hasConnected || getProcessor() != null;
	}

	private boolean isConnectionOptional = false;

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + (isDispatch() ? "dispatch" : "forward") + (isConnected() ? (" to " + getProcessor().getFullName()) : " disconnected");
	}

	@Override
	public String getFullName() {
		return getParentContainerScope().getFullName() + ContainerConstants.NAME_PORT_SEPERATOR + getName();
	}

	@Override
	public BasicOutputPort<A> setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public Class<A> getActionType() {
		return actionType;
	}

	@Override
	public boolean isConnectionOptional() {
		return isConnectionOptional;
	}

	@Override
	public Port<A> setConnectionOptional(boolean connectionOptional) {
		assertNotStarted();
		this.isConnectionOptional = connectionOptional;
		return this;
	}

	@Override
	public void pointTo(OutputPort<? super A> port) {
		assertNotStarted();
		if (destination != null)
			throw new ContainerException("already connected: " + this);
		this.destination = port;
	}

	@Override
	public void repointTo(OutputPort<? super A> port) {
		assertNotStarted();
		this.destination = port;
		this.processor = null;
		this.isDispatch = NOTSET;
		this.hasConnected = false;
	}

	@Override
	public void startDispatching() {
		prepareForSend();
		super.startDispatching();
	}

}
