package com.f1.ami.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.menu.AmiWebCustomContextMenu;
import com.f1.ami.web.menu.AmiWebCustomContextMenuListener;
import com.f1.ami.web.menu.AmiWebCustomContextMenuManager;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebInnerDesktopPortlet extends DesktopPortlet implements AmiWebAliasPortlet, AmiWebCustomContextMenuListener {

	public static final String DESKTOP = "@DESKTOP";
	private AmiWebService service;
	private String panelId = DESKTOP;
	private String fullAlias = "";
	private String windowColor;
	private String windowFontColor;
	private String windowColorTopLeft;
	private String windowColorBottomRight;
	private String windowButtonBg;
	private String windowButtonBorder;
	private String windowButtonShadow;
	private String windowButtonIconColor;

	private Integer titleFontSize;
	private String titleFontFamily;
	private Integer titlePadding;
	private Integer borderSize;
	private AmiWebCustomContextMenuManager menuManager;

	public AmiWebInnerDesktopPortlet(PortletConfig manager) {
		super(manager);
		setIsCustomizable(false);
		addOption(DesktopPortlet.OPTION_DOCLET_LOCATION, DesktopPortlet.VALUE_NONE);
		addOption(DesktopPortlet.OPTION_STYLE_CLASS_PREFIX, "ami_desktop");
		this.service = AmiWebUtils.getService(getManager());
		this.service.getDesktop().setDesktop(this);
		applyWindowColor();
		applyWindowColorTopLeft();
		applyWindowColorBottomRight();
		applyWindowButtonBg();
		applyWindowButtonBorder();
		applyWindowButtonShadow();
		applyWindowButtonIconColor();
		applyWindowFontColor();
		applyWindowBorder();
		applyWindowHeader();
		applyTitleFontStyle();
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebInnerDesktopPortlet> implements AmiWebPortletContainerBuilder<AmiWebInnerDesktopPortlet> {

		public static final String ID = "amidesktop";

		public Builder() {
			super(AmiWebInnerDesktopPortlet.class);
			setIcon("portlet_icon_desktop");
		}

		@Override
		public AmiWebInnerDesktopPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebInnerDesktopPortlet r = new AmiWebInnerDesktopPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Desktop";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

		@Override
		public void extractChildPorletIds(Map<String, Object> config, Map<String, Map> sink) {
			List<Map> windows = CH.getOrThrow(List.class, config, "windows");
			for (Map window : windows)
				sink.put(CH.getOrThrow(String.class, window, "portlet"), window);
		}

		@Override
		public boolean removePortletId(Map<String, Object> portletConfig, String amiPanelId) {
			List<Map> windows = CH.getOrThrow(List.class, portletConfig, "windows");
			for (int i = 0; i < windows.size(); i++)
				if (OH.eq(amiPanelId, CH.getOrThrow(String.class, windows.get(i), "portlet"))) {
					windows.remove(i);
					return true;
				}
			return false;
		}

		@Override
		public boolean replacePortletId(Map<String, Object> portletConfig, String oldPanelId, String nuwPanelId) {
			List<Map> windows = CH.getOrThrow(List.class, portletConfig, "windows");
			for (int i = 0; i < windows.size(); i++)
				if (OH.eq(oldPanelId, CH.getOrThrow(String.class, windows.get(i), "portlet"))) {
					windows.get(i).put("portlet", nuwPanelId);
					return true;
				}
			return false;
		}

	}

	@Override
	protected Portlet configSaveIdToPortlet(Map<String, String> mapping, String amiPanelId) {
		return service.getPortletByAliasDotPanelId(AmiWebUtils.getFullAlias(getAmiLayoutFullAlias(), amiPanelId));
	}

	@Override
	protected String portletToConfigSaveId(Portlet portlet) {
		if (portlet instanceof AmiWebAliasPortlet) {
			AmiWebAliasPortlet amiWebAliasPortlet = (AmiWebAliasPortlet) portlet;
			if (!this.service.getDesktop().getIsDoingExportTransient())
				amiWebAliasPortlet = amiWebAliasPortlet.getNonTransientPanel();
			if (amiWebAliasPortlet != null)
				return AmiWebUtils.getRelativeAlias(getAmiLayoutFullAlias(), amiWebAliasPortlet.getAmiLayoutFullAliasDotId());
		}
		return null;
	}

	@Override
	public String getAmiLayoutFullAlias() {
		return this.fullAlias;
	}
	@Override
	public String getAmiLayoutFullAliasDotId() {
		return AmiWebUtils.getFullAlias(this.getAmiLayoutFullAlias(), this.getAmiPanelId());
	}
	@Override
	public void onAmiInitDone() {
	}

	public void onLayoutInit() {
		this.menuManager = new AmiWebCustomContextMenuManager(this.service.getLayoutFilesManager().getLayout());
	}
	@Override
	public String getAmiPanelId() {
		return this.panelId;
	};

	public void onInitDone() {
		this.menuManager.onInitDone();
		this.menuManager.getRootMenu().addListener(this);
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		for (Window i : super.getWindows()) {
			if (!i.hasDefaultLocation()) {
				i.setDefaultLocationToCurrent();
				i.setDefaultStateToCurrent();
				i.setDefaultZIndexToCurrent();
			}
			if (i.isHidden(true))
				i.minimizeWindow();
		}
		this.service.getDesktop().getStylePeer().initStyle((Map<String, Object>) configuration.get("amiStyle"));
		this.panelId = (String) configuration.get("amiPanelId");
		// Build custom context menu
		this.menuManager.init(configuration.get("customMenu"));
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		AmiWebUtils.putSkipEmpty(r, "amiStyle", this.service.getDesktop().getStylePeer().getStyleConfiguration());
		r.remove("active");
		r.put("amiPanelId", getAmiPanelId());
		AmiWebUtils.putSkipEmpty(r, "customMenu", this.menuManager.getConfiguration());
		return r;
	}

	@Override
	protected boolean shouldSaveWindowConfiguration(Window window) {
		Portlet p = window.getPortlet();
		if (p instanceof AmiWebAliasPortlet)
			if (((AmiWebAliasPortlet) p).getNonTransientPanel() == null)
				return false;
		return super.shouldSaveWindowConfiguration(window);
	}

	@Override
	public AmiWebAbstractContainerPortlet getAmiParent() {
		return null;
	};
	@Override
	public Collection<AmiWebAliasPortlet> getAmiChildren() {
		return CH.getAllImplementing(getChildren().values(), AmiWebAliasPortlet.class);
	}

	@Override
	public AmiWebService getService() {
		return this.service;
	}

	@Override
	public boolean setAdn(String adn) {
		OH.assertEq(adn, DESKTOP);
		return false;

	}
	@Override
	public void setAmiUserPrefId(String userPrefId) {
		service.getPreferencesManager().setUserPrefId(userPrefId);
	}
	@Override
	public String getAmiUserPrefId() {
		return service.getPreferencesManager().getUserPrefId();
	}
	@Override
	public void applyUserPref(Map<String, Object> values) {
		service.getPreferencesManager().applyUserPref(values);
	}
	@Override
	public Map<String, Object> getUserPref() {
		return this.service.getPreferencesManager().exportWindowsPreferences(new HashMap<String, Object>());
	}

	@Override
	public Map<String, Object> getDefaultPref() {
		Map<String, Object> r = new HashMap();
		return r;
	}
	@Override
	public void setDefaultPref(Map<String, Object> defaultPref) {
	}

	public Integer getTitleFontSize() {
		return titleFontSize;
	}

	public void setTitleFontSize(Integer titleFontSize) {
		if (OH.eq(this.titleFontSize, titleFontSize))
			return;
		this.titleFontSize = titleFontSize;
		applyTitleFontStyle();
	}

	public String getTitleFontFamily() {
		return titleFontFamily;
	}

	public void setTitleFontFamily(String titleFontFamily) {
		if (OH.eq(this.titleFontFamily, titleFontFamily))
			return;
		this.titleFontFamily = titleFontFamily;
		applyTitleFontStyle();
	}
	public Integer getTitlePadding() {
		return titlePadding;
	}

	public void setTitlePadding(Integer titlePadding) {
		if (OH.eq(this.titlePadding, titlePadding))
			return;
		this.titlePadding = titlePadding;
		applyTitleFontStyle();
	}

	public Integer getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(Integer borderSize) {
		if (OH.eq(this.borderSize, borderSize))
			return;
		this.borderSize = borderSize;
		applyWindowBorder();
	}

	public String getWindowFontColor() {
		return this.windowFontColor;
	}
	public void setWindowFontColor(String newColor) {
		if (OH.eq(this.windowFontColor, newColor))
			return;
		this.windowFontColor = newColor;
		applyWindowFontColor();
	}

	public String getWindowColor() {
		return this.windowColor;
	}
	public void setWindowColor(String newColor) {
		if (OH.eq(this.windowColor, newColor))
			return;
		this.windowColor = newColor;
		applyWindowColor();
	}

	public String getWindowButtonBg() {
		return this.windowButtonBg;
	}
	public void setWindowButtonBg(String nuwColor) {
		if (OH.eq(this.windowButtonBg, nuwColor))
			return;
		this.windowButtonBg = nuwColor;
		applyWindowButtonBg();
	}

	public String getWindowButtonBorder() {
		return this.windowButtonBorder;
	}
	public String getWindowButtonIconColor() {
		return this.windowButtonIconColor;
	}
	public void setWindowButtonBorder(String nuwColor) {
		if (OH.eq(this.windowButtonBorder, nuwColor))
			return;
		this.windowButtonBorder = nuwColor;
		applyWindowButtonBorder();
	}

	public String getWindowButtonShadowColor() {
		return this.windowButtonShadow;
	}
	public void setWindowButtonShadowColor(String color) {
		if (OH.eq(this.windowButtonShadow, color))
			return;
		this.windowButtonShadow = color;
		applyWindowButtonShadow();
	}

	public void setWindowButtonIconColor(String nuwColor) {
		if (OH.eq(this.windowButtonIconColor, nuwColor))
			return;
		this.windowButtonIconColor = nuwColor;
		applyWindowButtonIconColor();
	}
	public String getWindowColorTopLeft() {
		return windowColorTopLeft;
	}
	public void setWindowColorTopLeft(String windowColorTopLeft) {
		if (OH.eq(this.windowColorTopLeft, windowColorTopLeft))
			return;
		this.windowColorTopLeft = windowColorTopLeft;
		applyWindowColorTopLeft();
	}
	public String getWindowColorBottomRight() {
		return windowColorBottomRight;
	}
	public void setWindowColorBottomRight(String windowColorBottomRight) {
		if (OH.eq(this.windowColorBottomRight, windowColorBottomRight))
			return;
		this.windowColorBottomRight = windowColorBottomRight;
		applyWindowColorBottomRight();
	}

	private void applyWindowColorBottomRight() {
		addOption(DesktopPortlet.OPTION_COLOR_WINDOW_DOWN, OH.noNull(this.windowColorBottomRight, "#FFFFFF"));
	}

	private void applyWindowColorTopLeft() {
		addOption(DesktopPortlet.OPTION_COLOR_WINDOW_UP, OH.noNull(this.windowColorTopLeft, "#FFFFFF"));
	}

	private void applyWindowButtonIconColor() {
		addOption(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON_ICON, OH.noNull(this.windowButtonIconColor, "#72706D"));
	}

	private void applyWindowButtonShadow() {
		addOption(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON_DOWN, OH.noNull(this.windowButtonShadow, "#cccccc"));
	}

	public void getUsedColors(Set<String> sink) {
		AmiWebUtils.getColors(this.windowButtonBg, sink);
		AmiWebUtils.getColors(this.windowButtonBorder, sink);
		AmiWebUtils.getColors(this.windowColor, sink);
		AmiWebUtils.getColors(this.windowFontColor, sink);
	}
	private void applyTitleFontStyle() {
		int titleFontSize = OH.noNull(this.titleFontSize, 14);
		int titlePadding = OH.noNull(this.titlePadding, 1);
		String titleFontFamily = OH.noNull(this.titleFontFamily, "arial");
		addOption(OPTION_WINDOW_FONTSTYLE, "_fs=" + titleFontSize + "|_fm=" + titleFontFamily);
		addOption(OPTION_WINDOW_BUTTON_HEIGHT, MH.clip(titleFontSize - 2, 12, 16));
		int headerSize = titleFontSize + titlePadding * 2;
		for (Window i : super.getWindows())
			i.setHeaderSize(headerSize);
	}
	private void applyWindowBorder() {
		int t = OH.noNull(this.borderSize, 5);
		for (Window i : super.getWindows())
			i.setBorderSize(t);
	}
	private void applyWindowHeader() {
	}
	private void applyWindowFontColor() {
		addOption(DesktopPortlet.OPTION_COLOR_WINDOW_TEXT, OH.noNull(this.windowFontColor, "#004400"));
	}
	private void applyWindowColor() {
		addOption(DesktopPortlet.OPTION_COLOR_WINDOW, OH.noNull(this.windowColor, "#cccccc"));
	}
	private void applyWindowButtonBg() {
		addOption(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON, OH.noNull(this.windowButtonBg, "#cccccc"));
	}
	private void applyWindowButtonBorder() {
		addOption(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON_UP, OH.noNull(this.windowButtonBorder, "#cccccc"));
	}
	@Override
	public boolean isReadonlyLayout() {
		return this.service.getLayoutFilesManager().getLayoutByFullAlias(getAmiLayoutFullAlias()).isReadonly();
	}

	//	@Override
	//	public void recompileAmiscript() {
	//	}
	//
	@Override
	public AmiWebCustomContextMenuManager getCustomContextMenu() {
		return this.menuManager;
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		Object ret = this.getService().getDesktop().getCallbacks().execute("onKey", keyEvent);
		if (ret != null && "stop".equalsIgnoreCase(OH.toString(ret)))
			return false;
		return super.onUserKeyEvent(keyEvent);
	}
	@Override
	public boolean onUserMouseEvent(MouseEvent mouseEvent) {
		Object ret = this.getService().getDesktop().getCallbacks().execute("onMouse", mouseEvent);
		if (ret != null && "stop".equalsIgnoreCase(OH.toString(ret)))
			return false;
		return super.onUserMouseEvent(mouseEvent);
	}

	@Override
	public String getAri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAri() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAriType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDomLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return null;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getDomClassType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDomValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTransient() {
		return false;
	}

	@Override
	public void setTransient(boolean isTransient) {
		// TODO Auto-generated method stub

	}

	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToDomManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFromDomManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toDerivedString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AmiWebScriptManagerForLayout getScriptManager() {
		return service.getScriptManager(this.getAmiLayoutFullAlias());
	}

	@Override
	public AmiWebFormulas getFormulas() {
		return null;
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return EmptyCalcTypes.INSTANCE;
	}

	@Override
	public void onChildAdded(AmiWebCustomContextMenu me, AmiWebCustomContextMenu child) {
		if (me.getChildrenCount() == 1)
			this.service.getDesktop().updateDashboard();
	}

	@Override
	public void onChildRemoved(AmiWebCustomContextMenu me, AmiWebCustomContextMenu child) {
		if (me.getChildrenCount() == 0)
			this.service.getDesktop().updateDashboard();
	}

	@Override
	public AmiWebAliasPortlet getNonTransientPanel() {
		return this;
	}

	@Override
	public String getConfigMenuTitle() {
		return "Desktop";
	}

	@Override
	protected Window newWindow(String title, Portlet portlet, int zindex, int flags) {
		return new AmiWebWindow(this, title, portlet, zindex, flags);
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
