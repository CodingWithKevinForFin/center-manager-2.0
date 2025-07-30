package com.f1.utils.flogger.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.f1.utils.EH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.flogger.Flogger;
import com.f1.utils.flogger.FloggerAdapter;
import com.f1.utils.flogger.FloggerFormatter;

public class BasicFlogger implements Flogger {
	private final List<FloggerCall> floggerCalls = new ArrayList<FloggerCall>();
	private final String transactionId;
	private final BasicFloggerManager manager;
	private int maxLevel = Flogger.ALL;

	public BasicFlogger(String txnId, BasicFloggerManager manager) {
		this.transactionId = txnId;
		this.manager = manager;
	}

	@Override
	public void log(Object innerLogger, boolean passThroughToInner, int level, Object message, Object extra) {
		if (level > maxLevel)
			maxLevel = level;
		FloggerAdapter<Object> adapter = manager.getAdapter(innerLogger);
		final int innerLoggerLevel = adapter.getLevel(innerLogger);
		if (level < innerLoggerLevel)
			return;
		FloggerCall call = new FloggerCall(adapter, innerLogger, level, message, extra, EH.currentTimeMillis(), Thread.currentThread());
		floggerCalls.add(call);
		if (passThroughToInner)
			call.adapter.log(call.innerLogger, call.level, transactionId, String.valueOf(call.message), call.now, call.now, call.ste, call.extra);
	}

	@Override
	public void all(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, ALL, message, extra);
	}

	@Override
	public void trace(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, TRACE, message, extra);

	}

	@Override
	public void finest(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, FINEST, message, extra);

	}

	@Override
	public void finer(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, FINER, message, extra);

	}

	@Override
	public void fine(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, FINE, message, extra);

	}

	@Override
	public void debug(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, DEBUG, message, extra);

	}

	@Override
	public void config(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, CONFIG, message, extra);
	}

	@Override
	public void info(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, INFO, message, extra);
	}

	@Override
	public void warning(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, WARNING, message, extra);
	}

	@Override
	public void error(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, ERROR, message, extra);
	}

	@Override
	public void severe(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, SEVERE, message, extra);
	}

	@Override
	public void fatal(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, FATAL, message, extra);
	}

	@Override
	public void off(Object innerLogger, boolean passThroughToInner, Object message, Object extra) {
		log(innerLogger, passThroughToInner, OFF, message, extra);
	}

	@Override
	public void all(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, ALL, message, null);

	}

	@Override
	public void trace(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, TRACE, message, null);

	}

	@Override
	public void finest(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, FINEST, message, null);

	}

	@Override
	public void finer(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, FINER, message, null);

	}

	@Override
	public void fine(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, FINE, message, null);

	}

	@Override
	public void debug(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, DEBUG, message, null);

	}

	@Override
	public void config(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, CONFIG, message, null);

	}

	@Override
	public void info(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, INFO, message, null);

	}

	@Override
	public void warning(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, WARNING, message, null);

	}

	@Override
	public void error(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, ERROR, message, null);

	}

	@Override
	public void severe(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, SEVERE, message, null);

	}

	@Override
	public void fatal(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, FATAL, message, null);

	}

	@Override
	public void off(Object innerLogger, boolean passThroughToInner, Object message) {
		log(innerLogger, passThroughToInner, OFF, message, null);

	}

	@Override
	public String getTransactionId() {
		return transactionId;
	}

	public void writeToInner() {
		long now = EH.currentTimeMillis();
		for (FloggerCall call : floggerCalls)
			call.adapter.log(call.innerLogger, call.level, transactionId, String.valueOf(call.message), call.now, now, call.ste, call.extra);
	}

	@Override
	public StringBuilder toDetailedString(FloggerFormatter formatter, StringBuilder sb) {
		try {
			for (FloggerCall call : floggerCalls)
				formatter
						.append(sb, call.adapter.getId(call.innerLogger), transactionId, call.message, call.level, call.extra, call.now, call.ste, call.thread);
		} catch (IOException e) {
			sb.append("#### ERROR PRINTING FLOGGER LOGS");
			sb.append(SH.NEWLINE);
			SH.printStackTrace("## ", "> ", e, sb);
			sb.append("####");
			sb.append(SH.NEWLINE);
		}
		return sb;
	}

	@Override
	public String toString() {
		return toSummaryString();
	}

	public static class FloggerCall {

		final public StackTraceElement ste;
		final public Object innerLogger;
		final public int level;
		final public Object message;
		final public Object extra;
		final public long now;
		final private FloggerAdapter<Object> adapter;
		final private Thread thread;

		public FloggerCall(FloggerAdapter<Object> adapter, Object innerLogger, int level, Object message, Object extra, long now, Thread thread) {
			this.adapter = adapter;
			this.innerLogger = innerLogger;
			this.level = level;
			this.message = message;
			this.extra = extra;
			this.now = now;
			this.ste = FloggerUtils.getStackTraceElement();
			this.thread = thread;
		}

		@Override
		public String toString() {
			return "FloggerCall [level=" + level + ", message=" + message + ", extra=" + extra + ", ste=" + ste + "]";
		}

	}

	@Override
	public boolean hasAtleast(int floggerLevel_) {
		return maxLevel >= floggerLevel_;
	}

	@Override
	public String toSummaryString() {
		return toSummaryString(DEFAULT_SUMMARY_MAX_LENGTH);
	}

	@Override
	public String toSummaryString(int maxLength) {
		int[] count = new int[Flogger.OFF + 1];
		int exceptions = 0;
		Throwable firstException = null;
		Object worstMessage = null;
		for (FloggerCall fc : floggerCalls) {
			if (fc.extra instanceof Throwable) {
				exceptions++;
				if (firstException == null)
					firstException = (Throwable) fc.extra;
			}
			if (worstMessage == null && fc.level == maxLevel)
				worstMessage = fc.message;
			count[fc.level]++;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getTransactionId()).append(" [");
		boolean first = true;
		if (exceptions > 0) {
			sb.append(exceptions).append(" EXCEPTION");
			if (exceptions > 1)
				sb.append('S');
			first = false;
		}
		for (int level = count.length - 1; level >= 0; level--)
			if (count[level] > 0) {
				if (first)

					first = false;
				else
					sb.append(", ");
				sb.append(count[level]).append(" ").append(FloggerUtils.getLevelAsText(level));
				if (count[level] > 1)
					sb.append('S');
			}
		sb.append("]");
		if (firstException != null) {
			sb.append('{').append(OH.noNull(firstException.getMessage(), firstException.getClass().getSimpleName()));
		} else if (worstMessage != null) {
			SH.s(worstMessage.toString(), sb.append('{'));
		}
		return SH.ddd(sb.toString(), maxLength - 1) + "}";
	}

	@Override
	public String toDetailedString() {
		return toDetailedString(manager.getDefaultFloggerFormatter(), new StringBuilder(floggerCalls.size() * 50)).toString();
	}

}
