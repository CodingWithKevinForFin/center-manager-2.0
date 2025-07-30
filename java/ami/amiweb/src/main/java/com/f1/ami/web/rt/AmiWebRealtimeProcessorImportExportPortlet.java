package com.f1.ami.web.rt;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.AmiWebViewConfigurationPortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.utils.LH;

public class AmiWebRealtimeProcessorImportExportPortlet extends AmiWebViewConfigurationPortlet implements FormPortletListener {
	private static final Logger log = LH.get();
	final AmiWebService service;
	final private AmiWebManagers dmManager;
	final private FormPortletButton submitButton = new FormPortletButton("Import");

	public AmiWebRealtimeProcessorImportExportPortlet(PortletConfig config) {
		super(config);

		this.formPortlet.addButton(this.submitButton, 0);
		this.formPortlet.addButton(new FormPortletButton("Cancel"), 1);

		this.service = AmiWebUtils.getService(this.getManager());
		this.dmManager = this.service.getWebManagers();
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			Map<String, Object> config = this.getConfiguration();
			try {
				String alias = "";//TODO:
				dmManager.importConfiguration("", config);
				close();
			} catch (RuntimeException e) {
				LH.warning(log, "Error importing datamodel", e);
				getManager().showAlert("Error importing datamodel. See More Details", e);
			}
		} else {
			close();
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		super.onFieldValueChanged(portlet, field, attributes);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

}
