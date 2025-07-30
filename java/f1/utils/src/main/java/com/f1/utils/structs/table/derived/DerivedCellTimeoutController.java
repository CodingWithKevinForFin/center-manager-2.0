package com.f1.utils.structs.table.derived;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.string.ExpressionParserException;

public class DerivedCellTimeoutController implements TimeoutController {

	private static final Logger log = LH.get();
	public static final long DEFAULT_TIMEOUT = 60000;

	private long timeoutMillis;
	private long startNanos;

	//	public DerivedCellTimeoutController() {
	//		this(DEFAULT_TIMEOUT);
	//	}

	public DerivedCellTimeoutController(long timeoutMillis) {
		this.startNanos = System.nanoTime();
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	public long getTimeoutMillis() {
		return timeoutMillis;
	}

	@Override
	public int throwIfTimedout() {
		if (Thread.interrupted())
			throw new ExpressionParserException(0, "THREAD_INTERRUPTED").setIsRuntime();
		int r = getTimeoutMillisRemainingOrZero();
		if (r == 0)
			throw new ExpressionParserException(0, "TIMEOUT_EXCEEDED(" + timeoutMillis + " millis)").setIsRuntime();
		return r;
	}

	@Override
	public int throwIfTimedout(DerivedCellCalculator block) {
		if (Thread.interrupted())
			throw new ExpressionParserException(block == null ? 0 : block.getPosition(), "THREAD_INTERRUPTED").setIsRuntime();
		int r = getTimeoutMillisRemainingOrZero();
		if (r == 0)
			throw new ExpressionParserException(block == null ? 0 : block.getPosition(), "TIMEOUT_EXCEEDED").setIsRuntime();
		return r;
	}
	@Override
	public void toDerivedThrowIfTimedout(DerivedCellCalculator block) {
		if (Thread.interrupted())
			throw new FlowControlThrow(block, "THREAD_INTERRUPTED");
		int r = getTimeoutMillisRemainingOrZero();
		if (r == 0)
			throw new FlowControlThrow(block, "TIMEOUT_EXCEEDED");
	}

	@Override
	public int getTimeoutMillisRemainingOrZero() {
		return (int) MH.clip(getRawTimeoutMillisRemaining(), 0, Integer.MAX_VALUE);
	}

	private long getRawTimeoutMillisRemaining() {
		return timeoutMillis - (System.nanoTime() - startNanos) / 1000000;
	}
	@Override
	public long getStartTimeNanos() {
		return this.startNanos;
	}

	@Override
	public int getTimeoutMillisRemaining() {
		return (int) MH.clip(getRawTimeoutMillisRemaining(), 1, Integer.MAX_VALUE);
	}

	public void addTime(long addTime) {
		this.timeoutMillis += addTime;
	}

}
