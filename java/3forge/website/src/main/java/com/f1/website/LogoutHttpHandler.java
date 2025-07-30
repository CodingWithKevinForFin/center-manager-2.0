package com.f1.website;

import java.io.IOException;

import com.f1.base.Clock;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;

public class LogoutHttpHandler extends AbstractHttpHandler {

	final private Clock clock;

	public LogoutHttpHandler(Clock clock) {
		this.clock = clock;
	}

	public void handle(HttpRequestResponse request) throws IOException {
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		try {
			HttpSession session = request.getSession(true);
			WebsiteUser user = WebsiteUtils.getUser(session);
			if (user != null)
				manager.audit(request, user.getUser(), "LOGOUT", null);
			WebsiteUtils.setLoggedIn(session, false);
			WebsiteUtils.setUser(session, null);
			WebsiteUtils.setSpoofingAdmin(session, null);
			/*
						int port = request.getHttpServer().getUnsecurePort();
			
						if (port != -1) {
							String url = HttpUtils.buildUrl(false, request.getHost(), port, HttpUtils.getCanonical(request.getRequestUri(), "/logout.html"), "");
							request.sendRedirect(url);
						} else*/
			request.sendRedirect("logout.html");
			session.getAttributes().remove("sectoken");

			return;
		} catch (Exception e) {
			WebsiteUtils.handleException(request, e);
		}
	}

}
