package com.f1.http.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Clock;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.HttpSessionManager;
import com.f1.http.HttpSessionManagerListener;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.GuidHelper;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class BasicHttpSessionManager implements HttpSessionManager {

	private static final Logger log = LH.get();

	private static final int DEFAULT_SESSION_REAPER_PERIOD_MS = 30000;
	private int reaperPeriodMs = DEFAULT_SESSION_REAPER_PERIOD_MS;
	private Clock clock;
	private ConcurrentMap<String, HttpSession> sessions = new CopyOnWriteHashMap<String, HttpSession>();
	private String cookieName;
	private long defaultTimeout;
	private LocaleFormatter defaultLocaleFormatter;

	final private String sessionPrefix;

	private String additionalCookieOptions;

	public BasicHttpSessionManager(Clock clock, String cookieName, long defaultTimeout, TimeUnit timeunit, LocaleFormatter defaultLocaleFormatter) {
		if (defaultLocaleFormatter == null)
			throw new NullPointerException("formatter");
		if (clock == null)
			throw new NullPointerException("clock");
		this.clock = clock;
		this.sessionPrefix = SH.substring(GuidHelper.getGuid(62), 0, 4);
		this.cookieName = cookieName;
		this.defaultTimeout = timeunit.toMillis(defaultTimeout);
		this.defaultLocaleFormatter = defaultLocaleFormatter;
	}

	@Override
	public HttpSession getHttpSession(HttpRequestResponse request) {
		return get(getSessionId(request));
	}
	@Override
	public HttpSession getHttpSession(String sessionId) {
		return sessions.get(sessionId);
	}

	private HttpSession get(String sessionId) {
		if (sessionId == null)
			return null;
		final HttpSession session = sessions.get(sessionId);
		if (session == null)
			return null;
		final long now = clock.getNow();
		session.touch(now);
		return session;
	}

	@Override
	public HttpSession getOrCreateHttpSession(HttpRequestResponse request) {
		String id = getSessionId(request);
		HttpSession r = get(id);
		if (r == null) {
			StringBuilder sink = new StringBuilder(50).append(sessionPrefix);
			GuidHelper.getGuid(62, sink);
			id = sink.toString();
			request.putCookie(cookieName, id, null, 0, getAdditionalCookieOptions());
			sessions.put(id, r = newSession(id, request));
		}
		return r;
	}

	private HttpSession newSession(String uid, HttpRequestResponse request) {
		if (log.isLoggable(Level.INFO))
			LH.info(log, "New Http Session from: ", request.getRemoteHost(), ": ", request.getRemotePort(), ". Assigning UID: ", uid);
		BasicHttpSession r = new BasicHttpSession(uid, clock.getNow(), defaultTimeout, defaultLocaleFormatter, this);
		fireNewSession(r);
		return r;
	}

	private void fireNewSession(BasicHttpSession r) {
		for (HttpSessionManagerListener listener : listeners) {
			try {
				listener.onNewSession(this, r);
			} catch (Exception e) {
				LH.warning(log, "listener threw exception on new session: ", e);
			}
		}
	}
	private void fireSessionExpired(HttpSession session) {
		for (HttpSessionManagerListener listener : listeners) {
			try {
				listener.onSessionExpired(this, session);
			} catch (Exception e) {
				LH.warning(log, "listener threw exception on session expired: ", e);
			}
		}
	}
	private void fireSessionReaperRan(long now) {
		for (HttpSessionManagerListener listener : listeners) {
			try {
				listener.onSessionReaperRan(this, now);
			} catch (Exception e) {
				LH.warning(log, "listener threw exception on session expired: ", e);
			}
		}
	}

	protected void onSessionClosed(BasicHttpSession session) {
		this.sessions.remove(session.getSessionId());
		for (HttpSessionManagerListener listener : listeners) {
			try {
				listener.onSessionClosed(this, session);
			} catch (Exception e) {
				LH.warning(log, "listener threw exception on session closed: ", e);
			}
		}
	}

	@Override
	public String getSessionId(HttpRequestResponse request) {
		final String r = request.getCookies().get(cookieName);
		return r != null && r.startsWith(this.sessionPrefix) ? r : null;
	}

	@Override
	public LocaleFormatter getDefaultFormatter() {
		return defaultLocaleFormatter;
	}

	@Override
	public void setDefaultFormatter(LocaleFormatter localeFormatter) {
		this.defaultLocaleFormatter = localeFormatter;
	}

	private HttpSessionManagerListener[] listeners = new HttpSessionManagerListener[0];

	@Override
	synchronized public void addListener(HttpSessionManagerListener listener) {
		this.listeners = AH.append(this.listeners, listener);
	}

	@Override
	synchronized public void removeListener(HttpSessionManagerListener listener) {
		this.listeners = AH.remove(this.listeners, listener);
	}

	@Override
	public void reapExpiredSessions() {
		final long now = this.clock.getNow();
		List<HttpSession> toRemove = null;
		for (HttpSession session : this.sessions.values()) {
			if (session.hasExpired(now)) {
				if (toRemove == null)
					toRemove = new ArrayList<HttpSession>();
				toRemove.add(session);
			}
		}
		if (toRemove != null) {
			LH.info(log, "Http Session Reaper found ", CH.size(toRemove), " out of ", this.sessions.size(), " session(s) for removal");
			for (HttpSession remove : toRemove) {
				fireSessionExpired(remove);
				remove.kill();
			}
		}
		fireSessionReaperRan(now);
	}

	@Override
	public void setSessionReaperPeriodMs(int periodMs) {
		this.reaperPeriodMs = periodMs;
	}

	@Override
	public int getSessionReaperPeriodMs() {
		return reaperPeriodMs;
	}

	public class ReaperRunner implements Runnable {

		@Override
		public void run() {
			while (isRunning()) {
				try {
					reapExpiredSessions();
				} catch (Throwable t) {
					LH.warning(log, "Error reaping", t);
				}
				OH.sleep(reaperPeriodMs);
			}
		}

	}

	private Thread sessionReaperThread;

	private boolean running;

	@Override
	public void start() {
		this.sessionReaperThread = new Thread(new ReaperRunner(), "Session Reaper");
		this.sessionReaperThread.start();

		if (running)
			throw new IllegalStateException("already running");
		this.running = true;
	}

	@Override
	public void stop() {
		if (!running)
			throw new IllegalStateException("not running");
		running = false;
	}
	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public void setDefaultSessionTimeoutPeriodMs(long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	@Override
	public long getDefaultSessionTimeoutPeriodMs() {
		return this.defaultTimeout;
	}

	@Override
	public int getSessionsCount() {
		return this.sessions.size();
	}

	@Override
	public Collection<HttpSession> getSessions() {
		return this.sessions.values();
	}

	@Override
	public String getCookieName() {
		return this.cookieName;
	}

	@Override
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	@Override
	public String getAdditionalCookieOptions() {
		return additionalCookieOptions;
	}

	@Override
	public void setAdditionalCookieOptions(String additionalCookieOptions) {
		this.additionalCookieOptions = additionalCookieOptions;
	}

}
