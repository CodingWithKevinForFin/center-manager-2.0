package com.f1.suite.web;

public interface UserLoginListener {

	//should return url where to redirect to
	String onLoginSuccess(HttpRequestAction request, WebState state, boolean isRelogin);

}
