package com.f1.website;

import java.io.IOException;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.OH;

public abstract class AbstractSecureHttpHandler extends AbstractHttpHandler {

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		super.handle(request);
		if (!request.getIsSecure()) {
			int port = request.getHttpServer().getSecurePort();
			String url = HttpUtils.buildUrl(true, request.getHost(), port, request.getRequestUri(), request.getQueryString(), new StringBuilder()).toString();
			request.sendRedirect(url);
			return;
		}
		//		request.putResponseHeader("Cache-control", "no-store");
		request.putResponseHeader("Pragma", "no-cache");
		request.setCacheControl("no-store");

		try {
			com.f1.http.HttpSession session = request.getSession(true);
			TfWebsiteManager config = WebsiteUtils.getConfig(request.getHttpServer());
			if (config == null)
				throw new RuntimeException("config not loaded");
			WebsiteUser user = WebsiteUtils.getUser(session);
			String uri = request.getRequestUri();
			if (user == null && uri.contains("/tutorials")) {
				WebsiteUtils.redirectToLoginIframe(request, "", "", "Your session has expired.");
				return;
			} else if (user == null) {
				WebsiteUtils.redirectToLogin(request, "", "", "Your session has expired.");
				return;
			} else if (!user.getEnabled()) {
				WebsiteUtils.redirectToLogin(request, "", "", "Account logged out");
				return;
			} else if (!WebsiteUtils.getLoggedIn(session) && uri.contains("/tutorials")) {
				WebsiteUtils.redirectToLoginIframe(request, "", "", "Your session has expired.");
				return;
			} else if (!WebsiteUtils.getLoggedIn(session)) {
				WebsiteUtils.redirectToLogin(request, user.getUserName(), "Your session has expired.", "");
				return;
			}
			if (this.requiresRequestToken) {
				String requestSectoken = request.getParams().get("tok");
				String sessionSectoken = WebsiteUtils.getSecureToken(session);
				TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
				if (requestSectoken == null) {
					manager.sendEmailTo3Forge(request, null, "User " + user.getEmail() + " visited " + request.getRequestUri() + " but 'tok' param is missing", "Missing tok param",
							null);
					WebsiteUtils.redirectToLogin(request, user.getUserName(), "Internal error due to missing token. Please contact support@3forge.com", "");
					return;
				} else if (sessionSectoken == null) {
					WebsiteUtils.redirectToLogin(request, user.getUserName(), "Session missing token.", "");
					//				manager.sendEmailTo3Forge(request, null, "User " + user.getEmail() + " visited " + request.getRequestUri() + " but session token missing",
					//						"Missing session sectoken", null);
					return;
				} else if (OH.ne(requestSectoken, sessionSectoken)) {
					WebsiteUtils.redirectToLogin(request, user.getUserName(), "Token mismatch.", "");
					manager.sendEmailTo3Forge(request, null,
							"User " + user.getEmail() + " visited " + request.getRequestUri() + " but 'tok' mismatch: " + requestSectoken + " vs " + sessionSectoken,
							"sectoken vs tok mismatch", null);
					return;
				}
			}
			service(request, config, user);
		} catch (Exception e) {
			WebsiteUtils.handleException(request, e);
		}
	}

	protected abstract void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception;

	public boolean getRequiresRequestToken() {
		return requiresRequestToken;
	}

	public void setRequiresRequestToken(boolean requiresRequestToken) {
		this.requiresRequestToken = requiresRequestToken;
	}

	private boolean requiresRequestToken = true;

}
