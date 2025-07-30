package com.f1.ami.web;

import java.util.List;
import java.util.Map;

public interface AmiWebDmPortletBuilder<T extends AmiWebDmPortlet> extends AmiWebPortletBuilder<T> {

	public List<String> extractUsedDmAndTables(Map<String, Object> portletConfig);

	void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name);

}
