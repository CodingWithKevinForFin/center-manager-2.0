package com.f1.http.impl;

import com.f1.http.HttpRequestResponse;
import com.f1.utils.TextMatcher;

public class UriRequestMatcher implements HttpRequestMatcher {

	final private TextMatcher matcher;

	public UriRequestMatcher(TextMatcher matcher) {
		this.matcher = matcher;//SH.m(expression);
	}

	@Override
	public boolean canHandle(HttpRequestResponse request) {
		return matcher.matches(request.getRequestUri());
	}

}
