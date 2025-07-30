package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.utils.CH;
import com.f1.utils.ColorGradient;
import com.f1.utils.ColorHelper;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiWebChartFormula_Color extends AmiWebChartFormula {
	public static final byte TYPE_COLOR_NONE = 10;
	public static final byte TYPE_COLOR_CONST = 11;
	public static final byte TYPE_COLOR_CUSTOM = 12;
	public static final byte TYPE_COLOR_CUSTOM_SERIES = 13;
	public static final byte TYPE_COLOR_CUSTOM_GRADIENT = 14;
	public static final byte TYPE_COLOR_DFLT_GRADIENT = 15;
	public static final byte TYPE_COLOR_DFLT_SERIES = 16;

	private byte colorType = TYPE_COLOR_NONE;
	private ColorGradient gradient;
	private List<String> series;
	private List<Color> seriesColors;

	public void setValue(byte type, List series, ColorGradient gradient, String value) {
		if (type == -1)
			throw new RuntimeException();
		this.colorType = type;
		switch (type) {
			case TYPE_COLOR_NONE:
				this.gradient = null;
				this.setSeries(null);
				setValue(null);
				break;
			case TYPE_COLOR_CONST:
				this.gradient = null;
				this.setSeries(null);
				setValue(value);
				break;
			case TYPE_COLOR_CUSTOM:
				this.gradient = null;
				this.setSeries(null);
				setValue(value);
				break;
			case TYPE_COLOR_CUSTOM_SERIES:
				this.gradient = null;
				this.setSeries(series);
				setValue(value);
				break;
			case TYPE_COLOR_CUSTOM_GRADIENT:
				this.gradient = gradient;
				this.setSeries(null);
				setValue(value);
				break;
			case TYPE_COLOR_DFLT_GRADIENT:
				this.gradient = null;
				this.setSeries(null);
				setValue(value);
				break;
			case TYPE_COLOR_DFLT_SERIES:
				this.gradient = null;
				this.setSeries(null);
				setValue(value);
				break;
		}

	}

	private void setSeries(List<String> series) {
		this.series = series;
		this.seriesColors = ColorHelper.parseColorsNoThrow(series);
	}

	public boolean testColorValue(byte type, List<String> customSeries, ColorGradient customGradient, String value, StringBuilder sb) {
		if (!super.testValue(value, sb))
			return false;
		DerivedCellCalculator calc = super.getCalc();
		switch (type) {
			case TYPE_COLOR_NONE:
				return true;
			case TYPE_COLOR_CONST:
				if (calc != null && calc.isConst())
					return true;
				sb.append("Error in ").append(getFullLabel()).append(" formula must be valid color");
				return false;
			case TYPE_COLOR_CUSTOM:
				if (calc == null) {
					sb.append("Error in ").append(getFullLabel()).append(" Formula required");
					return false;
				}
				if (!String.class.isAssignableFrom(calc.getReturnType())) {
					SH.getSimpleName(calc.getReturnType(), sb.append("Error in ").append(getFullLabel()).append(" return type must be a string, not "));
					return false;
				}
				if (super.getCalc().isConst()) {
					if (!ColorHelper.isColor((String) super.getCalc().get(null))) {
						sb.append("Error in ").append(getFullLabel()).append(" not a valid color");
						return false;
					}
				}
				return true;
			case TYPE_COLOR_CUSTOM_GRADIENT:
				if (customGradient == null || customGradient.getStopsCount() == 0) {
					sb.append("Error in ").append(getFullLabel()).append(" Gradient required");
					return false;
				}
				if (calc == null) {
					sb.append("Error in ").append(getFullLabel()).append(" Formula required");
					return false;
				}
				if (!Number.class.isAssignableFrom(calc.getReturnType())) {
					SH.getSimpleName(calc.getReturnType(), sb.append("Error in ").append(getFullLabel()).append(" return type must be a number, not "));
					return false;
				}
				return true;
			case TYPE_COLOR_CUSTOM_SERIES:
				if (CH.isEmpty(customSeries)) {
					sb.append("Error in ").append(getFullLabel()).append(" Series required");
					return false;
				}
				if (calc == null) {
					sb.append("Error in ").append(getFullLabel()).append(" Formula required");
					return false;
				}
				if (!Number.class.isAssignableFrom(calc.getReturnType())) {
					SH.getSimpleName(calc.getReturnType(), sb.append("Error in ").append(getFullLabel()).append(" return type must be a number, not "));
					return false;
				}
				return true;
			case TYPE_COLOR_DFLT_GRADIENT:
				if (calc == null) {
					sb.append("Error in ").append(getFullLabel()).append(" Formula required");
					return false;
				}
				if (!Number.class.isAssignableFrom(calc.getReturnType())) {
					SH.getSimpleName(calc.getReturnType(), sb.append("Error in ").append(getFullLabel()).append(" return type must be a number, not "));
					return false;
				}
				return true;
			case TYPE_COLOR_DFLT_SERIES:
				if (calc == null) {
					sb.append("Error in ").append(getFullLabel()).append(" Formula required");
					return false;
				}
				if (!Number.class.isAssignableFrom(calc.getReturnType())) {
					SH.getSimpleName(calc.getReturnType(), sb.append("Error in ").append(getFullLabel()).append(" return type must be a number, not "));
					return false;
				}
				return true;
		}
		return false;
	}

	@Override
	public void init(Object object) {
		if (object instanceof Map) {
			Map m = (Map) object;
			byte type = parseColorType(CH.getOr(String.class, m, "type", null));
			String value = CH.getOr(String.class, m, "value", null);
			List series = CH.getOrNoThrow(List.class, m, "series", null);
			String custGrad = CH.getOr(String.class, m, "gradient", null);
			ColorGradient g = SH.isInt(custGrad) ? null : new ColorGradient(custGrad);
			setValue(type, series, g, value);
		} else {
			setValue(SH.is(object) ? TYPE_COLOR_CUSTOM : TYPE_COLOR_NONE, null, null, (String) object);
		}
	}

	@Override
	public Object getConfiguration() {
		HashMap m = new HashMap();
		if (SH.is(this.value.getValue(false)))
			m.put("value", this.value.getValue(false));
		m.put("type", toColorType(this.colorType));
		switch (this.colorType) {
			case TYPE_COLOR_CUSTOM_GRADIENT:
			case TYPE_COLOR_DFLT_GRADIENT:
				if (this.gradient != null)
					m.put("gradient", this.gradient.toString());
				break;
			case TYPE_COLOR_CUSTOM_SERIES:
			case TYPE_COLOR_DFLT_SERIES:
				if (CH.isntEmpty(this.series))
					m.put("series", new ArrayList<String>(this.series));
				break;
		}
		return m;
	}

	@Override
	public boolean testValue(String text, StringBuilder sb) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean testValue(StringBuilder sb) {
		return testColorValue(this.colorType, this.series, this.gradient, this.getValue(), sb);
	}

	public AmiWebChartFormula_Color(AmiWebChartSeries series, String labelGroup, String name, String label) {
		super(series, labelGroup, name, label, TYPE_COLOR);
	}

	public ColorGradient getGradient() {
		return this.gradient;
	}

	public List<String> getSeries() {
		return this.series;
	}
	public List<Color> getSeriesColors() {
		return this.seriesColors;
	}

	public byte getColorType() {
		return colorType;
	}

	public void setColorType(byte colorType) {
		this.colorType = colorType;
	}
	private static byte parseColorType(String s) {
		if ("none".equals(s))
			return TYPE_COLOR_NONE;
		else if ("const".equals(s))
			return TYPE_COLOR_CONST;
		else if ("custom".equals(s))
			return TYPE_COLOR_CUSTOM;
		else if ("custSeries".equals(s))
			return TYPE_COLOR_CUSTOM_SERIES;
		else if ("custGradient".equals(s))
			return TYPE_COLOR_CUSTOM_GRADIENT;
		else if ("gradient".equals(s))
			return TYPE_COLOR_DFLT_GRADIENT;
		else if ("series".equals(s))
			return TYPE_COLOR_DFLT_SERIES;
		return TYPE_COLOR_CUSTOM;
	}
	private static String toColorType(byte c) {
		switch (c) {
			case TYPE_COLOR_NONE:
				return "none";
			case TYPE_COLOR_CONST:
				return "const";
			case TYPE_COLOR_CUSTOM:
				return "custom";
			case TYPE_COLOR_CUSTOM_SERIES:
				return "custSeries";
			case TYPE_COLOR_CUSTOM_GRADIENT:
				return "custGradient";
			case TYPE_COLOR_DFLT_GRADIENT:
				return "gradient";
			case TYPE_COLOR_DFLT_SERIES:
				return "series";
			default:
				return "custom";
		}
	}

	private boolean hasFormula() {
		return this.colorType != TYPE_COLOR_NONE;// && this.colorType != TYPE_COLOR_CONST;//|| super.hasFormulaOverride();
	}
	public String getFormula(boolean override) {
		if (!hasFormula())
			return null;
		return super.getFormula(override);
	}

	public void setFormula(String amiscript, boolean override) {
		if (!hasFormula())
			return;
		super.setFormula(amiscript, override);
	}
	public DerivedCellCalculator getFormulaCalc() {
		if (!hasFormula())
			return null;
		return super.getFormulaCalc();
	}

	public void recompileFormula() {
		if (!hasFormula())
			return;
		super.recompileFormula();
	}
	public Exception getFormulaError(boolean override) {
		if (!hasFormula())
			return null;
		return super.getFormulaError(override);

	}

	public boolean hasFormulaOverride() {
		if (!hasFormula())
			return false;
		return super.hasFormulaOverride();
	}
	public void clearFormulaOverride() {
		if (!hasFormula())
			return;
		super.clearFormulaOverride();
	}
	public Exception testFormula(String str) {
		if (!hasFormula())
			return null;
		return super.testFormula(str);
	}

}
