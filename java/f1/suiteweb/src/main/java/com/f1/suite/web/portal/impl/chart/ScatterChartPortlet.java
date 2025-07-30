package com.f1.suite.web.portal.impl.chart;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;

public class ScatterChartPortlet extends AbstractPortlet {

	public static final PortletSchema<ScatterChartPortlet> SCHEMA = new BasicPortletSchema<ScatterChartPortlet>("Chart", "ChartPortlet", ScatterChartPortlet.class, false, true);

	public ScatterChartPortlet(PortletConfig portletConfig) {
		super(portletConfig);
	}

	public void addScatterData(Table data, String labelColumn, String xColumn, String yColumn) {
		int labelColumnLoc = data.getColumn(labelColumn).getLocation();
		int xColumnLoc = data.getColumn(xColumn).getLocation();
		int yColumnLoc = data.getColumn(yColumn).getLocation();
		for (Row r : data.getRows())
			addPoint(r.getAt(labelColumnLoc, Caster_String.INSTANCE), r.getAt(xColumnLoc, Caster_Double.INSTANCE), r.getAt(yColumnLoc, Caster_Double.INSTANCE));
	}

	private final Map<String, Series> series = new LinkedHashMap<String, Series>();

	public void addPoint(String seriesName, double x, double y) {
		getSeries(seriesName).add(x, y);
		onDataChanged();
	}

	public void clear() {
		this.series.clear();
		flagPendingAjax();
	}

	private Series getSeries(String key) {
		return series.get(key);
	}
	public void addSeries(String id, String label) {
		this.series.put(id, new Series(label));
		flagPendingAjax();
	}
	public void setSeriesColor(String id, String color) {
		getSeries(id).setColor(color);
		flagPendingAjax();
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
			JsFunction func = callJsFunction("setData");
			func.addParamQuoted("SCATTER");
			JsonBuilder json = func.startJson();
			json.startMap();
			for (Entry<String, Series> s : this.series.entrySet()) {
				Series val = s.getValue();
				final DoubleArrayList xyValues = val.xyValues;
				json.addKey(s.getKey());
				json.startMap();
				json.addKey("xy");
				json.startList();
				for (int i = 0, l = xyValues.size(); i < l; i++) {
					json.addEntry(xyValues.getDouble(i));
				}
				json.endList();
				json.addKeyValueQuoted("color", val.color);
				json.addKeyValueQuoted("label", val.label);
				json.endMap();
			}
			json.endMap();
			json.close();
			func.end();
		}
	}
	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		super.handleCallback(callback, attributes);
	}

	@Override
	public PortletSchema<? extends ScatterChartPortlet> getPortletSchema() {
		return SCHEMA;
	}

	private static class Series {
		public Series(String label) {
			this.label = label;
		}
		public void setColor(String color) {
			this.color = color;
		}
		public void add(double x, double y) {
			xyValues.add(x);
			xyValues.add(y);
		}

		private DoubleArrayList xyValues = new DoubleArrayList();
		private String label;
		private String color;
	}

}
