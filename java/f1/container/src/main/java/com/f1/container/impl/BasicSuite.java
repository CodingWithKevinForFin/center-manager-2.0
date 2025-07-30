/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.base.Action;
import com.f1.container.Connectable;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.RequestProcessor;
import com.f1.container.ResultInputPort;
import com.f1.container.Suite;
import com.f1.container.exceptions.ContainerException;
import com.f1.utils.CH;

public class BasicSuite extends AbstractConnectable implements Suite {

	private List<Connectable> children = new ArrayList<Connectable>();

	@Override
	public <A extends Action> InputPort<A> exposeInputPort(Processor<A, ?> processor) {
		assertNotStarted();
		assertMember(processor);
		InputPort<A> r = processor.getInputPort();
		return exposeInputPort(r);
	}

	@Override
	public <A extends Action, D extends Action> RequestInputPort<A, D> exposeInputPort(RequestProcessor<A, ?, D> processor) {
		assertNotStarted();
		assertMember(processor);
		RequestInputPort<A, D> r = processor.getInputPort();
		return exposeInputPort(r);
	}

	@Override
	public <A extends Action> void exposeInputPortTo(Processor<? super A, ?> processor, InputPort<A> existing) {
		assertNotStarted();
		assertMember(processor);
		if (existing.getParentContainerScope() != this)
			throw new ContainerException("destination input plug not a member of this suite").setContainerScope(this).setSourcePort(existing);
		InputPort<? super A> r = processor.getInputPort();
		existing.pointTo(r);
	}

	@Override
	public <A extends Action, P extends InputPort<A>> P exposeInputPort(P inputPort) {
		assertNotStarted();
		assertMember(inputPort);
		P r = ContainerHelper.createPort(inputPort, this);
		r.pointTo(inputPort);
		addInputPort(r);
		return r;
	}

	@Override
	public <A extends Action, P extends OutputPort<A>> P exposeOutputPort(P existingOutputPort) {
		assertNotStarted();
		assertMember(existingOutputPort);
		P r = ContainerHelper.createPort(existingOutputPort, this);
		existingOutputPort.pointTo(r);
		addOutputPort(r);
		return r;
	}

	@Override
	public <A extends Action, P extends OutputPort<A>> P exposeOutputPortTo(OutputPort<A> existingOutputPort, P existing) {
		assertNotStarted();
		assertMember(existingOutputPort);
		if (existing.getParentContainerScope() != this)
			throw new ContainerException("destination output plug not a member of this suite").setContainerScope(this).setSourcePort(existing);
		existingOutputPort.pointTo(existing);
		return existing;
	}

	@Override
	public Collection<Connectable> getChildConnectables() {
		return children;
	}

	@Override
	public <A extends Action> void wire(OutputPort<A> output, InputPort<? super A> input, boolean isDispatch) {
		assertNotStarted();
		assertMember(output);
		assertMember(input);
		output.wire(input, isDispatch);
	}

	@Override
	public <A extends Action> void rewire(OutputPort<A> output, InputPort<? super A> input, boolean isDispatch) {
		assertNotStarted();
		assertMember(output);
		assertMember(input);
		output.rewire(input, isDispatch);
	}

	private void assertMember(Connectable c) {
		if (c.getParent() != this)
			throw new ContainerException("not a member of this suite").setContainerScope(this).setTargetContainerScope(c);
	}

	private void assertMember(Port p) {
		//		if (!(p.getParentContainerScope().getParent() == this || p.getParentContainerScope() == this))
		//			throw new ContainerException("port's parent not a member of this suite").setContainerScope(this).setSourcePort(p);
	}

	@Override
	public <R extends Connectable> R addChild(R child) {
		if (child.getParent() != null)
			throw new ContainerException("already a member of another suite").setContainerScope(this).setTargetContainerScope(child);
		if (isStarted()) {
			child.assertNotStarted();
			synchronized (this) {
				this.children = CH.copyAndAdd(children, addChildContainerScope(child));
			}
			child.start();
		} else
			children.add(addChildContainerScope(child));
		return child;
	}

	@Override
	public void addChildren(Connectable... p) {
		for (Connectable c : p)
			addChild(c);
	}

	@Override
	public <A extends Action> void wire(OutputPort<A> output, Processor<? super A, ?> input, boolean isDispatch) {
		wire(output, input.getInputPort(), isDispatch);
	}

	@Override
	public <A extends Action> void rewire(OutputPort<A> output, Processor<? super A, ?> input, boolean isDispatch) {
		rewire(output, input.getInputPort(), isDispatch);
	}

	@Override
	public <A extends Action> OutputPort<A> exposeInputPortAsOutput(Processor<A, ?> processor, boolean isDispatch) {
		if (processor instanceof RequestProcessor)
			return (OutputPort<A>) exposeInputPortAsOutput((RequestProcessor<?, ?, ?>) processor, isDispatch);
		return exposeInputPortAsOutput(processor.getInputPort(), isDispatch);
	}

	@Override
	public <A extends Action> OutputPort<A> exposeInputPortAsOutput(InputPort<A> inputPort, boolean isDispatch) {
		if (inputPort instanceof RequestInputPort)
			return (OutputPort<A>) exposeInputPortAsOutput((RequestInputPort<?, ?>) inputPort, isDispatch);
		assertNotStarted();
		assertMember(inputPort);
		OutputPort<A> r = new BasicOutputPort<A>(inputPort.getActionType(), this);
		r.wire(inputPort, isDispatch);
		addOutputPort(r);
		return r;
	}
	@Override
	public <A extends Action, D extends Action> RequestOutputPort<A, D> exposeInputPortAsOutput(RequestProcessor<A, ?, D> processor, boolean isDispatch) {
		return exposeInputPortAsOutput(processor.getInputPort(), isDispatch);
	}

	@Override
	public <A extends Action> void applyPartitionResolver(PartitionResolver<A> resolver, boolean overrideExisting, boolean recurse) {
		for (Connectable child : children) {
			if (child instanceof Processor) {
				Processor<?, ?> p = (Processor<?, ?>) child;
				if (!overrideExisting && p.getPartitionResolver() != null)
					continue;
				if (resolver.getActionType().isAssignableFrom(p.getActionType())) {
					((Processor<A, ?>) p).setPartitionResolver(resolver);
				}
			} else if (recurse && child instanceof Suite) {
				((Suite) child).applyPartitionResolver(resolver, overrideExisting, recurse);
			}
		}
	}

	public <REQ extends Action, RES extends Action> RequestOutputPort<REQ, RES> newRequestOutputPort(Class<REQ> requestActionType, Class<RES> responseActionType) {
		assertNotStarted();
		RequestOutputPort<REQ, RES> r = new BasicRequestOutputPort<REQ, RES>(requestActionType, responseActionType, this);
		addOutputPort(r);
		return r;
	}
	public <REQ extends Action, RES extends Action> RequestInputPort<REQ, RES> newRequestInputPort(Class<REQ> requestActionType, Class<RES> responseActionType) {
		assertNotStarted();
		RequestInputPort<REQ, RES> r = new BasicRequestInputPort<REQ, RES>(requestActionType, responseActionType, this);
		addInputPort(r);
		return r;
	}
	public <RES extends Action> ResultInputPort<RES> newResultInputPort(Class<RES> responseActionType) {
		assertNotStarted();
		ResultInputPort<RES> r = new BasicResultInputPort<RES>(responseActionType, this);
		addInputPort(r);
		return r;
	}
	public <T extends Action> InputPort<T> newInputPort(Class<T> actionType) {
		assertNotStarted();
		InputPort<T> r = new BasicInputPort<T>(actionType, this);
		addInputPort(r);
		return r;
	}

	@Override
	public <REQ extends Action, RES extends Action> RequestOutputPort<REQ, RES> exposeInputPortAsOutput(RequestInputPort<REQ, RES> port, boolean isDispatch) {
		assertNotStarted();
		assertMember(port);
		RequestOutputPort<REQ, RES> r = new BasicRequestOutputPort<REQ, RES>(port.getRequestActionType(), port.getResponseActionType(), this);
		r.wire(port, isDispatch);
		addOutputPort(r);
		return r;
	}

}
