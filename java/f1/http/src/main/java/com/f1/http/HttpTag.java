package com.f1.http;

import java.util.Map;

import com.f1.utils.OH;

public class HttpTag {
	static final public byte TYPE_OPEN = 1;
	static final public byte TYPE_CLOSE = 2;
	static final public byte TYPE_SIMPLE = 3;
	final private Map<String, String> attributes;
	final private String name;
	final private byte type;

	public HttpTag(byte type, String name, Map<String, String> attributes) {
		this.type = type;
		this.name = name;
		this.attributes = attributes;
	}

	public String getName() {
		return name;
	}

	public String getRequired(String attribute) {
		final String r = attributes.get(attribute);
		if (r == null)
			throw new RuntimeException("required attribute missing for tag " + name + ": " + attribute);
		return r;

	}

	public String toString() {
		return name;
	}

	public int getType() {
		return type;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String getOptional(String string, String deflt) {
		return OH.noNull(attributes.get(string), deflt);
	}

}
