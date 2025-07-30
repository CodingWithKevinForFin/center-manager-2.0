/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.f1.speedlogger.SpeedLogger2Streams;
import com.f1.speedlogger.SpeedLoggerEventListener;
import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.SpeedLoggerStream;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class BasicSpeedLogger implements SpeedLogger2Streams {
	public static final char ID_SEPERATOR = '.';

	final private long uid = SpeedLoggerUtils.generateUid();
	private final String id;
	private final SpeedLoggerManager manager;
	private volatile BasicSpeedLoggerConfig config;
	private boolean started = false;

	public BasicSpeedLogger(String id, SpeedLoggerManager manager) {
		this.manager = manager;
		this.id = id;
		this.config = new BasicSpeedLoggerConfig(Collections.EMPTY_LIST);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getMinimumLevel() {
		return config.minimumLevel;
	}

	@Override
	public void log(int level, Object msg) {
		if (!started) {
			if (manager.isStarted())
				started = true;
			else {
				manager.prelog(this, level, msg);
			}
		}
		BasicSpeedLoggerConfig c = config;
		if (level >= c.minimumLevel) {
			StackTraceElement st = level >= c.minLevelNeedsStackTrace ? getStackTraceElement() : null;
			long timeMs = level >= c.minLevelNeedsTime ? EH.currentTimeMillis() : 01;
			for (SpeedLoggerStream stream : c.streams) {
				if (stream.getMinimumLevel() > level)
					break;
				stream.log(msg, level, this, timeMs, st);
			}
		} 
		else
			for (int i = 0; i < listeners.length; i++)
				listeners[i].onlogEvent(null, this, null, 0, 0, level, msg, -1, null);

	}

	@Override
	public void addStreams(Collection<SpeedLoggerStream> sinks) {
		if (sinks == null || sinks.size() == 0)
			return;
		synchronized (this) {
			Map<String, SpeedLoggerStream> m = new HashMap<String, SpeedLoggerStream>();
			int origMinLevel = config.minimumLevel;
			for (SpeedLoggerStream sls : config.streams)
				CH.putOrThrow(m, sls.getSinkId(), sls);
			for (SpeedLoggerStream sls : sinks) {
				SpeedLoggerStream existing = m.get(sls.getSinkId());
				if (existing == null || sls.getId().startsWith(existing.getId()))
					m.put(sls.getSinkId(), sls);
			}
			this.config = new BasicSpeedLoggerConfig(m.values());
			if (origMinLevel != this.config.minimumLevel)
				for (int i = 0; i < listeners.length; i++)
					listeners[i].onLoggerMinLevelChanged(this, origMinLevel, getMinimumLevel());

		}

	}

	@Override
	synchronized public Collection<SpeedLoggerStream> getStreams() {
		return config.streams;
	}

	@Override
	public String toString() {
		return "SpeedLogger: " + id;
	}

	static public StackTraceElement getStackTraceElement() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 2; i < stackTrace.length; i++) {
			StackTraceElement st = stackTrace[i];
			String name = st.getClassName();
			if (!SH.startsWith(name, "com.f1.speedlogger") && name != LH.class.getName())
				return st;
		}
		return stackTrace.length > 1 ? stackTrace[2] : null;
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
}
