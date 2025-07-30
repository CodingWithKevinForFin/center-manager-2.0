package com.f1.suite.utils;

import java.util.logging.Level;
import com.f1.base.Action;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;

public class AckProcessor extends BasicProcessor<Action, State> {

	public AckProcessor() {
		super(Action.class, State.class, null);
	}

	@Override
	public void processAction(Action action, State state, ThreadScope threadScope) {
		action.ack(null);
		if (getLog().isLoggable(Level.INFO))
			getLog().info("Acked: " + action);
	}

}
