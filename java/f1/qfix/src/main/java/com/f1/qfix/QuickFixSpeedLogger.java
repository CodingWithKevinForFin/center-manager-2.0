package com.f1.qfix;

import quickfix.Log;

import com.f1.speedlogger.SpeedLogger;

public class QuickFixSpeedLogger implements Log {

	final private SpeedLogger logger;
	final private int level, errorLevel, eventLevel;

	public QuickFixSpeedLogger(SpeedLogger logger, int level, int eventLevel, int errorLevel) {
		this.logger = logger;
		this.level = level;
		this.eventLevel = eventLevel;
		this.errorLevel = errorLevel;
	}

	@Override
	public void clear() {
	}

	@Override
	public void onIncoming(String message) {
		if (logger.getMinimumLevel() <= level)
			logger.log(level, new Object[] { "INCOMING: ", message });
	}

	@Override
	public void onOutgoing(String message) {
		if (logger.getMinimumLevel() <= level)
			logger.log(level, new Object[] { "OUTGOING: ", message });

	}

	@Override
	public void onEvent(String message) {
		if (logger.getMinimumLevel() <= eventLevel)
			logger.log(level, new Object[] { "EVENT: ", message });

	}	@Override	public void onErrorEvent(String text) {		// TODO Auto-generated method stub			}

}
