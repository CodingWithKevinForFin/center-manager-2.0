package com.f1.utils;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingProgressCounter implements ProgressCounter {

	private long count = 0;
	private final long minIntervalCount;
	private final long minIntervalMills;
	private long lastCheckCount;
	private long lastCheckTime;
	private Logger logger;
	private String prefix;
	private Level level;

	public LoggingProgressCounter(Logger logger, Level level, String prefix, long frequencyCheck, long minInterval, TimeUnit timeUnit) {
		this.minIntervalMills = TimeUnit.MILLISECONDS.convert(minInterval, timeUnit);
		this.minIntervalCount = frequencyCheck;
		this.logger = logger;
		this.level = level;
		this.prefix = prefix;
		if (minIntervalMills > 0)
			this.lastCheckTime = EH.currentTimeMillis();
	}

	@Override
	public void onProgress(int countSinceLast, Object optionalDescription) {
		count += countSinceLast;
		if (count - lastCheckCount < minIntervalCount)
			return;
		lastCheckCount = count;
		if (minIntervalMills > 0) {
			long now = EH.currentTimeMillis();
			if (now - lastCheckTime < minIntervalMills)
				return;
			lastCheckTime = now;
		}
		log(optionalDescription);
	}

	private void log(Object optionalDescription) {
		if (optionalDescription == null)
			logger.log(level, prefix + " count=" + SH.toString(count));
		else
			logger.log(level, prefix + " count=" + SH.toString(count) + " (" + optionalDescription + ")");
	}
}
