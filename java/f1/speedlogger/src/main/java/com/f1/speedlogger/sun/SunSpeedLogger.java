/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.sun;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.utils.AH;

/**
 * adapts the sun logger to work with speed logger. see {@link SunSpeedLoggerLogManager} for details on configuration
 */
public class SunSpeedLogger extends Logger {

	private SpeedLogger inner;

	protected SunSpeedLogger(String name, String resourceBundleName, SpeedLogger logger) {
		super(name, resourceBundleName);
		this.inner = logger;
	}

	@Override
	public String getName() {
		return inner.getId();
	}

	@Override
	public void log(Level level, String msg) {
		inner.log(toSpeedLoggerLevel(level), msg);
	}

	@Override
	public void log(Level level, String msg, Object param1) {
		if (param1 == null)
			inner.log(toSpeedLoggerLevel(level), msg);
		else
			inner.log(toSpeedLoggerLevel(level), new Object[] { msg, param1 });
	}

	@Override
	public void log(Level level, String msg, Object params[]) {
		if (msg == null || msg.length() == 0)
			inner.log(toSpeedLoggerLevel(level), params);
		else if (params == null)
			inner.log(toSpeedLoggerLevel(level), msg);
		else
			inner.log(toSpeedLoggerLevel(level), AH.insert(params, 0, msg));
	}

	@Override
	public void log(Level level, String msg, Throwable thrown) {
		if (thrown == null)
			inner.log(toSpeedLoggerLevel(level), msg);
		else
			inner.log(toSpeedLoggerLevel(level), new Object[] { msg, thrown });
	}

	@Override
	public void severe(String msg) {
		inner.log(SpeedLoggerLevels.SEVERE, msg);

	}

	@Override
	public void warning(String msg) {
		inner.log(SpeedLoggerLevels.WARNING, msg);
	}

	@Override
	public void info(String msg) {
		inner.log(SpeedLoggerLevels.INFO, msg);

	}

	@Override
	public void config(String msg) {
		inner.log(SpeedLoggerLevels.CONFIG, msg);

	}

	@Override
	public void fine(String msg) {
		inner.log(SpeedLoggerLevels.FINE, msg);

	}

	@Override
	public void finer(String msg) {
		inner.log(SpeedLoggerLevels.FINER, msg);

	}

	@Override
	public void finest(String msg) {
		inner.log(SpeedLoggerLevels.FINEST, msg);
	}

	@Override
	public boolean isLoggable(Level level) {
		return toSpeedLoggerLevel(level) >= inner.getMinimumLevel();
	}

	@Override
	public Level getLevel() {
		return fromSpeedLoggerlevel(inner.getMinimumLevel());
	}

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

	public static int toSpeedLoggerLevel(Level level) {
		switch (level.intValue()) {
			case ALL:
				return SpeedLoggerLevels.ALL;
			case FINEST:
				return SpeedLoggerLevels.FINEST;
			case FINER:
				return SpeedLoggerLevels.FINER;
			case FINE:
				return SpeedLoggerLevels.FINE;
			case CONFIG:
				return SpeedLoggerLevels.CONFIG;
			case INFO:
				return SpeedLoggerLevels.INFO;
			case WARNING:
				return SpeedLoggerLevels.WARNING;
			case SEVERE:
				return SpeedLoggerLevels.SEVERE;
			case OFF:
				return SpeedLoggerLevels.OFF;
			default:
				throw new RuntimeException("Could not find level: " + level);
		}
	}

	public static Level fromSpeedLoggerlevel(int level) {
		switch (level) {
			case SpeedLoggerLevels.ALL:
				return Level.ALL;
			case SpeedLoggerLevels.FINEST:
				return Level.FINEST;
			case SpeedLoggerLevels.FINER:
				return Level.FINER;
			case SpeedLoggerLevels.FINE:
				return Level.FINE;
			case SpeedLoggerLevels.CONFIG:
				return Level.CONFIG;
			case SpeedLoggerLevels.INFO:
				return Level.INFO;
			case SpeedLoggerLevels.WARNING:
				return Level.WARNING;
			case SpeedLoggerLevels.SEVERE:
				return Level.SEVERE;
			case SpeedLoggerLevels.OFF:
				return Level.OFF;
			default:
				throw new RuntimeException("Could not find level: " + level);
		}

	}

	@Override
	public String toString() {
		return SunSpeedLogger.class.getSimpleName() + ": " + getName();
	}
}
