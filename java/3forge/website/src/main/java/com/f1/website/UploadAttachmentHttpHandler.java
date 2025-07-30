package com.f1.website;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.f1.email.MimeType;
import com.f1.email.MimeTypeManager;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.http.impl.HttpMultiPart;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

public class UploadAttachmentHttpHandler extends AbstractSecureHttpHandler {

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {

		Map<String, String> params = request.getParams();
		Integer pos = CH.getOrThrow(Caster_Integer.INSTANCE, params, "pos");
		String detach = CH.getOr(params, "detach", "attach");
		if ("detach".equals(detach)) {
			user.removeTempAttachment(pos);
		} else {
			List<HttpMultiPart> files = (List) request.getParamAsList("file");
			File root = new File(config.getFilesRoot(), user.getHomeDirectory());
			for (HttpMultiPart part : files) {
				String fileName = part.getFileName();
				fileName = HttpUtils.escapeHtml(fileName);
				fileName = SH.afterLast("/", fileName, fileName);
				fileName = SH.afterLast("\\", fileName, fileName);
				MimeType mimeType = MimeTypeManager.getInstance().getMimeTypeNoThrow(fileName); // Note: can also do mimetype check on the client side to avoid unnecessary data transfer.
				if (mimeType == null) {
					request.setResponseType(request.HTTP_400_BAD_REQUEST);
					request.getOutputStream().append("Unsupported file type for: " + fileName);
					continue;
				}
				user.putTempAttachment(pos, fileName, part.getData());
			}
		}
	}
}
