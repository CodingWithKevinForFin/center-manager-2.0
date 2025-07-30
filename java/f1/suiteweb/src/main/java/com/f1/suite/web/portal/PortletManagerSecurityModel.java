package com.f1.suite.web.portal;

import java.util.Map;

public interface PortletManagerSecurityModel {

	boolean hasPermissions(PortletManager manager, Portlet portlet, String type, Map<String, String> attributes);
	void raiseSecurityViolation(Portlet portlet, String type, Map<String, String> attributes);
	void raiseSecurityViolation(String reason);
	void throwSecurityException(Portlet portlet, String action);

	public void assertPermitted(Portlet source, String action, String permissableActions);
	@Deprecated
	boolean hasPermissions(PortletManager manager, String type, Map<String, String> attributes);

}
