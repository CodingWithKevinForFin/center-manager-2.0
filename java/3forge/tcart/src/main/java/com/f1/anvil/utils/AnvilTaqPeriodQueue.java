package com.f1.anvil.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Clearable;
import com.f1.utils.CircularList;
import com.f1.utils.OH;

public class AnvilTaqPeriodQueue implements Clearable {

	final private List<String> pendingSymbolQueue = new CircularList<String>();
	final private AnvilHorizonImpl nbboHorizon = new AnvilHorizonImpl();
	final private AnvilHorizonImpl tradeHorizon = new AnvilHorizonImpl();
	final private Map<String, AnvilBufferedEvent> horizonQueue = new HashMap<String, AnvilBufferedEvent>();
	private long periodMinTimeInclusive;
	private long periodMaxTimeExclusive;

	public AnvilTaqPeriodQueue() {
	}

	public Map<String, AnvilBufferedEvent> getHorizonQueue() {
		return horizonQueue;
	}

	public int getPendingSymbolQueueSize() {
		return pendingSymbolQueue.size();
	}
	public AnvilBufferedEvent popNextPendingBufferedEvent() {
		return horizonQueue.get(pendingSymbolQueue.remove(0));
	}

	public AnvilHorizonImpl getNbboHorizon() {
		return nbboHorizon;
	}

	public AnvilHorizonImpl getTradeHorizon() {
		return tradeHorizon;
	}

	public AnvilBufferedEvent getOrCreateAnvilBufferedEvent(String symbol) {
		AnvilBufferedEvent be = horizonQueue.get(symbol);
		if (be == null)
			horizonQueue.put(symbol, be = new AnvilBufferedEvent(symbol));
		return be;
	}

	public void ensureInQueue(AnvilBufferedEvent be) {
		if (!be.inQueue) {
			pendingSymbolQueue.add(be.symbol);
			be.inQueue = true;
		}
	}

	public boolean inPeriod(long time) {
		return time >= periodMinTimeInclusive && time < periodMaxTimeExclusive;
	}

	public void reset(long minTime, long period) {
		this.periodMinTimeInclusive = minTime;
		this.periodMaxTimeExclusive = minTime + period;
	}

	@Override
	public void clear() {
		OH.assertEq(this.nbboHorizon.getSize(), 0);
		OH.assertEq(this.tradeHorizon.getSize(), 0);
		OH.assertEq(this.pendingSymbolQueue.size(), 0);
		this.periodMinTimeInclusive = 0;
		this.periodMaxTimeExclusive = 0;
		this.nextQueue = null;
	}

	public long getPeriodMinTime() {
		return this.periodMinTimeInclusive;
	}
	public long getPeriodMaxTime() {
		return this.periodMaxTimeExclusive;
	}

	public long getOldestNbboTime() {
		return this.nbboHorizon.getOldestTime();
		//return r == -1L ? periodMinTimeInclusive : r;
	}

	public long getOldestTradeTime() {
		return this.tradeHorizon.getOldestTime();
		//return r == -1L ? periodMinTimeInclusive : r;
	}
	public AnvilTaqPeriodQueue nextQueue;

	public boolean isAfterPeriod(long time) {
		return time >= this.periodMaxTimeExclusive;
	}
	public boolean isBeforePeriod(long time) {
		return time < this.periodMinTimeInclusive;
	}

}
