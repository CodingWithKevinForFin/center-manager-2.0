package com.f1.website;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.email.EmailAttachment;
import com.f1.email.MimeType;
import com.f1.email.MimeTypeManager;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.utils.CH;
import com.f1.utils.Cksum;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class ContactSupportHttpHandler extends AbstractSecureHttpHandler {
	private static final Logger log = LH.get();
	private static final int MAX_ATTACH_TO_EMAIL = 10 * 1000 * 1000;

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		HttpSession session = request.getSession(true);
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		TfWebsiteDbService db = manager.getDb();
		Map<String, String> params = request.getParams();
		StringBuilder errors = new StringBuilder();
		String type = CreateAccountHttpHandler.getText(params, "type", "type", 32, errors);
		String message = CreateAccountHttpHandler.getText(params, "message", "Message", 10000, errors, OH.ne("quote", type), false);
		message = escape(message);
		String contactMethod = CreateAccountHttpHandler.getText(params, "contactMethod", "Contact Method", 32, errors);
		//onContactSupportResponse
		String func = CreateAccountHttpHandler.getText(params, "func", "func", 32, errors);
		String auditType = null;
		String typ = null;
		String userCount = null;
		Tuple2<String, byte[]>[] attachments = user.getTempAttachments();
		if (errors.length() == 0) {
			if (type.equals("question")) {
				auditType = "QUESTION";
				typ = "support";
				userCount = null;
			} else if (type.equals("quote")) {
				auditType = "QUOTE";
				userCount = params.get("userCount");
				typ = "quote";
			} else if (type.equals("download")) {
				auditType = "DWNLD_REQUEST";
				typ = "download";
			} else
				errors.append("Bad type: ").append(type);
		}
		if (errors.length() == 0) {
			long id = db.nextId();
			while (id % 10 == 0 || (id % 100) / 10 == 0)
				id = db.nextId(); //burn ids until the two digits aren'be zero

			String ticket = "3F-" + id;
			StringBuilder body = new StringBuilder("<span style='font-family:courier'>");
			body.append("Ticket: <B>").append(ticket).append("</B><BR>");
			String timestamp = user.formatDateTime(manager.getNow());
			body.append("Time: <B>").append(timestamp).append("</B><BR>");
			if (userCount != null)
				body.append("User Count: <B>").append(userCount).append("</B><BR>");
			body.append("Contact Method: <B>").append(contactMethod).append("</B><BR>");
			for (Tuple2<String, byte[]> i : attachments) {
				body.append("Attached: <PRE>").append(i.getA()).append(" ").append(SH.formatMemory(i.getB().length)).append("  (checksum=").append(Cksum.cksum(i.getB()))
						.append(")").append("</PRE><BR>");
			}
			body.append("Message: <BR><PRE>").append(message).append("</PRE><BR>");
			StringBuilder body2 = new StringBuilder();
			body2.append(body);
			body2.append("<P><U>User Information</U><P>");
			body2.append("Id: <B>").append(user.getId()).append("</B><BR>");
			body2.append("Status: <B>").append(user.getStatusText()).append("</B><BR>");
			body2.append("Email: <B>").append(user.getEmail()).append("</B><BR>");
			body2.append("Name: <B>").append(user.getFirstName()).append(" ").append(user.getLastName()).append("</B><BR>");
			body2.append("Company: <B>").append(user.getCompany()).append("</B><BR>");
			manager.audit(request, user.getUser(), auditType, SH.ddd(ticket + ": " + message, 255));
			Connection conn = null;
			try {
				File attachmentsDir = new File(config.getFilesRoot(), user.getHomeDirectory());
				IOH.ensureDir(attachmentsDir);
				conn = db.getConnection();
				manager.sendEmail(request, user, typ + ".st", CH.m("message", message, "ticket", ticket, "timestamp", timestamp), "support@3forge.com", null);
				Iterable<EmailAttachment> emailAttachments = toEmailAttachments(attachments, ticket, attachmentsDir);
				manager.sendEmailTo3Forge(request, user, body2.toString(), "User " + typ + " Ticket - " + ticket, emailAttachments);
				WebsiteUtils.populateFiles(config, user, "modified");
			} catch (Exception e) {
				LH.warning(log, "error with password update: ", user.getId(), e);
				errors.append("Unknown Error");
			} finally {
				IOH.close(conn);
			}
			request.getOutputStream().append(func).append("onContactSupportResponse(" + ticket + ")");
			user.getClearTempAttachments();
		} else
			request.getOutputStream().append(func).append("(false,'" + errors + "')");
	}

	private List<EmailAttachment> toEmailAttachments(Tuple2<String, byte[]>[] attachments, String ticket, File root) {
		if (attachments.length == 0)
			return Collections.emptyList();
		ArrayList<EmailAttachment> r = new ArrayList<EmailAttachment>(attachments.length);
		int pos = 0;
		for (Tuple2<String, byte[]> i : attachments) {
			pos++;
			File out = new File(root, ticket + "-" + pos + "-" + i.getA());
			try {
				IOH.writeData(out, i.getB());
			} catch (IOException e) {
				LH.warning(log, "Error writing file,", IOH.getFullPath(out), e);
			}

			if (i.getSize() > MAX_ATTACH_TO_EMAIL)
				continue;
			MimeType type = MimeTypeManager.getInstance().getMimeTypeNoThrow(i.getA());
			r.add(new EmailAttachment(i.getB(), type, i.getA()));
		}
		return r;
	}
	public String escape(String unsafe) {
		return unsafe.replace("/&/g", "&amp;").replace("/</g", "&lt;").replace("/>/g", "&gt;").replace("/\"/g", "&quot;").replace("/'/g", "&#039;");
	}
}
