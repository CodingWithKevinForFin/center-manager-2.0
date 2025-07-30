package com.f1.website;

import java.io.IOException;
import java.util.Map;

import com.f1.base.Clock;
import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public abstract class ExternalAuthHandler extends AbstractHttpHandler {
	final private Clock clock;

	public ExternalAuthHandler(Clock clock) {
		this.clock = clock;
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		super.handle(request);
		try {
			TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
			TfWebsiteDbService db = manager.getDb();
			Map<String, String> params = request.getParams();
			String username = params.get("username");
			request.setContentType("application/json");
			if (SH.isnt(username)) {
				request.getOutputStream().write(createJsonResponse(WebsiteUtils.CODE_EMAIL_REQUIRED));
				return;
			}
			String password = params.get("password");
			WebsiteUser user = WebsiteUtils.getUser(manager, username, this.clock);
			if (user != null && !user.getEnabled()) {
				request.getOutputStream().write(createJsonResponse(WebsiteUtils.CODE_ACCOUNT_LOCKED));
				return;
			}

			if (user == null || !user.matchesPassword(password)) {
				if (SH.isnt(password))
					request.getOutputStream().write(createJsonResponse(WebsiteUtils.CODE_PASSWORD_REQUIRED));
				else if (user == null)
					request.getOutputStream().write(createJsonResponse(WebsiteUtils.CODE_INVALID_EMAIL_OR_PASSWORD));
				else
					request.getOutputStream().write(createJsonResponse(WebsiteUtils.CODE_INVALID_EMAIL_OR_PASSWORD));

				return;
			} else {
				if (user.getStatus() != 105) {
					request.getOutputStream().write(createJsonResponse(WebsiteUtils.CODE_NON_ENTERPISE));
					return;
				}
				request.getOutputStream().write(createJsonResponse(WebsiteUtils.CODE_LOGIN_SUCCESS));
				manager.audit(request, username, "EXTERNAL_LOGIN_SUCCESS", null);
			}
		} catch (Exception e) {
			WebsiteUtils.handleException(request, e);
			request.getOutputStream().write(createJsonResponse(WebsiteUtils.CODE_UNKNOWN));
			return;
		}
	}
	public byte[] createJsonResponse(short authCode) {
		Map m = new HasherMap<String, String>();
		m.put("status_code", authCode);
		m.put("status_message", WebsiteUtils.externalAuthCode2Message.get(authCode));
		return ObjectToJsonConverter.INSTANCE_CLEAN.object2Bytes(m);
	}
}
