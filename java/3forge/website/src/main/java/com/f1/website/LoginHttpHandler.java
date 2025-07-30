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
import com.f1.utils.FastPrintStream;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class LoginHttpHandler extends AbstractHttpHandler {

	private static final int RESET_TIME_MS = 60000;
	private static final int[] DELAY_MS = new int[] { 0, 0, 0, 0, 1000, 1000, 1000, 1000, 2000, 2000, 2000, 4000, 10000 };

	private static class LoginAttempt {
		int attempt;
		long time;
	}

	//	private static final String INCORRECT_LOGIN_ATTEMPT = "incorrectLoginAttempt";
	//	private static final String INCORRECT_LOGIN_TIME = "incorrectLoginTime";
	private static final Logger log = LH.get(LoginHttpHandler.class);
	private final ConcurrentMap<String, LoginAttempt> loginAttemptsByIp = new ConcurrentHashMap<String, LoginAttempt>();
	private static final String updatedCSP = "default-src 'self'; font-src 'self' data:; img-src 'self' blob:; media-src 'self' blob:; require-trusted-types-for 'script';";

	final private Clock clock;

	public LoginHttpHandler(Clock clock) {
		this.clock = clock;
		this.putOverrideResponseHeader(HttpUtils.CONTENT_SECURITY_POLICY, updatedCSP);
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		try {
			TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
			String ipaddress = manager.getRemoteIpAddress(request);

			TfWebsiteDbService db = manager.getDb();
			HttpSession session = request.getSession(true);
			WebsiteUtils.setLoggedIn(session, false);
			WebsiteUtils.setUser(session, null);

			Map<String, String> params = request.getParams();
			String username = params.get("username");
			if (SH.isnt(username)) {
				WebsiteUtils.redirectToLogin(request, OH.noNull(username, ""), "", "Email Required");
				return;
			}
			String password = params.get("password");
			WebsiteUser user = WebsiteUtils.getUser(manager, username, this.clock);
			String loginToken2 = (String) session.getAttributes().get("loginToken");
			String loginToken = params.get("loginToken");
			if (SH.isnt(loginToken2) || OH.ne(loginToken, loginToken2)) {
				WebsiteUtils.redirectToLogin(request, OH.noNull(username, ""), "", "General Error");
				return;
			}
			ipaddress += "+" + username;
			LoginAttempt la = new LoginAttempt();
			LoginAttempt existingLa = loginAttemptsByIp.putIfAbsent(ipaddress, la);
			if (existingLa != null)
				la = existingLa;
			synchronized (la) {
				long now = manager.getClock().getNow();
				if (la.time > 0L && la.time + RESET_TIME_MS < now) {
					LH.info(log, "Reset login time");
					la.attempt = 0;
				}
				if (user == null || !user.matchesPassword(password)) {
					la.attempt++;
					LH.info(log, "Login failed for user: '", username, " attempt ", la.attempt);
					int sleep = DELAY_MS[Math.min(la.attempt, DELAY_MS.length - 1)];
					if (sleep > 0) {
						LH.info(log, "Bad password attempt #", la.attempt + " from '", ipaddress, "' so sleeping " + sleep + " millis");
						OH.sleep(sleep);
					}
					String msg = SH.isnt(password) ? "Password required" : "Email or password is incorrect";
					if (user != null)
						manager.audit(request, user.getUser(), "LOGIN_FAILED", msg);
					else
						manager.audit(request, username, "LOGIN_FAILED", msg);
					la.time = manager.getClock().getNow();
					WebsiteUtils.redirectToLogin(request, OH.noNull(username, ""), "", msg);
				} else {
					la.time = 0;
					la.attempt = 0;
					loginAttemptsByIp.remove(ipaddress);
					if (user != null && !user.getEnabled()) {
						manager.audit(request, user.getUser(), "LOGIN_FAILED", "Account locked");
						WebsiteUtils.redirectToLogin(request, OH.noNull(username, ""), "", "Account is locked, please contact support@3forge.com");
						return;
					}
					boolean trialEnded = user.getStatus() == WebsiteUser.STATUS_STARTED_TRIAL && user.getTrialExpiresOn() < now;
					boolean clearForgotGuid = user.getUser().getForgotGuid() != null;
					if (clearForgotGuid || trialEnded) {
						if (clearForgotGuid)
							LH.info(log, "Login succeeded so clearing out existing forgotten password guid for: ", username);
						if (trialEnded)
							LH.info(log, "Trial has ended for: ", username);
						Connection conn = null;
						TfWebsiteUser existing = null;
						try {
							conn = db.getConnection();
							existing = db.queryUser(user.getId(), conn);
							existing.setModifiedOn(manager.getNow());
							if (clearForgotGuid) {
								existing.setRevision(existing.getRevision() + 1);
								existing.setForgotGuid(null);
								db.addUser(request, existing, "FORGOT_CLEARED", conn);
							}
							if (trialEnded) {
								existing.setRevision(existing.getRevision() + 1);
								existing.setStatus(WebsiteUser.STATUS_ENDED_TRIAL);
								db.addUser(request, existing, "TRIAL_ENDED", conn);
							}
						} catch (Exception e) {
							request.getOutputStream().append("Unknown Error");
							return;
						} finally {
							IOH.close(conn);
						}
						WebsiteUtils.setUser(session, user = new WebsiteUser(existing, user.getClock(), user.getFormatter()));
					} else
						WebsiteUtils.setUser(session, user);
					WebsiteUtils.setSecureToken(session);
					manager.audit(request, user.getUser(), "LOGIN_SUCCESS", null);
					WebsiteUtils.setLoggedIn(session, true);
					int port = request.getHttpServer().getSecurePort();
					String url = HttpUtils.buildUrl(true, request.getHost(), port, HttpUtils.getCanonical(request.getRequestUri(), "dashboard.htm"), "");
					request.sendRedirect(url);
					//WebsiteUtils.populateFiles(manager, user, "name");
					WebsiteUtils.populateLicenses(manager, user);
					LH.info(log, "Login successful for user: ", username);
				}
			}
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
