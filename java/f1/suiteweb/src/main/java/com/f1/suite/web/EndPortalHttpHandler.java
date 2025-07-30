package com.f1.suite.web;

import java.io.IOException;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.utils.LH;

public class EndPortalHttpHandler extends AbstractHttpHandler {

	private static final Logger log = LH.get();

	final private String redirectUrl;

	public EndPortalHttpHandler(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		super.handle(request);
		HttpSession session = request.getSession(false);
		WebStatesManager wsm = WebStatesManager.get(session);
		if (wsm == null || !wsm.isLoggedIn()) {
			request.sendRedirect("/");
			return;
		}
		String pgid = request.getParams().get(BasicPortletManager.PAGEID);
		WebState state = wsm.getState(pgid);
		if (state != null) {
			LH.info(log, "Closing state for user '", wsm.getUserName(), "': ", state.getPgId());
			state.killWebState();
			request.sendRedirect(redirectUrl);
		}
	}

}
