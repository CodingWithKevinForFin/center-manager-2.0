package com.f1.ami.web;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField.FileData;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiWebUploadUserPrefsPortlet extends GridPortlet implements FormPortletListener {
	private static final Logger log = LH.get();
	private final AmiWebService service;
	private final FormPortlet form;
	private final FormPortletFileUploadField fileUploadField;
	private final FormPortletButton uploadLayoutButton;
	private final FormPortletButton cancelUploadLayoutButton;
	private PortletStyleManager_Form formStyle;

	public AmiWebUploadUserPrefsPortlet(PortletConfig config, AmiWebService amiWebService) {
		super(config);
		this.service = amiWebService;
		this.formStyle = getManager().getStyleManager().getFormStyle();
		this.form = new FormPortlet(generateConfig());
		this.fileUploadField = new FormPortletFileUploadField("Browse file: ");
		this.uploadLayoutButton = new FormPortletButton("Upload").setId("upload_layout");
		this.cancelUploadLayoutButton = new FormPortletButton("Cancel").setId("cancel_upload");
		this.form.addField(this.fileUploadField);
		this.form.addButton(uploadLayoutButton);
		this.form.addButton(cancelUploadLayoutButton);
		addChild(this.form, 0, 0);
		this.form.addFormPortletListener(this);
		prepareStyle();
	}
	private void prepareStyle() {
		this.fileUploadField.setCssStyle("style.paddingLeft=5px|style.paddingTop=5px");
		if (formStyle.isUseDefaultStyling())
			return;
		this.form.getFormPortletStyle().setCssStyle(service.getUserFormStyleManager().getFormStyle());
		this.form.getFormPortletStyle().setButtonPanelStyle(service.getUserFormStyleManager().getFormButtonPanelStyle());

		this.fileUploadField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=" + formStyle.getDefaultFormFontFam());
		this.fileUploadField.setBgColor(formStyle.getDefaultFormBgColor());
		this.fileUploadField.setBorderColor(formStyle.getFormBorderColor());
		this.fileUploadField.setFontColor(formStyle.getDefaultFormFontColor());
		this.fileUploadField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (OH.eq(button, this.uploadLayoutButton)) {
			String configText = getUploadedFileDataAsText();
			if (OH.ne(configText, null)) {
				List<Map<String, Object>> prefs = (List<Map<String, Object>>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(configText);
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
		} else if (OH.eq(button, this.cancelUploadLayoutButton)) {
			this.close();
		}
	}
	private String getUploadedFileDataAsText() {
		FileData fdata = this.fileUploadField.getValue();
		if (OH.eq(fdata, null)) {
			getManager().showAlert("You need to select a file to upload");
			LH.warning(log, "Empty file upload field.");
		} else {
			String contentType = fdata.getContentType();
			if (OH.eq(contentType, "application/octet-stream")) {
				try {
					byte[] bytes = fdata.getData();
					String textData = new String(bytes);
					return textData;
				} catch (Exception e) {
					getManager().showAlert("Error reading file. Please make sure it is in correct format");
					LH.warning(log, "Error reading the file " + getLayoutName() + " " + e);
				}
			} else {
				getManager().showAlert("Layout format is incompatible");
				LH.warning(log, "Incompatible content type: found " + contentType + " expected: application/octet-stream");
			}
		}
		return null;
	}
	public String getLayoutName() {
		return SH.trim(this.fileUploadField.getValue().getName());
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
}