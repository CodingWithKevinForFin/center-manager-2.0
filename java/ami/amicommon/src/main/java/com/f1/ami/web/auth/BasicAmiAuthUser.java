package com.f1.ami.web.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicAmiAuthUser implements AmiAuthUser {

	final private String userName;
	//	final private String firstName;
	//	final private String lastName;
	//	final private String phoneNumber;
	//	final private String email;
	//	final private String company;
	final private Map<String, Object> authAttributes;

	public BasicAmiAuthUser(String userName, Map<String, Object> authAttributes) {
		this.userName = userName;

		this.authAttributes = new HashMap<String, Object>();
		if (authAttributes != null)
			this.authAttributes.putAll(authAttributes);
	}
	@Deprecated
	public BasicAmiAuthUser(String userName, String firstName, String lastName, String phoneNumber, String email, String company, List<AmiAuthAttribute> authAttributes) {
		this.userName = userName;
		this.authAttributes = new HashMap<String, Object>();
		for (AmiAuthAttribute i : authAttributes)
			this.authAttributes.put(i.getKey(), i.getValue());
	}

	@Override
	public String getUserName() {
		return userName;
	}
	//
	//	@Override
	//	public String getFirstName() {
	//		return firstName;
	//	}
	//
	//	@Override
	//	public String getLastName() {
	//		return lastName;
	//	}
	//
	//	@Override
	//	public String getPhoneNumber() {
	//		return phoneNumber;
	//	}
	//
	//	@Override
	//	public String getEmail() {
	//		return email;
	//	}
	//
	//	@Override
	//	public String getCompany() {
	//		return company;
	//	}

	@Override
	public Map<String, Object> getAuthAttributes() {
		return authAttributes;
	}

}
