package com.f1.suite.web.portal;

import java.util.Map;

import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;

public interface PortletManagerListener extends PortletBackendListener {

	void onPageLoading(PortletManager basicPortletManager, Map<String, String> attributes, HttpRequestResponse action);
	void onFrontendCalled(PortletManager manager, Map<String, String> attributes, HttpRequestAction action);
	void onInit(PortletManager manager, Map<String, Object> configuration, String rootId);
	void onPageRefreshed(PortletManager basicPortletManager);
	void onMetadataChanged(PortletManager basicPortletManager);
	void onPortletManagerClosed();
}
