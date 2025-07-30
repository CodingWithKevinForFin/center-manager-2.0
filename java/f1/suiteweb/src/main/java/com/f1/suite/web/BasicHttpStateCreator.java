package com.f1.suite.web;

import java.util.logging.Logger;

import com.f1.container.Partition;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.utils.CH;
import com.f1.utils.GuidHelper;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

public class BasicHttpStateCreator implements HttpStateCreator {
	public static final Logger log = LH.get();
	public static final String SESSION_PARAM_SESSION_ACQUIRE_LOCK_TIMEOUT_SECONDS = "SESSION_TIMEOUT_SECONDS";
	public static final int SESSION_ACQUIRE_LOCK_TIMEOUT_SECONDS_DEFAULT = 180;

	final private int sessiontTimeoutSeconds;

	public BasicHttpStateCreator(int sessionTimeoutSeconds) {
		this.sessiontTimeoutSeconds = sessionTimeoutSeconds;
	}
	public BasicHttpStateCreator() {
		this.sessiontTimeoutSeconds = SESSION_ACQUIRE_LOCK_TIMEOUT_SECONDS_DEFAULT;
	}
	@Override
	public WebState createState(HttpRequestResponse req, Partition partition, WebStatesManager wsm, String pgid) {
		HttpSession session = req.getSession(true);
		WebState state = newState(wsm, pgid);
		state.touch(System.currentTimeMillis());
		partition.putState(state);
		wsm.setRemoteAddress(req.getRemoteHost());
		wsm.putState(state);
		session.getAttributes().put(SESSION_PARAM_SESSION_ACQUIRE_LOCK_TIMEOUT_SECONDS, this.sessiontTimeoutSeconds);
		return state;
	}
	protected WebState newState(WebStatesManager wsm, String pgid) {
		return new WebState(wsm, pgid);
	}

	public static int getSessionAcquireLockTimeoutSeconds(HttpSession session) {
		return CH.getOr(Caster_Integer.INSTANCE, session.getAttributes(), SESSION_PARAM_SESSION_ACQUIRE_LOCK_TIMEOUT_SECONDS, SESSION_ACQUIRE_LOCK_TIMEOUT_SECONDS_DEFAULT);
	}
	@Override
	public int getAcquireLockTimeoutSeconds() {
		return this.sessiontTimeoutSeconds;
	}
	@Override
	public String nextPgId() {
		GuidHelper gh = new GuidHelper();
		StringBuilder tmpSb = new StringBuilder();
		gh.getRandomGUID(62, SH.clear(tmpSb));
		SH.shuffle(tmpSb, gh.getRandom());
		return SH.substring(tmpSb, 0, 20);
	}
}
