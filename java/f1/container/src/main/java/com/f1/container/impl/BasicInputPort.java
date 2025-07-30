/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.Connectable;
import com.f1.container.ContainerConstants;
import com.f1.container.ContainerScope;
import com.f1.container.InputPort;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ContainerException;
import com.f1.utils.OH;

public class BasicInputPort<A extends Action> extends AbstractContainerScope implements InputPort<A> {

	final private Class<A> actionType;
	final private Connectable parent;
	private InputPort<? super A> destination;
	private Processor<? super A, ?> processor;

	// input of a processor
	public BasicInputPort(Class<A> actionType, Processor<? super A, ?> processor) {
		OH.assertNotNull(actionType);
		this.actionType = actionType;
		this.parent = processor;
		super.setParentContainerScope(parent);
		this.destination = null;
		this.processor = processor;
		initName();
	}

	protected void initName() {
		setName(actionType.getSimpleName() + "InputPort");
	}

	public BasicInputPort(Class<A> actionType, Connectable parent) {
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
		return true;
	}

	@Override
	public Processor<? super A, ?> getProcessor() {
		if (processor == null && destination != null)
			processor = destination.getProcessor();
		return processor;
	}

	@Override
	public boolean isConnected() {
		return getProcessor() != null;
	}

	private boolean isConnectionOptional = false;

	@Override
	public String toString() {
		return getClass().getSimpleName() + (isConnected() ? " to " + getProcessor().getFullName() : " disconnected");
	}

	@Override
	public String getFullName() {
		return getParentContainerScope().getFullName() + ContainerConstants.NAME_PORT_SEPERATOR + getName();
	}

	@Override
	public BasicInputPort<A> setName(String name) {
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
	public void pointTo(InputPort<? super A> port) {
		assertNotStarted();
		if (this.destination != null || isConnected())
			throw new ContainerException("already connected: " + this);
		this.destination = port;
	}

	@Override
	public void repointTo(InputPort<? super A> port) {
		assertNotStarted();
		this.processor = null;
		this.destination = port;
	}

	@Override
	public void dispatch(A action) {
		this.getContainer().getDispatchController().dispatch(null, getProcessor(), action, null, null);
	}

	@Override
	public void dispatch(A action, Object partitionId) {
		this.getContainer().getDispatchController().dispatch(null, getProcessor(), action, null, null);
	}

	@Override
	public void forward(A action, ThreadScope ts) {
		this.getContainer().getDispatchController().forward(null, getProcessor(), action, ts);
	}
}
