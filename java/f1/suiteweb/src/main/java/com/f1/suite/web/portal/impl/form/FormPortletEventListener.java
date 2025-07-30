package com.f1.suite.web.portal.impl.form;

import java.util.Map;

public interface FormPortletEventListener extends FormPortletListener {

	public void onFieldEvent(FormPortlet formPortlet, FormPortletField<?> field, String eventType, Map<String, String> attributes);

}
