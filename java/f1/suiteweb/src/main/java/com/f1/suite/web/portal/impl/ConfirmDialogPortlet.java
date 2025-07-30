package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.text.SimpleFastTextPortlet;
import com.f1.suite.web.portal.style.PortletStyleManager_Dialog;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class ConfirmDialogPortlet extends GridPortlet implements HtmlPortletListener, FormPortletListener, ConfirmDialog {

	public static class CloseOnOkay implements ConfirmDialogListener {

		final private Portlet target;

		public CloseOnOkay(Portlet target) {
			this.target = target;
		}

		@Override
		public boolean onButton(ConfirmDialog source, String id) {
			if (ID_YES.equals(id))
				target.close();
			return true;
		}

	}

	private HtmlPortlet textPortlet;
	private HtmlPortlet iconPortlet;
	private FormPortlet inputPortlet;
	private HtmlPortlet dummyPortlet;
	private Map<String, FormPortletButton> buttons = new HashMap<String, FormPortletButton>();
	private FormPortlet buttonsPortlet;
	private List<ConfirmDialogListener> listeners = new ArrayList<ConfirmDialogListener>();
	private Object correlationData;
	private Map<String, Tuple2<String, Portlet>> followUps = new HashMap<String, Tuple2<String, Portlet>>();
	private String callback;
	private FormPortletField inputField;
	private String details;
	private PortletStyleManager_Dialog styleManager;
	private HtmlPortlet detailsPortlet;

	public ConfirmDialogPortlet(PortletConfig config) {
		this(config, "", TYPE_MESSAGE, null, null);
	}
	public ConfirmDialogPortlet(PortletConfig config, String text, byte type) {
		this(config, text, type, null, null);
	}
	public ConfirmDialogPortlet(PortletConfig config, String text, byte type, ConfirmDialogListener listener) {
		this(config, text, type, listener, null);
	}
	public ConfirmDialogPortlet(PortletConfig config, String text, byte type, ConfirmDialogListener listener, FormPortletField input) {
		super(config);
		if (listener != null)
			addDialogListener(listener);
		this.styleManager = getManager().getStyleManager().getDialogStyle();
		setCssStyle(styleManager.getDefaultDialogBackgroundStyle());

		textPortlet = new HtmlPortlet(generateConfig(), "", null);
		textPortlet.setCssStyle(styleManager.getDefaultDialogTextStyle());
		GridPortlet buttonsGrid = new GridPortlet(generateConfig());
		buttonsPortlet = new FormPortlet(generateConfig());
		buttonsPortlet.addFormPortletListener(this);
		buttonsPortlet.onUserRequestFocus(null);
		if (input != null) {
			this.inputField = input;
			this.inputPortlet = new FormPortlet(generateConfig());
			this.inputPortlet.getFormPortletStyle().setCssStyle("_bg=#e2e2e2");
			this.inputPortlet.addField(input);
			if (SH.isnt(input.getTitle()))
				this.inputPortlet.getFormPortletStyle().setLabelsWidth(20);
			addChild(textPortlet, 0, 0, 2, 1);
			addChild(inputPortlet, 0, 1, 2, 1);
			addChild(buttonsGrid, 0, 2, 2, 1);
			if (input instanceof FormPortletTextAreaField) {
				setRowSize(0, 60);
			} else {
				setRowSize(1, 100);
			}
			setRowSize(2, 42);
			input.focus();
		} else {
			addChild(textPortlet, 0, 0, 2, 1);
			addChild(buttonsGrid, 0, 1, 2, 1);
			setRowSize(1, 42);
		}
		buttonsGrid.addChild(dummyPortlet = new HtmlPortlet(generateConfig(), "", null), 0, 0);
		buttonsGrid.addChild(this.buttonsPortlet, 1, 0);
		buttonsGrid.addChild(this.detailsPortlet = new HtmlPortlet(generateConfig(), "", null), 2, 0);
		buttonsGrid.setColSize(0, 65);
		buttonsGrid.setColSize(2, 65);
		setSuggestedSize(725, 250);
		setText(text);
		switch (type) {
			case TYPE_WAIT_WITH_CANCEL: {
				addButton(ID_NO, "Cancel");
				break;
			}
			case TYPE_MESSAGE: {
				addButton(ID_CLOSE, "Close");
				break;
			}
			case TYPE_ALERT: {
				addButton(ID_CLOSE, "Continue");
				break;
			}
			case TYPE_YES_NO: {
				addButton(ID_YES, "Yes");
				addButton(ID_NO, "No");
				break;
			}
			case TYPE_OK_CANCEL: {
				addButton(ID_YES, "OK");
				addButton(ID_NO, "Cancel");
				break;
			}
		}
		PortletStyleManager_Form defaultButtonStyle = styleManager.getDefaultButtonStyle();
		dummyPortlet.setCssStyle(defaultButtonStyle.getDefaultAlertFormButtonPanelBackgroundColor());
		detailsPortlet.setCssStyle(defaultButtonStyle.getDefaultAlertFormButtonPanelBackgroundColor());
		buttonsPortlet.setStyle(defaultButtonStyle);
		if (!styleManager.isUseDefaultStyling()) {
			String tcss = styleManager.buildTextCss(false);
			textPortlet.setCssStyle(tcss);
			styleManager.getAlertButtonStyle().updateButtonCss();
			buttonsPortlet.setStyle(styleManager.getAlertButtonStyle());
			dummyPortlet.setCssStyle(styleManager.getAlertButtonStyle().getFormButtonPanelStyle());
			detailsPortlet.setCssStyle(styleManager.getAlertButtonStyle().getFormButtonPanelStyle());
			if (inputPortlet != null) {
				inputPortlet.setStyle(styleManager.getUserButtonStyle());
				inputField.setLabelCssStyle(styleManager.buildTextCss(true));
				inputField.setBgColor(styleManager.getAlertBodyBackgroundColor());
				inputField.setBorderColor(styleManager.getAlertBodyFontColor());
				inputField.setFontColor(styleManager.getAlertBodyFontColor());
				inputField.setFieldFontFamily(styleManager.getAlertBodyFontFam());
			}
		}
	}

	private static String getCssClassForType(byte type) {
		switch (type) {
			case TYPE_MESSAGE:
			case TYPE_ALERT:
			case TYPE_WAIT_WITH_CANCEL:
				return "confirm_dialog_icon_important";
			case TYPE_YES_NO:
			case TYPE_OK_CANCEL:
			case TYPE_OK_CUSTOM:
				return "confirm_dialog_icon_question";
			default:
				throw new RuntimeException("invalid dialog type: " + type);
		}
	}
	@Override
	public ConfirmDialogPortlet setText(String text) {
		textPortlet.setHtml("<p>" + text);
		return this;
	}
	@Override
	public ConfirmDialogPortlet setStyle(byte style) {
		iconPortlet.setCssClass(getCssClassForType(style));
		return this;
	}

	public ConfirmDialogPortlet addButton(String id, String buttonText) {
		FormPortletButton button = buttons.get(id);
		if (button != null) {
			button.setName(buttonText);
			return this;
		}
		FormPortletButton btn = new FormPortletButton(buttonText);
		btn.setCorrelationData(id);
		this.buttonsPortlet.addButton(btn);
		buttons.put(id, btn);
		return this;
	}
	@Override
	public ConfirmDialogPortlet addButton(String id, String buttonText, int location) {
		FormPortletButton button = buttons.get(id);
		if (button != null) {
			button.setName(buttonText);
			return this;
		}
		FormPortletButton btn = new FormPortletButton(buttonText);
		btn.setCorrelationData(id);
		this.buttonsPortlet.addButton(btn, location);
		buttons.put(id, btn);
		return this;
	}
	@Override
	public void clearButtons() {
		this.buttons.clear();
		this.buttonsPortlet.clearButtons();
	}
	@Override
	public ConfirmDialogPortlet updateButton(String id, String buttonText) {
		FormPortletButton button = CH.getOrThrow(buttons, id);
		button.setName(buttonText);
		return this;
	}

	public void addDialogListener(ConfirmDialogListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void onUserClick(HtmlPortlet portlet) {
	}

	@Override
	public void onUserCallback(HtmlPortlet portlet, String id, int mousex, int mousey, HtmlPortlet.Callback attributes) {
		if ("DETAILS".equals(id)) {
			SimpleFastTextPortlet t = new SimpleFastTextPortlet(generateConfig());
			t.setLines(details);
			getManager().showDialog("Details", t);
		}
	}
	@Override
	public Object getCorrelationData() {
		return correlationData;
	}
	@Override
	public ConfirmDialogPortlet setCorrelationData(Object correlationData) {
		this.correlationData = correlationData;
		return this;
	}
	@Override
	public void setFollowupDialog(String id, String string, Portlet portlet) {
		CH.getOrThrow(buttons, id);
		followUps.put(id, new Tuple2<String, Portlet>(id, portlet));
	}
	@Override
	public ConfirmDialogPortlet setCallback(String string) {
		this.callback = string;
		return this;
	}

	@Override
	public String getCallback() {
		return this.callback;
	}

	@Override
	public Object getInputFieldValue() {
		return this.inputField == null ? null : this.inputField.getValue();
	}

	@Override
	public ConfirmDialogPortlet setInputFieldValue(Object value) {
		this.inputField.setValue(value);
		return this;
	}

	@Override
	public FormPortletField<? extends Object> getInputField() {
		return this.inputField;
	}

	// this gets called when there is a backend exeception
	@Override
	public void setDetails(String details) {
		this.details = details;
		if (this.details != null) {
			this.detailsPortlet.setHtml("<div style='text-decoration:underline;cursor:pointer;bottom:10px;right:10px;color:white' onclick='"
					+ this.detailsPortlet.generateCallback("DETAILS") + "' >More&gt;&gt;</div>");
			this.detailsPortlet.addListener(this);
		}
	}
	@Override
	public void onHtmlChanged(String old, String nuw) {
	}
	public static void confirmAndCloseWindow(Portlet target, String message) {
		target.getManager().showDialog("Confirm", new ConfirmDialogPortlet(target.getManager().generateConfig(), message, TYPE_OK_CANCEL, new CloseOnOkay(target)));
	}

	@Override
	public void closeDialog() {
		close();
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		boolean ok = true;
		String id = (String) button.getCorrelationData();
		for (ConfirmDialogListener listener : this.listeners)
			if (!listener.onButton(this, id))
				ok = false;
		if (ok)
			close();
		Tuple2<String, Portlet> tuple = followUps.get(id);
		if (tuple != null)
			getManager().showDialog(tuple.getA(), tuple.getB());

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (!keyEvent.isAltKey() && !keyEvent.isCtrlKey() && !keyEvent.isShiftKey()) {
			if (this.inputField == null) {
				if (KeyEvent.ENTER.equals(keyEvent.getKey())) {
					if (buttons.size() == 1)
						onButtonPressed(this.buttonsPortlet, CH.first(buttons.values()));
				} else {
					Entry<String, FormPortletButton> found = null;
					for (Entry<String, FormPortletButton> i : buttons.entrySet()) {
						if (SH.startsWithIgnoreCase(i.getValue().getName(), keyEvent.getKey(), 0)) {
							if (found != null) {
								found = null;
								break;
							}
							found = i;
						}
					}
					if (found != null)
						onButtonPressed(this.buttonsPortlet, found.getValue());
				}
			}
		}
		return super.onUserKeyEvent(keyEvent);
	}
	public FormPortlet getInputPortlet() {
		return inputPortlet;
	}

	@Override
	public void fireYesButton() {
		for (ConfirmDialogListener listener : this.listeners)
			listener.onButton(this, ID_YES);
	}

	@Override
	public void fireButton(String id) {
		for (ConfirmDialogListener listener : this.listeners)
			listener.onButton(this, id);
	}
}
