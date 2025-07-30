/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.menu.impl;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.WebMenuLinkListener;
import com.f1.suite.web.portal.style.PortletStyleManager_Menu;

public abstract class AbstractWebMenuItem implements WebMenuItem {

	private WebMenuLinkListener listener;
	private WebMenu parent;
	private String text;
	private String htmlIdSelector;
	private boolean enabled;
	private int priority;
	private String backgroundImage;

	public AbstractWebMenuItem(String text, boolean enabled) {
		this(text, enabled, -1);
	}

	public AbstractWebMenuItem(String text, boolean enabled, int priority) {
		this.text = text;
		this.enabled = enabled;
		this.priority = priority;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean getEnabled() {
		return enabled;
	}

	private String cssStyle;
	private PortletStyleManager_Menu style;

	public AbstractWebMenuItem setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
		return this;
	}

	@Override
	public String getCssStyle() {
		return this.cssStyle;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	protected void setPriority(int priority) {
		this.priority = priority;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getBackgroundImage() {
		return this.backgroundImage;
	}

	public AbstractWebMenuItem setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
		return this;
	}

	@Override
	public PortletStyleManager_Menu getStyle() {
		return this.style;
	}

	@Override
	public void setStyle(PortletStyleManager_Menu style) {
		this.style = style;
	}
	@Override
	public WebMenu getParent() {
		return this.parent;
	}
	@Override
	public WebMenuLinkListener getListener() {
		return this.listener;
	}

	@Override
	public void setParent(WebMenu parent) {
		if (parent == this.parent)
			return;
		if (parent != null && this.parent != null)
			throw new IllegalStateException("Parent already set");
		this.parent = parent;
	}

	@Override
	public void setListener(WebMenuLinkListener listener) {
		if (listener == this.listener)
			return;
		if (this.listener != null)
			throw new IllegalStateException("Listener already set");
		this.listener = listener;
	}

	@Override
	public String getHtmlIdSelector() {
		return htmlIdSelector;
	}

	@Override
	public void setHtmlIdSelector(String htmlIdSelector) {
		this.htmlIdSelector = htmlIdSelector;
	}
}
