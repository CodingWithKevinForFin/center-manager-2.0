package com.f1.suite.web.portal.impl.form;

import java.util.Arrays;
import java.util.Map;

import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;

public class FormPortletTextAreaField extends FormPortletTextEditField {

	public static final String JSNAME = "TextAreaField";
	private int cursorPos = -1;
	private int[] callbackKeys;
	private boolean disabled;
	private boolean hasButton;
	private StringBuilder stringBuff;

	public FormPortletTextAreaField(String title) {
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
		if (hasChanged(MASK_CONFIG))
			new JsFunction(pendingJs, jsObjectName, "init").addParam(hasButton).addParamJson(callbackKeys).end();
		super.updateJs(pendingJs);
	}
	@Override
	public void handleCallback(String action, Map<String, String> attributes) {
		if (action.equals("updateCursor")) {
			this.cursorPos = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "pos");
		} else
			super.handleCallback(action, attributes);
	}
	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {

		String type = CH.getOrNoThrow(Caster_String.INSTANCE, attributes, "type", "");
		if (SH.equals(type, "onchange")) {
			int start = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "s");
			int end = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "e");
			String change = CH.getOrThrow(Caster_String.INSTANCE, attributes, "c");
			SH.clear(stringBuff);
			if (this.getValue() != null)
				stringBuff.append(this.getValue());
			stringBuff.replace(start, end, SH.replaceAll(change, '\r', ""));
			if (SH.equals(stringBuff, getValue()))
				return false;
			this.setValueNoFire(stringBuff.toString());
		}
		return true;
	}

	@Override
	public FormPortletTextAreaField setValue(String text) {
		super.setValue(SH.replaceAll(SH.noNull(text), '\r', ""));
		return this;
	}
	public void moveCursor(int x, int y) {
		final String val = getValue();
		if (val == null)
			return;
		final int len = val.length();
		int i = 0;
		while (i < len && y > 0) {
			int j = val.indexOf('\n', i);
			if (j == -1) {
				i = len;
				break;
			}
			y--;
			i = j + 1;
		}
		if (i < len) {
			int j = val.indexOf('\n', i);
			if (j == -1) {
				i = len;
			} else {
				if (j - i < x)
					x = j - i;
				i += x;
			}
		}
		setCursorPosition(i);
	}

	public FormPortletTextAreaField setId(String id) {
		super.setId(id);
		return this;
	}

	public void setCallbackKeys(int... asciiCodes) {
		if (Arrays.equals(asciiCodes, this.callbackKeys))
			return;
		this.callbackKeys = asciiCodes;
		flagConfigChanged();
	}

	public FormPortletTextAreaField setDisabled(Boolean b) {
		if (b == null || this.disabled == b)
			return this;
		this.disabled = b;
		flagConfigChanged();
		return this;
	}
	public boolean isDisabled() {
		return disabled;
	}

	public FormPortletTextAreaField setHasButton(boolean b) {
		if (this.hasButton == b)
			return this;
		this.hasButton = b;
		flagConfigChanged();
		return this;
	}
	public boolean getHasButton() {
		return hasButton;
	}

	public void insertTextNoThrow(int position, String text) {
		if (getValue() == null)
			setValue(text);
		else if (position == -1 || position >= getValue().length())
			setValue(getValue() + text);
		else
			setValue(SH.splice(getValue(), position, 0, text));
	}

	@Override
	public FormPortletTextAreaField setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public FormPortletTextAreaField setHeight(int height) {
		super.setHeight(height);
		return this;
	}

	@Override
	public FormPortletTextAreaField setWidth(int width) {
		super.setWidth(width);
		return this;
	}
}
