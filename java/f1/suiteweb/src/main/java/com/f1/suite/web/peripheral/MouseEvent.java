package com.f1.suite.web.peripheral;

import com.f1.base.ToStringable;

public class MouseEvent implements ToStringable {
	public static final byte CLICK = 0;
	public static final byte CONTEXTMENU = 1;
	public static final byte DOUBLECLICK = 2;
	public static final byte MOUSEDOWN = 3;
	public static final byte MOUSEUP = 4;
	private byte mouseEventType;
	private boolean ctrlKey;
	private boolean altKey;
	private boolean shiftKey;
	private int x;
	private int y;
	private int button;

	public MouseEvent(byte mouseEventType, int b, int x, int y, boolean ctrlKey, boolean shiftKey, boolean altKey) {
		this.mouseEventType = mouseEventType;
		this.button = b;
		this.x = x;
		this.y = y;
		this.ctrlKey = ctrlKey;
		this.altKey = altKey;
		this.shiftKey = shiftKey;
	}
	public byte getMouseEvent() {
		return mouseEventType;
	}
	public int getButton() {
		return button;
	}
	public int getMouseX() {
		return x;
	}
	public int getMouseY() {
		return y;
	}
	public boolean isShiftKey() {
		return shiftKey;
	}
	public boolean isAltKey() {
		return altKey;
	}
	public boolean isCtrlKey() {
		return ctrlKey;
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
		sink.append("MouseEvent:");
		sink.append(mouseEventType);
		if (ctrlKey)
			sink.append("+ctrl");
		if (altKey)
			sink.append("+alt");
		if (shiftKey)
			sink.append("+shift");
		return sink;
	}

}
