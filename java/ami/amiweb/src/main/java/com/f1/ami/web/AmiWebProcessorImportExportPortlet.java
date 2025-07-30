package com.f1.ami.web;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortletListener;

public class AmiWebProcessorImportExportPortlet extends AmiWebViewConfigurationPortlet implements FormPortletListener {

	public AmiWebProcessorImportExportPortlet(PortletConfig config, AmiWebRealtimeProcessor target) {
		super(config);
		if (target != null)
			this.setConfiguration(target.getService().getWebManagers().exportConfiguration(target));
	}

}
