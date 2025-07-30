package com.f1.suite.web.portal;

import java.util.Map;

public interface PortletContainerBuilder<T extends PortletContainer> extends PortletBuilder<T> {

	public void extractChildPorletIds(Map<String, Object> config, Map<String, Map> portletId2DetailsSink);

}
