package com.f1.http;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.f1.utils.LocaleFormatter;

public interface HttpSession {

	public ConcurrentMap<String, Object> getAttributes();

	public long getLastAccess();

	public long getStartTime();

	public void kill();

	boolean isAlive();

	public void touch(long now);

	public void setTimeout(long timeout, TimeUnit timeUnit);

	public long getTimeout();

	public Object getSessionId();

	public LocaleFormatter getFormatter();

	public void setFormatter(LocaleFormatter formatter);

	public boolean hasExpired(long now);

	public HttpSessionManager getManager();

	public String getDescription();

}
