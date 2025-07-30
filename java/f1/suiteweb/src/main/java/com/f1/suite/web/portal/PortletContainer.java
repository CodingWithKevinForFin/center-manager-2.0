/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal;

import java.util.Map;

public interface PortletContainer extends Portlet {

	int getChildrenCount();

	public Map<String, Portlet> getChildren();

	public Portlet getChild(String id);

	public void addChild(Portlet portlet);

	public Portlet removeChild(String portletId);

	public void replaceChild(String removedPortletId, Portlet replacement);

	void bringToFront(String portletId);

	/**
	 * @return true only if a child can be added to it now. Must return false if not customizable
	 */
	public boolean hasVacancy();

	/**
	 * @return true only if the user may add / remove portlets to this container.
	 */
	public boolean isCustomizable();

	public int getChildOffsetX(String id);
	public int getChildOffsetY(String id);
}
