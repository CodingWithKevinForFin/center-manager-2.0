package com.f1.website;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import com.f1.base.Clock;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.FastPrintStream;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class LoginResetLinkHttpHandler extends AbstractHttpHandler {

	private static final int RESET_TIME_MS = 60000;
	private static final int[] DELAY_MS = new int[] { 0, 0, 0, 0, 1000, 1000, 1000, 1000, 2000, 2000, 2000, 4000, 10000 };

	private static class LoginAttempt {
		int attempt;
		long time;
	}

	//	private static final String INCORRECT_LOGIN_ATTEMPT = "incorrectLoginAttempt";
	//	private static final String INCORRECT_LOGIN_TIME = "incorrectLoginTime";
	private static final Logger log = LH.get(LoginResetLinkHttpHandler.class);
	private final ConcurrentMap<String, LoginAttempt> loginAttemptsByIp = new ConcurrentHashMap<String, LoginAttempt>();
	final private Clock clock;

	public LoginResetLinkHttpHandler(Clock clock) {
		this.clock = clock;
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		try {
			TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
			TfWebsiteDbService db = manager.getDb();
			HttpSession session = request.getSession(true);
			WebsiteUtils.setLoggedIn(session, false);
			WebsiteUtils.setUser(session, null);

			Map<String, String> params = request.getParams();
			String username = params.get("username");
			if (SH.isnt(username)) {
				WebsiteUtils.redirectToForgotPassword(request, OH.noNull(username, ""), "", "Email Required");
				return;
			}
			WebsiteUser user = WebsiteUtils.getUser(manager, username, this.clock);
			long expires = manager.getNow() + 60000L * 30L;//30 minutes
			String forgotGuid = CreateAccountHttpHandler.createGuid(expires, "F");
			int port = request.getHttpServer().getSecurePort();
			Connection conn = null;
			TfWebsiteUser existing = null;
			if (user != null) {
				try {
					conn = db.getConnection();
					existing = db.queryUser(user.getId(), conn);
					existing.setRevision(existing.getRevision() + 1);
					existing.setModifiedOn(manager.getNow());
					existing.setForgotGuid(forgotGuid);
					db.addUser(request, existing, "FORGOT_PASS", conn);
				} catch (Exception e) {
					LH.warning(log, e);
					request.getOutputStream().append("Unknown Error");
					return;
				} finally {
					IOH.close(conn);
				}
				String url = HttpUtils.buildUrl(true, request.getHost(), port, HttpUtils.getCanonical(request.getRequestUri(), "/verify?guid=" + forgotGuid), "");
				manager.sendEmail(request, user, "passwordreset.st", CH.m("url", url), "noreply@3forge.com", null);
				WebsiteUtils.redirectToForgotPassword(request, OH.noNull(username, ""), "A password reset email has been sent to your inbox", "");
			} else if (!user.getEnabled())
				WebsiteUtils.redirectToForgotPassword(request, OH.noNull(username, ""), "", "Account locked");
			else
				WebsiteUtils.redirectToForgotPassword(request, OH.noNull(username, ""), "", "Account not found");

			return;
		} catch (Exception e) {
			WebsiteUtils.handleException(request, e);
		}

	}

	//gets called from login.html
	public static void generateToken(HttpRequestResponse req) {
		FastPrintStream out = req.getOutputStream();
		HttpSession s = req.getSession(true);
		String loginToken = GuidHelper.getGuid();
		s.getAttributes().put("loginToken", loginToken);
		out.print(loginToken);
	}
}
