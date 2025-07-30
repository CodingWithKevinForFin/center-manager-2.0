package com.f1.suite.web.portal.impl.form;

import java.util.Map;

import com.f1.suite.web.JsFunction;

public class FormPortletButtonField extends FormPortletField<String> {

	public static final String JSNAME = "ButtonField";
	private boolean shouldDisableAfterFirstClick = false;
	private boolean disabledDueToClick;

	public FormPortletButtonField(String title) {
		super(String.class, title);
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
	public FormPortletButtonField setValue(String value) {
		super.setValue(value);
		return this;
	}
	@Override
	public FormPortletButtonField setTitle(String title) {
		super.setTitle(title);
		return this;
	}

	@Override
	public FormPortletButtonField setCorrelationData(Object correlationData) {
		super.setCorrelationData(correlationData);
		return this;
	}
	@Override
	public boolean isExportImportSupported() {
		return false;
	}

	@Override
	public FormPortletButtonField setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_OPTIONS))
			new JsFunction(pendingJs, jsObjectName, "shouldDisableAfterFirstClick").addParam(this.shouldDisableAfterFirstClick).addParam(this.disabledDueToClick).end();
		super.updateJs(pendingJs);
	}

	@Override
	public FormPortletButtonField setHeight(int height) {
		super.setHeight(height);
		return this;
	}

	@Override
	public FormPortletButtonField setWidth(int width) {
		super.setWidth(width);
		return this;
	}
	public boolean shouldDisableAfterFirstClick() {
		return this.shouldDisableAfterFirstClick;
	}
	public void setDisableAfterFirstClick(boolean disableAfterFirstClick) {
		if (shouldDisableAfterFirstClick == disableAfterFirstClick)
			return;
		shouldDisableAfterFirstClick = disableAfterFirstClick;
		this.disabledDueToClick = false;
		flagChange(MASK_OPTIONS);
	}

	@Override
	public boolean onMenuItem(Map<String, String> attributes) {
		if (!shouldDisableAfterFirstClick)
			return true;
		if (disabledDueToClick)
			return false;
		this.disabledDueToClick = true;
		return true;
	}

	public void resetDisabledDueToClick() {
		if (!this.disabledDueToClick)
			return;
		this.disabledDueToClick = false;
		flagChange(MASK_OPTIONS);
	}
	public boolean isDisabledDueToClick() {
		return this.disabledDueToClick;
	}
	public boolean setDisabledDueToClick() {
		if (!shouldDisableAfterFirstClick || this.disabledDueToClick)
			return false;
		this.disabledDueToClick = true;
		flagChange(MASK_OPTIONS);
		return true;
	}
}
