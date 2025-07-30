package com.f1.suite.web.portal;

import com.f1.suite.web.portal.impl.PortletNotification;

public interface PortletNotificationListener {
	public void onNotificationClosed(PortletManager manager, PortletNotification notification);
	public void onNotificationUserClicked(PortletManager manager, PortletNotification notification);
	public void onNotificationDenied(PortletManager manager, PortletNotification notification);
}
