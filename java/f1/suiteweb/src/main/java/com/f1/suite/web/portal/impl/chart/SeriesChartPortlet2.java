package com.f1.suite.web.portal.impl.chart;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.MH;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarColumnDouble;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class SeriesChartPortlet2 extends AbstractPortlet {

	public static final byte STYLE_BAR = 1;
	public static final byte STYLE_LINE = 2;
	public static final byte STYLE_AREA = 3;
	public static final byte STYLE_BAR_STACKED = 4;
	public static final byte STYLE_AREA_STACKED = 5;

	public static final String OPTION_RANGE_LABEL_SUFFIX = "yLblSfx";
	public static final String OPTION_Y_MAX = "yMax";
	public static final String OPTION_Y_MIN = "yMin";
	public static final String OPTION_X_MAX = "xMax";
	public static final String OPTION_X_MIN = "xMin";
	public static final String OPTION_H_GRID_HIDE = "yGridHide";//true or false
	public static final String OPTION_V_GRID_HIDE = "xGridHide";//true or false
	public static final String OPTION_BORDER_HIDE = "borderHide";//true or false
	public static final String OPTION_RANGE_LABEL_HIDE = "yLblHide";//true or false
	public static final String OPTION_DOMAIN_LABEL_HIDE = "xLblHide";//true or false
	public static final String OPTION_KEY_POSITION = "keyPos";//below
	public static final String OPTION_TITLE = "title";
	public static final String OPTION_CHART_TEXT = "chartText";
	public static final String OPTION_CHART_TEXT_FONT = "chartTextFont";
	public static final String OPTION_CHART_TEXT_STYLE = "chartTextStyle";

	public static final Object POSITION_BELOW = "below";
	private static final String DOMAIN_ID = "";

	private ColumnarTable data = new ColumnarTable(new Class[] { String.class }, new String[] { DOMAIN_ID });

	public static final PortletSchema<SeriesChartPortlet2> SCHEMA = new BasicPortletSchema<SeriesChartPortlet2>("Chart", "ChartPortlet", SeriesChartPortlet2.class, false, true);
	private byte style;

	public SeriesChartPortlet2(PortletConfig portletConfig) {
		super(portletConfig);
		this.style = STYLE_LINE;
	}

	public void addSeries(String id, String label) {
		data.addColumn(double.class, id);
		series.put(id, new Series(id, series.size(), label, WebHelper.getUniqueColor(series.size())));
	}

	public void addRow(String name, double[] values) {
		Object[] row = new Object[values.length + 1];
		row[0] = name;
		for (int i = 0; i < values.length; i++)
			row[i + 1] = values[i];
		data.getRows().addRow(row);
	}

	public void setSeriesColor(String name, String color) {
		getSeries(name).setColor(color);
	}
	public void setSeriesLabel(String name, String label) {
		getSeries(name).setLabel(label);
	}

	public void setStyle(byte style) {
		this.style = style;
		onDataChanged();
	}

	private Map<String, Object> options = new HashMap<String, Object>();

	public Object addOption(String key, Object value) {
		onDataChanged();
		return options.put(key, value);
	}
	public Object removeOption(String key) {
		onDataChanged();
		return options.remove(key);
	}
	public void clearOptions() {
		this.options.clear();
		onDataChanged();
	}
	public Object getOption(String option) {
		return options.get(option);
	}
	public Set<String> getOptions() {
		return options.keySet();
	}

	private final Map<String, Series> series = new LinkedHashMap<String, Series>();

	private Series getSeries(String id) {
		return series.get(id);
	}

	private static String generateColor(int idx) {
		return WebHelper.getUniqueColor(idx);
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
			switch (style) {
				case STYLE_BAR:
					func.addParamQuoted("BAR");
					break;
				case STYLE_BAR_STACKED:
					func.addParamQuoted("BAR_STACKED");
					break;
				case STYLE_LINE:
					func.addParamQuoted("LINE");
					break;
				case STYLE_AREA:
					func.addParamQuoted("AREA");
					break;
				case STYLE_AREA_STACKED:
					func.addParamQuoted("AREA_STACKED");
					break;
				default:
					throw new RuntimeException("bad chart series style: " + style);
			}
			JsonBuilder json = func.startJson();
			json.startMap();
			json.addKey("domains");
			json.startList();
			ColumnarColumn domainCol = (ColumnarColumn) data.getColumn(DOMAIN_ID);
			int size = data.getSize();
			for (int j = 0; j < size; j++)
				json.addEntryQuoted(domainCol.getValue(j));
			json.endList();
			json.addKey("series");
			json.startMap();
			int i = 0;
			for (Entry<String, Series> s : this.series.entrySet()) {
				Series val = s.getValue();
				json.addKey("s" + i++);
				json.startMap();
				json.addKeyValueQuoted("color", val.color);
				json.addKeyValueQuoted("label", val.label);
				json.addKey("values");
				json.startList();
				ColumnarColumnDouble col = (ColumnarColumnDouble) data.getColumn(val.columnId);
				for (int j = 0; j < size; j++) {
					if (col.isNull(j))
						json.addEntryQuoted((String) null);
					else {
						double value = col.getDouble(j);
						if (MH.isntNumber(value))
							json.addEntryQuoted((String) null);
						else
							json.addEntry(value);
					}
				}
				json.endList();
				json.endMap();
			}
			json.endMap();
			json.endMap();
			json.close();
			JsonBuilder optionsJson = func.startJson();
			optionsJson.addQuoted(options);
			optionsJson.close();
			func.end();
		}
	}
	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("userClick".equals(callback)) {
		} else
			super.handleCallback(callback, attributes);
	}

	@Override
	public PortletSchema<? extends SeriesChartPortlet2> getPortletSchema() {
		return SCHEMA;
	}

	public static class Series {
		private String label;
		private String color;
		private int position;
		private String columnId;

		public Series(String columnId, int position, String label, String color) {
			this.position = position;
			this.label = label;
			this.color = color;
			this.columnId = columnId;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public void setColor(String color) {
			this.color = color;
		}
	}

	public void clear() {
		this.series.clear();
		this.data.clear();
		while (this.data.getColumnsCount() > 1)
			this.data.removeColumn(1);
		onDataChanged();
	}

	public Set<String> getSeries() {
		return series.keySet();
	}

	public Series removeSeries(String name) {
		Series r = series.remove(name);
		if (r != null)
			onDataChanged();
		return r;
	}
	public String getSeriesColor(String seriesId) {
		return series.get(seriesId).color;
	}
	public String getSeriesLabel(String seriesId) {
		return series.get(seriesId).label;
	}
	public byte getStyle() {
		return style;
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		addOption(OPTION_TITLE, title);
	}

	public Map<String, Object> getOptionsMap() {
		return this.options;
	}

	public void setOptionsMap(Map<String, Object> options) {
		this.options.clear();
		if (options != null)
			this.options.putAll(options);
	}

}
