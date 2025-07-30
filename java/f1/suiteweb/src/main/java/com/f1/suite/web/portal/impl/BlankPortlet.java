package com.f1.suite.web.portal.impl;

import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;

public class BlankPortlet extends AbstractPortlet {

	public BlankPortlet(PortletConfig manager) {
		super(manager);
	}

	public static final PortletSchema<BlankPortlet> SCHEMA = new BasicPortletSchema<BlankPortlet>("Blank", "BlankPortlet", BlankPortlet.class, false, true);

	@Override
	public PortletSchema getPortletSchema() {
		return SCHEMA;
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("showAddPortletDialog".equals(callback)) {
			PortletBuilderPortlet pbp = new PortletBuilderPortlet(generateConfig(), false);
			pbp.setPortletIdOfParentToAddPortletTo(getPortletId());
			getManager().showDialog("Add Portlet", pbp);
		} else
			super.handleCallback(callback, attributes);
	}

	public static class Builder extends AbstractPortletBuilder<BlankPortlet> {

		public static final String ID = "blank";

		public Builder() {
			super(BlankPortlet.class);
			setIcon("portlet_icon_blank");
		}

		@Override
		public BlankPortlet buildPortlet(PortletConfig portletConfig) {
			BlankPortlet r = new BlankPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Blank";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}
}
