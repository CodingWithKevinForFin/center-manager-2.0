package com.f1.suite.web.portal;

public interface PortletBuilder<T extends Portlet> {

	T buildPortlet(PortletConfig config);

	public String getPortletBuilderName();

	public String getPortletBuilderId();

	public String[] getPath();

	public Class<T> getPortletType();

	public String getIcon();

	public boolean getIsUserCreatable();

}
