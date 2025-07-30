package com.f1.website;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.ScryptUtils;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.CharMatcher;

public class CreateAccountHttpHandler extends AbstractHttpHandler {
	private static final Logger log = LH.get();
	public static String[] blacklistedDomains;

	//	private static final Pattern PHONE_PATTERN = Pattern
	//			.compile("^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$");
	@Override
	public void handle(HttpRequestResponse request) throws IOException {

		HttpSession session = request.getSession(true);
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		String blacklist = request.getHttpServer().getProperties().getProperty("blacklisted_domains").toString();
		String[] tempblacklistedDomains = blacklist.split(",\\s*");
		blacklistedDomains = tempblacklistedDomains;

		TfWebsiteDbService db = manager.getDb();
		WebsiteUtils.setLoggedIn(session, false);

		Map<String, String> params = request.getParams();
		StringBuilder errors = new StringBuilder();
		String fname = getText(params, "fname", "First Name", 64, errors);
		String lname = getText(params, "lname", "Last Name", 64, errors);
		String phone = get(params, "phone", "Phone Number", 20, errors);
		String company = getText(params, "company", "Company", 64, errors);
		String use = get(params, "use", "Intended Use", 50, errors);
		if ("other".equals(use)) {
			use = getText(params, "otheruse", "Intended Use", 500, errors);
		}
		String role = getText(params, "role", "Title/Role", 64, errors);
		String email = get(params, "email", "Email Address", 255, errors);
		String pass1 = get(params, "pass1", "Password", 32, errors);
		String pass2 = get(params, "pass2", "Retype Password", 32, errors);
		if (errors.length() == 0 && !verifyPhone(phone))
			errors.append("Invalid Phone Number. ");
		if (errors.length() == 0 && !verifyEmail(email))
			errors.append("Invalid Email Address. ");
		if (errors.length() == 0 && !verifyEmailDomain(email))
			errors.append("Please register using an eligible business email. Reach out to us to learn more.");
		if (errors.length() == 0 && OH.ne(pass1, pass2))
			errors.append("Password and Retype Password mismatch. ");
		if (errors.length() == 0 && !manager.verifyPassword(pass1))
			errors.append("Password must be 7 to 32 characters and contain an uppercase, lowercase, numeric and special character.");
		if (errors.length() == 0) {
			Connection conn = null;
			try {
				conn = db.getConnection();
				TfWebsiteUser existing = db.queryUser(email, conn);
				if (existing != null)
					errors.append("Email Address already exists");
			} catch (Exception e) {
				LH.warning(log, "error with user: ", email, e);
				errors.append("Account creation error 1001");
			} finally {
				IOH.close(conn);
			}
		}
		String passScrypt = encryptPassword(pass1, errors);
		TfWebsiteUser user = null;

		String verifyGuid = createGuid(manager.getNow() + 86400L * 1000L * 1000L, "V");//1000 days
		if (errors.length() == 0) {
			Connection conn = null;
			// check email
			try {
				conn = db.getConnection();
				TfWebsiteUser existing = db.queryUser(email, conn);
				if (existing != null) {
					errors.append("Email Address already exists");
				}
			} catch (Exception e) {
				LH.warning(log, "error with user: ", user, e);
				errors.append("Account creation error 1003");
			}
			user = db.nw(TfWebsiteUser.class);
			user.setEmail(email);
			user.setId(db.nextId());
			user.setLastName(lname);
			user.setFirstName(fname);
			user.setPhone(phone);
			user.setCompany(company);
			user.setUsername(email);
			user.setCreatedOn(manager.getNow());
			user.setModifiedOn(user.getCreatedOn());
			user.setRevision(0);
			user.setEnabled(true);
			user.setLicenseApps("");
			user.setlicenseInstances("");
			user.setPassword(passScrypt);
			user.setRole(role);
			user.setIntendedUse(use);
			user.setVerifyGuid(verifyGuid);
			user.setStatus(WebsiteUser.STATUS_NEW);
			// try add user
			try {
				db.addUser(request, user, "CREATED", conn);
			} catch (Exception e) {
				LH.warning(log, "error adding user: ", user, e);
				errors.append("Account creation error 1004");
			} finally {
				IOH.close(conn);
			}
		}
		if (errors.length() == 0) {
			WebsiteUser user2 = new WebsiteUser(user, manager.getClock(), manager.createFormatter());
			WebsiteUtils.setUser(session, user2);
			WebsiteUtils.setLoggedIn(session, true);
			WebsiteUtils.populateFiles(manager, user2, "modified");
			request.getOutputStream().append("window.location='/dashboard.htm'");
			int port = request.getHttpServer().getSecurePort();
			String url = HttpUtils.buildUrl(true, request.getHost(), port, HttpUtils.getCanonical(request.getRequestUri(), "/verify?guid=" + verifyGuid), "");
			// payload for email template
			Map<Object, Object> content = new HashMap<Object, Object>();
			content.put("fname", fname);
			content.put("lname", lname);
			content.put("email", email);
			content.put("phone", phone);
			content.put("use", use);
			content.put("role", role);
			content.put("company", company);
			session.getAttributes().put("sectoken", GuidHelper.getGuid());
			manager.sendEmail(request, user2, "welcome.st", CH.m("url", url), "noreply@3forge.com", null);
			manager.sendEmailTo3ForgeWithTemplate(request, null, "accountcreated.st", content, null);
		} else {
			String t = errors.toString();
			//			manager.sendEmailTo3Forge(request, null, "User: " + fname + " " + lname + " at " + email + " ==> " + errors, "Create account Failed", null);
			manager.audit(request, email, "ACCOUNT_CREATE_FAILED", t);
			request.getOutputStream().append("showError('" + t + "')");
		}
	}
	public static String createGuid(long expires, String code) {
		return GuidHelper.getGuid(62) + "." + code + "." + SH.toString(expires, 62);
	}
	public static String encryptPassword(String pass1, StringBuilder errors) {
		String passScrypt = null;
		if (errors.length() == 0) {
			try {
				passScrypt = ScryptUtils.scrypt(pass1, 5, 5, 5);
			} catch (GeneralSecurityException e1) {
				LH.warning(log, "error with password scrypt: ", e1);
				errors.append("Account creation error 1002");
			}
		}
		return passScrypt;
	}

	private static final CharMatcher Letter = new BasicCharMatcher("a-zA-Z", false);

	public static String getText(Map<String, String> params, String key, String description, int maxLength, StringBuilder sink) {
		return getText(params, key, description, maxLength, sink, true, false);
	}
	public static String getText(Map<String, String> params, String key, String description, int maxLength, StringBuilder sink, boolean required, boolean checkForInvalidLetters) {
		String r = get(params, key, description, maxLength, sink, required);
		if (sink.length() == 0) {
			if (r != null) {
				if (r.length() < 2 || SH.indexOf(r, 0, Letter) == -1 || isSilly(r)) {
					sink.append(description).append(" is not valid. ");
					return null;
				}
				if (checkForInvalidLetters)
					if (r.contains("=") || r.contains("|") || r.contains(","))
						sink.append(description).append(" has an invalid letter");
			}
		}
		return r;
	}

	private static final Set<String> SILLY = CH.s("ASDF", "1234", "QWERTY", "HJKL");

	private static boolean isSilly(String r) {
		for (String s : SILLY)
			if (SH.startsWithIgnoreCase(r, s, 0))
				return true;
		return false;
	}
	public static String get(Map<String, String> params, String key, String description, int maxLength, StringBuilder sink) {
		return get(params, key, description, maxLength, sink, true);
	}
	public static String get(Map<String, String> params, String key, String description, int maxLength, StringBuilder sink, boolean required) {
		String r = SH.trim(params.get(key));
		if (sink.length() == 0) {
			if (SH.isnt(r) && required) {
				sink.append(description).append(" Required. ");
				return null;
			} else if (r != null && r.length() > maxLength) {
				sink.append(description).append(" exceeds max length of ").append(maxLength).append(". ");
				return null;
			}
		}
		return r;
	}

	public static boolean verifyPhone(String phone) {
		return true;
	}

	public static boolean verifyEmail(String email) {
		if (OH.isntBetween(email.length(), 1, 255))
			return false;
		return SH.isValidEmail(email);
	}

	public static boolean verifyEmailDomain(String email) {
		for (String blacklistedDomain : blacklistedDomains) {
			if (email.contains("@" + blacklistedDomain)) {
				return false;
			}
		}
		return SH.isValidEmail(email);
	}

}
