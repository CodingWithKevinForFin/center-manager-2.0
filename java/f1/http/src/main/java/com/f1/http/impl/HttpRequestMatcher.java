package com.f1.http.impl;

import com.f1.http.HttpRequestResponse;

public interface HttpRequestMatcher {

	public boolean canHandle(HttpRequestResponse request);
}
