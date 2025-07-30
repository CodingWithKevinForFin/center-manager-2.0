package com.f1.speedloggerSlf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.impl.SpeedLoggerInstance;

public class Slf4jSpeedLoggerManager implements ILoggerFactory {

	private SpeedLoggerManager inner;

	public Slf4jSpeedLoggerManager(SpeedLoggerManager inner) {
		this.inner = inner;
	}

	public Slf4jSpeedLoggerManager() {
		this(SpeedLoggerInstance.getInstance());
	}

	@Override
	public Logger getLogger(String name) {
		return new Slf4jSpeedLogger(name, inner.getLogger(name));
	}
}
