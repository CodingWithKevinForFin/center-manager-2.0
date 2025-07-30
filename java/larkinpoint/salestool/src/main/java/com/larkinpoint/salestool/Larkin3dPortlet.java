package com.larkinpoint.salestool;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.chart.SurfaceChartPortlet;

public class Larkin3dPortlet extends SurfaceChartPortlet {

	public Larkin3dPortlet(PortletConfig portletConfig) {
		super(portletConfig);
	}

	public static class Builder extends AbstractPortletBuilder<Larkin3dPortlet> {

		private static final String ID = "larkin3dChart";

		public Builder() {
			super(Larkin3dPortlet.class);
		}

		@Override
		public Larkin3dPortlet buildPortlet(PortletConfig portletConfig) {
			Larkin3dPortlet portlet = new Larkin3dPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Larkin 3d Chart";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

}
