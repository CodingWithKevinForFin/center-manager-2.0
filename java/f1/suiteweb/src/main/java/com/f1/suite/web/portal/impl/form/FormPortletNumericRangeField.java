package com.f1.suite.web.portal.impl.form;

import java.util.Map;

import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_String;

public class FormPortletNumericRangeField extends FormPortletField<Double> {

	public static final String JSNAME = "NumericRangeField";
	public static final String OPTION_SCROLL_GRIP_COLOR = "SCROLL_GRIP_COLOR";
	public static final String OPTION_SCROLL_TRACK_LEFT_COLOR = "SCROLL_TRACK_LEFT_COLOR";
	public static final String OPTION_SCROLL_TRACK_RIGHT_COLOR = "SCROLL_TRACK_RIGHT_COLOR";
	private double min = 0, max = 100;
	private int decimals = 0;
	private String scrollGripColor;
	private String rightScrollTrackColor;
	private String leftScrollTrackColor;
	private boolean textHidden = false;
	private boolean sliderHidden = false;
	private boolean nullable = false;
	private double step = 1;

	public FormPortletNumericRangeField(String title) {
		super(Double.class, title);
	}
	public FormPortletNumericRangeField(String title, double min, double max, int decimals) {
		super(Double.class, title);
		this.setRange(min, max);
		this.setDecimals(decimals);
	}

	public FormPortletNumericRangeField setDecimals(int decimals) {
		if (OH.eq(this.decimals, decimals))
			return this;
		this.step = decimals2Step(decimals);
		this.decimals = decimals;
		this.flagConfigChanged();
		return this;
	}

	static public double decimals2Step(int decimals) {
		if (decimals < 0)
			throw new IllegalArgumentException("decimals must not be negative:" + decimals);
		return 1d / MH.toTheTenthDouble(decimals);
	}

	static public int step2Decimals(double step) {
		if (step <= 0d)
			throw new IllegalArgumentException("Step must be positive:" + step);
		for (int r = 0; r < 20; r++) {
			double t = step * MH.toTheTenthDouble(r);
			if (MH.diff(t, (long) t) < 1E-20)
				return r;
		}
		throw new IllegalArgumentException("Could not get decimals for step:" + step);
	}

	public FormPortletNumericRangeField setStep(double step) {
		if (OH.eq(this.step, step))
			return this;
		this.step = step;
		this.decimals = step2Decimals(step);
		this.flagConfigChanged();
		return this;
	}
	public FormPortletNumericRangeField setRange(double min, double max) {
		if (OH.gt(min, max))
			throw new IllegalArgumentException("invalid min, max pair: " + min + " > " + max);
		this.min = min;
		this.max = max;
		this.flagConfigChanged();
		return this;
	}

	public FormPortletNumericRangeField setValue(double value) {
		super.setValue(value);
		return this;
	}

	@Override
	public String getjsClassName() {
		return JSNAME;
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {

		JsFunction js = new JsFunction(pendingJs);
		if (hasChanged(MASK_CONFIG)) {
			double min = getMin();
			double max = getMax();
			Double def = getDefaultValue() == null ? min : getDefaultValue().doubleValue();
			int width = 100;
			boolean textHidden = isTextHidden();
			boolean nullable = isNullable();
			js.reset(jsObjectName, "initSlider").addParam(getValue()).addParam(min).addParam(max).addParam(decimals).addParam(step).addParam(width).addParam(textHidden)
					.addParam(sliderHidden).addParam(nullable).addParam(def).end();
		}
		if (hasChanged(MASK_STYLE)) {
			Map<String, String> options = super.getForm().getStyleManager().getDefaultFormFieldOptions(getjsClassName());
			String style = getCssStyle();
			String scrollGripColor = this.scrollGripColor != null ? this.scrollGripColor : CH.getOr(Caster_String.INSTANCE, options, OPTION_SCROLL_GRIP_COLOR, null);
			String leftTrackColor = this.leftScrollTrackColor != null ? this.leftScrollTrackColor : CH.getOr(Caster_String.INSTANCE, options, OPTION_SCROLL_TRACK_LEFT_COLOR, null);
			String scrollTrackColor = this.rightScrollTrackColor != null ? this.rightScrollTrackColor
					: CH.getOr(Caster_String.INSTANCE, options, OPTION_SCROLL_TRACK_RIGHT_COLOR, null);
			Integer borderRadius = this.getBorderRadius();
			Integer borderWidth = this.getBorderWidthMaterialized();
			String fieldFontFamily = this.getFieldFontFamily();
			js.reset(jsObjectName, "applySliderStyles").addParamQuoted(style).addParamQuoted(getFontColor()).addParamQuoted(getBgColor())
					.addParamQuoted(this.getBorderColorMaterialized()).addParamQuoted(scrollGripColor).addParamQuoted(leftTrackColor).addParamQuoted(scrollTrackColor)
					.addParam(borderRadius).addParam(borderWidth).addParamQuoted(fieldFontFamily).end();
		}
		super.updateJs(pendingJs);

	}
	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		final Double value = CH.getOr(Caster_Double.INSTANCE, attributes, "value", null);
		if (OH.eq(value, this.getValue()))
			return false;
		setValueNoFire(value);
		return true;
	}

	public double getMin() {
		return min;
	}
	public double getMax() {
		return max;
	}

	public int getDecimals() {
		return decimals;
	}

	public Integer getIntValue() {
		return getValue() == null ? null : getValue().intValue();
	}
	public String getScrollGripColor() {
		return scrollGripColor;
	}
	public FormPortletNumericRangeField setScrollGripColor(String scrollGripColor) {
		if (scrollGripColor == null || scrollGripColor == this.scrollGripColor)
			return this;
		this.scrollGripColor = scrollGripColor;
		flagStyleChanged();
		return this;
	}
	public String getScrollTrackColor() {
		return rightScrollTrackColor;
	}
	public FormPortletNumericRangeField setScrollTrackColor(String scrollTrackColor) {
		if (scrollTrackColor == null || scrollTrackColor == this.rightScrollTrackColor)
			return this;
		this.rightScrollTrackColor = scrollTrackColor;
		flagStyleChanged();
		return this;
	}
	public String getLeftScrollTrackColor() {
		return leftScrollTrackColor;
	}
	public FormPortletNumericRangeField setLeftScrollTrackColor(String leftScrollTrackColor) {
		if (leftScrollTrackColor == null || leftScrollTrackColor == this.leftScrollTrackColor)
			return this;
		this.leftScrollTrackColor = leftScrollTrackColor;
		flagStyleChanged();
		return this;
	}
	public boolean isTextHidden() {
		return textHidden;
	}
	public FormPortletNumericRangeField setTextHidden(boolean textHidden) {
		if (this.textHidden == textHidden)
			return this;
		this.textHidden = textHidden;
		flagConfigChanged();
		return this;
	}

	public boolean isSliderHidden() {
		return sliderHidden;
	}
	public FormPortletNumericRangeField setSliderHidden(boolean sliderHidden) {
		if (this.sliderHidden == sliderHidden)
			return this;
		this.sliderHidden = sliderHidden;
		flagConfigChanged();
		return this;
	}
	public boolean isNullable() {
		return nullable;
	}
	public FormPortletNumericRangeField setNullable(boolean nullable) {
		if (this.nullable == nullable)
			return this;
		this.nullable = nullable;
		flagConfigChanged();
		return this;
	}

	@Override
	public FormPortletNumericRangeField setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public FormPortletNumericRangeField setCorrelationData(Object correlationData) {
		super.setCorrelationData(correlationData);
		return this;
	}

	@Override
	public FormPortletNumericRangeField setWidth(int width) {
		super.setWidth(width);
		return this;
	}

	@Override
	public FormPortletNumericRangeField setHeight(int height) {
		super.setHeight(height);
		return this;
	}
	public double getStep() {
		return this.step;
	}

}
