package com.f1.website;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.LH;

public class FaqSearchHandler extends AbstractHttpHandler {

	private static final Logger log = LH.get(FaqSearchHandler.class);

	@Override

	public void handle(HttpRequestResponse req) throws IOException {
		super.handle(req);
		try {
			TfWebsiteManager manager = WebsiteUtils.getConfig(req.getHttpServer());

			// saves search query from faq.html
			Map<String, String> params = req.getParams();
			if (params != null && params.containsKey("keyword"))
				manager.audit(req, "", "FAQ_SEARCH_KEYWORD", params.get("keyword"));

		} catch (Exception e) {

			WebsiteUtils.handleException(req, e);
		}
	}

}
