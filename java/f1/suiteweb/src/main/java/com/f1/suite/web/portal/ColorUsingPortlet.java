package com.f1.suite.web.portal;

import java.util.Set;

public interface ColorUsingPortlet extends Portlet {

	public void getUsedColors(Set<String> sink);
}
