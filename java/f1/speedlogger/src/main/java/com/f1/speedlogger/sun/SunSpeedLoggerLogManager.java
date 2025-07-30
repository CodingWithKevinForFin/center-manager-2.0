/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.sun;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.impl.SpeedLoggerInstance;

/**
 * 
 * used to replace the default sun logger manager in order to allow the speed logger to receive log events from the java.util.logging framwork. To use this manager, simply add the
 * following option to the JVM:
 * 
 * -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager
 * 
 */

public class SunSpeedLoggerLogManager extends LogManager {
	private ConcurrentMap<String, SunSpeedLogger> loggers = new ConcurrentHashMap<String, SunSpeedLogger>();
	private SpeedLoggerManager manager;

	public SunSpeedLoggerLogManager(SpeedLoggerManager manager) {
		this.manager = manager;
	}

	public SunSpeedLoggerLogManager() {
		this(SpeedLoggerInstance.getInstance());
	}

	@Override
	public Logger getLogger(String name) {
		SunSpeedLogger r = loggers.get(name);
		if (r == null) {
			SunSpeedLogger existing = loggers.putIfAbsent(name, r = new SunSpeedLogger(name, null, manager.getLogger(name)));
			if (existing != null)
				r = existing;
		}
		return r;
	}

}
