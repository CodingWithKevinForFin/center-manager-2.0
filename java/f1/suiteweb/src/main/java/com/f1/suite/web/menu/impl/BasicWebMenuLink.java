/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.menu.impl;

import com.f1.suite.web.menu.WebMenuLink;

public class BasicWebMenuLink extends AbstractWebMenuItem implements WebMenuLink {

	final private String action;
	private char keystroke;
	private boolean autoclose = true;
	private String javascript;

	public BasicWebMenuLink(String text, boolean enabled, String action) {
		this(text, enabled, action, -1);
	}
	public BasicWebMenuLink(String text, boolean enabled, String action, int priority) {
		super(text, enabled, priority);
		this.action = action;
	}

	@Override
	public String getAction() {
		return this.action;
	}

	@Override
	public boolean getAutoclose() {
		return autoclose;
	}

	public BasicWebMenuLink setAutoclose(boolean autoclose) {
		this.autoclose = autoclose;
		return this;
	}
	public BasicWebMenuLink setOnClickJavascript(String javascript) {
		this.javascript = javascript;
		return this;
	}

	@Override
	public String getOnClickJavascript() {
		return this.javascript;
	}
	@Override
	public String toString() {
		return "BasicWebMenuLink [action=" + action + ", autoclose=" + autoclose + ", javascript=" + javascript + ", getText()=" + getText() + ", getEnabled()=" + getEnabled()
				+ ", getCssStyle()=" + getCssStyle() + ", getPriority()=" + getPriority() + ", getBackgroundImage()=" + getBackgroundImage() + "]";
	}

	@Override
	public char getKeystroke() {
		return keystroke;
	}

	public BasicWebMenuLink setKeystroke(char keystroke) {
		this.keystroke = keystroke;
		return this;
	}
	@Override
	public BasicWebMenuLink setBackgroundImage(String backgroundImage) {
		super.setBackgroundImage(backgroundImage);
		return this;
	}

}
