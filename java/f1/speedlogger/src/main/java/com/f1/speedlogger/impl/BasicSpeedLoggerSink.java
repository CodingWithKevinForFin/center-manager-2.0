/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerEventListener;
import com.f1.speedlogger.SpeedLoggerSink;
import com.f1.utils.AH;

public class BasicSpeedLoggerSink implements SpeedLoggerSink {

	final private long uid = SpeedLoggerUtils.generateUid();
	private final String id;
	private final Writer writer;
	private final Map<String, String> configuration;
	volatile private long bytesWritten;
	volatile private long calls;
	final private String description;

	public BasicSpeedLoggerSink(Map<String, String> configuration, String id, Writer writer, String description) {
		this.configuration = configuration;
		this.id = id;
		this.writer = writer;
		this.description = description;
	}

	@Override
	public void flush() {
		try {
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SpeedLoggerSink: " + id;
	}

	@Override
	public Map<String, String> getConfiguration() {
		return configuration;
	}

	@Override
	public void write(char[] data, int dataStart, int dataLength, int level, SpeedLogger loggerId, Object origMessage) throws IOException {
		writer.write(data, 0, dataLength);
		writer.flush();
		calls++;
		bytesWritten += data.length;
	}

	@Override
	public long getBytesWritten() {
		return bytesWritten;
	}

	@Override
	public long getLogCalls() {
		return calls;
	}

	@Override
	public long getUid() {
		return uid;
	}

	private SpeedLoggerEventListener[] listeners = SpeedLoggerUtils.EMPTY_LISTENER_ARRAY;

	@Override
	public void addSpeedLoggerEventListener(SpeedLoggerEventListener listener) {
		listeners = AH.append(listeners, listener);
	}

	@Override
	public SpeedLoggerEventListener[] getListeners() {
		return listeners;
	}

	@Override
	public String describe() {
		return description;
	}
}
