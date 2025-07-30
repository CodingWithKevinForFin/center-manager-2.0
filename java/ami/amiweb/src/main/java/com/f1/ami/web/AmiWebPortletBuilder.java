package com.f1.ami.web;

import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.PortletBuilder;

public interface AmiWebPortletBuilder<T extends AmiWebAliasPortlet> extends PortletBuilder<T> {

	List<AmiWebPortletDef.Callback> getCallbacks(Map<String, Object> portletConfig);

	//	public List<String> extractUsedDmAndTables(Map<String, Object> portletConfig);
	//
	//	void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name);
}
