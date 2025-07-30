package com.f1.ami.web;

import java.util.Map;

import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletListener;

public class AmiWebViewCallbackConfigurationPortlet extends AmiWebViewConfigurationPortlet implements FormPortletListener {
	final private FormPortletButton submitButton = new FormPortletButton("Import");
	final private AmiWebEditAmiScriptCallbacksPortlet editCallbacksPortlet;
	final private AmiWebEditAmiScriptCallbackPortlet editPortlet;
	final private AmiWebAmiScriptCallback callback;

	public AmiWebViewCallbackConfigurationPortlet(PortletConfig generateConfig, AmiWebEditAmiScriptCallbacksPortlet editCallbacks, AmiWebEditAmiScriptCallbackPortlet editPortlet) {
		super(generateConfig);
		this.editCallbacksPortlet = editCallbacks;
		this.editPortlet = editPortlet;
		this.callback = editPortlet.getCallback();

		this.setConfiguration(this.callback.getConfiguration());
		this.formPortlet.addButton(this.submitButton, 0);
		this.formPortlet.addButton(new FormPortletButton("Cancel"), 1);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			Map<String, Object> config = this.getConfiguration();
			StringBuilder sink = new StringBuilder();
			try {
				String amiLayoutAlias = this.editCallbacksPortlet.getAmiLayoutAlias();
				this.callback.init((AmiWebDmsImpl) this.callback.getReturnSchema().getDatamodel(), amiLayoutAlias, config, sink);
				this.callback.initLinkedVariables();
				this.editPortlet.loadFrom(this.callback);

				close();
			} catch (RuntimeException e) {
				getManager().showAlert("Error importing datamodel. See More Details", e);
			}
		} else {
			close();
		}
	}
}
