package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;

public class SaveLayoutPortlet extends FormPortlet {

	private FormPortletTextAreaField textArea;

	public SaveLayoutPortlet(PortletConfig config) {
		super(config);
		this.textArea = addField(new FormPortletTextAreaField("Data"));
	}

	public void setText(String text) {
		this.textArea.setValue(text);
	}

	public static class Builder extends AbstractPortletBuilder<SaveLayoutPortlet> {

		public static final String ID = "SaveLayout";

		public Builder() {
			super(SaveLayoutPortlet.class);
			setIcon("portlet_icon_form");
		}

		@Override
		public SaveLayoutPortlet buildPortlet(PortletConfig portletConfig) {
			SaveLayoutPortlet r = new SaveLayoutPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "SaveLayout";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

}
