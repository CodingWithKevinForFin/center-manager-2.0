package com.f1.suite.web;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.f1.container.Partition;
import com.f1.container.PartitionController;
import com.f1.container.impl.AbstractConnectable;
import com.f1.http.HttpHandler;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.utils.LH;
import com.f1.utils.concurrent.LinkedHasherMap;

public abstract class HttpStateHandler extends AbstractConnectable implements HttpHandler {

	private static final String HTTP_SLOW_SESSION_LOCK_WARN_MS = "http.slow.session.lock.warn.ms";

	private static final int DEFAULT_TIMEOUT_SECONDS = 60;

	private HttpStateCreator stateCreator;

	private int lockWarnTime = 1000;

	private PartitionController pc;
	private Map<String, String> overriddenResponseHeaders;

	public HttpStateHandler(HttpStateCreator stateCreator) {
		this.stateCreator = stateCreator;
	}

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		addOverridenResponseHeaders(req);
		Map<String, String> params = req.getParams();
		final String pgid = params.get(BasicPortletManager.PAGEID);
		if (pgid == null) {
			error(req, "Missing pgid");
			return;
		}
		final HttpSession session = req.getSession(false);
		if (session == null) {
			error(req, "Missing session");
			return;
		}
		final WebStatesManager wsm = WebStatesManager.get(session);
		if (wsm == null) {
			req.getOutputStream().append("redirectToLogin();");
			req.setContentType("text/plain");
			return;
		}
		final WebState state = wsm.getState(pgid);
		if (state == null) {
			req.getOutputStream().append("redirectToLogin();");
			req.setContentType("text/plain");
			return;
		}
		Partition partition = state.getPartition();
		Object data;
		long lockRequestTime = System.currentTimeMillis();
		int stateTimeoutSeconds = getStateTimeoutSeconds();
		if (!partition.lockForWrite(stateTimeoutSeconds, TimeUnit.SECONDS)) {
			req.setResponseType(HttpRequestResponse.HTTP_500_SERVICE_ERROR);
			LH.info(log, "Session timeout (", stateTimeoutSeconds, " seconds): ", session == null ? "<no-session>" : (pgid + " (" + session.getDescription() + ")"));
		} else {
			long lockTime = System.currentTimeMillis() - lockRequestTime;
			if (lockTime >= lockWarnTime) {
				LH.info(log, "Session acquisition slow for ", pgid, "(", session.getDescription(), ")", ": ", lockTime, " ms");
			}
			try {
				HttpRequestAction request = nw(HttpRequestAction.class);
				request.setRequest(req);
				data = handle(request, state);
			} finally {
				partition.unlockForWrite();
			}
			handleAfterUnlock(req, data);
		}
	}
	private void error(HttpRequestResponse req, String message) {
		throw new RuntimeException(message + ": " + req.getRequestUrl());

	}

	@Override
	public void init() {
		lockWarnTime = getTools().getOptional(HTTP_SLOW_SESSION_LOCK_WARN_MS, lockWarnTime);
		super.init();
		this.pc = getContainer().getPartitionController();
	}

	public abstract Object handle(HttpRequestAction request, WebState state);
	public abstract void handleAfterUnlock(HttpRequestResponse req, Object data);

	public int getStateTimeoutSeconds() {
		return this.stateCreator == null ? BasicHttpStateCreator.SESSION_ACQUIRE_LOCK_TIMEOUT_SECONDS_DEFAULT : this.stateCreator.getAcquireLockTimeoutSeconds();
	}

	@Override
	public void putOverrideResponseHeader(String key, String value) {
		if (overriddenResponseHeaders == null)
			this.overriddenResponseHeaders = new LinkedHasherMap<String, String>();
		this.overriddenResponseHeaders.put(key, value);
	}

	@Override
	public String getOverrideResponseHeader(String key) {
		return this.overriddenResponseHeaders.get(key);
	}

	@Override
	public String removeOverrideResponseHeader(String key) {
		return this.overriddenResponseHeaders.remove(key);
	}

	private void addOverridenResponseHeaders(HttpRequestResponse req) {
		if (overriddenResponseHeaders == null)
			return;
		for (Entry<String, String> e : this.overriddenResponseHeaders.entrySet())
			req.putResponseHeader(e.getKey(), e.getValue());
	}

}
