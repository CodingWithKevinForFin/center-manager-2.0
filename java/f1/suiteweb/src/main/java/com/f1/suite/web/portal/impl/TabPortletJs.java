package com.f1.suite.web.portal.impl;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.style.PortletStyleManager_Tab;
import com.f1.utils.OH;

public class TabPortletJs {

	private final TabPortlet portlet;
	private final PortletManager manager;
	private final TabPortletStyle portletStyle;
	private PortletStyleManager_Tab styleManager;
	final private JsFunction jsFunction;
	final private JsFunction lcvFunction;

	private int inLcv = 0;
	private boolean wroteLcv = false;
	private static String VARNAME = "t";

	public TabPortletJs(TabPortlet portlet) {
		this.portlet = portlet;
		this.manager = this.portlet.getManager();
		this.jsFunction = new JsFunction(portlet.getJsObjectName());
		this.portletStyle = this.portlet.getTabPortletStyle();
		this.styleManager = portletStyle.getStyleManager();
		this.lcvFunction = new JsFunction("t");
	}

	private JsFunction callJsFunction(String functionName) {
		return callJsFunction(manager.getPendingJs(), functionName);
	}

	private void ensureLcvWritten() {
		if (inLcv > 0) {
			if (!wroteLcv) {
				manager.getPendingJs().append("{var ").append(VARNAME).append('=').append(this.portlet.getJsObjectName()).append(";\n");
				wroteLcv = true;
			}
		}
	}
	private JsFunction callJsFunction(StringBuilder sink, String functionName) {
		if (inLcv > 0) {
			ensureLcvWritten();
			return lcvFunction.reset(sink, functionName);
		} else
			return jsFunction.reset(sink, functionName);
	}
	public void drainJavascriot() {

	}
	protected void callJsFunction_buildTabs() {
		initLcv();
		try {
			String bc = this.portletStyle.backgroundColor != null ? this.portletStyle.backgroundColor : null;
			String sc = this.portletStyle.selectColor != null ? this.portletStyle.selectColor : this.styleManager.getDefaultSelectedColor();
			String usc = this.portletStyle.unselectColor != null ? this.portletStyle.unselectColor : this.styleManager.getDefaultUnselectedColor();
			String stc = this.portletStyle.selectTextColor != null ? this.portletStyle.selectTextColor : this.styleManager.getDefaultSelectedTextColor();
			String ustc = this.portletStyle.unselectTextColor != null ? this.portletStyle.unselectTextColor : this.styleManager.getDefaultUnselectedTextColor();
			String sswc = this.portletStyle.getSelectShadow() != null ? this.portletStyle.getSelectShadow() : this.styleManager.getDefaultSelectedShadow();
			String usswc = this.portletStyle.getUnselectShadow() != null ? this.portletStyle.getUnselectShadow() : this.styleManager.getDefaultUnselectedShadow();
			String tbc = this.portletStyle.borderColor != null ? this.portletStyle.borderColor : this.styleManager.getDefaultBorderColor();
			String stbc = this.portletStyle.selBorderColor != null ? this.portletStyle.selBorderColor : this.styleManager.getDefaultSelBorderColor();
			int stbs = this.portletStyle.selBorderSize != null ? this.portletStyle.selBorderSize : this.styleManager.getDefaultSelBorderSize();
			boolean farAligned = this.portletStyle.isVertical ? !portletStyle.isOnBottom : portletStyle.isOnRight;
			boolean isBottom = this.portletStyle.isVertical ? portletStyle.isOnRight : portletStyle.isOnBottom;
			boolean isVertical = this.portletStyle.isVertical;
			callJsFunction("initTabs").addParam(portletStyle.isShowTabsOverride()).addParam(portletStyle.hasExtraButtonAlways || portletStyle.isCustomizable)
					.addParam(portletStyle.hasMenuAlways || portletStyle.isCustomizable).addParamQuoted(this.portletStyle.addButtonText).addParamQuoted(bc).addParamQuoted(sc)
					.addParamQuoted(usc).addParamQuoted(stc).addParamQuoted(ustc).addParamQuoted(sswc).addParamQuoted(usswc).addParam(this.portletStyle.isHidden)
					.addParam(this.portletStyle.tabHeight).addParam(this.portletStyle.tabPaddingTop).addParam(this.portletStyle.tabPaddingBottom)
					.addParam(this.portletStyle.tabSpacing).addParam(this.portletStyle.getFontSize()).addParam(this.portletStyle.getInitialPadding()).addParam(farAligned)
					.addParam(isBottom).addParam(isVertical).addParam(portletStyle.leftRounding).addParam(portletStyle.rightRounding)
					.addParamQuoted(this.portletStyle.menuArrowColor).addParam(this.portletStyle.menuArrowSize).addParam(this.portletStyle.getTabFloatSize())
					.addParam(this.portletStyle.getTabPaddingStart()).addParam(this.portletStyle.getHasAddButton()).addParamQuoted(this.portletStyle.getAddButtonColor())
					.addParamQuoted(tbc).addParamQuoted(this.portletStyle.getFontFamily()).addParamQuoted(stbc).addParam(stbs).addParam(!this.portletStyle.hideArrangeTabs).end();
			for (Tab tab : portlet.getTabs())
				callJsAddTab(tab);
			if (portlet.getVisibleChildLocation() != -1)
				callJsFunction_setActiveTab(portlet.getVisibleChildLocation());
			callJsFunction_repaint();
			if (portlet.getVisibleChildLocation() != -1)
				callJsFunction("focusTab").end();
		} finally {
			endLcv();
		}

	}
	private void callJsAddTab(Tab tab) {
		JsFunction t = callJsFunction("addTab").addParam(tab.getLocation()).addParamQuoted(OH.noNull(tab.getHover(), "")).addParamQuotedHtml(tab.getTitle())
				.addParam(tab.getAllowTitleEdit()).addParamQuoted(tab.getSelectColor()).addParamQuoted(tab.getUnselectColor()).addParamQuoted(tab.getSelectTextColor())
				.addParamQuoted(tab.getUnselectTextColor()).addParam(tab.isHidden()).addParamQuoted(tab.getBlinkColor()).addParam(tab.getBlinkPeriod())
				.addParamQuoted(tab.getHtmlIdSelector()).end();

	}
	public void callJsFunction_setActiveTab(int location) {
		callJsFunction("setActiveTab").addParam(location).end();
	}
	public void callJsFunction_repaint() {
		callJsFunction("rp").end();
	}
	public void callJsFunction_focusTab() {
		callJsFunction("focusTab").end();
	}
	public void endLcv() {
		OH.assertGt(this.inLcv, 0);
		if (--this.inLcv == 0 && this.wroteLcv) {
			manager.getPendingJs().append("}\n");
			this.wroteLcv = false;
		}
	}

	public void initLcv() {
		++this.inLcv;
	}
}
