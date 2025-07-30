package com.f1.utils.flogger.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.f1.utils.flogger.Flogger;
import com.f1.utils.flogger.FloggerAdapter;

public class SunFloggerAdapter implements FloggerAdapter<Logger> {
	final private static int ALL = Integer.MIN_VALUE;
	final private static int FINEST = 300;
	final private static int FINER = 400;
	final private static int FINE = 500;
	final private static int CONFIG = 700;
	final private static int INFO = 800;
	final private static int WARNING = 900;
	final private static int SEVERE = 1000;
	final private static int OFF = Integer.MAX_VALUE;

	static {
		if (ALL != Level.ALL.intValue())
			throw new RuntimeException("ALL level incorrect");
		if (FINEST != Level.FINEST.intValue())
			throw new RuntimeException("FINEST level incorrect");
		if (FINER != Level.FINER.intValue())
			throw new RuntimeException("FINER level incorrect");
		if (FINE != Level.FINE.intValue())
			throw new RuntimeException("FINE level incorrect");
		if (CONFIG != Level.CONFIG.intValue())
			throw new RuntimeException("CONFIG level incorrect");
		if (INFO != Level.INFO.intValue())
			throw new RuntimeException("INFO level incorrect");
		if (WARNING != Level.WARNING.intValue())
			throw new RuntimeException("WARNING level incorrect");
		if (SEVERE != Level.SEVERE.intValue())
			throw new RuntimeException("SEVERE level incorrect");
		if (OFF != Level.OFF.intValue())
			throw new RuntimeException("OFF level incorrect");
	}

	public static int getFloggerLevel(Level level) {
		switch (level.intValue()) {
			case ALL :
				return Flogger.ALL;
			case FINEST :
				return Flogger.FINEST;
			case FINER :
				return Flogger.FINER;
			case FINE :
				return Flogger.FINE;
			case CONFIG :
				return Flogger.CONFIG;
			case INFO :
				return Flogger.INFO;
			case WARNING :
				return Flogger.WARNING;
			case SEVERE :
				return Flogger.SEVERE;
			case OFF :
				return Flogger.OFF;
			default :
				throw new RuntimeException("Could not find level: " + level);
		}
	}

	public static Level getSunLevel(int level) {
		switch (level) {
			case Flogger.ALL :
				return Level.ALL;
			case Flogger.FINEST :
				return Level.FINEST;
			case Flogger.FINER :
				return Level.FINER;
			case Flogger.FINE :
				return Level.FINE;
			case Flogger.CONFIG :
				return Level.CONFIG;
			case Flogger.INFO :
				return Level.INFO;
			case Flogger.WARNING :
				return Level.WARNING;
			case Flogger.SEVERE :
				return Level.SEVERE;
			case Flogger.OFF :
				return Level.OFF;
			default :
				throw new RuntimeException("Could not find level: " + level);
		}
	}

	@Override
	public Class<Logger> getLoggerType() {
		return Logger.class;
	}

	@Override
	public boolean canAdapt(Object innerLogger) {
		return innerLogger instanceof Logger;
	}

	@Override
	public void log(Logger innerLogger, int level, String txnId, String message, long timeOfLog, long now, StackTraceElement ste, Object extra) {
		innerLogger.log(getSunLevel(level), txnId + message, (Throwable) extra);
	}

	@Override
	public int getLevel(Logger innerLogger) {
		return getFloggerLevel(innerLogger.getLevel());
	}

	@Override
	public String getId(Logger innerLogger) {
		return innerLogger.getName();
	}

}
