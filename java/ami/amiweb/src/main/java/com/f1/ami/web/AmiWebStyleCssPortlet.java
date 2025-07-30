package com.f1.ami.web;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiDebugMessageListener;
import com.f1.ami.web.style.AmiWebStyle;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;

public class AmiWebStyleCssPortlet extends GridPortlet
		implements FormPortletListener, ConfirmDialogListener, FormPortletContextMenuFactory, FormPortletContextMenuListener, AmiDebugMessageListener {
	private static final Logger log = LH.get();
	private FormPortlet bodyForm;
	private AmiWebFormPortletAmiScriptField bodyField;
	private AmiWebService service;
	private AmiWebDesktopPortlet desktop;
	private String origCustomCss;
	private FormPortletTitleField titleField;
	private boolean hasPendingChanges = false;
	private boolean isRunning = false;
	private DividerPortlet div;
	private HtmlPortlet errorPortlet;
	private Object o;
	private double errorDivOffset = .7;
	//	private AmiWebCustomCssManager cssManager;
	private AmiWebStyle style;

	public AmiWebStyleCssPortlet(PortletConfig config, AmiWebDesktopPortlet desktop) {
		super(config);
		//		this.cssManager = desktop.getService().getCustomCssManager();
		this.desktop = desktop;
		this.service = AmiWebUtils.getService(getManager());
		this.bodyForm = new FormPortlet(generateConfig());
		this.errorPortlet = new HtmlPortlet(generateConfig());
		this.div = addChild(new DividerPortlet(generateConfig(), false, this.bodyForm, this.errorPortlet), 0, 0);
		this.div.setOffset(1);
		this.bodyForm.getFormPortletStyle().setLabelsWidth(20);
		this.titleField = bodyForm.addField(new FormPortletTitleField(""));
		FormPortletField<String> instructions = bodyForm.addField(new FormPortletDivField(""));
		instructions.setWidth(instructions.WIDTH_STRETCH);
		instructions.setValue("(1) Class name <u>must</u> start with <b><i>" + AmiWebCustomCssManager.PUBLIC + "</i></b> in order to be referenced in custom HTML (2) Use <i><b>"
				+ AmiWebCustomCssManager.EXTENDS_CSS + "</b>: other_class</i> for css inheritance");
		this.bodyField = bodyForm.addField(new AmiWebFormPortletAmiScriptField("", this.service.getPortletManager(), ""));
		this.bodyField.focus();
		this.bodyField.setMode("css");
		this.setSuggestedSize(1000, getManager().getRoot().getHeight() - 50);
		this.bodyField.setHeight(FormPortletField.SIZE_STRETCH);
		this.bodyForm.addFormPortletListener(this);
		this.bodyForm.setMenuFactory(this);
		this.bodyForm.addMenuListener(this);
	}

	public void setStyle(AmiWebStyle style) {
		this.style = style;
		this.origCustomCss = style.getCss().getCustomCss();
		boolean isReadonly = style.getReadOnly();
		this.errorPortlet.setHtml("");
		if (this.div.getOffset() != 1)
			this.errorDivOffset = this.div.getOffset();
		this.div.setOffset(1);
		this.titleField.setValue("Custom CSS:" + (isReadonly ? " (READONLY LAYOUT)" : ""));
		this.bodyField.setDisabled(isReadonly);
		this.bodyField.setValue(SH.is(origCustomCss) ? origCustomCss : "");
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	private void setCode(String script) {
		AmiWebDebugManagerImpl debugManager = new AmiWebDebugManagerImpl(this.service);
		debugManager.setShouldDebug(AmiDebugMessage.SEVERITY_WARNING, true);
		this.isRunning = true;
		debugManager.addDebugMessageListener(this);
		try {
			this.errorPortlet.setHtml("");
			if (this.div.getOffset() != 1)
				this.errorDivOffset = this.div.getOffset();
			this.div.setOffset(1);
			this.style.getCss().setCustomCss(script, debugManager);
		} finally {
			debugManager.removeDebugMessageListener(this);
			this.isRunning = false;
		}
	}
	private String getCode() {
		return bodyField.getValue();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.bodyField) {
			updatePendingChanges();
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == 13 && mask != 0) {
			apply();
			return;
		}
		if (field == this.bodyField)
			this.bodyField.onSpecialKeyPressed(formPortlet, field, keycode, mask, cursorPosition);
	}

	public boolean apply() {
		if (!hasPendingChanges())
			return true;
		setCode(getCode());
		updatePendingChanges();
		if (!hasPendingChanges)
			return true;
		PortletHelper.ensureVisible(this);
		this.bodyField.focus();
		return false;
	}

	public boolean hasPendingChanges() {
		return !this.isRunning && this.hasPendingChanges;
	}

	private void updatePendingChanges() {
		if (hasPendingChanges != OH.ne(this.bodyField.getValue(), style.getCss().getCustomCss())) {
			hasPendingChanges = !hasPendingChanges;
			if (hasPendingChanges) {
				this.titleField.setValue("Custom CSS: <span style='color:#000088'>(ALT + ENTER to apply changed)</span>");
			} else {
				boolean isReadonly = style.getReadOnly();
				this.titleField.setValue("Custom CSS:" + (isReadonly ? " (READONLY LAYOUT)" : ""));
			}
		}
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("CLOSE".equals(source.getCallback())) {
			if (OH.eq(id, ConfirmDialogPortlet.ID_YES)) {
				setCode(this.origCustomCss);
				close();
			}
		}
		return true;
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		AmiWebMenuUtils.createColorsMenu(r, false, this.style);
		AmiWebMenuUtils.createOperatorsMenu(r, this.service, "");
		return r;
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebMenuUtils.processContextMenuAction(service, action, node);
	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ESCAPE.equals(keyEvent.getKey()))
			return true;
		return super.onUserKeyEvent(keyEvent);
	}
	@Override
	public void onClosed() {
		super.onClosed();
	}

	@Override
	public void onAmiDebugMessage(AmiDebugManager manager, AmiDebugMessage message) {
		Throwable exc = message.getException();
		if (exc instanceof ExpressionParserException) {
			StringBuilder before = new StringBuilder();
			String after;
			String exception;
			ExpressionParserException epe = (ExpressionParserException) exc;
			this.bodyField.setCursorPosition(epe.getPosition());
			if (epe.getExpression() == null) {
				Map<Object, Object> details = message.getDetails();
				String expression = (String) details.get("AmiScript");
				if (expression != null)
					epe.setExpression(expression);
			}
			before.append("Custom CSS Error\n\n").append(epe.toLegibleStringBefore());
			exception = epe.toLegibleStringException(true);
			after = epe.toLegibleStringAfter();
			StringBuilder errorMessage = new StringBuilder();

			errorMessage.append("<div class='ami_epe_before'>").append(WebHelper.escapeHtmlNewLineToBr(before.toString())).append("</div>");
			errorMessage.append("<div class='ami_epe_exception'>").append(WebHelper.escapeHtmlNewLineToBr(exception)).append("</div>");
			errorMessage.append("<div class='ami_epe_after'>").append(WebHelper.escapeHtmlNewLineToBr(after)).append("</div>");

			String text = errorMessage.toString();
			this.errorPortlet.setHtml(text);
			this.errorPortlet.setCssStyle("_fg=#880000|_bg=#FFFFFF|_fm=left|_fm=monospace" + (SH.is(text) ? "|style.border=1px solid #880000" : "|style.border=4px blue none"));
			this.div.setOffset(Math.min(errorDivOffset, .9));
		} else {
			String text = SH.printStackTrace(exc);
			this.errorPortlet.setHtml(text);
			this.errorPortlet.setCssStyle("_fg=#880000|_bg=#FFFFFF|_fm=left|_fm=monospace" + (SH.is(text) ? "|style.border=1px solid #880000" : "|style.border=4px blue none"));
			this.div.setOffset(Math.min(errorDivOffset, .9));
		}
	}
	@Override
	public void onAmiDebugMessagesRemoved(AmiDebugManager manager, AmiDebugMessage message) {
	}

	public boolean hasChanged() {
		return OH.ne(this.origCustomCss, getCode());
	}

	public void revertChanges() {
		setCode(this.origCustomCss);
	}

}
