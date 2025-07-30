/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table;

public interface WebButton {
	public static final String BUTTON = "button";
	public static final String CHECKBOX = "checkbox";
	public static final String ANCHOR = "anchor";

	public String getButtonType();
	public void setButtonType(String type);

	public String getText();
	public void setText(String title);

	public String getAction();
	public void setAction(String action);

	public boolean getOn();
	public void setOn(boolean checked);
}
