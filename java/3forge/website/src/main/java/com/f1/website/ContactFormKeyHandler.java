package com.f1.website;

import java.io.IOException;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.Cksum;
import com.f1.utils.ContentType;
import com.f1.utils.LH;
import com.f1.utils.casters.Caster_String;

public class ContactFormKeyHandler extends AbstractHttpHandler {
	private static final String RECAPTCHA_SECRET_KEY = "testPassedFromRecaptcha1122";
	private static final Logger log = LH.get(ContactFormKeyHandler.class);

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		super.handle(req);
		try {
			String hashCode = Caster_String.INSTANCE.cast(generateHash(req));
			ContentType mimetype = ContentType.getTypeByFileExtension("txt");
			req.setContentTypeAsBytes((mimetype != null) ? mimetype.getMimeTypeAsBytes() : "application/octet-stream".getBytes());
			req.getOutputStream().write(hashCode.getBytes());
		} catch (Exception e) {
			LH.warning(log, "Error generating key for the contact form: ", e);
		}
	}

	public static Long generateHash(HttpRequestResponse req) {
		String content = req.getRemoteHost() + RECAPTCHA_SECRET_KEY + req.getHost();
		return Cksum.cksum(content.getBytes());
	}

}
