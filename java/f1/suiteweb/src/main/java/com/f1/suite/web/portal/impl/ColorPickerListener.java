package com.f1.suite.web.portal.impl;

public interface ColorPickerListener {

	public void onColorChanged(ColorPickerPortlet target, String oldColor, String nuwColor);

	public void onOkayPressed(ColorPickerPortlet target);
	public void onCancelPressed(ColorPickerPortlet target);

}
