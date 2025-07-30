package com.f1.ami.web.surface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebFormulasListener;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.charts.AmiWebChartAxisPortlet;
import com.f1.ami.web.charts.AmiWebChartUtils;
import com.f1.utils.CH;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;

public class AmiWebSurfaceAxisPortlet implements AmiWebDomObject, AmiWebFormulasListener {

	public static final String VAR_NAME = "n";

	public static final byte DIMENSION_X = 1;
	public static final byte DIMENSION_Y = 2;
	public static final byte DIMENSION_Z = 3;

	//	private static final int[] HUMAN_FACTORS = new int[] { 1, 2, 5, 10, 20, 25, 50, 100 };
	public static byte TYPE_NUMERIC = 1;
	public static byte TYPE_LOGRITHMIC = 2;
	public static byte TYPE_POSITION = 3;

	private final byte dimension;

	private boolean isSeries = false;
	private boolean autoMinValue = true;
	private boolean autoMaxValue = true;
	private boolean autoMinorValue = true;
	private boolean autoMajorValue = true;
	private byte type;
	private double logrithmicBase;
	private double minValue = 0;
	private double maxValue = 100;
	private boolean reverse;
	private double majorUnit = 10;
	private double minorUnit = 2;
	private AmiWebFormula formatterFormula;
	private DerivedCellCalculator currentFormatter;
	private int startPadding = 0;
	private int endPadding = 0;
	private String fontStyle = "";
	private int fontSize = 12;
	// tick line length
	private int majorUnitSize = 2;
	private int minorUnitSize = 1;
	private String fontFamily = "Arial";
	private int textRotate = 0;
	private int textPadding = 2;
	private String fontColor = "#000000";
	private String lineColor = "#000000";
	private String otherLineColor = "#CCCCCC";
	private String title = "";
	private String titleColor = "#000000";
	private String titleFont = "Arial";
	private int titlePadding = 1;
	private int titleSize = 5;

	public AmiWebSurfaceAxisPortlet(AmiWebSurfacePortlet chart, byte dimension) {
		this.dimension = dimension;
		this.chart = chart;
		this.formulas = new AmiWebFormulasImpl(this);
		this.formatterFormula = formulas.addFormula("display", Object.class);
		this.formulas.addFormulasListener(this);
		updateAri();
	}

	public byte getType() {
		return type;
	}

	private DoubleArrayList majorTicks = new DoubleArrayList();
	private boolean majorTicksStale = true;
	private boolean isViewStale;

	public DoubleArrayList getMajorTicks() {
		if (majorTicksStale) {
			this.majorTicks.clear();
			if (this.isSeries) {
				if (this.series != null)
					for (int i = 0; i < this.series.size(); i++)
						this.majorTicks.add(i + .5d);
			} else {
				double minValue = getMinValue();
				double maxValue = getMaxValue();
				double majorUnit = getMajorUnit();
				this.majorTicks.add(minValue);
				double end = maxValue - majorUnit / 2;
				for (double i = minValue + majorUnit; i <= end; i += majorUnit) {
					this.majorTicks.add(i);
				}
				if (minValue < maxValue && this.getLength() > 50) {
					this.majorTicks.add(maxValue);
				}
			}
			majorTicksStale = false;
		}
		return majorTicks;
	}

	private final SingletonCalcFrame tmp = new SingletonCalcFrame(VAR_NAME, Double.class, null);
	private boolean rangeStale = false;
	private List<String> series;
	private Map<String, Double> seriesToPosition;
	private AmiWebSurfacePortlet chart;

	private int length = 100;

	private String ari;

	public String format(double val, ReusableCalcFrameStack sf) {
		if (currentFormatter == null)
			return SH.toString(val);
		tmp.setValue(val);
		return AmiUtils.snn(currentFormatter.get(sf.reset(tmp)), "null");
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = new HashMap<String, Object>();
		r.put("fontColor", this.fontColor);
		r.put("lineColor", this.lineColor);
		r.put("oLineColor", this.otherLineColor);
		r.put("autoMajorValue", this.autoMajorValue);
		r.put("autoMinorValue", this.autoMinorValue);
		r.put("autoMaxValue", this.autoMaxValue);
		r.put("autoMinValue", this.autoMinValue);
		r.put("reverse", this.reverse);
		r.put("logBase", this.logrithmicBase);
		r.put("minValue", this.minValue);
		r.put("maxValue", this.maxValue);
		r.put("minorUnit", this.minorUnit);
		r.put("majorUnit", this.majorUnit);
		r.put("startPadding", this.startPadding);
		r.put("endPadding", this.endPadding);
		r.put("fontStyle", this.fontStyle);
		r.put("fontSize", this.fontSize);
		r.put("majorUnitSize", this.majorUnitSize);
		r.put("minorUnitSize", this.minorUnitSize);
		r.put("fontFamily", this.fontFamily);
		r.put("textRotate", this.textRotate);
		r.put("textPadding", this.textPadding);
		r.put("title", this.title);
		r.put("titleColor", this.titleColor);
		r.put("titleFont", this.titleFont);
		r.put("titlePadding", this.titlePadding);
		r.put("titleSize", this.titleSize);
		r.put("format", this.formatterFormula.getFormulaConfig());
		r.put("isSeries", this.isSeries);
		return r;
	}

	public void updateData() {
		if (isSeries) {
			Set<Object> values = new LinkedHashSet<Object>();
			this.series = new ArrayList<String>(values.size());
			switch (dimension) {
				case DIMENSION_X:
					values.addAll(getChart().getUniqueX());
					break;
				case DIMENSION_Y:
					values.addAll(getChart().getUniqueY());
					break;
				case DIMENSION_Z:
					values.addAll(getChart().getUniqueZ());
					break;
			}
			this.seriesToPosition = new HashMap<String, Double>();
			int pos = 0;
			for (Object i : values) {
				String name = AmiUtils.snn(i, "null");
				this.series.add(name);
				this.seriesToPosition.put(name, pos + .5d);
				pos++;
			}
			this.majorTicksStale = true;
		} else {
			double max = Double.NaN;
			double min = Double.NaN;
			if (this.autoMaxValue) {
				switch (dimension) {
					case DIMENSION_X:
						max = getChart().getMaxX();
						break;
					case DIMENSION_Y:
						max = getChart().getMaxY();
						break;
					case DIMENSION_Z:
						max = getChart().getMaxZ();
						break;
				}
			} else
				max = this.maxValue;
			if (this.autoMinValue) {
				switch (dimension) {
					case DIMENSION_X:
						min = getChart().getMinX();
						break;
					case DIMENSION_Y:
						min = getChart().getMinY();
						break;
					case DIMENSION_Z:
						min = getChart().getMinZ();
						break;
				}
			} else
				min = this.minValue;
			double diff = max - min;
			if (diff == 0)
				diff = max == 0 ? 1 : max * .1;
			if (diff > 0) {
				if (this.autoMinValue || this.autoMaxValue) {
					double size = AmiWebChartUtils.calcUnitSize(getLength() / 50, diff);
					if (this.autoMaxValue)
						setMaxValue(roundUp(max + diff * .01, size));
					if (this.autoMinValue)
						setMinValue(roundDown(min - diff * .01, size));
					this.majorTicksStale = true;
				}
				if (formatterFormula.getFormulaCalc() == null) {
					if (diff < .01)
						this.currentFormatter = buildFormatter("formatNumber(n,\"0.0000\",\"\")");
					else if (diff < 100)
						this.currentFormatter = buildFormatter("formatNumber(n,\"0.00\",\"\")");
					else
						this.currentFormatter = buildFormatter("formatNumber(n,\"0\",\"\")");
				} else
					this.currentFormatter = formatterFormula.getFormulaCalc();
				if (this.autoMajorValue)
					setMajorUnit(AmiWebChartUtils.calcUnitSize(getLength() / 50, diff));
				if (this.autoMinorValue)
					setMinorUnit(AmiWebChartUtils.calcUnitSize(getLength() / 10, diff));
			}
		}
	}
	private DerivedCellCalculator buildFormatter(String numberFormula) {
		return this.chart.getScriptManager().toCalc(numberFormula, this.getFormulaVarTypes(null), this, null);
	}
	private int getLength() {
		return length;
	}

	public void setLength(int length) {
		if (this.length == length)
			return;
		this.length = length;
		this.majorTicksStale = true;
	}

	public static double roundDown(double val, double unit) {
		if (val < 0)
			return -roundUp(-val, unit);
		int mag = MH.getMagnitude(unit);
		double den;
		if (mag < 0)
			den = 1d / MH.toTheTenth(-mag);
		else
			den = MH.toTheTenth(mag - 1);
		int mod = (int) Math.round(unit / den);
		return MH.roundBy((int) (val / den), mod, MH.ROUND_DOWN) * den;
	}
	public static double roundUp(double val, double unit) {
		int mag = MH.getMagnitude(unit);
		double den;
		if (mag < 0)
			den = 1d / MH.toTheTenth(-mag);
		else
			den = MH.toTheTenth(mag - 1);
		int mod = (int) Math.round(unit / den);
		double r = MH.roundBy((int) Math.ceil(val / den), mod, MH.ROUND_UP) * den;
		return r;
	}

	public void init(Map<String, Object> configuration) {
		this.fontColor = CH.getOr(Caster_String.INSTANCE, configuration, "fontColor", this.fontColor);
		this.lineColor = CH.getOr(Caster_String.INSTANCE, configuration, "lineColor", this.lineColor);
		this.otherLineColor = CH.getOr(Caster_String.INSTANCE, configuration, "oLineColor", this.otherLineColor);
		this.autoMajorValue = CH.getOr(Caster_Boolean.INSTANCE, configuration, "autoMajorValue", this.autoMajorValue);
		this.autoMinorValue = CH.getOr(Caster_Boolean.INSTANCE, configuration, "autoMinorValue", this.autoMinorValue);
		this.autoMaxValue = CH.getOr(Caster_Boolean.INSTANCE, configuration, "autoMaxValue", this.autoMaxValue);
		this.autoMinValue = CH.getOr(Caster_Boolean.INSTANCE, configuration, "autoMinValue", this.autoMinValue);
		this.reverse = CH.getOr(Caster_Boolean.INSTANCE, configuration, "reverse", this.reverse);
		this.logrithmicBase = CH.getOr(Caster_Double.INSTANCE, configuration, "logBase", this.logrithmicBase);
		this.minValue = CH.getOr(Caster_Double.INSTANCE, configuration, "minValue", this.minValue);
		this.maxValue = CH.getOr(Caster_Double.INSTANCE, configuration, "maxValue", this.maxValue);
		this.minorUnit = CH.getOr(Caster_Double.INSTANCE, configuration, "minorUnit", this.minorUnit);
		this.majorUnit = CH.getOr(Caster_Double.INSTANCE, configuration, "majorUnit", this.majorUnit);
		this.startPadding = CH.getOr(Caster_Integer.INSTANCE, configuration, "startPadding", this.startPadding);
		this.endPadding = CH.getOr(Caster_Integer.INSTANCE, configuration, "endPadding", this.endPadding);
		this.fontStyle = CH.getOr(Caster_String.INSTANCE, configuration, "fontStyle", this.fontStyle);
		this.fontSize = CH.getOr(Caster_Integer.INSTANCE, configuration, "fontSize", this.fontSize);
		this.majorUnitSize = CH.getOr(Caster_Integer.INSTANCE, configuration, "majorUnitSize", this.majorUnitSize);
		this.minorUnitSize = CH.getOr(Caster_Integer.INSTANCE, configuration, "minorUnitSize", this.minorUnitSize);
		this.fontFamily = CH.getOr(Caster_String.INSTANCE, configuration, "fontFamily", this.fontFamily);
		this.textRotate = CH.getOr(Caster_Integer.INSTANCE, configuration, "textRotate", this.textRotate);
		this.textPadding = CH.getOr(Caster_Integer.INSTANCE, configuration, "textPadding", this.textPadding);
		this.title = CH.getOr(Caster_String.INSTANCE, configuration, "title", this.title);
		this.titleColor = CH.getOr(Caster_String.INSTANCE, configuration, "titleColor", this.titleColor);
		this.titleFont = CH.getOr(Caster_String.INSTANCE, configuration, "titleFont", this.titleFont);
		this.titlePadding = CH.getOr(Caster_Integer.INSTANCE, configuration, "titlePadding", this.titlePadding);
		this.titleSize = CH.getOr(Caster_Integer.INSTANCE, configuration, "titleSize", this.titleSize);
		this.formatterFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "format", null));
		this.isSeries = CH.getOr(Caster_Boolean.INSTANCE, configuration, "isSeries", false);
		//		this.buildFormatter(ormat);
		updateData();
		majorTicksStale = true;
	}

	public AmiWebService getService() {
		return this.chart.getService();
	}

	public double getLogrithmicBase() {
		return logrithmicBase;
	}

	public void setLogrithmicBase(double logrithmicBase) {
		if (this.logrithmicBase == logrithmicBase)
			return;
		this.logrithmicBase = logrithmicBase;
		this.majorTicksStale = true;
		flagRangeStale();
	}

	public double getMinValue() {
		if (isSeries)
			return 0;
		return minValue;
	}
	public void setMinValue(double minValue) {
		if (this.minValue == minValue)
			return;
		this.minValue = minValue;
		this.majorTicksStale = true;
		flagRangeStale();
	}

	public double getMaxValue() {
		if (isSeries && this.series != null)
			return this.series.size();
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		if (this.maxValue == maxValue)
			return;
		this.maxValue = maxValue;
		this.majorTicksStale = true;
		flagRangeStale();
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		if (this.reverse == reverse)
			return;
		this.reverse = reverse;
		this.majorTicksStale = true;
		flagRangeStale();
	}

	public double getMajorUnit() {
		return majorUnit;
	}

	public void setMajorUnit(double majorUnit) {
		if (this.majorUnit == majorUnit)
			return;
		this.majorUnit = majorUnit;
		this.majorTicksStale = true;
		flagRangeStale();
	}

	public double getMinorUnit() {
		return minorUnit;
	}

	public void setMinorUnit(double minorUnit) {
		if (this.minorUnit == minorUnit)
			return;
		this.minorUnit = minorUnit;
		this.majorTicksStale = true;
		flagRangeStale();
	}

	public String getNumberFormula(boolean override) {
		return formatterFormula.getFormula(override);
	}

	public void setNumberFormula(String numberFormula) {
		this.formatterFormula.setFormula(numberFormula, false);
		//		if (OH.eq(this.format, numberFormula))
		//			return;
		//		buildFormatter(numberFormula);
		//		this.format = numberFormula;
		this.majorTicksStale = true;
		flagRangeStale();
	}

	public int getStartPadding() {
		return startPadding;
	}

	public void setStartPadding(int startPadding) {
		if (this.startPadding == startPadding)
			return;
		this.startPadding = startPadding;
		this.majorTicksStale = true;
		flagRangeStale();
	}

	public int getEndPadding() {
		return endPadding;
	}

	public void setEndPadding(int endPadding) {
		if (this.endPadding == endPadding)
			return;
		this.endPadding = endPadding;
		this.majorTicksStale = true;
		flagRangeStale();
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(String fontStyle) {
		if (OH.eq(this.fontStyle, fontStyle))
			return;
		this.fontStyle = fontStyle;
		flagViewStale();
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		if (this.fontSize == fontSize)
			return;
		this.fontSize = fontSize;
		flagViewStale();
	}

	public int getMajorUnitSize() {
		return majorUnitSize;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (OH.eq(this.title, title))
			return;
		this.title = title;
		flagViewStale();
	}

	public String getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(String titleColor) {
		if (OH.eq(this.titleColor, titleColor))
			return;
		this.titleColor = titleColor;
		flagViewStale();
	}

	public String getTitleFont() {
		return titleFont;
	}

	public void setTitleFont(String titleFont) {
		if (OH.eq(this.titleFont, titleFont))
			return;
		this.titleFont = titleFont;
		flagViewStale();
	}

	public int getTitlePadding() {
		return titlePadding;
	}

	public void setTitlePadding(int titlePadding) {
		if (this.titlePadding == titlePadding)
			return;
		this.titlePadding = titlePadding;
		flagViewStale();
	}

	public int getTitleSize() {
		return titleSize;
	}

	public void setTitleSize(int titleSize) {
		if (this.titleSize == titleSize)
			return;
		this.titleSize = titleSize;
		flagViewStale();
	}

	public void setMajorUnitSize(int majorUnitSize) {
		if (this.majorUnitSize == majorUnitSize)
			return;
		this.majorUnitSize = majorUnitSize;
		this.majorTicksStale = true;
		flagViewStale();
	}

	public int getMinorUnitSize() {
		return minorUnitSize;
	}

	public void setMinorUnitSize(int minorUnitSize) {
		if (this.minorUnitSize == minorUnitSize)
			return;
		this.minorUnitSize = minorUnitSize;
		flagViewStale();
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		if (OH.eq(this.fontFamily, fontFamily))
			return;
		this.fontFamily = fontFamily;
		flagViewStale();
	}

	public int getTextRotate() {
		return textRotate;
	}

	public void setTextRotate(int textRotate) {
		if (this.textRotate == textRotate)
			return;
		this.textRotate = textRotate;
		flagViewStale();
	}

	public int getTextPadding() {
		return textPadding;
	}

	public void setTextPadding(int textPadding) {
		if (this.textPadding == textPadding)
			return;
		this.textPadding = textPadding;
		flagViewStale();
	}

	public boolean getAutoMinValue() {
		return autoMinValue;
	}

	public void setAutoMinValue(boolean autoMinValue) {
		if (this.autoMinValue == autoMinValue)
			return;
		this.autoMinValue = autoMinValue;
		this.flagRangeStale();
	}

	public boolean getAutoMaxValue() {
		return autoMaxValue;
	}

	public void setAutoMaxValue(boolean autoMaxValue) {
		if (this.autoMaxValue == autoMaxValue)
			return;
		this.autoMaxValue = autoMaxValue;
		this.flagRangeStale();
	}

	public boolean getAutoMinorValue() {
		return autoMinorValue;
	}

	public void setAutoMinorValue(boolean autoMinorValue) {
		if (this.autoMinorValue == autoMinorValue)
			return;
		this.autoMinorValue = autoMinorValue;
		this.flagRangeStale();
	}

	public boolean getAutoMajorValue() {
		return autoMajorValue;
	}

	public void setAutoMajorValue(boolean autoMajorValue) {
		if (this.autoMajorValue == autoMajorValue)
			return;
		this.autoMajorValue = autoMajorValue;
		this.flagRangeStale();
	}

	public String getFontColor() {
		return this.fontColor;
	}
	public void setFontColor(String fontColor) {
		if (OH.eq(this.fontColor, fontColor))
			return;
		this.fontColor = fontColor;
		flagViewStale();
	}
	public String getLineColor() {
		return this.lineColor;
	}
	public void setLineColor(String lineColor) {
		if (OH.eq(this.lineColor, lineColor))
			return;
		this.lineColor = lineColor;
		flagViewStale();
	}
	public String getOtherLineColor() {
		return this.otherLineColor;
	}
	public void setOtherLineColor(String otherLineColor) {
		if (OH.eq(this.otherLineColor, otherLineColor))
			return;
		this.otherLineColor = otherLineColor;
		flagViewStale();
	}

	public AmiWebSurfacePortlet getChart() {
		return this.chart;
	}

	public void flagRangeStale() {
		if (this.rangeStale)
			return;
		flagViewStale();
	}
	public void flagViewStale() {
		this.chart.flagViewStale();
		if (this.isViewStale)
			return;
		this.isViewStale = true;
	}
	public boolean getIsSeries() {
		return isSeries;
	}

	public void setIsSeries(boolean isSeries) {
		if (this.isSeries == isSeries)
			return;
		this.isSeries = isSeries;
		this.flagRangeStale();
		majorTicksStale = true;
	}
	public double getSeriesPosition(String string) {
		return seriesToPosition.get(string);
	}
	public void onChartInitDone() {
	}

	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.chart.getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.chart.getAmiLayoutFullAliasDotId() + "?" + this.getDomLabel();
		this.ari = AmiWebDomObject.ARI_TYPE_CHART_AXIS + ":" + this.amiLayoutFullAliasDotId;
		chart.getService().getDomObjectsManager().fireAriChanged(this, oldAri);
	}
	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return null;
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
	public boolean isTransient() {
		return this.chart.isTransient();
	}
	@Override
	public void setTransient(boolean isTransient) {
		throw new UnsupportedOperationException("Invalid operation");
	}

	private boolean isManagedByDomManager = false;
	final private AmiWebFormulasImpl formulas;
	private String amiLayoutFullAlias;
	private String amiLayoutFullAliasDotId;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.chart.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;
		}
	}
	@Override
	public void removeFromDomManager() {
		getService().getDomObjectsManager().fireRemoved(this);
		if (this.isManagedByDomManager == true) {
			AmiWebService service = this.chart.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}
	@Override
	public AmiWebFormulas getFormulas() {
		return this.formulas;
	}
	@Override
	public String getAmiLayoutFullAlias() {
		return this.amiLayoutFullAlias;
	}
	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.amiLayoutFullAliasDotId;
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return AmiWebChartAxisPortlet.VARTYPES;
	}

	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_CHART_AXIS;
	}

	@Override
	public String getDomLabel() {
		switch (dimension) {
			case DIMENSION_X:
				return "X";
			case DIMENSION_Y:
				return "Y";
			case DIMENSION_Z:
				return "Z";
			default:
				throw new RuntimeException("Unknown: " + dimension);
		}
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}
	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.getChart();
	}
	@Override
	public Class<?> getDomClassType() {
		return AmiWebChartAxisPortlet.class;
	}
	@Override
	public Object getDomValue() {
		return this;
	}
	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		this.chart.flagViewStale();
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
