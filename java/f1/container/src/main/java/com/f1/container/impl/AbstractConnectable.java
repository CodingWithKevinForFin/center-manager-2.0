/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.f1.base.Action;
import com.f1.container.Connectable;
import com.f1.container.ContainerScope;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultOutputPort;
import com.f1.container.Suite;
import com.f1.container.exceptions.ContainerException;

public abstract class AbstractConnectable extends AbstractContainerScope implements Connectable {

	private Suite parentSuite;
	private List<InputPort<?>> inputs = new ArrayList<InputPort<?>>();
	private List<OutputPort<?>> outputs = new ArrayList<OutputPort<?>>();

	@Override
	public Collection<InputPort<?>> getInputs() {
		return Collections.unmodifiableCollection(inputs);
	}

	@Override
	public Collection<OutputPort<?>> getOutputs() {
		return Collections.unmodifiableCollection(outputs);
	}

	protected void addInputPort(InputPort<?> input) {
		if (!input.isInput())
			throw new ContainerException(this, "adding output port as input port").setSourcePort(input);
		this.inputs.add(addChildContainerScope(input));
	}

	protected void clearInputPorts() {
		this.inputs.clear();
	}

	protected void addOutputPort(OutputPort<?> output) {
		if (output.isInput())
			throw new ContainerException(this, "adding input port as output port").setSourcePort(output);
		this.outputs.add(addChildContainerScope(output));
	}

	@Override
	public Suite getParent() {
		return parentSuite;
	}

	@Override
	public void setParent(Suite parentSuite) {
		setParentContainerScope(parentSuite);
	}

	@Override
	public void setParentContainerScope(ContainerScope cs) {
		if (cs instanceof Suite)
			this.parentSuite = (Suite) cs;
		super.setParentContainerScope(cs);
	}

	@Override
	public void start() {
		super.start();
		ContainerHelper.assertNameValid(getName());
	}

	public <T extends Action> OutputPort<T> newOutputPort(Class<T> actionType) {
		assertNotStarted();
		OutputPort<T> r = new BasicOutputPort<T>(actionType, this);
		addOutputPort(r);
		return r;
	}

	public <T extends Action, RES extends Action> RequestOutputPort<T, RES> newRequestOutputPort(Class<T> actionType, Class<RES> responseType) {
		assertNotStarted();
		BasicRequestOutputPort<T, RES> r = new BasicRequestOutputPort<T, RES>(actionType, responseType, this);
		addOutputPort(r);
		return r;
	}
	public <RES extends Action> ResultOutputPort<RES> newResultOutputPort(Class<RES> responseType) {
		assertNotStarted();
		BasicResultOutputPort<RES> r = new BasicResultOutputPort<RES>(responseType, this);
		addOutputPort(r);
		return r;
	}

}
