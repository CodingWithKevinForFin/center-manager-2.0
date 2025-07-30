package com.f1.website;

import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.utils.CH;
import com.f1.utils.LH;

public class ResendHttpHandler extends AbstractSecureHttpHandler {
	private static final Logger log = LH.get();

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		TfWebsiteDbService db = manager.getDb();
		if (user.getStatus() != WebsiteUser.STATUS_NEW) {
			WebsiteUtils.redirectToAction(request, "Account has already been verified.");
			return;
		}
		String verifyGuid = user.getUser().getVerifyGuid();
		int port = request.getHttpServer().getSecurePort();
		if (verifyGuid != null) {
			String url = HttpUtils.buildUrl(true, request.getHost(), port, HttpUtils.getCanonical(request.getRequestUri(), "/verify?guid=" + verifyGuid), "");
			manager.sendEmail(request, user, "verificationresend.st", CH.m("url", url), "noreply@3forge.com", null);
			WebsiteUtils.redirectToAction(request, "Resent verification email to: " + user.getEmail());
		}

	}
}
