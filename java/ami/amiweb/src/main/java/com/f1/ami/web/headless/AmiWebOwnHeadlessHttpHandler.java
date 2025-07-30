package com.f1.ami.web.headless;

import java.io.IOException;

import com.f1.ami.web.auth.AmiWebStatesManager;
import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.WebStatesManager;
import com.f1.suite.web.portal.impl.BasicPortletManager;

public class AmiWebOwnHeadlessHttpHandler extends AbstractHttpHandler {

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		super.handle(req);
		String pgId = req.getParams().get(BasicPortletManager.PAGEID);
		if (pgId == null)
			throw new RuntimeException("pgid not found");
		AmiWebStatesManager wsm = (AmiWebStatesManager) WebStatesManager.get(req.getSession(false));
		if (wsm == null)
			throw new RuntimeException("wms not found");
		if (!wsm.isDev())
			throw new RuntimeException("ISDEV not true");
		AmiWebHeadlessManager hm = wsm.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
		AmiWebHeadlessSession session = hm.findSessionByPgid(pgId);
		if (session == null)
			throw new RuntimeException("pgid not found: " + pgId);
		AmiWebHeadlessWebState ws = session.getWebState();
		ws.removeMeFromManager();
		ws.setWebStatesManager(wsm);
		wsm.putState(session.getWebState());
		req.sendRedirect(BasicPortletManager.URL_PORTAL + "?" + BasicPortletManager.PAGEID + "=" + pgId);
	}

}
