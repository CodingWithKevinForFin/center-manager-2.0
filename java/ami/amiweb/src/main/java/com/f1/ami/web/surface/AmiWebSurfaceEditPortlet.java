package com.f1.ami.web.surface;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;

public class AmiWebSurfaceEditPortlet extends GridPortlet {

	final private AmiWebSurfacePortlet surface;
	final private FormPortlet form;

	public AmiWebSurfaceEditPortlet(PortletConfig config, AmiWebSurfacePortlet amiWebSurfacePortlet) {
		super(config);
		this.surface = amiWebSurfacePortlet;
		form = new FormPortlet(generateConfig());
		addChild(form, 0, 0);
	}

}
