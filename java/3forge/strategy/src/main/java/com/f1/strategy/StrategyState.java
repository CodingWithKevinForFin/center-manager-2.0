package com.f1.strategy;

import com.f1.container.ContainerTools;
import com.f1.container.impl.BasicState;
import com.f1.fixomsclient.OmsClientState;
import com.f1.pofo.oms.ChildNewOrderRequest;
import com.f1.pofo.oms.ChildOrderRequest;
import com.f1.pofo.oms.OmsAction;
import com.f1.utils.MultiTimer;
import com.f1.utils.Timer;

public class StrategyState extends BasicState implements OrderManager {

	private Strategy strategy;
	private OmsClientState omsClientState;
	private StrategyWrapper strategyWrapper;

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public OmsClientState getOmsClientState() {
		return this.omsClientState;
	}

	public void init(OmsClientState clientState, StrategyWrapper strategyWrapper) {
		this.omsClientState = clientState;
		this.strategyWrapper = strategyWrapper;
	}

	private MultiTimer timers = new MultiTimer();
	private long nextExpectedTimer = Timer.DONE;

	public void addTimer(Timer timer) {
		long nextTime = timer.calculateNextOccurance(strategyWrapper.getTools().getNow());
		if (nextTime == Timer.DONE) {
			strategy.onTimerDone(timer);
			return;
		}
		if (nextTime < nextExpectedTimer || nextExpectedTimer == Timer.DONE) {
			nextExpectedTimer = nextTime;
			strategyWrapper.scheduleTimer(getPartitionId(), nextExpectedTimer);
		}
		timers.addTimer(nextTime, timer);
	}

	public boolean cancelTimer(Timer timer) {
		return timers.removeTimer(timer);
	}

	public void fireTimer(long scheduledTime, long now) {
		if (scheduledTime != nextExpectedTimer)
			return;
		for (Timer timer : timers.getCurrentTimers(scheduledTime)) {
			strategy.onTimer(timer, scheduledTime, now);
		}
		nextExpectedTimer = timers.calculateNextOccurance(Math.max(scheduledTime + 1, now));
		if (nextExpectedTimer != Timer.DONE)
			strategyWrapper.scheduleTimer(getPartitionId(), nextExpectedTimer);
		for (Timer timer : timers.popFinishedTimers()) {
			strategy.onTimerDone(timer);
		}
	}
	@Override
	public String createChildOrder(ChildNewOrderRequest child, String rootId) throws RequestException {
		return strategyWrapper.createChildOrder(getPartitionId(), child, rootId);
	}

	@Override
	public void cancelChildOrder(String parentId, String childID) throws RequestException {
		strategyWrapper.cancelChildOrder(getPartitionId(), parentId, childID);
	}

	@Override
	public String replaceChildOrder(ChildOrderRequest child, String rootId) {
		return strategyWrapper.replaceChildOrder(getPartitionId(), child, rootId);
	}

	@Override
	public void orderStatusUpdate(OmsAction update, String rootId) {
		strategyWrapper.orderStatusUpdate(update, rootId);
	}

	@Override
	public ChildOrderRequest amendRequest() {
		return strategyWrapper.amendRequest();
	}

	@Override
	public ChildNewOrderRequest newChildRequest() {
		return strategyWrapper.newChildRequest();
	}

	@Override
	public void cancelAllTimers() {
		strategyWrapper.cancelAllTimers();
	}

	@Override
	public ContainerTools getTools() {
		return strategyWrapper.getTools();
	}
}
