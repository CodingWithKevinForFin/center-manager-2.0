package com.f1.ami.web;

public interface AmiWebHiddenPanelsListener {

	void onHiddenPanelIdChanged(AmiWebLayoutFile amiWebLayoutFile, String oldPanelId, String newPanelId);
	void onHiddenPanelRemoved(AmiWebLayoutFile amiWebLayoutFile, AmiWebPortletDef def);
	void onHiddenPanelAdded(AmiWebLayoutFile amiWebLayoutFile, AmiWebPortletDef def);

}
