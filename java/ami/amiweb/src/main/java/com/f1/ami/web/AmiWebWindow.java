package com.f1.ami.web;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;

public class AmiWebWindow extends Window {

	public AmiWebWindow(AmiWebInnerDesktopPortlet owner, String name, Portlet portlet, int zindex, int flags) {
		super(owner, name, portlet, zindex, flags);
	}

	@Override
	public AmiWebInnerDesktopPortlet getDesktop() {
		return (AmiWebInnerDesktopPortlet) super.getDesktop();
	}

}
