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

public class RequestDemoHttpHandler extends AbstractHttpHandler {

	private static final Logger log = LH.get(RequestDemoHttpHandler.class);

	private static final String CR = "<BR>" + SH.NEWLINE;

	public RequestDemoHttpHandler() {
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		try {
			TfWebsiteManager config = WebsiteUtils.getConfig(request.getHttpServer());
			HttpSession session = request.getSession(true);
			Map<String, Object> attributes = session.getAttributes();

			Map<String, String> params = request.getParams();
			String name = params.get("name");
			String email = params.get("email");
			String company = params.get("company");
			String phone = params.get("phone");
			String message = params.get("message");

			LH.info(log, "RECEIVED DEMO REQUEST: ", params);

			StringBuilder er = new StringBuilder();
			//			if (SH.isnt(name))
			//				er.append("Full Name Required. ");
			//			if (SH.isnt(company))
			//				er.append("Company Name Required. ");
			//			if (SH.isnt(message))
			//				er.append("Message Required. ");
			if (SH.isnt(email))
				er.append("Email Required. ");
			else if (!SH.isValidEmail(email))
				er.append("Email is not valid. ");

			if (er.length() > 0) {
				attributes.put("rd_errors", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + er.toString());
				attributes.put("cu_name", name);
				attributes.put("cu_email", email);
				attributes.put("cu_company", company);
				attributes.put("cu_phone", phone);
				attributes.put("cu_message", message);
				request.sendRedirect("request.html");
			} else {
				StringBuilder body = new StringBuilder();
				body.append("Server Host: ").append(request.getHost()).append(CR);
				body.append("Current Time: ").append(new Date()).append(CR);
				body.append("Remote Ip: ").append(request.getRemoteHost()).append(CR + "<P>");
				body.append("Full Name: ").append(SH.noNull(name)).append(CR);
				body.append("Email: ").append(SH.noNull(email)).append(CR);
				body.append("Company: ").append(SH.noNull(company)).append(CR);
				body.append("Phone: ").append(SH.noNull(phone)).append(CR);
				body.append("Message: ").append(SH.noNull(message)).append(CR);

				String emailAddress = config.getEmailAddress();
				config.getEmailClient().sendEmail(body.toString(), "DEMO REQUEST FROM WEBSITE", CH.l(emailAddress), emailAddress, true, null);
				attributes.remove("cu_errors");
				attributes.remove("cu_name");
				attributes.remove("cu_email");
				attributes.remove("cu_company");
				attributes.remove("cu_phone");
				attributes.remove("cu_subject");
				attributes.remove("cu_message");
				attributes.put("tu_fname", name);
				request.sendRedirect("thanks.html");
			}

		} catch (Exception e) {
			WebsiteUtils.handleException(request, e);
		}

	}

}
