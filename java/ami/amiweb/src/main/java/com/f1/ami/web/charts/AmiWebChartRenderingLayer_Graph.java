package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.charts.AmiWebChartSeries.Grouping;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_RenderingLayer_Graph;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.utils.CH;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.IterableIterator;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebChartRenderingLayer_Graph extends AmiWebChartRenderingLayer<AmiWebChartSeries_Graph> implements ConfirmDialogListener, AmiWebStyledPortlet {

	public static final char TYPE_BAR = 'B';
	public static final char TYPE_SCATTER = 'S';
	private String vGridColor = "#AAAAAA";
	private String hGridColor = "#AAAAAA";
	private String vMidGridColor = null;
	private String hMidGridColor = null;
	private String vMajorGridColor = null;
	private String hMajorGridColor = null;
	private String borderColor = "#444444";

	private int vGridSize;
	private int hGridSize;
	private int vMidGridSize;
	private int hMidGridSize;
	private int vMajorGridSize;
	private int hMajorGridSize;

	private IntKeyMap<List<AmiWebChartShape>> groupId2Shapes = new IntKeyMap<List<AmiWebChartShape>>();

	public AmiWebChartRenderingLayer_Graph(AmiWebChartPlotPortlet parentPlot) {
		super(parentPlot);

		getStylePeer().initStyle();
	}

	public void drainJavascript() {
	}

	public String getType() {
		return "Chart";
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		List<Map<String, Object>> layers = new ArrayList<Map<String, Object>>();
		AmiWebChartSeries layer = this.getSeries();
		layers.add(layer.getConfiguration());
		r.put("layers", layers);
		return r;
	}
	public void init(String alias, Map<String, Object> layer) {
		super.init(alias, layer);
		List<Map<String, Object>> series = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, layer, "layers");
		for (Map<String, Object> ser : series) {
			AmiWebChartSeries_Graph l = new AmiWebChartSeries_Graph(getPlot().getService(), this.getChart(), this.getDataModelSchema(), this);
			l.init(ser);
			this.getChart().registerUsedId(l.getId());
			setSeries(l);
		}
	}

	public static final List<Double> ZEROS = CH.l(OH.valueOf(0d));

	private void updateLookAndFeel(AmiWebImageGenerator_Chart r) {
		AmiWebChartAxisPortlet xAxis = getXAxis();
		AmiWebChartAxisPortlet yAxis = getYAxis();
		xAxis.ensureDataProcessed();
		yAxis.ensureDataProcessed();

		r.setBorderColor(parseColor(borderColor));
		r.setOpacity(getOpacity());

		boolean v = false;
		if (vGridSize > 0 && SH.is(vGridColor)) {
			r.setVGridColor(parseColor(vGridColor));
			r.setVGridSize(vGridSize);
			v = true;
		}
		if (vMidGridSize > 0 && SH.is(vMidGridColor)) {
			r.setVMidGridColor(parseColor(vMidGridColor));
			r.setVMidGridSize(vMidGridSize);
			v = true;
		}
		if (vMajorGridSize > 0 && SH.is(vMajorGridColor)) {
			r.setVMajorGridColor(parseColor(vMajorGridColor));
			r.setVMajorGridSize(vMajorGridSize);
			v = true;
		}
		if (v) {
			r.setVMajorGrid(yAxis.getMajorTicks());
			r.setVGrid(yAxis.getLabelTicks());
		}
		boolean h = false;
		if (hGridSize > 0 && SH.is(hGridColor)) {
			r.setHGridColor(parseColor(hGridColor));
			r.setHGridSize(hGridSize);
			h = true;
		}
		if (hMidGridSize > 0 && SH.is(hMidGridColor)) {
			r.setHMidGridColor(parseColor(hMidGridColor));
			r.setHMidGridSize(hMidGridSize);
			h = true;
		}
		if (hMajorGridSize > 0 && SH.is(hMajorGridColor)) {
			r.setHMajorGridColor(parseColor(hMajorGridColor));
			r.setHMajorGridSize(hMajorGridSize);
			h = true;
		}
		if (h) {
			r.setHMajorGrid(xAxis.getMajorTicks());
			r.setHGrid(xAxis.getLabelTicks());
		}
	}

	@Override
	public AmiWebImageGenerator createImageGenerator() {
		this.groupId2Shapes.clear();
		AmiWebImageGenerator_Chart r = new AmiWebImageGenerator_Chart();
		updateLookAndFeel(r);
		AmiWebChartAxisPortlet xAxis = getXAxis();
		AmiWebChartAxisPortlet yAxis = getYAxis();

		boolean hasRelationships = CH.isntEmpty(this.getChart().getDmLinksFromThisPortlet()) || this.getChart().getCustomContextMenu().getRootMenu().hasChildren();
		boolean snapTop = yAxis != null && !yAxis.isReverse();
		boolean snapLeft = xAxis != null && !xAxis.isReverse();

		DoubleArrayList tmp = new DoubleArrayList();
		AmiWebChartSeries_Graph ser = this.getSeries();
		IndexedList<String, Grouping> groupings = ser.getUserSelectedGroupings();
		String lineType = (String) ser.getLineTypeFormula().getConstValue();
		final byte lineTypeCode;
		if (AmiWebChartSeries_Graph.LINE_HORZ.equals(lineType))
			lineTypeCode = AmiWebChartGraphicsWrapper.LINE_TYPE_HORZ;
		else if (AmiWebChartSeries_Graph.LINE_VERT.equals(lineType))
			lineTypeCode = AmiWebChartGraphicsWrapper.LINE_TYPE_VERT;
		else if (AmiWebChartSeries_Graph.LINE_HORZ_QUAD_BEZIER.equals(lineType))
			lineTypeCode = AmiWebChartGraphicsWrapper.LINE_TYPE_HORZ_QUAD_BEZIER;
		else if (AmiWebChartSeries_Graph.LINE_VERT_QUAD_BEZIER.equals(lineType))
			lineTypeCode = AmiWebChartGraphicsWrapper.LINE_TYPE_VERT_QUAD_BEZIER;
		else if (AmiWebChartSeries_Graph.LINE_HORZ_CUBIC_BEZIER.equals(lineType))
			lineTypeCode = AmiWebChartGraphicsWrapper.LINE_TYPE_HORZ_CUBIC_BEZIER;
		else if (AmiWebChartSeries_Graph.LINE_VERT_CUBIC_BEZIER.equals(lineType))
			lineTypeCode = AmiWebChartGraphicsWrapper.LINE_TYPE_VERT_CUBIC_BEZIER;
		else
			lineTypeCode = AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT;
		String descFontFamily = getChart().getDescFontFamily();
		String descFontStyle = getChart().getDescFontStyle();

		for (Grouping group : groupings.values()) {
			AmiWebChartFormula x = ser.getXLabelField();
			AmiWebChartFormula y = ser.getYLabelField();
			List<Object> xnames = group.getValuesForFormula(x.getName());
			List<Object> ynames = group.getValuesForFormula(y.getName());
			String groupName = group.getName();
			Map<String, Object> series = new HashMap<String, Object>();
			series.put("lineType", lineTypeCode);
			series.put("descFontFam", descFontFamily);
			series.put("descFontStyle", descFontStyle);
			series.put("size", group.getSize());
			series.put("position", ser.getPosition());
			series.put("name", groupName);
			series.put("groupId", group.getId());
			series.put("layerPos", this.getzPosition());
			for (int i = 0, l = ser.getFormulasCount(); i < l; i++) {

				AmiWebChartFormula formula = ser.getFormulaAt(i);
				if (formula.getIsHidden())
					continue;
				if (formula == ser.getSelectableFormula() && SH.isnt(formula.getValue())) {
					series.put(formula.getName(), new boolean[] { hasRelationships });
					continue;
				}
				List values = group.getValuesForFormula(formula.getName());
				if (CH.isEmpty(values)) {
					if (formula == ser.getXField() || formula == ser.getYField()) {
						values = ZEROS;
					} else
						continue;
				}
				Object list;
				if (formula.isXBound()) {
					tmp.clear();
					for (int pos = 0; pos < group.getSize(); pos++) {
						Object name = getAt(xnames, pos);
						Object num = (Object) getAt(values, pos);
						if (num instanceof Number) {
							double pos2 = xAxis.getPosition(name, (Number) num);
							tmp.add(pos2);
						} else {
							tmp.add(Double.NaN);
						}
					}
					list = tmp.toDoubleArray();
				} else if (formula.isYBound()) {
					tmp.clear();
					for (int pos = 0; pos < group.getSize(); pos++) {
						Object name = getAt(ynames, pos);

						Object num = (Object) getAt(values, pos);
						if (num instanceof Number) {
							double pos2 = yAxis.getPosition(name, (Number) num);
							tmp.add(pos2);
						} else {
							tmp.add(Double.NaN);
						}
					}
					list = tmp.toDoubleArray();
				} else {
					if (formula.isReturnTypeNumber()) {
						tmp.clear();
						for (Number n : ((List<Number>) values))
							tmp.add(n == null ? Double.NaN : n.doubleValue());
						list = tmp.toDoubleArray();
					} else if (formula.isReturnTypeColor()) {
						list = getColors((AmiWebChartFormula_Color) formula, values);
					} else if (formula.getReturnType() == Boolean.class || formula.isReturnTypeBoolean()) {
						boolean[] lst = new boolean[values.size()];
						for (int n = 0; n < values.size(); n++) {
							Boolean val = (Boolean) values.get(n);
							lst[n] = val == null ? false : val.booleanValue();
						}
						list = lst;
					} else {
						String[] lst = new String[values.size()];
						for (int n = 0; n < values.size(); n++) {
							Object val = values.get(n);
							lst[n] = AmiUtils.s(val);
						}
						list = lst;
					}

				}
				series.put(formula.getName(), list);
			}
			List<AmiWebChartShape> shapes = calculateShapes(series, snapLeft, snapTop);
			series.put("shapes", shapes.toArray(new AmiWebChartShape[shapes.size()]));
			this.groupId2Shapes.put(group.getId(), shapes);
			r.addSeries(series);
		}
		return r;
	}

	private List<AmiWebChartShape> calculateShapes(Map<String, Object> series, boolean snapLeft, boolean snapTop) {
		List<AmiWebChartShape> shapes = new ArrayList<AmiWebChartShape>();
		int layerPos = (Integer) series.get("layerPos");
		int groupId = (Integer) series.get("groupId");
		int len = (Integer) series.get("size");
		double[] xPos = (double[]) series.get("xPos");
		double[] yPos = (double[]) series.get("yPos");
		double[] mBorderSize = (double[]) series.get("mBorderSize");
		double[] mWidth = (double[]) series.get("mWidth");
		double[] mHeight = (double[]) series.get("mHeight");
		double[] mTop = (double[]) series.get("mTop");
		double[] mBottom = (double[]) series.get("mBottom");
		double[] mLeft = (double[]) series.get("mLeft");
		double[] mRight = (double[]) series.get("mRight");
		boolean[] sel = (boolean[]) series.get("sel");
		String[] mShape = (String[]) series.get("mShape");
		Color[] mColor = (Color[]) series.get("mColor");
		Color[] mBorderColor = (Color[]) series.get("mBorderColor");
		for (int i = 0; i < len; i++) {
			Color borderColor = AmiWebChartUtils.deref(mBorderColor, i);
			Color fillColor = AmiWebChartUtils.deref(mColor, i);
			double x = AmiWebChartUtils.deref(xPos, i);
			double y = AmiWebChartUtils.deref(yPos, i);
			int borderSize = AmiWebChartUtils.rd(AmiWebChartUtils.deref(mBorderSize, i));
			String shape = AmiWebChartUtils.deref(mShape, i);
			shapes.add(new AmiWebChartShape_XY(layerPos, groupId, i, AmiWebChartUtils.deref(sel, i), AmiWebChartUtils.deref(mLeft, i), AmiWebChartUtils.deref(mTop, i),
					AmiWebChartUtils.deref(mRight, i), AmiWebChartUtils.deref(mBottom, i), x, y, AmiWebChartUtils.deref(mWidth, i), AmiWebChartUtils.deref(mHeight, i),
					AmiWebChartShape.parseShape(shape), borderSize, borderColor, fillColor, snapLeft, snapTop));
		}
		return shapes;
	}

	private Object getAt(List<Object> values, int pos) {
		return values.size() == 1 ? values.get(0) : values.size() == 0 ? null : values.get(pos);

	}

	public String getVGridColor() {
		return vGridColor;
	}

	public void setVGridColor(String vGridColor) {
		if (OH.eq(this.vGridColor, vGridColor))
			return;
		this.vGridColor = vGridColor;
		flagViewStale();
	}

	public String getHGridColor() {
		return hGridColor;
	}

	public void setHGridColor(String hGridColor) {
		if (OH.eq(this.hGridColor, hGridColor))
			return;
		this.hGridColor = hGridColor;
		flagViewStale();
	}

	public String getVMidGridColor() {
		return vMidGridColor;
	}

	public void setVMidGridColor(String vMidGridColor) {
		if (OH.eq(this.vMidGridColor, vMidGridColor))
			return;
		this.vMidGridColor = vMidGridColor;
		flagViewStale();
	}

	public String getHMidGridColor() {
		return hMidGridColor;
	}

	public void setHMidGridColor(String hMidGridColor) {
		if (OH.eq(this.hMidGridColor, hMidGridColor))
			return;
		this.hMidGridColor = hMidGridColor;
		flagViewStale();
	}

	public String getVMajorGridColor() {
		return vMajorGridColor;
	}

	public void setVMajorGridColor(String vMajorGridColor) {
		if (OH.eq(this.vMajorGridColor, vMajorGridColor))
			return;
		this.vMajorGridColor = vMajorGridColor;
		flagViewStale();
	}

	public String getHMajorGridColor() {
		return hMajorGridColor;
	}

	public void setHMajorGridColor(String hMajorGridColor) {
		if (OH.eq(this.hMajorGridColor, hMajorGridColor))
			return;
		this.hMajorGridColor = hMajorGridColor;
		flagViewStale();
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		if (OH.eq(this.borderColor, borderColor))
			return;
		this.borderColor = borderColor;
		flagViewStale();
	}

	public void clearData() {
		AmiWebChartSeries_Graph ser = this.getSeries();
		ser.clearData();
		flagViewStale();
	}

	@Override
	public void buildData(AmiWebDmTableSchema datamodel, CalcFrameStack sf) {
		AmiWebChartSeries_Graph ser = this.getSeries();
		ser.buildData(datamodel, sf);
		AmiWebChartAxisPortlet xaxis = getXAxis();
		if (xaxis != null)
			xaxis.onDataChanged();
		AmiWebChartAxisPortlet yaxis = getYAxis();
		if (yaxis != null)
			yaxis.onDataChanged();
		flagDataStale();
	}

	public Number getMaxX() {
		Number r = null;
		AmiWebChartSeries_Graph i = this.getSeries();
		r = MH.maxAvoidNull(r, i.getXField().getMax());
		r = MH.maxAvoidNull(r, i.getX2Field().getMax());
		r = MH.maxAvoidNull(r, i.getLeftField().getMax());
		r = MH.maxAvoidNull(r, i.getRightField().getMax());
		return r;
	}
	public Number getMaxY() {
		Number r = null;
		AmiWebChartSeries_Graph i = this.getSeries();
		r = MH.maxAvoidNull(r, i.getYField().getMax());
		r = MH.maxAvoidNull(r, i.getY2Field().getMax());
		r = MH.maxAvoidNull(r, i.getTopField().getMax());
		r = MH.maxAvoidNull(r, i.getBottomField().getMax());
		return r;
	}
	public Number getMinX() {
		Number r = null;
		AmiWebChartSeries_Graph i = this.getSeries();
		r = MH.minAvoidNull(r, i.getXField().getMin());
		r = MH.minAvoidNull(r, i.getX2Field().getMin());
		r = MH.minAvoidNull(r, i.getLeftField().getMin());
		r = MH.minAvoidNull(r, i.getRightField().getMin());
		return r;
	}

	public static byte getType(AmiWebChartFormula formula) {
		if (SH.isnt(formula.getValue()))
			return AmiWebChartAxisFormatter.AXIS_DATA_TYPE_UNKNOWN;
		if (formula.isConst())
			return AmiWebChartAxisFormatter.AXIS_DATA_TYPE_CONST;
		Class<?> type = formula.getReturnType();
		if (type == DateNanos.class)
			return AmiWebChartAxisFormatter.AXIS_DATA_TYPE_UTCN;
		if (type == DateMillis.class)
			return AmiWebChartAxisFormatter.AXIS_DATA_TYPE_UTC;
		if (type == Double.class || type == Float.class)
			return AmiWebChartAxisFormatter.AXIS_DATA_TYPE_REAL;
		if (type == Long.class || type == Integer.class)
			return AmiWebChartAxisFormatter.AXIS_DATA_TYPE_WHOLE;
		return AmiWebChartAxisFormatter.AXIS_DATA_TYPE_WHOLE;
	}
	public byte getTypeY() {
		byte r = AmiWebChartAxisFormatter.AXIS_DATA_TYPE_UNKNOWN;
		AmiWebChartSeries_Graph i = this.getSeries();
		r = MH.max(r, getType(i.getYField()));
		r = MH.max(r, getType(i.getY2Field()));
		r = MH.max(r, getType(i.getTopField()));
		r = MH.max(r, getType(i.getBottomField()));
		r = MH.max(r, getType(i.getVerticalStackOn()));
		return r;
	}
	public byte getTypeX() {
		byte r = AmiWebChartAxisFormatter.AXIS_DATA_TYPE_UNKNOWN;
		AmiWebChartSeries_Graph i = this.getSeries();
		r = MH.max(r, getType(i.getXField()));
		r = MH.max(r, getType(i.getX2Field()));
		r = MH.max(r, getType(i.getLeftField()));
		r = MH.max(r, getType(i.getRightField()));
		r = MH.max(r, getType(i.getHorizontalStackOn()));
		return r;
	}
	public Number getMinY() {
		Number r = null;
		AmiWebChartSeries_Graph i = this.getSeries();
		r = MH.minAvoidNull(r, i.getYField().getMin());
		r = MH.minAvoidNull(r, i.getY2Field().getMin());
		r = MH.minAvoidNull(r, i.getTopField().getMin());
		r = MH.minAvoidNull(r, i.getBottomField().getMin());
		return r;
	}
	public void getUniqueXLabels(Set<Object> sink) {
		AmiWebChartSeries_Graph i = this.getSeries();
		Set<Object> t = i.getXLabelField().getUniqueValues();
		if (!t.isEmpty())
			sink.addAll(t);
		else if (i.getXLabelField().getReturnType() == null && i.getUserSelectedGroupings().getSize() > 0)
			sink.add(null);
	}
	public void getUniqueYLabels(Set<Object> sink) {
		AmiWebChartSeries_Graph i = this.getSeries();
		Set<Object> t = i.getYLabelField().getUniqueValues();
		if (!t.isEmpty())
			sink.addAll(t);
		else if (i.getYLabelField().getReturnType() == null && i.getUserSelectedGroupings().getSize() > 0)
			sink.add(null);
	}

	@Override
	public WebMenuItem populateConfigMenu(String prefix) {
		return null;
	}

	@Override
	public void onAmiContextMenu(String id) {
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		return false;
	}

	@Override
	public String getJsClassName() {
		return null;
	}

	@Override
	public void onStyleValueChanged(short key, Object old, Object value) {
		switch (key) {
			case AmiWebStyleConsts.CODE_GR_LYR_BDR_CL:
				setBorderColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_GR_LYR_H_GRD_CL:
				setHGridColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_GR_LYR_H_MAJ_GRD_CL:
				setHMajorGridColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_GR_LYR_H_MID_GRD_CL:
				setHMidGridColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_GR_LYR_V_GRD_CL:
				setVGridColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_GR_LYR_V_MAJ_GRD_CL:
				setVMajorGridColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_GR_LYR_V_MID_GRD_CL:
				setVMidGridColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_V_GRD_SZ:
				setVGridSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_V_MAJ_GRD_SZ:
				setVMajorGridSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_V_MID_GRD_SZ:
				setVMidGridSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_H_GRD_SZ:
				setHGridSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_H_MAJ_GRD_SZ:
				setHMajorGridSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_H_MID_GRD_SZ:
				setHMidGridSize(Caster_Integer.INSTANCE.cast(value));
				break;
		}
	}

	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_RenderingLayer_Graph.TYPE_LAYER_GRAPH;
	}

	public int getVGridSize() {
		return vGridSize;
	}

	public void setVGridSize(int vGridSize) {
		if (this.vGridSize == vGridSize)
			return;
		this.vGridSize = vGridSize;
		flagViewStale();
	}

	public int getHGridSize() {
		return hGridSize;
	}

	public void setHGridSize(int hGridSize) {
		if (this.hGridSize == hGridSize)
			return;
		this.hGridSize = hGridSize;
		flagViewStale();
	}

	public int getVMidGridSize() {
		return vMidGridSize;
	}

	public void setVMidGridSize(int vMidGridSize) {
		if (this.vMidGridSize == vMidGridSize)
			return;
		this.vMidGridSize = vMidGridSize;
		flagViewStale();
	}

	public int getHMidGridSize() {
		return hMidGridSize;
	}

	public void setHMidGridSize(int hMidGridSize) {
		if (this.hMidGridSize == hMidGridSize)
			return;
		this.hMidGridSize = hMidGridSize;
		flagViewStale();
	}

	public int getVMajorGridSize() {
		return vMajorGridSize;
	}

	public void setVMajorGridSize(int vMajorGridSize) {
		if (this.vMajorGridSize == vMajorGridSize)
			return;
		this.vMajorGridSize = vMajorGridSize;
		flagViewStale();
	}

	public int getHMajorGridSize() {
		return hMajorGridSize;
	}

	public void setHMajorGridSize(int hMajorGridSize) {
		if (this.hMajorGridSize == hMajorGridSize)
			return;
		this.hMajorGridSize = hMajorGridSize;
		flagViewStale();
	}

	@Override
	public AmiWebChartRenderingLayer<AmiWebChartSeries_Graph> copy() {
		AmiWebChartRenderingLayer_Graph output = new AmiWebChartRenderingLayer_Graph(getPlot());
		output.init(this.getChart().getAmiLayoutFullAlias(), getConfiguration());
		return output;
	}

	@Override
	public String exportToText() {
		Map<String, Object> config = (Map<String, Object>) ((List) getConfiguration().get("layers")).get(0);
		Map<String, Object> style = this.getStylePeer().exportConfig();
		String dm = this.getDmAliasDotName();
		String table = this.getDmTableName();
		Map<Object, Object> top = CH.m("config", config, "style", style, "dm", dm, "table", table);
		return JSON_CONVERTER.objectToString(top);
	}

	@Override
	public void importFromText(String text, StringBuilder errorSink) {
		Map<String, Object> top = (Map<String, Object>) JSON_CONVERTER.stringToObject(text);
		Map<String, Object> config = (Map<String, Object>) top.get("config");
		Map<String, Object> style = (Map<String, Object>) top.get("style");
		String dm = (String) top.get("dm");
		String table = (String) top.get("table");
		config.put("id", this.getSeries().getId());
		this.getStylePeer().importConfig(style);
		this.setDm(dm, table);
		getSeries().init(config);
	}

	@Override
	public Iterable<AmiWebChartShape> getCurrentShapes() {
		return IterableIterator.create(this.groupId2Shapes.values());
	}

	protected AmiWebImageGenerator updateForZoom(AmiWebImageGenerator current) {
		AmiWebImageGenerator_Chart r = new AmiWebImageGenerator_Chart();
		updateLookAndFeel(r);
		r.copySeries((AmiWebImageGenerator_Chart) current);
		return r;
	}

	@Override
	public String toDerivedString() {
		return getAri();
	}
	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}

	@Override
	List<AmiWebChartShape> getShapesAtGroup(int groupId) {
		return this.groupId2Shapes.get(groupId);
	}
}
