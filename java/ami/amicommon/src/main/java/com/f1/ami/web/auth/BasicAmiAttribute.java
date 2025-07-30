package com.f1.ami.web.auth;

public class BasicAmiAttribute implements AmiAuthAttribute {

	final private String key;
	final private Object value;

	public BasicAmiAttribute(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
