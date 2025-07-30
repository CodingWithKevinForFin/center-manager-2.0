package com.f1.website;

import java.io.IOException;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.impl.BasicHttpRequestResponse;
import com.f1.utils.LH;

public class RedirectionHttpHandler extends AbstractHttpHandler {

	final private String destination;
	private static final Logger log = Logger.getLogger(BasicHttpRequestResponse.class.getName());

	public RedirectionHttpHandler(String destination) {
		this.destination = destination;
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
		super.handle(request);

		if (destination.contains("platform-overview")) {
			manager.audit(request, "", "REDIRECT PRODUCTS", request.getRemoteHost());
			LH.info(log, "REDIRECT PRODUCTS: " + request.getRemoteHost());
		}
		request.sendRedirect(destination);
	}

}