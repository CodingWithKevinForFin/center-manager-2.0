package com.f1.suite.web.portal;


public class BasicPortletDownload implements PortletDownload {

	private String name;
	private byte[] data;

	public BasicPortletDownload(String name, byte[] data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public byte[] getData() {
		return data;
	}

}
