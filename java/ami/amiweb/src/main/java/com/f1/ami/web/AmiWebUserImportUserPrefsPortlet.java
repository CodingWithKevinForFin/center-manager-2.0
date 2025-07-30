package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.SH;
import com.f1.utils.StringFormatException;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiWebUserImportUserPrefsPortlet extends GridPortlet implements FormPortletListener {

	private AmiWebService service;
	private FormPortlet formPortlet;
	private FormPortletTextAreaField textAreaField;
	private FormPortletButton importButton;
	private FormPortletButton cancelButton;

	public AmiWebUserImportUserPrefsPortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.service = service;
		this.formPortlet = new FormPortlet(generateConfig());
		FormPortletTitleField titleField = this.formPortlet.addField(new FormPortletTitleField(""));
		this.importButton = this.formPortlet.addButton(new FormPortletButton("Import"));
		this.cancelButton = this.formPortlet.addButton(new FormPortletButton("Cancel"));
		this.textAreaField = formPortlet.addField(new FormPortletTextAreaField(""));
		this.formPortlet.addFormPortletListener(this);
		textAreaField.setLeftPosPx(5);
		textAreaField.setTopPosPx(30);
		textAreaField.setBottomPosPx(50);
		textAreaField.setRightPosPx(5);
		titleField.setTopPosPx(5);
		titleField.setHeightPx(25);
		titleField.setLeftPosPx(5);
		titleField.setWidthPx(800);
		titleField.setValue("Paste the previously exported preferences into the below text area and click import");
		addChild(this.formPortlet);
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton) {
			close();
		} else if (button == this.importButton) {
			String value = this.textAreaField.getValue();
			if (SH.isnt(value)) {
				getManager().showAlert("Please paste the layout preferences JSON into the text area");
				return;
			}
			List<Map<String, Object>> prefs = null;
			try {
				Object obj = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(value);
				if (obj instanceof List)
					prefs = (List<Map<String, Object>>) obj;
				else if (obj instanceof Map) {
					prefs = new ArrayList<Map<String, Object>>();
					prefs.add((Map) obj);
				} else {
					getManager().showAlert("Invalid JSON format, expecting Map or List");
					return;
				}
			} catch (StringFormatException e) {
				getManager().showAlert("Invalid JSON format");
				return;
			}
			byte cpim = service.getDesktop().getInEditMode() ? AmiWebVarsManager.CUST_PREF_IMPORT_MODE_ACCEPT : this.service.getVarsManager().getCustomPrefsImportMode();
			for (Map<String, Object> map : prefs) {
				String upid = (String) map.get("upid");
				if (upid != null) {
					Map<String, Object> pref = (Map<String, Object>) map.get("pref");
					if (pref == null) {
						getManager().showAlert("upid " + upid + " is missing 'pref' attribute");
						return;
					}
				} else {
					String cpid = (String) map.get("cpid");
					if (cpid != null) {
						if (cpim == AmiWebVarsManager.CUST_PREF_IMPORT_MODE_REJECT) {
							getManager().showAlert("Access Denied: Can not set custom preferences for '" + cpid + "'");
							return;
						}
						Object pref = map.get("pref");
						if (pref == null) {
							getManager().showAlert("cpid " + cpid + " is missing 'pref' attribute");
							return;
						}
					} else {
						getManager().showAlert("Clause missing upid/cpid");
						return;
					}
				}
			}
			if (cpim == AmiWebVarsManager.CUST_PREF_IMPORT_MODE_ACCEPT)
				this.service.getVarsManager().applyCustomPrefs(prefs, false);
			this.service.getDesktop().getCallbacks().execute("onUserPrefsLoading", prefs);
			this.service.applyUserPrefs(prefs);
			this.service.getDesktop().getCallbacks().execute("onUserPrefsLoaded", prefs);
			close();
		}

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
}
