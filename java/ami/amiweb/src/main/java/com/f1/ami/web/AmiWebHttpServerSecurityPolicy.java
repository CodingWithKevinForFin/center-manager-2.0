package com.f1.ami.web;

import java.util.Set;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpServer;
import com.f1.http.impl.HttpServerSecurityPolicy;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class AmiWebHttpServerSecurityPolicy implements HttpServerSecurityPolicy {
	//REFER to TfWebsiteManager
	private static final Logger log = LH.get();
	final private String hostname;
	final private Set<String> httpMethods;
	private TextMatcher hostnameMatcher;

	public AmiWebHttpServerSecurityPolicy(String hostname, String httpMethods) {
		this.hostname = hostname;
		if (SH.is(hostname))
			this.hostnameMatcher = SH.m(hostname);
		this.httpMethods = SH.splitToSet(",", SH.toUpperCase(httpMethods));
	}

	@Override
	public int checkRequest(HttpServer server, HttpRequestResponse request) {
		String secPurpose = request.getHeader().get("Sec-Purpose");
		if (SH.indexOf(secPurpose, "prefetch", 0) != -1) {
			LH.info(log, "Request From ", request.getRemoteHost(), " has Sec-Purpose header with prefetch so returning FORBIDDEN (403) for " + request.getRequestUrl());
			return HttpRequestResponse.HTTP_403_FORBIDDEN;
		}
		//Different from TfWebsiteManager because we have to support older configurations that don't have the http hostname set
		if (this.hostnameMatcher != null && !hostnameMatcher.matches(request.getHost())) {
			LH.info(log, "Request From ", request.getRemoteHost(), " has invalid host '", request.getHost(), "' vs expected '", this.hostname, "'. Returning UNAUTHORIZED (401)");
			return HttpRequestResponse.HTTP_401_UNAUTHORIZED;
		}
		//		if (OH.ne(HttpRequestResponse.POST, request.getMethod()) && OH.ne(HttpRequestResponse.GET, request.getMethod())) {
		if (!httpMethods.contains(request.getMethod())) {
			LH.info(log, "Request From ", request.getRemoteHost(), " has invalid method '", request.getMethod(), "' vs expected 'POST' or 'GET'. Returning BAD_REQUEST (400)");
			return HttpRequestResponse.HTTP_400_BAD_REQUEST;
		}
		return HttpRequestResponse.HTTP_200_OK;

	}

}
