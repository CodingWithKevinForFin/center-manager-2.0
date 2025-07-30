package com.f1.website;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class ErrorHandler extends AbstractHttpHandler {
	Set<String> IGNORE_EXTENSIONS = CH.s("png", "gif", "jpg");
	static final private Logger log = LH.get();

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		super.handle(req);
		String uri = req.getRequestUri();
		LH.warning(log, "Unknown uri: " + uri);
		String extension = SH.afterLast(uri, ".", null);
		if (IGNORE_EXTENSIONS.contains(SH.toLowerCase(extension)))
			return;
		req.sendRedirect("/404");
	}

}
