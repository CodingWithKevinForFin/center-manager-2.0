package com.f1.suite.web.portal.impl.form;

import com.f1.suite.web.menu.WebMenu;

public interface FormPortletContextMenuFactory {
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition);
}
