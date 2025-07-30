package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;

public class BasicPortletConfig implements PortletConfig {

	final private PortletManager portletManager;
	final private String portletId;
	final private boolean rebuild;
	final private String builderId;

	public BasicPortletConfig(PortletManager portletManager) {
		this(portletManager, portletManager.generateId(), null, false);

	}
	public BasicPortletConfig(PortletManager portletManager, String portletId, String builderId, boolean isRebuild) {
		this.portletManager = portletManager;
		this.portletId = portletId;
		this.rebuild = isRebuild;
		this.builderId = builderId;
	}
	@Override
	public PortletManager getPortletManager() {
		return portletManager;
	}

	@Override
	public String getPortletId() {
		return portletId;
	}

	@Override
	public boolean isRebuild() {
		return rebuild;
	}

	@Override
	public String getBuilderId() {
		return builderId;
	}

}
