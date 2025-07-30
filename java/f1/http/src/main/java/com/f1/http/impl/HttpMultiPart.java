package com.f1.http.impl;

import java.util.Map;

public class HttpMultiPart {

	private Map<String, String> params;
	private byte[] data;
	private String contentType;

	public HttpMultiPart(byte[] data, String contentType, Map<String, String> params) {
		this.data = data;
		this.params = params;
		this.contentType = contentType;
	}

	public String getFileName() {
		return params.get("filename");
	}

	public String getName() {
		return params.get("name");
	}

	public String getContentType() {
		return contentType;
	}

	public byte[] getData() {
		return data;
	}

}
