package com.f1.suite.web;

import java.io.IOException;

import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;

public class ModCountHttpHandler extends AbstractHttpHandler {

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		super.handle(request);
		final WebStatesManager wsm = WebStatesManager.get(request.getSession(false));
		if (wsm != null && wsm.isLoggedIn())
			request.getOutputStream().print(wsm.getModCount());
		else
			request.getOutputStream().print("none");
	}

}
