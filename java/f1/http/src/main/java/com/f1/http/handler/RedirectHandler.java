package com.f1.http.handler;

import java.io.IOException;

import com.f1.http.HttpRequestResponse;

public class RedirectHandler extends AbstractHttpHandler {

	final private String destination;

	public RedirectHandler(String destination) {
		this.destination = destination;
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		super.handle(request);
		request.sendRedirect(destination);
	}

}
