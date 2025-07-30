package com.f1.http.handler;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.http.HttpHandler;
import com.f1.http.HttpRequestResponse;
import com.f1.utils.concurrent.LinkedHasherMap;

public abstract class AbstractHttpHandler implements HttpHandler {
	private Map<String, String> overriddenResponseHeaders;

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		addOverridenResponseHeaders(req);
	}

	@Override
	public void putOverrideResponseHeader(String key, String value) {
		if (overriddenResponseHeaders == null)
			this.overriddenResponseHeaders = new LinkedHasherMap<String, String>();
		if (value == null) {
			this.removeOverrideResponseHeader(key);
			return;
		}
		this.overriddenResponseHeaders.put(key, value);
	}

	@Override
	public String getOverrideResponseHeader(String key) {
		return this.overriddenResponseHeaders.get(key);
	}

	@Override
	public String removeOverrideResponseHeader(String key) {
		return this.overriddenResponseHeaders.remove(key);
	}

	private void addOverridenResponseHeaders(HttpRequestResponse req) {
		if (overriddenResponseHeaders == null)
			return;
		for (Entry<String, String> e : this.overriddenResponseHeaders.entrySet())
			req.putResponseHeader(e.getKey(), e.getValue());
	}
}
