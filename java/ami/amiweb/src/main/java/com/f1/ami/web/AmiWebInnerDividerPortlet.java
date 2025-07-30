package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

public class AmiWebInnerDividerPortlet extends DividerPortlet {

	private AmiWebDividerPortlet owner;
	private AmiWebService service;

	public AmiWebInnerDividerPortlet(PortletConfig manager, boolean isVertical, AmiWebDividerPortlet owner) {
		super(manager, isVertical);
		this.owner = owner;
		this.service = AmiWebUtils.getService(getManager());
	}

	@Override
	protected Portlet configSaveIdToPortlet(Map<String, String> config, String amiPanelId) {
		return service.getPortletByAliasDotPanelId(AmiWebUtils.getFullAlias(owner.getAmiLayoutFullAlias(), amiPanelId));
	}

	@Override
	protected String portletToConfigSaveId(Portlet portlet) {
		if (portlet instanceof AmiWebAliasPortlet) {
			AmiWebAliasPortlet amiWebAliasPortlet = (AmiWebAliasPortlet) portlet;
			if (!this.service.getDesktop().getIsDoingExportTransient())
				amiWebAliasPortlet = amiWebAliasPortlet.getNonTransientPanel();
			if (amiWebAliasPortlet != null)
				return AmiWebUtils.getRelativeAlias(owner.getAmiLayoutFullAlias(), amiWebAliasPortlet.getAmiLayoutFullAliasDotId());
		}
		return null;
	}
	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if (SH.equals(callback, "onCustomMenu")) {
			int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			WebMenu menu = this.owner.createMenu(this);
			if (menu != null) {
				getManager().showContextMenu(menu, this.owner, x, y);
			}
		} else
			super.handleCallback(callback, attributes);
	}
	public AmiWebDividerPortlet getOwner() {
		return this.owner;
	}

}
