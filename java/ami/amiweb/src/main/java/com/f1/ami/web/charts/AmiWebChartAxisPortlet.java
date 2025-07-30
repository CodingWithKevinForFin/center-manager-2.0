package com.f1.ami.web.charts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebFormatterManager;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.AmiWebStyledPortletPeer;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_ChartAxis;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.utils.CH;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.math.BigDecimalMath;
import com.f1.utils.structs.table.derived.DerivedHelper;

public class AmiWebChartAxisPortlet extends AbstractPortlet implements AmiWebManagedPortlet, ConfirmDialogListener, AmiWebStyledPortlet, AmiWebDomObject {

	public static final com.f1.base.CalcTypes VARTYPES = new com.f1.utils.structs.table.stack.SingletonCalcTypes(AmiWebChartAxisPortlet.VAR_NAME, Double.class);

	private static final BigDecimal TWO = new BigDecimal(2);
	static final String VAR_NAME = "n";
	public static byte TYPE_NUMERIC = 1;
	public static byte TYPE_LOGRITHMIC = 2;
	public static byte TYPE_POSITION = 3;
	final public static byte FORMAT_TYPE_NUMERIC = 0;
	final public static byte FORMAT_TYPE_DATETIME = 1;
	final public static byte FORMAT_TYPE_DATE = 2;
	final public static byte FORMAT_TYPE_TIME = 3;
	final public static byte FORMAT_TYPE_CUSTOM = 4;
	final public static byte FORMAT_TYPE_AUTO = 5;

	public static final PortletSchema<AmiWebChartAxisPortlet> SCHEMA = new BasicPortletSchema<AmiWebChartAxisPortlet>("AmiAxisPortlet");
	private int axisId;
	private boolean isVertical;
	private AmiWebChartGridPortlet chart;
	private byte position;
	private int offset;//FROM LEFT OR TOP
	private int rowOrCol;

	private AmiWebOverrideValue<Boolean> autoMinValue = new AmiWebOverrideValue<Boolean>(true);
	private AmiWebOverrideValue<Boolean> autoMaxValue = new AmiWebOverrideValue<Boolean>(true);
	private AmiWebOverrideValue<Boolean> autoMinorValue = new AmiWebOverrideValue<Boolean>(true);
	private AmiWebOverrideValue<Boolean> autoMajorValue = new AmiWebOverrideValue<Boolean>(true);
	private AmiWebOverrideValue<Double> minValue = new AmiWebOverrideValue<Double>(0d); //TODO: DOUBLES ARE NOT ACCURATE ENOUGH FOR LARGE NUMBERS
	private AmiWebOverrideValue<Double> maxValue = new AmiWebOverrideValue<Double>(100d); //TODO: DOUBLES ARE NOT ACCURATE ENOUGH FOR LARGE NUMBERS
	private AmiWebOverrideValue<BigDecimal> minValueBd = new AmiWebOverrideValue<BigDecimal>(b(minValue.get())); //TODO: DOUBLES ARE NOT ACCURATE ENOUGH FOR LARGE NUMBERS
	private AmiWebOverrideValue<BigDecimal> maxValueBd = new AmiWebOverrideValue<BigDecimal>(b(maxValue.get())); //TODO: DOUBLES ARE NOT ACCURATE ENOUGH FOR LARGE NUMBERS

	private double zoom = 1;//zoom factor, 2 = everything is double the size
	//		Note this.getChartZoom().getZoom(); stores the same zoom
	private double zoomLocation = 0;//pixels to the left after zoom.

	private boolean isZoomed = false;
	private boolean reverse;
	private double currentMajorUnit;
	private AmiWebOverrideValue<Double> majorUnit = new AmiWebOverrideValue<Double>(10d);
	private AmiWebOverrideValue<Double> minorUnit = new AmiWebOverrideValue<Double>(2d);
	private String format;
	private char orientation;

	private AmiWebOverrideValue<String> title = new AmiWebOverrideValue<String>("");
	private int titleSize = 20;
	private String titleFontFamily = "arial";
	private String titleColor = "#000000";
	private int titlePadding = 35;
	private int titleRotate = 90;

	private int labelFontSize = 12;
	private String labelFontFamily = "arial";
	private String labelFontColor = "#000000";
	private int labelPadding = 32;
	private int labelRotate = 90;
	private String labelFontStyle = "";//TODO: NOT USED 

	private int numberFontSize = 10;
	private String numberFontFamily = "georgia";
	private String numberFontColor = "#000000";
	private int numberPadding = 0;
	private int numberRotate = 0;

	private int labelTickSize;
	private int majorUnitTickSize = 10;
	private int minorUnitTickSize = 5;
	private String bgColor = "#FFFFFF";
	private String lineColor = "#000000";

	private int groupPadding = 20;
	private int startPadding = 0;
	private int endPadding = 0;
	private AmiWebChartAxisFormatter axisFormatter;
	private byte formatType = FORMAT_TYPE_AUTO;

	private byte status = STATUS_DATA_CHANGED;
	private static final byte STATUS_NO_CHANGE = 0;
	private static final byte STATUS_OFFSET_CHANGED = 1;//offset changed
	private static final byte STATUS_VIEW_CHANGED = 2;//zoom of window size changed
	private static final byte STATUS_CONFIG_CHANGED = 3;//the range changed, due to a stying change
	private static final byte STATUS_DATA_CHANGED = 4;//we need to re-evaluate the underlying data

	private List<String> series = new ArrayList<String>();
	private Map<Object, Integer> seriesToPosition = new HashMap<Object, Integer>();
	final private AmiWebStyledPortletPeer stylePeer;
	final private AmiWebDesktopPortlet desktop;
	private DoubleArrayList majorTicks = new DoubleArrayList();
	private ArrayList<BigDecimal> majorTicksBd = new ArrayList<BigDecimal>();
	private double positionFactor1;
	private double positionFactor2;
	private double positionFactor3;
	private BigDecimal positionFactor1Bd;
	private BigDecimal positionFactor2Bd;
	private BigDecimal positionFactor3Bd;
	private boolean positionFactorsNeedCalculating = true;
	private byte axisFormatterType;
	private boolean isGroupOrdered = true;
	private int seriesMaxLength;
	private String ari;
	private boolean needsBdMath;

	public AmiWebChartAxisPortlet(AmiWebChartGridPortlet amiWebChartGridPortlet, int axisId, PortletConfig config, byte position, int rowOrCol, int offset) {
		super(config);
		this.chart = amiWebChartGridPortlet;
		this.formulas = new AmiWebFormulasImpl(this);
		axisFormatter = new AmiWebChartAxisPortlet_Auto(this);
		this.formulas.addOrReplaceFormula(this.axisFormatter);
		this.axisId = axisId;
		this.position = position;
		this.offset = offset;
		this.rowOrCol = rowOrCol;
		this.isVertical = position == AmiWebChartGridPortlet.POS_L || position == AmiWebChartGridPortlet.POS_R;
		this.reverse = isVertical;
		if (isVertical)
			this.numberRotate = 0;
		switch (position) {
			case AmiWebChartGridPortlet.POS_T:
				this.orientation = 'B';
				break;
			case AmiWebChartGridPortlet.POS_B:
				this.orientation = 'T';
				break;
			case AmiWebChartGridPortlet.POS_L:
				this.orientation = 'R';
				break;
			case AmiWebChartGridPortlet.POS_R:
				this.orientation = 'L';
				break;
		}

		this.desktop = AmiWebUtils.getService(config.getPortletManager()).getDesktop();
		this.stylePeer = new AmiWebStyledPortletPeer(this, this.desktop.getService());
		this.stylePeer.initStyle();
	}
	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}

	public boolean isVertical() {
		return isVertical;
	}

	@Override
	public boolean onAmiContextMenu(String id) {
		if ("delete".equals(id)) {
			int deps = 0;
			for (AmiWebChartPlotPortlet i : getChart().getPlots())
				for (AmiWebChartRenderingLayer r : i.getRenderyingLayers())
					if (r.getXAxisId() == this.axisId || r.getYAxisId() == this.axisId)
						deps++;
			String msg = "Delete this axis?";
			if (deps > 0)
				getManager().showAlert("Can not delete axis because  there are  <B>" + deps + " rendering layer(s)</B> using it.");
			else
				getManager().showDialog("Delete Axis", new ConfirmDialogPortlet(generateConfig(), msg, ConfirmDialogPortlet.TYPE_YES_NO, this).setCallback("DELETE"));
			return true;
		} else if ("add_before".equals(id)) {
			chart.addAxis(position, rowOrCol, offset);
			return true;
		} else if ("add_after".equals(id)) {
			chart.addAxis(position, rowOrCol, offset + 1);
			return true;
		} else if ("edit".equals(id)) {
			getManager().showDialog("Edit Axis", new AmiWebChartEditAxisPortlet(generateConfig(), this)).setShadeOutside(false);
			return true;
		} else if ("style".equals(id)) {
			AmiWebUtils.showStyleDialog("Axis Style", this, new AmiWebEditStylePortlet(this.stylePeer, generateConfig()), generateConfig());
			return true;
		}
		return false;
	}
	@Override
	public void populateLowerConfigMenu(WebMenu headMenu) {
		if (!getChart().isReadonlyLayout()) {
			if (!isTransient()) {
				headMenu.add(new BasicWebMenuLink("Edit Highlighted Axis...", true, "edit"));
				headMenu.add(new BasicWebMenuLink("Style...", true, "style"));
				BasicWebMenu t = (BasicWebMenu) new BasicWebMenu("Add Axis", true).setBackgroundImage(AmiWebConsts.ICON_ADD);
				t.add(new BasicWebMenuLink(isVertical() ? "Left" : "Above", true, "add_before"));
				t.add(new BasicWebMenuLink(isVertical() ? "Right" : "Below", true, "add_after"));
				headMenu.add(t);
			}
			headMenu.add(new BasicWebMenuLink("Delete Highlighted Axis", true, "delete").setBackgroundImage(AmiWebConsts.ICON_DELETE));
		}
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
	}

	@Override
	public String getConfigMenuTitle() {
		return "Chart Axis";
	}

	@Override
	public boolean getIsFreeFloatingPortlet() {
		return false;
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("DELETE".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				for (AmiWebChartPlotPortlet i : getChart().getPlots()) {
					for (AmiWebChartRenderingLayer r : i.getRenderyingLayers()) {
						if (r.getXAxisId() == this.axisId)
							r.setXAxis(-1);
						if (r.getYAxisId() == this.axisId)
							r.setYAxis(-1);
					}
				}
				this.chart.removeAxis(position, rowOrCol, offset);
			}
			return true;
		}
		return false;
	}

	public byte getPosition() {
		return position;
	}

	public void setPosition(byte position) {
		this.position = position;
		updateAri();
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
		updateAri();
	}

	public int getRowOrCol() {
		return rowOrCol;
	}

	public void setRowOrCol(int rowOrCol) {
		this.rowOrCol = rowOrCol;
	}

	public String getBgColor() {
		return bgColor;
	}

	public double[] getLabelTicks() {
		int pos = 0, size = this.seriesToPosition.size();
		double[] r = new double[size];
		for (int i = 0; i < size; i++)
			r[pos++] = getGroupMidpoint(i);
		return r;
	}

	public boolean isZoomed() {
		return isZoomed;
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible()) {
			switch (status) {
				case STATUS_DATA_CHANGED: {
					updateData();
					callInitJs();
					break;
				}
				case STATUS_CONFIG_CHANGED:
				case STATUS_VIEW_CHANGED: {
					callInitJs();
					break;
				}
				case STATUS_OFFSET_CHANGED: {
					double zoomOffset = getPositionOffset();
					callJsFunction("setZoomOffset").addParam(zoomOffset).end();
					callJsFunction("drawLines").end();
					break;
				}
			}
			status = STATUS_NO_CHANGE;
		}
	}

	@Override
	public void setVisible(boolean isVisible) {
		if (!getVisible() && isVisible)
			flagViewStale();

		super.setVisible(isVisible);
	}

	@Override
	protected void initJs() {
		super.initJs();
		if (this.getWidth() != 0 || this.getHeight() != 0) {
			callJsSetSize();
		}
		if (getVisible()) {
			flagViewStale();
			flagPendingAjax();
		}
	}
	//use get() for transient fields
	private void callInitJs() {

		final double bucketSizeAfterPadding = getLengthOfGroupAfterPaddingAfterZoom();

		double zoomOffset = getPositionOffset();
		callJsFunction("setZoomOffset").addParam(zoomOffset).end();
		JsFunction func = callJsFunction("init");
		JsonBuilder json = func.startJson();
		json.startMap();

		double min = getMinValue();
		double max = getMaxValue();
		double diff = max - min;
		{
			double majorUnit = this.majorUnit.get();
			double minorUnit = this.minorUnit.get();

			if (isZoomed || autoMajorValue.get()) {
				majorUnit = (this.axisFormatter.calcUnitSize(bucketSizeAfterPadding / this.getAutoMinBucketSize(), diff, this.axisFormatterType));
			}
			this.axisFormatter.useDefaultFormatter(majorUnit, min, max, this.axisFormatterType);
			if (isZoomed || autoMinorValue.get()) {
				minorUnit = this.axisFormatter.calcMinorUnitSize(majorUnit);
			}
			if (majorUnit <= 0) {
				majorUnit = 0;
			} else if (bucketSizeAfterPadding * majorUnit / diff < 2) {
				majorUnit = 2 * diff / bucketSizeAfterPadding;
			}

			this.currentMajorUnit = majorUnit;

			double minorUnitValue = minorUnit * bucketSizeAfterPadding / diff;
			json.addKeyValue("minorUnit", minorUnitValue);
			json.addKeyValue("minorUnitCount", minorUnit >= majorUnit || minorUnitValue < 1.5 ? 0 : ((majorUnit / minorUnit) - 1));
		}

		json.addKeyValueQuoted("bgColor", this.bgColor);
		json.addKeyValueQuoted("lineColor", this.lineColor);
		json.addKeyValue("reverse", this.reverse);
		json.addKeyValueQuoted("orientation", this.orientation);

		json.addKeyValueQuoted("title", this.title.get());
		json.addKeyValue("titleSize", this.titleSize);
		json.addKeyValueQuoted("titleFont", this.titleFontFamily);
		json.addKeyValueQuoted("titleColor", this.titleColor);
		json.addKeyValue("titlePadding", this.titlePadding);
		json.addKeyValue("titleRotate", this.titleRotate);

		json.addKeyValue("labelFontSize", this.labelFontSize);
		json.addKeyValueQuoted("labelFontFamily", this.labelFontFamily);
		json.addKeyValueQuoted("labelFontColor", this.labelFontColor);
		json.addKeyValue("labelPadding", this.labelPadding);
		json.addKeyValue("labelRotate", this.labelRotate);

		json.addKeyValue("numberFontSize", this.numberFontSize);
		json.addKeyValueQuoted("numberFontFamily", this.numberFontFamily);
		json.addKeyValue("numberPadding", this.numberPadding);
		json.addKeyValue("numberRotate", this.numberRotate);
		json.addKeyValueQuoted("numberFontColor", this.numberFontColor);

		json.addKeyValue("majorUnitSize", this.majorUnitTickSize);
		json.addKeyValue("minorUnitSize", this.minorUnitTickSize);
		json.addKeyValue("labelTickSize", this.labelTickSize);

		json.addKeyValueQuoted("labelFontStyle", this.labelFontStyle);//NOT USED
		json.endMap();
		func.end();

		func = callJsFunction("setLabels");
		func.addParam(bucketSizeAfterPadding);

		ensureFactorsCalculated();
		{
			double bucketSize = positionFactor1;

			double t = (this.labelFontSize * 1.5d) / Math.cos(this.labelRotate / 180d * Math.PI);
			t = Math.min(t, this.seriesMaxLength * this.labelFontSize * .6d);
			int n = Math.max(1, (int) (t / Math.abs(bucketSize) / this.zoom));
			int skip;
			if (n >= 1073741824)
				skip = 1073741824;
			else
				for (skip = 1; skip < n; skip *= 2)
					;

			func.addParam(this.series.size());
			json = func.startJson();
			json.startList();
			for (int i = 0; i < this.series.size(); i += skip) {
				json.startMap();
				json.addKeyValue("l", (bucketSize * i + this.positionFactor3) * this.zoom);
				json.addKeyValueQuoted("n", this.series.get(i));
				json.endMap();
			}
			json.endList();
		}
		this.majorTicks.clear();
		this.majorTicksBd.clear();
		if (bucketSizeAfterPadding > 1 && currentMajorUnit > 0) {
			if (this.needsBdMath) {
				BigDecimal bdMin = minValueBd.get();
				BigDecimal bdMax = maxValueBd.get();
				BigDecimal bgCmu = new BigDecimal(currentMajorUnit);
				BigDecimal end = bdMin.max(bdMax.subtract(bgCmu.divide(TWO)));
				BigDecimal bdInc = new BigDecimal(currentMajorUnit);
				BigDecimal i = bdMin;
				while (i.compareTo(end) <= 0) {
					this.majorTicksBd.add(i);
					i = i.add(bdInc);
				}
				if (bdMax.subtract(bdMin).compareTo(bgCmu.divide(TWO)) > 0 || this.majorTicksBd.size() == 1)
					this.majorTicksBd.add(bdMax);
			} else {
				double end = Math.max(maxValue.get() - currentMajorUnit * .5, minValue.get());
				double i = minValue.get();
				while (i <= end) {
					this.majorTicks.add(i);
					i += currentMajorUnit;
				}
				if (maxValue.get() - minValue.get() > currentMajorUnit * .5 || this.majorTicks.size() == 1)
					this.majorTicks.add(maxValue.get());
			}
		}
		if (min == max) {
			json = func.startJson();
			json.startList();
			json.startMap();
			json.addKeyValueQuoted("n", axisFormatter.formatExact(min));
			json.addKeyValue("p", getPosition(min) * this.zoom);
			json.endMap();
			json.endList();
			func.end();
		} else {
			json = func.startJson();
			json.startList();
			if (currentMajorUnit > 0) {
				int maxLength = 1;
				if (majorTicksBd.size() > 0) {
					for (int i = 0; i < this.majorTicksBd.size(); i++) {
						BigDecimal d = this.majorTicksBd.get(i);
						maxLength = Math.max(maxLength, SH.length(axisFormatter.format(d)));
					}
				} else {
					for (int i = 0; i < this.majorTicks.size(); i++) {
						double d = this.majorTicks.get(i);
						maxLength = Math.max(maxLength, SH.length(axisFormatter.format(d)));
					}
				}
				ensureFactorsCalculated();
				//				double t2 = positionFactor2 * this.currentMajorUnit * this.zoom;
				//				double t = (this.numberFontSize * 1.5d) / Math.cos(this.numberRotate / 180d * Math.PI);
				//				t = Math.min(t, maxLength * this.numberFontSize * .5d);
				//				int n = Math.max(1, (int) MH.round(t / Math.abs(t2), MH.ROUND_UP));

				BigDecimal t2 = positionFactor2Bd.multiply(b(this.currentMajorUnit * this.zoom));
				double t = (this.numberFontSize * 1.5d) / Math.cos(this.numberRotate / 180d * Math.PI);
				t = Math.min(t, maxLength * this.numberFontSize * .5d);
				int n = Math.max(1, b(t).divide(t2.abs(), 0, BigDecimal.ROUND_UP).intValue());
				int skip;
				for (skip = 1; skip < n; skip *= 2)
					;
				if (majorTicksBd.size() > 0) {
					int size = this.majorTicksBd.size();
					for (int i = 0; i < size; i++) {
						BigDecimal d = this.majorTicksBd.get(i);
						json.startMap();
						json.addKeyValueQuoted("n", (i == 0 || (i % skip == 0 && i <= size - skip) || i == size - 1) ? axisFormatter.format(d) : ""); //only include first,intervals,last
						json.addKeyValue("p", getPosition(d) * this.zoom);
						json.endMap();
					}
				} else {
					int size = this.majorTicks.size();
					for (int i = 0; i < size; i++) {
						double d = this.majorTicks.get(i);
						json.startMap();
						json.addKeyValueQuoted("n", (i == 0 || (i % skip == 0 && i <= size - skip) || i == size - 1) ? axisFormatter.format(d) : ""); //only include first,intervals,last
						json.addKeyValue("p", getPosition(d) * this.zoom);
						json.endMap();
					}
				}
			}
			json.endList();
			func.end();
		}
	}
	//used by graph
	public double[] getMajorTicks() {
		int pos = 0, size = this.seriesToPosition.size();
		if (majorTicksBd.size() > 0) {
			int size2 = this.majorTicksBd.size();
			double[] r = new double[size2 * size];
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size2; j++)
					r[pos++] = getPositionAt(i, this.majorTicksBd.get(j));
			return r;
		} else {
			int size2 = this.majorTicks.size();
			double[] r = new double[size2 * size];
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size2; j++)
					r[pos++] = getPositionAt(i, this.majorTicks.getDouble(j));
			return r;
		}
	}

	//always put the original value for AmiWebOverrideValue fields(AKA, PUT getValue()), don't put transient override values in 
	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("formatType", this.formatType);
		r.put("autoMajorValue", this.autoMajorValue.getValue());
		r.put("autoMinorValue", this.autoMinorValue.getValue());
		r.put("autoMaxValue", this.autoMaxValue.getValue());
		r.put("autoMinValue", this.autoMinValue.getValue());
		r.put("reverse", this.reverse);
		if (!this.autoMinValue.getValue())
			r.put("minValue", this.minValue.getValue());
		if (!this.autoMaxValue.getValue())
			r.put("maxValue", this.maxValue.getValue());
		if (!this.autoMinorValue.getValue())
			r.put("minorUnit", this.minorUnit.getValue());
		if (!this.autoMajorValue.getValue())
			r.put("majorUnit", this.majorUnit.getValue());
		r.put("orientation", this.orientation);
		r.put("fontStyle", this.labelFontStyle);
		r.put("title", this.title.getValue());
		r.put("axisId", this.axisId);
		r.put("format", this.format);
		r.put("amiStyle", this.stylePeer.getStyleConfiguration());
		r.put("isGroupOrdered", this.isGroupOrdered);
		return r;
	}

	//TODO: not sure if I should use get() or getValue() for override fields, I think it is get()
	private void updateData() {
		Set<Object> values = new LinkedHashSet<Object>();
		for (AmiWebChartPlotPortlet plot : getChart().getPlots()) {
			for (AmiWebChartRenderingLayer layer : plot.getRenderyingLayers()) {
				if (this.isVertical) {
					if (layer.getYAxisId() == this.axisId)
						((AmiWebChartRenderingLayer_Graph) layer).getUniqueYLabels(values);
				} else {
					if (layer.getXAxisId() == this.axisId)
						((AmiWebChartRenderingLayer_Graph) layer).getUniqueXLabels(values);
				}
			}
		}
		if (values.size() == 0)
			values.add(null);
		this.series.clear();
		this.seriesMaxLength = 1;
		this.seriesToPosition.clear();
		int pos = 0;
		Collection<Object> values2;
		if (this.isGroupOrdered)
			values2 = CH.sort((Set) values);
		else
			values2 = values;
		for (Object i : values2) {
			String name = formatTitle(i);
			if (name != null && name.length() > seriesMaxLength)
				seriesMaxLength = name.length();
			this.series.add(name);
			this.seriesToPosition.put(i, OH.valueOf(pos++));
		}

		axisFormatterType = AmiWebChartAxisFormatter.AXIS_DATA_TYPE_UNKNOWN;
		for (AmiWebChartPlotPortlet plot : getChart().getPlots()) {
			for (AmiWebChartRenderingLayer layer : plot.getRenderyingLayers()) {
				if (this.isVertical) {
					if (layer.getYAxisId() == this.axisId)
						axisFormatterType = MH.max(axisFormatterType, ((AmiWebChartRenderingLayer_Graph) layer).getTypeY());
				} else {
					if (layer.getXAxisId() == this.axisId)
						axisFormatterType = MH.max(axisFormatterType, ((AmiWebChartRenderingLayer_Graph) layer).getTypeX());
				}
			}
		}
		Number max = null;
		Number min = null;
		if (this.autoMaxValue.get()) {
			for (AmiWebChartPlotPortlet plot : getChart().getPlots()) {
				for (AmiWebChartRenderingLayer layer : plot.getRenderyingLayers()) {
					if (this.isVertical) {
						if (layer.getYAxisId() == this.axisId)
							max = MH.maxAvoidNull(max, ((AmiWebChartRenderingLayer_Graph) layer).getMaxY());
					} else {
						if (layer.getXAxisId() == this.axisId)
							max = MH.maxAvoidNull(max, ((AmiWebChartRenderingLayer_Graph) layer).getMaxX());
					}
				}
			}
		} else
			max = this.maxValueBd.get();
		if (this.autoMinValue.get()) {
			for (AmiWebChartPlotPortlet plot : getChart().getPlots()) {
				for (AmiWebChartRenderingLayer layer : plot.getRenderyingLayers()) {
					if (this.isVertical) {
						if (layer.getYAxisId() == this.axisId)
							min = MH.minAvoidNull(min, ((AmiWebChartRenderingLayer_Graph) layer).getMinY());
					} else {
						if (layer.getXAxisId() == this.axisId)
							min = MH.minAvoidNull(min, ((AmiWebChartRenderingLayer_Graph) layer).getMinX());
					}
				}
			}
		} else
			min = minValueBd.get();
		Number diff;

		if (min == null || max == null) {
			diff = null;
		} else {
			BigDecimal dbMin = BigDecimalMath.INSTANCE.cast(min);
			BigDecimal dbMax = BigDecimalMath.INSTANCE.cast(max);
			BigDecimal dbDiff = dbMax.subtract(dbMin);
			//			if (dbDiff.compareTo(BigDecimal.ZERO) == 0) {
			//				BigDecimal t = dbMin.multiply(b(.001d));
			//				min = dbMin.subtract(t);
			//				max = dbMax.add(t);
			//				diff = t.multiply(TWO);
			//			} else {
			min = dbMin;
			max = dbMax;
			diff = dbDiff.doubleValue();
			//			}
		}

		double lengthPerSeries = getLengthOfGroupAfterPaddingAfterZoom();
		if (diff != null) {
			if (this.autoMinValue.get() || this.autoMaxValue.get()) {
				if (diff.doubleValue() == 0) {
					setMaxValue(max);
					setMinValue(min);
				} else {
					double size = this.axisFormatter.calcUnitSize(lengthPerSeries / this.getAutoMinBucketSize(), diff, this.axisFormatterType);
					if (this.autoMaxValue.get())
						setMaxValue(this.axisFormatter.roundUp(max, size));
					if (this.autoMinValue.get())
						setMinValue(this.axisFormatter.roundDown(min, size));
				}
			}
			if (this.autoMajorValue.get())
				setMajorUnit(this.axisFormatter.calcUnitSize(lengthPerSeries / this.getAutoMinBucketSize(), diff, this.axisFormatterType));
			if (format == null)
				this.axisFormatter.useDefaultFormatter(this.currentMajorUnit, min, max, this.axisFormatterType);
		} else {
			if (autoMaxValue.get() || max == null)
				setMaxValue(100);
			if (autoMinValue.get() || min == null)
				setMinValue(0);
		}
		flagViewStale();
		this.positionFactorsNeedCalculating = true;
		for (AmiWebChartPlotPortlet plot : getChart().getPlots()) {
			for (AmiWebChartRenderingLayer layer : plot.getRenderyingLayers()) {
				if (this.isVertical) {
					if (layer.getYAxisId() == this.axisId)
						layer.flagDataStale();
				} else {
					if (layer.getXAxisId() == this.axisId)
						layer.flagDataStale();
				}
			}
		}
	}
	private double getAutoMinBucketSize() {
		return 4 + this.numberFontSize * 3;
	}

	private double getPositionOffset() {
		if (this.isReverse())
			return zoomLocation;
		else
			return zoomLocation;
	}

	public double getPosition(Object name, Number value) {
		Integer groupPosition = getSeriesPosition(name);
		return getPositionAt(groupPosition == null ? 0 : groupPosition.intValue(), value);
	}
	public double getPositionAt(int groupPosition, Number value) {
		ensureFactorsCalculated();
		return (double) groupPosition * this.positionFactor1 + getPosition(value) + this.positionFactor3;
	}
	private double getGroupMidpoint(int group) {
		ensureFactorsCalculated();
		return (double) this.positionFactor1 * (group + .5) + positionFactor3 + (isReverse() ? +this.groupPadding : -this.groupPadding);
	}

	private double getPosition(Number value) {
		ensureFactorsCalculated();
		if (this.positionFactor2 == 0)//there is only one point
			return this.positionFactor1 / 2;
		if (value instanceof BigDecimal || value.longValue() > Long.MAX_VALUE / 1000)
			return BigDecimalMath.INSTANCE.cast(value).subtract(this.minValueBd.get()).multiply(b(this.positionFactor2)).doubleValue();
		return (value.doubleValue() - this.minValue.get()) * this.positionFactor2;
	}
	private void ensureFactorsCalculated() {
		if (positionFactorsNeedCalculating && this.series.size() > 0) {
			this.positionFactorsNeedCalculating = false;
			BigDecimal rl = b(getRawLength());

			BigDecimal bdStartPadding = b(this.startPadding);
			BigDecimal bdEndPadding = b(this.endPadding);
			BigDecimal bdMaxValue = this.maxValueBd.get();
			BigDecimal bdMinValue = this.minValueBd.get();
			BigDecimal bdGroupPadding = b(this.groupPadding);
			BigDecimal lMinusPadding = rl.subtract(bdStartPadding).subtract(bdEndPadding);
			BigDecimal lenPerGroup = divide(lMinusPadding, b(this.series.size()));
			BigDecimal lenPerGroupMinusPadding = lenPerGroup.subtract(bdGroupPadding.multiply(TWO));
			BigDecimal diff = bdMaxValue.subtract(bdMinValue);
			this.positionFactor1Bd = lenPerGroup;
			this.positionFactor2Bd = diff.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : divide(lenPerGroupMinusPadding, diff);
			this.positionFactor3Bd = bdGroupPadding.add(bdStartPadding);
			if (reverse) {
				this.positionFactor1Bd = this.positionFactor1Bd.negate();
				this.positionFactor2Bd = this.positionFactor2Bd.negate();
				this.positionFactor3Bd = rl.subtract(this.positionFactor3Bd);
			}
			this.positionFactor1 = positionFactor1Bd.doubleValue();
			this.positionFactor2 = positionFactor2Bd.doubleValue();
			this.positionFactor3 = positionFactor3Bd.doubleValue();
			this.needsBdMath = needsBigMath(this.positionFactor1, this.positionFactor1Bd) || needsBigMath(this.positionFactor2, this.positionFactor2Bd)
					|| needsBigMath(this.positionFactor3, this.positionFactor3Bd);
		}
	}

	private static final BigDecimal BD_100 = BigDecimal.valueOf(100);
	private static final BigDecimal BD_0_9 = BigDecimal.valueOf(.9);
	private static final BigDecimal BD_1_1 = BigDecimal.valueOf(1.1);

	private boolean needsBigMath(double d, BigDecimal bd) {
		BigDecimal bd2 = b(d);
		if (OH.eq(bd2, bd))
			return true;
		if (OH.gt(bd2.subtract(bd).abs(), BD_100))
			return true;
		BigDecimal pct = divide(bd, bd2);
		if (OH.isntBetween(pct, BD_0_9, BD_1_1))
			return false;
		return true;
	}
	private BigDecimal divide(BigDecimal n, BigDecimal d) {
		return n.divide(d, MathContext.DECIMAL64);
	}
	private BigDecimal b(double rl) {
		return new BigDecimal(rl);
	}
	public static void main(String a[]) {
		long l = Long.MAX_VALUE;
		long t = (long) (l * .1);
		System.out.println(t);
		System.out.println((l - 1000) / 10);
		System.out.println((long) (((double) l - 1000) * .1));
		double d = (double) new BigDecimal(Long.MAX_VALUE).multiply(TWO).doubleValue();
		System.out.println(d);
		System.out.println((long) d);
		System.out.println(Long.MAX_VALUE);
	}

	private double getLengthOfGroupAfterPaddingAfterZoom() {
		ensureFactorsCalculated();
		double rl = getRawLength();
		double lMinusPadding = rl - this.startPadding - this.endPadding;
		double lenPerGroup = lMinusPadding / this.series.size();
		double lenPerGroupMinusPadding = lenPerGroup - this.groupPadding * 2d;
		return lenPerGroupMinusPadding * this.zoom;
	}

	//total length of graph in pixels
	private int getRawLength() {
		return this.isVertical ? getHeight() : getWidth();
	}
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		if (getVisible()) {
			flagRangeStale();
			flagViewStale();
		}
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		setFormatType(CH.getOr(Caster_Byte.INSTANCE, configuration, "formatType", (byte) 0));
		this.autoMajorValue = new AmiWebOverrideValue<Boolean>(CH.getOr(Caster_Boolean.INSTANCE, configuration, "autoMajorValue", this.autoMajorValue.getValue()));
		this.autoMinorValue = new AmiWebOverrideValue<Boolean>(CH.getOr(Caster_Boolean.INSTANCE, configuration, "autoMinorValue", this.autoMinorValue.getValue()));
		this.autoMaxValue = new AmiWebOverrideValue<Boolean>(CH.getOr(Caster_Boolean.INSTANCE, configuration, "autoMaxValue", this.autoMaxValue.getValue()));
		this.autoMinValue = new AmiWebOverrideValue<Boolean>(CH.getOr(Caster_Boolean.INSTANCE, configuration, "autoMinValue", this.autoMinValue.getValue()));
		this.reverse = CH.getOr(Caster_Boolean.INSTANCE, configuration, "reverse", this.reverse);
		this.minValue = new AmiWebOverrideValue<Double>(CH.getOr(Caster_Double.INSTANCE, configuration, "minValue", this.minValue.getValue()));
		this.minValueBd = new AmiWebOverrideValue<BigDecimal>(b(this.minValue.getValue()));
		this.maxValue = new AmiWebOverrideValue<Double>(CH.getOr(Caster_Double.INSTANCE, configuration, "maxValue", this.maxValue.getValue()));
		this.maxValueBd = new AmiWebOverrideValue<BigDecimal>(b(this.maxValue.getValue()));
		this.minorUnit = new AmiWebOverrideValue<Double>(CH.getOr(Caster_Double.INSTANCE, configuration, "minorUnit", this.minorUnit.getValue()));
		this.majorUnit = new AmiWebOverrideValue<Double>(CH.getOr(Caster_Double.INSTANCE, configuration, "majorUnit", this.majorUnit.getValue()));
		this.orientation = CH.getOr(Caster_Character.INSTANCE, configuration, "orientation", this.orientation);
		this.labelFontStyle = CH.getOr(Caster_String.INSTANCE, configuration, "fontStyle", this.labelFontStyle);
		this.title.set(CH.getOr(Caster_String.INSTANCE, configuration, "title", this.title.getValue()), true);
		this.axisId = CH.getOrThrow(Caster_Integer.INSTANCE, configuration, "axisId");
		this.chart.registerUsedId(this.axisId);
		this.format = CH.getOr(Caster_String.INSTANCE, configuration, "format", null);
		this.isGroupOrdered = CH.getOr(Caster_Boolean.INSTANCE, configuration, "isGroupOrdered", Boolean.FALSE);
		this.stylePeer.initStyle((Map<String, Object>) configuration.get("amiStyle"));
		this.positionFactorsNeedCalculating = true;
	}

	private boolean isInitDone = false;

	public void onAmiInitDone() {
		if (this.isInitDone)
			throw new IllegalStateException("already init done");
		this.isInitDone = true;
		this.onFormatChanged(format);

	}
	@Override
	public AmiWebService getService() {
		return this.chart.getService();
	}

	public double getMinValue() {
		return minValue.getValue();
	}
	public void setMinValue(Number minValue) {
		if (OH.eq(this.minValue.getValue(), minValue))
			return;
		this.minValueBd.set(BigDecimalMath.INSTANCE.cast(minValue), true);
		this.minValue.set(minValue.doubleValue(), true);
		flagRangeStale();
	}

	public void setMinValueOverride(Number minValue) {
		this.minValue.setOverride(minValue.doubleValue());
		this.minValueBd.setOverride(b(minValue.doubleValue()));
		flagRangeStale();
	}

	public double getMaxValue() {
		return maxValue.getValue();
	}

	public double getMaxValueOverride() {
		return maxValue.get();
	}

	//this goes/perists to config
	public void setMaxValue(Number maxValue) {
		if (OH.eq(this.maxValue.getValue(), maxValue))
			return;
		this.maxValueBd.set(BigDecimalMath.INSTANCE.cast(maxValue), true);
		this.maxValue.set(maxValue.doubleValue(), true);
		flagRangeStale();
	}

	public void setMaxValueOverride(Number maxValue) {
		this.maxValue.setOverride(maxValue.doubleValue());
		this.maxValueBd.setOverride(b(maxValue.doubleValue()));
		flagRangeStale();
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		if (this.reverse == reverse)
			return;
		this.reverse = reverse;
		flagRangeStale();
	}

	public double getMajorUnit() {
		return majorUnit.getValue();
	}

	public double getMajorUnitOverride() {
		return majorUnit.get();
	}

	public void setMajorUnit(double majorUnit) {
		if (OH.eq(this.majorUnit.getValue().doubleValue(), majorUnit))
			return;
		this.majorUnit.set(majorUnit, true);
		flagRangeStale();
	}

	public void setMajorUnitOverride(double majorUnit) {
		this.majorUnit.setOverride(majorUnit);
		flagRangeStale();
	}

	public double getMinorUnit() {
		return minorUnit.getValue();
	}

	public double getMinorUnitOverride() {
		return minorUnit.get();
	}

	public void setMinorUnit(double minorUnit) {
		if (OH.eq(this.minorUnit.getValue().doubleValue(), minorUnit))
			return;
		this.minorUnit.set(minorUnit, true);
		flagRangeStale();
	}

	public void setMinorUnitOverride(double minorUnit) {
		this.minorUnit.setOverride(minorUnit);
		flagRangeStale();
	}

	public String getNumberFormula() {
		return format;
	}

	public void setNumberFormula(String numberFormula) {
		if (OH.eq(this.format, numberFormula))
			return;
		onFormatChanged(numberFormula);
		this.format = numberFormula;
		flagRangeStale();
	}

	private void onFormatChanged(String numberFormula) {
		if (this.formatType == FORMAT_TYPE_CUSTOM)
			((AmiWebChartAxisPortlet_Custom) this.axisFormatter).onFormatChanged(numberFormula);
	}

	public int getStartPadding() {
		return startPadding;
	}

	public void setStartPadding(int startPadding) {
		if (this.startPadding == startPadding)
			return;
		this.startPadding = startPadding;
		flagRangeStale();
	}

	public int getEndPadding() {
		return endPadding;
	}

	public void setEndPadding(int endPadding) {
		if (this.endPadding == endPadding)
			return;
		this.endPadding = endPadding;
		flagRangeStale();
	}

	public String getFontStyle() {
		return labelFontStyle;
	}

	public void setFontStyle(String labelFontStyle) {
		if (OH.eq(this.labelFontStyle, labelFontStyle))
			return;
		this.labelFontStyle = labelFontStyle;
		flagViewStale();
	}
	public void setBgColor(String bgColor) {
		if (OH.eq(this.bgColor, bgColor))
			return;
		this.bgColor = bgColor;
		flagViewStale();
	}

	public int getLabelFontSize() {
		return labelFontSize;
	}

	public void setLabelFontSize(int labelFontSize) {
		if (this.labelFontSize == labelFontSize)
			return;
		this.labelFontSize = labelFontSize;
		flagViewStale();
	}

	public int getMajorUnitTickSize() {
		return majorUnitTickSize;
	}

	public String getTitle() {
		return title.getValue();
	}

	public String getTitleOverride() {
		return title.get();
	}

	public void setTitle(String title) {
		if (OH.eq(this.title.getValue(), title))
			return;
		this.title.set(title, true);
		flagViewStale();
	}

	public void setTitleOverride(String title) {
		this.title.setOverride(title);
		flagViewStale();
	}

	final public boolean clearOverride() {
		if (this.title.clearOverride()) {
			setTitle(title.get());
			return true;
		}
		return false;
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

	public String getTitleFontFamily() {
		return titleFontFamily;
	}

	public void setTitleFontFamily(String titleFontFamily) {
		if (OH.eq(this.titleFontFamily, titleFontFamily))
			return;
		this.titleFontFamily = titleFontFamily;
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

	public void setMajorUnitTickSize(int majorUnitTickSize) {
		if (this.majorUnitTickSize == majorUnitTickSize)
			return;
		this.majorUnitTickSize = majorUnitTickSize;
		flagViewStale();
	}

	public int getMinorUnitTickSize() {
		return minorUnitTickSize;
	}

	public void setMinorUnitSize(int minorUnitSize) {
		if (this.minorUnitTickSize == minorUnitSize)
			return;
		this.minorUnitTickSize = minorUnitSize;
		flagViewStale();
	}

	public String getLabelFontFamily() {
		return labelFontFamily;
	}

	public void setLabelFontFamily(String fontFamily) {
		if (OH.eq(this.labelFontFamily, fontFamily))
			return;
		this.labelFontFamily = fontFamily;
		flagViewStale();
	}

	public int getLabelRotate() {
		return labelRotate;
	}

	public void setLabeleRotate(int labelRotate) {
		if (this.labelRotate == labelRotate)
			return;
		this.labelRotate = labelRotate;
		flagViewStale();
	}

	public int getLabelPadding() {
		return labelPadding;
	}

	public void setLabelPadding(int textPadding) {
		if (this.labelPadding == textPadding)
			return;
		this.labelPadding = textPadding;
		flagViewStale();
	}

	public void setVertical(boolean isVertical) {
		if (this.isVertical == isVertical)
			return;
		this.isVertical = isVertical;
		updateAri();
		flagRangeStale();
	}

	public boolean getAutoMinValue() {
		return autoMinValue.getValue();
	}

	public boolean getAutoMinValueOverride() {
		return autoMinValue.get();
	}

	public void setAutoMinValue(boolean autoMinValue) {
		if (this.autoMinValue.getValue() == autoMinValue)
			return;
		this.autoMinValue.set(autoMinValue, true);
		onDataChanged();
	}

	public void setAutoMinValueOverride(boolean autoMinValue) {
		this.autoMinValue.setOverride(autoMinValue);
		onDataChanged();
	}

	public boolean getAutoMaxValue() {
		return autoMaxValue.getValue();
	}

	public boolean getAutoMaxValueOverride() {
		return autoMaxValue.get();
	}

	public void setAutoMaxValue(boolean autoMaxValue) {
		if (this.autoMaxValue.getValue() == autoMaxValue)
			return;
		this.autoMaxValue.set(autoMaxValue, true);
		onDataChanged();
	}

	public void setAutoMaxValueOverride(boolean autoMaxValue) {
		this.autoMaxValue.setOverride(autoMaxValue);
		onDataChanged();
	}

	public boolean getAutoMinorValue() {
		return autoMinorValue.getValue();
	}

	public boolean getAutoMinorValueOverride() {
		return autoMinorValue.get();
	}

	public void setAutoMinorValue(boolean autoMinorValue) {
		if (this.autoMinorValue.getValue() == autoMinorValue)
			return;
		this.autoMinorValue.set(autoMinorValue, true);
		onDataChanged();
	}

	public void setAutoMinorValueOverride(boolean autoMinorValue) {
		this.autoMinorValue.setOverride(autoMinorValue);
		onDataChanged();
	}

	public boolean getAutoMajorValue() {
		return autoMajorValue.getValue();
	}

	public boolean getAutoMajorValueOverride() {
		return autoMajorValue.get();
	}

	public void setAutoMajorValue(boolean autoMajorValue) {
		if (this.autoMajorValue.getValue() == autoMajorValue)
			return;
		this.autoMajorValue.set(autoMajorValue, true);
		onDataChanged();
	}

	public void setAutoMajorValueOverride(boolean autoMajorValue) {
		this.autoMajorValue.setOverride(autoMajorValue);
		onDataChanged();
	}

	public String getLabelFontColor() {
		return this.labelFontColor;
	}
	public void setLabelFontColor(String fontColor) {
		if (OH.eq(this.labelFontColor, fontColor))
			return;
		this.labelFontColor = fontColor;
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
	public int getAxisId() {
		return axisId;
	}

	public AmiWebChartGridPortlet getChart() {
		return this.chart;
	}

	public void onDataChanged() {
		if (setStatus(STATUS_DATA_CHANGED)) {
			this.positionFactorsNeedCalculating = true;
		}
	}

	private boolean setStatus(byte status) {
		if (status < this.status)
			return false;
		this.status = status;
		flagPendingAjax();
		return true;
	}
	private void flagRangeStale() {
		if (setStatus(STATUS_CONFIG_CHANGED)) {
			this.positionFactorsNeedCalculating = true;
			for (AmiWebChartPlotPortlet p : this.getChart().getPlots())
				for (AmiWebChartRenderingLayer i : p.getRenderyingLayers())
					if (i.getXAxisId() == axisId || i.getYAxisId() == axisId)
						i.flagViewStale();
		}
	}
	public void flagViewStale() {
		if (setStatus(STATUS_VIEW_CHANGED)) {
			this.positionFactorsNeedCalculating = true;
		}
	}
	public int getSeriesPosition(Object string) {
		Integer r = seriesToPosition.get(string);
		if (r == null)
			throw new RuntimeException("bad series: " + string);
		return r.intValue();
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("zoom".equals(callback)) {
			int pos = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "pos");
			int delta = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "delta");
			if (isVertical)
				this.chart.zoomAtPoint(-1, this.rowOrCol, -1, -1, pos, delta);
			else
				this.chart.zoomAtPoint(this.rowOrCol, -1, pos, delta, -1, -1);
		} else if ("zoomMove".equals(callback)) {
			int delta = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "delta");
			if (isVertical)
				this.chart.moveZoom(-1, this.rowOrCol, -1, delta);
			else
				this.chart.moveZoom(this.rowOrCol, -1, delta, -1);
			callJsFunction("zoomMoveConsumed").end();
		} else
			super.handleCallback(callback, attributes);
	}

	public int getTitleRotate() {
		return titleRotate;
	}
	public void setTitleRotate(int titleRotate) {
		if (OH.eq(this.titleRotate, titleRotate))
			return;
		this.titleRotate = titleRotate;
		flagViewStale();
	}
	public int getNumberRotate() {
		return numberRotate;
	}
	public void setNumberRotate(int numberRotate) {
		if (OH.eq(this.numberRotate, numberRotate))
			return;
		this.numberRotate = numberRotate;
		flagViewStale();
	}
	public int getNumberFontSize() {
		return numberFontSize;
	}
	public void setNumberFontSize(int numberFontSize) {
		if (OH.eq(this.numberFontSize, numberFontSize))
			return;
		this.numberFontSize = numberFontSize;
		flagViewStale();
		flagRangeStale();
	}
	public String getNumberFontFamily() {
		return numberFontFamily;
	}
	public void setNumberFontFamily(String numberFontFamily) {
		if (OH.eq(this.numberFontFamily, numberFontFamily))
			return;
		this.numberFontFamily = numberFontFamily;
		flagViewStale();
	}
	public String getNumberFontColor() {
		return numberFontColor;
	}
	public void setNumberFontColor(String numberFontColor) {
		if (OH.eq(this.numberFontColor, numberFontColor))
			return;
		this.numberFontColor = numberFontColor;
		flagViewStale();
	}
	public int getNumberPadding() {
		return numberPadding;
	}
	public void setNumberPadding(int numberPadding) {
		if (OH.eq(this.numberPadding, numberPadding))
			return;
		this.numberPadding = numberPadding;
		flagViewStale();
	}
	public int getLabelTickSize() {
		return labelTickSize;
	}
	public void setLabelTickSize(int labelTickSize) {
		if (OH.eq(this.labelTickSize, labelTickSize))
			return;
		this.labelTickSize = labelTickSize;
		flagViewStale();
	}
	public int getGroupPadding() {
		return groupPadding;
	}
	public void setGroupPadding(int groupPadding) {
		if (OH.eq(this.groupPadding, groupPadding))
			return;
		this.groupPadding = groupPadding;
		flagRangeStale();
	}
	@Override
	public void getUsedColors(Set<String> sink) {
		AmiWebUtils.getColors(this.bgColor, sink);
		AmiWebUtils.getColors(this.titleColor, sink);
		AmiWebUtils.getColors(this.labelFontColor, sink);
		AmiWebUtils.getColors(this.lineColor, sink);
		AmiWebUtils.getColors(this.numberFontColor, sink);
	}

	@Override
	public void onStyleValueChanged(short key, Object old, Object value) {
		if (value != null) {
			switch (key) {
				case AmiWebStyleConsts.CODE_AX_END_PD:
					setEndPadding(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_GROUP_PD:
					setGroupPadding(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_LBL_ROTATE:
					setLabeleRotate(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_LBL_FONT_CL:
					setLabelFontColor((String) value);
					break;
				case AmiWebStyleConsts.CODE_AX_LBL_FONT_FAM:
					setLabelFontFamily((String) value);
					break;
				case AmiWebStyleConsts.CODE_AX_LBL_FONT_SZ:
					setLabelFontSize(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_LBL_PD:
					setLabelPadding(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_LBL_TICK_SZ:
					setLabelTickSize(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_LINE_CL:
					setLineColor((String) value);
					break;
				case AmiWebStyleConsts.CODE_AX_MAJ_UNIT_SZ:
					setMajorUnitTickSize(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_MINOR_UNIT_SZ:
					setMinorUnitSize(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_NUM_FONT_CL:
					setNumberFontColor((String) value);
					break;
				case AmiWebStyleConsts.CODE_AX_NUM_FONT_FAM:
					setNumberFontFamily((String) value);
					break;
				case AmiWebStyleConsts.CODE_AX_NUM_FONT_SZ:
					setNumberFontSize(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_NUM_PD:
					setNumberPadding(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_NUM_ROTATE:
					setNumberRotate(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_START_PD:
					setStartPadding(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_TITLE_CL:
					setTitleColor((String) value);
					break;
				case AmiWebStyleConsts.CODE_AX_TITLE_FONT_FAM:
					setTitleFontFamily((String) value);
					break;
				case AmiWebStyleConsts.CODE_AX_TITLE_PD:
					setTitlePadding(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_TITLE_ROTATE:
					setTitleRotate(Caster_Integer.INSTANCE.cast(value));
					break;
				case AmiWebStyleConsts.CODE_AX_TITLE_SZ:
					setTitleSize(Caster_Integer.INSTANCE.cast(value));
					break;
			}
		}

	}
	@Override
	public AmiWebStyledPortletPeer getStylePeer() {
		return this.stylePeer;
	}
	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_ChartAxis.TYPE_CHART_AXIS;
	}
	@Override
	public void setShowConfigButtons(boolean showConfigButtons) {

	}
	@Override
	public boolean getShowConfigButtons() {
		return getChart().getShowConfigButtons();
	}
	public byte getFormatType() {
		return formatType;
	}
	public void setFormatType(byte formatType) {
		this.formatType = formatType;
		switch (this.formatType) {
			case FORMAT_TYPE_AUTO:
				this.axisFormatter = new AmiWebChartAxisPortlet_Auto(this);
				break;
			case FORMAT_TYPE_CUSTOM:
				this.axisFormatter = new AmiWebChartAxisPortlet_Custom(this);
				break;
			case FORMAT_TYPE_NUMERIC:
				this.axisFormatter = new AmiWebChartAxisPortlet_Number(this);
				break;
			case FORMAT_TYPE_TIME:
				this.axisFormatter = new AmiWebChartAxisPortlet_Time(this);
				break;
			case FORMAT_TYPE_DATE:
				this.axisFormatter = new AmiWebChartAxisPortlet_Date(this);
				break;
			case FORMAT_TYPE_DATETIME:
				this.axisFormatter = new AmiWebChartAxisPortlet_Datetime(this, true);
				break;
		}
		this.formulas.addOrReplaceFormula(this.axisFormatter);
		onDataChanged();
	}
	public void onUserZoomed(AmiWebChartZoom zoom) {

		if (this.zoom != zoom.getZoom()) {
			this.zoomLocation = zoom.getOffset();
			this.zoom = zoom.getZoom();
			this.flagViewStale();
		} else if (this.zoomLocation != zoom.getOffset()) {
			this.zoomLocation = zoom.getOffset();
			this.setStatus(STATUS_OFFSET_CHANGED);
		}
	}
	private AmiWebChartZoom getChartZoom() {
		if (isVertical)
			return this.chart.getZoomY(this.rowOrCol);
		else
			return this.chart.getZoomX(this.rowOrCol);
	}
	public double getZoom() {
		return this.zoom;
	}
	public double getZoomLocation() {
		return this.zoomLocation;
	}
	public double getVisibleLength() {
		return this.getChartZoom().getLength();
	}
	public double getVisibleMinValue() {
		//		if (this.isReverse())
		//			return this.getVisibleMaxValue();
		double totalValueRange = this.maxValue.get() - this.minValue.get();
		double totalPxRange = this.getVisibleLength() * this.zoom;
		double percent;
		if (this.isReverse())
			percent = 1 - (MH.abs(this.getZoomLocation()) + this.getVisibleLength()) / totalPxRange;
		else
			percent = MH.abs(this.getZoomLocation()) / totalPxRange;

		double visibleMin = this.minValue.get() + (totalValueRange * percent);

		return visibleMin;
	}
	public double getVisibleMaxValue() {
		//		if (this.isReverse())
		//			return this.getVisibleMinValue();
		double totalValueRange = this.maxValue.get() - this.minValue.get();
		double totalPxRange = this.getVisibleLength() * this.zoom;
		double percent;
		if (this.isReverse())
			percent = MH.abs(this.getZoomLocation()) / totalPxRange;
		else
			percent = 1 - (MH.abs(this.getZoomLocation()) + this.getVisibleLength()) / totalPxRange;

		double visibleMax = this.maxValue.get() - (totalValueRange * percent);
		return visibleMax;
	}
	public void setZoom(double zoom) {
		if (zoom < 1.0)
			zoom = 1.0;
		else if (zoom > AmiWebChartZoom.MAX_ZOOM)
			zoom = AmiWebChartZoom.MAX_ZOOM;
		if (this.zoom == zoom)
			return;
		this.zoom = zoom;
		if (this.isVertical)
			this.chart.setZoomAndOffset(-1, this.rowOrCol, -1, -1, this.zoom, this.zoomLocation);
		else
			this.chart.setZoomAndOffset(this.rowOrCol, -1, this.zoom, this.zoomLocation, -1, -1);
		this.flagViewStale();
		//		this.chart.zoomAtPoint(col, row, x, xdelta, y, ydelta);
		//		this.chart.moveZoom(col, row, xoffsetDelta, yoffsetDelta);
		//		this.chart.setZoomAndOffset(col, row, xzoom, xoffset, yzoom, yoffset);
		//		this.chart.setZoomClip(col, row, xstart, xend, ystart, yend);
	}
	public void setZoomLocation(double zoomLocation) {
		double lowerOffset = this.getVisibleLength() - (this.getVisibleLength() * this.zoom); // should return negative offset
		if (zoomLocation > 0)
			zoomLocation = 0;
		if (zoomLocation < lowerOffset)
			zoomLocation = lowerOffset;
		if (this.zoomLocation == zoomLocation)
			return;
		this.zoomLocation = zoomLocation;
		if (this.isVertical)
			this.chart.setZoomAndOffset(-1, this.rowOrCol, -1, -1, this.zoom, this.zoomLocation);
		else
			this.chart.setZoomAndOffset(this.rowOrCol, -1, this.zoom, this.zoomLocation, -1, -1);
		this.flagViewStale();
	}

	public void setZoomByMinMax(double min, double max) {
		if (min >= max)
			return;
		//Fist have to calculate the new zoom by taking the total range divided by the visible range
		double totalVisibleValue = max - min;
		double newZoom = (this.maxValue.get() - this.minValue.get()) / totalVisibleValue;

		double percent;
		if (this.isReverse())
			percent = (this.maxValue.get() - max) / totalVisibleValue;
		else
			percent = (min - this.minValue.get()) / totalVisibleValue;

		// Calculate the new location by calculating the percent offset based on difference in value vs total value
		// Then taking the visible length and multiplying by that percent
		double newLocation = -(getVisibleLength() * percent);

		if (this.zoom == newZoom && this.zoomLocation == newLocation)
			return;
		this.zoom = newZoom;
		this.zoomLocation = newLocation;
		if (this.isVertical)
			this.chart.setZoomAndOffset(-1, this.rowOrCol, -1, -1, this.zoom, this.zoomLocation);
		else
			this.chart.setZoomAndOffset(this.rowOrCol, -1, this.zoom, this.zoomLocation, -1, -1);
		this.flagViewStale();
	}

	public void ensureDataProcessed() {
		if (this.status == STATUS_DATA_CHANGED) {
			updateData();
		}
	}
	public boolean getGroupOrdering() {
		return this.isGroupOrdered;
	}
	public void setGroupOrdering(boolean b) {
		if (b == this.isGroupOrdered)
			return;
		this.isGroupOrdered = b;
		onDataChanged();
	}
	private String formatTitle(Object i) {
		if (i == null)
			return null;
		AmiWebFormatterManager formatterManager = this.getService().getFormatterManager();
		if (i instanceof DateMillis || i instanceof DateNanos)
			return formatterManager.getDatetimeFormatter().format(i);
		else if (i instanceof Number) {
			if (i instanceof Double || i instanceof Float || i instanceof BigDecimal)
				return formatterManager.getDecimalFormatter().format(i);
			return formatterManager.getIntegerFormatter().format(i);
		}
		return AmiUtils.s(i);
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
		return getSide() + "-" + getRowOrCol() + "," + getOffset();
	}
	private String getDistance() {
		return SH.toString(this.offset);
	}
	private String getSide() {
		return getSide(this.position);
	}
	public static String getSide(byte pos) {
		switch (pos) {
			case AmiWebChartGridPortlet.POS_R:
				return "RIGHT";
			case AmiWebChartGridPortlet.POS_B:
				return "BOTTOM";
			case AmiWebChartGridPortlet.POS_T:
				return "TOP";
			case AmiWebChartGridPortlet.POS_L:
				return "LEFT";
			default:
				return "UNKNOWN:" + pos;
		}
	}
	public static byte parseSide(String side) {
		if ("RIGHT".equals(side))
			return AmiWebChartGridPortlet.POS_R;
		if ("LEFT".equals(side))
			return AmiWebChartGridPortlet.POS_L;
		if ("TOP".equals(side))
			return AmiWebChartGridPortlet.POS_T;
		if ("BOTTOM".equals(side))
			return AmiWebChartGridPortlet.POS_B;
		return -1;
	}
	private String getOrientation() {
		return isVertical ? "VERT" : "HORZ";
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
	public void onClosed() {
		this.removeFromDomManager();
		super.onClosed();
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
		return VARTYPES;
	}

	public List<String> getSeries() {
		return series;
	}

	// should ideally be called getOrientation, but it exists for vertical/horizontal
	public char getAxisOrientation() {
		return orientation;
	}

	public AmiWebChartAxisFormatter getAxisFormatter() {
		return axisFormatter;
	}

	private static final ToIntFunction<String> stringLength = new ToIntFunction<String>() {
		@Override
		public int applyAsInt(String label) {
			return label.length();
		}
	};

	public int getRequiredSpace() {
		int padding = getTitle().length() > 0 ? getTitlePadding() : 0;

		String d = axisFormatter.formatExact(getMaxValue());
		int l = 0;
		int fontSize;
		if (d != null) {
			fontSize = getNumberFontSize();
			l = d.length();
			padding += getMajorUnitTickSize() + getNumberPadding();
		} else {
			fontSize = getLabelFontSize();
			padding += getLabelTickSize() + getLabelPadding();
			l = getSeries().stream().mapToInt(stringLength).max().orElse(0);
		}
		return OH.max(padding, l * fontSize / 2);
	}

	public double getGroupingSize() {
		return getLengthOfGroupAfterPaddingAfterZoom();
	}

	@Override
	public void onParentStyleChanged(AmiWebStyledPortletPeer peer) {
		this.setHtmlCssClass(peer.getParentStyle());
	}
	//	@Override
	//	public void onVarConstChanged(String var) {
	//		if (this.formulas.onVarConstChanged(var))
	//			this.onDataChanged();
	//	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
