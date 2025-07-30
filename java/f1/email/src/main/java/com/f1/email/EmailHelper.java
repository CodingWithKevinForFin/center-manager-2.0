package com.f1.email;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.f1.utils.IOH;
import com.f1.utils.SH;

public class EmailHelper {

	private static final Pattern PATTERN = Pattern.compile("[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}", Pattern.CASE_INSENSITIVE);

	static public List<EmailAttachment> loadAttachments(List<String> files) throws IOException {
		final List<EmailAttachment> r = new ArrayList<EmailAttachment>();
		if (files == null)
			return r;
		for (String file : files) {
			final File f = new File(file);
			final MimeType mt = MimeTypeManager.getInstance().getMimeTypeForFileName(file);
			r.add(new EmailAttachment(IOH.readData(f), mt, f.getName()));
		}
		return r;
	}
	static public List<EmailAttachment> loadAttachmentsFromFiles(List<File> files) throws IOException {
		final List<EmailAttachment> r = new ArrayList<EmailAttachment>();
		if (files == null)
			return r;
		for (File f : files) {
			final MimeType mt = MimeTypeManager.getInstance().getMimeTypeForFileName(f.getName());
			r.add(new EmailAttachment(IOH.readData(f), mt, f.getName()));
		}
		return r;
	}

	public static boolean isValidEmailAddress(String email) {
		return SH.isValidEmail(email);
	}

	public static void assertEmailValid(String email) {
		if (!isValidEmailAddress(email))
			throw new IllegalArgumentException("not a valid email address: " + email);
	}
}
