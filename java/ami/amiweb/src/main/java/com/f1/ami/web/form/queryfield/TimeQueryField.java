package com.f1.ami.web.form.queryfield;

import java.util.Map;
import java.util.TimeZone;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormTimeFieldFactory;
import com.f1.base.DateMillis;
import com.f1.suite.web.portal.impl.form.FormPortletTimeChooserField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.structs.Tuple2;

public class TimeQueryField extends QueryField<FormPortletTimeChooserField> {

	public TimeQueryField(AmiWebFormTimeFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletTimeChooserField("", form.getService().getVarsManager().getTimeZone(), false));
		// grab display format upon creation
		PortletStyleManager_Form formStyleManager = form.getEditableForm().getStyleManager();
		getField().setTimeDisplayFormat(formStyleManager.getTimeDisplayFormat());
	}

	@Override
	public boolean setValue(Object value) {
		if (value == null) {
			getField().setValue(null);
		}
		if (!(value instanceof Number))
			return false;

		Long l = Caster_Long.INSTANCE.cast(value);
		getField().setValue(new Tuple2<Long, Long>(l, null));
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
		Tuple2<Long, Long> value2 = getField().getValue();
		if (value2 == null)
			return null;
		final Long value = value2.getA();
		return value == null ? null : new DateMillis(value);
	}
	public Integer getMinute(String timeZoneId) {
		return getField().getMinuteStart(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getMinute() {
		return getField().getMinuteStart();
	}
	public Integer getHour(String timeZoneId) {
		return getField().getHourStart(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getHour() {
		return getField().getHourStart();
	}
	public Integer getHourOfDay(String timeZoneId) {
		return getField().getHourOfDayStart(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getHourOfDay() {
		return getField().getHourOfDayStart();
	}
	public boolean getIsPm(String timeZoneId) {
		return getField().getIsPmStart(TimeZone.getTimeZone(timeZoneId));
	}
	public boolean getIsPm() {
		return getField().getIsPmStart();
	}
	public Integer getSecond(String timeZoneId) {
		return getField().getSecondStart(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getSecond() {
		return getField().getSecondStart();
	}
	public Integer getMillis(String timeZoneId) {
		return getField().getMillisStart(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getMillis() {
		return getField().getMillisStart();
	}

	@Override
	public void init(Map<String, Object> initArgs) {
		PortletStyleManager_Form formStyleManager = this.getForm().getEditableForm().getStyleManager();
		getField().setTimeDisplayFormat(formStyleManager.getTimeDisplayFormat());
		super.init(initArgs);
	}
}
