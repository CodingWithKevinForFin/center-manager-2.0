package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.ThrowableHandler;
import com.f1.container.exceptions.ProcessorException;
import com.f1.utils.SH;

public class BasicThrowableHandler implements ThrowableHandler<Action, State> {

	private boolean shouldAck = true;

	@Override
	public void handleThrowable(Processor<? extends Action, ? extends State> p, Action a, State s, ThreadScope t, Throwable thrown) throws Throwable {
		try {
			if (a != null && shouldAck)
				a.ack(getClass().getSimpleName() + " caught exception: " + SH.printStackTrace(thrown));
		} catch (Exception e) {

		}
		if (thrown instanceof ProcessorException)
			throw thrown;
		throw new ProcessorException("error in processor", thrown).setAction(a).setTargetProcessor(p).setTargetState(s);
	}

	public void setShouldAckWithError(boolean shouldAck) {
		this.shouldAck = shouldAck;
	}

	public boolean getShouldAckWithError() {
		return shouldAck;
	}
}
