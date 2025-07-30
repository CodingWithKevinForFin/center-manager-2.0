package com.f1.ami.web.charts;

import com.f1.suite.web.portal.PortletConfig;

public class AmiWebChartEditLayerPortlet_RadialGraph extends AmiWebChartEditLayerPortlet<AmiWebChartSeries_RadialGraph> {

	public AmiWebChartEditLayerPortlet_RadialGraph(PortletConfig config, AmiWebChartRenderingLayer_RadialGraph layer, AmiWebChartSeries_RadialGraph series) {
		super(config, layer, series, new AmiWebChartEditRenderingLayerPortlet_RadialGraph(config.getPortletManager().generateConfig(), layer.getPlot(), layer));
		addEditor(new AmiWebChartEditSeriesPortlet_RadialGraph(generateConfig()), "advanced_radial_chart.png", "Advanced");
		addEditor(new AmiWebChartEditSeriesPortlet_RadialGraph_Pie(generateConfig()), "pie_chart.png", "Pie");
		addEditor(new AmiWebChartEditSeriesPortlet_RadialGraph_Bar(generateConfig()), "radial_bar_chart.png", "Bar");
		addEditor(new AmiWebChartEditSeriesPortlet_RadialGraph_Speedometer(generateConfig()), "speedometer.png", "Speedometer");
	}

}
