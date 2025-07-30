package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.charts.AmiWebChartSeries.Grouping;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_RenderingLayer_RadialGraph;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.utils.CH;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.IterableIterator;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;

public class AmiWebChartRenderingLayer_RadialGraph extends AmiWebChartRenderingLayer<AmiWebChartSeries_RadialGraph> implements ConfirmDialogListener, AmiWebStyledPortlet {

	public AmiWebChartRenderingLayer_RadialGraph(AmiWebChartPlotPortlet parentPlot) {
		super(parentPlot);
		flagDataStale();

		getStylePeer().initStyle();
	}

	public void drainJavascript() {
	}

	public String getType() {
		return "Radial";
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("flipX", this.flipX);
		r.put("flipY", this.flipY);
		//		r.put("aFormula", this.angleLabelFormula.getFormulaConfig());
		//		r.put("rFormula", this.radiusLabelFormula.getFormulaConfig());
		List<Map<String, Object>> layers = new ArrayList<Map<String, Object>>();
		AmiWebChartSeries layer = this.getSeries();
		layers.add(layer.getConfiguration());
		r.put("layers", layers);
		return r;
	}
	public void init(String alias, Map<String, Object> layer) {
		super.init(alias, layer);
		this.flipX = CH.getOr(Caster_Boolean.INSTANCE, layer, "flipX", false);
		this.flipY = CH.getOr(Caster_Boolean.INSTANCE, layer, "flipY", false);
		this.autoMaxValue = CH.getOr(Caster_Boolean.INSTANCE, layer, "autoMaxValue", this.autoMaxValue);
		this.autoMinValue = CH.getOr(Caster_Boolean.INSTANCE, layer, "autoMinValue", this.autoMinValue);
		this.minValue = CH.getOr(Caster_Double.INSTANCE, layer, "minValue", this.minValue);
		this.maxValue = CH.getOr(Caster_Double.INSTANCE, layer, "maxValue", this.maxValue);
		// Convert decimal positions to percentages 
		layer.put("xPos", 100 * CH.getOr(Caster_Double.INSTANCE, layer, "xPos", .5));
		layer.put("yPos", 100 * CH.getOr(Caster_Double.INSTANCE, layer, "yPos", .5));
		//		this.angleLabelFormula.initFormula(CH.getOr(Caster_String.INSTANCE, layer, "aFormula", null));
		//		this.radiusLabelFormula.initFormula(CH.getOr(Caster_String.INSTANCE, layer, "rFormula", null));
		List<Map<String, Object>> series = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, layer, "layers");
		OH.assertEq(series.size(), 1);
		for (Map<String, Object> ser : series) {
			AmiWebChartSeries_RadialGraph l = new AmiWebChartSeries_RadialGraph(getPlot().getService(), this.getChart(), this.getDataModelSchema(), this);
			l.init(ser);
			this.getChart().registerUsedId(l.getId());
			setSeries(l);
		}
		flagDataStale();
	}
	@Override
	public AmiWebImageGenerator createImageGenerator() {
		this.groupId2Shapes.clear();
		boolean hasRelationships = CH.isntEmpty(this.getChart().getDmLinksFromThisPortlet()) || this.getChart().getCustomContextMenu().getRootMenu().hasChildren();
		AmiWebImageGenerator_RadialChart r = new AmiWebImageGenerator_RadialChart();
		CalcFrameStack sf = this.getPlot().getChart().getStackFrame();

		int angleAdd = flipX ? 180 : 0;
		boolean angleReverse = flipX != flipY;
		r.setOpacity(getOpacity());
		r.setCenterX(this.getPlot().getWidth() * this.centerXPos);
		r.setCenterY(this.getPlot().getHeight() * (1 - this.centerYPos));
		r.setBorderColor(parseColor(borderColor));
		r.setCircleColor(parseColor(circleColor));
		r.setSpokesColor(parseColor(spokesColor));
		r.setBorderSize(borderSize);
		r.setCircleSize(circleSize);
		r.setSpokesSize(spokesSize);
		r.setSpokesCount(spokesCount);
		r.setInnerPaddingPx(innerPaddingPx);
		r.setOuterPaddingPx(outerPaddingPx);
		r.setFontColor(parseColor(this.labelColor));
		r.setFontSize(this.labelSize);
		r.setsAngle(angleAdd + (angleReverse ? -this.startAngle : this.startAngle));
		r.setlAngle(angleAdd + (angleReverse ? -this.labelAngle : this.labelAngle));
		r.seteAngle(angleAdd + (angleReverse ? -this.endAngle : this.endAngle));
		r.setCirclesCount(this.circlesCount);

		double maxY = nullToNan(getMaxY());
		double minY = nullToNan(getMinY());
		double diff = maxY - minY;
		if (diff == 0) {
			double t = maxY * .01;
			if (t == 0)
				t = 1;
			maxY = maxY + t;
			minY = minY - t;
		}
		if (MH.isNumber(maxY)) {

			r.setRMax(maxY);
			r.setRMin(minY);
			List<String> rLabels = new ArrayList<String>();
			double scale = (maxY - minY) / circlesCount;
			AmiWebFormula radiusFormula = this.getSeries().getRadiusFormula();
			if (radiusFormula.getFormulaCalc() != null) {
				SingletonCalcFrame m = new SingletonCalcFrame(AmiWebChartAxisPortlet.VAR_NAME, null, Double.class);
				ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf, m);
				for (int i = 0; i < circlesCount; i++) {
					double n = i * scale + minY;
					m.setValue(n);
					Object d = radiusFormula.getFormulaCalc().get(rsf);
					rLabels.add(AmiUtils.s(d));
				}
			} else {
				for (int i = 0; i < circlesCount; i++) {
					double n = i * scale + minY;
					rLabels.add(SH.toString((int) n));
				}
			}
			r.setRLabels(rLabels);
		} else {
			r.setRMax(Double.NaN);
			r.setRLabels(null);
		}
		List<String> aLabels = new ArrayList<String>();
		AmiWebFormula angleFormula = this.getSeries().getAngleFormula();
		if (angleFormula.getFormulaCalc() == null) {
			for (int i = 0; i <= spokesCount; i++) {
				double n = startAngle + (double) Math.abs(endAngle - startAngle) * i / spokesCount;
				if (n >= 360)
					n -= 360;
				aLabels.add("");
			}
		} else {
			SingletonCalcFrame m = new SingletonCalcFrame(AmiWebChartAxisPortlet.VAR_NAME, null, Double.class);
			ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf, m);
			for (int i = 0; i <= spokesCount; i++) {
				double n = startAngle + (double) Math.abs(endAngle - startAngle) * i / spokesCount;
				if (n >= 360)
					n -= 360;
				m.setValue(n);
				Object d = angleFormula.getFormulaCalc().get(rsf);
				aLabels.add(AmiUtils.s(d));
			}
		}
		r.setALabels(aLabels);
		DoubleArrayList tmp = new DoubleArrayList();
		AmiWebChartSeries_RadialGraph ser = this.getSeries();
		IndexedList<String, Grouping> groupings = ser.getUserSelectedGroupings();
		double outer = Math.min(getPlot().getWidth(), getPlot().getHeight()) / 2 - this.outerPaddingPx;
		double rScale = (outer - this.innerPaddingPx) / (maxY - minY);
		for (Grouping group : groupings.values()) {
			String groupName = group.getName();
			Map<String, Object> series = new HashMap<String, Object>();
			series.put("descFontFam", this.getChart().getDescFontFamily());
			series.put("descFontStyle", this.getChart().getDescFontStyle());
			series.put("size", group.getSize());
			series.put("position", ser.getPosition());
			series.put("name", groupName);
			series.put("groupId", group.getId());
			series.put("layerPos", this.getzPosition());

			for (int i = 0; i < ser.getFormulasCount(); i++) {

				AmiWebChartFormula formula = ser.getFormulaAt(i);
				if (formula.getIsHidden())
					continue;
				if (formula == ser.getSelectableFormula() && SH.isnt(formula.getValue())) {
					series.put(formula.getName(), new boolean[] { hasRelationships });
					continue;
				}
				Object list;
				List values = group.getValuesForFormula(formula.getName());
				if (formula.isYBound()) {
					tmp.clear();
					for (int pos = 0; pos < group.getSize(); pos++) {
						Object num = (Object) getAt(values, pos);
						if (num instanceof Number) {
							double offset = ((Number) num).doubleValue();
							double pos2 = (offset - minY) * rScale + this.innerPaddingPx;
							tmp.add(pos2);
						} else {
							tmp.add(Double.NaN);
						}
					}
					list = tmp.toDoubleArray();
				} else if (formula.isReturnTypeColor()) {
					list = getColors((AmiWebChartFormula_Color) formula, values);
					//					Color[] lst = new Color[values.size()];
					//					for (int n = 0; n < values.size(); n++) {
					//						Object val = values.get(n);
					//						lst[n] = val == null ? null : parseColor(val.toString());
					//					}
					//					list = lst;
				} else if (formula.isXBound()) {
					tmp.clear();
					for (int pos = 0; pos < group.getSize(); pos++) {
						Object num = (Object) getAt(values, pos);
						if (num instanceof Number) {
							double offset = ((Number) num).doubleValue();
							double pos2 = angleAdd + (angleReverse ? -offset : offset);
							tmp.add(pos2);
						} else {
							tmp.add(Double.NaN);
						}
					}
					list = tmp.toDoubleArray();
				} else if (formula.isReturnTypeNumber()) {
					tmp.clear();
					for (Number n : ((List<Number>) values))
						tmp.add(n == null ? Double.NaN : n.doubleValue());
					list = tmp.toDoubleArray();
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
				series.put(formula.getName(), list);
			}
			List<AmiWebChartShape> shapes = calculateShapes(series, r.getCenterX(), r.getCenterY());
			series.put("shapes", shapes.toArray(new AmiWebChartShape[shapes.size()]));
			this.groupId2Shapes.put(group.getId(), shapes);
			r.addSeries(series);
		}
		return r;
	}
	static private double nullToNan(Number n) {
		return n == null ? Double.NaN : n.doubleValue();
	}

	private List<AmiWebChartShape> calculateShapes(Map<String, Object> series, double centerX, double centerY) {
		List<AmiWebChartShape> shapes = new ArrayList<AmiWebChartShape>();
		int layerPos = (Integer) series.get("layerPos");
		int groupId = (Integer) series.get("groupId");
		double[] xPos = (double[]) series.get("xPos");
		double[] yPos = (double[]) series.get("yPos");
		Color[] mColor = (Color[]) series.get("mColor");
		Color[] mBorderColor = (Color[]) series.get("mBorderColor");
		String[] desc = (String[]) series.get("desc");
		double[] mBorderSize = (double[]) series.get("mBorderSize");
		double[] mWidth = (double[]) series.get("mWidth");
		double[] mHeight = (double[]) series.get("mHeight");
		double[] mTop = (double[]) series.get("mTop");
		double[] mBottom = (double[]) series.get("mBottom");
		double[] mLeft = (double[]) series.get("mLeft");
		double[] mRight = (double[]) series.get("mRight");
		boolean[] sel = (boolean[]) series.get("sel");
		String[] mShape = (String[]) series.get("mShape");
		int len = (Integer) series.get("size");
		for (int i = 0; i < len; i++) {
			double x = AmiWebChartUtils.deref(xPos, i);
			double y = AmiWebChartUtils.deref(yPos, i);
			double bottom = AmiWebChartUtils.deref(mBottom, i);
			double top = AmiWebChartUtils.deref(mTop, i);
			double left = AmiWebChartUtils.deref(mLeft, i);
			double right = AmiWebChartUtils.deref(mRight, i);
			boolean selected = AmiWebChartUtils.deref(sel, i);
			int borderSize = AmiWebChartUtils.rd(AmiWebChartUtils.deref(mBorderSize, i));
			String shape = AmiWebChartUtils.deref(mShape, i);
			Color fillStyle = AmiWebChartUtils.deref(mColor, i);
			Color strokeStyle = AmiWebChartUtils.deref(mBorderColor, i);
			double width = AmiWebChartUtils.deref(mWidth, i);
			double height = AmiWebChartUtils.deref(mHeight, i);
			if ("wedge".equals(shape)) {
				shapes.add(new AmiWebChartShape_Wedge(layerPos, groupId, i, selected, borderSize, (strokeStyle), (fillStyle), centerX, centerY, top, left, bottom, right, y, x,
						height, width));
			} else {
				x = x * AmiWebChartUtils.PI_180;
				double yr = AmiWebChartUtils.radiansToY(y, x);
				double xr = AmiWebChartUtils.radiansToX(y, x);
				shapes.add(new AmiWebChartShape_XY(layerPos, groupId, i, selected, left, top, right, bottom, centerX + xr, centerY + yr, width, height,
						AmiWebChartShape.parseShape(shape), borderSize, strokeStyle, fillStyle, true, true));
			}
		}

		return shapes;
	}

	private static Object getAt(List<Object> values, int pos) {
		return values.size() == 1 ? values.get(0) : values.size() == 0 ? null : values.get(pos);

	}
	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		if (OH.eq(this.borderColor, borderColor))
			return;
		this.borderColor = borderColor;
		flagDataStale();
	}

	@Override
	public void buildData(AmiWebDmTableSchema datamodel, CalcFrameStack sf) {
		AmiWebChartSeries ser = this.getSeries();
		ser.buildData(datamodel, sf);
		flagDataStale();
	}

	public Number getMaxY() {
		if (!this.autoMaxValue)
			return this.maxValue;
		Number r = null;
		AmiWebChartSeries_RadialGraph i = this.getSeries();
		r = MH.maxAvoidNull(r, i.getYField().getMax());
		r = MH.maxAvoidNull(r, i.getY2Field().getMax());
		r = MH.maxAvoidNull(r, i.getTopField().getMax());
		r = MH.maxAvoidNull(r, i.getBottomField().getMax());
		return r;
	}
	public Number getMinY() {
		Number r = null;
		if (!this.autoMinValue) {
			if (!this.autoMaxValue && this.maxValue < this.minValue)
				return this.maxValue;
			return this.minValue;
		}
		AmiWebChartSeries_RadialGraph i = this.getSeries();
		r = MH.minAvoidNull(r, i.getYField().getMin());
		r = MH.minAvoidNull(r, i.getY2Field().getMin());
		r = MH.minAvoidNull(r, i.getTopField().getMin());
		r = MH.minAvoidNull(r, i.getBottomField().getMin());
		return r;
	}
	public void getUniqueX(Set<Object> sink) {
		AmiWebChartSeries_RadialGraph i = this.getSeries();
		sink.addAll(i.getXField().getUniqueValues());
	}
	public void getUniqueY(Set<Object> sink) {
		AmiWebChartSeries_RadialGraph i = this.getSeries();
		sink.addAll(i.getYField().getUniqueValues());
	}

	private byte keyPosition;
	private double centerXPos;
	private double centerYPos;
	private String borderColor = "#444444";
	private String spokesColor;
	private String circleColor;
	private int borderSize;
	private int spokesSize;
	private int circleSize;
	private int spokesCount;
	private int innerPaddingPx;
	private int outerPaddingPx;

	private String labelColor = "#888888";
	private int labelSize = 14;
	private int startAngle = 0;
	private int labelAngle = 90;
	private int endAngle = 360;
	private boolean flipX = false;
	private boolean flipY = false;
	private int circlesCount = 10;
	//	private AmiWebFormula angleLabelFormula;
	//	private AmiWebFormula radiusLabelFormula;
	//	private String angleLabelFormula;
	//	private String radiusLabelFormula;
	//	private DerivedCellCalculator angleLabelFormulaCalc;
	//	private DerivedCellCalculator radiusLabelFormulaCalc;
	private boolean autoMinValue = true;
	private boolean autoMaxValue = true;
	private double minValue = 0;
	private double maxValue = 100;
	private IntKeyMap<List<AmiWebChartShape>> groupId2Shapes = new IntKeyMap<List<AmiWebChartShape>>();

	public byte getKeyPosition() {
		return keyPosition;
	}

	public void setKeyPosition(byte keyPosition) {
		this.keyPosition = keyPosition;
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

	public double getCenterXPos() {
		return this.centerXPos;
	}
	public double getCenterYPos() {
		return this.centerYPos;
	}
	public String getSpokesColor() {
		return this.spokesColor;
	}
	public String getCircleColor() {
		return this.circleColor;
	}
	public int getSpokesCount() {
		return this.spokesCount;
	}
	public int getInnerPaddingPx() {
		return this.innerPaddingPx;
	}
	public int getOuterPaddingPx() {
		return this.outerPaddingPx;
	}

	public String getLabelColor() {
		return this.labelColor;
	}
	public int getLabelSize() {
		return this.labelSize;
	}
	public int getStartAngle() {
		return this.startAngle;
	}
	public int getLabelAngle() {
		return this.labelAngle;
	}
	public int getEndAngle() {
		return this.endAngle;
	}
	public boolean getFlipX() {
		return this.flipX;
	}
	public boolean getFlipY() {
		return this.flipY;
	}
	public int getCirclesCount() {
		return this.circlesCount;
	}
	//	public String getRadiusLabelFormula(boolean override) {
	//		return this.radiusLabelFormula.getFormula(override);
	//	}
	//	public String getAngleLabelFormula(boolean override) {
	//		return this.angleLabelFormula.getFormula(override);
	//	}

	public void setCenterXPos(double d) {
		if (d == this.centerXPos)
			return;
		this.centerXPos = d;
		flagDataStale();
	}
	public void setCenterYPos(double d) {
		if (d == this.centerYPos)
			return;
		this.centerYPos = d;
		flagDataStale();
	}
	public void setSpokesColor(String value) {
		if (OH.eq(value, this.spokesColor))
			return;
		this.spokesColor = value;
		flagDataStale();
	}
	public void setCircleColor(String value) {
		if (OH.eq(value, this.circleColor))
			return;
		this.circleColor = value;
		flagDataStale();
	}
	public void setSpokesCount(int spokesCount) {
		if (OH.eq(spokesCount, this.spokesCount))
			return;
		this.spokesCount = spokesCount;
		flagDataStale();
	}
	public void setInnerPaddingPx(int paddingPx) {
		if (OH.eq(paddingPx, this.innerPaddingPx))
			return;
		this.innerPaddingPx = paddingPx;
		flagDataStale();
	}
	public void setOuterPaddingPx(int paddingPx) {
		if (OH.eq(paddingPx, this.outerPaddingPx))
			return;
		this.outerPaddingPx = paddingPx;
		flagDataStale();
	}

	public void setLabelColor(String labelColor) {
		if (OH.eq(labelColor, this.labelColor))
			return;
		this.labelColor = labelColor;
		flagDataStale();
	}
	public void setLabelSize(int labelSize) {
		if (OH.eq(labelSize, this.labelSize))
			return;
		this.labelSize = labelSize;
		flagDataStale();
	}
	public void setStartAngle(int startAngle) {
		if (OH.eq(startAngle, this.startAngle))
			return;
		this.startAngle = startAngle;
		flagDataStale();
	}
	public void setLabelAngle(int labelAngle) {
		if (OH.eq(labelAngle, this.labelAngle))
			return;
		this.labelAngle = labelAngle;
		flagDataStale();
	}
	public void setEndAngle(int endAngle) {
		if (OH.eq(endAngle, this.endAngle))
			return;
		this.endAngle = endAngle;
		flagDataStale();
	}
	public void setFlipX(boolean flipX) {
		if (OH.eq(flipX, this.flipX))
			return;
		this.flipX = flipX;
		flagDataStale();
	}
	public void setFlipY(boolean flipY) {
		if (OH.eq(flipY, this.flipY))
			return;
		this.flipY = flipY;
		flagDataStale();
	}
	public void setCirclesCount(int circlesCount) {
		if (OH.eq(circlesCount, this.circlesCount))
			return;
		this.circlesCount = circlesCount;
		flagDataStale();
	}
	//	public void setRadiusLabelFormula(String radiusLabelFormula, boolean override) {
	//		if (OH.eq(radiusLabelFormula, this.radiusLabelFormula))
	//			return;
	//		this.radiusLabelFormula.setFormula(radiusLabelFormula, override);
	//			flagDataStale();
	//		this.radiusLabelFormula = radiusLabelFormula;
	//		this.radiusLabelFormulaCalc = buildAxisFormatter(radiusLabelFormula);
	//	}

	//	private DerivedCellCalculator buildAxisFormatter(String numberFormula) {
	//		return getChart().getScriptManager().toCalc(numberFormula, AmiWebChartAxisPortlet.VARTYPES, this);
	//	}

	//	public void setAngleLabelFormula(String angleLabelFormula, boolean override) {
	//		this.angleLabelFormula.setFormula(angleLabelFormula, override);
	//		if (OH.eq(angleLabelFormula, this.angleLabelFormula))
	//			return;
	//		this.angleLabelFormula = angleLabelFormula;
	//		this.angleLabelFormulaCalc = buildAxisFormatter(angleLabelFormula);
	//		flagDataStale();
	//	}
	public boolean getAutoMinValue() {
		return autoMinValue;
	}

	public void setAutoMinValue(boolean autoMinValue) {
		if (this.autoMinValue == autoMinValue)
			return;
		this.autoMinValue = autoMinValue;
		flagDataStale();
	}

	public boolean getAutoMaxValue() {
		return autoMaxValue;
	}

	public void setAutoMaxValue(boolean autoMaxValue) {
		if (this.autoMaxValue == autoMaxValue)
			return;
		this.autoMaxValue = autoMaxValue;
		flagDataStale();
	}

	public double getMinValue() {
		return minValue;
	}
	public void setMinValue(double minValue) {
		if (this.minValue == minValue)
			return;
		this.minValue = minValue;
		flagDataStale();
	}

	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		if (this.maxValue == maxValue)
			return;
		this.maxValue = maxValue;
		flagDataStale();
	}

	public void clearData() {
		AmiWebChartSeries_RadialGraph ser = this.getSeries();
		ser.clearData();
		flagDataStale();
	}

	@Override
	public void onStyleValueChanged(short key, Object old, Object value) {
		switch (key) {
			case AmiWebStyleConsts.CODE_RD_LYR_START_ANGLE:
				setStartAngle(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_SPOKES_COUNT:
				setSpokesCount(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_SPOKES_CL:
				setSpokesColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_OUTER_PD_PX:
				setOuterPaddingPx(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_LBL_SZ:
				setLabelSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_LBL_CL:
				setLabelColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_LBL_ANGLE:
				setLabelAngle(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_INNER_PD_PX:
				setInnerPaddingPx(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_END_ANGLE:
				setEndAngle(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_CIRCLES_COUNT:
				setCirclesCount(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_CIRCLE_CL:
				setCircleColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_CENTER_Y:
				setCenterYPos(Caster_Double.INSTANCE.cast(value) / 100d);
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_CENTER_X:
				setCenterXPos(Caster_Double.INSTANCE.cast(value) / 100d);
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_BDR_CL:
				setBorderColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_SPOKES_SZ:
				setSpokesSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_CIRCLE_SZ:
				setCircleSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_RD_LYR_BDR_SZ:
				setBorderSize(Caster_Integer.INSTANCE.cast(value));
				break;
		}
	}

	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_RenderingLayer_RadialGraph.TYPE_LAYER_RADIAL_GRAPH;
	}

	public int getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(int borderSize) {
		if (this.borderSize == borderSize)
			return;
		this.borderSize = borderSize;
		flagDataStale();
	}

	public int getSpokesSize() {
		return spokesSize;
	}

	public void setSpokesSize(int spokesSize) {
		if (this.spokesSize == spokesSize)
			return;
		this.spokesSize = spokesSize;
		flagDataStale();
	}

	public int getCircleSize() {
		return circleSize;
	}

	public void setCircleSize(int circleSize) {
		if (this.circleSize == circleSize)
			return;
		this.circleSize = circleSize;
		flagDataStale();
	}

	@Override
	public AmiWebChartRenderingLayer<AmiWebChartSeries_RadialGraph> copy() {
		AmiWebChartRenderingLayer_RadialGraph output = new AmiWebChartRenderingLayer_RadialGraph(getPlot());
		output.init(getChart().getAmiLayoutFullAlias(), getConfiguration());
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

	@Override
	public List<AmiWebChartShape> getShapesAtGroup(int groupId) {
		return this.groupId2Shapes.get(groupId);
	}

	protected AmiWebImageGenerator updateForZoom(AmiWebImageGenerator current) {
		return this.createImageGenerator();
	}

}
