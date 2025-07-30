/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.menu.impl;

public class BasicWebMenuDivider extends AbstractWebMenuItem {
	public static final String DEFAULT_CSS_STYLE = "_cna=menu_divider";

	public BasicWebMenuDivider() {
		this(-1);
	}

	public BasicWebMenuDivider(int priority) {
		super("", false, priority);
		setCssStyle(DEFAULT_CSS_STYLE);
	}
	public BasicWebMenuDivider(String cssStyle) {
		super("", false);
		setCssStyle(cssStyle);
	}

}
