/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.vortex.ssoweb;

import java.util.logging.Logger;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.ContainerTools;
import com.f1.suite.web.PortletManagerFactory;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletUserConfigStore;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;

public class SsoPortletManagerFactory extends PortletManagerFactory {

	private static final Logger log = LH.get();

	public SsoPortletManagerFactory(ContainerTools tools, LocaleFormatter f) {
		super(tools, f);
	}

	@Override
	public void init() {
		ContainerBootstrap.registerMessagesInPackages(getTools().getServices().getGenerator(), "com.sso.messages");
		super.init();
	}

	@Override
	public void applyServices(PortletManager portletManager) {
		super.applyServices(portletManager);
		SsoService ssoService = new SsoService(portletManager);
		portletManager.registerService(ssoService);
		portletManager.setUserConfigStore(ssoService);
	}
	@Override
	public void applyBuilders(PortletManager portletManager) {
		super.applyBuilders(portletManager);
		String path = "Vortex.Entitlements";
		Boolean isEntitlements = portletManager.getTools().getOptional("entitlements.enabled", Boolean.TRUE);
		if (isEntitlements) {
			portletManager.addPortletBuilder(new SsoAttributesTablePortlet.Builder().setPath(path));
			portletManager.addPortletBuilder(new SsoUsersTablePortlet.Builder().setPath(path));
			portletManager.addPortletBuilder(new SsoEventsTablePortlet.Builder().setPath(path));
			portletManager.addPortletBuilder(new SsoGroupsTablePortlet.Builder().setPath(path));
			portletManager.addPortletBuilder(new SsoGroupMembersTablePortlet.Builder().setPath(path));
			portletManager.addPortletBuilder(new NewSsoUserFormPortlet.Builder().setPath(path));
			portletManager.addPortletBuilder(new NewSsoGroupFormPortlet.Builder().setPath(path));
			//portletManager.addPortletBuilder(new UpdateSsoGroupFormPortlet.Builder().setPath(gpath));
			portletManager.addPortletBuilder(new SsoGroupsTreePortlet.Builder().setPath(path));
			portletManager.addPortletBuilder(new SsoMaskingTablePortlet.Builder().setPath(path));
			portletManager.addPortletBuilder(new SsoAttributeFormPortlet.Builder().setPath(path));
		}
	}

	public void applyDefaultLayout(PortletManager portletManager) {
		try {
			PortletUserConfigStore configStore = portletManager.getUserConfigStore();
			if (configStore != null) {
				portletManager.loadConfig();
			}
		} catch (Exception e) {
			LH.warning(log, "error loading config for session: ", portletManager.getState().getWebStatesManager().getUser().getUserName(), e);
		}
		//portletManager.getRoot().addChild(children);
	}
}
