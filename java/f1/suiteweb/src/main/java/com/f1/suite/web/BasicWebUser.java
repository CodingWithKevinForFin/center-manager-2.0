package com.f1.suite.web;

public class BasicWebUser implements WebUser {

	private String userName;

	@Override
	public String getUserName() {
		return userName;
	}

	public BasicWebUser(String userName) {
		this.userName = userName;
	}

}
