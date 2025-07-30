package com.f1.ami.web.form.queryfield;

import java.util.Map;
import java.util.TimeZone;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormDateTimeFieldFactory;
import com.f1.base.DateMillis;
import com.f1.suite.web.portal.impl.form.FormPortletAbsctractCalendarField;
import com.f1.suite.web.portal.impl.form.FormPortletDateTimeChooserField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.Tuple2;

public class DateTimeQueryField extends QueryField<FormPortletDateTimeChooserField> {

	public DateTimeQueryField(AmiWebFormDateTimeFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletDateTimeChooserField("", form.getService().getVarsManager().getTimeZone(), false));
		// grab display format upon creation
		PortletStyleManager_Form formStyleManager = form.getEditableForm().getStyleManager();
		getField().setValueDisplayFormat(formStyleManager.getDateDisplayFormat(), formStyleManager.getTimeDisplayFormat());
	}

	@Override
	public boolean setValue(Object value) {
		Long val = Caster_Long.INSTANCE.cast(value, false, false);
		if (val == null && value != null)
			return false;
		getField().setValue(new Tuple2<Long, Long>(val, null));
		return true;
	}
	@Override
	public int getVarsCount() {
		return 1;
	}
	@Override
	public Class<?> getVarTypeAt(int i) {
		return DateMillis.class;
	}
	@Override
	public DateMillis getValue(int i) {
		final Long value = getField().getValue().getA();
		return value == null ? null : new DateMillis(value);
	}

	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		CH.putNoNull(sink, FormPortletAbsctractCalendarField.ENABLE_LAST_N_DAYS_L, getField().getEnableLastNDays().getA());
		CH.putNoNull(sink, FormPortletAbsctractCalendarField.DISABLE_FUTURE_DATES_L, getField().getDisableFutureDates().getA());
		return super.getJson(sink);
	}

	public Integer getMinute(String timeZoneId) {
		return getField().getMinute(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getMinute() {
		return getField().getMinute();
	}
	public Integer getHour(String timeZoneId) {
		return getField().getHour(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getHour() {
		return getField().getHour();
	}
	public Integer getHourOfDay(String timeZoneId) {
		return getField().getHourOfDay(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getHourOfDay() {
		return getField().getHourOfDay();
	}
	public boolean getIsPM(String timeZoneId) {
		return getField().getIsPM(TimeZone.getTimeZone(timeZoneId));
	}
	public boolean getIsPM() {
		return getField().getIsPM();
	}
	public Integer getSecond(String timeZoneId) {
		return getField().getSecond(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getSecond() {
		return getField().getSecond();
	}
	public Integer getMillis(String timeZoneId) {
		return getField().getMillis(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getMillis() {
		return getField().getMillis();
	}
	public Integer getDay(String timeZoneId) {
		return getField().getDay(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getDay() {
		return getField().getDay();
	}
	public Integer getMonth(String timeZoneId) {
		return getField().getMonth(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getMonth() {
		return getField().getMonth();
	}
	public Integer getYear(String timeZoneId) {
		return getField().getYear(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getYear() {
		return getField().getYear();
	}

	// for recovering user input
	@Override
	public void init(Map<String, Object> initArgs) {
		String ldl = CH.getOr(Caster_String.INSTANCE, initArgs, FormPortletAbsctractCalendarField.ENABLE_LAST_N_DAYS_L, null);
		Boolean fdl = CH.getOr(Caster_Boolean.INSTANCE, initArgs, FormPortletAbsctractCalendarField.DISABLE_FUTURE_DATES_L, false);
		getField().setEnableLastNDays(new Tuple2<String, String>(ldl, null));
		getField().setDisableFutureDates(new Tuple2<Boolean, Boolean>(fdl, false));
		PortletStyleManager_Form formStyleManager = this.getForm().getEditableForm().getStyleManager();
		getField().setValueDisplayFormat(formStyleManager.getDateDisplayFormat(), formStyleManager.getTimeDisplayFormat());
		super.init(initArgs);
	}
}
