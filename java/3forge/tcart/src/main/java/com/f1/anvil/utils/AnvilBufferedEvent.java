package com.f1.anvil.utils;

import com.f1.base.Clearable;

public class AnvilBufferedEvent implements Clearable {

	public AnvilHorizonEvent nbboEvent;
	public AnvilHorizonEvent tradeEvent;
	public boolean inQueue;
	public long volume;
	public long nbboCnt, tradeCnt;
	public double low, high, px, value;
	public double open = Double.NaN;
	public boolean isFirst = true;
	public long size;
	public final String symbol;
	public long time;

	public AnvilBufferedEvent(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public void clear() {
		this.inQueue = false;
		this.nbboEvent = null;
		this.tradeEvent = null;
		tradeCnt = nbboCnt = 0;
		open = Double.NaN;
	}

	public void aggregateTrade(double px, long size, double high, double low, double value, long volume, long time) {
		this.px = px;
		this.high = high;
		this.low = low;
		this.volume = volume;
		this.value = value;
		this.size = size;
		this.time = time;
	}

	public void aggregateTradeWithOpen(double px, long size, double high, double low, double open, double value, long volume, long time) {
		this.open = open;
		this.px = px;
		this.high = high;
		this.low = low;
		this.volume = volume;
		this.value = value;
		this.size = size;
		this.time = time;
	}
}
