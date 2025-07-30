package com.f1.website;

import java.io.File;
import java.util.logging.Logger;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class DownloadHttpHandler extends AbstractSecureHttpHandler {

	private static final Logger log = LH.get(DownloadHttpHandler.class);

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		final Table t = user.getFiles();
		final Integer id = Integer.parseInt(request.getParams().get("id"));
		Row row = null;
		for (Row r : t.getRows()) {
			if (OH.eq(id, r.get("id"))) {
				row = r;
				break;
			}
		}
		File file = (File) row.get("path");
		String fileName = (String) row.get("name");
		byte[] data = IOH.readData(file);
		HttpUtils.respondWithFile(fileName, data, request);
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		manager.audit(request, user.getUser(), "DOWNLOAD", fileName);
		LH.info(log, "Download for user '", user.getUserName(), "': " + fileName);
	}

}
