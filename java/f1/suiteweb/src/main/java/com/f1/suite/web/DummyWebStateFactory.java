package com.f1.suite.web;

import java.util.logging.Level;

import com.f1.base.Action;
import com.f1.container.Partition;
import com.f1.container.Processor;
import com.f1.container.StateGenerator;
import com.f1.container.impl.AbstractContainerScope;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class DummyWebStateFactory extends AbstractContainerScope implements StateGenerator<Action, WebState> {

	@Override
	public WebState createState(Partition partition, Action a, Processor<? extends Action, ?> processor) {
		if (log.isLoggable(Level.FINE))
			LH.fine(log, "Ignoring backend Message for unknown state: ", OH.getClassName(a));
		return null;
	}

	@Override
	public Class<WebState> getStateType() {
		return WebState.class;
	}

}
