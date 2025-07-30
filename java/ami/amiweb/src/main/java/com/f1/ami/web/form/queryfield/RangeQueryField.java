package com.f1.ami.web.form.queryfield;

import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormRangeFieldFactory;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;

public class RangeQueryField extends QueryField<FormPortletNumericRangeField> {

	private double minVal;
	private double maxVal;
	private double step;

	public RangeQueryField(AmiWebFormRangeFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletNumericRangeField(""));
		this.minVal = 0;
		this.maxVal = 0;
		this.step = 1;
	}

	@Override
	public void init(Map<String, Object> initArgs) {
		super.init(initArgs);
		Double step = CH.getOr(Caster_Double.PRIMITIVE, initArgs, "s", null);
		if (step == null) {
			step = FormPortletNumericRangeField.decimals2Step(CH.getOr(Caster_Integer.PRIMITIVE, initArgs, "p", 0));
		}
		this.setStep(step);
		this.setRange(CH.getOr(Caster_Double.PRIMITIVE, initArgs, "min", this.minVal), CH.getOr(Caster_Double.PRIMITIVE, initArgs, "max", this.maxVal));
		getField().setValue(CH.getOr(Caster_Double.PRIMITIVE, initArgs, "min", 0d));
		getField().setDefaultValue(CH.getOr(Caster_Double.PRIMITIVE, initArgs, "min", 0d));
		// I don't think this is exposed to user
		getField().setTextHidden(CH.getOr(Caster_Boolean.PRIMITIVE, initArgs, "textHidden", false));
		getField().setSliderHidden(!CH.getOr(Caster_Boolean.PRIMITIVE, initArgs, "showSlider", true));
	}

	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		FormPortletNumericRangeField field = getField();
		CH.m(sink, "min", this.getMin(), "max", getMax(), "s", this.getStep(), "showSlider", !field.isSliderHidden());
		return super.getJson(sink);
	}
	public double getMin() {
		return minVal;
	}
	public double getMax() {
		return maxVal;
	}
	public void setRange(double min, double max) {
		this.minVal = min;
		this.maxVal = max;
		this.getField().setRange(min, max);
	}
	public void setStep(double step) {
		this.step = step;
		this.getField().setStep(this.step);
	}
	public double getStep() {
		return step;
	}
}