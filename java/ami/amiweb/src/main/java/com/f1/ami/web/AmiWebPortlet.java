package com.f1.ami.web;

import java.util.Collection;

import com.f1.ami.web.charts.AmiWebManagedPortlet;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.menu.AmiWebCustomContextMenuManager;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Table;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public interface AmiWebPortlet extends AmiWebManagedPortlet, AmiWebStyledPortlet, AmiWebAliasPortlet, AmiWebDomObject {

	String getAmiTitle(boolean b);
	String getTitle(boolean b);
	void setAmiTitle(String title, boolean b);

	void clearAmiData();

	CalcTypes getPortletVarTypes();
	CalcFrame getPortletVars();
	public boolean putPortletVar(String key, Object value, Class type);

	public String getPanelType();

	public boolean runAmiLink(String name);
	public boolean runAmiLinkId(String id);

	com.f1.base.CalcTypes getUserDefinedVariables();

	public void clearUserSelection();

	boolean isRealtime();

	void onLinkingChanged(AmiWebDmLink link);
	public AmiWebDmLink getCurrentLinkFilteringThis();

	byte NONE = 0;
	byte ALL = 1;
	byte SELECTED = 2;

	public Table getSelectableRows(AmiWebDmLink link, byte type);
	public boolean hasSelectedRows(AmiWebDmLink link);

	Collection<AmiWebDmLink> getDmLinksFromThisPortlet();
	Collection<AmiWebDmLink> getDmLinksToThisPortlet();

	public void addDmLinkFromThisPortlet(AmiWebDmLink link);
	public void removeDmLinkFromThisPortlet(AmiWebDmLink link);
	public void addDmLinkToThisPortlet(AmiWebDmLink link);
	public void removeDmLinkToThisPortlet(AmiWebDmLink link);
	AmiWebCustomContextMenuManager getCustomContextMenu();
	AmiWebScriptManagerForLayout getScriptManager();
	AmiWebPanelSettingsPortlet showSettingsPortlet();

	public ReusableCalcFrameStack getStackFrame();
}
