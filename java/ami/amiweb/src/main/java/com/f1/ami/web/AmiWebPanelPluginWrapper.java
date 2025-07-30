package com.f1.ami.web;

import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;

public class AmiWebPanelPluginWrapper extends AmiWebAbstractPortletBuilder<AmiWebPluginPortlet> implements AmiWebDmPortletBuilder<AmiWebPluginPortlet> {

	private AmiWebPanelPlugin plugin;

	public AmiWebPanelPluginWrapper(AmiWebPanelPlugin s) {
		super(AmiWebPluginPortlet.class);
		this.plugin = s;
	}

	public AmiWebPanelPlugin getPlugin() {
		return this.plugin;
	}

	@Override
	public AmiWebPluginPortlet buildPortlet(PortletConfig config) {
		return plugin.createPanel(config);
	}

	@Override
	public String getPortletBuilderName() {
		return this.plugin.getDisplayName();
	}

	@Override
	public String getPortletBuilderId() {
		return this.plugin.getPluginId();
	}

	@Override
	public List<String> extractUsedDmAndTables(Map<String, Object> portletConfig) {
		return plugin.extraceUsedDms(portletConfig);
	}

	@Override
	public void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name) {
		plugin.replaceUsedDmAt(portletConfig, position, name);

	}

}
