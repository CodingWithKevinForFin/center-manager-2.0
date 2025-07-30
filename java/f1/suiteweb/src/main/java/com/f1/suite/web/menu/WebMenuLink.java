/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.menu;

public interface WebMenuLink extends WebMenuItem {

	public String getAction();

	public boolean getAutoclose();

	public String getOnClickJavascript();

	char getKeystroke();
}
