package com.f1.suite.web.portal.impl.form;

import java.util.Map;

public interface FormPortletListener {

	public void onButtonPressed(FormPortlet portlet, FormPortletButton button);
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes);
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition);
}
