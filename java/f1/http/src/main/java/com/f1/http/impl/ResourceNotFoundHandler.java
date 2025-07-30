package com.f1.http.impl;

import java.io.IOException;

import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;

public class ResourceNotFoundHandler extends AbstractHttpHandler {

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		super.handle(req);
		req.setResponseType(BasicHttpRequestResponse.BYTES_HTTP_404_NOT_FOUND);
	}

}
