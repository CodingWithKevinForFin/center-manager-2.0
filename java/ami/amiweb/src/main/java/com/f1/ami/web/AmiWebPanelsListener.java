package com.f1.ami.web;

public interface AmiWebPanelsListener {
	void onAmiWebPanelAdded(AmiWebPortlet portlet);
	void onAmiWebPanelRemoved(AmiWebPortlet portlet, boolean isHide);
	void onAmiWebPanelLocationChanged(AmiWebPortlet portlet);
	void onAmiWebPanelIdChanged(AmiWebPortlet portlet, String oldAdn, String newAdn);

}
