package com.f1.speedloggerSlf4j;

import org.slf4j.Logger;
import org.slf4j.Marker;

import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.utils.AH;

public class Slf4jSpeedLogger implements Logger {

	private SpeedLogger logger;
	private String name;

	public Slf4jSpeedLogger(String name, SpeedLogger logger) {
		this.name = name;
		this.logger = logger;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isDebugEnabled() {
		return isLoggable(SpeedLoggerLevels.DEBUG);
	}
	@Override
	public boolean isDebugEnabled(Marker arg0) {
		return isLoggable(SpeedLoggerLevels.DEBUG, arg0);
	}
	@Override
	public void debug(String arg) {
		log(SpeedLoggerLevels.DEBUG, arg);
	}
	@Override
	public void debug(String arg0, Object arg1) {
		log(SpeedLoggerLevels.DEBUG, arg0, arg1);
	}
	@Override
	public void debug(String arg0, Object... arg1) {
		log(SpeedLoggerLevels.DEBUG, arg0, arg1);
	}
	@Override
	public void debug(String arg0, Throwable arg1) {
		log(SpeedLoggerLevels.DEBUG, arg0, arg1);
	}
	@Override
	public void debug(Marker arg0, String arg1) {
		log(SpeedLoggerLevels.DEBUG, arg0, arg1);
	}
	@Override
	public void debug(String arg0, Object arg1, Object arg2) {
		log(SpeedLoggerLevels.DEBUG, arg0, arg1, arg2);
	}
	@Override
	public void debug(Marker arg0, String arg1, Object arg2) {
		log(SpeedLoggerLevels.DEBUG, arg0, arg1, arg2);
	}
	@Override
	public void debug(Marker arg0, String arg1, Object... arg2) {
		log(SpeedLoggerLevels.DEBUG, arg0, arg1, arg2);
	}
	@Override
	public void debug(Marker arg0, String arg1, Throwable arg2) {
		log(SpeedLoggerLevels.DEBUG, arg0, arg1, arg2);
	}
	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {
		log(SpeedLoggerLevels.DEBUG, arg0, arg1, arg2);
	}

	//error
	@Override
	public boolean isErrorEnabled() {
		return isLoggable(SpeedLoggerLevels.ERROR);
	}
	@Override
	public boolean isErrorEnabled(Marker arg0) {
		return isLoggable(SpeedLoggerLevels.ERROR, arg0);
	}
	@Override
	public void error(String arg) {
		log(SpeedLoggerLevels.ERROR, arg);
	}
	@Override
	public void error(String arg0, Object arg1) {
		log(SpeedLoggerLevels.ERROR, arg0, arg1);
	}
	@Override
	public void error(String arg0, Object... arg1) {
		log(SpeedLoggerLevels.ERROR, arg0, arg1);
	}
	@Override
	public void error(String arg0, Throwable arg1) {
		log(SpeedLoggerLevels.ERROR, arg0, arg1);
	}
	@Override
	public void error(Marker arg0, String arg1) {
		log(SpeedLoggerLevels.ERROR, arg0, arg1);
	}
	@Override
	public void error(String arg0, Object arg1, Object arg2) {
		log(SpeedLoggerLevels.ERROR, arg0, arg1, arg2);
	}
	@Override
	public void error(Marker arg0, String arg1, Object arg2) {
		log(SpeedLoggerLevels.ERROR, arg0, arg1, arg2);
	}
	@Override
	public void error(Marker arg0, String arg1, Object... arg2) {
		log(SpeedLoggerLevels.ERROR, arg0, arg1, arg2);
	}
	@Override
	public void error(Marker arg0, String arg1, Throwable arg2) {
		log(SpeedLoggerLevels.ERROR, arg0, arg1, arg2);
	}
	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3) {
		log(SpeedLoggerLevels.ERROR, arg0, arg1, arg2);
	}

	//info
	@Override
	public boolean isInfoEnabled() {
		return isLoggable(SpeedLoggerLevels.INFO);
	}
	@Override
	public boolean isInfoEnabled(Marker arg0) {
		return isLoggable(SpeedLoggerLevels.INFO, arg0);
	}
	@Override
	public void info(String arg) {
		log(SpeedLoggerLevels.INFO, arg);
	}
	@Override
	public void info(String arg0, Object arg1) {
		log(SpeedLoggerLevels.INFO, arg0, arg1);
	}
	@Override
	public void info(String arg0, Object... arg1) {
		log(SpeedLoggerLevels.INFO, arg0, arg1);
	}
	@Override
	public void info(String arg0, Throwable arg1) {
		log(SpeedLoggerLevels.INFO, arg0, arg1);
	}
	@Override
	public void info(Marker arg0, String arg1) {
		log(SpeedLoggerLevels.INFO, arg0, arg1);
	}
	@Override
	public void info(String arg0, Object arg1, Object arg2) {
		log(SpeedLoggerLevels.INFO, arg0, arg1, arg2);
	}
	@Override
	public void info(Marker arg0, String arg1, Object arg2) {
		log(SpeedLoggerLevels.INFO, arg0, arg1, arg2);
	}
	@Override
	public void info(Marker arg0, String arg1, Object... arg2) {
		log(SpeedLoggerLevels.INFO, arg0, arg1, arg2);
	}
	@Override
	public void info(Marker arg0, String arg1, Throwable arg2) {
		log(SpeedLoggerLevels.INFO, arg0, arg1, arg2);
	}
	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3) {
		log(SpeedLoggerLevels.INFO, arg0, arg1, arg2);
	}

	//trace
	@Override
	public boolean isTraceEnabled() {
		return isLoggable(SpeedLoggerLevels.TRACE);
	}
	@Override
	public boolean isTraceEnabled(Marker arg0) {
		return isLoggable(SpeedLoggerLevels.TRACE, arg0);
	}
	@Override
	public void trace(String arg) {
		log(SpeedLoggerLevels.TRACE, arg);
	}
	@Override
	public void trace(String arg0, Object arg1) {
		log(SpeedLoggerLevels.TRACE, arg0, arg1);
	}
	@Override
	public void trace(String arg0, Object... arg1) {
		log(SpeedLoggerLevels.TRACE, arg0, arg1);
	}
	@Override
	public void trace(String arg0, Throwable arg1) {
		log(SpeedLoggerLevels.TRACE, arg0, arg1);
	}
	@Override
	public void trace(Marker arg0, String arg1) {
		log(SpeedLoggerLevels.TRACE, arg0, arg1);
	}
	@Override
	public void trace(String arg0, Object arg1, Object arg2) {
		log(SpeedLoggerLevels.TRACE, arg0, arg1, arg2);
	}
	@Override
	public void trace(Marker arg0, String arg1, Object arg2) {
		log(SpeedLoggerLevels.TRACE, arg0, arg1, arg2);
	}
	@Override
	public void trace(Marker arg0, String arg1, Object... arg2) {
		log(SpeedLoggerLevels.TRACE, arg0, arg1, arg2);
	}
	@Override
	public void trace(Marker arg0, String arg1, Throwable arg2) {
		log(SpeedLoggerLevels.TRACE, arg0, arg1, arg2);
	}
	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {
		log(SpeedLoggerLevels.TRACE, arg0, arg1, arg2);
	}
	//warn
	@Override
	public boolean isWarnEnabled() {
		return isLoggable(SpeedLoggerLevels.INFO);
	}
	@Override
	public boolean isWarnEnabled(Marker arg0) {
		return isLoggable(SpeedLoggerLevels.INFO, arg0);
	}
	@Override
	public void warn(String arg) {
		log(SpeedLoggerLevels.WARNING, arg);
	}
	@Override
	public void warn(String arg0, Object arg1) {
		log(SpeedLoggerLevels.WARNING, arg0, arg1);
	}
	@Override
	public void warn(String arg0, Object... arg1) {
		log(SpeedLoggerLevels.WARNING, arg0, arg1);
	}
	@Override
	public void warn(String arg0, Throwable arg1) {
		log(SpeedLoggerLevels.WARNING, arg0, arg1);
	}
	@Override
	public void warn(Marker arg0, String arg1) {
		log(SpeedLoggerLevels.WARNING, arg0, arg1);
	}
	@Override
	public void warn(String arg0, Object arg1, Object arg2) {
		log(SpeedLoggerLevels.WARNING, arg0, arg1, arg2);
	}
	@Override
	public void warn(Marker arg0, String arg1, Object arg2) {
		log(SpeedLoggerLevels.WARNING, arg0, arg1, arg2);
	}
	@Override
	public void warn(Marker arg0, String arg1, Object... arg2) {
		log(SpeedLoggerLevels.WARNING, arg0, arg1, arg2);
	}
	@Override
	public void warn(Marker arg0, String arg1, Throwable arg2) {
		log(SpeedLoggerLevels.WARNING, arg0, arg1, arg2);
	}
	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {
		log(SpeedLoggerLevels.WARNING, arg0, arg1, arg2);
	}

	//generic
	public void log(int level, String arg) {
		this.logger.log(level, arg);
	}
	public void log(int level, String arg0, Object arg1) {
		this.logger.log(level, new Object[] { arg0, arg1 });
	}
	public void log(int level, String arg0, Object[] arg1) {
		this.logger.log(level, AH.insert(arg1, 0, arg0));
	}
	public void log(int level, String arg0, Throwable arg1) {
		this.logger.log(level, new Object[] { arg0, arg1 });
	}
	public void log(int level, String arg0, Object arg1, Object arg2) {
		this.logger.log(level, new Object[] { arg0, arg1, arg2 });
	}
	private boolean isLoggable(int level) {
		return this.logger.getMinimumLevel() >= level;
	}

	//generic, marker version
	public void log(int level, Marker arg0, String arg1) {
		this.logger.log(level, arg1);
	}
	public void log(int level, Marker arg0, String arg1, Object arg2) {
		this.logger.log(level, new Object[] { arg1, arg2 });
	}
	public void log(int level, Marker arg0, String arg1, Object[] arg2) {
		this.logger.log(level, AH.insert(arg2, 0, arg1));
	}
	public void log(int level, Marker arg0, String arg1, Throwable arg2) {
		this.logger.log(level, new Object[] { arg1, arg2 });
	}
	public void log(int level, Marker arg0, String arg1, Object arg2, Object arg3) {
		this.logger.log(level, new Object[] { arg1, arg2, arg3 });
	}
	private boolean isLoggable(int level, Marker arg0) {
		return this.logger.getMinimumLevel() >= level;
	}
}
