package com.f1.ami.web;

import com.f1.suite.web.portal.PortletConfig;

public class AmiWebTabSettingsPortlet extends AmiWebPanelSettingsPortlet {

	public AmiWebTabSettingsPortlet(PortletConfig config, AmiWebTabPortlet portlet) {
		super(config, portlet);

		getTitleField().setVisible(false);

	}

}
