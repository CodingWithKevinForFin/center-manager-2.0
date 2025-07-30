package com.f1.ami.web.form.queryfield;

import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormDateFieldFactory;
import com.f1.base.DateMillis;
import com.f1.base.Day;
import com.f1.suite.web.portal.impl.form.FormPortletAbsctractCalendarField;
import com.f1.suite.web.portal.impl.form.FormPortletDayChooserField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.BasicDay;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.Tuple2;

public class DateQueryField extends QueryField<FormPortletDayChooserField> {

	//	private static final String HEADER_COLOR = "headerColor";

	public DateQueryField(AmiWebFormDateFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletDayChooserField("", form.getService().getVarsManager().getTimeZone(), false));
		// grab display format upon creation
		PortletStyleManager_Form formStyleManager = form.getEditableForm().getStyleManager();
		getField().setValueDisplayFormat(formStyleManager.getDateDisplayFormat(), formStyleManager.getTimeDisplayFormat());
	}
	@Override
	public Class<?> getVarTypeAt(int i) {
		return DateMillis.class;
	}
	@Override
	public boolean setValue(Object value) {
		if (value == null) {
			getField().setValue(null);
			return true;
		}
		if (!(value instanceof Number))
			return false;
		TimeZone tz = getField().getTimeZone();
		BasicDay bd = new BasicDay(tz, new Date(((Number) value).longValue()));
		getField().setValue(new Tuple2<Day, Day>(bd, null));
		return true;
	}
	@Override
	public DateMillis getValue(int i) {
		return getField().getValue() == null ? null : getField().getValue().getA() == null ? null : new DateMillis(getField().getValue().getA().getStartMillis());
	}

	// save user input
	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		//		CH.putNoNull(sink, HEADER_COLOR, getPrimaryColor());
		CH.putNoNull(sink, FormPortletAbsctractCalendarField.ENABLE_LAST_N_DAYS_L, getField().getEnableLastNDays().getA());
		CH.putNoNull(sink, FormPortletAbsctractCalendarField.ENABLE_LAST_N_DAYS_R, getField().getEnableLastNDays().getB());
		CH.putNoNull(sink, FormPortletAbsctractCalendarField.DISABLE_FUTURE_DATES_L, getField().getDisableFutureDates().getA());
		CH.putNoNull(sink, FormPortletAbsctractCalendarField.DISABLE_FUTURE_DATES_R, getField().getDisableFutureDates().getB());
		return super.getJson(sink);
	}
	public Day getDayValue() {
		return getField().getValue().getA();
	}
	public Byte getDay() {
		Day d = getDayValue();
		return d == null ? null : d.getDay();
	}
	public Byte getMonth() {
		Day d = getDayValue();
		return d == null ? null : d.getMonth();
	}
	public Short getYear() {
		Day d = getDayValue();
		return d == null ? null : d.getYear();
	}

	// for recovering user input
	@Override
	public void init(Map<String, Object> initArgs) {
		String ldl = CH.getOr(Caster_String.INSTANCE, initArgs, FormPortletAbsctractCalendarField.ENABLE_LAST_N_DAYS_L, null);
		String ldr = CH.getOr(Caster_String.INSTANCE, initArgs, FormPortletAbsctractCalendarField.ENABLE_LAST_N_DAYS_R, null);
		Boolean fdl = CH.getOr(Caster_Boolean.INSTANCE, initArgs, FormPortletAbsctractCalendarField.DISABLE_FUTURE_DATES_L, false);
		Boolean fdr = CH.getOr(Caster_Boolean.INSTANCE, initArgs, FormPortletAbsctractCalendarField.DISABLE_FUTURE_DATES_R, false);
		getField().setEnableLastNDays(new Tuple2<String, String>(ldl, ldr));
		getField().setDisableFutureDates(new Tuple2<Boolean, Boolean>(fdl, fdr));
		PortletStyleManager_Form formStyleManager = this.getForm().getEditableForm().getStyleManager();
		getField().setValueDisplayFormat(formStyleManager.getDateDisplayFormat(), formStyleManager.getTimeDisplayFormat());
		super.init(initArgs);
	}
}