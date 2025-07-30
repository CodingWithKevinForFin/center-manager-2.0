package com.f1.suite.web.portal;


public interface PortletConfig {

	public PortletManager getPortletManager();

	public String getPortletId();

	public String getBuilderId();

	public boolean isRebuild();

}
