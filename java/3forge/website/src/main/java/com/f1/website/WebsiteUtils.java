package com.f1.website;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.base.Clock;
import com.f1.base.Table;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpServer;
import com.f1.http.HttpSession;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.FastPrintStream;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.table.BasicTable;

public class WebsiteUtils {
	private static final Logger log = Logger.getLogger(WebsiteUtils.class.getName());
	private static final String SESSION_ISLOGGEDIN = "isLoggedIn";
	private static final String SESSION_USER = "user";

	public static TfWebsiteManager getConfig(HttpServer servletContext) {
		return (TfWebsiteManager) servletContext.getAttributes().get(TfWebsiteManager.NAME);
	}

	public static WebsiteUser getUser(TfWebsiteManager config, String user, Clock clock) throws IOException {
		Connection conn = null;
		try {
			conn = config.getDb().getConnection();
			LH.info(log, "login attempt for '", user, "'");
			TfWebsiteUser tfuser = config.getDb().queryUser(user, conn);
			LH.info(log, "login result for '", user, "': ", (tfuser == null ? -1 : tfuser.getId()));
			if (tfuser == null)
				return null;
			LocaleFormatter formatter = new BasicLocaleFormatter(Locale.getDefault(), TimeZone.getTimeZone("EST5EDT"), false, OH.EMPTY_FILE_ARRAY, Collections.EMPTY_MAP);
			WebsiteUser r = new WebsiteUser(tfuser, clock, formatter);
			return r;
		} catch (Exception e) {
			LH.warning(log, "Error", e);
			return null;
		} finally {
			IOH.close(conn);
		}

	}

	public static void populateFiles(TfWebsiteManager config, WebsiteUser user, String sorting) throws IOException {
		user.setFileSorting(sorting);
		Table t = new BasicTable(String.class, "id", String.class, "name", String.class, "modified", String.class, "size", String.class, "author", String.class, "path",
				Boolean.class, "writeable");

		File directory = new File(config.getFilesRoot(), user.getHomeDirectory());
		IOH.ensureDir(directory);
		com.f1.utils.Formatter dateFormatter = user.getFormatter().getDateFormatter(LocaleFormatter.DATETIME);

		int i = 0;
		for (File f : directory.listFiles()) {
			if (f.isFile() && f.canRead())
				t.getRows().addRow(i++, f.getName(), dateFormatter.format(f.lastModified()), SH.formatMemory(f.length()), "system", f.getCanonicalFile(), f.canWrite());
		}
		TableHelper.sortDesc(t, sorting);
		user.setFiles(t);
	}
	public static void populateLicenses(TfWebsiteManager config, WebsiteUser user) throws IOException {
		Table t = new BasicTable(String.class, "id", String.class, "name", String.class, "appName", String.class, "appInstance", String.class, "created", String.class, "expires",
				String.class, "path", String.class, "host", String.class, "data");

		File directory = new File(config.getLicensesRoot(), user.getHomeDirectory());
		IOH.ensureDir(directory);
		int i = 0;
		for (File f : directory.listFiles()) {
			try {
				if (!f.isFile())
					continue;
				String data = IOH.readText(f);
				final String[] parts = SH.split("|", data);
				final String appName = parts[1];
				final String appInstance = parts[2];
				final String host = parts[3];
				final String created = parts[4];
				final String expires = parts[5];
				final String fileName = SH.beforeLast(f.getName(), ".");
				t.getRows().addRow(i++, fileName, appName, appInstance, created, expires, f.getAbsolutePath(), host, SH.trim(data));
			} catch (Exception e) {
				throw new RuntimeException("error processing file: " + f.getAbsolutePath(), e);
			}
		}
		TableHelper.sort(t, "id", "expires", "appName", "appInstance", "host");
		user.setLicenses(t);

	}

	public static WebsiteUser getUser(HttpSession session) {
		return (WebsiteUser) session.getAttributes().get(SESSION_USER);
	}

	public static void setUser(HttpSession session, WebsiteUser user) {
		session.getAttributes().put(SESSION_USER, user);
	}

	public static boolean getLoggedIn(HttpSession session) {
		Boolean r = (Boolean) session.getAttributes().get(SESSION_ISLOGGEDIN);
		return Boolean.TRUE.equals(r);
	}

	public static void assertLoggedIn(HttpSession session) {
		if (!getLoggedIn(session))
			throw new RuntimeException("not logged in");
	}

	public static void setLoggedIn(HttpSession session, boolean loggedIn) {
		session.getAttributes().put(SESSION_ISLOGGEDIN, loggedIn);
	}

	public static void handleException(HttpRequestResponse request, Exception e) {
		LH.warning(log, "Error from request: ", request, e);
		try {
			if (request.getHttpServer().getProperties().getOptional("debug", false))
				request.getOutputStream().print("<pre>" + SH.printStackTrace(e) + "</pre>");
			else {
				HttpSession session = request.getSession(true);
				setLoggedIn(session, false);
				request.sendRedirect("login3.htm");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void redirectToLogin(HttpRequestResponse request, String username, String message, String errorMessage) {
		Map<String, Object> vals = request.getAttributes();
		vals = request.getSession(true).getAttributes();
		vals.put("login_error_message", errorMessage);
		vals.put("login_message", message);
		vals.put("login_username", username);
		request.sendRedirect("/login.html");
	}
	public static void redirectToLoginUsingJavascript(HttpRequestResponse request, String username, String message, String errorMessage) {
		Map<String, Object> vals = request.getAttributes();
		vals = request.getSession(true).getAttributes();
		vals.put("login_error_message", errorMessage);
		vals.put("login_message", message);
		vals.put("login_username", username);
		request.getOutputStream().append("window.top.location='/login.html'");
	}
	public static void redirectToLoginIframe(HttpRequestResponse request, String username, String message, String errorMessage) throws IOException {
		Map<String, Object> vals = request.getAttributes();
		vals.put("login_error_message", errorMessage);
		vals.put("login_message", message);
		vals.put("login_username", username);

		request.setResponseType(200);
		request.setContentType("text/html;charset=UTF-8");

		FastPrintStream out = request.getOutputStream();
		out.println("<html><head><title>Redirecting...</title></head><body>");
		out.println("<script type='text/javascript'>");
		out.println("if (window.top !== window.self) {");
		out.println("  window.top.location.href = '/login.html';");
		out.println("} else {");
		out.println("  window.location.href = '/login.html';");
		out.println("}");
		out.println("</script>");
		out.println("</body></html>");
		out.flush();
	}
	public static void redirectToForgotPassword(HttpRequestResponse request, String username, String message, String errorMessage) {
		Map<String, Object> vals = request.getAttributes();
		vals.put("login_error_message", errorMessage);
		vals.put("login_message", message);
		vals.put("login_username", username);
		request.sendRedirect("/forgot-password.html");
	}
	public static void redirectToAction(HttpRequestResponse request, String message) {
		Map<String, Object> vals = request.getAttributes();
		vals.put("action_message", message);
		try {
			request.forward("action.htm");
		} catch (IOException e) {
			LH.warning(log, e);
		}
	}
	public static void RedirectToPressReleaseRegistration(HttpRequestResponse request, String message) {
		Map<String, Object> vals = request.getAttributes();
		vals.put("press_release_message", message);
		try {
			request.forward("press_release.html");
		} catch (IOException e) {
			LH.warning(log, "Error redirecting to Press Release Page " + e);
		}
	}
	public static void setSpoofingAdmin(HttpSession session, WebsiteUser user) {
		if (user == null)
			session.getAttributes().remove("spoofing_admin");
		else
			session.getAttributes().put("spoofing_admin", user.getUserName());
	}
	public static String getSpoofingAdmin(HttpSession session, WebsiteUser user) {
		return (String) session.getAttributes().get("spoofing_admin");
	}
	public static void setSecureToken(HttpSession session) {
		session.getAttributes().put("sectoken", GuidHelper.getGuid());
	}
	public static String getSecureToken(HttpSession session) {
		return (String) session.getAttributes().get("sectoken");
	}
	public static void removeSecureToken(HttpSession session) {
		session.getAttributes().remove("sectoken");
	}

	// Exnternal Auth
	public static final short CODE_EMAIL_REQUIRED = 1;
	public static final short CODE_PASSWORD_REQUIRED = 2;
	public static final short CODE_ACCOUNT_LOCKED = 3;
	//	public static final short CODE_EMAIL_NONEXISTENT = 4;
	//	public static final short CODE_WRONG_PASSWORD = 5;
	//	public static final short CODE_EMAIL_PASS_MISMATCH = 6;
	public static final short CODE_NON_ENTERPISE = 7;
	public static final short CODE_LOGIN_SUCCESS = 8;
	public static final short CODE_UNKNOWN = 9;
	public static final short CODE_INVALID_EMAIL_OR_PASSWORD = 10;

	public static final Map<Short, String> externalAuthCode2Message = new HasherMap<Short, String>();
	static {
		externalAuthCode2Message.put(CODE_EMAIL_REQUIRED, "username required");
		externalAuthCode2Message.put(CODE_PASSWORD_REQUIRED, "password required");
		externalAuthCode2Message.put(CODE_ACCOUNT_LOCKED, "Login failed. Account locked");
		//		externalAuthCode2Message.put(CODE_EMAIL_NONEXISTENT, "Email does not exist");
		//		externalAuthCode2Message.put(CODE_WRONG_PASSWORD, "Wrong password");
		//		externalAuthCode2Message.put(CODE_EMAIL_PASS_MISMATCH, "Email and password do not match");
		externalAuthCode2Message.put(CODE_NON_ENTERPISE, "Non enterprise user");
		externalAuthCode2Message.put(CODE_LOGIN_SUCCESS, "Success");
		externalAuthCode2Message.put(CODE_UNKNOWN, "Unknown status");
		externalAuthCode2Message.put(CODE_INVALID_EMAIL_OR_PASSWORD, "invalid username or password");
	}

}
