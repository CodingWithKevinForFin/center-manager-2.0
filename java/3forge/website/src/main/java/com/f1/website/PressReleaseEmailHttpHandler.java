package com.f1.website;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.Cksum;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.CharMatcher;

public class PressReleaseEmailHttpHandler extends AbstractHttpHandler {
	static final String SECRET = "3forgepressreleasesecret";
	static final int HASHCODE_COLUMN_INDEX = 8;
	private static final Logger log = LH.get();
	private static final Collection<String> emailDomainBlackList = new HashSet<String>();

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		TfWebsiteManager manager = WebsiteUtils.getConfig(req.getHttpServer());
		TfWebsiteDbService db = manager.getDb();
		PressReleaseEmailHttpHandler.initBlackListedEmailDomains();

		Map<String, String> params = req.getParams();
		StringBuilder errors = new StringBuilder();
		String fname = getText(params, "fname", "First Name", 64, errors);
		String lname = getText(params, "lname", "Last Name", 64, errors);
		String email = get(params, "email", "Email Address", 255, errors);
		String company = getText(params, "company", "Company", 64, errors);
		String phone = get(params, "phone", "Phone Number", 20, errors);
		String documentName = get(params, "press-release-version", "Press Release Version", 255, errors);
		boolean contactByPhone = OH.eq(params.get("contact_medium_phone"), "contact_phone") ? true : false;
		boolean contactByEmail = OH.eq(params.get("contact_medium_email"), "contact_email") ? true : false;

		if (errors.length() == 0 && isInValidEmailDomain(email))
			errors.append("Please provide an email address affiliated with your company.");
		if (errors.length() == 0 && !verifyEmail(email))
			errors.append("Invalid Email Address. ");
		if (errors.length() == 0 && !verifyPhone(phone))
			errors.append("Invalid Phone Number. ");
		if (errors.length() == 0) {
			TfWebsiteUser user = db.nw(TfWebsiteUser.class);
			user.setEmail(email);
			user.setFirstName(fname);
			user.setLastName(lname);
			user.setCompany(company);
			user.setPhone(phone);
			WebsiteUser webUser = new WebsiteUser(user, manager.getClock(), manager.createFormatter());

			Long hashCode = generateUrlHash(fname, lname, email, documentName);
			try {
				db.addPressReleaseUser(req, webUser, contactByPhone, contactByEmail, db.getConnection());

				String queryString = "?fname=" + fname + "&lname=" + lname + "&email=" + email + "&document_name=" + documentName + "&hc=" + hashCode;
				String pressReleaseDownloadUrl = HttpUtils.buildUrl(true, req.getHost(), req.getPort(),
						HttpUtils.getCanonical(req.getRequestUri(), "downloadPressRelease") + queryString, "");

				// create payload for email template.
				Map<Object, Object> content = new HashMap<Object, Object>();
				content.put("url", pressReleaseDownloadUrl);
				content.put("fname", fname);
				content.put("lname", lname);
				content.put("email", email);
				content.put("phone", phone);
				content.put("company", company);
				content.put("hc", hashCode);
				content.put("documentName", documentName);
				content.put("contact_phone", contactByPhone ? "Yes" : "No");
				content.put("contact_email", contactByEmail ? "Yes" : "No");

				LH.info(log, "Press Release Download Request: " + documentName);
				manager.audit(req, fname + " " + lname, "PR_DOWNLOAD_REQUEST", "file: " + documentName);
				manager.sendEmail(req, webUser, "pressrelease_user_email.st", content, "noreply@3forge.com", null);
				manager.sendEmailTo3ForgeWithTemplate(req, null, "pressrelease_request_internal.st", content, null);

				String successMessage = email;
				req.getOutputStream().append("showModal('" + successMessage + "')" + ";" + "clearForm();");
			} catch (SQLException e) {
				LH.warning(log, "Error adding press release user in the database " + e);
			} catch (Exception e) {
				LH.warning(log, "Error sending email to press release user  " + e);
			}
		} else {
			String errorMessage = errors.toString();
			req.getOutputStream().append("showErrorMessage('" + errorMessage + "');");
		}
	}
	private static void initBlackListedEmailDomains() {
		emailDomainBlackList.add("gmail");
		emailDomainBlackList.add("hotmail");
		emailDomainBlackList.add("yahoo");
		emailDomainBlackList.add("outlook");
		emailDomainBlackList.add("aol");
		emailDomainBlackList.add("icloud");
		emailDomainBlackList.add("tutanota");
		emailDomainBlackList.add("yandex");
		emailDomainBlackList.add("proton");
		emailDomainBlackList.add("gmx");
		emailDomainBlackList.add("mail");
	}

	public static Long generateUrlHash(String fname, String lname, String email, String documentName) {
		String content = PressReleaseEmailHttpHandler.SECRET + fname + lname + email + documentName;
		return Cksum.cksum(content.getBytes());
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

	public static boolean isInValidEmailDomain(String email) {
		int startIndex = SH.indexOf(email, '@', 0) + 1;
		int endIndex = SH.indexOf(email, '.', startIndex);
		String domain = SH.substring(email, startIndex, endIndex);
		if (PressReleaseEmailHttpHandler.emailDomainBlackList.contains(SH.toLowerCase(domain)))
			return true;
		return false;
	}

	public static boolean verifyEmail(String email) {
		if (OH.isntBetween(email.length(), 1, 255))
			return false;
		return SH.isValidEmail(email);
	}

}
