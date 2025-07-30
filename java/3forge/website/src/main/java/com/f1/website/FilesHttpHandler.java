package com.f1.website;

import com.f1.http.HttpRequestResponse;
import com.f1.utils.CH;

public class FilesHttpHandler extends AbstractSecureHttpHandler {

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		String sort = CH.getOr(request.getParams(), "sort", "modified");

		WebsiteUtils.populateFiles(config, user, sort);
		request.forward("secure_files.htm");
	}

}
