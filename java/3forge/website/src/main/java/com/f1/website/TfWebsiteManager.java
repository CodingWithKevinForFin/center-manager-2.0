package com.f1.website;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.base.Clock;
import com.f1.email.EmailAttachment;
import com.f1.email.EmailClient;
import com.f1.email.MimeTypeManager;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpServer;
import com.f1.http.impl.HttpServerSecurityPolicy;
import com.f1.stringmaker.impl.StringMakerUtils;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class TfWebsiteManager implements HttpServerSecurityPolicy {

	private static final String X_FORWARDED_FOR = "X-Forwarded-For";
	private static final Logger log = LH.get();
	public static final String NAME = "TFMANAGER";
	private static final int MAX_ATTACHMENT_SIZE = 10 * 1024 * 1024;

	private String userConfigFile;
	private String filesRoot;
	private String licensesRoot;
	private String emailAddress;
	final private TfWebsiteDbService db;
	final private EmailClient emailClient;
	final private Clock clock;
	private long trialPeriodMs;
	final private String forgeEmail;// = "info@3forge.com";
	final private File dataDir;
	final private String resourcesUrl;
	final private File pressReleaseDir;
	final private String emailOverride;
	final private boolean allowWeakPasswords;
	private boolean usingAwsLoadbalancer;
	private String hostname;
	private TextMatcher hostnameMatcher;

	public TfWebsiteManager(TfWebsiteDbService db, File dataDir, File pressReleaseDir, EmailClient emailClient, Clock clock, String resourcesUrl, String emailOverride,
			String forgeEmail, boolean allowWeakPasswords) {
		this.db = db;
		this.emailClient = emailClient;
		this.clock = clock;
		this.dataDir = dataDir;
		this.pressReleaseDir = pressReleaseDir;
		this.resourcesUrl = resourcesUrl;
		this.emailOverride = emailOverride;
		this.allowWeakPasswords = allowWeakPasswords;
		this.forgeEmail = forgeEmail;
		IOH.assertDirExists(dataDir, "data.dir");
	}

	public TfWebsiteDbService getDb() {
		return this.db;
	}

	public void setUserConfigFile(String userConfigFile) {
		this.userConfigFile = userConfigFile;
	}

	public String getUserConfigFile() {
		return userConfigFile;
	}

	public void setFilesRoot(String filesRoot) {
		this.filesRoot = filesRoot;
	}

	public String getFilesRoot() {
		return filesRoot;
	}

	public void setLicensesRoot(String licensesRoot) {
		this.licensesRoot = licensesRoot;
	}

	public String getLicensesRoot() {
		return licensesRoot;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	//	public EmailClient getEmailClient() {
	//		return emailClient;
	//	}

	public Clock getClock() {
		return this.clock;
	}

	public long getNow() {
		return this.clock.getNow();
	}

	public LocaleFormatter createFormatter() {
		return new BasicLocaleFormatter(Locale.getDefault(), TimeZone.getTimeZone("EST5EDT"), false, OH.EMPTY_FILE_ARRAY, Collections.EMPTY_MAP);
	}

	public long getTrialPeriodMs() {
		return this.trialPeriodMs;
	}

	public void setTrialPeriodMs(long trialPeriodMs) {
		this.trialPeriodMs = trialPeriodMs;
	}

	public long audit(HttpRequestResponse request, TfWebsiteUser user, String audit, String description) {
		Connection conn = null;
		try {
			conn = getDb().getConnection();
			return getDb().addAudit(request, user, null, audit, description, conn);
		} catch (Exception e) {
			LH.warning(log, "audit failed", e);
			return -1;
		} finally {
			IOH.close(conn);
		}
	}
	public long audit(HttpRequestResponse request, String username, String audit, String description) {
		Connection conn = null;
		try {
			conn = getDb().getConnection();
			return getDb().addAudit(request, null, username, audit, description, conn);
		} catch (Exception e) {
			LH.warning(log, "audit failed", e);
			return -1;
		} finally {
			IOH.close(conn);
		}
	}
	public void sendEmail(HttpRequestResponse request, WebsiteUser user, String templateName, Map params, String from, Iterable<EmailAttachment> attachments) {
		try {
			if (user != null)
				params.put("fname", user.getFirstName());
			//			int port = request.getHttpServer().getUnsecurePort();
			//			String url = HttpUtils.buildUrl(false, EH.getLocalHost(), port, "", "");
			params.put("resources", this.resourcesUrl);

			String text = processTemplate(templateName, params);
			String subject = SH.beforeFirst(text, "\n");
			String body = SH.afterFirst(text, "\n");
			audit(request, user.getUser(), "SEND_EMAIL", templateName + " - " + subject);
			String to = SH.is(emailOverride) ? emailOverride : user.getEmail();
			this.emailClient.sendEmail(body, subject, CH.l(to), "noreply@3forge.com", true, attachments);
		} catch (IOException e) {
			LH.warning(log, "send email failed", e);
		}
	}
	public void sendEmailTo3ForgeWithTemplate(HttpRequestResponse request, WebsiteUser user, String templateName, Map params, Iterable<EmailAttachment> attachments) {
		try {
			if (OH.ne(user, null))
				audit(request, user.getUser(), "SEND_EMAIL_TO_3F", "Account created by " + user.getUserName());
			params.put("resources", this.resourcesUrl);
			String text = processTemplate(templateName, params);
			String subject = SH.beforeFirst(text, "\n");
			String body = SH.afterFirst(text, "\n");
			ArrayList<EmailAttachment> att = prepareAttachments(attachments);
			this.emailClient.sendEmail(body, subject, CH.l(forgeEmail), "noreply@3forge.com", true, att);
		} catch (IOException e) {
			LH.warning(log, "send email failed", e);
		}
	}
	public void sendEmailTo3Forge(HttpRequestResponse request, WebsiteUser user, String body, String subject, Iterable<EmailAttachment> attachments) {
		try {
			if (user != null)
				audit(request, user.getUser(), "SEND_EMAIL_TO_3F", subject);
			ArrayList<EmailAttachment> at2 = prepareAttachments(attachments);
			this.emailClient.sendEmail(body, subject, CH.l(forgeEmail), "noreply@3forge.com", true, at2);
		} catch (IOException e) {
			LH.warning(log, "send email failed", e);
		}
	}

	public ArrayList<EmailAttachment> prepareAttachments(Iterable<EmailAttachment> attachments) {
		ArrayList<EmailAttachment> att = new ArrayList<EmailAttachment>();
		if (attachments != null) {
			int totalSize = 0;
			for (EmailAttachment i : attachments) {
				totalSize += i.getData().length;
				if (totalSize < MAX_ATTACHMENT_SIZE)
					att.add(i);
				else
					att.add(new EmailAttachment(("MAX FILE SIZE REACHED. " + i.getName() + " is " + i.getData().length + " bytes").getBytes(),
							MimeTypeManager.getInstance().getMimeTypeForFileName(".txt"), i.getName() + " to large.txt"));
			}
		}
		return att;
	}
	public EmailClient getEmailClient() {
		return this.emailClient;
	}

	public String processTemplate(String file, Map<String, Object> m) {
		try {
			File file2 = new File(dataDir, file);
			String txt = IOH.readText(file2, true);
			LH.info(log, "Read ", txt.length(), " bytes from template: ", IOH.getFullPath(file2));
			return StringMakerUtils.toString(txt, m);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public boolean verifyPassword(String password) {
		if (password == null || OH.isntBetween(password.length(), 7, 32))
			return false;
		if (allowWeakPasswords)
			return true;

		boolean hasUpper = false, hasLower = false, hasNumber = false, hasSpecial = false;
		for (char c : password.toCharArray()) {
			if (OH.isBetween(c, 'A', 'Z'))
				hasUpper = true;
			else if (OH.isBetween(c, 'a', 'z'))
				hasLower = true;
			else if (OH.isBetween(c, '0', '9'))
				hasNumber = true;
			else
				hasSpecial = true;
		}
		return hasUpper && hasLower && hasNumber && hasSpecial;
	}

	public File getPressReleaseDir() {
		return pressReleaseDir;
	}

	public String getRemoteIpAddress(HttpRequestResponse request) {
		String r;
		if (usingAwsLoadbalancer) {
			r = request.getHeader().get(X_FORWARDED_FOR);
			if (r == null)
				throw new RuntimeException("Missing aws header: " + X_FORWARDED_FOR);
			if (r.length() > 25) {//ip version 6:
				if (SH.startsWith(r, '['))//ip version six with port number
					r = SH.beforeFirst(r, ']');
			} else
				r = SH.beforeFirst(r, ':');
		} else
			r = request.getRemoteHost();
		return r;
	}

	public boolean isUsingAwsLoadbalancer() {
		return usingAwsLoadbalancer;
	}

	public void setUsingAwsLoadbalancer(boolean usingAws) {
		this.usingAwsLoadbalancer = usingAws;
	}

	public void setHostName(String hostname) {
		this.hostname = hostname;
		if (SH.is(hostname))
			this.hostnameMatcher = SH.m(hostname);
		else
			this.hostnameMatcher = null;
	}

	@Override
	public int checkRequest(HttpServer server, HttpRequestResponse request) {
		// Check for null, if hostname is null or not defined reject request
		if (this.hostnameMatcher == null || !this.hostnameMatcher.matches(request.getHost())) {
			LH.info(log, "Request From ", request.getRemoteHost(), " has invalid host '", request.getHost(), "' vs expected '", this.hostname, "'. Returning UNAUTHORIZED (401)");
			return HttpRequestResponse.HTTP_401_UNAUTHORIZED;
		}
		if (OH.ne(HttpRequestResponse.POST, request.getMethod()) && OH.ne(HttpRequestResponse.GET, request.getMethod())) {
			LH.info(log, "Request From ", request.getRemoteHost(), " has invalid method '", request.getMethod(), "' vs expected 'POST' or 'GET'. Returning BAD_REQUEST (400)");
			return HttpRequestResponse.HTTP_400_BAD_REQUEST;
		}
		return HttpRequestResponse.HTTP_200_OK;
	}
	public static void main(String[] args) {
		// Multiple values plus string termination
		String p = "3forge.com$|google.com$";
		String t = "www.google.comx";
		String t2 = "www.3forge.com";
		TextMatcher m = SH.m(p);
		// False
		System.out.println(m.matches(t));
		// True
		System.out.println(m.matches(t2));

		// If the pattern is empty string, everything is matched be careful to make sure to set a valid pattern
		String p2 = "";
		TextMatcher m2 = SH.m(p);
		System.out.println(m2.matches(t2));
	}
}
