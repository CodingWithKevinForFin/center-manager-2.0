package com.f1.suite.web.portal.impl.chart;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;

public class SeriesChartPortlet extends AbstractPortlet {

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

	private boolean allowConfig = false;
	private String chartText;

	public static final PortletSchema<SeriesChartPortlet> SCHEMA = new BasicPortletSchema<SeriesChartPortlet>("Chart", "ChartPortlet", SeriesChartPortlet.class, false, true);
	private final HashMap<String, Integer> domains2EntriesCount = new LinkedHashMap<String, Integer>();
	private byte style;

	public SeriesChartPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		this.style = STYLE_LINE;
		this.chartText = "";
	}

	public void setSeriesColor(String name, String color) {
		getSeries(name).color = color;
	}
	public void setSeriesLabel(String name, String label) {
		getSeries(name).label = label;
	}
	public void setSeriesVisible(String name, boolean isVisible) {
		getSeries(name).setVisible(isVisible);
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

	public void addSeriesData(Table data, String domainColumn, String... seriesColumns) {
		int xColumnLoc = data.getColumn(domainColumn).getLocation();
		for (String seriesColumn : seriesColumns) {
			final int colLoc = data.getColumn(seriesColumn).getLocation();
			final Series series = getSeries(seriesColumn);
			for (Row row : data.getRows()) {
				String domainName = row.getAt(xColumnLoc, Caster_String.INSTANCE);
				int cnt = OH.noNull(domains2EntriesCount.get(domainName), 0);
				domains2EntriesCount.put(domainName, cnt + seriesColumn.length());
			}
			for (Row row : data.getRows()) {
				String domainName = row.getAt(xColumnLoc, Caster_String.INSTANCE);
				Double value = row.getAt(colLoc, Caster_Double.INSTANCE);
				if (value != null)
					series.add(domainName, value);
			}
		}
		onDataChanged();
	}

	private final Map<String, Series> series = new LinkedHashMap<String, Series>();

	public void addPoint(String seriesName, String domain, double y) {
		getSeries(seriesName).add(domain, y);
		int cnt = OH.noNull(domains2EntriesCount.get(domain), 0);
		domains2EntriesCount.put(domain, cnt + 1);
		onDataChanged();
	}
	public void setVisible(String seriesName, boolean isVisible) {
		getSeries(seriesName).setVisible(isVisible);
	}

	private Series getSeries(String key) {
		Series r = series.get(key);
		if (r == null)
			series.put(key, r = new Series(key, generateColor(series.size())));
		return r;
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
			json.addQuoted(domains2EntriesCount.keySet());
			json.addKey("series");
			json.startMap();
			int i = 0;
			for (Entry<String, Series> s : this.series.entrySet()) {

				Series val = s.getValue();
				if (val.isVisible() == false)
					continue;
				json.addKey("s" + i++);
				json.startMap();
				json.addKeyValueQuoted("color", val.color);
				json.addKeyValueQuoted("label", val.label);
				json.addKey("values");
				json.startList();
				for (String domain : domains2EntriesCount.keySet()) {
					json.addEntry(val.xyValues.get(domain));
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
			int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			if (allowConfig) {
				SeriesChartConfigFormPortlet p = new SeriesChartConfigFormPortlet(generateConfig(), this);
				getManager().showDialog("chart config", p);
			}
		} else
			super.handleCallback(callback, attributes);
	}

	@Override
	public PortletSchema<? extends SeriesChartPortlet> getPortletSchema() {
		return SCHEMA;
	}

	private static class Series {
		private Map<String, Double> xyValues = new HashMap<String, Double>();
		private String label;
		private String color;
		private boolean isVisible;

		public Series(String label, String color) {
			this.label = label;
			this.color = color;
			this.setVisible(true);
		}
		public void add(String x, double y) {
			xyValues.put(x, y);
		}
		public boolean isVisible() {
			return isVisible;
		}
		public void setVisible(boolean isVisible) {
			this.isVisible = isVisible;
		}
	}

	public void clear() {
		this.domains2EntriesCount.clear();
		this.series.clear();
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
	public boolean getSeriesVisible(String seriesId) {
		return series.get(seriesId).isVisible();
	}
	public byte getStyle() {
		return style;
	}

	public boolean getAllowConfig() {
		return allowConfig;
	}

	public void setAllowConfig(boolean allowConfig) {
		this.allowConfig = allowConfig;
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		addOption(OPTION_TITLE, title);
	}

	public String getChartText() {
		return chartText;
	}

	public void setChartText(String chartText) {
		this.chartText = chartText;
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
