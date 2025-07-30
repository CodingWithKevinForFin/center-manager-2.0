package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;

public class AmiWebEditAmiScriptCallbackDialogPortlet extends GridPortlet implements FormPortletListener, ConfirmDialogListener, AmiWebSpecialPortlet {

	private AmiWebAmiScriptCallbacks target;
	final private FormPortlet form;
	final private FormPortletButton cancelButton;
	final private FormPortletButton submitButton;
	final private AmiWebService service;
	private AmiWebEditAmiScriptCallbacksPortlet inner;

	public AmiWebEditAmiScriptCallbackDialogPortlet(PortletConfig config, AmiWebAmiScriptCallbacks target) {
		super(config);
		this.target = target;
		this.inner = new AmiWebEditAmiScriptCallbacksPortlet(generateConfig(), target);
		this.service = AmiWebUtils.getService(config.getPortletManager());
		addChild(this.inner, 0, 0);
		this.form = addChild(new FormPortlet(generateConfig()), 0, 1);
		this.setRowSize(1, 40);
		this.submitButton = this.form.addButton(new FormPortletButton("Submit"));
		this.cancelButton = this.form.addButton(new FormPortletButton("Cancel"));
		this.form.addFormPortletListener(this);
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			if (!apply())
				return;
			this.submit();
			this.close();
		} else if (button == this.cancelButton) {
			boolean changed = hasChanged();
			if (changed)
				getManager().showDialog("Revert Changes",
						new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to revert changes made?", ConfirmDialogPortlet.TYPE_YES_NO, this).setCallback("CANCEL"));
			else
				this.close();
		}
	}
	private boolean hasChanged() {
		return this.inner.hasChanged();
	}
	private boolean hasPendingChanges() {
		return this.inner.hasPendingChanges();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("CANCEL".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				close();
			}
			return true;
		}
		return false;

	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ESCAPE.equals(keyEvent.getKey()))
			return true;
		return super.onUserKeyEvent(keyEvent);
	}
	public boolean submit() {
		return this.inner.submitChanges();
	}
	public boolean apply() {
		return this.inner.applyTo(this.target, null);
	}

	public AmiWebAmiScriptCallbacks getCallbacks() {
		return this.target;
	}

	public AmiWebEditAmiScriptCallbacksPortlet getCallbacksEditor() {
		return this.inner;
	}
}
