package com.f1.website;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;

public class ContactUsHttpHandler extends AbstractHttpHandler {

	private static final Logger log = LH.get(ContactUsHttpHandler.class);
	private static final String CR = "<BR>" + SH.NEWLINE;

	public ContactUsHttpHandler() {
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		try {
			TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
			HttpSession session = request.getSession(true);
			Map<String, Object> attributes = session.getAttributes();

			Map<String, String> params = request.getParams();
			//			String firstName = params.get("fname");
			//			String lastName = params.get("lname");
			String fullName = params.get("full-name");
			String email = params.get("email");
			String company = params.get("company");
			String phone = params.get("phone");
			String subject = params.get("subject");
			String message = params.get("message");
			String hashCodeClient = params.get("hash-code");
			LH.info(log, "RECEIVED CONACT US: ", params);

			StringBuilder er = new StringBuilder();
			String hashCodeServer = Caster_String.INSTANCE.cast(ContactFormKeyHandler.generateHash(request));
			if (!hashCodeServer.equals(hashCodeClient)) {
				er.append("Bad Request. ");
				LH.warning(log, "RECAPTCHA BYPASSED, HashCode: ", params.get("hash-code"));
			}
			if (SH.isnt(fullName))
				er.append("Name Required. ");
			//			if (SH.isnt(firstName))
			//				er.append("First Name Required");
			//			else if (SH.isnt(lastName))
			//				er.append("Last Name Required");
			else if (SH.isnt(email))
				er.append("Email Required. ");
			else if (!SH.isValidEmail(email))
				er.append("Email is not valid. ");
			else if (SH.isnt(company))
				er.append("Company Name Required. ");
			else if (SH.isnt(message))
				er.append("Message Required. ");

			if (er.length() > 0) {
				attributes.put("cu_errors", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + er.toString());
				attributes.put("cu_fullname", fullName);
				//				attributes.put("cu_fname", firstName);
				//				attributes.put("cu_lname", lastName);
				attributes.put("cu_email", email);
				attributes.put("cu_company", company);
				attributes.put("cu_phone", phone);
				attributes.put("cu_subject", subject);
				attributes.put("cu_message", message);
				request.sendRedirect("contact.html");
			} else {

				StringBuilder body = new StringBuilder();
				body.append("Server Host: ").append(request.getHost()).append(CR);
				body.append("Current Time: ").append(new Date()).append(CR);
				body.append("Remote Ip: ").append(request.getRemoteHost()).append(CR + "<P>");
				body.append("Name: ").append(SH.noNull(fullName)).append(CR);
				//				body.append("First Name: ").append(SH.noNull(firstName)).append(CR);
				//				body.append("Last Name: ").append(SH.noNull(lastName)).append(CR);
				body.append("Email: ").append(SH.noNull(email)).append(CR);
				body.append("Company: ").append(SH.noNull(company)).append(CR);
				body.append("Phone: ").append(SH.noNull(phone)).append(CR);
				body.append("Subject: ").append(SH.noNull(subject)).append(CR);
				body.append("Message: ").append(SH.noNull(message)).append(CR);
				String emailAddress = manager.getEmailAddress();
				manager.getEmailClient().sendEmail(body.toString(), "CONTACT REQUEST FROM WEBSITE", CH.l(emailAddress), emailAddress, true, null);

				attributes.remove("cu_errors");
				attributes.remove("cu_fullname");
				//				attributes.remove("cu_fname");
				//				attributes.remove("cu_lname");
				attributes.remove("cu_email");
				attributes.remove("cu_company");
				attributes.remove("cu_phone");
				attributes.remove("cu_subject");
				attributes.remove("cu_message");
				//				attributes.put("tu_fname", firstName);
				//				attributes.put("tu_lname", lastName);
				attributes.put("cu_fullname", fullName);
				request.sendRedirect("thanks.html");
			}

		} catch (Exception e) {
			WebsiteUtils.handleException(request, e);
		}

	}
}
