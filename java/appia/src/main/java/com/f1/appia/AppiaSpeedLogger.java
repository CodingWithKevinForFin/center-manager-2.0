package com.f1.appia;

import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.impl.SpeedLoggerInstance;
import com.javtech.appia.javatoolkit.middleware.Logger;

public class AppiaSpeedLogger extends Logger {

	static {
		System.err.println("CREATING APPIA-SPEED LOGGER ");
	}
	private static final String APPIA_READ_PREFIX = "APPIA_READ ";
	private static final String WRITE_PREFIX = "APPAI_WRITE ";
	private static final int WRITE_LEVEL = SpeedLoggerLevels.INFO;
	private static final int READ_LEVEL = SpeedLoggerLevels.INFO;

	final private SpeedLogger inner;
	final private String toString;

	public AppiaSpeedLogger(String name) {
		System.err.println("CREATING APPIA-SPEED LOGGER INSTANCE: " + name);
		this.inner = SpeedLoggerInstance.getInstance().getLogger(name);
		this.toString = AppiaSpeedLogger.class.getSimpleName() + ":" + name;
	}

	@Override
	public String toString() {
		return toString;
	}

	@Override
	public void debug(String text) {
		inner.log(SpeedLoggerLevels.DEBUG, text);
	}

	@Override
	public void error(String text) {
		inner.log(SpeedLoggerLevels.ERROR, text);
	}

	@Override
	public void exception(Throwable thrown) {
		inner.log(SpeedLoggerLevels.SEVERE, thrown);

	}

	@Override
	public void read(String text) {
		inner.log(READ_LEVEL, new Object[] { APPIA_READ_PREFIX, text });
	}

	@Override
	public void trace(String text) {
		inner.log(SpeedLoggerLevels.TRACE, text);

	}

	@Override
	public void warning(String text) {
		inner.log(SpeedLoggerLevels.WARNING, text);
	}

	@Override
	public void write(String text) {
		inner.log(WRITE_LEVEL, new Object[] { WRITE_PREFIX, text });
	}

	@Override
	public boolean isLog(int mask) {
		return true;
	}
}
