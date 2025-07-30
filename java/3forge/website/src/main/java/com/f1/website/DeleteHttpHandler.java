package com.f1.website;

import java.io.File;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.http.HttpRequestResponse;
import com.f1.utils.OH;

public class DeleteHttpHandler extends AbstractSecureHttpHandler {

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		final Table t = user.getFiles();
		final int id = Integer.parseInt(request.getParams().get("id"));
		Row row = null;
		for (Row r : t.getRows()) {
			if (OH.eq(id, r.get("id"))) {
				row = r;
				break;
			}
		}
		File file = (File) row.get("path");
		if (file.canWrite())
			file.delete();
		else
			throw new RuntimeException("can not modify:" + file);
		config.audit(request, user.getUser(), "USER_DELETE_FILE", file.getName());
		request.sendRedirect("secure_files");
	}

}
