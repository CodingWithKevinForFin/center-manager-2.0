package com.f1.suite.web.portal.impl;

import java.util.Map;

public class PortletNotification {

	final private String title;
	final private String body;
	final private String imageUrl;
	final private String id;
	final private Map<String, String> options;

	public PortletNotification(String id, String title, String body, String imageUrl, Map<String, String> options) {
		this.id = id;
		this.title = title;
		this.body = body;
		this.imageUrl = imageUrl;
		this.options = options;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getId() {
		return this.id;
	}
	public Map<String, String> getOptions() {
		return this.options;
	}

}
