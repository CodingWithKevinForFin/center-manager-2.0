package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.TabPortlet;

public class AmiWebInnerTabPortlet extends TabPortlet {

	private AmiWebService service;
	private AmiWebTabPortlet owner;

	public AmiWebInnerTabPortlet(PortletConfig manager, AmiWebTabPortlet owner) {
		super(manager);
		this.owner = owner;
		this.service = AmiWebUtils.getService(getManager());
		this.getTabPortletStyle().setTabFloatSize(0);
	}

	@Override
	protected Portlet configSaveIdToPortlet(Map<String, String> orig, String amiPanelId) {
		return service.getPortletByAliasDotPanelId(AmiWebUtils.getFullAlias(owner.getAmiLayoutFullAlias(), amiPanelId));
	}

	@Override
	protected String portletToConfigSaveId(Portlet portlet) {
		if (portlet instanceof AmiWebAliasPortlet)
			return AmiWebUtils.getRelativeAlias(owner.getAmiLayoutFullAlias(), ((AmiWebAliasPortlet) portlet).getAmiLayoutFullAliasDotId());
		else
			return null;
	}
	@Override
	protected void layoutChildTab(Tab tab) {
		tab.getPortlet().setSize(getWidth(), getHeight());
	}
	public static void removeChildPanelId(Map<String, Object> portletConfig, String amiPanelId) {

	}

}
