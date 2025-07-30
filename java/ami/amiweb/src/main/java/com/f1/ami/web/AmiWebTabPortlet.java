package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmError;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Tabs;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.office.spreadsheet.SpreadSheetWorksheet;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.TabListener;
import com.f1.suite.web.portal.impl.TabManager;
import com.f1.suite.web.portal.impl.TabPlaceholderPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.TabPortletStyle;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.table.impl.SpreadSheetBuilder;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.MapInMapInMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.MutableCalcFrame;

public class AmiWebTabPortlet extends AmiWebAbstractContainerPortlet
		implements AmiWebDmPortlet, TabManager, FormPortletContextMenuFactory, FormPortletContextMenuListener, ConfirmDialogListener, TabListener {

	public static final String FORMULA_TITLE = "title";
	public static final String FORMULA_UNSELECT_COLOR = "unselect_color";
	public static final String FORMULA_SELECT_COLOR = "select_color";
	public static final String FORMULA_UNSELECT_TEXT_COLOR = "unselect_text_color";
	public static final String FORMULA_SELECT_TEXT_COLOR = "select_text_color";
	public static final String FORMULA_BLINK_COLOR = "blink_color";
	public static final String FORMULA_BLINK_PERIOD = "blink_period";
	public static final ParamsDefinition CALLBACK_DEF_ONCLICK = new ParamsDefinition("onClick", Object.class,
			"Boolean fromArrow,com.f1.ami.web.AmiWebTabEntry curTab,com.f1.ami.web.AmiWebTabEntry prevTab");
	static {
		CALLBACK_DEF_ONCLICK.addDesc("Called when a tab is clicked");
		CALLBACK_DEF_ONCLICK.addParamDesc(0, "true=arrow icon is clicked");
		CALLBACK_DEF_ONCLICK.addParamDesc(1, "current tab");
		CALLBACK_DEF_ONCLICK.addParamDesc(2, "previous tab");
	}

	private AmiWebService service;
	private boolean isUndockAllowed = true;
	private static final Logger log = LH.get();
	private AmiWebInnerTabPortlet tab;
	private IntKeyMap<AmiWebTabEntry> amiTabs = new IntKeyMap<AmiWebTabEntry>();
	private Map<String, AmiWebTabEntry> tabsById = new HashMap<String, AmiWebTabEntry>();
	private int tabPaddingBottom;

	private boolean hideExportSS;
	private static final int MAX_TAB_TITLE_LENGTH = 32;

	public AmiWebTabPortlet(PortletConfig manager) {
		super(manager);
		this.getGridPortlet().setCssStyle("style.overflow=visible");
		this.setShowConfigButtons(false);
		this.tab = new AmiWebInnerTabPortlet(generateConfig(), this);
		this.tab.addTabListener(this);
		setChild(tab);
		tab.getTabPortletStyle().setHasMenuAlways(true);
		tab.getTabPortletStyle().setHasExtraButtonAlways(false);
		tab.setTabManager(this);
		this.service = AmiWebUtils.getService(getManager());
		this.getStylePeer().initStyle();
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebTabPortlet>
			implements AmiWebPortletContainerBuilder<AmiWebTabPortlet>, AmiWebDmPortletBuilder<AmiWebTabPortlet> {

		public Builder() {
			super(AmiWebTabPortlet.class);
			setIcon("portlet_icon_tabs");
		}

		public static final String ID = "tab";

		@Override
		public AmiWebTabPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebTabPortlet r = new AmiWebTabPortlet(portletConfig);
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

		@Override
		public void extractChildPorletIds(Map<String, Object> config, Map<String, Map> sink) {
			List<Map> tabs = CH.getOrThrow(List.class, config, "tabs");
			for (Map tab : tabs)
				sink.put(CH.getOrThrow(String.class, tab, "child"), tab);
		}

		@Override
		public boolean removePortletId(Map<String, Object> portletConfig, String amiPanelId) {
			List<Map> tabs = CH.getOrThrow(List.class, portletConfig, "tabs");
			for (int i = 0; i < tabs.size(); i++)
				if (OH.eq(amiPanelId, CH.getOrThrow(String.class, tabs.get(i), "child"))) {
					tabs.remove(i);
					return true;
				}
			return false;
		}
		@Override
		public boolean replacePortletId(Map<String, Object> portletConfig, String oldPanelId, String nuwPanelId) {
			List<Map> tabs = CH.getOrThrow(List.class, portletConfig, "tabs");
			for (int i = 0; i < tabs.size(); i++)
				if (OH.eq(oldPanelId, CH.getOrThrow(String.class, tabs.get(i), "child"))) {
					tabs.get(i).put("child", nuwPanelId);
					return true;
				}
			return false;
		}

		@Override
		public List<String> extractUsedDmAndTables(Map<String, Object> portletConfig) {
			List<Map> tabscnf = CH.getOrThrow(List.class, portletConfig, "tabs");
			List<String> r = new ArrayList<String>(tabscnf.size());
			for (int i = 0; i < tabscnf.size(); i++)
				r.add((String) tabscnf.get(i).get("dmadn"));
			return r;
		}

		@Override
		public void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name) {
			List<Map> tabscnf = CH.getOrThrow(List.class, portletConfig, "tabs");
			tabscnf.get(position).put("dmadn", name);
		}

	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();

		List<Map<String, Object>> tabs = new ArrayList<Map<String, Object>>(this.amiTabs.size());
		List<AmiWebTabEntry> tabs2 = CH.l(this.amiTabs.values());
		Collections.sort(tabs2, AmiWebTabEntry.COMPARATOR_LOCATION);
		for (AmiWebTabEntry i : tabs2) {
			Map<String, Object> config = i.getConfiguration();
			Portlet p = i.getTab().getPortlet();
			if (p instanceof TabPlaceholderPortlet)
				p = ((TabPlaceholderPortlet) p).getTearoutPortlet();
			//			if (i.isTransient())
			//				continue;
			AmiWebAliasPortlet amiWebAliasPortlet = (AmiWebAliasPortlet) p;
			if (!this.service.getDesktop().getIsDoingExportTransient())
				amiWebAliasPortlet = amiWebAliasPortlet.getNonTransientPanel();
			if (amiWebAliasPortlet == null)//isTransient
				continue;
			config.put("child", AmiWebUtils.getRelativeAlias(getAmiLayoutFullAlias(), amiWebAliasPortlet.getAmiLayoutFullAliasDotId()));
			tabs.add(config);
		}
		if (this.tab.getDefaultTab() != null)
			r.put("defaultTab", getTabFor(this.tab.getDefaultTab()).getId());
		else
			r.put("defaultTab", "");
		r.put("tabs", tabs);
		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		List<Map<String, Object>> tabs = (List<Map<String, Object>>) configuration.get("tabs");
		if (tabs != null) {
			for (Map<String, Object> i : tabs) {
				String childPanelId = (String) i.get("child");
				AmiWebAliasPortlet panel = service.getPortletByAliasDotPanelId(AmiWebUtils.getFullAlias(getAmiLayoutFullAlias(), childPanelId));
				Tab t = this.tab.addChild("", panel);
				//				this.amiTabs.getOrThrow(t.getTabId()).init(i);
				AmiWebTabEntry tab2 = new AmiWebTabEntry(this, t, i);
				this.amiTabs.putOrThrow(tab2.getTabId(), tab2);
				CH.putOrThrow(this.tabsById, tab2.getId(), tab2);
				tab2.updateAri();
				tab2.addToDomManager();
			}
			redoDmReferences();
		}
		if (configuration.containsKey("selected"))
			this.tab.selectTab(CH.getOrThrow(Caster_Integer.INSTANCE, configuration, "selected"));
		if (configuration.containsKey("default")) {//backwards compatiblity
			Tab defaultTab = this.tab.getTabAtLocation(CH.getOrThrow(Caster_Integer.INSTANCE, configuration, "default"));
			defaultTab.setIsDefault(true);
			this.tab.setActiveTab(defaultTab.getPortlet());
		}
		if (configuration.containsKey("defaultTab")) {
			String tabId = CH.getOrThrow(Caster_String.INSTANCE, configuration, "defaultTab");
			if ("".contentEquals(tabId)) {
				this.tab.clearDefaultTab();
				setSelectedTab(null);
			} else {
				AmiWebTabEntry t = this.getTabById(tabId);
				t.setIsDefault(true);
				this.tab.setActiveTab(t.getTab().getPortlet());
			}
		}
		onEditModeChanged(this.service.getDesktop().getInEditMode());
		redoDmReferences();
	}
	@Override
	public void onUserMenu(TabPortlet tabPortlet, Tab tab, String menuId) {
		service.getSecurityModel().assertPermitted(this, menuId, "popout,undock,redock,exportss,arrange,cust_menu_action_*");
		Tab selectedTab = this.tab.getSelectedTab();
		if ("popout".equals(menuId)) {
			if (!isUndockAllowed)
				service.getSecurityModel().throwSecurityException(this, "popout");
			popoutTab(tab);
		} else if ("undock".equals(menuId)) {
			if (!isUndockAllowed)
				service.getSecurityModel().throwSecurityException(this, "undock");
			undockTab(tab);
		} else if ("redock".equals(menuId)) {
			redockTab(tab);
		} else if ("arrange".equals(menuId)) {
			getManager().showDialog("Arrange Tabs", new AmiWebArrangeTabsEndUserPortlet(generateConfig(), this, this.getTabFor(selectedTab)));
		} else if ("exportss".equals(menuId)) {
			boolean formatSheet = "always".equals(service.getVarsManager().getSpreadSheetFormatOption()) ? true : false;
			SpreadSheetBuilder sb = new SpreadSheetBuilder();
			Collection<AmiWebAbstractTablePortlet> tables = AmiWebUtils.findPortletsByTypeFollowUndocked(tab.getPortlet(), AmiWebAbstractTablePortlet.class);
			for (AmiWebAbstractTablePortlet i : tables) {
				if (i.getTable().getRows().size() > SpreadSheetWorksheet.MAX_ROW_COUNT) {
					getManager().showAlert("Max spreadsheet row of " + SpreadSheetWorksheet.MAX_ROW_COUNT + " count exceeded: " + i.getTable().getRows().size());
					return;
				}
			}
			for (AmiWebAbstractTablePortlet i : tables) {
				if (i instanceof AmiWebObjectTablePortlet) {
					AmiWebObjectTablePortlet rtTable = (AmiWebObjectTablePortlet) i;
					if (rtTable.isHalted()) {
						rtTable.setVisible(true);
						sb.addSheet(i.getTablePortlet(), i.getTablePortlet().getTable().getTable().getTitle(), false, formatSheet);
						rtTable.stopProcessingAMiData(true);
						rtTable.setVisible(false);
						continue;
					}
				}
				sb.addSheet(i.getTablePortlet(), i.getTablePortlet().getTable().getTable().getTitle(), false, formatSheet);
			}
			byte[] bytes = sb.build();
			getManager().pushPendingDownload(new BasicPortletDownload(tab.getTitle() + ".xlsx", bytes));
		} else if ("rename_tab".equals(menuId)) {
			if (!this.getService().getDesktop().getInEditMode())
				return;
			getManager().showDialog("Tab Settings", new AmiWebTabEntrySettingsPortlet(generateConfig(), this, amiTabs.get(tab.getTabId())));
		} else if ("delete_tab".equals(menuId)) {
			if (selectedTab.getPortlet() instanceof AmiWebBlankPortlet)
				deleteTab(selectedTab);
			else if (selectedTab.getPortlet() instanceof TabPlaceholderPortlet) {
				getManager().showAlert("Please redock tab before deleting");
			} else {
				this.service.getDesktop().onUserDeletePortlet((AmiWebAliasPortlet) selectedTab.getPortlet());
			}
		}

		//		} else if ("move_left".equals(menuId)) {
		//			this.tab.moveTabLeft(selectedTab);
		//		} else if ("move_right".equals(menuId)) {
		//			this.tab.moveTabRight(selectedTab);
		//		} else if ("move_leftmost".equals(menuId)) {
		//			this.tab.moveTabLeftmost(selectedTab);
		//		} else if ("move_rightmost".equals(menuId)) {
		//			this.tab.moveTabRightmost(selectedTab);
		else if ("add_left".equals(menuId)) {
			AmiWebDesktopPortlet desktop = this.service.getDesktop();
			int selPosition = this.tab.getSelectedTab().getLocation();
			String title = SH.getNextId("New Tab", tabPortlet.getTabTitles(), 2);
			Tab child = tabPortlet.addChild(selPosition, title, desktop.newAmiWebAmiBlankPortlet(getAmiLayoutFullAlias()), true);
			desktop.getManager().onPortletAdded(child.getPortlet());
			showTabsStyleEditorIfSet();
		} else if ("add_right".equals(menuId)) {
			AmiWebDesktopPortlet desktop = this.service.getDesktop();
			int selPosition = this.tab.getSelectedTab().getLocation();
			String title = SH.getNextId("New Tab", tabPortlet.getTabTitles(), 2);
			Tab child = tabPortlet.addChild(selPosition + 1, title, desktop.newAmiWebAmiBlankPortlet(getAmiLayoutFullAlias()), true);
			desktop.getManager().onPortletAdded(child.getPortlet());
			showTabsStyleEditorIfSet();
		} else if ("newTab".equals(menuId)) {
			AmiWebDesktopPortlet desktop = this.service.getDesktop();
			int selPosition = this.getTabsCount() - 1;
			String title = SH.getNextId("New Tab", tabPortlet.getTabTitles(), 2);
			Tab child = tabPortlet.addChild(selPosition + 1, title, desktop.newAmiWebAmiBlankPortlet(getAmiLayoutFullAlias()), true);
			desktop.getManager().onPortletAdded(child.getPortlet());
			showTabsStyleEditorIfSet();
		} else if ("set_default".equals(menuId)) {
			selectedTab.setIsDefault(true);
			getService().getDesktop().flagUpdateWindowLinks();
		} else if ("clear_default".equals(menuId)) {
			tabPortlet.clearDefaultTab();
			getService().getDesktop().flagUpdateWindowLinks();
		} else if (isCustomContextMenuAction(menuId)) {
			processCustomContextMenuAction(menuId);
		}
	}
	public boolean hasOverrides() {
		AmiWebTabEntry st = getSelectedTab();
		if (st == null || !st.isTransient()) {
			if (tab.getSelectedTab() != tab.getDefaultTab())
				return true;
		}
		for (AmiWebTabEntry i : this.amiTabs.values()) {
			if (i.hasLocationHiddenOverrides())
				return true;
		}
		return false;
	}
	public void setOverrideToDefault() {
		AmiWebTabEntry selected = getSelectedTab();
		if (selected == null || !selected.isTransient()) {
			if (selected == null)
				tab.clearDefaultTab();
			else
				selected.setIsDefault(true);
		}
		for (int i = 0; i < getTabsCount(); i++)
			getTabAt(i).setLocation(i, false);
	}
	public void clearOverrides() {
		if (!hasOverrides())
			return;
		for (AmiWebTabEntry i : this.amiTabs.values())
			i.clearLocationHiddenOverrides();
		List<AmiWebTabEntry> tabs = CH.l(this.amiTabs.values());
		Collections.sort(tabs, AmiWebTabEntry.COMPARATOR_LOCATION);
		for (AmiWebTabEntry i : tabs) {
			this.getInnerContainer().moveTab(i.getTab(), i.getLocation(false));
			i.getTab().setHidden(i.getHidden(false));
		}
	}
	public void showTabsStyleEditorIfSet() {
		if (AmiWebConsts.STYLE_EDITOR_SHOW.equals(service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_SHOW_STYLE_EDITOR_TABS)) && service.getDesktop().getInEditMode())
			AmiWebUtils.showStyleDialog("Tab Style", this, new AmiWebEditStylePortlet(this.getStylePeer(), generateConfig()), generateConfig());
	}
	public void redockTab(Tab tab) {
		TabPlaceholderPortlet placeholder = (TabPlaceholderPortlet) tab.getPortlet();
		if (this.tab.getTabPortletStyle().getHideTabWhenPoppedOut() == true)
			tab.setHidden(false);
		placeholder.popin();
	}
	public void popoutTab(Tab tab) {
		Portlet p = tab.getPortlet();
		if (p instanceof TabPlaceholderPortlet) {
			TabPlaceholderPortlet tabPlaceholderPortlet = (TabPlaceholderPortlet) p;
			if (tabPlaceholderPortlet.getTearoutWindow() != null && tabPlaceholderPortlet.getTearoutWindow().isPoppedOut())
				return;
			tabPlaceholderPortlet.popout();
			tabPlaceholderPortlet.setFullyDockOnClose(false);
		} else {
			int x = PortletHelper.getAbsoluteLeft(p);
			int y = PortletHelper.getAbsoluteTop(p);
			TabPlaceholderPortlet p2 = new TabPlaceholderPortlet(tab, this.service.getDesktop().getDesktop(), service.getDesktop().getIsPopoutEnabled(), x, y);
			p2.popoutDirectly();
		}
	}
	public void popoutTab(Tab tab, int x, int y, int w, int h) {
		Portlet p = tab.getPortlet();
		if (p instanceof TabPlaceholderPortlet) {
			TabPlaceholderPortlet tabPlaceholderPortlet = (TabPlaceholderPortlet) p;
			if (tabPlaceholderPortlet.getTearoutWindow() != null && tabPlaceholderPortlet.getTearoutWindow().isPoppedOut())
				return;
			tabPlaceholderPortlet.popout(x, y, w, h);
			tabPlaceholderPortlet.setFullyDockOnClose(false);
		} else {
			TabPlaceholderPortlet p2 = new TabPlaceholderPortlet(tab, this.service.getDesktop().getDesktop(), service.getDesktop().getIsPopoutEnabled(), x, y);
			p2.popoutDirectly(x, y, w, h);
		}
	}
	public TabPlaceholderPortlet undockTab(Tab tab) {
		Portlet p = tab.getPortlet();
		int x = PortletHelper.getAbsoluteLeft(p);
		int y = PortletHelper.getAbsoluteTop(p);

		if (p instanceof TabPlaceholderPortlet) { //The tab is already undocked
			TabPlaceholderPortlet r = (TabPlaceholderPortlet) p;
			if (r.getTearoutWindow().isPoppedOut()) {
				r.setFullyDockOnClose(false);
				r.getTearoutWindow().closePopup();
			}
			return r;
		}
		TabPlaceholderPortlet p2 = new TabPlaceholderPortlet(tab, this.service.getDesktop().getDesktop(), service.getDesktop().getIsPopoutEnabled(), x, y);
		p2.undock();

		return p2;
	}
	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		super.handleCallback(callback, attributes);
	}
	@Override
	public WebMenu createMenu(TabPortlet tabPortlet, Tab tab) {
		BasicWebMenu r = new BasicWebMenu();
		AmiWebTabEntry amiTab = getTabFor(tab);
		boolean inEditMode = this.service.getDesktop().getInEditMode();
		if (inEditMode) {
			if (this.isReadonlyLayout()) {
				r.addChild(new BasicWebMenuLink("READONLY PANEL (" + amiTab.getId() + ")", false, "").setCssStyle(AmiWebConsts.TITLE_CSS2));
			} else if (this.isTransient()) {
				r.addChild(new BasicWebMenuLink("TRANSIENT PANEL (" + amiTab.getId() + ")", false, "").setCssStyle(AmiWebConsts.TITLE_CSS2));
			} else if (amiTab.isTransient()) {
				r.addChild(new BasicWebMenuLink("TRANSIENT TAB (" + amiTab.getId() + ")", false, "").setCssStyle(AmiWebConsts.TITLE_CSS2));
			} else
				r.addChild(new BasicWebMenuLink("TAB (" + amiTab.getId() + ")", false, "").setCssStyle(AmiWebConsts.TITLE_CSS2));
		}
		if (isUndockAllowed && !PortletHelper.findParentByType(this, RootPortlet.class).isPopupWindow() && !service.getDesktop().getDesktop().isInTearout(this)) {
			if (!(tab.getPortlet() instanceof TabPlaceholderPortlet)) {
				r.addChild(new BasicWebMenuLink("Popout", true, "popout"));
				r.addChild(new BasicWebMenuLink("Undock", true, "undock"));
			} else
				r.addChild(new BasicWebMenuLink("Redock", true, "redock"));
		}
		if (!this.getHideExportSsMenuItem()) {
			boolean hasTables = !AmiWebUtils.findPortletsByTypeFollowUndocked(tab.getPortlet(), AmiWebAbstractTablePortlet.class).isEmpty();
			if (hasTables)
				r.addChild(new BasicWebMenuLink("Export To Spreadsheet", true, "exportss"));
		}
		if (!inEditMode) {
			if (!this.getInnerContainer().getTabPortletStyle().getHideArrangeTabs())
				r.addChild(new BasicWebMenuLink("Arrange Tabs", true, "arrange"));
		} else {
			String editMenuCss = "className=ami_edit_menu";
			r.addChild(new BasicWebMenuLink("Arrange Tabs", true, "arrange"));
			if (!this.isReadonlyLayout() && !this.isTransient()) {
				r.addChild(new BasicWebMenuLink("Settings / Rename", true, "rename_tab").setCssStyle(editMenuCss));
				if (!amiTab.isTransient()) {
					boolean isDefault = tab.getIsDefault();
					if (!isDefault)
						r.addChild(new BasicWebMenuLink("Set as Default", true, "set_default").setCssStyle(editMenuCss));
					else
						r.addChild(new BasicWebMenuLink("Clear Default", true, "clear_default").setCssStyle(editMenuCss));
				}
				r.addChild(new BasicWebMenuLink("Delete", true, "delete_tab").setCssStyle(editMenuCss));
				boolean isVertical = this.tab.getTabPortletStyle().getIsVertical();
				r.addChild(new BasicWebMenuLink("Add tab " + (isVertical ? "above" : "to left"), true, "add_" + (isVertical ? "right" : "left")).setCssStyle(editMenuCss));
				r.addChild(new BasicWebMenuLink("Add tab " + (isVertical ? "below" : "to right"), true, "add_" + (isVertical ? "left" : "right")).setCssStyle(editMenuCss));
			}
		}
		addCustomMenuItems(r);
		return r;
	}
	private boolean getHideExportSsMenuItem() {
		return this.hideExportSS;
	}
	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		TabPortlet tab = this.getInnerContainer();
		if (tab.getChildrenCount() == 1) {
			WebMenu changeMenu = new BasicWebMenu("Change Table Type", true);
			changeMenu.add(new BasicWebMenuLink("contents of the single tab", true, "remove_last_tab"));
		}
		headMenu.add(new BasicWebMenuLink("Add Tab...", true, "add_tab").setBackgroundImage(AmiWebConsts.ICON_ADD));
		boolean showUntab = this.getInnerContainer().getTabsCount() == 1;
		headMenu.add(new BasicWebMenuLink("Remove Tab, Keep Panel...", showUntab, "untab_last").setBackgroundImage(AmiWebConsts.ICON_DELETE));

	}
	@Override
	public boolean onAmiContextMenu(String id) {
		if ("remove_last_tab".equals(id)) {
			Collection<AmiWebAliasPortlet> removed = this.getAmiChildren();
			getService().getDesktop().replacePortlet(this.getPortletId(), CH.first(removed));
			return true;
		} else if ("add_tab".equals(id)) {
			TabPortlet tab = this.getInnerContainer();
			String title = SH.getNextId("New Tab", tab.getTabTitles(), 2);
			Tab child = tab.addChild(tab.getChildrenCount(), title, getService().getDesktop().newAmiWebAmiBlankPortlet(this.getAmiLayoutFullAlias()), true);
			getManager().onPortletAdded(child.getPortlet());
			this.showTabsStyleEditorIfSet();
			return true;
		} else if ("untab_last".equals(id)) {
			removeTabKeepPanel();
			return true;
		} else
			return super.onAmiContextMenu(id);
	}
	private void removeTabKeepPanel() {
		if (this.getInnerContainer().getTabsCount() == 1) {
			Tab child = this.getInnerContainer().getTabAtLocation(0);
			AmiWebAliasPortlet replacement = (AmiWebAliasPortlet) child.getPortlet();
			this.getService().getDesktop().replacePortlet(this.getPortletId(), replacement);
		}
	}
	@Override
	public void onUserAddTab(TabPortlet tabPortlet) {
		AmiWebDesktopPortlet desktop = this.service.getDesktop();
		int selPosition = this.getTabsCount() - 1;
		String title = SH.getNextId("New Tab", tabPortlet.getTabTitles(), 2);
		Tab child = tabPortlet.addChild(selPosition + 1, title, desktop.newAmiWebAmiBlankPortlet(getAmiLayoutFullAlias()), true);
		desktop.getManager().onPortletAdded(child.getPortlet());
		showTabsStyleEditorIfSet();
	}

	@Override
	public void onUserRenamedTab(TabPortlet tabPortlet, Tab tab, String newName) {
		if (isInitDone())
			updateTab(amiTabs.get(tab.getTabId()));
	}
	public boolean getIsUndockAllowed() {
		return isUndockAllowed;
	}
	public void setIsUndockAllowed(Boolean isUndockAllowed) {
		if (isUndockAllowed == null || this.isUndockAllowed == isUndockAllowed)
			return;
		this.isUndockAllowed = isUndockAllowed;
		if (isUndockAllowed == false)
			this.tab.redockTabs();
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		//		if ("RESET_AND_ARRANGE".equals(source.getCallback())) {
		//			if (ConfirmDialogPortlet.ID_YES.contentEquals(id)) {
		//				AmiWebTabEntry t = getSelectedTab();
		//				clearOverrides();
		//				getManager().showDialog("Arrange Tabs", new AmiWebArrangeTabsPortlet(generateConfig(), this, t));
		//			}
		//		}
		return true;
	}
	private void deleteTab(Tab selectedTab) {
		Portlet portlet = selectedTab.getPortlet();
		if (portlet instanceof TabPlaceholderPortlet) {
			((TabPlaceholderPortlet) portlet).popin();
			portlet = selectedTab.getPortlet();
		}
		this.tab.removeChild(portlet.getPortletId());
		portlet.close();
	}
	@Override
	public void onStyleValueChanged(short key, Object old, Object nuw) {
		super.onStyleValueChanged(key, old, nuw);
		switch (key) {
			case AmiWebStyleConsts.CODE_VT:
				this.tab.getTabPortletStyle().setIsVertical((Boolean) nuw);
				onPaddingChanged();
				break;
			case AmiWebStyleConsts.CODE_RT:
				this.tab.getTabPortletStyle().setIsOnRight((Boolean) nuw);
				getService().getDesktop().flagUpdateWindowLinks();
				onPaddingChanged();
				break;
			case AmiWebStyleConsts.CODE_BTM:
				this.tab.getTabPortletStyle().setIsOnBottom((Boolean) nuw);
				getService().getDesktop().flagUpdateWindowLinks();
				onPaddingChanged();
				break;
			case AmiWebStyleConsts.CODE_FONT_SZ:
				this.tab.getTabPortletStyle().setFontSize(Caster_Integer.INSTANCE.cast(nuw));
				onPaddingChanged();
				break;
			case AmiWebStyleConsts.CODE_TAB_HT:
				this.tab.getTabPortletStyle().setTabHeight(Caster_Integer.INSTANCE.cast(nuw));
				onPaddingChanged();
				break;
			case AmiWebStyleConsts.CODE_PAD_TP:
				this.tab.getTabPortletStyle().setTabPaddingTop(Caster_Integer.INSTANCE.cast(nuw));
				onPaddingChanged();
				break;
			case AmiWebStyleConsts.CODE_PAD_BTM:
				this.tab.getTabPortletStyle().setTabPaddingBottom(Caster_Integer.INSTANCE.cast(nuw));
				onPaddingChanged();
				break;
			case AmiWebStyleConsts.CODE_PAD_START:
				this.tab.getTabPortletStyle().setTabPaddingStart(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_SPACING:
				this.tab.getTabPortletStyle().setTabSpacing(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_ROUND_LF:
				this.tab.getTabPortletStyle().setLeftRounding(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_ROUND_RT:
				this.tab.getTabPortletStyle().setRightRounding(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_TABS_HIDE:
				this.tab.getTabPortletStyle().setIsHidden((Boolean) nuw);
				onPaddingChanged();
				break;
			case AmiWebStyleConsts.CODE_UNDOCK:
				this.setIsUndockAllowed((Boolean) nuw);
				break;
			case AmiWebStyleConsts.CODE_BG_CL:
				this.tab.getTabPortletStyle().setBackgroundColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SEL_CL:
				this.tab.getTabPortletStyle().setSelectedColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SEL_TXT_CL:
				this.tab.getTabPortletStyle().setSelectTextColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_UNSEL_CL:
				this.tab.getTabPortletStyle().setUnselectedColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_UNSEL_TXT_CL:
				this.tab.getTabPortletStyle().setUnselectTextColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_HIDE_TAB_WHEN_POPPEDOUT:
				this.tab.getTabPortletStyle().setHideTabWhenPoppedOut((Boolean) nuw);
				break;
			case AmiWebStyleConsts.CODE_HASADDBUTTON:
				this.tab.getTabPortletStyle().setHasAddButton((Boolean) nuw);
				break;
			case AmiWebStyleConsts.CODE_ADDBUTTON_CL:
				this.tab.getTabPortletStyle().setAddButtonColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_HIDE_EXPORT_SS_MENUITEM:
				this.setHideExportSsMenuItem((Boolean) nuw);
				break;
			case AmiWebStyleConsts.CODE_HIDE_ARRANGE_TAB_MENUITEM:
				this.tab.getTabPortletStyle().setHideArrangeTabs(Boolean.TRUE.equals(nuw));
				break;
			case AmiWebStyleConsts.CODE_TAB_BDR_CL:
				this.tab.getTabPortletStyle().setBorderColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SEL_TAB_BDR_CL:
				this.tab.getTabPortletStyle().setSelBorderColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SEL_TAB_BDR_SZ:
				this.tab.getTabPortletStyle().setSelBorderSize(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_FONT_FAM:
				this.tab.getTabPortletStyle().setFontFamily((String) nuw);
				onPaddingChanged();
				break;
		}
	}
	private void setHideExportSsMenuItem(Boolean nuw) {
		this.hideExportSS = Boolean.TRUE.equals(nuw);
	}
	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Tabs.TYPE_TABS;
	}
	@Override
	public void clearAmiData() {
	}
	@Override
	public String getPanelType() {
		return "tabs";
	}
	@Override
	public void clearUserSelection() {
	}
	@Override
	public boolean isRealtime() {
		return false;
	}
	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		return null;
	}
	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		return false;
	}
	@Override
	public String getConfigMenuTitle() {
		return "Tabs";
	}
	@Override
	public void getUsedColors(Set<String> sink) {
	}
	@Override
	public TabPortlet getInnerContainer() {
		return this.tab;
	}

	@Override
	protected void rebuilddPadding(int paddingTopPx, int paddingRightPx, int paddingBottomPx, int paddingLeftPx, int innerPaddingTopPx, int innerPaddingRightPx,
			int innerPaddingBottomPx, int innerPaddingLeftPx) {
		int calcTop = paddingTopPx;
		int calcRight = paddingRightPx;
		int calcBottom = paddingBottomPx;
		int calcLeft = paddingLeftPx;
		int calcInnerTop = innerPaddingTopPx;
		int calcInnerRight = innerPaddingRightPx;
		int calcInnerBottom = innerPaddingBottomPx;
		int calcInnerLeft = innerPaddingLeftPx;

		if (this.tab.getTabPortletStyle().getIsHidden() != true) {
			int inner = this.tab.getTabPortletStyle().getTabPaddingBottom();
			int outer = this.tab.getTabPortletStyle().getTabPaddingTop() + this.tab.getTabPortletStyle().getTabHeight();
			if (this.tab.getTabPortletStyle().getIsVertical()) {
				if (this.tab.getTabPortletStyle().getIsOnRight()) {
					calcRight = outer;
					calcInnerRight = innerPaddingRightPx + inner;
				} else {
					calcLeft = outer;
					calcInnerLeft = innerPaddingLeftPx + inner;
				}
			} else {
				if (this.tab.getTabPortletStyle().getIsOnBottom()) {
					calcBottom = outer;
					calcInnerBottom = innerPaddingBottomPx + inner;
				} else {
					calcTop = outer;
					calcInnerTop = innerPaddingTopPx + inner;
				}

			}

		}
		super.rebuilddPadding(calcTop, calcRight, calcBottom, calcLeft, calcInnerTop, calcInnerRight, calcInnerBottom, calcInnerLeft);

		String clr = super.getPaddingShadowColor();
		int hpx = super.getPaddingShadowHPx();
		int spx = super.getPaddingShadowSizePx();
		int vpx = super.getPaddingShadowVPx();
		if (spx > 0 && SH.is(clr)) {
			this.tab.getTabPortletStyle().setSelectShadow(hpx + "px " + vpx + "px " + spx + "px " + (spx / 4) + "px " + clr);
			this.tab.getTabPortletStyle().setUnselectShadow(hpx + "px " + vpx + "px " + (spx / 3) + "px " + (spx / 12) + "px " + clr);
		} else {
			this.tab.getTabPortletStyle().setSelectShadow(null);
			this.tab.getTabPortletStyle().setUnselectShadow(null);
		}
	}
	@Override
	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel) {
	}

	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		if (datamodel != null) {
			MapInMap<String, Integer, AmiWebTabEntry> m = this.dmAliasDotNames2table2tid2tab.get(datamodel.getAmiLayoutFullAliasDotId());
			if (m != null)
				for (AmiWebTabEntry e : m.valuesMulti())
					updateTab(e);
		}
	}
	public void updateTab(AmiWebTabEntry amitab) {
		StringBuilder es = new StringBuilder();
		MutableCalcFrame vals = new MutableCalcFrame();

		AmiWebDm dm = amitab.getDmAliasDotName() == null ? null : this.getService().getDmManager().getDmByAliasDotName(amitab.getDmAliasDotName());
		if (dm != null) {
			AmiWebDmTableSchema schema = dm.getResponseOutSchema().getTable(amitab.getDmTableName());
			if (schema != null)
				vals.putAllTypes(schema.getClassTypes());
			Tableset tableSet = dm.getResponseTableset();
			Table table = tableSet == null ? null : tableSet.getTableNoThrow(amitab.getDmTableName());
			if (table != null) {
				TableList rows = table.getRows();
				if (rows.size() > 0) {
					vals.putAllTypeValues(rows.get(0));
				}
			}
		}
		AmiWebDebugManagerImpl dbg = getService().getDebugManager();
		AmiWebTabEntry thiz = amitab;
		Tab t = tab.getTabById(amitab.getTabId());
		AmiWebScriptManagerForLayout sm = getScriptManager();
		String title = AmiUtils.s(sm.parseAndExecuteAmiScript(amitab.getNameFormula().getFormula(true), es, vals, dbg, AmiDebugMessage.TYPE_FORMULA, thiz, FORMULA_TITLE));
		if (es.length() > 0)
			title = "##Error##";
		t.setTitle(SH.ddd(title, MAX_TAB_TITLE_LENGTH));
		t.setSelectColor(
				AmiUtils.s(sm.parseAndExecuteAmiScript(amitab.getSelectColorFormula().getFormula(true), es, vals, dbg, AmiDebugMessage.TYPE_FORMULA, thiz, FORMULA_SELECT_COLOR)));
		t.setUnselectColor(AmiUtils
				.s(sm.parseAndExecuteAmiScript(amitab.getUnselectColorFormula().getFormula(true), es, vals, dbg, AmiDebugMessage.TYPE_FORMULA, thiz, FORMULA_UNSELECT_COLOR)));
		t.setSelectTextColor(AmiUtils
				.s(sm.parseAndExecuteAmiScript(amitab.getSelectTextColorFormula().getFormula(true), es, vals, dbg, AmiDebugMessage.TYPE_FORMULA, thiz, FORMULA_SELECT_TEXT_COLOR)));
		t.setUnselectTextColor(AmiUtils.s(sm.parseAndExecuteAmiScript(amitab.getUnselectTextColorFormula().getFormula(true), es, vals, dbg, AmiDebugMessage.TYPE_FORMULA, thiz,
				FORMULA_UNSELECT_TEXT_COLOR)));
		t.setBlinkColor(
				AmiUtils.s(sm.parseAndExecuteAmiScript(amitab.getBlinkColorFormula().getFormula(true), es, vals, dbg, AmiDebugMessage.TYPE_FORMULA, thiz, FORMULA_BLINK_COLOR)));

		// extra validation for blink frequency
		Integer parsed = Caster_Integer.INSTANCE
				.cast(sm.parseAndExecuteAmiScript(amitab.getBlinkPeriodFormula().getFormula(true), es, vals, dbg, AmiDebugMessage.TYPE_FORMULA, thiz, FORMULA_BLINK_PERIOD));
		if (parsed == null || parsed < AmiWebTabEntry.MIN_BLINK_PERIOD)
			parsed = AmiWebTabEntry.MIN_BLINK_PERIOD;
		t.setBlinkPeriod(Caster_String.INSTANCE.cast(parsed));

		if (t.getPortlet() instanceof TabPlaceholderPortlet) {
			TabPlaceholderPortlet tpp = (TabPlaceholderPortlet) t.getPortlet();
			String title2 = t.getTitle() + " [undocked]";
			tpp.getTearoutWindow().setName(title2);
			if (tpp.getTearoutWindow().isPoppedOut()) {
				RootPortlet po = getManager().getPopoutForPortletId(tpp.getTearoutPortlet().getPortletId());
				po.setTitle(title2);
			}
		}

	}
	public boolean testFormula(String dmId, AmiWebTabEntry amitab, String dmTableName, String value, StringBuilder errorSink, Class<?> returnType, String formulaName) {
		if (SH.isnt(value))
			return true;
		MutableCalcFrame vals = new MutableCalcFrame();

		AmiWebDm dm = dmId == null ? null : this.getService().getDmManager().getDmByAliasDotName(dmId);
		if (dm != null) {
			AmiWebDmTableSchema schema = dm.getResponseOutSchema().getTable(dmTableName);
			if (schema != null)
				vals.putAllTypes(schema.getClassTypes());
			Tableset tableSet = dm.getResponseTableset();
			Table table = tableSet == null ? null : tableSet.getTableNoThrow(dmTableName);
			if (table != null) {
				TableList rows = table.getRows();
				if (rows.size() > 0) {
					vals.putAllTypeValues(rows.get(0));
				}
			}
		}
		AmiWebDebugManagerImpl dbg = new AmiWebDebugManagerImpl(getService());
		getScriptManager().parseAndExecuteAmiScript(value, errorSink, vals, dbg, AmiDebugMessage.TYPE_TEST, amitab, formulaName);
		return errorSink.length() == 0;
	}
	@Override
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error) {
	}
	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {
	}
	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return getVisible() && this.dmAliasDotNames2table2tid2tab.containsKey(datamodel.getAmiLayoutFullAliasDotId());
	}

	@Override
	public Set<String> getUsedDmTables(String dmName) {
		MapInMap<String, Integer, AmiWebTabEntry> m = this.dmAliasDotNames2table2tid2tab.get(dmName);
		return m == null ? Collections.EMPTY_SET : m.keySet();
	}

	@Override
	public Set<String> getUsedDmVariables(String dmName, String dmTable, Set<String> r) {
		return r;
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebMenuUtils.processContextMenuAction(this.getService(), action, node);
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		Collection<AmiWebDmTableSchema> dm = AmiWebUtils.getUsedTableSchemas(this);
		AmiWebMenuUtils.createVariablesMenu(r, false, this);

		AmiWebMenuUtils.createOperatorsMenu(r, this.getService(), this.getAmiLayoutFullAlias());
		return r;
	}

	@Override
	public void applyUserPref(Map<String, Object> values) {

		if (!values.containsKey("activeTabId")) {
			//backwards compatibility
			Integer location = CH.getOr(Caster_Integer.INSTANCE, values, "activeTabLocation", null);
			if (location != null && location < this.tab.getChildrenCount())
				this.tab.setActiveTab(this.tab.getTabAtLocation(location).getPortlet());
			List<Map<String, Object>> windows = (List<Map<String, Object>>) values.get("tabs");
			if (windows != null) {
				for (Map<String, Object> m : windows) {
					String state = CH.getOrThrow(Caster_String.INSTANCE, m, "state");
					location = CH.getOr(Caster_Integer.INSTANCE, m, "location", null);
					if (location >= this.tab.getChildrenCount())
						continue;
					Tab t = this.tab.getTabAtLocation(location);
					if (location != null) {
						if ("docked".equals(state)) {
							if (t.getPortlet() instanceof TabPlaceholderPortlet) {
								TabPlaceholderPortlet php = (TabPlaceholderPortlet) t.getPortlet();
								php.popin();
							}
						} else {
							if (isUndockAllowed) {
								boolean pop = CH.getOr(Caster_Boolean.INSTANCE, m, "pop", Boolean.FALSE);
								if (pop && t.getPortlet() instanceof TabPlaceholderPortlet) {
									final TabPlaceholderPortlet tpp = (TabPlaceholderPortlet) t.getPortlet();
									final Window tearoutWindow = tpp.getTearoutWindow();
									if (tearoutWindow.isPoppedOut()) {
										AmiWebPreferencesManager.importUserPrefs(tearoutWindow, m);
										continue;
									}
								}
								TabPlaceholderPortlet php = undockTab(t);
								Window window = php.getTearoutWindow();
								AmiWebPreferencesManager.importUserPrefs(window, m);
								if (window.isPoppedOut())
									php.setFullyDockOnClose(true);
							}
						}
					}
				}
			}
		} else {
			List<Map<String, Object>> windows = (List<Map<String, Object>>) values.get("tabs");
			if (windows != null) {
				int actualPos = 0;
				for (Map<String, Object> m : windows) {
					String state = CH.getOrThrow(Caster_String.INSTANCE, m, "state");
					String id = CH.getOrThrow(Caster_String.INSTANCE, m, "id");
					AmiWebTabEntry at = this.getTabById(id);
					if (at == null)
						continue;
					if (at.getHidden(true))
						continue;
					Tab t = at.getTab();
					int loc = actualPos++;
					tab.moveTabDontMoveHidden(t, loc);
					if ("docked".equals(state)) {
						if (t.getPortlet() instanceof TabPlaceholderPortlet) {
							TabPlaceholderPortlet php = (TabPlaceholderPortlet) t.getPortlet();
							php.popin();
						}
						t.setHidden(false);
					} else if ("hidden".equals(state)) {
						t.setHidden(true);
					} else {
						if (isUndockAllowed) {
							boolean pop = CH.getOr(Caster_Boolean.INSTANCE, m, "pop", Boolean.FALSE);
							if (pop && t.getPortlet() instanceof TabPlaceholderPortlet) {
								final TabPlaceholderPortlet tpp = (TabPlaceholderPortlet) t.getPortlet();
								final Window tearoutWindow = tpp.getTearoutWindow();
								if (tearoutWindow.isPoppedOut()) {
									AmiWebPreferencesManager.importUserPrefs(tearoutWindow, m);
									continue;
								}
							}
							TabPlaceholderPortlet php = undockTab(t);
							Window window = php.getTearoutWindow();
							AmiWebPreferencesManager.importUserPrefs(window, m);
							if (window.isPoppedOut())
								php.setFullyDockOnClose(true);
						}
					}
				}
			}
			String id = CH.getOr(Caster_String.INSTANCE, values, "activeTabId", null);
			if (id != null) {
				AmiWebTabEntry t = this.getTabById(id);
				if (t != null)
					this.tab.setActiveTab(t.getTab().getPortlet());
			}
		}

		super.applyUserPref(values);

	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		this.tab.drainJavascript();
	}
	@Override
	public Map<String, Object> getUserPref() {
		Map<String, Object> r = super.getUserPref();
		AmiWebTabEntry selectedTab = this.getSelectedTab();
		if (selectedTab != null) {
			r.put("activeTabId", selectedTab.getId());
		} else
			r.put("activeTabId", "");//Maybe not ideal, but his lets us know we are not to go into backwards compatibility mode
		List<Map<String, Object>> tabs = new ArrayList<Map<String, Object>>();
		List<AmiWebTabEntry> values = CH.l(this.amiTabs.values());
		Collections.sort(values, AmiWebTabEntry.COMPARATOR_LOCATION_OVERRIDE);
		for (AmiWebTabEntry amitab : values) {
			if (amitab.getHidden(true))
				continue;
			Tab t = tab.getTabById(amitab.getTabId());
			LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
			m.put("id", amitab.getId());
			if (t.getPortlet() instanceof TabPlaceholderPortlet) {
				TabPlaceholderPortlet php = (TabPlaceholderPortlet) t.getPortlet();
				Window win = php.getTearoutWindow();
				AmiWebPreferencesManager.exportWindowUserPref(win, m);
			} else
				m.put("state", "docked");
			tabs.add(m);
		}
		r.put("tabs", tabs);
		return r;
	}
	@Override
	public void onTabSelected(TabPortlet tabPortlet, Tab tab) {
	}

	@Override
	public void onTabRemoved(TabPortlet tabPortlet, Tab tab) {
		AmiWebTabEntry tabEntry = this.amiTabs.get(tab.getTabId());
		tabEntry.removeFromDomManager();
		this.amiTabs.remove(tabEntry.getTabId());
		this.tabsById.remove(tabEntry.getId());
		if (isInitDone())
			redoDmReferences();
	}
	@Override
	public void onTabAdded(TabPortlet tabPortlet, Tab tab) {
		if (!isInitDone())
			return;
		AmiWebTabEntry tab2 = new AmiWebTabEntry(this, tab);
		this.amiTabs.putOrThrow(tab2.getTabId(), tab2);
		CH.putOrThrow(this.tabsById, tab2.getId(), tab2);
		tab2.updateAri();
		tab2.addToDomManager();
		tab2.getNameFormula().setFormula(SH.quoteToJavaConst('"', tab.getTitle()), false);
		redoDmReferences();
	}
	@Override
	public void onClosed() {
		this.tab.removeTabListener(this);
		super.onClosed();
	}
	@Override
	public void onTabMoved(TabPortlet tabPortlet, int origPosition, Tab tab) {
		if (getService().getDesktop().getInEditMode())
			getService().getDesktop().flagUpdateWindowLinks();
		this.onTabLocationChanged(true);
	}
	public void redoDmReferences() {
		Set<Tuple2<String, String>> oldDmTables = new HashSet<Tuple2<String, String>>();
		Set<Tuple2<String, String>> nuwDmTables = new HashSet<Tuple2<String, String>>();
		Set<String> oldDms = new HashSet<String>();
		Set<String> nuwDms = new HashSet<String>();

		for (Tuple3<String, String, Map<Integer, AmiWebTabEntry>> i : dmAliasDotNames2table2tid2tab.keysMulti())
			oldDmTables.add(new Tuple2<String, String>(i.getA(), i.getB()));
		oldDms.addAll(dmAliasDotNames2table2tid2tab.keySet());

		this.dmAliasDotNames2table2tid2tab.clear();

		for (AmiWebTabEntry i : this.amiTabs.values())
			if (i.getDmAliasDotName() != null)
				dmAliasDotNames2table2tid2tab.putMulti(i.getAliasDotName(), i.getDmTableName(), i.getTabId(), i);
		nuwDms.addAll(dmAliasDotNames2table2tid2tab.keySet());

		for (Tuple3<String, String, Map<Integer, AmiWebTabEntry>> i : dmAliasDotNames2table2tid2tab.keysMulti())
			nuwDmTables.add(new Tuple2<String, String>(i.getA(), i.getB()));
		AmiWebDmManager dmManager = this.getService().getDmManager();
		for (Tuple2<String, String> i : CH.comm(oldDmTables, nuwDmTables, true, false, false))
			dmManager.onPanelDmDependencyChanged(this, i.getA(), i.getB(), false);
		for (Tuple2<String, String> i : CH.comm(oldDmTables, nuwDmTables, false, true, false))
			dmManager.onPanelDmDependencyChanged(this, i.getA(), i.getB(), true);
		for (String i : CH.comm(oldDms, nuwDms, true, false, false)) {
			AmiWebDm dm = dmManager.getDmByAliasDotName(i);
			if (dm != null)
				dm.removeDmListener(this);
		}
		for (String i : CH.comm(oldDms, nuwDms, false, true, false)) {
			AmiWebDm dm = dmManager.getDmByAliasDotName(i);
			if (dm != null)
				dm.addDmListener(this);
		}
		for (AmiWebTabEntry i : this.amiTabs.values())
			updateTab(i);
	}

	private MapInMapInMap<String, String, Integer, AmiWebTabEntry> dmAliasDotNames2table2tid2tab = new MapInMapInMap<String, String, Integer, AmiWebTabEntry>();

	public int getTabsCount() {
		return getInnerContainer().getTabsCount();
	}
	public AmiWebTabEntry getTabAt(int i) {
		Tab t = getInnerContainer().getTabAtLocation(i);
		return this.amiTabs.get(t.getTabId());
	}
	public AmiWebTabEntry getTabById(String i) {
		return this.tabsById.get(i);
	}

	public AmiWebTabEntry getTabFor(Portlet targetObject) {
		for (Tab i : this.getInnerContainer().getTabs()) {
			Portlet p = i.getPortlet();
			if (p == targetObject || (p instanceof TabPlaceholderPortlet && ((TabPlaceholderPortlet) i.getPortlet()).getTearoutPortlet() == targetObject))
				return this.amiTabs.get(i.getTabId());
		}
		return null;
	}
	public AmiWebTabEntry getTabFor(Tab r) {
		return r == null ? null : this.amiTabs.get(r.getTabId());
	}
	public AmiWebInnerTabPortlet getTabPortlet() {
		return this.tab;
	}

	@Override
	public String getAmiScriptClassName() {
		return "TabsPanel";
	}
	@Override
	protected void initJs() {
		super.initJs();
	}
	@Override
	public Set<String> getUsedDmAliasDotNames() {
		return this.dmAliasDotNames2table2tid2tab.keySet();
	}
	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		MapInMap<String, Integer, AmiWebTabEntry> m = this.dmAliasDotNames2table2tid2tab.remove(oldAliasDotName);
		if (m != null) {
			this.dmAliasDotNames2table2tid2tab.put(dm.getAmiLayoutFullAliasDotId(), m);
			for (AmiWebTabEntry e : m.valuesMulti())
				e.onDmNameChanged(dm.getAmiLayoutFullAliasDotId());
		}
	}
	@Override
	public Collection<AmiWebAliasPortlet> getAmiChildren() {
		List<Portlet> t = CH.l(this.getInnerContainer().getChildren().values());
		for (int i = 0; i < t.size(); i++)
			if (t.get(i) instanceof TabPlaceholderPortlet)
				t.set(i, ((TabPlaceholderPortlet) t.get(i)).getTearoutPortlet());
		return (List) t;
	}

	public void onEditModeChanged(boolean inEditMode) {
		TabPortlet i = getInnerContainer();
		int titleWidth = getManager().getPortletMetrics().getWidth(getAmiLayoutFullAliasDotId(), getCssStyle(), 13);
		TabPortletStyle style = i.getTabPortletStyle();
		if (!inEditMode)
			style.setInitialPadding(0);
		else
			style.setInitialPadding(style.isVertical ? 25 : (titleWidth + 25 + 16 + 2));
		style.setMenuArrowColor(inEditMode ? "#00DD00" : null);
		if (inEditMode)
			style.setMenuArrowSize(5);
		else {
			if (this.getCustomContextMenu().getRootMenu().hasChildren() || !this.hideExportSS || this.isUndockAllowed
					|| !this.getInnerContainer().getTabPortletStyle().getHideArrangeTabs())
				style.setMenuArrowSize(5);
			else
				style.setMenuArrowSize(0);
		}
		style.setShowTabsOverride(inEditMode);
		for (Tab t : i.getTabs())
			t.setAllowTitleEdit(inEditMode);
	}

	@Override
	public void updateAri() {
		super.updateAri();
		int tabsCount = this.getTabsCount();
		for (int i = 0; i < tabsCount; i++) {
			this.getTabAt(i).updateAri();
		}
	}
	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		List<AmiWebDomObject> children = super.getChildDomObjects();
		int tabsCount = this.getTabsCount();
		for (int i = 0; i < tabsCount; i++) {
			children.add(this.getTabAt(i));
		}
		return children;

	}
	public String getNextId(String tabId) {
		return SH.getNextId(tabId, this.tabsById.keySet());
	}
	public void onIdChanged(AmiWebTabEntry amiWebTabEntry, String oldId, String tabId) {
		this.tabsById.remove(oldId);
		CH.putOrThrow(this.tabsById, tabId, amiWebTabEntry);
	}
	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebTabSettingsPortlet(generateConfig(), this);
	}
	public AmiWebTabEntry getSelectedTab() {
		Tab t = getInnerContainer().getSelectedTab();
		return t == null ? null : getTabFor(t);
	}
	public void setSelectedTab(AmiWebTabEntry st) {
		this.tab.setActiveTab(st == null ? null : st.getTab().getPortlet());
	}
	public void onTabLocationChanged(boolean current) {
		for (AmiWebTabEntry tab : this.amiTabs.values()) {
			tab.updateLocationFromTab(current);
		}
	}
	@Override
	public void onTabClicked(TabPortlet tabPortlet, Tab curTab, Tab prevTab, boolean onArrow) {
		this.callbacks.execute("onClick", onArrow, getTabFor(curTab), getTabFor(prevTab));
	}

}
