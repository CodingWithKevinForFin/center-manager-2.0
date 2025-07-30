package com.f1.bootstrap.appmonitor;

import java.util.concurrent.atomic.AtomicLong;

import com.f1.base.IdeableGenerator;
import com.f1.povo.f1app.F1AppLogger;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailLoggerEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerEventListener;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.SpeedLoggerSink;
import com.f1.utils.EH;

public class AppMonitorLoggerListener extends AbstractAppMonitorObjectListener<F1AppLogger, SpeedLogger> implements SpeedLoggerEventListener {

	final private AtomicLong exceptionsCount = new AtomicLong();
	final private AtomicLong fineCount = new AtomicLong();
	final private AtomicLong warningCount = new AtomicLong();
	final private AtomicLong errorCount = new AtomicLong();
	final private AtomicLong droppedCount = new AtomicLong();
	final private AtomicLong bytes = new AtomicLong();
	final private IdeableGenerator generator;

	private static boolean first = true;

	public AppMonitorLoggerListener(AppMonitorState state, SpeedLogger logger) {
		super(state, logger);
		generator = state.getPartition().getContainer().getServices().getGenerator();
		if (first) {
			//TODO: this is a hack to make sure the class is not compiled inside the logger
			getState().getGenerator().nw(F1AppAuditTrailLoggerEvent.class);
			first = false;
		}
	}

	@Override
	public void onlogEvent(SpeedLoggerSink sink, SpeedLogger logger, char[] data, int dataStart, int dataLength, int level, Object msg, long timeMs, StackTraceElement ste) {
		AppMonitorAuditRule[] rules = getAuditRuleIdsOrNull();
		if (rules != null) {
			int matchCount = 0;
			for (AppMonitorAuditRule rule : rules) {
				AppMonitorLoggerRule loggerRule = (AppMonitorLoggerRule) rule;
				if (loggerRule.level <= level) {
					matchCount++;
				}
			}
			if (matchCount > 0) {
				F1AppAuditTrailLoggerEvent event = generator.nw(F1AppAuditTrailLoggerEvent.class);
				long ruleIds[] = new long[matchCount];
				matchCount = 0;
				if (rules.length == 1) {
					ruleIds[0] = rules[0].getId();
				} else {
					for (AppMonitorAuditRule rule : rules) {
						AppMonitorLoggerRule loggerRule = (AppMonitorLoggerRule) rule;
						if (loggerRule.level <= level)
							ruleIds[matchCount++] = loggerRule.getId();
					}
				}
				event.setType(F1AppAuditTrailRule.EVENT_TYPE_LOG);
				event.setAgentRuleIds(ruleIds);
				event.setLogLevel(level);
				if (msg != null) {
					event.setPayloadAsBytes(getState().getGenericConverter().object2Bytes(msg));
					event.setPayloadFormat(F1AppAuditTrailEvent.FORMAT_BYTES_F1);
				}
				if (ste != null) {
					event.setClassName(ste.getClassName());
					event.setFileName(ste.getFileName());
					event.setLineNumber(ste.getLineNumber());
					event.setMethodName(ste.getMethodName());
				} else
					event.setLineNumber(-1);
				if (timeMs > 0)
					event.setTimeMs(timeMs);
				else
					event.setTimeMs(EH.currentTimeMillis());
				event.setAuditSequenceNumber(getState().nextAuditSequenceNumber());
				event.setAgentF1ObjectId(getAgentObject().getId());
				onAuditEvent(event);
			}
		}

		if (sink == null) {
			droppedCount.incrementAndGet();
			return;
		}
		if (hasThrowable(msg))
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

	public static boolean hasThrowable(Object msg) {
		if (msg instanceof Throwable)
			return true;
		if (msg instanceof Object[])
			for (Object o : (Object[]) msg)
				if (o instanceof Throwable)
					return true;
		return false;
	}

	@Override
	public Class<F1AppLogger> getAgentType() {
		return F1AppLogger.class;
	}

	@Override
	protected void populate(SpeedLogger source, F1AppLogger sink) {
		sink.setLoggerId(source.getId());
		sink.setMinLogLevel(source.getMinimumLevel());
		sink.setExceptionsCount(getExceptionsCount());
		long fine = getFineCount();
		long warn = getWarningCount();
		long errr = getErrorCount();
		long droppedCount = getDroppedCount();
		sink.setTotalEventsCount(fine + warn + errr);
		sink.setWarningOrHigherCount(warn + errr);
		sink.setErrorOrHigherCount(errr);
		sink.setBytesLoggedCount(bytes.get());
		sink.setDroppedCount(droppedCount);
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
	private long getDroppedCount() {
		return droppedCount.get();
	}

	@Override
	public byte getListenerType() {
		return TYPE_LOGGER;
	}

}
