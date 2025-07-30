package com.f1.http.impl;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpServer;

public interface HttpServerSecurityPolicy {

	//return 200 (HttpRequestResponse.HTTP_200_OK if this request passes the security policy,otherwise the error to return
	public int checkRequest(HttpServer server, HttpRequestResponse req);

}
