package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;

public class DropDownMenuPortlet extends AbstractPortlet {

	private List<WebDropDownMenuListener> menuListeners = new ArrayList<WebDropDownMenuListener>();

	public static final String OPTION_ALIGN = "align";
	public static final String OPTION_GO_UP = "goUp";

	private String cssStyle = "";
	private boolean styleChanged = false;
	private IndexedList<String, BasicWebMenuLink> menus = new BasicIndexedList<String, BasicWebMenuLink>();
	private boolean optionsChanged;

	public DropDownMenuPortlet(PortletConfig portletConfig) {
		super(portletConfig);
	}

	public void addMenu(String id, String title, int position) {
		if (menus.containsKey(id))
			throw new RuntimeException("duplicate menu id: " + id);
		menus.add(id, new BasicWebMenuLink(title, true, id), position);
		menusChanged = true;
		flagPendingAjax();
	}

	public BasicWebMenuLink findMenuByName(String title) {
		for (BasicWebMenuLink menu : menus.values())
			if (OH.eq(title, menu.getText()))
				return menu;
		return null;
	}
	public void addMenu(String id, String title) {
		addMenu(id, title, menus.getSize());
	}

	public void clearMenus() {
		if (this.menus.getSize() == 0)
			return;
		this.menus.clear();
		this.menusChanged = true;
		this.flagPendingAjax();
	}

	public boolean removeMenu(String id) {
		if (menus.removeNoThrow(id) != null) {
			menusChanged = true;
			flagPendingAjax();
			return true;
		}
		return false;
	}

	public static final PortletSchema<DropDownMenuPortlet> SCHEMA = new BasicPortletSchema<DropDownMenuPortlet>("Dropdown", "DropDownMenuPortlet", DropDownMenuPortlet.class, true,
			true);

	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("showmenu".equals(callback)) {
			String id = CH.getOrThrow(Caster_String.INSTANCE, attributes, "id");
			showMenu(id);
		} else if ("menuitem".equals(callback)) {
			String action = CH.getOrThrow(Caster_String.INSTANCE, attributes, "id");
			fireOnMenu(getManager().getMenuManager().fireLinkForId(action));
		} else
			super.handleCallback(callback, attributes);
	}
	public void showMenu(String id) {
		if (contextMenuFactory != null) {
			WebMenu menu = contextMenuFactory.createMenu(this, id, this.menus.get(id));
			if (menu != null) {
				if (getVisible()) {
					JsFunction jsf = callJsFunction("showMenu");
					jsf.addParamQuoted(id);
					jsf.addParamJson(PortletHelper.menuToJson(getManager(), menu));
					jsf.end();
				}
			}
		}
	}
	private void buildOptionsJs() {
		JsFunction func = callJsFunction("setOptions");
		JsonBuilder optionsJson = func.startJson();
		optionsJson.addQuoted(options);
		optionsJson.close();
		func.end();
	}

	private Map<String, Object> options = new HashMap<String, Object>();

	private WebDropDownMenuFactory contextMenuFactory;

	private boolean menusChanged = true;;

	public Object addOption(String key, Object value) {
		Object old = options.put(key, value);
		if (OH.ne(old, value)) {
			flagPendingAjax();
			this.optionsChanged = true;
		}
		return old;
	}
	public Object removeOption(String key) {
		flagPendingAjax();
		this.optionsChanged = true;
		return options.remove(key);
	}
	public void clearOptions() {
		flagPendingAjax();
		this.options.clear();
		this.optionsChanged = true;
	}
	public Object getOption(String option) {
		flagPendingAjax();
		return options.get(option);
	}
	public Set<String> getOptions() {
		flagPendingAjax();
		return options.keySet();
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible()) {
			if (optionsChanged) {
				optionsChanged = false;
				buildOptionsJs();
			}
			if (menusChanged) {
				menusChanged = false;
				buildMenusJs();
			}
			if (styleChanged) {
				styleChanged = false;
				updateCssStyleJs();

			}

		}
	}
	@Override
	public void initJs() {
		super.initJs();
		buildMenusJs();
		buildOptionsJs();
		updateCssStyleJs();
	}

	private void updateCssStyleJs() {
		JsFunction jsfunction = callJsFunction("setCssStyle").addParamQuoted(this.cssStyle).end();
	}
	private void buildMenusJs() {
		JsFunction jsfunction = callJsFunction("setMenus");
		JsonBuilder json = jsfunction.startJson();
		json.startList();
		for (BasicWebMenuLink i : this.menus.values()) {
			json.startMap();
			if (SH.is(i.getOnClickJavascript()))
				json.addKeyValueQuoted("onclickJs", i.getOnClickJavascript());
			json.addKeyValueQuoted("id", i.getAction());
			json.addKeyValueQuoted("text", i.getText());
			json.endMap();
		}
		json.endList();
		jsfunction.end();

		json.end();
	}
	public WebDropDownMenuFactory getContextMenuFactory() {
		return contextMenuFactory;
	}

	public void setContextMenuFactory(WebDropDownMenuFactory contextMenuFactory) {
		this.contextMenuFactory = contextMenuFactory;
	}

	public void addMenuContextListener(WebDropDownMenuListener listener) {
		menuListeners.add(listener);
	}

	public boolean removeMenuContextListener(WebDropDownMenuListener listener) {
		return menuListeners.remove(listener);
	}

	private void fireOnMenu(WebMenuLink webMenuLink) {
		if (webMenuLink != null)
			for (WebDropDownMenuListener ml : menuListeners)
				ml.onContextMenu(this, webMenuLink.getAction());
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (processUserKeyEventForMenuBar(keyEvent))
			return true;
		return super.onUserKeyEvent(keyEvent);
	}

	public boolean processUserKeyEventForMenuBar(KeyEvent keyEvent) {
		if (keyEvent.isJustAltKey()) {
			for (BasicWebMenuLink s : this.menus.values()) {
				if (SH.startsWithIgnoreCase(s.getText(), keyEvent.getKey(), 0)) {
					showMenu(s.getAction());
					return true;
				}
			}
		}
		return false;
	}

	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		if (SH.equals(this.cssStyle, cssStyle))
			return;
		this.cssStyle = cssStyle;
		this.flagPendingStyleChanged();
	}
	public void flagPendingStyleChanged() {
		this.styleChanged = true;
		this.flagPendingAjax();
	}
}
