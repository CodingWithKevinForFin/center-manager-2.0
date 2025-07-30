package com.f1.suite.web;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.f1.base.Caster;
import com.f1.http.HttpSession;
import com.f1.http.impl.BasicHttpSession;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;

public class WebStatesManager {

	//	private LocaleFormatter localFormatter;
	private boolean loggedIn = false;
	private long loginTime;
	private String remoteAddress = null;
	private WebUser webUser;
	private HttpSession session;
	private Map<String, Object> userAttributes = Collections.EMPTY_MAP;
	final private ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();
	public static final String ID = "WEBSTATES";

	public static WebStatesManager getOrThrow(HttpSession session) {
		return (WebStatesManager) CH.getOrThrow(session.getAttributes(), ID);
	}
	public static WebStatesManager get(HttpSession session) {
		return session == null ? null : (WebStatesManager) session.getAttributes().get(ID);
	}
	//	public static WebStatesManager getOrCreate(HttpSession session) {
	//		Object r = session.getAttributes().get(ID);
	//		if (r == null) {
	//			r = new WebStatesManager(session);
	//			Object existing = session.getAttributes().putIfAbsent(ID, r);
	//			if (existing != null)
	//				r = existing;
	//		}
	//		return (WebStatesManager) r;
	//	}

	public WebStatesManager(HttpSession session) {
		this.session = session;
	}

	private ConcurrentMap<String, WebState> webstates = new CopyOnWriteHashMap<String, WebState>();
	private AtomicInteger modcount = new AtomicInteger(1);

	public WebState getState(String pgId) {
		return this.webstates.get(pgId);
	}
	private WebState getStateOrThrow(String pgId) {
		return CH.getOrThrow(this.webstates, pgId);
	}

	public void putState(WebState state) {
		CH.putOrThrow(this.webstates, state.getPgId(), state);
		modcount.incrementAndGet();
	}

	public WebState removeState(String pgId) {
		WebState r = this.webstates.remove(pgId);
		if (r != null)
			modcount.incrementAndGet();
		return r;
	}

	public int getPgIdsCount() {
		return this.webstates.size();
	}
	public Set<String> getPgIds() {
		return this.webstates.keySet();
	}
	public static WebState getOrThrow(HttpSession session, String pgId) {
		if (pgId == null)
			throw new NullPointerException(BasicPortletManager.PAGEID);
		WebStatesManager wsm = getOrThrow(session);
		return wsm.getStateOrThrow(pgId);
	}

	//	public void reset() {
	//		this.loggedIn = false;
	//		getAttributes().clear();
	//		setUser(null);
	//		getUserAttributes().clear();
	//	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void logIn(long now) {
		loggedIn = true;
		this.loginTime = now;
	}

	//	public LocaleFormatter getFormatter() {
	//		return session.getFormatter();
	//	}

	//	public void setFormatter(LocaleFormatter localeFormatter) {
	//		this.localFormatter = localeFormatter;
	//	}

	public WebUser getUser() {
		return webUser;
	}

	public void setUser(WebUser user) {
		this.webUser = user;
		if (user != null && session instanceof BasicHttpSession)
			((BasicHttpSession) this.session).setDescription(user.getUserName());
	}

	public long getLoginTime() {
		return this.loginTime;
	}
	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public void setUserAttributes(Map<String, Object> attributes) {
		this.userAttributes = Collections.unmodifiableMap(attributes);
	}

	public Map<String, Object> getUserAttributes() {
		return userAttributes;
	}
	public HttpSession getSession() {
		return this.session;
	}

	public void setSession(HttpSession session) {
		this.session = null;
	}
	public String getUserName() {
		return this.webUser == null ? null : this.webUser.getUserName();
	}
	public <T> T getUserAttribute(Caster<T> instance, String key, T dflt) {
		Object a = this.userAttributes.get(key);
		if (a == null)
			return dflt;
		T r = instance.cast(a, false, false);
		return r == null ? dflt : r;
	}
	public void incrementModCount() {
		this.modcount.incrementAndGet();
	}

	public int getModCount() {
		return this.modcount.get();
	}
	public static WebState get(HttpSession session, String pgid) {
		WebStatesManager wsm = get(session);
		return wsm.getState(pgid);
	}
	public boolean canAddSession() {
		return true;
	}
	public void onHttpSessionClosed() {
	}
	public ConcurrentMap<String, Object> getAttributes() {
		return attributes;
	}

}
