package com.f1.suite.web.portal.impl.form;

import java.util.Map;

import com.f1.suite.web.portal.impl.HtmlPortlet;

public interface HtmlPortletCustomCallbackListener {

	public void onCustomCallback(HtmlPortlet formPortlet, String customType, Object customParamsJson, Map<String, String> rawAttributes);

}
