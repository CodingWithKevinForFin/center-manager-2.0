package com.f1.http;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.impl.BasicHttpServer;
import com.f1.http.impl.HttpServerSocket;
import com.f1.http.impl.UriRequestMatcher;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.PropertiesBuilder;
import com.f1.utils.PropertyController;
import com.f1.utils.concurrent.FastThreadPool;

public class TestHttpServer extends AbstractHttpHandler {

	public static void main(String a[]) throws IOException {
		FastThreadPool tp = new FastThreadPool(10, "HttpServer");
		tp.start();
		PropertyController pc = new PropertiesBuilder().resolveProperties(false);
		BasicHttpServer s = new BasicHttpServer(tp, pc, new BasicLocaleFormatter(Locale.getDefault(), TimeZone.getDefault(), true, OH.EMPTY_FILE_ARRAY, Collections.EMPTY_MAP));
		HttpServerSocket socket = new HttpServerSocket(9090);
		s.addServerSocket(socket);
		s.addHttpHandler(new UriRequestMatcher(null), new TestHttpServer(), false);
		s.start();
	}
	@Override
	public void handle(HttpRequestResponse httpConnection) throws IOException {
		super.handle(httpConnection);
		System.out.println("cookie:" + httpConnection.getCookies());
		httpConnection.putCookie("Rob", "Cooke" + httpConnection.getCookies(), null, 0, null);
		StringBuilder sb = new StringBuilder();
		sb.append(httpConnection.getMethod()).append("<B>").append(httpConnection.getRequestUri()).append("</B> ? ").append(httpConnection.getQueryString()).append("<BR>");
		sb.append("<table>");
		for (Map.Entry<String, String> e : httpConnection.getParams().entrySet()) {
			sb.append("<TR><TD>").append(e.getKey()).append("<TD>").append(e.getValue());
		}
		sb.append("</table>");
		httpConnection.getOutputStream().print(sb);
	}

}
