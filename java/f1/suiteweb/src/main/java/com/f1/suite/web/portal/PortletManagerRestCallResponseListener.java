package com.f1.suite.web.portal;

public interface PortletManagerRestCallResponseListener {

	int CODE_NO_RESPONSE = -1;
	public void onRestCallResponse(int httpCode, String response);
}
