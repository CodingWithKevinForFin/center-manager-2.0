package com.f1.suite.web.portal.impl.form;

import java.util.Map;

public interface FormPortletCustomCallbackListener {

	public void onCustomCallback(FormPortlet formPortlet, String customType, Object customParamsJson, Map<String, String> rawAttributes);

}
