package com.larkinpoint.salestool;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.chart.Basic3dPortlet;

public class LarkinLogoPortlet extends Basic3dPortlet {

	public LarkinLogoPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		setPosition(15, 45, 15);

		int col1 = 0x434d90;
		int col2 = 0x76a9d1;
		int x = 60, y = 60;
		addBox(0 - x, 30 - y, 0, 30, 90, 30, col1);
		addBox(30 - x, 0 - y, 0, 90, 30, 30, col2);
		addBox(90 - x, 60 - y, 0, 30, 30, 30, col2);
		addBox(60 - x, 90 - y, 0, 30, 30, 30, col1);
		addTriangle(new Triangle(0 - x, 0 - y, 0, col1, 0 - x, 30 - y, 0, col1, 30 - x, 30 - y, 0, col1));
		addTriangle(new Triangle(0 - x, 0 - y, 0, col2, 30 - x, 0 - y, 0, col2, 30 - x, 30 - y, 0, col2));
		addTriangle(new Triangle(0 - x, 0 - y, 30, col1, 0 - x, 30 - y, 30, col1, 30 - x, 30 - y, 30, col1));
		addTriangle(new Triangle(0 - x, 0 - y, 30, col2, 30 - x, 0 - y, 30, col2, 30 - x, 30 - y, 30, col2));

		addTriangle(new Triangle(90 - x, 90 - y, 0, col1, 90 - x, 120 - y, 0, col1, 120 - x, 120 - y, 0, col1));
		addTriangle(new Triangle(90 - x, 90 - y, 0, col2, 120 - x, 90 - y, 0, col2, 120 - x, 120 - y, 0, col2));
		addTriangle(new Triangle(90 - x, 90 - y, 30, col1, 90 - x, 120 - y, 30, col1, 120 - x, 120 - y, 30, col1));
		addTriangle(new Triangle(90 - x, 90 - y, 30, col2, 120 - x, 90 - y, 30, col2, 120 - x, 120 - y, 30, col2));

		addTriangle(new Triangle(0 - x, 0 - y, 0, col1, 0 - x, 30 - y, 0, col1, 0 - x, 30 - y, 30, col1));
		addTriangle(new Triangle(0 - x, 0 - y, 0, col1, 0 - x, 0 - y, 30, col1, 0 - x, 30 - y, 30, col1));

		addTriangle(new Triangle(0 - x, 0 - y, 0, col2, 30 - x, 0 - y, 0, col2, 30 - x, 0 - y, 30, col2));
		addTriangle(new Triangle(0 - x, 0 - y, 0, col2, 0 - x, 0 - y, 30, col2, 30 - x, 0 - y, 30, col2));

		addTriangle(new Triangle(120 - x, 90 - y, 0, col2, 120 - x, 120 - y, 0, col2, 120 - x, 120 - y, 30, col2));
		addTriangle(new Triangle(120 - x, 90 - y, 0, col2, 120 - x, 90 - y, 30, col2, 120 - x, 120 - y, 30, col2));

		addTriangle(new Triangle(90 - x, 120 - y, 0, col1, 120 - x, 120 - y, 0, col1, 120 - x, 120 - y, 30, col1));
		addTriangle(new Triangle(90 - x, 120 - y, 0, col1, 90 - x, 120 - y, 30, col1, 120 - x, 120 - y, 30, col1));
		//addBox(30, 0, 0, 90, 30, 30, 0x76a9d1);
		//addBox(-90, -60, -30, 30, 60, 30, 0x64886e);
		//addBox(-60, -90, -30, 30, 90, 30, 0x64886e);

		//addBox(-30, -150, -30, 30, 150, 30, 0xe5234);
		//addBox(0, -30, -30, 60, 30, 30, 0xe5234);
		//addBox(0, -90, -30, 30, 60, 30, 0x64886e);
		//addBox(30, -60, -30, 30, 30, 30, 0x64886e);
		//addBox(60, -40, -30, 30, 40, 30, 0x64886e);
		setBackgroundColor("");
		setBackgroundClass("larkin_logo");
		//	addOption("centerY", "-30");
		//	addOption("centerX", "-170");
		setCenterY(-50);
		setCenterX(0);
		//addOption("rotY", "180");
		//addOption("rotX", "180");
		//addOption("rotZ", "180");
		setRotY(0);
		setRotX(0);
		setRotZ(45);
		setZoom(.3);

	}
	private void addBox(int x1, int y1, int z1, int w, int h, int dep, int color) {
		int x2 = x1 + w;
		int y2 = y1 + h;
		int z2 = z1 + dep;
		addTriangle(new Triangle(x1, y1, z1, color, x2, y1, z1, color, x2, y2, z1, color));
		addTriangle(new Triangle(x1, y1, z1, color, x1, y2, z1, color, x2, y2, z1, color));

		addTriangle(new Triangle(x1, y1, z2, color, x2, y1, z2, color, x2, y2, z2, color));
		addTriangle(new Triangle(x1, y1, z2, color, x1, y2, z2, color, x2, y2, z2, color));

		addTriangle(new Triangle(x1, y1, z1, color, x1, y1, z2, color, x1, y2, z2, color));
		addTriangle(new Triangle(x1, y1, z1, color, x1, y2, z1, color, x1, y2, z2, color));

		addTriangle(new Triangle(x2, y1, z1, color, x2, y1, z2, color, x2, y2, z2, color));
		addTriangle(new Triangle(x2, y1, z1, color, x2, y2, z1, color, x2, y2, z2, color));

		addTriangle(new Triangle(x1, y1, z1, color, x1, y1, z2, color, x2, y1, z2, color));
		addTriangle(new Triangle(x1, y1, z1, color, x2, y1, z1, color, x2, y1, z2, color));

		addTriangle(new Triangle(x1, y2, z1, color, x1, y2, z2, color, x2, y2, z2, color));
		addTriangle(new Triangle(x1, y2, z1, color, x2, y2, z1, color, x2, y2, z2, color));
	}

	public static class Builder extends AbstractPortletBuilder<LarkinLogoPortlet> {

		private static final String ID = "larkin3dLogo";

		public Builder() {
			super(LarkinLogoPortlet.class);
		}

		@Override
		public LarkinLogoPortlet buildPortlet(PortletConfig portletConfig) {
			LarkinLogoPortlet portlet = new LarkinLogoPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Larkin 3d Logo";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

}
