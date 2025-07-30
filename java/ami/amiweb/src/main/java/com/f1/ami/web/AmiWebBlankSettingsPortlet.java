package com.f1.ami.web;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;

public class AmiWebBlankSettingsPortlet extends AmiWebPanelSettingsPortlet {

	private FormPortletButtonField dmButton;
	private final AmiWebBlankPortlet blankPortlet;

	public AmiWebBlankSettingsPortlet(PortletConfig config, AmiWebBlankPortlet portlet) {
		super(config, portlet);
		this.blankPortlet = portlet;
		getTitleField().setVisible(false);

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		super.onButtonPressed(portlet, button);
	}
}
