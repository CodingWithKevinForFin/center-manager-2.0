package com.f1.ami.web.form.queryfield;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormSubRangeFieldFactory;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeSubRangeField;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.structs.Tuple2;

public class SubRangeQueryField extends QueryField<FormPortletNumericRangeSubRangeField> {

	private double minVal;
	private double maxVal;
	private double step;

	public SubRangeQueryField(AmiWebFormSubRangeFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletNumericRangeSubRangeField(""));
		this.step = 0;
		this.minVal = 0;
		this.maxVal = 0;
	}
	@Override
	public void init(Map<String, Object> initArgs) {
		super.init(initArgs);
		getField().setTextHidden(CH.getOr(Caster_Boolean.PRIMITIVE, initArgs, "textHidden", false));
		Double step = CH.getOr(Caster_Double.PRIMITIVE, initArgs, "s", null);
		if (step == null) {
			step = FormPortletNumericRangeField.decimals2Step(CH.getOr(Caster_Integer.PRIMITIVE, initArgs, "p", 0));
		}
		if (step <= 0.0)
			step = 1.0;
		this.setStep(step);
		this.setRange(CH.getOr(Caster_Double.PRIMITIVE, initArgs, "min", 0d), CH.getOr(Caster_Double.PRIMITIVE, initArgs, "max", 0d));
		getField().setValue(new Tuple2<Double, Double>(CH.getOr(Caster_Double.PRIMITIVE, initArgs, "min", 0d), CH.getOr(Caster_Double.PRIMITIVE, initArgs, "max", 0d)));
	}
	public double getMax() {
		return maxVal;
	}
	public double getMin() {
		return minVal;
	}
	public void setRange(double min, double max) {
		this.minVal = min;
		this.maxVal = max;
		this.getField().setRange(min, max);
	}
	@Override
	public int getVarsCount() {
		return 2;
	}
	@Override
	public Class<?> getVarTypeAt(int i) {
		return Double.class;
	}
	@Override
	public Double getValue(int i) {
		if (getField().getValue() != null) {
			if (i == 0)
				return getField().getValue().getA();
			return getField().getValue().getB();
		}
		return null;
	}
	@Override
	public String getSuffixNameAt(int i) {
		if (i == 0)
			return AmiWebQueryFormPortlet.SUFFIX_MIN;
		else
			return AmiWebQueryFormPortlet.SUFFIX_MAX;
	}

	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		FormPortletNumericRangeSubRangeField field = this.getField();
		//		sink.put("outerTrack", getSecondaryColor());
		//		sink.put("innerTrack", getPrimaryColor());
		sink.put("min", this.getMin());
		sink.put("max", this.getMax());
		sink.put("s", this.getStep());
		//		sink.put("fontColor", getFontColor());
		sink.put("textHidden", field.isTextHidden());
		return super.getJson(sink);
	}

	@Override
	public Object getValue() {
		Tuple2<Double, Double> t = getField().getValue();
		ArrayList<Double> r = new ArrayList<Double>(2);
		if (t == null) {
			return null;
		} else {

			r.add(t.getA());
			r.add(t.getB());
		}
		return r;

	};
	@Override
	public Class getValueType() {
		return List.class;
	}

	@Override
	public boolean setValue(Object value) {
		if (value == null) {
			getField().setValue(null);
			return true;
		}
		if (!(value instanceof List))
			return false;

		List list = (List) value;
		if (list.size() != 2)
			throw new RuntimeException("Subrange List must contain exactly 2 entries, not: " + list.size());
		Double a = Caster_Double.INSTANCE.cast(list.get(0), false, false);
		Double b = Caster_Double.INSTANCE.cast(list.get(1), false, false);
		Tuple2<Double, Double> tuple = new Tuple2<Double, Double>(a, b);
		getField().setValue(tuple);
		return true;
	}

	public void setStep(double step) {
		this.step = step;
		this.getField().setStep(this.step);
	}
	public double getStep() {
		return step;
	}

	@Override
	public Object getDefaultValue() {
		return CH.l(this.getMin(), this.getMax());
	}

}
