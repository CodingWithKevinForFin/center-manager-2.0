/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.f1.speedlogger.SpeedLoggerSink;
import com.f1.speedlogger.SpeedLoggerSinkFactory;

/**
 * used to log information to a simple printstream or print writer. This by default is an async writer, meaning writes will be buffered until either size or time limit has been
 * reached.
 */
public class ConsoleSpeedLoggerSinkFactory implements SpeedLoggerSinkFactory {

	/**
	 * max number of milliseconds after a write(..) before flushing to {@link #out}
	 */
	public static final String OPTION_DELAYFLUSHMS = "delayFlushMs";
	final private Map<String, String> configuration;
	final private String id;
	final private PrintWriter out;

	public ConsoleSpeedLoggerSinkFactory(String id, PrintStream out) {
		this(id, new PrintWriter(out instanceof PrintStreamToSpeedLogger ? ((PrintStreamToSpeedLogger) out).getInner() : out));
	}

	public ConsoleSpeedLoggerSinkFactory(String id, PrintWriter out) {
		this.out = out;
		this.id = id;
		this.configuration = new HashMap<String, String>();
		this.configuration.put(OPTION_DELAYFLUSHMS, "-1");
	}

	@Override
	public Map<String, String> getConfiguration() {
		return configuration;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public SpeedLoggerSink createSink(String id, Map<String, String> configuration) {
		int delay = Integer.parseInt(configuration.get(OPTION_DELAYFLUSHMS));
		if (delay < 0)
			return new BasicSpeedLoggerSink(configuration, id, out, "Console");
		throw new RuntimeException("tod");
	}

}
