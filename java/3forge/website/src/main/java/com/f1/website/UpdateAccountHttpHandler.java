package com.f1.website;

import java.sql.Connection;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class UpdateAccountHttpHandler extends AbstractSecureHttpHandler {
	private static final Logger log = LH.get();

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		HttpSession session = request.getSession(true);
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		TfWebsiteDbService db = manager.getDb();

		Map<String, String> params = request.getParams();
		StringBuilder errors = new StringBuilder();
		String fname = CreateAccountHttpHandler.getText(params, "fname", "First Name", 64, errors);
		String lname = CreateAccountHttpHandler.getText(params, "lname", "Last Name", 64, errors);
		String phone = CreateAccountHttpHandler.get(params, "phone", "Phone Number", 20, errors);
		String company = CreateAccountHttpHandler.getText(params, "company", "Company", 64, errors);
		String role = CreateAccountHttpHandler.getText(params, "role", "Title/Role", 64, errors);
		if (errors.length() == 0 && !CreateAccountHttpHandler.verifyPhone(phone))
			errors.append("Invalid Phone Number. ");
		if (errors.length() == 0) {
			Connection conn = null;
			TfWebsiteUser existing = null;
			try {
				conn = db.getConnection();
				existing = db.queryUser(user.getId(), conn);
				StringBuilder changes = new StringBuilder();
				existing.setFirstName(changed("First Name", existing.getFirstName(), fname, changes));
				existing.setLastName(changed("Last Name", existing.getLastName(), lname, changes));
				existing.setPhone(changed("Phone Number", existing.getPhone(), phone, changes));
				existing.setCompany(changed("Company", existing.getCompany(), company, changes));
				existing.setRole(changed("Title/Role", existing.getRole(), role, changes));
				if (changes.length() > 0) {
					existing.setRevision(existing.getRevision() + 1);
					existing.setModifiedOn(manager.getNow());
					db.addUser(request, existing, "UPDATE_ACCOUNT", conn);
					manager.sendEmail(request, user, "accountupdate.st", CH.m("changes", changes), "noreply@3forge.com", null);
					WebsiteUser user2 = new WebsiteUser(existing, manager.getClock(), manager.createFormatter());
					WebsiteUtils.setUser(session, user2);
					WebsiteUtils.setLoggedIn(session, true);
				} else
					errors.append("No changes made");
			} catch (Exception e) {
				LH.warning(log, "error with password update: ", user.getId(), e);
				errors.append("Unknown Error");
			} finally {
				IOH.close(conn);
			}
		}
		if (errors.length() > 0)
			request.getOutputStream().append("onChangeAccountResponse(false,'" + errors + "');");
		else {
			request.getOutputStream().append("onChangeAccountResponse(true,'');");
		}
	}
	private String changed(String description, String old, String nuw, StringBuilder changes) {
		if (OH.ne(old, nuw))
			changes.append("<BR>* ").append(description).append(" changed from '").append(old).append("' to '").append(nuw).append("'<BR>");
		return nuw;
	}
}
