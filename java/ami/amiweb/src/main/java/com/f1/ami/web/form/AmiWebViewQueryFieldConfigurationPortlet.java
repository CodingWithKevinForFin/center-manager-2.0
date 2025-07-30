package com.f1.ami.web.form;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.web.AmiWebViewConfigurationPortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;

public class AmiWebViewQueryFieldConfigurationPortlet extends AmiWebViewConfigurationPortlet {
	final private FormPortletButton submitButton = new FormPortletButton("Import");
	final private Map<String, Object> configuration;
	final private AmiWebQueryFieldWizardPortlet editor;

	public AmiWebViewQueryFieldConfigurationPortlet(PortletConfig config, AmiWebQueryFieldWizardPortlet editor) {
		super(config);
		this.editor = editor;
		this.configuration = new HashMap<String, Object>();
		this.editor.exportQueryFieldConfig(configuration);

		this.setConfiguration(this.configuration);
		this.formPortlet.addButton(this.submitButton, 0);
		this.formPortlet.addButton(new FormPortletButton("Cancel"), 1);
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			Map<String, Object> config = this.getConfiguration();
			boolean success = this.editor.importQueryFieldConfig(config);
			if (success)
				close();
			else
				getManager().showAlert("Error importing field ");
		} else {
			close();
		}
	}

}
