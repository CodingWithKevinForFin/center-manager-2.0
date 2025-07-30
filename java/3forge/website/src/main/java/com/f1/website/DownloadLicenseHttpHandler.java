package com.f1.website;

import java.io.FileInputStream;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.http.HttpRequestResponse;
import com.f1.utils.ContentType;
import com.f1.utils.IOH;

public class DownloadLicenseHttpHandler extends AbstractSecureHttpHandler {

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		final Table t = user.getLicenses();
		final int id = Integer.parseInt(request.getParams().get("id"));
		Row row = t.getRows().get(id);
		String file = (String) row.get("path");
		String fileName = (String) row.get("name");
		String expires = (String) row.get("expires");
		ContentType mimetype = ContentType.getTypeByFileExtension("txt");

		request.setContentTypeAsBytes((mimetype != null) ? mimetype.getMimeTypeAsBytes() : "application/octet-stream".getBytes());
		request.putResponseHeader("Content-Disposition", "attachment; filename=\"" + "f1license.txt" + "\"");
		config.audit(request, user.getUser(), "VIEW_LICENSE", fileName);
		IOH.pipe(new FileInputStream(file), request.getOutputStream());
	}

}
