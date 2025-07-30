package com.f1.ami.web;

import java.io.File;
import java.util.Map;

import com.f1.base.F1LicenseInfo;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.IOH;

public class AmiWebLicenseInfoPortlet extends GridPortlet implements FormPortletListener {

	private FormPortlet form;
	private FormPortletTextField licenseHost;
	private FormPortletTextField licenseApps;
	private FormPortletTextField licenseStartDate;
	private FormPortletTextField licenseEndDate;
	private FormPortletTextField licenseText;
	private FormPortletTextField licenseLocation;
	private FormPortletButton closeBtn;

	public AmiWebLicenseInfoPortlet(PortletConfig config, File licenseFile) {
		super(config);
		this.form = new FormPortlet(generateConfig());
		this.licenseHost = new FormPortletTextField("Host").setValue(F1LicenseInfo.getLicenseHost());
		this.licenseApps = new FormPortletTextField("Applications").setValue(F1LicenseInfo.getLicenseApp());
		this.licenseStartDate = new FormPortletTextField("Start Date").setValue(F1LicenseInfo.getLicenseStartDate());
		this.licenseEndDate = new FormPortletTextField("End Date").setValue(F1LicenseInfo.getLicenseEndDate());
		this.licenseText = new FormPortletTextField("Key").setValue(F1LicenseInfo.getLicenseText()).setWidth(600);
		this.licenseLocation = new FormPortletTextField("Location").setValue(IOH.getFullPath(licenseFile)).setWidth(600);
		this.closeBtn = new FormPortletButton("Close");

		this.form.addField(licenseHost);
		this.form.addField(licenseApps);
		this.form.addField(licenseStartDate);
		this.form.addField(licenseEndDate);
		this.form.addField(licenseText);
		this.form.addField(licenseLocation);
		this.form.addButton(closeBtn);
		addChild(this.form);
		this.form.addFormPortletListener(this);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.closeBtn)
			close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

}
