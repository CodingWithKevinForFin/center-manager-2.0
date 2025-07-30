package com.f1.http;

import java.io.IOException;

public interface HttpHandler {

	void handle(HttpRequestResponse req) throws IOException;
	/*
	 * This will override the default response headers configured in the httpserver
	 */
	void putOverrideResponseHeader(String key, String value);
	String getOverrideResponseHeader(String key);
	String removeOverrideResponseHeader(String key);

}
