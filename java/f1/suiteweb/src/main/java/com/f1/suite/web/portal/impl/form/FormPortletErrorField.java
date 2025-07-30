package com.f1.suite.web.portal.impl.form;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.OH;

public class FormPortletErrorField<TYPE> extends FormPortletField<TYPE> {

	private FormPortletField<TYPE> field;
	private List<String> errors = Collections.EMPTY_LIST;;

	public FormPortletErrorField(FormPortletField<TYPE> field) {
		super(field.getType(), field.getTitle());
		this.field = field;
	}

	@Override
	public void rebuildJs(StringBuilder pendingJs) {
		super.rebuildJs(pendingJs);
		field.rebuildJs(pendingJs);
		if (CH.isntEmpty(errors)) {
			JsFunction f = new JsFunction(pendingJs, jsObjectName, "getElement().appendChild(makeErrorIcon");
			f.startJson().addQuoted(errors);
			f.close().end();
		}
	}

	@Override
	public String getjsClassName() {
		return field.getjsClassName();
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		return field.onUserValueChanged(attributes);
	}

	@Override
	public FormPortletField<TYPE> setValue(TYPE value) {
		field.setValue(value);
		return this;
	}

	@Override
	public TYPE getValue() {
		return field.getValue();
	}

	@Override
	public TYPE getDefaultValue() {
		return field.getDefaultValue();
	}

	@Override
	public FormPortletErrorField<TYPE> setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public String getName() {
		return field.getName();
	}

	public void setErrors(List<String> errors) {
		if (OH.eq(this.errors, errors))
			return;
		this.errors = errors;
		flagChange(MASK_VALUE);
	}

	@Override
	public FormPortletField<TYPE> setDisabled(Boolean disabled) {
		if (disabled == null)
			return this;
		field.setDisabled(disabled);
		return this;
	}

	@Override
	public boolean isDisabled() {
		return field.isDisabled();
	}

	@Override
	public FormPortletField<TYPE> setVisible(boolean visible) {
		field.setVisible(visible);
		return this;
	}

	@Override
	public boolean isVisible() {
		return field.isVisible();
	}

	@Override
	public FormPortletField<TYPE> setCorrelationData(Object correlationData) {
		field.setCorrelationData(correlationData);
		return this;
	}

	@Override
	public Object getCorrelationData() {
		return field.getCorrelationData();
	}

	@Override
	public FormPortletField<TYPE> setHelp(String help) {
		field.setHelp(help);
		return this;
	}

	@Override
	public String getHelp() {
		return field.getHelp();
	}

	public FormPortletField<TYPE> getField() {
		return field;
	}

	@Override
	public String getJsValue() {
		return field.getJsValue();
	}

	@Override
	public boolean isExportImportSupported() {
		return false;
	}

	@Override
	public void setForm(FormPortlet form) {
		super.setForm(form);
		this.field.setForm(form);
	}
}
