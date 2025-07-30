package com.f1.ami.web;

import java.io.IOException;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.HttpWebSuite;
import com.f1.utils.LH;

public class AmiWebLogoutHttpHandler extends AbstractHttpHandler {
	private static final Logger log = LH.get();
	private AmiWebSSOPlugin ssoPlugin;

	public AmiWebLogoutHttpHandler() {
	}

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		super.handle(req);
		HttpSession session = req.getSession(false);
		if (session != null)
			session.kill();
		String url = (String) req.getHttpServer().getAttributes().get(HttpWebSuite.ATTRIBUTE_LOGGED_OUT_URL);
		req.sendRedirect(url);
	}

}
