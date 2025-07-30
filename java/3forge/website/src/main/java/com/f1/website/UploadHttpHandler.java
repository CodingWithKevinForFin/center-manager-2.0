package com.f1.website;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.f1.http.HttpRequestResponse;
import com.f1.http.impl.HttpMultiPart;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class UploadHttpHandler extends AbstractSecureHttpHandler {

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {

		Map<String, String> params = request.getParams();
		List<HttpMultiPart> files = (List) request.getParamAsList("file");

		File root = new File(config.getFilesRoot(), user.getHomeDirectory());
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		for (HttpMultiPart part : files) {
			String fileName = part.getFileName();
			fileName = SH.afterLast("/", fileName, fileName);
			fileName = SH.afterLast("\\", fileName, fileName);
			String newFileName = fileName;
			File file = null;
			for (int i = 2;; i++) {
				file = new File(root, newFileName);
				if (file.exists() && !file.canWrite()) {
					if (fileName.indexOf('.') == -1)
						newFileName = fileName + i;
					else
						newFileName = SH.beforeLast(fileName, '.') + i + "." + SH.afterLast(fileName, '.');
				} else
					break;
			}
			if (OH.eq(fileName, newFileName))
				manager.audit(request, user.getUser(), "UPLOAD", fileName);
			else
				manager.audit(request, user.getUser(), "UPLOAD", fileName + " as " + newFileName);

			IOH.writeData(file, part.getData());
		}

		String sessionSectoken = WebsiteUtils.getSecureToken(request.getSession(false));
		request.sendRedirect("secure_files?tok=" + sessionSectoken);
	}

}
