/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.Action;
import com.f1.container.OutputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ResultOutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ContainerException;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicState;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.structs.BasicMultiMap;

public class ClassRoutingProcessor<A extends Action> extends BasicProcessor<A, BasicState> {

	public ClassRoutingProcessor(Class<A> actionType) {
		super(actionType, BasicState.class);
	}

	public ClassRoutingProcessor(Class<A> actionType, PartitionResolver<? super Action> resolver) {
		super(actionType, BasicState.class, resolver);
	}

	private CopyOnWriteHashMap<Class<?>, List<OutputPort<?>>> class2ports = new CopyOnWriteHashMap<Class<?>, List<OutputPort<?>>>();
	private CopyOnWriteHashMap<Class<?>, List<RequestOutputPort<?, ?>>> class2Requestports = new CopyOnWriteHashMap<Class<?>, List<RequestOutputPort<?, ?>>>();
	private CopyOnWriteHashMap<Class<?>, List<ResultOutputPort<?>>> class2Resultports = new CopyOnWriteHashMap<Class<?>, List<ResultOutputPort<?>>>();

	private BasicMultiMap.List<Class<?>, OutputPort<?>> registeredTypes = new BasicMultiMap.List<Class<?>, OutputPort<?>>();
	private BasicMultiMap.List<Class<?>, RequestOutputPort<?, ?>> registeredRequestTypes = new BasicMultiMap.List<Class<?>, RequestOutputPort<?, ?>>();
	private BasicMultiMap.List<Class<?>, ResultOutputPort<?>> registeredResultTypes = new BasicMultiMap.List<Class<?>, ResultOutputPort<?>>();

	@Override
	public void start() {
		super.start();
		class2ports.putAll(registeredTypes);
		class2Requestports.putAll(registeredRequestTypes);
		class2Resultports.putAll(registeredResultTypes);
	}


	public <T extends Action> OutputPort<T> newOutputPort(Class<T> actionType) {
		assertNotStarted();
		OutputPort<T> r = super.newOutputPort(actionType).setName(actionType.getSimpleName() + "OutPort");
		registeredTypes.putMulti(actionType, r);
		return r;
	}

	@Override
	public void processAction(A origAction, BasicState state, ThreadScope threadLocal) {
		if (origAction instanceof RequestMessage) {
			processRequestMessage((RequestMessage) origAction, state, threadLocal);
			return;
		} else if (origAction instanceof ResultMessage) {
			processResultMessage((ResultMessage) origAction, state, threadLocal);
			return;
		}
		A action = origAction;
		List<OutputPort<?>> port = class2ports.get(action.getClass());
		if (port == null) {
			Entry<Class<?>, List<OutputPort<?>>> best = null;
			for (Map.Entry<Class<?>, List<OutputPort<?>>> e : class2ports.entrySet()) {
				if (e.getKey().isAssignableFrom(action.getClass())) {
					if (best == null)
						best = e;
					else if (best.getKey().isAssignableFrom(e.getKey()))
						best = e;
				}
			}
			if (best != null)
				class2ports.put(action.getClass(), port = best.getValue());
		}
		if (port == null)
			throw new ContainerException("no port available for class " + action.getClass().getName()).set("registered types", registeredTypes.keySet());
		for (OutputPort<?> p : port)
			((OutputPort<A>) p).send(origAction, threadLocal);
	}

	//Requests
	protected void processRequestMessage(RequestMessage<A> request, BasicState state, ThreadScope threadScope) {
		Class<? extends Action> clazz = request.getAction().getClass();
		List<RequestOutputPort<?, ?>> port = class2Requestports.get(clazz);
		if (port == null) {
			Entry<Class<?>, List<RequestOutputPort<?, ?>>> best = null;
			for (Map.Entry<Class<?>, List<RequestOutputPort<?, ?>>> e : class2Requestports.entrySet()) {
				if (e.getKey().isAssignableFrom(clazz)) {
					if (best == null)
						best = e;
					else if (best.getKey().isAssignableFrom(e.getKey()))
						best = e;
				}
			}
			if (best != null)
				class2Requestports.put(clazz, port = best.getValue());
		}
		if (port == null)
			throw new ContainerException("no request port available for class " + clazz.getName()).set("registered requestTypes", registeredRequestTypes.keySet());
		for (RequestOutputPort<?, ?> p : port)
			((RequestOutputPort<A, ?>) p).send(request, threadScope);
	}

	@Override
	public <C extends Action, V extends Action> RequestOutputPort<C, V> newRequestOutputPort(Class<C> requestType, Class<V> responseType) {
		assertNotStarted();
		RequestOutputPort<C, V> r = super.newRequestOutputPort(requestType, responseType).setName(requestType.getSimpleName() + "OutputPort");
		registeredRequestTypes.putMulti(requestType, r);
		return r;
	}

	//Results
	protected void processResultMessage(ResultMessage<A> request, BasicState state, ThreadScope threadScope) {
		Class<? extends Action> clazz = request.getAction().getClass();
		List<ResultOutputPort<?>> port = class2Resultports.get(clazz);
		if (port == null) {
			Entry<Class<?>, List<ResultOutputPort<?>>> best = null;
			for (Map.Entry<Class<?>, List<ResultOutputPort<?>>> e : class2Resultports.entrySet()) {
				if (e.getKey().isAssignableFrom(clazz)) {
					if (best == null)
						best = e;
					else if (best.getKey().isAssignableFrom(e.getKey()))
						best = e;
				}
			}
			if (best != null)
				class2Resultports.put(clazz, port = best.getValue());
		}
		if (port == null)
			throw new ContainerException("no request port available for class " + clazz.getName()).set("registered resultTypes", registeredResultTypes.keySet());
		for (ResultOutputPort<?> p : port)
			((ResultOutputPort<A>) p).send(request, threadScope);
	}

	@Override
	public <C extends Action> ResultOutputPort<C> newResultOutputPort(Class<C> requestType) {
		assertNotStarted();
		ResultOutputPort<C> r = super.newResultOutputPort(requestType).setName(requestType.getSimpleName() + "OutputPort");
		registeredResultTypes.putMulti(requestType, r);
		return r;
	}

}
