package com.f1.ami.web.charts;

import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;

public abstract class AmiWebChartEditRenderingLayerPortlet<T extends AmiWebChartRenderingLayer<?>> extends GridPortlet {

	private T layer;

	public AmiWebChartEditRenderingLayerPortlet(PortletConfig config, T layer) {
		super(config);
		this.layer = layer;
	}

	public T getLayer() {
		return layer;
	}

	public abstract AmiWebEditStylePortlet getEditStylePortlet();

	public void updateDmModelButton() {
	}

}
