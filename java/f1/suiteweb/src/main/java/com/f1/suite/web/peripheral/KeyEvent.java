package com.f1.suite.web.peripheral;

import com.f1.base.ToStringable;
import com.f1.suite.web.portal.Portlet;
import com.f1.utils.structs.table.derived.ToDerivedString;

public class KeyEvent implements ToStringable, ToDerivedString {
	public static final byte KEYDOWN = 0;
	public static final byte KEYUP = 1;
	public static final byte KEYPRESS = 2;
	public static final String ENTER = "Enter";
	public static final String ESCAPE = "Escape";

	final private byte type;
	final private String key;
	final private boolean ctrlKey;
	final private boolean shiftKey;
	final private boolean altKey;
	final private Portlet targetPortlet;
	final private String targetAttachmentId;

	public KeyEvent(String key, boolean ctrlKey, boolean shiftKey, boolean altKey, byte type, Portlet targetPortlet, String targetAttachmentId) {
		this.targetPortlet = targetPortlet;
		this.targetAttachmentId = targetAttachmentId;
		this.key = key;
		this.ctrlKey = ctrlKey;
		this.shiftKey = shiftKey;
		this.altKey = altKey;
		this.type = type;
	}

	public byte getKeyEvent() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public boolean isCtrlKey() {
		return ctrlKey;
	}

	public boolean isShiftKey() {
		return shiftKey;
	}

	public boolean isAltKey() {
		return altKey;
	}
	public boolean isJustCtrlKey() {
		return ctrlKey && !altKey && !shiftKey;
	}

	public boolean isJustShiftKey() {
		return shiftKey && !ctrlKey && !altKey;
	}

	public boolean isJustAltKey() {
		return altKey && !ctrlKey && !shiftKey;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (ctrlKey)
			sink.append("ctrl+");
		if (altKey)
			sink.append("alt+");
		if (shiftKey)
			sink.append("shift+");
		sink.append(key);
		return sink;
	}

	@Override
	public String toDerivedString() {
		return toDerivedString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		sb.append("KeyEvent: ");
		return toString(sb);
	}

	public Portlet getTargetPortlet() {
		return targetPortlet;
	}

	public String getTargetAttachmentId() {
		return targetAttachmentId;
	}

}
