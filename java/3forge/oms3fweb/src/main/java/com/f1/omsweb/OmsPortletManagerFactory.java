/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.omsweb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.f1.container.ContainerTools;
import com.f1.suite.web.PortletManagerFactory;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.IOH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.vortex.ssoweb.SsoPortletManagerFactory;

public class OmsPortletManagerFactory extends PortletManagerFactory {

	private SsoPortletManagerFactory ssoHandler;
	private Map<String, String> sectorMap;

	public OmsPortletManagerFactory(ContainerTools tools, LocaleFormatter f) {
		super(tools, f);
		//	setCallback(callBack);
	}

	@Override
	public void applyServices(PortletManager manager) {
		super.applyServices(manager);
		//	applyServices(manager);
		getSsoHandler().applyServices(manager);
		manager.registerService(new OrdersService(manager, sectorMap));
		//manager.registerService(new UserEventsService(manager));

	}

	@Override
	public void init() {
		super.init();
		try {
			String text = IOH.readTextFromResource("ref_data.txt");
			sectorMap = new HashMap<String, String>();
			for (String row : SH.splitLines(text)) {
				String[] parts = SH.split('|', row);
				if (parts.length > 1)
					sectorMap.put(SH.replaceAll(parts[0], '^', '.'), parts[1]);
			}
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public void applyBuilders(PortletManager portletManager) {
		super.applyBuilders(portletManager);
		getSsoHandler().applyBuilders(portletManager);
		String path = "Order Management";
		String bpath = path + ".Blotters";
		String tpath = path + ".Order Tickets";
		String vpath = path + ".Visualization";
		String mpath = "Monitoring.Audit Trails";
		portletManager.addPortletBuilder(new ParentOrdersPortlet.Builder().setPath(bpath));
		portletManager.addPortletBuilder(new ChildOrdersPortlet.Builder().setPath(bpath));
		portletManager.addPortletBuilder(new ChildExecutionsPortlet.Builder().setPath(bpath));
		portletManager.addPortletBuilder(new ParentExecutionsPortlet.Builder().setPath(bpath));
		portletManager.addPortletBuilder(new NewOrderFormPortlet.Builder(getSessionNames()).setPath(tpath));
		portletManager.addPortletBuilder(new NewBasketFormPortlet.Builder().setPath(tpath));
		portletManager.addPortletBuilder(new ExecBrokerChartPortlet.Builder().setPath(vpath));
		portletManager.addPortletBuilder(new ExecTimeseriesChartPortlet.Builder().setPath(vpath));
		portletManager.addPortletBuilder(new UserEventsTablePortlet.Builder().setPath(mpath));
		portletManager.addPortletBuilder(new OmsTreePortlet.Builder().setPath(bpath));
		//portletManager.addPortletBuilder(new TestTreePortlet.Builder().setPath(path));
		portletManager.addPortletBuilder(new SectorTreemapPortlet.Builder(sectorMap).setPath(vpath));
	}

	@Override
	public void applyDefaultLayout(PortletManager portletManager, Map<String, String> urlParams) {
		getSsoHandler().applyDefaultLayout(portletManager);
		if (portletManager.getRoot().getChildren().isEmpty())
			super.applyDefaultLayout(portletManager, urlParams);
	}

	private String sessionNames;

	public void setSessionNames(String sessionNames) {
		this.sessionNames = sessionNames;
	}

	public String getSessionNames() {
		return sessionNames;
	}

	public SsoPortletManagerFactory getSsoHandler() {
		return ssoHandler;
	}

	public void setSsoHandler(SsoPortletManagerFactory ssoHandler) {
		this.ssoHandler = ssoHandler;
	}
}
