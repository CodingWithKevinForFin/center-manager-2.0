package com.f1.suite.web.portal.impl.chart;

import java.util.List;
import java.util.Map;

import com.f1.base.Table;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.utils.DoubleArrayList;

public class FastChartPortlet extends AbstractPortlet {
	public static final byte TYPE_SCATTER = 1;
	public static final byte TYPE_SERIES = 2;

	public static final PortletSchema<FastChartPortlet> SCHEMA = new BasicPortletSchema<FastChartPortlet>("Chart", "ChartPortlet", FastChartPortlet.class, false, true);
	private Table data;
	private byte type;
	private int labelColumnLoc;

	//scatter
	private int scatterXColumnLoc;
	private int scatterYColumnLoc;

	//series
	private int[] seriesColumnLocs;

	public FastChartPortlet(PortletConfig portletConfig) {
		super(portletConfig);
	}

	public void setSeriesData(Table data, String labelColumn, String... seriesColumns) {
		this.data = data;
		this.type = TYPE_SERIES;
		int labelColumnLoc = data.getColumn(labelColumn).getLocation();
		int seriesColumnLocs[] = new int[seriesColumns.length];
		for (int i = 0; i < seriesColumns.length; i++)
			this.seriesColumnLocs[i] = data.getColumn(seriesColumns[i]).getLocation();

		onDataChanged();
	}

	public void setScatterData(Table data, String labelColumn, String xColumn, String yColumn) {
		this.data = data;
		this.type = TYPE_SCATTER;
		int labelColumnLoc = data.getColumn(labelColumn).getLocation();
		int scatterXColumnLoc = data.getColumn(xColumn).getLocation();
		int scatterYColumnLoc = data.getColumn(yColumn).getLocation();
		onDataChanged();
	}

	private void onDataChanged() {
		flagPendingAjax();
	}

	@Override
	public void initJs() {
		super.initJs();
		flagPendingAjax();
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible()) {
			getManager().getPendingJs().append("{var t=");
			callJsFunction("getTree").end();
			getManager().getPendingJs().append("}");

		}
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		super.handleCallback(callback, attributes);
	}

	@Override
	public PortletSchema<? extends FastChartPortlet> getPortletSchema() {
		return SCHEMA;
	}

	private static class Scatter {
		private DoubleArrayList xValues;
		private DoubleArrayList yValues;
		private String label;
		private String color;
	}

	private class Series {
		private DoubleArrayList yValues;
		private List<String> xLabels;
		private String label;
		private String color;
	}

}
