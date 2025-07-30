/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.Action;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.AbstractRequestProcessor;
import com.f1.container.impl.BasicState;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;

public class ClassRoutingRequestProcessor<A extends Action, RES extends Action> extends AbstractRequestProcessor<A, BasicState, RES> {

	public ClassRoutingRequestProcessor(Class<A> actionType, Class<RES> responseType, PartitionResolver<A> resolver) {
		super(actionType, BasicState.class, responseType, resolver);
	}

	private CopyOnWriteHashMap<Class<?>, RequestOutputPort<?, ?>> class2ports = new CopyOnWriteHashMap<Class<?>, RequestOutputPort<?, ?>>();
	private Map<Class<?>, RequestOutputPort<?, ?>> registeredTypes = new HashMap<Class<?>, RequestOutputPort<?, ?>>();

	@Override
	public <T extends Action, RES extends Action> RequestOutputPort<T, RES> newRequestOutputPort(Class<T> requestType, Class<RES> responseType) {
		assertNotStarted();
		RequestOutputPort<T, RES> r = super.newRequestOutputPort(requestType, responseType).setName(requestType.getSimpleName() + "OutputPort");
		CH.putOrThrow(registeredTypes, requestType, r);
		return r;
	}

	@Override
	public void processAction(RequestMessage<A> action, BasicState state, ThreadScope threadScope) throws Exception {
		RequestOutputPort<?, ?> port = class2ports.get(action.getAction().getClass());
		if (port == null) {
			Entry<Class<?>, RequestOutputPort<?, ?>> best = null;
			for (Map.Entry<Class<?>, RequestOutputPort<?, ?>> e : class2ports.entrySet()) {
				if (e.getKey().isAssignableFrom(action.getAction().getClass())) {
					if (best == null)
						best = e;
					else if (best.getKey().isAssignableFrom(e.getKey()))
						best = e;
				}
			}
			if (best != null)
				class2ports.put(action.getAction().getClass(), port = best.getValue());
		}
		if (port == null)
			throw new RuntimeException("no port available for class " + action.getAction().getClass().getName());
		((RequestOutputPort<A, ?>) port).send(action, threadScope);
	}

	@Override
	public void start() {
		super.start();
		class2ports.putAll(registeredTypes);
	}

}
