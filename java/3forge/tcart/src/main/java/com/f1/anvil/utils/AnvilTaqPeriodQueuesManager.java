package com.f1.anvil.utils;

import java.util.logging.Logger;

import com.f1.ami.center.table.AmiRow;
import com.f1.base.Generator;
import com.f1.utils.LH;
import com.f1.utils.concurrent.ObjectPoolForClearable;

public class AnvilTaqPeriodQueuesManager implements Generator<AnvilTaqPeriodQueue> {

	private ObjectPoolForClearable<AnvilTaqPeriodQueue> taqPeriodQueuesPool = new ObjectPoolForClearable<AnvilTaqPeriodQueue>(this, 1024);
	private static final Logger log = LH.get(AnvilTaqPeriodQueuesManager.class);
	private long maxNbboTime = 0L;
	private long maxTradeTime = 0L;
	private AnvilTaqPeriodQueue readBuffer = null;
	private AnvilTaqPeriodQueue writeBufferForNbbo = null;
	private AnvilTaqPeriodQueue writeBufferForTrade = null;
	final private long period;

	public AnvilTaqPeriodQueue getNbboTradeBufferForReading() {
		return readBuffer;
	}

	public AnvilTaqPeriodQueuesManager(long period) {
		this.period = period;
	}
	public boolean hasPendingSymbols() {
		return this.readBuffer != null && this.readBuffer.getPendingSymbolQueueSize() > 0;
	}
	public boolean hasNextPeriod() {
		return this.readBuffer != null && this.readBuffer != this.writeBufferForNbbo && this.readBuffer != this.writeBufferForTrade && this.readBuffer.nextQueue != null;
	}
	public long moveToNextPeriod() {
		if (!hasNextPeriod() || hasPendingSymbols())
			return -1;
		long r = this.readBuffer.getPeriodMinTime() + period - 1;
		AnvilTaqPeriodQueue garbage = this.readBuffer;
		this.readBuffer = this.readBuffer.nextQueue;
		taqPeriodQueuesPool.recycle(garbage);
		return r;
	}
	public AnvilBufferedEvent onTrade(String symbol, long time, AmiRow row) {
		if (time < maxTradeTime) {
			LH.info(log, "Dropping out-of-order trade: " + row);
			return null;
		}
		maxTradeTime = time;
		AnvilTaqPeriodQueue buffer = getWriteBufferForTrade(time);
		AnvilBufferedEvent be = buffer.getOrCreateAnvilBufferedEvent(symbol);
		if (be.tradeEvent != null) {
			be.tradeEvent.updateEvent(time, row);
		} else {
			be.tradeEvent = buffer.getTradeHorizon().addTime(time, row);
		}
		be.tradeCnt++;
		buffer.ensureInQueue(be);
		return be;
	}

	public AnvilBufferedEvent onNbbo(String symbol, long time, AmiRow row) {
		if (time < maxNbboTime) {
			LH.info(log, "Dropping out-of-order nbbo: " + row);
			return null;
		}
		maxNbboTime = time;
		AnvilTaqPeriodQueue buffer = getWriteBufferForNbbo(time);
		AnvilBufferedEvent be = buffer.getOrCreateAnvilBufferedEvent(symbol);
		if (be.nbboEvent != null) {
			be.nbboEvent.updateEvent(time, row);
		} else {
			be.nbboEvent = buffer.getNbboHorizon().addTime(time, row);
		}
		buffer.ensureInQueue(be);
		be.nbboCnt++;
		be.time = time;
		return be;
	}
	public long getNewestNbboTime() {
		return this.maxNbboTime;
	}
	public long getNewestTradeTime() {
		return this.maxTradeTime;
	}
	public long getOldestTaqTime() {
		return Math.min(getOldestTradeTime(), getOldestNbboTime());
	}
	public long getOldestNbboTime() {
		if (this.readBuffer == null)
			return 0L;
		long r = this.readBuffer.getOldestNbboTime();
		if (r != -1)
			return r;
		for (AnvilTaqPeriodQueue t = this.readBuffer.nextQueue; t != null; t = t.nextQueue)
			if ((r = t.getOldestNbboTime()) != -1)
				return r;
		return this.readBuffer.getPeriodMinTime();
	}
	public long getOldestTradeTime() {
		if (this.readBuffer == null)
			return 0L;
		long r = this.readBuffer.getOldestTradeTime();
		if (r != -1)
			return r;
		for (AnvilTaqPeriodQueue t = this.readBuffer.nextQueue; t != null; t = t.nextQueue)
			if ((r = t.getOldestTradeTime()) != -1)
				return r;
		return this.readBuffer.getPeriodMinTime();
	}
	public AnvilBufferedEvent popNextPendingBufferedEvent() {
		return this.readBuffer.popNextPendingBufferedEvent();
	}

	@Override
	public AnvilTaqPeriodQueue nw() {
		return new AnvilTaqPeriodQueue();
	}

	private AnvilTaqPeriodQueue getWriteBufferForNbbo(long time) {
		if (readBuffer == null) {
			this.readBuffer = this.taqPeriodQueuesPool.nw();
			this.readBuffer.reset(roundDownToPeriod(time), period);
			return this.writeBufferForTrade = writeBufferForNbbo = this.readBuffer;
		}
		while (!writeBufferForNbbo.inPeriod(time)) {
			AnvilTaqPeriodQueue next = writeBufferForNbbo.nextQueue;
			if (next == null || next.isBeforePeriod(time)) {
				writeBufferForNbbo = writeBufferForNbbo.nextQueue = taqPeriodQueuesPool.nw();
				writeBufferForNbbo.reset(roundDownToPeriod(time), period);
				writeBufferForNbbo.nextQueue = next;
				break;
			} else if (writeBufferForNbbo.isBeforePeriod(time))
				throw new IllegalStateException();
			else
				writeBufferForNbbo = next;
		}
		return writeBufferForNbbo;
	}
	private AnvilTaqPeriodQueue getWriteBufferForTrade(long time) {
		if (readBuffer == null) {
			this.readBuffer = this.taqPeriodQueuesPool.nw();
			this.readBuffer.reset(roundDownToPeriod(time), period);
			return this.writeBufferForTrade = writeBufferForNbbo = this.readBuffer;
		}
		while (!writeBufferForTrade.inPeriod(time)) {
			AnvilTaqPeriodQueue next = writeBufferForTrade.nextQueue;
			if (next == null || next.isBeforePeriod(time)) {
				writeBufferForTrade = writeBufferForTrade.nextQueue = taqPeriodQueuesPool.nw();
				writeBufferForTrade.reset(roundDownToPeriod(time), period);
				writeBufferForTrade.nextQueue = next;
				break;
			} else if (writeBufferForTrade.isBeforePeriod(time))
				throw new IllegalStateException();
			else
				writeBufferForTrade = next;
		}
		return writeBufferForTrade;
	}
	private long roundDownToPeriod(long time) {
		return (time / period) * period;
	}

	public StringBuilder getStats(StringBuilder sb) {
		sb.append("maxNbbo=").append(maxNbboTime);
		sb.append(",maxTrade=").append(maxTradeTime);
		sb.append(",oldNbbo=").append(this.getOldestNbboTime());
		sb.append(",oldTrad=").append(this.getOldestTradeTime());
		sb.append(",read=").append(System.identityHashCode(this.readBuffer));
		sb.append(",nWrt=").append(System.identityHashCode(this.writeBufferForNbbo));
		sb.append(",tWrt=").append(System.identityHashCode(this.writeBufferForTrade));
		sb.append(", queue=");
		for (AnvilTaqPeriodQueue t = readBuffer; t != null; t = t.nextQueue)
			sb.append("[").append(System.identityHashCode(t)).append(" ").append(t.getPeriodMinTime()).append("-").append(t.getPeriodMaxTime()).append(" size=")
					.append(t.getPendingSymbolQueueSize()).append("]").append(t.nextQueue == null ? "" : "->");
		return sb;
	}
}
