package com.f1.suite.web.portal.impl.form;

import com.f1.suite.web.menu.WebMenu;

public interface FormPortletContextMenuForButtonFactory {
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletButton button, int cursorPosition);
}
