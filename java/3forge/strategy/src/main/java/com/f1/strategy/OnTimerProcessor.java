package com.f1.strategy;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;

public class OnTimerProcessor extends BasicProcessor<TimerAction, StrategyState> {

	public OnTimerProcessor() {
		super(TimerAction.class, StrategyState.class);
	}

	@Override
	public void processAction(TimerAction action, StrategyState state, ThreadScope threadScope) throws Exception {
		state.fireTimer(action.getScheduledTime(), getTools().getNow());

	}

}
