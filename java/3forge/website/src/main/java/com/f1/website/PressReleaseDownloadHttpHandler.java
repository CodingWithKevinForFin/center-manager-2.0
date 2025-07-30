package com.f1.website;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.CharMatcher;

public class PressReleaseDownloadHttpHandler extends AbstractHttpHandler {
	private static final Logger log = LH.get(PressReleaseDownloadHttpHandler.class);

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		TfWebsiteManager config = WebsiteUtils.getConfig(request.getHttpServer());
		Map<String, String> params = request.getParams();
		StringBuilder errors = new StringBuilder();
		String fname = get(params, "fname", "First Name", 255, errors);
		String lname = get(params, "lname", "Last Name", 255, errors);
		String email = get(params, "email", "Email Address", 255, errors);
		String documentName = get(params, "document_name", "Document Name", 255, errors);
		Long hashCodeFromUrl = Caster_Long.INSTANCE.cast(get(params, "hc", "Hash Code / Checksum", 255, errors));

		if (OH.ne(params.size(), 5)) {
			LH.warning(log, "QueryParameterLengthMismatch: The download url for user " + fname + " " + lname + " email(" + email + ") has been tampered.");
			request.getOutputStream().append("bad url");
		} else {
			Long systemHash = PressReleaseEmailHttpHandler.generateUrlHash(fname, lname, email, documentName);
			if (OH.eq(hashCodeFromUrl, systemHash)) {
				try {
					Set<String> permittedFiles = CH.s(config.getPressReleaseDir().list());
					OH.assertTrue(permittedFiles.contains(documentName));
					File file = new File(config.getPressReleaseDir(), documentName);
					byte[] data = IOH.readData(file);
					HttpUtils.respondWithFile(documentName, data, request);

					Map<Object, Object> content = new HashMap<Object, Object>();
					content.put("fname", fname);
					content.put("lname", lname);
					content.put("email", email);
					content.put("documentName", documentName);
					content.put("remoteHost", request.getRemoteHost());
					content.put("userAgent", request.getHeader().get("User-Agent"));

					LH.info(log, "Press Release Registration Download for user '", fname + " " + lname, "': " + documentName);
					config.audit(request, fname + " " + lname, "PR_DOWNLOAD_REGISTRATION", "file: " + documentName);
					config.sendEmailTo3ForgeWithTemplate(request, null, "pressrelease_registration_internal.st", content, null);
				} catch (Exception e) {
					LH.info(log, "Exception downloading press release document: " + documentName + "\n" + e);
				}
			} else {
				LH.warning(log, "The download url for user " + fname + " " + lname + " email(" + email + ") has been tampered");
				request.getOutputStream().append("bad url");
			}
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

}
