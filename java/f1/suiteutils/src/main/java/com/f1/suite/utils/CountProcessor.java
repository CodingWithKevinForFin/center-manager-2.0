/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils;

import java.util.concurrent.atomic.AtomicLong;

import com.f1.base.Action;
import com.f1.container.State;
import com.f1.container.ThreadScope;

public class CountProcessor<A extends Action> extends ChainProcessor<A, State> {

	private AtomicLong count = new AtomicLong();

	public CountProcessor(Class<A> actionType) {
		super(actionType, State.class);
	}

	@Override
	public void processAction(A action, State state, ThreadScope theadLocal) throws Exception {
		count.incrementAndGet();
		super.processAction(action, state, theadLocal);
	}

	public void reset() {
		count.set(0);
	}

	public long getCount() {
		return count.get();
	}
}
