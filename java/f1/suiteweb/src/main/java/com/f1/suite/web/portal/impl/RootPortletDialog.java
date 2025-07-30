package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.style.PortletStyleManager_Dialog;

public class RootPortletDialog {
	public Portlet portlet;
	public int left;
	public int top;
	private int zindex;
	public boolean visible;
	private boolean hasCloseButton = true;
	private boolean escapeKeyCloses = true;
	private List<RootPortletDialogListener> listeners = new ArrayList<RootPortletDialogListener>();
	private RootPortlet root;
	private boolean shadeOutside = true;
	private boolean closeOnClickOutside = false;
	private String stylePrefix;
	private int headerSize;
	private int borderSize;
	private Map<String, Object> options = new HashMap<String, Object>();
	final private boolean modal;

	public RootPortletDialog(RootPortlet root, Portlet portlet, int zindex, boolean isModal) {
		this.modal = isModal;
		this.portlet = portlet;
		this.zindex = zindex;
		this.visible = true;
		this.root = root;

	}

	public boolean getHasCloseButton() {
		return hasCloseButton;
	}
	public boolean getEscapeKeyCloses() {
		return escapeKeyCloses;
	}

	public void setZindex(int i) {
		zindex = i;
	}

	public int getZindex() {
		return zindex;
	}

	public Portlet getPortlet() {
		return portlet;
	}

	public void setPosition(int left, int top) {
		this.left = Math.max(left, borderSize);
		this.top = Math.max(top, headerSize);
		for (RootPortletDialogListener i : this.listeners)
			i.onDialogMoved(this);
	}

	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public void setHasCloseButton(boolean hasCloseButton) {
		this.hasCloseButton = hasCloseButton;
	}
	public void setEscapeKeyCloses(boolean escapeKeyCloases) {
		this.escapeKeyCloses = escapeKeyCloases;
	}

	public boolean getIsModal() {
		return this.modal;
	}

	public void addListener(RootPortletDialogListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(RootPortletDialogListener listener) {
		this.listeners.remove(listener);
	}

	protected List<RootPortletDialogListener> getListeners() {
		return this.listeners;
	}

	public void close() {
		this.root.removeChild(this.portlet.getPortletId());
	}
	public void fireOnClosed() {
		for (RootPortletDialogListener i : this.listeners)
			i.onDialogClosed(this);
	}
	public void fireOnUserClosed() {
		for (RootPortletDialogListener i : this.listeners)
			i.onUserCloseDialog(this);
	}

	public void fireOnClickedOutside() {
		for (RootPortletDialogListener i : this.listeners)
			i.onDialogClickoutside(this);
	}

	public void fireOnVisible(boolean b) {
		for (RootPortletDialogListener i : this.listeners)
			i.onDialogVisible(this, b);
	}

	public void setShadeOutside(boolean b) {
		this.shadeOutside = b;
	}
	public boolean getShadeOutside() {
		return this.shadeOutside;
	}

	public boolean isCloseOnClickOutside() {
		return closeOnClickOutside;
	}

	public void setCloseOnClickOutside(boolean closeOnClickOutside) {
		this.closeOnClickOutside = closeOnClickOutside;
	}

	public String getStylePrefix() {
		return this.stylePrefix;
	}

	public void setCssClassPrefix(String stylePrefix) {
		this.stylePrefix = stylePrefix;
	}

	public int getHeaderSize() {
		return this.headerSize;
	}

	public int getBorderSize() {
		return this.borderSize;
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}

	public void setBorderSize(int borderSize) {
		this.borderSize = borderSize;
	}
	public Map<String, Object> getOptions() {
		return options;
	}
	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}
	public int getOuterLeft() {
		return left - this.borderSize;
	}

	public int getOuterTop() {
		return top - this.borderSize - this.headerSize;
	}

	public int getOuterBottom() {
		return top + getHeight() + this.borderSize;
	}

	public int getOuterRight() {
		return left + getWidth() + this.borderSize;
	}

	public int getOuterHeight() {
		return getHeight() + borderSize + borderSize + headerSize;
	}
	public int getOuterWidth() {
		return getWidth() + borderSize + borderSize;
	}

	public int getHeight() {
		return portlet.getHeight();
	}
	public int getWidth() {
		return portlet.getWidth();
	}

	public void setStyle(PortletStyleManager_Dialog styleManager) {
		setCssClassPrefix(styleManager.getDefaultDialogCssClassPrefix());
		if (styleManager.isUseDefaultStyling()) {
			setBorderSize(styleManager.getDefaultDialogBorderSize());
			setHeaderSize(styleManager.getDefaultDialogHeaderSize());
		} else {
			setBorderSize(styleManager.getDialogBorderSize());
			setHeaderSize(styleManager.getDialogHeaderSize());
			styleManager.setHasCloseButton(hasCloseButton);
			styleManager.buildCustomCssStyle(); // update header
		}
		getOptions().putAll(styleManager.getStyleOptions());
	}

	public void setRoot(RootPortlet root) {
		this.root = root;
	}

}
