package com.f1.suite.web.portal.impl.text;

import com.f1.suite.web.peripheral.KeyEvent;

public interface TextPortletListener {
	public void onTextContextMenu(FastTextPortlet portlet, String id);
	public boolean onTextUserKeyEvent(FastTextPortlet portlet, KeyEvent keyEvent);
}
