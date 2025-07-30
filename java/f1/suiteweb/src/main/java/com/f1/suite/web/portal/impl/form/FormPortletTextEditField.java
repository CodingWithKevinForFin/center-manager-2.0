package com.f1.suite.web.portal.impl.form;

import java.util.Map;

import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

public abstract class FormPortletTextEditField extends FormPortletField<String> {

	private int cursorPos;
	private int selStart;
	private int selEnd;

	public FormPortletTextEditField(Class<String> type, String title) {
		super(type, title);
	}

	final public void setSelection(int start, int end) {
		start = clipCursor(start);
		end = clipCursor(end);
		this.selStart = start;
		this.selEnd = end;
		flagChange(MASK_SELECTION);
	}

	final public void setCursorPosition(int cursorPosition) {
		cursorPosition = clipCursor(cursorPosition);
		if (this.cursorPos == cursorPosition)
			return;
		this.cursorPos = cursorPosition;
		flagChange(MASK_CURSOR);
	}

	private int clipCursor(int cp) {
		String t = this.getValue();
		if (t == null)
			return 0;
		return MH.clip(cp, 0, t.length());
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_SELECTION))
			new JsFunction(pendingJs, jsObjectName, "changeSelection").addParam(selStart).addParam(selEnd).end();
		super.updateJs(pendingJs);
		if (hasChanged(MASK_CURSOR))
			new JsFunction(pendingJs, jsObjectName, "moveCursor").addParam(cursorPos).end();
	}
	@Override
	public void handleCallback(String action, Map<String, String> attributes) {
		if (action.equals("updateCursor")) {
			this.cursorPos = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "pos");
		} else
			super.handleCallback(action, attributes);
	};

	final public void insertAtCursor(String value) {
		if (value != null) {
			final String t = getValue();
			if (t == null) {
				setValue(value.toString());
				this.cursorPos = getValue().length();
			} else if (value.length() > 0) {
				int pos = getCursorPosition();
				setValue(SH.splice(t, pos, 0, value));
				setCursorPosition(pos + value.length());
			}
		}
	}

	final public int getCursorPosition() {
		return clipCursor(cursorPos);
	}

	public void setCursorPositionNoFire(int cursorPosition) {
		cursorPosition = clipCursor(cursorPosition);
		if (this.cursorPos == cursorPosition)
			return;
		this.cursorPos = cursorPosition;
	}
}
