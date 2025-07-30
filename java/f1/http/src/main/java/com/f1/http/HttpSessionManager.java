package com.f1.http;

import java.util.Collection;

import com.f1.utils.LocaleFormatter;

public interface HttpSessionManager {

	public HttpSession getHttpSession(HttpRequestResponse request);

	public HttpSession getOrCreateHttpSession(HttpRequestResponse request);

	public Object getSessionId(HttpRequestResponse request);

	public LocaleFormatter getDefaultFormatter();

	public void setDefaultFormatter(LocaleFormatter localeFormatter);

	public void addListener(HttpSessionManagerListener listener);
	public void removeListener(HttpSessionManagerListener listener);

	public void reapExpiredSessions();

	public void setSessionReaperPeriodMs(int periodMs);
	public int getSessionReaperPeriodMs();

	public void start();
	public void stop();

	boolean isRunning();

	public void setDefaultSessionTimeoutPeriodMs(long optional);
	public long getDefaultSessionTimeoutPeriodMs();

	public int getSessionsCount();

	public String getCookieName();
	public void setCookieName(String cookieName);

	void setAdditionalCookieOptions(String additionalCookieOptions);

	String getAdditionalCookieOptions();

	Collection<HttpSession> getSessions();

	HttpSession getHttpSession(String sessionId);
}
