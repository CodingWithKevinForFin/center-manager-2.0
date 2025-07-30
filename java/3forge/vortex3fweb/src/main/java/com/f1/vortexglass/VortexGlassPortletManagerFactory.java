package com.f1.vortexglass;

import java.util.Map;

import com.f1.container.ContainerTools;
import com.f1.suite.web.PortletManagerFactory;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.vortex.ssoweb.SsoPortletManagerFactory;
import com.vortex.web.VortexPortalHttpHandler;

public class VortexGlassPortletManagerFactory extends PortletManagerFactory {

	private VortexPortalHttpHandler testTrackHandler;
	private SsoPortletManagerFactory ssoHandler;
	private Map<String, String> scripts;

	public VortexGlassPortletManagerFactory(ContainerTools tools) {
		super(tools);
	}

	public void init() {
		super.init();
		scripts = SH.splitToMap(',', '=', getTools().getOptional("demo.scripts", ""));
	}

	@Override
	public void applyServices(PortletManager manager) {
		super.applyServices(manager);
		getSsoHandler().applyServices(manager);
		testTrackHandler.applyServices(manager);
	}

	@Override
	public void applyDefaultLayout(PortletManager portletManager) {
		getSsoHandler().applyDefaultLayout(portletManager);
		if (portletManager.getRoot().getChildren().isEmpty())
			super.applyDefaultLayout(portletManager);
	}

	@Override
	public void applyBuilders(PortletManager portletManager) {
		super.applyBuilders(portletManager);
		if (CH.isntEmpty(scripts))
			portletManager.addPortletBuilder(new DemoScriptPortlet.Builder(scripts).setPath("Demo"));
		testTrackHandler.applyBuilders(portletManager);
		if (getSsoHandler() != null)
			getSsoHandler().applyBuilders(portletManager);
	}

	public SsoPortletManagerFactory getSsoHandler() {
		return ssoHandler;
	}

	public void setSsoHandler(SsoPortletManagerFactory ssoHandler) {
		this.ssoHandler = ssoHandler;
	}

	public VortexPortalHttpHandler getTestTrackHandler() {
		return testTrackHandler;
	}

	public void setTestTrackHandler(VortexPortalHttpHandler testTrackHandler) {
		this.testTrackHandler = testTrackHandler;
	}

}
