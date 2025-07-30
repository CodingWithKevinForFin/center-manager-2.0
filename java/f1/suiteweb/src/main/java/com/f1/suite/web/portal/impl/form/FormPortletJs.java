package com.f1.suite.web.portal.impl.form;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class FormPortletJs {
	private static final String RESET_BUTTONS = "resetButtons";
	private static final String RESET = "reset";
	private static final String SET_HTML_LAYOUT = "setHtmlLayout";
	private static final String REPAINT = "repaint";
	private static final String ADD_FIELD = "addField";
	private static final String REMOVE_FIELD = "removeField";
	private static final String ADD_BUTTON = "addButton";
	private static final String SET_VALUE = "setValue";
	private static final String GET_FIELD = "getField";
	private static final String SET_CSS_STYLE = "setCssStyle";
	private static final String SET_SCROLL = "setScroll";
	private static final String SET_SCROLL_OPTIONS = "setScrollOptions";
	private static final String SET_BUTTON_STYLE = "setButtonStyle";
	private static final String SET_LABEL_WIDTH = "setLabelWidth";
	private static final String SET_FIELD_POSITION = "setFieldPosition";
	private static final String SET_FIELD_LABEL_POSITION = "setFieldLabelPosition";
	private static final String SET_FIELD_STYLE_OPTIONS = "setFieldStyleOptions";
	private static final String SET_FIELD_LABEL_SIZE = "setFieldLabelSize";
	private static final String SET_FIELD_HIDDEN = "setFieldHidden";
	private static final String SHOW_CONTEXT_MENU = "showContextMenu";
	private static final String SHOW_BUTTON_CONTEXT_MENU = "showButtonContextMenu";
	private static String VARNAME = "t";

	final private PortletManager manager;
	final private JsFunction jsFunction;
	final private JsFunction lcvFunction;
	final private FormPortlet formPortlet;
	final private FormPortletStyle formPortletStyle;
	private int inLcv = 0;
	private boolean wroteLcv = false;

	public FormPortletJs(FormPortlet formPortlet) {
		this.formPortlet = formPortlet;
		this.formPortletStyle = formPortlet.getFormPortletStyle();
		this.manager = this.formPortlet.getManager();
		this.jsFunction = new JsFunction(formPortlet.getJsObjectName());
		this.lcvFunction = new JsFunction(VARNAME);
	}

	private JsFunction callJsFunction(String functionName) {
		return callJsFunction(manager.getPendingJs(), functionName);
	}

	private void ensureLcvWritten() {
		if (inLcv > 0) {
			if (!wroteLcv) {
				manager.getPendingJs().append("{var ").append(VARNAME).append('=').append(this.formPortlet.getJsObjectName()).append(";\n");
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

	public void buildButtons() {
		initLcv();
		try {
			this.callJsFunction_resetButtons();
			if (this.formPortletStyle.getShowBottomButtons()) {
				for (Map.Entry<String, FormPortletButton> e : formPortlet.getButtonsList()) {
					FormPortletButton button = e.getValue();
					this.runJs_addButton(button);
				}
			}
		} finally {
			endLcv();
		}
	}

	protected void endLcv() {
		OH.assertGt(this.inLcv, 0);
		if (--this.inLcv == 0 && this.wroteLcv) {
			manager.getPendingJs().append("}\n");
			this.wroteLcv = false;
		}
	}

	protected void initLcv() {
		++this.inLcv;
	}

	public void callJsFunction_reset() {
		callJsFunction(RESET).end();
	}
	public void callJsFunction_resetButtons() {
		callJsFunction(RESET_BUTTONS).end();
	}
	public void callJsFunction_setHtmlLayout(String htmlLayout, int htmlRotate) {
		callJsFunction(SET_HTML_LAYOUT).addParamQuoted(htmlLayout).addParam(htmlRotate).end();
	}
	public void callJsFunction_setCssStyle(String cssStyle) {
		callJsFunction(SET_CSS_STYLE).addParamQuoted(cssStyle).end();
	}
	public void callJsFunction_setLabelWidth(int labelsWidth, int labelPadding, String labelsStyle, int fieldSpacing, int widthStretchPadding) {
		callJsFunction(SET_LABEL_WIDTH).addParam(labelsWidth).addParam(labelPadding).addParamQuoted(labelsStyle).addParam(fieldSpacing).addParam(widthStretchPadding).end();
	}
	public void callJsFunction_setButtonStyle(int buttonHeight, int buttonPaddingT, int buttonPaddingB, String buttonPanelStyle, String buttonsStyle, int buttonsSpacing) {
		callJsFunction(SET_BUTTON_STYLE).addParam(buttonHeight).addParam(buttonPaddingT).addParam(buttonPaddingB).addParamQuoted(buttonPanelStyle).addParamQuoted(buttonsStyle)
				.addParam(buttonsSpacing).end();
	}

	public void callJsFunction_repaint() {
		callJsFunction(REPAINT).end();
	}

	public void callJsFunction_setScroll() {
		callJsFunction(SET_SCROLL).addParam(this.formPortlet.getClipLeft()).addParam(this.formPortlet.getClipTop()).end();
	}

	public void callJsFunction_setScrollOptions() {
		this.callJsFunction(SET_SCROLL_OPTIONS).addParam(this.formPortletStyle.getScrollBarWidth()).addParamQuoted(this.formPortletStyle.getScrollGripColor())
				.addParamQuoted(this.formPortletStyle.getScrollTrackColor()).addParamQuoted(this.formPortletStyle.getScrollButtonColor())
				.addParamQuoted(this.formPortletStyle.getScrollIconsColor()).addParamQuoted(this.formPortletStyle.getScrollBorderColor())
				.addParamQuoted(this.formPortletStyle.getScrollBarRadius()).addParam(this.formPortletStyle.getScrollBarHideArrows())
				.addParamQuoted(this.formPortletStyle.getScrollBarCornerColor()).end();
	}
	public void runJs_removeField(String name) {
		callJsFunction(REMOVE_FIELD).addParamQuoted(name).end();
	}
	public void runJs_addField(FormPortletField<?> field) {
		StringBuilder pendingJs = manager.getPendingJs();

		String css = field.getStyle();
		if (!field.getIgnoreDefaultStyle())
			css = joinStyles(this.formPortlet.getStyleManager().getDefaultFormFieldStyle(field.getjsClassName()), css);
		JsFunction func = callJsFunction(ADD_FIELD).startParam();
		new JsFunction(pendingJs.append("new "), null, field.getjsClassName()).addParam("t.form").addParamQuoted(field.getId())
				.addParamQuotedHtml(this.formPortlet.formatText(field.getTitle())).close();
		func.addParamQuoted(css).addParam(!field.isVisible()).end();
		field.flagRebuild();
	}

	//if both populated, b will override a
	static private String joinStyles(String a, String b) {
		if (SH.isnt(a))
			return b;
		if (SH.isnt(b))
			return a;
		LinkedHashMap<String, String> t = new LinkedHashMap<String, String>();
		SH.splitToMap(t, '|', '=', a);
		SH.splitToMap(t, '|', '=', b);
		return SH.joinMap('|', '=', t);
	}

	protected void runJs_addButton(FormPortletButton button) {
		StringBuilder pendingJs = manager.getPendingJs();
		pendingJs.append("{var button= ");
		callJsFunction(ADD_BUTTON).addParamQuoted(button.getId()).addParamQuotedHtml(button.getName()).addParamQuoted(button.getCssStyle()).end();
		button.rebuildJs("button", pendingJs);
		pendingJs.append("}" + SH.NEWLINE);
	}
	protected void runJs_showContextMenu(WebMenu menu, String fieldId) {
		Map<String, Object> menuModel = PortletHelper.menuToJson(this.manager, menu);
		JsFunction jsf = new JsFunction(this.manager.getPendingJs(), formPortlet.getJsObjectName(), SHOW_CONTEXT_MENU);
		jsf.addParamJson(menuModel).addParamQuoted(fieldId);
		jsf.end();
	}
	protected void runJs_showButtonContextMenu(WebMenu menu) {
		Map<String, Object> menuModel = PortletHelper.menuToJson(this.manager, menu);
		JsFunction jsf = new JsFunction(this.manager.getPendingJs(), formPortlet.getJsObjectName(), SHOW_BUTTON_CONTEXT_MENU);
		jsf.addParamJson(menuModel);
		jsf.end();
	}

	public void callJsFunction_UpdateJs(FormPortletField<?> f, StringBuilder pendingJs) {
		this.ensureLcvWritten();
		pendingJs.append("{var ");
		pendingJs.append(f.getJsObjectName()); // Fields should be 'f'
		pendingJs.append('=');
		callJsFunction(GET_FIELD).addParamQuoted(f.getId()).end();
		if (f.hasChanged(FormPortletField.MASK_REBUILD))
			f.rebuildJs(pendingJs);
		f.updateJs(pendingJs);
		pendingJs.append("}").append(SH.NEWLINE);
		f.clearChanges();

	}

}
