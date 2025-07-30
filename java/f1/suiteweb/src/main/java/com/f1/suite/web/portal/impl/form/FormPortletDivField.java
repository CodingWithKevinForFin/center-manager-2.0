package com.f1.suite.web.portal.impl.form;

import java.util.Map;

public class FormPortletDivField extends FormPortletField<String> {
	public static final String JSNAME = "DivField";

	public FormPortletDivField(String title) {
		super(String.class, title);
		setBorderColor(null);
		this.setDefaultValue("");
	}

	@Override
	public String getjsClassName() {
		return JSNAME;
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		return false;
	}

	@Override
	public boolean canFocus() {
		return false;
	}
}
