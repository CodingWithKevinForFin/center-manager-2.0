package com.f1.website;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class ResetPasswordHttpHandler extends AbstractHttpHandler {
	private static final Logger log = LH.get();

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		HttpSession session = request.getSession(true);
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		TfWebsiteDbService db = manager.getDb();

		Map<String, String> params = request.getParams();
		StringBuilder errors = new StringBuilder();
		String email = CreateAccountHttpHandler.get(params, "email", "Email Address", 255, errors);
		String pass1 = CreateAccountHttpHandler.get(params, "pass1", "New Password", 32, errors);
		String pass2 = CreateAccountHttpHandler.get(params, "pass2", "Retype New Password", 32, errors);
		String guid = params.get("guid");
		if (errors.length() == 0 && OH.ne(pass1, pass2))
			errors.append("Password and Retype Password mismatch. ");
		if (errors.length() == 0 && !manager.verifyPassword(pass1))
			errors.append("Password must be 7 to 32 characters and contain an uppercase, lowercase, numeric and special character.");
		if (errors.length() == 0 && SH.isnt(guid)) {
			errors.append("Bad Guid");
		}
		TfWebsiteUser existing = null;
		if (errors.length() == 0) {
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
				try {
					conn = db.getConnection();
					existing = db.queryUserByForgotGuid(guid, conn);
					if (existing == null || !existing.getEnabled()) {
						WebsiteUtils.redirectToLogin(request, "", "", "This email link is no longer valid");
						return;
					}
				} catch (Exception e) {
					LH.warning(log, "error with guid: ", guid, e);
					request.getOutputStream().append("Unknown Error");
					return;
				} finally {
					IOH.close(conn);
				}
			}
		}
		if (errors.length() == 0 && !SH.equalsIgnoreCase(email, existing.getEmail())) {
			errors.append("Incorrect Email verify. ");
		}

		String passEnc = CreateAccountHttpHandler.encryptPassword(pass1, errors);
		if (errors.length() == 0) {
			Connection conn = null;
			try {
				conn = db.getConnection();
				existing = db.queryUser(existing.getId(), conn);
				existing.setRevision(existing.getRevision() + 1);
				existing.setModifiedOn(manager.getNow());
				existing.setPassword(passEnc);
				existing.setForgotGuid(null);
				db.addUser(request, existing, "RESET_PASS", conn);
			} catch (Exception e) {
				LH.warning(log, "error with password update: ", existing.getId(), e);
				errors.append("Unknown Error");
			} finally {
				IOH.close(conn);
			}
		}
		if (errors.length() == 0) {
			WebsiteUser user2 = new WebsiteUser(existing, manager.getClock(), manager.createFormatter());
			WebsiteUtils.setLoggedIn(session, false);
			WebsiteUtils.setUser(session, null);
			manager.sendEmail(request, user2, "passwordchanged.st", CH.m(), "noreply@3forge.com", null);
			WebsiteUtils.redirectToLoginUsingJavascript(request, OH.noNull(existing.getUsername(), ""), "Password reset accepted", "");
		}
		if (errors.length() != 0)
			request.getOutputStream().append("showError('" + errors + "')");
	}
}
