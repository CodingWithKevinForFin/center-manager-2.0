/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web;

import com.f1.base.Message;
import com.f1.http.HttpRequestResponse;
import com.f1.utils.OH;

public abstract class HttpRequestAction implements Message {

	abstract public HttpRequestResponse getRequest();

	abstract public void setRequest(HttpRequestResponse request);

	public HttpRequestAction clone() {
		try {
			return (HttpRequestAction) super.clone();
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
}
