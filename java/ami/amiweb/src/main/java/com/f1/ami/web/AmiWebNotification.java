package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.portal.impl.PortletNotification;

public class AmiWebNotification {

	final private PortletNotification notification;
	final private Map attachment;

	public AmiWebNotification(PortletNotification notification, Map attachment) {
		this.notification = notification;
		this.attachment = attachment;
	}

	public PortletNotification getNotification() {
		return notification;
	}

	public Map getAttachment() {
		return attachment;
	}

}
