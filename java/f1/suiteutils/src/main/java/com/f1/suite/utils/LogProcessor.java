/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils;

import java.util.logging.Level;

import com.f1.base.Action;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.utils.OH;

public class LogProcessor<A extends Action> extends ChainProcessor<A, State> {

	private Level level = Level.INFO;
	private String prefix = null;

	public LogProcessor(Class<A> actionType) {
		super(actionType, State.class);
	}

	@Override
	public void processAction(A action, State state, ThreadScope theadLocal) throws Exception {
		if (prefix != null)
			getLog().log(level, prefix + OH.toString(action));
		else
			getLog().log(level, OH.toString(action));
		super.processAction(action, state, theadLocal);
	}

	public void setLevel(Level level) {
		assertNotStarted();
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

	public void setPrefix(String prefix) {
		assertNotStarted();
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}
}
