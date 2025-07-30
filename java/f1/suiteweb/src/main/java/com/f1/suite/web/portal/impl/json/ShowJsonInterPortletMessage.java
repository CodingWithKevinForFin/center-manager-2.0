package com.f1.suite.web.portal.impl.json;

import com.f1.suite.web.portal.InterPortletMessage;

public class ShowJsonInterPortletMessage implements InterPortletMessage {

	final private String json;

	public ShowJsonInterPortletMessage(String json) {
		this.json = json;
	}

	public String getJson() {
		return json;
	}

}
