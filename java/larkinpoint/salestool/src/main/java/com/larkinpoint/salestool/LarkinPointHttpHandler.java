package com.larkinpoint.salestool;

import java.util.Map;

import com.f1.container.ContainerTools;
import com.f1.suite.web.PortalHttpStateCreator;
import com.f1.suite.web.PortletManagerFactory;
import com.f1.suite.web.portal.PortletManager;
import com.vortex.ssoweb.SsoPortalHttpHandler;

public class LarkinPointHttpHandler extends PortletManagerFactory {

	final private SsoPortalHttpHandler ssoHandler;
	private Map<String, String> scripts;

	public LarkinPointHttpHandler(ContainerTools tools, SsoPortalHttpHandler sso, PortalHttpStateCreator stateCreator) {
		super(tools);
		ssoHandler = sso;
	}

	public void init() {
		super.init();
	}

	@Override
	public void applyServices(PortletManager manager) {
		super.applyServices(manager);
		manager.registerService(new LarkinPointWebService(manager));
		ssoHandler.applyServices(manager);
	}

	@Override
	public void applyDefaultLayout(PortletManager portletManager) {
		ssoHandler.applyDefaultLayout(portletManager);
		if (portletManager.getRoot().getChildren().isEmpty())
			super.applyDefaultLayout(portletManager);
	}

	@Override
	public void applyBuilders(PortletManager portletManager) {
		super.applyBuilders(portletManager);
		portletManager.addPortletBuilder(new LarkinUnderlyingDataPortlet.Builder().setPath("Show Underlyings"));
		portletManager.addPortletBuilder(new LarkinOptionDataPortlet.Builder().setPath("Options"));
		portletManager.addPortletBuilder(new LarkinOptionTimeSeriesPortlet.Builder().setPath("Options"));
		portletManager.addPortletBuilder(new LarkinOptionQueryToolPortlet.Builder().setPath("Options"));

		portletManager.addPortletBuilder(new LarkinRunFilePortlet.Builder().setPath("Load Options"));
		portletManager.addPortletBuilder(new LarkinLogoPortlet.Builder().setPath("logo"));
		//	portletManager.addPortletBuilder(new LarkinTreeMap.Builder().setPath("logo"));
		//ssoHandler.applyBuilders(portletManager);
	}
}
