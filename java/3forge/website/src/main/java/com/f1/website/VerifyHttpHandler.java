package com.f1.website;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class VerifyHttpHandler extends AbstractHttpHandler {
	private static final Logger log = LH.get();

	@Override
	public void handle(HttpRequestResponse request) throws IOException {

		HttpSession session = request.getSession(true);
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		TfWebsiteDbService db = manager.getDb();

		Map<String, String> params = request.getParams();
		String guid = params.get("guid");
		if (SH.isnt(guid)) {
			request.getOutputStream().append("Bad Guid");
			return;
		}
		long now = manager.getNow();
		long time;
		String type;
		try {
			String[] parts = SH.split('.', guid);
			time = SH.parseLong(parts[2], 62);
			type = parts[1];
		} catch (Exception e) {
			LH.warning(log, e);
			request.getOutputStream().append("Bad Guid");
			return;
		}
		if ("F".equals(type)) {
			if (now > time) {
				WebsiteUtils.redirectToLogin(request, "", "", "This email link has expired");
				return;
			}
			Connection conn = null;
			TfWebsiteUser existing = null;
			try {
				conn = db.getConnection();
				existing = db.queryUserByForgotGuid(guid, conn);
				if (existing == null) {
					WebsiteUtils.redirectToLogin(request, "", "", "This email link is no longer valid");
					return;
				}
				if (existing.getVerifyGuid() != null) {
					existing.setRevision(existing.getRevision() + 1);
					existing.setModifiedOn(manager.getNow());
					existing.setVerifyGuid(null);
					if (existing.getStatus() != WebsiteUser.STATUS_ENTERPRISE) {
						existing.setStatus(WebsiteUser.STATUS_VERIFIED);
						db.addUser(request, existing, "VERIFY_EMAIL2", conn);
					} else
						manager.audit(request, existing.getUsername(), "VERIFY AFTER EXPIRE", "User verified after link expired.");
				}
			} catch (Exception e) {
				LH.warning(log, "error with guid: ", guid, e);
				request.getOutputStream().append("Unknown Error");
				return;
			} finally {
				IOH.close(conn);
			}
			Map<String, Object> vals = request.getAttributes();
			vals.put("forgot_guid", guid);
			request.forward("reset-password.html");
		} else if ("V".equals(type)) {
			Connection conn = null;
			TfWebsiteUser existing = null;
			try {
				conn = db.getConnection();
				existing = db.queryUserByVerifyGuid(guid, conn);
				if (existing == null) {
					WebsiteUtils.redirectToLogin(request, "", "", "This email link is no longer valid");
					return;
				}
				existing.setRevision(existing.getRevision() + 1);
				existing.setModifiedOn(manager.getNow());
				existing.setVerifyGuid(null);
				if (existing.getStatus() != WebsiteUser.STATUS_ENTERPRISE) {
					existing.setStatus(WebsiteUser.STATUS_VERIFIED);
					db.addUser(request, existing, "VERIFY_EMAIL", conn);
				} else
					manager.audit(request, existing.getUsername(), "VERIFY AFTER ENTERPRISE", "User verified email after being set to Enterprise.");

			} catch (Exception e) {
				LH.warning(log, "error with guid: ", guid, e);
				request.getOutputStream().append("Unknown Error");
				return;
			} finally {
				IOH.close(conn);
			}
			if (WebsiteUtils.getLoggedIn(session)) {
				WebsiteUser user = WebsiteUtils.getUser(session);
				if (user != null && existing.getId() == user.getId()) {
					WebsiteUtils.setUser(session, new WebsiteUser(existing, user.getClock(), user.getFormatter()));
					WebsiteUtils.redirectToAction(request, "Email Address Verified: " + user.getEmail());
					return;
				}
			} else
				WebsiteUtils.redirectToLogin(request, existing.getUsername(), "Thanks for verifying your email address", "");
		} else {
			request.getOutputStream().append("Bad Guid");
			return;
		}
	}
}
