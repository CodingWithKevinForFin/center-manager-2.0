package com.f1.speedloggerLog4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.net.Priority;

import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerLevels;

public class Log4jSpeedLogger {

	private SpeedLogger inner;

	protected Log4jSpeedLogger(String name, SpeedLogger inner) {

		this.inner = inner;

	}

	public boolean isEnabledFor(Level level) {

		return mapPriority(level) >= inner.getMinimumLevel();

	}

	public void log(Level level, Object message, Throwable t) {

		if (t == null)

			inner.log(mapPriority(level), message);

		else

			inner.log(mapPriority(level), new Object[] { message, t });

	}

	public void log(String callerClassName, Level level, Object message, Throwable t) {

		inner.log(mapPriority(level), new Object[] { message, t });

	}

	public void log(Level level, Object message) {

		inner.log(mapPriority(level), message);

	}
	// Priority is now Level
	private int mapPriority(final Level lvl) {
		switch (lvl.getStandardLevel()) {
			case TRACE:
				return SpeedLoggerLevels.TRACE;
			case ALL:
				return SpeedLoggerLevels.ALL;
			case DEBUG:
				return SpeedLoggerLevels.DEBUG;
			case ERROR:
				return SpeedLoggerLevels.ERROR;
			case FATAL:
				return SpeedLoggerLevels.FATAL;
			case INFO:
				return SpeedLoggerLevels.INFO;
			case OFF:
				return SpeedLoggerLevels.OFF;
			case WARN:
				return SpeedLoggerLevels.WARNING;

		}
		return lvl.intLevel();

	}
	@Deprecated
	private int mapPriority(final Priority priority) {
		return -1;
	}

	public void trace(Object message) {

		inner.log(SpeedLoggerLevels.TRACE, message);

	}

	public void trace(Object message, Throwable t) {

		if (t == null)

			inner.log(SpeedLoggerLevels.TRACE, message);

		else

			inner.log(SpeedLoggerLevels.TRACE, new Object[] { message, t });

	}

	public boolean isTraceEnabled() {

		return inner.getMinimumLevel() <= SpeedLoggerLevels.TRACE;

	}

	public void debug(Object message) {

		inner.log(SpeedLoggerLevels.DEBUG, message);

	}

	public void debug(Object message, Throwable t) {

		inner.log(SpeedLoggerLevels.DEBUG, message);

	}

	public boolean isDebugEnabled() {

		return inner.getMinimumLevel() <= SpeedLoggerLevels.DEBUG;

	}

	public void error(Object message) {

		inner.log(SpeedLoggerLevels.ERROR, message);

	}

	public void error(Object message, Throwable t) {

		if (t == null)

			inner.log(SpeedLoggerLevels.ERROR, message);

		else

			inner.log(SpeedLoggerLevels.ERROR, new Object[] { message, t });

	}

	public void fatal(Object message) {

		inner.log(SpeedLoggerLevels.FATAL, message);

	}

	public void fatal(Object message, Throwable t) {

		if (t == null)

			inner.log(SpeedLoggerLevels.FATAL, message);

		else

			inner.log(SpeedLoggerLevels.FATAL, new Object[] { message, t });

	}

	public boolean isInfoEnabled() {

		return inner.getMinimumLevel() <= SpeedLoggerLevels.INFO;

	}

	public void info(Object message) {

		inner.log(SpeedLoggerLevels.INFO, message);

	}

	public void info(Object message, Throwable t) {

		if (t == null)

			inner.log(SpeedLoggerLevels.INFO, message);

		else

			inner.log(SpeedLoggerLevels.INFO, new Object[] { message, t });

	}

	public void warn(Object message) {

		inner.log(SpeedLoggerLevels.WARNING, message);

	}

	public void warn(Object message, Throwable t) {

		if (t == null)

			inner.log(SpeedLoggerLevels.WARNING, message);

		else

			inner.log(SpeedLoggerLevels.WARNING, new Object[] { message, t });

	}

	public static void main(String[] args) {
		//		Log4jSpeedLogger sl = new Log4jSpeedLogger("Tom", new BasicSpeedLogger("ad", new BasicSpeedLoggerManager()));
		//		int mapPriority1 = sl.mapPriority(Level.TRACE);
		//		int mapPriority2 = sl.mapPriority(Level.ALL);
		//		int mapPriority3 = sl.mapPriority(Level.DEBUG);
		//		int mapPriority4 = sl.mapPriority(Level.ERROR);
		//		int mapPriority5 = sl.mapPriority(Level.FATAL);
		//		int mapPriority6 = sl.mapPriority(Level.INFO);
		//		int mapPriority7 = sl.mapPriority(Level.OFF);
		//		int mapPriority8 = sl.mapPriority(Level.WARN);
		//		System.out.println(mapPriority1);
		//		System.out.println(mapPriority2);
		//		System.out.println(mapPriority3);
		//		System.out.println(mapPriority4);
		//		System.out.println(mapPriority5);
		//		System.out.println(mapPriority6);
		//		System.out.println(mapPriority7);
		//		System.out.println(mapPriority8);
	}

}
