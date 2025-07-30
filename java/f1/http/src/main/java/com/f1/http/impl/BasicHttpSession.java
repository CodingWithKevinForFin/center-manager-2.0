package com.f1.http.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.http.HttpSession;
import com.f1.http.HttpSessionManager;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;

public class BasicHttpSession implements HttpSession {

	private static class NullSupportingConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

		public V put(K key, V value) {
			return value == null ? super.remove(key) : super.put(key, value);
		};
		public V putIfAbsent(K key, V value) {
			return value == null ? super.remove(key) : super.putIfAbsent(key, value);
		};

		@Override
		public void putAll(Map<? extends K, ? extends V> m) {
			for (java.util.Map.Entry<? extends K, ? extends V> v : m.entrySet())
				put(v.getKey(), v.getValue());
		}
	}

	private static final Logger log = LH.get();
	private ConcurrentMap<String, Object> attributes = new NullSupportingConcurrentHashMap<String, Object>();
	private long lastAccessTime;
	final private long startTime;
	private boolean isValid;
	private long timeout;
	final private Object sessionId;
	private LocaleFormatter formatter;
	final private BasicHttpSessionManager manager;
	private String description = null;

	@Override
	public ConcurrentMap<String, Object> getAttributes() {
		return attributes;
	}

	public BasicHttpSession(Object sessionId, long now, long timeOut, LocaleFormatter formatter, BasicHttpSessionManager manager) {
		if (formatter == null)
			throw new NullPointerException("formatter");
		this.startTime = this.lastAccessTime = now;
		this.isValid = true;
		this.sessionId = sessionId;
		this.timeout = timeOut;
		this.formatter = formatter;
		this.manager = manager;
	}

	@Override
	public long getLastAccess() {
		return lastAccessTime;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public void kill() {
		isValid = false;
		this.manager.onSessionClosed(this);
	}
	@Override
	public boolean isAlive() {
		return isValid;
	}

	@Override
	public void touch(long now) {
		if (now < this.lastAccessTime)
			LH.info(log, "Ignoring old touch before last access time: ", now, " < ", lastAccessTime);
		else
			this.lastAccessTime = now;
	}

	@Override
	public void setTimeout(long timeout, TimeUnit timeUnit) {
		this.timeout = timeUnit.toMillis(timeout);
	}

	@Override
	public long getTimeout() {
		return timeout;
	}

	@Override
	public Object getSessionId() {
		return sessionId;
	}

	@Override
	public LocaleFormatter getFormatter() {
		return formatter;
	}

	public void setFormatter(LocaleFormatter formatter) {
		if (formatter == null)
			throw new NullPointerException("formatter");
		this.formatter = formatter;
	}

	@Override
	public boolean hasExpired(long now) {
		return timeout != -1 && getLastAccess() + timeout < now;
	}

	@Override
	public HttpSessionManager getManager() {
		return this.manager;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
