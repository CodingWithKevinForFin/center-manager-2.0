package com.f1.ami.web;

import java.util.regex.Pattern;

import com.f1.ami.amicommon.rest.AmiRestSessionAuth;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.ami.web.auth.BasicAmiAuthUser;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.impl.BasicHttpServer;
import com.f1.suite.web.WebStatesManager;

public class AmiWebHttpServer extends BasicHttpServer implements AmiRestSessionAuth {
	private static final Pattern POLLING_PATTERN = Pattern.compile(".*ASCII:\r\nportletManager.onSeqnum\\('\\d+'\\);\r\nportletManager.onJsProcessed\\(null\\);\r\n$",
			Pattern.DOTALL);
	private boolean debugPolling;

	@Override
	public void debug(HttpRequestResponse httpReqRes, String description, String reqText, String resText) {
		if (debugPolling || !POLLING_PATTERN.matcher(resText).matches())
			super.debug(httpReqRes, description, reqText, resText);
	}

	public void setDebugPolling(boolean b) {
		this.debugPolling = b;
	}

	@Override
	public AmiAuthUser getUser(HttpRequestResponse req) {
		HttpSession session = req.getSession(false);
		if (session == null)
			return null;
		WebStatesManager states = WebStatesManager.get(session);
		if (states == null)
			return null;
		return new BasicAmiAuthUser(states.getUserName(), states.getUserAttributes());
	}

}
