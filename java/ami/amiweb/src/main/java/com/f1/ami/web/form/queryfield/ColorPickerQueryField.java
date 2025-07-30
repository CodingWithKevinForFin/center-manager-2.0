package com.f1.ami.web.form.queryfield;

import java.util.Map;

import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormColorPickerFieldFactory;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Boolean;

public class ColorPickerQueryField extends QueryField<FormPortletColorField> {

	public ColorPickerQueryField(AmiWebFormColorPickerFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletColorField(""));
		getField().setValueNoFire("");
		getField().setDefaultValue("");
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
