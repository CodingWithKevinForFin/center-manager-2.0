package com.f1.website;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class ChangePasswordHttpHandler extends AbstractSecureHttpHandler {
	private static final Logger log = LH.get();

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws IOException {
		HttpSession session = request.getSession(true);
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		TfWebsiteDbService db = manager.getDb();

		Map<String, String> params = request.getParams();
		StringBuilder errors = new StringBuilder();
		String pass = CreateAccountHttpHandler.get(params, "pass", "Current Password", 255, errors);
		String pass1 = CreateAccountHttpHandler.get(params, "newpass1", "New Password", 32, errors);
		String pass2 = CreateAccountHttpHandler.get(params, "newpass2", "Retype New Password", 32, errors);

		if (errors.length() == 0 && !user.matchesPassword(pass))
			errors.append("Incorrect Existing Password");
		if (errors.length() == 0 && OH.ne(pass1, pass2))
			errors.append("Password and Retype Password mismatch. ");
		if (errors.length() == 0 && OH.eq(pass, pass1))
			errors.append("New Password is Same as Existing Password. ");
		if (errors.length() == 0 && !config.verifyPassword(pass1))
			errors.append("Password must be 7 to 32 characters and contain an uppercase, lowercase, numeric and special character.");

		String passEnc = CreateAccountHttpHandler.encryptPassword(pass1, errors);
		TfWebsiteUser existing = null;
		if (errors.length() == 0) {
			Connection conn = null;
			try {
				conn = db.getConnection();
				existing = db.queryUser(user.getId(), conn);
				if (OH.ne(existing.getPassword(), user.getUser().getPassword())) {
					errors.append("Password has already been changed");
				} else {
					existing.setRevision(existing.getRevision() + 1);
					existing.setModifiedOn(manager.getNow());
					existing.setPassword(passEnc);
					db.addUser(request, existing, "CHANGE_PASS", conn);
				}
			} catch (Exception e) {
				LH.warning(log, "error with password update: ", user.getId(), e);
				errors.append("Unknown Error");
			} finally {
				IOH.close(conn);
			}
		}
		if (errors.length() == 0) {
			WebsiteUser user2 = new WebsiteUser(existing, manager.getClock(), manager.createFormatter());
			WebsiteUtils.setUser(session, user2);
			WebsiteUtils.setLoggedIn(session, true);
			for (HttpSession otherSession : CH.l(request.getHttpServer().getHttpSessionManager().getSessions())) {
				if (otherSession == session)
					continue;
				WebsiteUser otherUser = WebsiteUtils.getUser(otherSession);
				if (otherUser != null && OH.eq(otherUser.getUserName(), user2.getUserName())) {
					LH.info(log, "User ", user2.getUserName(), " changed password from session ", session.getSessionId(),
							" so killing other session " + otherSession.getSessionId());
					WebsiteUtils.setLoggedIn(otherSession, false);
					WebsiteUtils.setUser(session, null);
					WebsiteUtils.setSpoofingAdmin(session, null);
				}
			}
			//			manager.sendEmail(request, user, "Your password has been changed<BR>Please contact Support@3forge.com if you did not do this",
			//					"3Forge Account Activity - Password Changed", "noreply@3forge.com", null);
			manager.sendEmail(request, user, "passwordchanged.st", CH.m(), "noreply@3forge.com", null);
			request.getOutputStream().append("passwordChanged();");
		}
		if (errors.length() != 0)
			request.getOutputStream().append("showError('" + errors + "')");
	}
}
