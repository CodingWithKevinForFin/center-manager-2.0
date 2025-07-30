package com.f1.ami.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.F1LicenseInfo;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

public class AmiWebLicenseDialogPortlet extends GridPortlet implements FormPortletListener {
	private static final String CANCEL = "Cancel";
	private static final String SUBMIT = "Submit";
	private static final Logger log = LH.get();
	private static final String BUTTON_FIELD_BACKGROUND = "#E27025";
	private static final String FORM_INPUT_FIELD_PADDING = "5px";
	private static final String FORM_TITLE_TEXT_SIZE = "10px";

	private FormPortlet form;
	private FormPortletTextField licenseKeyInput;
	private String oldLicenseKey;
	private FormPortletTitleField textOr;
	private FormPortletButtonField generateLicenseKeyButton;
	private String licenseAuthUrl;
	private FormPortletTitleField licenseAuthUrlField;
	private FormPortletTitleField licenseHostField;
	private FormPortletButton submitButton;
	private FormPortletButton cancelButton;

	public AmiWebLicenseDialogPortlet(PortletConfig config) {
		super(config);
		this.form = new FormPortlet(generateConfig());

		this.licenseKeyInput = new FormPortletTextField("Enter License Key: ").setId("lincense_input");
		this.licenseHostField = new FormPortletTitleField("for " + F1LicenseInfo.getLicenseHost());
		this.licenseAuthUrl = this.getManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_LICENSE_AUTH_URL, "");

		this.form.addField(this.licenseKeyInput);
		this.form.addField(this.licenseHostField);
		addStylesToInitialFormFields();
		addLicenseGenerateFieldsIfNeeded();

		try {
			populateLicenseKeyInputIfFileExists();
			this.submitButton = new FormPortletButton(SUBMIT).setId("submit_new_license");
			this.cancelButton = new FormPortletButton(CANCEL).setId("cancel");

			this.form.addButton(this.submitButton);
			this.form.addButton(this.cancelButton);
			addChild(this.form, 0, 0);
			this.form.addFormPortletListener(this);
		} catch (IOException e) {
			LH.warning(log, "Exception found while configuring license " + e);
		}
	}

	private void addStylesToInitialFormFields() {
		this.licenseKeyInput.setValue("").setWidth(500).setCssStyle("style.padding=" + FORM_INPUT_FIELD_PADDING);
		this.licenseHostField.setCssStyle("style.marginLeft=22%|style.fontSize=" + FORM_TITLE_TEXT_SIZE);
	}

	private void addLicenseGenerateFieldsIfNeeded() {
		if (SH.isntEmpty(getLicenseAuthUrl())) {
			this.textOr = new FormPortletTitleField("OR").setId("option");
			this.generateLicenseKeyButton = (FormPortletButtonField) new FormPortletButtonField("").setId("generate_license_key");
			this.licenseAuthUrlField = new FormPortletTitleField("from " + licenseAuthUrl);
			this.form.addField(this.textOr);
			this.form.addField(this.generateLicenseKeyButton);
			this.form.addField(this.licenseAuthUrlField);
			addStylesToLicenseGenerateFields();
		}
	}

	private void addStylesToLicenseGenerateFields() {
		this.textOr.setCssStyle("style.marginLeft=33%");
		this.generateLicenseKeyButton.setValue("Generate License Key").setCssStyle("style.marginLeft=35%|style.background=" + BUTTON_FIELD_BACKGROUND);
		this.licenseAuthUrlField.setCssStyle("style.marginLeft=16%|style.fontSize=" + FORM_TITLE_TEXT_SIZE);
	}

	private String getLicenseAuthUrl() {
		return licenseAuthUrl;
	}

	private void populateLicenseKeyInputIfFileExists() throws IOException {
		File target = this.getManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_LICENSE_FILE, new File("./f1license.txt"));
		if (target.length() != 0) {
			String data = SH.trim(IOH.readText(target));
			setOldLicenseKey(data);
			this.licenseKeyInput.setValue(data);
		} else {
			LH.warning(log, "license file does not exist, initializing one");
		}
	}

	private void showNewLicenseForm(String licenseKey) {
		FormPortlet fp = new FormPortlet(generateConfig());
		fp.addField(new FormPortletTextField("New License Key:").setId("licenseKey").setValue(licenseKey).setWidth(620).setDisabled(true)
				.setCssStyle("style.padding=" + FORM_INPUT_FIELD_PADDING));
		FormPortletButtonField button = (FormPortletButtonField) new FormPortletButtonField("").setId("update_license").setValue("Update License Key")
				.setCorrelationData(licenseKey).setCssStyle("style.marginLeft=50%|style.background=" + BUTTON_FIELD_BACKGROUND);
		fp.addField(button);
		fp.addFormPortletListener(this);
		getManager().showDialog("License Key", fp);
	}

	private void showAuthenticationForm() {
		FormPortlet fp = new FormPortlet(generateConfig());
		fp.addField(new FormPortletTextField("Hostname:").setId("hostname").setValue(F1LicenseInfo.getLicenseHost()).setCssStyle("style.padding=" + FORM_INPUT_FIELD_PADDING));
		fp.addField(new FormPortletTitleField("Please enter your credentials below for " + getLicenseAuthUrl() + " account").setCssStyle("style.fontSize=" + FORM_TITLE_TEXT_SIZE));
		fp.addField(new FormPortletTextField("Username:").setId("username").setValue("").setCssStyle("style.padding=" + FORM_INPUT_FIELD_PADDING));
		fp.addField(new FormPortletTextField("Password:").setId("password").setPassword(true).setValue("").setCssStyle("style.padding=" + FORM_INPUT_FIELD_PADDING));
		fp.addField((FormPortletButtonField) new FormPortletButtonField("").setId("request_key").setValue("Request License Key").setWidth(200)
				.setCssStyle("style.background=" + BUTTON_FIELD_BACKGROUND));
		fp.addFormPortletListener(this);
		getManager().showDialog("License Details", fp);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (OH.eq(button.getName(), SUBMIT)) {
			String newLicenseKey = SH.trim((String) getLicenseKey());
			String oldLicenseKey = SH.trim((String) getOldLicenseKey());
			if (SH.isEmpty(newLicenseKey))
				getManager().showAlert("License Key cannot be empty");
			else if (newLicenseKey.equals(oldLicenseKey)) {
				LH.info(log, "License key has not been changed. Skipping writing it to file.");
				getManager().showAlert("Thanks! You need to restart AMI for the license to take effect.");
				this.close();
			} else {
				if (isLicenseValid(newLicenseKey))
					writeToFileNewLicenseKey(newLicenseKey);
				else {
					getManager().showAlert("Invalid License Format!");
					LH.warning(log, "INVALID LICENSE FORMAT: user has entered an invalid license key");
				}
			}
		} else if (OH.eq(button.getName(), CANCEL))
			this.close();
	}
	private void writeToFileNewLicenseKey(String newLicenseKey) {
		try {
			File target = this.getManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_LICENSE_FILE, new File("./f1license.txt"));
			IOH.writeText(target, newLicenseKey);
			getManager().showAlert("Thanks! You need to restart AMI for the license to take effect.");
			this.close();
		} catch (Exception e) {
			LH.warning(log, "Exception found while configuring license " + e);
		}
	}

	private boolean isLicenseValid(String licenseKey) {
		List<String> tokens = SH.splitToList("|", licenseKey);
		if (tokens.size() != 7)
			return false;
		else {
			int licenseStartDate = Caster_Integer.PRIMITIVE.cast(tokens.get(4));
			int licenseEndDate = Caster_Integer.PRIMITIVE.cast(tokens.get(5));
			if (licenseStartDate >= licenseEndDate)
				return false;
		}
		return true;
	}

	public String getLicenseKey() {
		return this.licenseKeyInput.getValue();
	}

	public String getOldLicenseKey() {
		return oldLicenseKey;
	}

	public void setOldLicenseKey(String val) {
		this.oldLicenseKey = val;
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (OH.eq(field.getId(), "generate_license_key")) {
			showAuthenticationForm();
		} else if (field.getId().equals("request_key")) {
			String hostname = SH.trim((String) portlet.getField("hostname").getValue());
			String username = SH.trim((String) portlet.getField("username").getValue());
			String password = SH.trim((String) portlet.getField("password").getValue());
			if (SH.isntEmpty(hostname) && SH.isntEmpty(username) && SH.isntEmpty(password)) {
				try {
					String requestUrl = SH.trim(this.getManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_LICENSE_AUTH_URL, "")) + "/authenticate";
					URL url = new URL(requestUrl);
					String parameters = "host=" + hostname + "&username=" + username + "&password=" + password + "&signin="; // the appName params will be filled in the handler for website.

					byte[] data = parameters.getBytes();
					Map<String, List<String>> returnHeadersSink = new HashMap<String, List<String>>();
					byte[] result = IOH.doPost(url, null, data, returnHeadersSink, true, -1);
					String message = returnHeadersSink.get("message").get(0);

					if (message.equals("success")) {
						String licenseKey = returnHeadersSink.get("licenseKey").get(0);
						this.showNewLicenseForm(licenseKey);
						portlet.close();
					} else
						getManager().showAlert(message);
				} catch (Exception e) {
					getManager().showAlert("Could not connect to " + this.getManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_LICENSE_AUTH_URL, ""), e);
				}
			} else
				getManager().showAlert("Please fill in all the fields");
		} else if (field.getId().equals("update_license")) {
			this.licenseKeyInput.setValue((String) field.getCorrelationData()).setCssStyle("style.boxShadow=0 0 15px #E27025 inset|style.padding=" + FORM_INPUT_FIELD_PADDING);
			portlet.close();
			this.textOr.setVisible(false);
			this.generateLicenseKeyButton.setVisible(false);
			this.licenseAuthUrlField.setVisible(false);
			this.licenseHostField.setVisible(false);
			FormPortletTitleField text = (FormPortletTitleField) new FormPortletTitleField("License Added! \n Click submit to continue").setId("success")
					.setCssStyle("style.marginLeft=16%");

			this.form.addField(text);
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

}
