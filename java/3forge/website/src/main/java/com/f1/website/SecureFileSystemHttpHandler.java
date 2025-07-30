package com.f1.website;

import java.io.File;

import com.f1.codegen.CodeCompiler;
import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.FileSystemHttpHandler;
import com.f1.utils.SH;

public class SecureFileSystemHttpHandler extends AbstractSecureHttpHandler {

	private FileSystemHttpHandler inner;

	public SecureFileSystemHttpHandler(File base, String baseUrl, long cacheTimeMs, CodeCompiler compiler) {
		super.setRequiresRequestToken(false);
		this.inner = new FileSystemHttpHandler(false, base, baseUrl, cacheTimeMs, "index.html");
	}

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {

		if (user.getStatus() != WebsiteUser.STATUS_ENTERPRISE) {
			request.sendRedirect("/tutorials_access.html");
			return;
		}
		if (SH.startsWithIgnoreCase(request.getRequestUri(), "/tutorials") && SH.indexOf(request.getRequestUri(), "index.html", 0) != -1)
			config.audit(request, user.getUser(), "TUTORIAL_PAGE_VISIT", request.getRequestUri());

		this.inner.handle(request);
	}

}
