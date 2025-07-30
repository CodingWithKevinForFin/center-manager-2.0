package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletMenuManager;
import com.f1.suite.web.portal.style.PortletStyleManager_Menu;
import com.f1.utils.BundledTextFormatter;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;

public class BasicPortletMenuManager implements PortletMenuManager {

	private PortletManager manager;
	private int nextId = 1;
	private IntKeyMap<WebMenuLink> ids2ActionsSink = new IntKeyMap<WebMenuLink>();
	private WebMenuItem activeMenu;
	private PortletStyleManager_Menu menuStyle;

	public BasicPortletMenuManager(PortletManager manager) {
		this.manager = manager;
		setMenuStyle(manager.getStyleManager().getMenuStyle());
	}

	public void setMenuStyle(PortletStyleManager_Menu styleManager) {
		this.menuStyle = styleManager;
	}

	public PortletStyleManager_Menu getMenuStyle() {
		return this.menuStyle;
	}

	@Override
	public Map<String, Object> setActiveMenuAndGenerateJson(WebMenuItem menu) {
		resetIds();
		Map<String, Object> r = menuToJson(menu, this.menuStyle);
		r.put("curseqnum", this.manager.getCurrentSeqNum());
		this.activeMenu = menu;
		return r;
	}
	@Override
	public WebMenuLink fireLinkForId(String id) {
		int i = SH.parseIntSafe(id, true, false);
		WebMenuLink r = this.ids2ActionsSink.get(i);
		if (r == null)
			return null;
		if (r.getAutoclose())
			resetIds();
		for (WebMenuItem link = r; link != null; link = link.getParent())
			if (link.getListener() != null)
				if (link.getListener().onMenuItem(r))
					return null;
		return r;
	}
	@Override
	public String getActionForId(String id) {
		int i = SH.parseIntSafe(id, true, false);
		WebMenuLink r = this.ids2ActionsSink.get(i);
		if (r == null) {
			this.manager.getSecurityModel().raiseSecurityViolation("Bad menu id: " + id + ", existing are: " + this.ids2ActionsSink.keys());
			return null;
		}
		if (r.getAutoclose())
			resetIds();
		return r.getAction();
	}

	@Override
	public void resetIds() {
		this.ids2ActionsSink.clear();
		this.activeMenu = null;
		if (nextId > 10000)
			nextId = 1;
	}

	@Override
	public WebMenuItem getActiveMenu() {
		return this.activeMenu;
	}

	public void setEndUserStyle(Map<String, Object> styleSink, PortletStyleManager_Menu style) {
		if (style == null)
			return;
		CH.putNoNull(styleSink, "bgCl", style.getBgColor());
		CH.putNoNull(styleSink, "fontCl", style.getFontColor());
		CH.putNoNull(styleSink, "divCl", style.getDividerColor());
		CH.putNoNull(styleSink, "disFontCl", style.getDisabledFontColor());
		CH.putNoNull(styleSink, "disBgColor", style.getDisabledBgColor());
		CH.putNoNull(styleSink, "borderTpLfCl", style.getBorderTopLeftColor());
		CH.putNoNull(styleSink, "borderBtmRtCl", style.getBorderBottomRightColor());
		CH.putNoNull(styleSink, "hoverBgCl", style.getHoverBgColor());
		CH.putNoNull(styleSink, "hoverFontCl", style.getHoverFontColor());
	}

	private Map<String, Object> menuToJson(WebMenuItem menu, PortletStyleManager_Menu style) {
		if (menu == null)
			return null;
		if (menu.getStyle() != null)
			style = menu.getStyle();

		BundledTextFormatter formatter = manager.getTextFormatter();
		Map<String, Object> r = CH.m("text", formatter.format(menu.getText()), "enabled", menu.getEnabled());
		if (SH.is(menu.getHtmlIdSelector()))
			r.put("hids", menu.getHtmlIdSelector());
		if (menu instanceof WebMenu) {
			r.put("type", "menu");
			WebMenu webMenu = (WebMenu) menu;
			setEndUserStyle(r, style);
			if (CH.isntEmpty(webMenu.getChildren())) {
				List<Object> l = new ArrayList<Object>(webMenu.getChildren().size());
				for (WebMenuItem i : webMenu.getChildren()) {
					Map<String, Object> t = menuToJson(i, style);
					if (t != null)
						l.add(t);
				}
				r.put("children", l);
			}
		} else if (menu instanceof WebMenuLink) {
			r.put("type", "action");
			WebMenuLink webLink = (WebMenuLink) menu;
			setEndUserStyle(r, style);
			int i = nextId++;
			ids2ActionsSink.put(i, webLink);
			r.put("action", SH.toString(i));
			if (SH.is(webLink.getOnClickJavascript()))
				r.put("onclickJs", webLink.getOnClickJavascript());
			if (!webLink.getAutoclose())
				r.put("autoclose", false);
			if (SH.is(webLink.getKeystroke()))
				r.put("keystroke", SH.toString(webLink.getKeystroke()));
		} else if (menu instanceof BasicWebMenuDivider) {
			r.put("type", "divider");
			BasicWebMenuDivider div = (BasicWebMenuDivider) menu;
			setEndUserStyle(r, style);
			r.put("style", div.getCssStyle());
		}
		if (SH.is(menu.getCssStyle()))
			r.put("style", menu.getCssStyle());
		if (SH.is(menu.getBackgroundImage()))
			r.put("backgroundImage", menu.getBackgroundImage());
		return r;
	}

}
