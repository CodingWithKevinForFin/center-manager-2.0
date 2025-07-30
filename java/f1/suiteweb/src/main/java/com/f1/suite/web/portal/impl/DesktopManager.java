package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.impl.DesktopPortlet.PopupWindowListener;

public interface DesktopManager {

	void onUserDeleteWindow(DesktopPortlet desktop, DesktopPortlet.Window window);
	void onUserPopoutWindow(DesktopPortlet desktop, DesktopPortlet.Window window);
	void onUserPopoutWindowClosed(PopupWindowListener popupWindowListener, DesktopPortlet.Window window);

}
