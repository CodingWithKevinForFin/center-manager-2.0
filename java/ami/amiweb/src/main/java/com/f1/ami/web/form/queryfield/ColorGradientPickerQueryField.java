package com.f1.ami.web.form.queryfield;

import java.util.Map;

import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormColorGradientPickerFieldFactory;
import com.f1.suite.web.portal.impl.form.FormPortletColorGradientField;
import com.f1.utils.CH;
import com.f1.utils.ColorGradient;
import com.f1.utils.casters.Caster_Boolean;

public class ColorGradientPickerQueryField extends QueryField<FormPortletColorGradientField> {

	public ColorGradientPickerQueryField(AmiWebFormColorGradientPickerFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletColorGradientField(""));
	}

	@Override
	public boolean setValue(Object value) {
		if (value instanceof String) {
			try {
				value = new ColorGradient(0d, 1d, (String) value);
			} catch (Exception e) {
				value = null;
			}
		}
		return super.setValue(value);
	}

	private AmiWebOverrideValue<Boolean> alaphaEnabled = new AmiWebOverrideValue<Boolean>(false);

	public void setAlaphaEnabled(boolean value, boolean override) {
		alaphaEnabled.setValue(value, override);
		getField().setAlphaEnabled(alaphaEnabled.getValue(true));
	}
	public boolean getAlaphaEnabled(boolean override) {
		return alaphaEnabled.getValue(override);
	}

	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		CH.putExcept(sink, "alpha", getAlaphaEnabled(false), Boolean.FALSE);
		return super.getJson(sink);
	}

	@Override
	public void init(Map<String, Object> initArgs) {
		setAlaphaEnabled(CH.getOr(Caster_Boolean.INSTANCE, initArgs, "alpha", Boolean.FALSE), false);
		super.init(initArgs);
	}

}
