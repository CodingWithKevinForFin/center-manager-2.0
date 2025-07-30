package com.f1.website;

import java.sql.Connection;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.HttpUtils;
import com.f1.utils.IOH;
import com.f1.utils.LH;

public abstract class DownloadTrialHttpHandler extends AbstractSecureHttpHandler {
	private static final Logger log = LH.get();

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		HttpSession session = request.getSession(true);
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		TfWebsiteDbService db = manager.getDb();

		switch (user.getStatus()) {
			case WebsiteUser.STATUS_NEW:
				WebsiteUtils.redirectToAction(request, "You need to verify your email");
				return;
			case WebsiteUser.STATUS_ENDED_TRIAL:
				WebsiteUtils.redirectToAction(request, "Your trial period has ended");
				return;
			case WebsiteUser.STATUS_VERIFIED: {
				Connection conn = null;
				TfWebsiteUser existing = null;
				try {
					conn = db.getConnection();
					existing = db.queryUser(user.getId(), conn);
					if (existing.getStatus() == WebsiteUser.STATUS_VERIFIED) {
						existing.setRevision(existing.getRevision() + 1);
						existing.setModifiedOn(manager.getNow());
						existing.setTrialExpiresOn(manager.getNow() + manager.getTrialPeriodMs());
						existing.setStatus(WebsiteUser.STATUS_STARTED_TRIAL);
						db.addUser(request, existing, "TRIAL_STARTED", conn);
					}

				} catch (Exception e) {
					LH.warning(log, "error with user: ", user.getId(), e);
					WebsiteUtils.redirectToLogin(request, existing.getUsername(), "", "Unknown error");
					return;
				} finally {
					IOH.close(conn);
				}
				WebsiteUtils.setUser(session, new WebsiteUser(existing, user.getClock(), user.getFormatter()));
				WebsiteUtils.redirectToAction(request, "Email Address Verified: " + user.getEmail());
			}
			case WebsiteUser.STATUS_ENTERPRISE:
			case WebsiteUser.STATUS_STARTED_TRIAL: {
				try {
					String fileName = "file.txt";
					HttpUtils.respondWithFile(fileName, "this is a test".getBytes(), request);
					manager.audit(request, user.getUser(), "TRIAL_DOWNLOAD", "file=" + fileName);
				} catch (Exception e) {
					LH.warning(log, "error with user: ", user.getId(), e);
					WebsiteUtils.redirectToLogin(request, user.getUserName(), "", "Unknown file error");
				}
				break;
			}
			default:
				WebsiteUtils.redirectToLogin(request, user.getUserName(), "", "Unknown status: " + user.getStatus());
		}

	}
}
