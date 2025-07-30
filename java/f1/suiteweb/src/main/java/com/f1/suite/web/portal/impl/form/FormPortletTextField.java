package com.f1.suite.web.portal.impl.form;

import java.util.Map;

import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;

public class FormPortletTextField extends FormPortletTextEditField {

	public static final String JSNAME = "TextField";
	private int maxLength = 1024;
	private boolean isPassword;
	private boolean disabled;
	private boolean hasButton;
	private int[] callbackKeys;
	private boolean pendingSelection;
	private StringBuilder stringBuff;

	public FormPortletTextField(String title) {
		super(String.class, title);
		this.stringBuff = new StringBuilder();
		setDefaultValue("");
	}

	@Override
	public String getjsClassName() {
		return JSNAME;
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_CONFIG)) {
			new JsFunction(pendingJs, jsObjectName, "init").addParam(maxLength).addParam(isPassword()).addParam(hasButton).addParamJson(callbackKeys).addParam(false).end();
		}
		super.updateJs(pendingJs);
	}

	public int getMaxLength() {
		return maxLength;
	}

	public FormPortletTextField setMaxChars(int maxLength) {
		this.maxLength = maxLength;
		flagConfigChanged();
		return this;
	}

	@Override
	public FormPortletTextField setValue(String value) {
		super.setValue(SH.replaceAll(SH.noNull(value), '\r', ""));
		return this;
	}
	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		String type = CH.getOrNoThrow(Caster_String.INSTANCE, attributes, "type", "");
		if (SH.equals(type, "onchange")) {
			String currValue = this.getValue();
			int start = CH.getOr(Caster_Integer.INSTANCE, attributes, "s", 0);
			int end = CH.getOr(Caster_Integer.INSTANCE, attributes, "e", currValue == null ? 0 : currValue.length());
			String change = CH.getOrThrow(Caster_String.INSTANCE, attributes, "c");
			SH.clear(stringBuff);

			if (currValue != null)
				stringBuff.append(currValue);
			try {
				stringBuff.replace(start, end, SH.replaceAll(change, '\r', ""));
			} catch (Exception e) {
				int mod = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "mid");
				throw new RuntimeException("Error with '" + currValue + "'" + " curMod: " + this.getModificationNumber() + " chgmod: " + mod, e);
			}

			if (SH.equals(stringBuff, getValue()))
				return false;
			this.setValueNoFire(stringBuff.toString());
		}
		super.onUserValueChanged(attributes);
		return true;
	}

	public boolean isPassword() {
		return isPassword;
	}

	public FormPortletTextField setPassword(boolean isPassword) {
		if (isPassword == this.isPassword)
			return this;
		this.isPassword = isPassword;
		flagConfigChanged();
		return this;
	}

	public FormPortletTextField setDisabled(Boolean b) {
		if (b == null || this.disabled == b)
			return this;
		this.disabled = b;
		flagConfigChanged();
		return this;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public FormPortletTextField setHasButton(boolean b) {
		if (this.hasButton == b)
			return this;
		this.hasButton = b;
		flagConfigChanged();
		return this;
	}
	public boolean getHasButton() {
		return hasButton;
	}

	public FormPortletTextField setId(String id) {
		super.setId(id);
		return this;
	}

	public void setCallbackKeys(int... asciiCodes) {
		this.callbackKeys = asciiCodes;
		flagConfigChanged();
	}

	public FormPortletTextField setCorrelationData(Object correlationData) {
		super.setCorrelationData(correlationData);
		return this;
	}

	@Override
	public FormPortletTextField setTitleIsClickable(boolean tic) {
		super.setTitleIsClickable(tic);
		return this;
	}

	@Override
	public FormPortletTextField setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public FormPortletTextField setWidth(int width) {
		super.setWidth(width);
		return this;
	}

	@Override
	public FormPortletTextField setHeight(int height) {
		super.setHeight(height);
		return this;
	}

}
