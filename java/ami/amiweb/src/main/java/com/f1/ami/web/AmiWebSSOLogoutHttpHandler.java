package com.f1.ami.web;

import java.io.IOException;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.HttpWebSuite;
import com.f1.utils.LH;

public class AmiWebSSOLogoutHttpHandler extends AbstractHttpHandler {
	private static final Logger log = LH.get();
	private AmiWebSSOPlugin ssoPlugin;

	public AmiWebSSOLogoutHttpHandler(AmiWebSSOPlugin plugin) {
		this.ssoPlugin = plugin;
	}
	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		super.handle(req);
		String url = null;
		try {
			url = this.ssoPlugin.handleLogout(req);
		} catch (Exception e) {
			LH.warning(log, "Unexpected error handling " + this.ssoPlugin.getPluginId() + " logout request", e);
			req.getOutputStream().print("Critical Error Handling " + this.ssoPlugin.getPluginId() + " Logout Request.");
		}
		if (url == null)
			url = (String) req.getHttpServer().getAttributes().get(HttpWebSuite.ATTRIBUTE_LOGGED_OUT_URL);

		req.sendRedirect(url);
		// Session gets killed by the logout handler
	}

}
