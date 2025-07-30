package com.f1.bootstrap.appmonitor;

import java.util.concurrent.atomic.AtomicLong;

import com.f1.povo.f1app.F1AppLoggerSink;
import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerEventListener;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.SpeedLoggerSink;

public class AppMonitorLoggerSinkListener extends AbstractAppMonitorObjectListener<F1AppLoggerSink, SpeedLoggerSink> implements SpeedLoggerEventListener {

	final private AtomicLong exceptionsCount = new AtomicLong();
	final private AtomicLong fineCount = new AtomicLong();
	final private AtomicLong warningCount = new AtomicLong();
	final private AtomicLong errorCount = new AtomicLong();
	final private AtomicLong bytes = new AtomicLong();

	public AppMonitorLoggerSinkListener(AppMonitorState state, SpeedLoggerSink sink) {
		super(state, sink);
	}

	@Override
	public void onlogEvent(SpeedLoggerSink sink, SpeedLogger logger, char[] data, int dataStart, int dataLength, int level, Object msg, long time, StackTraceElement ste) {
		if (AppMonitorLoggerListener.hasThrowable(msg))
			exceptionsCount.incrementAndGet();
		if (level < SpeedLoggerLevels.WARNING)
			fineCount.incrementAndGet();
		else if (level < SpeedLoggerLevels.ERROR)
			warningCount.incrementAndGet();
		else if (level < SpeedLoggerLevels.OFF)
			errorCount.incrementAndGet();
		bytes.addAndGet(dataLength);
		flagChanged();
	}
	@Override
	public void onLoggerMinLevelChanged(SpeedLogger logger, int oldLevel, int newLevel) {
		flagChanged();
	}

	@Override
	public Class<F1AppLoggerSink> getAgentType() {
		return F1AppLoggerSink.class;
	}

	@Override
	protected void populate(SpeedLoggerSink source, F1AppLoggerSink sink) {
		sink.setSinkId(source.getId());
		sink.setExceptionsCount(getExceptionsCount());
		long fine = getFineCount();
		long warn = getWarningCount();
		long errr = getErrorCount();
		sink.setTotalEventsCount(fine + warn + errr);
		sink.setWarningOrHigherCount(warn + errr);
		sink.setErrorOrHigherCount(errr);
		sink.setBytesLoggedCount(bytes.get());
	}

	private long getErrorCount() {
		return errorCount.get();
	}

	private long getWarningCount() {
		return warningCount.get();
	}

	private long getFineCount() {
		return fineCount.get();
	}

	private long getExceptionsCount() {
		return exceptionsCount.get();
	}

	@Override
	public byte getListenerType() {
		return TYPE_LOGGER_SINK;
	}

}
