package com.f1.website;

import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.HttpUtils;
import com.f1.utils.LH;

public class SpoofHttpHandler extends AbstractSecureHttpHandler {
	private static final Logger log = LH.get();

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		HttpSession session = request.getSession(true);
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		TfWebsiteDbService db = manager.getDb();

		switch (user.getStatus()) {
			case WebsiteUser.STATUS_ADMIN:

				WebsiteUtils.setLoggedIn(session, true);
				int port = request.getHttpServer().getSecurePort();
				String username = (String) request.getParams().get("username");
				WebsiteUser spoofingUser = WebsiteUtils.getUser(manager, username, config.getClock());
				if (spoofingUser == null) {
					WebsiteUtils.redirectToLogin(request, user.getUserName(), "", "Unknown user: " + username);
					return;
				}
				manager.audit(request, user.getUser(), "SPOOFING_TO", spoofingUser.getUserName());
				manager.audit(request, spoofingUser.getUser(), "SPOOFING_FROM", user.getUserName());
				WebsiteUtils.setUser(session, spoofingUser);
				WebsiteUtils.populateFiles(manager, spoofingUser, "modified");
				WebsiteUtils.populateLicenses(manager, spoofingUser);
				WebsiteUtils.setSpoofingAdmin(session, user);
				String url = HttpUtils.buildUrl(true, request.getHost(), port, HttpUtils.getCanonical(request.getRequestUri(), "dashboard.htm"), "");
				request.sendRedirect(url);
				return;
			default: {
				WebsiteUtils.setLoggedIn(session, false);
				WebsiteUtils.setUser(session, null);
				WebsiteUtils.setSpoofingAdmin(session, null);
				WebsiteUtils.redirectToLogin(request, user.getUserName(), "", "bad status: " + user.getStatus());
			}
		}

	}
}
