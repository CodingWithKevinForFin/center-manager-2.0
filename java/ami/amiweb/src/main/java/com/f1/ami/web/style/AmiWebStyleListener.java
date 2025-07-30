package com.f1.ami.web.style;

public interface AmiWebStyleListener {

	public void onStyleValueChanged(AmiWebStyleImpl style, String styleType, short styleKey, Object old, Object nuw);

	void onVarColorRemoved(String key);
	void onVarColorAdded(String key, String color);
	void onVarColorUpdated(String key, String old, String color);
	boolean isVarColorUsed(String color);
}
