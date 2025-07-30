package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.InterPortletMessage;

public class SetHtmlInterPortletMessage implements InterPortletMessage {

	final private String html;

	public SetHtmlInterPortletMessage(String html) {
		this.html = html;
	}

	public String getHtml() {
		return html;
	}

}
