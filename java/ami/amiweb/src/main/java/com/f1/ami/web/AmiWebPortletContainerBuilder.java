package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletContainerBuilder;

public interface AmiWebPortletContainerBuilder<T extends PortletContainer & AmiWebAliasPortlet> extends AmiWebPortletBuilder<T>, PortletContainerBuilder<T> {

	boolean removePortletId(Map<String, Object> portletConfig, String amiPanelId);

	boolean replacePortletId(Map<String, Object> portletConfig, String oldPanelId, String amiPanelId);
}
