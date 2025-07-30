package com.f1.ami.web.charts;

import com.f1.ami.web.AmiWebLockedPermissiblePortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.suite.web.portal.ColorUsingPortlet;

public interface AmiWebManagedPortlet extends AmiWebContextMenuFactory, AmiWebContextMenuListener, ColorUsingPortlet, AmiWebLockedPermissiblePortlet {

	String getConfigMenuTitle();
	boolean getIsFreeFloatingPortlet();
	AmiWebService getService();
	//	AmiWebManager getAgentManager();
	void setShowConfigButtons(boolean showConfigButtons);
	boolean getShowConfigButtons();

}
