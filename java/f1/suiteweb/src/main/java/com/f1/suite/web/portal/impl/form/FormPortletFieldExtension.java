package com.f1.suite.web.portal.impl.form;

import java.util.Map;

public interface FormPortletFieldExtension {
	public String getJsObjectName();
	public void onUserValueChanged(Map<String, String> attributes);
	public void rebuildJs(StringBuilder pendingJs);
	public void updateJs(StringBuilder pendingJs);
	public int getExtensionIndex();
	public void handleCallback(String callback, Map<String, String> attributes);
	public boolean hasUpdate();

}
