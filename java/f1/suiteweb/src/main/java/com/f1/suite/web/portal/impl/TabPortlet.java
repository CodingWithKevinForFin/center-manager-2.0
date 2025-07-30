/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;

public class TabPortlet extends AbstractPortletContainer implements ConfirmDialogListener, WebMenuListener {

	private static final Logger log = Logger.getLogger(TabPortlet.class.getName());
	public static final PortletSchema<TabPortlet> SCHEMA = new BasicPortletSchema<TabPortlet>("Tab", "TabPortlet", TabPortlet.class, true, true);

	private TabManager tabManager;
	final private IndexedList<Integer, Tab> tabs = new BasicIndexedList<Integer, TabPortlet.Tab>();
	final private List<TabListener> tabListeners = new ArrayList<TabListener>();
	final private TabPortletStyle tabPortletStyle;
	final private TabPortletJs tabsJs;

	private Tab currentMenuTab;
	private int visibleChildLocation = -1;

	private int nextTabId = 0;

	public TabPortlet(PortletConfig manager) {
		super(manager);
		this.tabPortletStyle = new TabPortletStyle(this, getManager().getStyleManager().getTabStyle());
		this.tabsJs = new TabPortletJs(this);
	}

	public TabPortletStyle getTabPortletStyle() {
		return this.tabPortletStyle;
	}
	public int getVisibleChildLocation() {
		return this.visibleChildLocation;
	}

	@Override
	protected void layoutChildren() {
		Tab select = getSelectedTab();
		for (Tab tab : tabs.values())
			if (tab == select) {
				layoutChildTab(tab);
				if (getVisible())
					makeChildVisible(tab.getPortlet(), true);
			} else {
				makeChildVisible(tab.getPortlet(), false);
			}
	}
	protected void layoutChildTab(Tab tab) {
		if (tabPortletStyle.isHidden)
			tab.getPortlet().setSize(getWidth(), getHeight());
		else {
			if (this.tabPortletStyle.getTabFloatSize() != TabPortletStyle.NO_FLOAT)
				tab.getPortlet().setSize(getWidth(), getHeight());
			else if (tabPortletStyle.isVertical) {
				tab.getPortlet().setSize(getWidth() - getTotalTabHeight(), getHeight());
			} else
				tab.getPortlet().setSize(getWidth(), getHeight() - getTotalTabHeight());
		}

	}
	public void fireLayoutChildren() {
		this.layoutChildren();
	}

	public Tab getSelectedTab() {
		return visibleChildLocation == -1 ? null : tabs.getAt(visibleChildLocation);
	}

	public int getTotalTabHeight() {
		return tabPortletStyle.tabHeight + tabPortletStyle.tabPaddingBottom + tabPortletStyle.tabPaddingTop;
	}

	@Override
	public void addChild(Portlet child) {
		addChild(null, child, -1);
	}

	public Tab addChild(String childName, Portlet child) {
		return this.addChild(childName, child, -1);
	}
	public Tab addChild(String childName, Portlet child, int id) {
		if (childName == null) {
			if (child.getPortletConfig().getBuilderId() != null)
				childName = formatText(getManager().getPortletBuilder(child.getPortletConfig().getBuilderId()).getPortletBuilderName());
			else
				childName = "new tab";
		}
		super.addChild(child);
		Tab tab = new Tab(this, child, tabs.getSize(), id == -1 ? generateNextTabId() : id);
		if (tabs.getSize() == 0)
			tab.setIsDefault(true);
		tabs.add(tab.getTabId(), tab, tab.getLocation());
		tab.setTitle(childName);
		if (getChildrenCount() == 1)
			this.selectTab(tab.getLocation());
		if (getVisible())
			layoutChildren();
		for (TabListener listener : this.tabListeners)
			listener.onTabAdded(this, tab);
		flagTabsChanged();
		return tab;
	}
	public Tab addChild(int position, String childName, Portlet child) {
		return this.addChild(position, childName, child, false);
	}
	public Tab addChild(int position, String childName, Portlet child, boolean forceVisible) {
		if (this.tabs.getSize() == 0)
			forceVisible = true;
		if (position > tabs.getSize())
			throw new IndexOutOfBoundsException("position > tab count:" + position + " > " + tabs.getSize());
		if (childName == null) {
			if (child.getPortletConfig().getBuilderId() != null)
				childName = formatText(getManager().getPortletBuilder(child.getPortletConfig().getBuilderId()).getPortletBuilderName());
			else
				childName = "new tab";
		}
		if (forceVisible) {
			visibleChildLocation = -1;
		}
		super.addChild(child);
		Tab tab = new Tab(this, child, position, generateNextTabId());
		tabs.add(tab.getTabId(), tab, tab.getLocation());
		tab.setTitle(childName);

		for (int i = position + 1; i < tabs.getSize(); i++)
			tabs.getAt(i).setLocation(i);
		if (getChildrenCount() == 1)
			this.selectTab(tab.getLocation());
		if (forceVisible) {
			setActiveTab(child);
		} else {
			if (position <= visibleChildLocation)
				visibleChildLocation++;
			if (getVisible())
				layoutChildren();
		}
		for (TabListener listener : this.tabListeners)
			listener.onTabAdded(this, tab);
		flagTabsChanged();
		return tab;
	}
	public void flagTabsChanged() {
		if (getVisible())
			flagPendingAjax();
	}

	private int generateNextTabId() {
		return nextTabId++;
	}

	public void moveTab(Tab tab, int position) {
		int location = tab.getLocation();
		if (position == location)
			return;
		if (position >= tabs.getSize() || position < 0)
			throw new IndexOutOfBoundsException("position,tab count:" + position + ", " + tabs.getSize());
		if (visibleChildLocation == location)
			visibleChildLocation = position;
		this.currentMenuTab = null;
		tabs.removeAt(location);
		tab.setLocation(position);
		tabs.add(tab.getTabId(), tab, position);
		for (int i = 0; i < tabs.getSize(); i++)
			tabs.getAt(i).setLocation(i);
		for (TabListener listener : this.tabListeners)
			listener.onTabMoved(this, location, tab);
		flagTabsChanged();
	}

	//Not position should NOT consider hidden tabs... Meaning positions=0 is the first non-hidden tab
	public void moveTabDontMoveHidden(Tab t, int position) {
		for (int i = 0, end = position; i <= end; i++)
			if (getTabAtLocation(i).isHidden())
				position++;
		int sourcPosition = t.getLocation();
		moveTab(t, position);
		if (sourcPosition < position) {//moved to the right....Need to move hidden tabs right 
			for (int i = position - 1; i >= sourcPosition; i--) {
				Tab t2 = getTabAtLocation(i);
				if (t2.isHidden())
					moveTab(t2, i + 1);
			}
		} else {//moved to the left... need to move hidden tabs left too
			for (int i = position + 1; i <= sourcPosition; i++) {
				Tab t2 = getTabAtLocation(i);
				if (t2.isHidden())
					moveTab(t2, i - 1);
			}
		}
	}
	@Override
	public Portlet removeChild(String childId) {
		Portlet r = super.removeChild(childId);
		Tab removed = findTab(r);
		tabs.removeAt(removed.getLocation());
		if (tabs.getSize() == 0)
			visibleChildLocation = -1;
		else if (visibleChildLocation > 0 && visibleChildLocation >= removed.getLocation())
			visibleChildLocation--;
		for (int i = removed.getLocation(); i < tabs.getSize(); i++) {
			tabs.getAt(i).setLocation(i);
		}
		if (getVisible()) {
			layoutChildren();
		}
		for (TabListener listener : this.tabListeners)
			listener.onTabRemoved(this, removed);
		flagTabsChanged();
		return r;
	}

	public Tab findTab(Portlet r) {
		for (Tab tab : tabs.values()) {
			if (tab.getPortlet() == r)
				return tab;
		}
		return null;
	}

	@Override
	public void initJs() {
		super.initJs();
		flagPendingAjax();
	}
	@Override
	public void drainJavascript() {
		if (this.getVisible()) {
			this.tabsJs.callJsFunction_buildTabs();
			super.drainJavascript();
		}
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		boolean canCustomize = tabPortletStyle.isCustomizable || this.tabManager != null;
		if ("renameTabDiag".equals(callback)) {
			int tabindex = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "tabindex");
			Tab tab = tabs.getAt(tabindex);
			this.currentMenuTab = tab;
			this.onMenuItem("rename_tab");
			return;
		}

		if ("moveTab".equals(callback)) {
			if (this.getTabPortletStyle().getHideArrangeTabs())
				throw new IllegalStateException("can not move tabs");
			int tabIndex = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "tabindex");
			int loc = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "nwtabindex");

			//			if (!getTabPortletStyle().isShowTabsOverride())
			Tab tab = getTabAtLocation(tabIndex);
			Tab selectedTab = this.getSelectedTab();
			if (!getTabPortletStyle().isShowTabsOverride())
				this.moveTabDontMoveHidden(tab, loc);
			else
				this.moveTab(tab, loc);
			if (selectedTab != null)
				this.setActiveTab(selectedTab.getPortlet());
		} else if ("tab".equals(callback) || "tabClick".equals(callback)) {
			int curTabInd = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "tabindex");
			Tab curTab = getTabAtLocation(curTabInd);
			if (curTab.isHidden() && !this.tabPortletStyle.isShowTabsOverride())
				getManager().getSecurityModel()
						.raiseSecurityViolation("Attempt to access hidden tab '" + curTab.getTitle() + "' at " + curTabInd + "' from portlet " + this.getPortletId());
			if ("tabClick".equals(callback)) {
				int prevTabInd = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "prevTab", -1);
				Tab prevTab = getTabAtLocation(prevTabInd);
				clickTab(curTab, prevTab, false);
			} else
				selectTab(curTabInd);
		} else if ("newTab".equals(callback)) {
			if (this.tabManager != null)
				this.tabManager.onUserAddTab(this);
		} else if ("showAddPortletDialog".equals(callback)) {
			if (!canCustomize)
				return;
			if (this.tabManager != null)
				this.tabManager.onUserAddTab(this);
			else
				super.handleCallback(callback, attributes);
		} else if ("onMenu".equals(callback)) {
			int curTabInd = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "tabindex");
			int prevTabInd = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "prevTab", -1);
			Tab curTab = getTabAtLocation(curTabInd);
			Tab prevTab = getTabAtLocation(prevTabInd);
			clickTab(curTab, prevTab, true);
			selectTab(curTabInd);
			int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			if (tabManager != null) {
				Tab t = this.tabs.getAt(curTabInd);
				WebMenu menu = tabManager.createMenu(this, t);
				if (menu != null) {
					getManager().showContextMenu(menu, this, x, y);
				}
				this.currentMenuTab = t;
			} else {
				if (!canCustomize)
					return;
				getManager().showContextMenu(new BasicWebMenu(new BasicWebMenuLink("Delete Tab", true, "deleteTab")), this, x, y);
			}
		} else if ("renameTab".equals(callback)) {
			int tabindex = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "tabindex");
			Tab tab = tabs.getAt(tabindex);
			if (!canCustomize && !tab.allowEditTitle)
				return;
			String text = CH.getOrThrow(Caster_String.INSTANCE, attributes, "text");
			if (SH.isnt(text))
				return;
			if (tabManager != null) {
				tabManager.onUserRenamedTab(this, tab, text);
			} else {
				tab.setTitle(text);
			}
		} else
			super.handleCallback(callback, attributes);

	}
	public void clickTab(Tab curTab, Tab prevTab, Boolean b) {
		for (TabListener listener : this.tabListeners)
			listener.onTabClicked(this, curTab, prevTab, b);
	}

	public void selectTab(int location) {
		Tab selected = getSelectedTab();
		if (location < -1 || location >= this.getTabsCount())
			return;
		if (selected != null) {
			if (selected.getLocation() == location)
				return;
			makeChildVisible(selected.getPortlet(), false);
		}
		visibleChildLocation = location;
		selected = getSelectedTab();
		if (getVisible()) {
			layoutChildren();
			if (selected != null)
				makeChildVisible(selected.getPortlet(), true);
			this.tabsJs.initLcv();
			this.tabsJs.callJsFunction_setActiveTab(visibleChildLocation);
			this.tabsJs.callJsFunction_repaint();
			this.tabsJs.callJsFunction_focusTab();
			this.tabsJs.endLcv();
		}
		for (TabListener listener : this.tabListeners)
			listener.onTabSelected(this, selected);
	}
	@Override
	public PortletSchema<TabPortlet> getPortletSchema() {
		return SCHEMA;
	}

	@Override
	public void replaceChild(String removed, Portlet replacement) {
		Tab tab = findTab(getChild(removed));
		tab.setPortlet(replacement);
		super.removeChild(removed);
		super.addChild(replacement);
		flagTabsChanged();
	}

	@Override
	public void onClosed() {
		super.onClosed();
		for (Portlet p : this.getChildren().values())
			p.onClosed();
	}

	public class Tab {
		private Portlet portlet;
		private int location;
		private TabPortlet tabPortlet;
		private String title;
		private String selectColor;
		private String unselectColor;
		private String selectTextColor;
		private String unselectTextColor;
		private String blinkColor;
		private String blinkPeriod;
		private boolean allowEditTitle = true;
		private boolean isDefault = false;
		private boolean isHidden = false;
		private int tabId;
		private String hover;
		private String htmlIdSelector;

		public boolean getAllowTitleEdit() {
			return allowEditTitle;
		}
		public int getTabId() {
			return this.tabId;
		}
		public boolean isHidden() {
			return isHidden;
		}
		public void setHidden(boolean hidden) {
			isHidden = hidden;
			flagTabsChanged();
		}
		public void setAllowTitleEdit(boolean allowEditTitle) {
			if (this.allowEditTitle == allowEditTitle)
				return;
			flagTabsChanged();
			this.allowEditTitle = allowEditTitle;

		}
		public Tab(TabPortlet tabPortlet, Portlet portlet, int location, int tabId) {
			this.tabPortlet = tabPortlet;
			this.portlet = portlet;
			this.setLocation(location);
			this.tabId = tabId;
		}

		private void setPortlet(Portlet portlet) {
			this.portlet = portlet;
		}

		public String getTitle() {
			return this.title;
		}

		public void setTitle(String title) {
			if (OH.eq(this.title, title) && OH.eq(portlet.getTitle(), title))
				return;
			this.portlet.setTitle(title);
			this.title = title;
			tabPortlet.flagTabsChanged();
		}

		public Portlet getPortlet() {
			return portlet;
		}

		public int getLocation() {
			return location;
		}

		private void setLocation(int location) {
			this.location = location;
		}

		public TabPortlet getTabPortlet() {
			return this.tabPortlet;
		}
		public void setIsDefault(boolean b) {
			if (b == isDefault)
				return;
			if (b)
				clearDefaultTab();
			isDefault = b;
		}
		public boolean getIsDefault() {
			return this.isDefault;
		}
		public String getSelectColor() {
			return selectColor;
		}
		public void setSelectColor(String selectColor) {
			if (OH.eq(this.selectColor, selectColor))
				return;
			this.selectColor = selectColor;
			flagTabsChanged();
		}
		public String getUnselectColor() {
			return unselectColor;
		}
		public void setUnselectColor(String unselectColor) {
			if (OH.eq(this.unselectColor, unselectColor))
				return;
			this.unselectColor = unselectColor;
			flagTabsChanged();
		}
		public String getSelectTextColor() {
			return selectTextColor;
		}
		public void setSelectTextColor(String selectTextColor) {
			if (OH.eq(this.selectTextColor, selectTextColor))
				return;
			this.selectTextColor = selectTextColor;
			flagTabsChanged();
		}
		public String getUnselectTextColor() {
			return unselectTextColor;
		}
		public void setUnselectTextColor(String unselectTextColor) {
			if (OH.eq(this.unselectTextColor, unselectTextColor))
				return;
			this.unselectTextColor = unselectTextColor;
			flagTabsChanged();
		}
		public String getBlinkColor() {
			return blinkColor;
		}
		public void setBlinkColor(String blinkColor) {
			if (OH.eq(this.blinkColor, blinkColor))
				return;
			this.blinkColor = blinkColor;
			flagTabsChanged();
		}
		public String getBlinkPeriod() {
			return blinkPeriod;
		}
		public void setBlinkPeriod(String blinkPeriod) {
			if (OH.eq(this.blinkPeriod, blinkPeriod))
				return;
			this.blinkPeriod = blinkPeriod;
			flagTabsChanged();
		}
		public void setHover(String hover) {
			if (OH.eq(this.hover, hover))
				return;
			this.hover = hover;
			flagTabsChanged();
		}
		public String getHover() {
			return this.hover;
		}
		public void setHtmlIdSelector(String his) {
			if (OH.eq(this.htmlIdSelector, his))
				return;
			this.htmlIdSelector = his;
			flagTabsChanged();
		}
		public String getHtmlIdSelector() {
			return this.htmlIdSelector;
		}

		@Override
		public String toString() {
			return this.title;
		}
	}

	@Override
	protected void onUserDeleteChild(String childPortletId) {
		Portlet removed = removeChild(childPortletId);
		removed.onClosed();
	}

	public void clearDefaultTab() {
		for (Tab tab : this.tabs.values()) {
			tab.setIsDefault(false);
		}
	}

	public void removeTab(Tab tab) {
		removeChild(tab.getPortlet().getPortletId());
	}

	protected String portletToConfigSaveId(Portlet portlet) {
		return portlet.getPortletId();
	}
	protected Portlet configSaveIdToPortlet(Map<String, String> origToNewIdMapping, String portletId) {
		return getManager().getPortlet(origToNewIdMapping.get(portletId));
	}
	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("selected", this.visibleChildLocation);
		List<Map<String, Object>> tabConfigs = new ArrayList<Map<String, Object>>();
		for (Tab tab : tabs.values()) {
			Portlet portlet = tab.getPortlet();
			boolean hidden = tab.isHidden();
			if (portlet instanceof TabPlaceholderPortlet) {
				portlet = ((TabPlaceholderPortlet) portlet).getTearoutPortlet();
				hidden = false;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("tabId", tab.getTabId());
			map.put("title", tab.getTitle());
			map.put("portlet", portletToConfigSaveId(portlet));
			if (tab.getIsDefault())
				map.put("default", tab.getIsDefault());
			if (hidden)
				map.put("hidden", hidden);
			tabConfigs.add(map);
		}
		r.put("tabs", tabConfigs);
		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		List<Map<String, Object>> tabConfigs = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "tabs");
		int selected = CH.getOrThrow(Caster_Integer.INSTANCE, configuration, "selected");

		tabs.clear();
		int maxId = this.nextTabId;
		for (Map<String, Object> tagConfig : tabConfigs) {
			String tabTitle;
			if (tagConfig.containsKey("title2")) {
				tabTitle = CH.getOrThrow(Caster_String.INSTANCE, tagConfig, "title2");
			} else {
				tabTitle = CH.getOr(Caster_String.INSTANCE, tagConfig, "title", "");
			}
			String portletId = CH.getOrThrow(Caster_String.INSTANCE, tagConfig, "portlet");
			Boolean isDefault = CH.getOr(Caster_Boolean.INSTANCE, tagConfig, "default", Boolean.FALSE);
			int id = CH.getOr(Caster_Integer.INSTANCE, tagConfig, "tabId", -1);
			Tab t = addChild(tabTitle, configSaveIdToPortlet(origToNewIdMapping, portletId), id);
			maxId = Math.max(maxId, t.getTabId() + 1);
			t.setIsDefault(isDefault);
			t.setHidden(CH.getOr(Caster_Boolean.PRIMITIVE, tagConfig, "hidden", false));
		}
		this.nextTabId = maxId;
		Tab defaultTab = getDefaultTab();
		if (defaultTab != null) {
			selectTab(defaultTab.location);
		} else {

			if (selected >= 0 && selected < getChildrenCount())
				this.selectTab(selected);
			else
				LH.warning(log, "Could not select tab for ", getManager().describeUser(), ": ", getPortletId());
		}
	}
	public void redockTabs() {
		for (Tab tab : this.tabs.values()) {
			if (tab.getPortlet() instanceof TabPlaceholderPortlet) {
				TabPlaceholderPortlet ph = (TabPlaceholderPortlet) tab.getPortlet();
				ph.popin();
				tab.setHidden(false);
			}
		}
	}

	public Tab getDefaultTab() {
		for (Tab tab : this.tabs.values()) {
			if (tab.getIsDefault()) {
				return tab;
			}
		}
		return null;//this.tabs.getSize() == 0 || this.visibleChildLocation == -1 ? null : this.tabs.getAt(0);
	}

	public void setTabHidden(int tabId, boolean hidden) {
		Tab tabAtLocation = this.getTabById(tabId);
		if (tabAtLocation.isHidden() == hidden)
			return;
		tabAtLocation.setHidden(hidden);
		flagTabsChanged();
	}
	public boolean getTabHidden(int location) {
		return this.getTabById(location).isHidden();
	}

	public static class Builder extends AbstractPortletBuilder<TabPortlet> {

		public Builder() {
			super(TabPortlet.class);
			setIcon("portlet_icon_tabs");
		}

		public static final String ID = "tab";

		@Override
		public TabPortlet buildPortlet(PortletConfig portletConfig) {
			TabPortlet r = new TabPortlet(portletConfig);
			return r;

		}

		@Override
		public String getPortletBuilderName() {
			return "Tabs";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void bringToFront(String portletId) {
		Tab window = findTab(getChild(portletId));
		if (getSelectedTab() == window)
			return;
		selectTab(window.getLocation());
		if (getVisible())
			layoutChildren();
	}

	@Override
	public boolean hasVacancy() {
		return true;
	}

	@Override
	public boolean isCustomizable() {
		return this.tabPortletStyle.isCustomizable;
	}

	//TODO:inline
	public void setIsCustomizable(boolean isCustomizable) {
		this.tabPortletStyle.setIsCustomizable(isCustomizable);
	}

	public void setActiveTab(Portlet child) {
		if (child == null) {
			selectTab(-1);
			return;
		}
		for (int i = 0; i < this.tabs.getSize(); i++)
			if (tabs.getAt(i).getPortlet() == child) {
				selectTab(i);
				getSelectedTab().getPortlet().onUserRequestFocus(null);
				return;
			}
		throw new RuntimeException("not a child: " + child);
	}

	public void addTabListener(TabListener tabListener) {
		this.tabListeners.add(tabListener);
	}
	public void removeTabListener(TabListener tabListener) {
		this.tabListeners.remove(tabListener);
	}

	public void setTabManager(TabManager tabManager) {
		this.tabManager = tabManager;
	}

	public TabManager getTabManager() {
		return tabManager;
	}

	public Tab getTabAtLocation(int tabIndex) {
		return tabIndex == -1 ? null : tabs.getAt(tabIndex);
	}

	public Tab getTabForPortlet(Portlet portlet) {
		for (Tab i : this.tabs.values())
			if (i.getPortlet() == portlet)
				return i;
		return null;
	}
	public Tab getTabByName(String name) {
		for (Tab i : this.tabs.values())
			if (i.getTitle().equals(name))
				return i;
		return null;
	}

	public boolean hasDefaultTab() {
		for (Tab tab : getTabs()) {
			if (tab.getIsDefault())
				return true;
		}
		return false;
	}

	final public Iterable<Tab> getTabs() {
		return tabs.values();
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		Integer tabIndex = (Integer) source.getCorrelationData();
		if (ConfirmDialog.ID_YES.equals(id) && tabIndex != null)
			removeChild(tabs.getAt(tabIndex).getPortlet().getPortletId());
		return true;
	}

	@Override
	public int getChildOffsetX(String id) {
		if (visibleChildLocation == -1)
			return -1;
		final Portlet portlet = getTabAtLocation(visibleChildLocation).getPortlet();
		if (tabPortletStyle.getTabFloatSize() != TabPortletStyle.NO_FLOAT)
			return 0;
		if (OH.eq(portlet.getPortletId(), id))
			return !tabPortletStyle.isVertical || tabPortletStyle.isOnBottom ? 0 : getTotalTabHeight();
		return -1;
	}

	@Override
	public int getChildOffsetY(String id) {
		if (visibleChildLocation == -1)
			return -1;
		final Portlet portlet = getTabAtLocation(visibleChildLocation).getPortlet();
		if (OH.eq(portlet.getPortletId(), id)) {
			if (tabPortletStyle.getTabFloatSize() != TabPortletStyle.NO_FLOAT)
				return 0;
			return tabPortletStyle.isVertical || tabPortletStyle.isOnBottom ? 0 : getTotalTabHeight();
		}
		return -1;
	}

	@Override
	public void onMenuItem(String id) {
		Tab tab = this.currentMenuTab;
		if (tab != null) {
			if (this.tabManager == null) {
				if ("deleteTab".equals(id) && tabPortletStyle.isCustomizable) {
					ConfirmDialogPortlet deletePortlet = new ConfirmDialogPortlet(generateConfig(),
							"Are you sure you want to delete '" + tabs.getAt(tab.getLocation()).getTitle() + "' Tab?", ConfirmDialogPortlet.TYPE_YES_NO, this);
					deletePortlet.setCorrelationData(tab.getLocation());
					getManager().showDialog("Delete tab", deletePortlet);
				}

			} else
				this.tabManager.onUserMenu(this, tab, id);
		}
	}

	@Override
	public void onMenuDismissed() {
		this.currentMenuTab = null;
	}

	public Set<String> getTabTitles() {
		Set<String> r = new HashSet<String>(this.tabs.getSize());
		for (Tab i : this.tabs.values())
			r.add(i.getTitle());
		return r;
	}

	public void moveTabLeft(Tab tab) {
		final int origLocation = tab.getLocation();
		if (origLocation > 0)
			moveTab(tab, origLocation - 1);
	}
	public void moveTabRight(Tab tab) {
		final int origLocation = tab.getLocation();
		if (origLocation < this.tabs.getSize() - 1)
			moveTab(tab, origLocation + 1);
	}
	public void moveTabLeftmost(Tab tab) {
		moveTab(tab, 0);
	}
	public void moveTabRightmost(Tab tab) {
		moveTab(tab, this.tabs.getSize() - 1);
	}

	public int getTabsCount() {
		return this.tabs.getSize();
	}

	public Tab getTabByIdNoThrow(int tabId) {
		return this.tabs.getNoThrow(tabId);
	}
	public Tab getTabById(int tabId) {
		return this.tabs.get(tabId);
	}

}
