/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerAppender;
import com.f1.speedlogger.SpeedLoggerEventListener;
import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.SpeedLoggerSink;
import com.f1.speedlogger.SpeedLoggerStream;

public class BasicSpeedLoggerStream implements SpeedLoggerStream {

	static final ThreadLocal<BufferedWriter> threadLocalBuffers = new ThreadLocal<BufferedWriter>();

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	final private int minimumLevel;
	final private String id;
	private boolean running = true;
	final private SpeedLoggerSink sink;
	final private SpeedLoggerAppender appender;

	final private SpeedLoggerManager manager;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getMinimumLevel() {
		return minimumLevel;
	}

	@Override
	public boolean getRequiresStackTrace() {
		return appender.getRequiresStackTrace();
	}

	@Override
	public boolean getRequiresTimeMs() {
		return appender.getRequiresTimeMs();
	}

	@Override
	public void log(Object msg, int level, SpeedLogger logger, long timeMs, StackTraceElement stackTrace) {
		if (!running)
			return;
		BufferedWriter writer = threadLocalBuffers.get();
		if (writer == null)
			threadLocalBuffers.set(writer = new BufferedWriter(DEFAULT_BUFFER_SIZE));
		try {
			appender.append(writer, msg, level, logger, timeMs, stackTrace);
			char[] data = writer.getInner();
			int size = writer.getSize();
			sink.write(data, 0, size, level, logger, msg);

			SpeedLoggerEventListener[] listeners = sink.getListeners();
			for (int i = 0; i < listeners.length; i++)
				listeners[i].onlogEvent(sink, logger, data, 0, size, level, msg, timeMs, stackTrace);

			listeners = logger.getListeners();
			for (int i = 0; i < listeners.length; i++)
				listeners[i].onlogEvent(sink, logger, data, 0, size, level, msg, timeMs, stackTrace);

			listeners = manager.getListeners();
			for (int i = 0; i < listeners.length; i++)
				listeners[i].onlogEvent(sink, logger, data, 0, size, level, msg, timeMs, stackTrace);

		} catch (Exception e) {
			System.err.println("Exception while logging so shutting down " + this);
			e.printStackTrace(System.err);
			running = false;
		}
		writer.clear(2048);
	}

	@Override
	public String getAppenderId() {
		return appender.getId();
	}

	@Override
	public String getSinkId() {
		return sink.getId();
	}

	public BasicSpeedLoggerStream(String id, SpeedLoggerManager manager, SpeedLoggerAppender appender, SpeedLoggerSink sink, int minimumLevel) {
		this.id = id;
		this.manager = manager;
		this.appender = appender;
		this.sink = sink;
		this.minimumLevel = minimumLevel;
	}

	@Override
	public String toString() {
		return "SpeedLoggerStream: " + id + "( sink=" + sink.getId() + " appender=" + appender.getId() + ")";
	}

	@Override
	public String describe() {
		return getAppenderId() + ';' + getSinkId() + ';' + SpeedLoggerUtils.getLevelAsString(getMinimumLevel());
	}

}
