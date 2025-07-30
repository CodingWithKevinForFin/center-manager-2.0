/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.menu;

import java.util.Comparator;
import java.util.List;

public interface WebMenu extends WebMenuItem {

	public List<WebMenuItem> getChildren();

	public void add(WebMenuItem basicWebMenuLink);

	public int getChildrenCount();
	public void add(int position, WebMenuItem menuItem);

	public void sort();
	public void sort(Comparator<? super WebMenuItem> c);
}
