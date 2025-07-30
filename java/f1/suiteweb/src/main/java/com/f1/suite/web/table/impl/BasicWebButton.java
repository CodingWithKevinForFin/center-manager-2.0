/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import com.f1.suite.web.table.WebButton;

public class BasicWebButton implements WebButton {

	private String text;
	private String title;
	private String action;
	private String buttonType;
	private boolean isOn;

	@Override
	public String getButtonType() {
		return buttonType;
	}

	@Override
	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}
	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String title) {
		this.title = title;
	}

	@Override
	public String getAction() {
		return action;
	}

	@Override
	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public boolean getOn() {
		return isOn;
	}

	@Override
	public void setOn(boolean on) {
		this.isOn = on;
	}

	public BasicWebButton() {
	}
	public BasicWebButton(String buttonType, String text, String action, String title) {
		this.text = text;
		this.action = action;
		this.title = title;
	}

}
