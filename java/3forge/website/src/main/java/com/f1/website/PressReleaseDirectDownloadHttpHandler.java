package com.f1.website;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class PressReleaseDirectDownloadHttpHandler extends AbstractHttpHandler {

	private static final Logger log = LH.get();

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		TfWebsiteManager manager = WebsiteUtils.getConfig(req.getHttpServer());
		Set<String> permittedFiles = CH.s(manager.getPressReleaseDir().list());
		Map<String, String> params = req.getParams();
		String documentName = params.get("document_name");
		try {
			OH.assertTrue(permittedFiles.contains(documentName));
			File file = new File(manager.getPressReleaseDir(), documentName);
			byte[] data = IOH.readData(file);
			HttpUtils.respondWithFile(documentName, data, req);

			Map<Object, Object> content = new HashMap<Object, Object>();
			content.put("documentName", documentName);
			content.put("remoteHost", req.getRemoteHost());
			content.put("userAgent", req.getHeader().get("User-Agent"));

			LH.info(log, "Press Release Direct Download: " + documentName);
			manager.audit(req, "user", "PR_DOWNLOAD_DIRECT", "file: " + documentName);
			manager.sendEmailTo3ForgeWithTemplate(req, null, "pressrelease_direct_internal.st", content, null);
		} catch (Exception e) {
			LH.info(log, "Exception downloading press release document: " + documentName + "\n" + e);
			req.getOutputStream().append("bad url");
		}

	}

}
