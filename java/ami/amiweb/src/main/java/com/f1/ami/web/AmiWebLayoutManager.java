package com.f1.ami.web;

import java.util.logging.Logger;

import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.LH;

public class AmiWebLayoutManager {

	private static final Logger log = LH.get();
	public static final String DEFAULT_LAYOUT_NAME = null;//"untitled.ami";
	private AmiWebService service;

	public AmiWebLayoutManager(AmiWebService service) {
		this.service = service;
	}

	private PortletManager getManager() {
		return this.service.getPortletManager();
	}

	public int getDialogHeight() {
		int scaledHeight = (int) (getManager().getRoot().getHeight() * 0.8);
		return Math.min(scaledHeight, 1200);
	}
	public int getDialogWidth() {
		int scaledWidth = (int) (getManager().getRoot().getWidth() * 0.8);
		return Math.min(scaledWidth, 1900);
	}
}
