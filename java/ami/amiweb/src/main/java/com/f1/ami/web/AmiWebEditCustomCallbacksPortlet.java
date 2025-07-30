package com.f1.ami.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.utils.OH;

public class AmiWebEditCustomCallbacksPortlet extends GridPortlet implements FormPortletListener, ConfirmDialogListener, AmiWebSpecialPortlet {

	private TabPortlet tabs;
	private Map<String, AmiWebEditAmiScriptCallbacksPortlet> methodPortlets = new LinkedHashMap<String, AmiWebEditAmiScriptCallbacksPortlet>();
	private FormPortlet buttonsForm;
	private FormPortletButton submitButton;
	private FormPortletButton cancelButton;
	private FormPortletButton applyButton;
	private AmiWebService service;

	public AmiWebEditCustomCallbacksPortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.service = service;
		addChild(tabs = new TabPortlet(generateConfig()));
		tabs.setIsCustomizable(false);
		for (String s : service.getLayoutFilesManager().getFullAliasesByPriority()) {
			AmiWebAmiScriptCallbacks t = service.getScriptManager(s).getLayoutCallbacks();
			AmiWebEditAmiScriptCallbacksPortlet child = new AmiWebEditAmiScriptCallbacksPortlet(service.getPortletManager().generateConfig(), t);
			methodPortlets.put(s, child);
			tabs.addChild("".equals(s) ? "<root>" : s, child);
		}
		this.buttonsForm = addChild(new FormPortlet(generateConfig()), 0, 1);
		this.setRowSize(1, 35);
		this.submitButton = this.buttonsForm.addButton(new FormPortletButton("Submit"));
		this.cancelButton = this.buttonsForm.addButton(new FormPortletButton("Cancel"));
		this.applyButton = this.buttonsForm.addButton(new FormPortletButton("Apply"));
		this.buttonsForm.addFormPortletListener(this);
	}

	public boolean hasPendingChanges() {
		for (AmiWebEditAmiScriptCallbacksPortlet i : this.methodPortlets.values())
			if (i.hasPendingChanges())
				return true;
		return false;
	}

	public boolean apply() {
		for (Entry<String, AmiWebEditAmiScriptCallbacksPortlet> i : this.methodPortlets.entrySet()) {
			if (!i.getValue().apply()) {
				this.tabs.bringToFront(i.getValue().getPortletId());
				return false;
			}
		}
		for (Entry<String, AmiWebEditAmiScriptCallbacksPortlet> i : this.methodPortlets.entrySet()) {
			AmiWebAmiScriptCallbacks t = service.getScriptManager(i.getKey()).getLayoutCallbacks();
			if (!i.getValue().applyTo(t, null)) {
				this.tabs.bringToFront(i.getValue().getPortletId());
				return false;
			}
		}
		return true;

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == submitButton) {
			if (apply())
				close();
		} else if (button == cancelButton) {
			for (AmiWebEditAmiScriptCallbacksPortlet i : this.methodPortlets.values()) {
				if (i.hasChanged()) {
					getManager().showDialog("Cancel",
							new ConfirmDialogPortlet(generateConfig(), "Abandon changes?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("CLOSE"));
					return;
				}
			}
			close();
		} else if (button == applyButton) {
			apply();
		}

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	public AmiWebEditAmiScriptCallbacksPortlet getCallbacksEditor(String layout) {
		return this.methodPortlets.get(layout);
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("CLOSE".equals(source.getCallback())) {
			if (OH.eq(id, ConfirmDialogPortlet.ID_YES)) {
				//				for (AmiWebPortletScriptSettingsPortlet i : this.methodPortlets.values())
				//					i.revertChanges();
				close();
			}
		}
		return true;
	}

}
