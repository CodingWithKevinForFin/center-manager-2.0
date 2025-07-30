package com.f1.ami.web;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.utils.OH;

public class AmiWebCustomCssTabsPortlet extends GridPortlet implements AmiWebSpecialPortlet, FormPortletListener, ConfirmDialogListener {

	private TabPortlet tabs;
	private Map<String, AmiWebCustomCssPortlet> cssPortlets = new LinkedHashMap<String, AmiWebCustomCssPortlet>();
	private FormPortlet buttonsForm;
	private FormPortletButton submitButton;
	private FormPortletButton cancelButton;
	private FormPortletButton applyButton;

	public AmiWebCustomCssTabsPortlet(PortletConfig config, AmiWebService service) {
		super(config);
		addChild(tabs = new TabPortlet(generateConfig()));
		tabs.setIsCustomizable(false);

		AmiWebCustomCssManager cssManager = service.getCustomCssManager();
		for (String s : service.getLayoutFilesManager().getFullAliasesByPriority()) {
			AmiWebLayoutFile lo = service.getLayoutFilesManager().getLayoutByFullAlias(s);
			if (lo.getDuplicateStatus() == AmiWebLayoutFile.SECONDARY)
				continue;
			AmiWebCustomCssPortlet child = new AmiWebCustomCssPortlet(generateConfig(), service.getDesktop(), s);
			cssPortlets.put(s, child);
			Tab tab = tabs.addChild("".equals(s) ? "<root>" : s, child);
			child.setTab(tab);
		}
		this.buttonsForm = addChild(new FormPortlet(generateConfig()), 0, 1);
		this.setRowSize(1, 35);
		this.submitButton = this.buttonsForm.addButton(new FormPortletButton("Submit"));
		this.cancelButton = this.buttonsForm.addButton(new FormPortletButton("Cancel"));
		this.applyButton = this.buttonsForm.addButton(new FormPortletButton("Apply"));
		this.buttonsForm.addFormPortletListener(this);
	}

	public boolean hasPendingChanges() {
		for (AmiWebCustomCssPortlet i : this.cssPortlets.values())
			if (i.hasPendingChanges())
				return true;
		return false;
	}

	public boolean apply() {
		for (AmiWebCustomCssPortlet i : this.cssPortlets.values())
			if (i.hasPendingChanges()) {
				i.apply();
				if (!i.hasPendingChanges()) {
					this.tabs.bringToFront(i.getPortletId());
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
			for (AmiWebCustomCssPortlet i : this.cssPortlets.values()) {
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

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("CLOSE".equals(source.getCallback())) {
			if (OH.eq(id, ConfirmDialogPortlet.ID_YES)) {
				for (AmiWebCustomCssPortlet i : this.cssPortlets.values())
					i.revertChanges();
				close();
			}
		}
		return true;
	}
}
