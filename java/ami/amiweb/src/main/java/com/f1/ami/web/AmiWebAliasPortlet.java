package com.f1.ami.web;

import java.util.Collection;
import java.util.Map;

import com.f1.ami.web.menu.AmiWebCustomContextMenuManager;
import com.f1.suite.web.portal.Portlet;

public interface AmiWebAliasPortlet extends Portlet, AmiWebDomObject {

	public String getConfigMenuTitle();
	public boolean setAdn(String adn);
	public void onAmiInitDone();
	public String getAmiPanelId();
	public AmiWebService getService();
	public AmiWebAliasPortlet getAmiParent();
	public Collection<AmiWebAliasPortlet> getAmiChildren();

	public void setAmiUserPrefId(String userPrefId);//DO NOT CALL DIRECTLY, Use AmiWebSevice::registerAmiUserPrefId
	public String getAmiUserPrefId();
	public void applyUserPref(Map<String, Object> values);
	public Map<String, Object> getUserPref();
	public Map<String, Object> getDefaultPref();
	public void setDefaultPref(Map<String, Object> defaultPref);
	public boolean isReadonlyLayout();
	public AmiWebCustomContextMenuManager getCustomContextMenu();
	AmiWebScriptManagerForLayout getScriptManager();

	//If I'm non-transient return me, otherwise if I have a non-transient child panels(recursively) return that child. Otherwise return null;
	AmiWebAliasPortlet getNonTransientPanel();
}
