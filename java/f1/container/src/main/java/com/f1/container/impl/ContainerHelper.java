/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.Connectable;
import com.f1.container.Container;
import com.f1.container.ContainerConstants;
import com.f1.container.ContainerScope;
import com.f1.container.DispatchController;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.Partition;
import com.f1.container.Port;
import com.f1.container.ProcessActionListener;
import com.f1.container.Processor;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.Suite;
import com.f1.container.exceptions.ContainerException;
import com.f1.container.wrapper.DispatchControllerWrapper;
import com.f1.utils.IndentedStringBuildable;
import com.f1.utils.LH;
import com.f1.utils.Labeler;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.BasicLabeler;

public class ContainerHelper {
	private static final Logger log = Logger.getLogger(ContainerHelper.class.getName());
	public static final ProcessActionListener[] EMPTY_PROCESS_EVENT_LISTENER_ARRAY = new ProcessActionListener[0];
	private static AtomicLong uid = new AtomicLong(0);

	public static void assertNameValid(String name) {
		if (name == null)
			throw new ContainerException("name required");
		if (name.indexOf(ContainerConstants.NAME_PORT_SEPERATOR) != -1 || name.indexOf(ContainerConstants.NAME_SEPERATOR) != -1)
			throw new ContainerException("invalid name: '" + name + "'");
	}

	static public void describe(Object o, StringBuilder sb) {
		if (o instanceof ContainerScope) {
			sb.append('(').append(o.getClass().getSimpleName()).append(')').append(((ContainerScope) o).getFullName());
		} else
			SH.s(o, sb);
	}

	static public void describeCs(ContainerScope container, IndentedStringBuildable sb) {
		sb.append(container.getFullName()).append("    (").append(container.getClass().getSimpleName()).append(')').appendNewLine();
		sb.indent();
		for (ContainerScope cs : container.getChildContainerScopes())
			describeCs(cs, sb);
		sb.outdent();
	}

	public static String describe(ContainerScope cs) {
		return cs.getClass().getName();
	}

	public static void throwException(Object... message) {
		StringBuilder sb = new StringBuilder();
		Throwable cause = null;
		for (Object o : message) {
			if (o instanceof Throwable && cause == null)
				cause = (Throwable) o;
			else
				describe(o, sb);
		}
		throw new ContainerException(sb.toString(), cause);

	}

	public static List<ContainerScope> getAllChildren(ContainerScope c) {
		List<ContainerScope> r = new ArrayList<ContainerScope>();
		getAllChildren(c, r);
		return r;
	}

	private static void getAllChildren(ContainerScope c, Collection<ContainerScope> sink) {
		sink.add(c);
		for (ContainerScope cs : c.getChildContainerScopes())
			getAllChildren(cs, sink);
	}

	public StringBuilder describeException(ContainerException e, StringBuilder sb) {
		Action action = e.getAction();
		Throwable cause = e.getCause();
		String message = e.getMessage();

		Partition sourcePartition = e.getSourcePartition();
		Object sourcePartitionId = e.getSourcePartitionId();
		Processor sourceProcessor = e.getSourceProcessor();
		State sourceState = e.getSourceState();
		Class sourceStateType = e.getSourceStateType();

		Partition targetPartition = e.getTargetPartition();
		Object targetPartitionId = e.getTargetPartitionId();
		Processor targetProcessor = e.getTargetProcessor();
		State targetState = e.getTargetState();
		Class targetStateType = e.getTargetStateType();

		if (sourcePartitionId == null && sourcePartition != null)
			sourcePartitionId = sourcePartition.getPartitionId();

		if (sourceStateType == null && sourceState != null)
			sourceStateType = sourceState.getType();

		if (targetPartitionId == null && targetPartition != null)
			targetPartitionId = targetPartition.getPartitionId();

		if (targetStateType == null && targetState != null)
			targetStateType = targetState.getType();

		return sb;

	}

	static public void getAllChildren(ContainerScope root, List<ContainerScope> sink) {
		sink.add(root);
		for (ContainerScope cs : root.getChildContainerScopes())
			getAllChildren(cs, sink);
	}

	public static String diagnose(ContainerScope cs) {
		Labeler labeler = new BasicLabeler();
		cs.diagnose(labeler);
		return labeler.toString();
	}

	public static <P extends Port<?>> P createPort(P p, Connectable parent) {
		Class<? extends Port> c = p.getClass();
		P r;
		if (c == BasicInputPort.class)
			r = (P) new BasicInputPort(p.getActionType(), parent);
		else if (c == BasicRequestInputPort.class) {
			RequestInputPort rp = (RequestInputPort) p;
			r = (P) new BasicRequestInputPort(rp.getRequestActionType(), rp.getResponseActionType(), parent);
		} else if (c == BasicOutputPort.class)
			r = (P) new BasicOutputPort(p.getActionType(), parent);
		else if (c == BasicRequestOutputPort.class) {
			RequestOutputPort rp = (RequestOutputPort) p;
			r = (P) new BasicRequestOutputPort(rp.getRequestActionType(), rp.getResponseActionType(), parent);
		} else
			throw new RuntimeException("unkown port type: " + OH.getClassName(p));
		r.setConnectionOptional(p.isConnectionOptional());
		return r;
	}

	static public <A extends Action, B extends Action> CastingProcessor<A, B> wireCast(Suite suite, OutputPort<A> output, InputPort<B> input, boolean b) {
		if (input.getActionType().isAssignableFrom(output.getActionType())) {
			LH.warning(log, "no need to cast ports. output port: ", output, ", input port: ", input, ", suite: ", suite);
			suite.wire((OutputPort) output, input, b);
			return null;
		}

		final CastingProcessor<A, B> r = new CastingProcessor<A, B>(output.getActionType(), input.getActionType());
		suite.addChild(r);
		suite.wire(output, r, false);
		suite.wire(r.getOutput(), input, b);
		return r;
	}

	static public <A extends Action, B extends Action> CastingProcessor<A, B> wireCast(Suite suite, OutputPort<A> output, Processor<B, ?> input, boolean b) {
		final CastingProcessor<A, B> r = new CastingProcessor<A, B>(output.getActionType(), input.getActionType());
		suite.addChild(r);
		suite.wire(output, r, false);
		suite.wire(r.getOutput(), input, b);
		return r;
	}

	public static long nextContainerScopeUid() {
		long r = uid.incrementAndGet();
		return r;
	}

	public static ResultMessage<?> call(Container container, String name, Message msg, int i) {
		final Processor processor = (Processor) container.getRootSuite().getChild(name);
		final RequestMessage request = container.nw(RequestMessage.class);
		request.setAction(msg);
		final Object partitionId = processor.getPartitionResolver().getPartitionId(request);
		final ResultActionFuture future = container.getResultActionFutureController().createFuture(container);
		request.setFuture(future);
		container.getDispatchController().dispatch(null, processor, request, partitionId, null);
		return future.getResult(i);
	}

	public static BasicDispatcherController getBasicDispatchController(Container c) {
		DispatchController r = c.getDispatchController();
		while (true) {
			if (r instanceof BasicDispatcherController)
				return (BasicDispatcherController) r;
			else if (r instanceof DispatchControllerWrapper) {
				r = ((DispatchControllerWrapper) r).getInner();
			} else
				return null;
		}
	}

}
