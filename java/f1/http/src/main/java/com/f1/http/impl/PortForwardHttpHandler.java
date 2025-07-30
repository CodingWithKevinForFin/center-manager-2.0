package com.f1.http.impl;

import java.io.IOException;
import java.util.concurrent.Executor;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;

public class PortForwardHttpHandler extends AbstractHttpHandler {

	private final int securePort;

	public PortForwardHttpHandler(int securePort) {
		this.securePort = securePort;
	}

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		String qs = req.getQueryString();
		if (qs == null && req.getRequestContentLength() > 0)
			qs = new String(req.getRequestContentBuffer(), 0, req.getRequestContentLength());
		String s = HttpUtils.buildUrl(true, req.getHost(), securePort, req.getRequestUri(), qs);
		req.sendRedirect(s);
	}

	public static void forward(HttpServerSocket socket, int port, Executor tp) throws IOException {
		final BasicHttpServer s2 = new BasicHttpServer();
		s2.setThreadPool(tp);
		s2.addServerSocket(socket);
		PortForwardHttpHandler t = new PortForwardHttpHandler(port);
		s2.addHttpHandler("*", t, true);
		s2.start();
	}
}
