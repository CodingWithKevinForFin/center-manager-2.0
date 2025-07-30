package com.f1.suite.web.portal.impl.form;

import java.util.Map;

import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.Tuple2;

public class FormPortletNumericRangeSubRangeField extends FormPortletField<Tuple2<Double, Double>> {
	public static final String OPTION_SCROLL_GRIP_COLOR = "SCROLL_GRIP_COLOR";
	public static final String OPTION_SCROLL_TRACK_OUTER_COLOR = "SCROLL_TRACK_OUTER_COLOR";
	public static final String OPTION_SCROLL_TRACK_INNER_COLOR = "SCROLL_TRACK_INNER_COLOR";
	private double min = 0, max = 100;
	private int decimals = 0;
	private String scrollGripColor;
	private String outerTrackColor;
	private String innerTrackColor;
	private boolean textHidden = false;
	private boolean sliderHidden = false;
	private boolean nullable = false;
	private double step = 1;

	public FormPortletNumericRangeSubRangeField(Class<Tuple2<Double, Double>> type, String title) {
		super((Class) Tuple2.class, title);
	}
	public FormPortletNumericRangeSubRangeField(String title, double min, double max, int decimals) {
		super((Class) Tuple2.class, title);
		this.setRange(min, max);
		this.setDecimals(decimals);
	}
	public FormPortletNumericRangeSubRangeField(String title) {
		super((Class) Tuple2.class, title);
	}
	public void setDecimals(int decimals) {
		if (OH.eq(this.decimals, decimals))
			return;
		this.step = FormPortletNumericRangeField.decimals2Step(decimals);
		this.decimals = decimals;
		this.flagConfigChanged();
	}
	public void setStep(double step) {
		if (OH.eq(this.step, step))
			return;
		this.step = step;
		this.decimals = FormPortletNumericRangeField.step2Decimals(step);
		this.flagConfigChanged();
	}

	public int getDecimals() {
		return this.decimals;
	}
	public void setRange(double min, double max) {
		this.setMin(min);
		this.setMax(max);
	}
	@Override
	public String getjsClassName() {
		return "NumericRangeSubRangeField";
	}

	public void updateJs(StringBuilder pendingJs) {

		if (hasChanged(MASK_CONFIG)) {
			Tuple2<Double, Double> def = this.getDefaultValue();
			Tuple2<Double, Double> val = this.getValue();
			Double valA;
			Double valB;
			double min = getMin();
			double max = getMax();
			if (val == null) {
				valA = null;
				valB = null;
			} else {
				valA = val.getA() == null ? min : val.getA().doubleValue();
				valB = val.getB() == null ? min : val.getB().doubleValue();
			}
			int width = 100;
			boolean textHidden = isTextHidden();
			boolean nullable = isNullable();
			Double defValA = def == null ? getMin() : def.getA().doubleValue();
			Double defValB = def == null ? getMax() : def.getB().doubleValue();
			JsFunction js = new JsFunction(pendingJs, jsObjectName, "initSliders").addParam(valA).addParam(valB).addParam(min).addParam(max).addParam(width).addParam(decimals)
					.addParam(step).addParam(textHidden).addParam(sliderHidden).addParam(nullable).addParam(defValA).addParam(defValB).end();
		}
		if (hasChanged(MASK_STYLE)) {
			Map<String, String> options = super.getForm().getStyleManager().getDefaultFormFieldOptions(getjsClassName());
			String scrollGripColor = this.scrollGripColor != null ? this.scrollGripColor : CH.getOr(Caster_String.INSTANCE, options, OPTION_SCROLL_GRIP_COLOR, null);
			String outerTrackColor = this.outerTrackColor != null ? this.outerTrackColor : CH.getOr(Caster_String.INSTANCE, options, OPTION_SCROLL_TRACK_OUTER_COLOR, null);
			String innerTrackColor = this.innerTrackColor != null ? this.innerTrackColor : CH.getOr(Caster_String.INSTANCE, options, OPTION_SCROLL_TRACK_INNER_COLOR, null);
			String style = getCssStyle();
			int borderRadius = this.getBorderRadius() == null ? 0 : this.getBorderRadius().intValue();
			int borderWidth = this.getBorderWidthMaterialized() == null ? 0 : this.getBorderWidthMaterialized().intValue();
			String fieldFontFamily = this.getFieldFontFamily();
			JsFunction js = new JsFunction(pendingJs, jsObjectName, "applySliderStyles").addParamQuoted(style).addParamQuoted(getFontColor()).addParamQuoted(getBgColor())
					.addParamQuoted(this.getBorderColorMaterialized()).addParamQuoted(scrollGripColor).addParamQuoted(outerTrackColor).addParamQuoted(innerTrackColor)
					.addParam(borderRadius).addParam(borderWidth).addParamQuoted(fieldFontFamily).end();
		}
		super.updateJs(pendingJs);
	}
	public boolean isTextHidden() {
		return textHidden;
	}

	public void setTextHidden(boolean textHidden) {
		this.textHidden = textHidden;
	}

	public String getScrollGripColor() {
		return scrollGripColor;
	}

	public FormPortletNumericRangeSubRangeField setScrollGripColor(String scrollGripColor) {
		if (scrollGripColor == null || scrollGripColor == this.scrollGripColor)
			return this;
		this.scrollGripColor = scrollGripColor;
		flagConfigChanged();
		return this;
	}

	public String getOuterTrackColor() {
		return outerTrackColor;
	}

	public FormPortletNumericRangeSubRangeField setOuterTrackColor(String outerTrackColor) {
		if (outerTrackColor == null || outerTrackColor == this.outerTrackColor)
			return this;

		this.outerTrackColor = outerTrackColor;
		flagConfigChanged();
		return this;
	}

	public String getInnerTrackColor() {
		return innerTrackColor;
	}

	public FormPortletNumericRangeSubRangeField setInnerTrackColor(String innerTrackColor) {
		if (innerTrackColor == null || innerTrackColor == this.innerTrackColor)
			return this;
		this.innerTrackColor = innerTrackColor;
		flagConfigChanged();
		return this;
	}

	public void setValue(Double val1, Double val2) {
		Tuple2 t = new Tuple2<Double, Double>(val1, val2);
		super.setValue(t);
	}
	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		final Double value = CH.getOr(Caster_Double.INSTANCE, attributes, "value", null);
		final Double value2 = CH.getOr(Caster_Double.INSTANCE, attributes, "value2", null);
		Tuple2<Double, Double> currentValue = this.getValue();
		if (currentValue != null)
			if ((OH.eq(value, currentValue.getA()) && OH.eq(value2, currentValue.getB())))
				return false;
		setValueNoFire(new Tuple2<Double, Double>(value, value2));
		return true;
	}
	@Override
	public String getJsValue() {
		if (getValue() == null)
			return "null,+null";
		return getValue().getA() + "," + getValue().getB();
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		if (MH.eq(this.min, min))
			return;
		this.min = min;
		this.flagConfigChanged();
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		if (MH.eq(this.max, max))
			return;
		this.max = max;
		this.flagConfigChanged();
	}
	public boolean isSliderHidden() {
		return sliderHidden;
	}
	public void setSliderHidden(boolean sliderHidden) {
		this.sliderHidden = sliderHidden;
		flagConfigChanged();
	}
	public boolean isNullable() {
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	@Override
	public FormPortletNumericRangeSubRangeField setName(String name) {
		super.setName(name);
		return this;
	}
	public double getStep() {
		return this.step;
	}
}
