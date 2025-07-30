package com.f1.website;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.impl.HttpMultiPart;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.CharMatcher;

public class SubmitApplicationHttpHandler extends AbstractHttpHandler {

	private static final Logger log = LH.get();

	public void handle(HttpRequestResponse request) throws IOException {
		HttpSession session = request.getSession(true);
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		TfWebsiteDbService db = manager.getDb();

		WebsiteUtils.setLoggedIn(session, false);

		Map<String, String> params = request.getParams();
		System.out.println(params);
		StringBuilder errors = new StringBuilder();
		String jobtitle = get(params, "jobtitle", "Job Title", 64, errors);
		String fname = getText(params, "fname", "First Name", 64, errors);
		String lname = getText(params, "lname", "Last Name", 64, errors);
		String email = getText(params, "email", "Email Address", 255, errors);
		String phone = get(params, "phone", "Phone Number", 20, errors);

		LH.info(log, "RECEIVED JOB APPLICATION: ", params);

		System.out.println("test 1" + request.getParamAsList("app_resume"));

		List<HttpMultiPart> files1 = (List) request.getParamAsList("app_resume");

		byte[] app_resume = null;

		for (HttpMultiPart part : files1) {
			app_resume = part.getData();
		}

		byte[] cover_letter = null;

		byte[] transcript = null;

		if (jobtitle.contains("Intern")) {
			List<HttpMultiPart> files2 = (List) request.getParamAsList("transcript");

			for (HttpMultiPart part : files2) {
				transcript = part.getData();
			}

			List<HttpMultiPart> files3 = (List) request.getParamAsList("cover_letter");

			if (files3 != null) {
				for (HttpMultiPart part : files3) {
					cover_letter = part.getData();
				}
			}

		} else {
			List<HttpMultiPart> files2 = (List) request.getParamAsList("cover_letter");

			if (files2 != null) {
				for (HttpMultiPart part : files2) {
					cover_letter = part.getData();
				}
			}
		}

		String pron = get(params, "pronoun", "Pronouns", 64, errors);
		String pref_fname = get(params, "pref_fname", "Preferred First Name", 64, errors);
		String hear_3forge = get(params, "hear_forge", "How they Heard About 3forge", 255, errors);
		String forge_family = get(params, "forge_family", "Previous Family at 3forge", 64, errors);
		String office_location = get(params, "office_location", "Office Location", 255, errors);
		String previous_work = get(params, "previous_work", "Worked Previously at 3forge", 64, errors);
		String sponsorship = get(params, "sponsorship", "Requires Sponsorship", 64, errors);

		TfWebsiteApplicant applicant = null;

		Connection conn = null;

		try {
			conn = db.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		applicant = db.nw(TfWebsiteApplicant.class);
		applicant.setjobtitle(jobtitle);
		applicant.setemail(email);
		applicant.setlname(lname);
		applicant.setfname(fname);
		applicant.setphone(phone);
		applicant.setforge_family(forge_family);
		applicant.sethear_forge(hear_3forge);
		applicant.setCreatedOn(manager.getNow());
		applicant.setModifiedOn(applicant.getCreatedOn());
		applicant.setpronoun(pron);
		applicant.setpref_fname(pref_fname);
		applicant.setoffice_location(office_location);
		applicant.setprevious_work(previous_work);
		applicant.setsponsorship(sponsorship);
		applicant.setapp_resume(app_resume);
		applicant.setcover_letter(cover_letter);
		applicant.settimestamp(applicant.getCreatedOn());
		String appfullname = "" + applicant.getfname() + " " + applicant.getlname();
		try {
			db.addApplicant(request, applicant, conn);
			System.out.println("application submitted! for: " + appfullname);
			LH.info(log, "SUBMITTED APPLICATION FOR: ", appfullname);
		} catch (Exception e) {
			LH.warning(log, "ERROR SUBMITTING APPLICATION FOR: ", appfullname, e);
			errors.append("Job Application submission error");
		} finally {
			IOH.close(conn);
		}
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

	private static final Set<String> SILLY = CH.s("ASDF", "1234", "QWERTY", "HJKL");

	private static boolean isSilly(String r) {
		for (String s : SILLY)
			if (SH.startsWithIgnoreCase(r, s, 0))
				return true;
		return false;
	}

	public static boolean verifyPhone(String phone) {
		return true;
	}

	public static boolean verifyEmail(String email) {
		if (OH.isntBetween(email.length(), 1, 255))
			return false;
		return SH.isValidEmail(email);
	}
}