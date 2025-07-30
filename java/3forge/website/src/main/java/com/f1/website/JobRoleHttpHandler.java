package com.f1.website;

import java.io.IOException;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.LH;
import com.f1.utils.casters.Caster_Byte;

public class JobRoleHttpHandler extends AbstractHttpHandler {

	private static final Logger log = LH.get(JobRoleHttpHandler.class);

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		try {
			TfWebsiteManager manager = WebsiteUtils.getConfig(req.getHttpServer());
			// role id must match to the corresponding roles in jobs.html page.
			byte roleId = Caster_Byte.PRIMITIVE.cast(req.getParams().get("role"));
			switch (roleId) {
				case 1:
					manager.audit(req, "", "JOB_ROLE_CLICKED", "Technology Summer Internship - NYC");
					break;
				case 2:
					manager.audit(req, "", "JOB_ROLE_CLICKED", "Jr Solutions Engineer Full Time - NYC");
					break;
				case 3:
					manager.audit(req, "", "JOB_ROLE_CLICKED", "Technology Summer Internship - London");
					break;
				case 4:
					manager.audit(req, "", "JOB_ROLE_CLICKED", "Jr Solutions Engineer Full Time - London");
					break;
				case 5:
					manager.audit(req, "", "JOB_ROLE_CLICKED", "Technology Summer Internship - Singapore");
					break;
				case 6:
					manager.audit(req, "", "JOB_ROLE_CLICKED", "Jr Solutions Engineer Full Time - Singapore");
					break;
				case 7:
					manager.audit(req, "", "JOB_ROLE_CLICKED", "Jr Solutions Engineer Full Time - Toronto");
					break;
				default:
					LH.warning(log, "Unknown Role, role: " + roleId);
					manager.audit(req, "", "JOB_ROLE_CLICKED", "Unknown Role: " + roleId);
					break;
			}
		} catch (Exception e) {
			LH.warning(log, "Exception handling job role click, role: " + req.getParams().get("role"));
			WebsiteUtils.handleException(req, e);
		}
	}
}
