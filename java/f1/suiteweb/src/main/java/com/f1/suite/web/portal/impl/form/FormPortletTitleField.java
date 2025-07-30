package com.f1.suite.web.portal.impl.form;

import java.util.Map;

public class FormPortletTitleField extends FormPortletField<String> {

	public static final String JSNAME = "TitleField";

	public FormPortletTitleField(String title) {
		super(String.class, "");
		super.setBorderColor(null);
		setValue(title);
		setDefaultValue("");
	}
	@Override
	public String getjsClassName() {
		return JSNAME;
	}

	@Override
	public FormPortletTitleField setValue(String value) {
		super.setValue(value);
		return this;
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		return false;
	}

	public FormPortletTitleField setId(String id) {
		super.setId(id);
		return this;
	}

	@Override
	public boolean isExportImportSupported() {
		return false;
	}

	public FormPortletTitleField setStyle(String string) {
		setCssStyle(string);
		return this;
	}

	@Override
	public FormPortletTitleField setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public FormPortletTitleField setHeight(int height) {
		super.setHeight(height);
		return this;
	}

	@Override
	public FormPortletTitleField setWidth(int width) {
		super.setWidth(width);
		return this;
	}

	@Override
	public boolean canFocus() {
		return false;
	}

}
