/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;
import com.f1.container.RequestProcessor;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;

public abstract class BasicRequestProcessor<A extends Action, S extends State, R extends Action> extends AbstractRequestProcessor<A, S, R> implements RequestProcessor<A, S, R> {

	public BasicRequestProcessor(Class<A> innerActionType, Class<S> stateType, Class<R> responseType) {
		super(innerActionType, stateType, responseType);
	}

	public BasicRequestProcessor(Class<A> innerActionType, Class<S> stateType, Class<R> responseType, PartitionResolver<A> resolver) {
		super(innerActionType, stateType, responseType, resolver);
	}

	private ObjectGeneratorForClass<ResultMessage<R>> resultGenerator;

	@Override
	public void processAction(RequestMessage<A> action, S state, ThreadScope threadScope) throws Exception {
		R ra = processRequest(action, state, threadScope);
		if (ra != null) {
			ResultMessage<R> result = resultGenerator.nw();
			result.setActionNoThrowable(ra);
			reply(action, result, threadScope);
		}
	}

	protected abstract R processRequest(RequestMessage<A> action, S state, ThreadScope threadScope) throws Exception;

	@Override
	public void start() {
		super.start();
		resultGenerator = (ObjectGeneratorForClass) getGenerator(ResultMessage.class);
	}

	public final ResultMessage<R> nwResultMessage(R action) {
		ResultMessage<R> r = nw(ResultMessage.class);
		r.setAction(action);
		return r;
	}
}
