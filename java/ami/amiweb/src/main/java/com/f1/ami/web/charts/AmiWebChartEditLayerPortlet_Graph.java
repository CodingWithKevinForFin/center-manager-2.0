package com.f1.ami.web.charts;

import com.f1.suite.web.portal.PortletConfig;

public class AmiWebChartEditLayerPortlet_Graph extends AmiWebChartEditLayerPortlet<AmiWebChartSeries_Graph> {

	public AmiWebChartEditLayerPortlet_Graph(PortletConfig config, AmiWebChartRenderingLayer_Graph layer, AmiWebChartSeries_Graph series) {
		super(config, layer, series, new AmiWebChartEditRenderingLayerPortlet_Graph(config.getPortletManager().generateConfig(), layer.getPlot(), layer));
		addEditor(new AmiWebChartEditSeriesPortlet_Graph(generateConfig()), "advanced_chart.png", "Advanced");
		addEditor(new AmiWebChartEditSeriesPortlet_Graph_Bar(generateConfig(), true), "bar_chart_v.png", "V Bar");
		addEditor(new AmiWebChartEditSeriesPortlet_Graph_Bar(generateConfig(), false), "bar_chart_h.png", "H Bar");
		addEditor(new AmiWebChartEditSeriesPortlet_Graph_Scatter(generateConfig()), "scatter_plot.png", "Scatter");
		addEditor(new AmiWebChartEditSeriesPortlet_Graph_Line(generateConfig()), "line_chart.png", "Line");
		addEditor(new AmiWebChartEditSeriesPortlet_Graph_Area(generateConfig()), "area_chart.png", "Area");
	}

}
