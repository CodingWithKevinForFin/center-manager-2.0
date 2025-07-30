package com.f1.ami.web.form.queryfield;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormTimeRangeFieldFactory;
import com.f1.base.DateMillis;
import com.f1.suite.web.portal.impl.form.FormPortletTimeChooserField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.structs.Tuple2;

public class TimeRangeQueryField extends QueryField<FormPortletTimeChooserField> {

	public TimeRangeQueryField(AmiWebFormTimeRangeFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletTimeChooserField("", form.getService().getVarsManager().getTimeZone(), true));
		// grab display format upon creation
		PortletStyleManager_Form formStyleManager = form.getEditableForm().getStyleManager();
		getField().setTimeDisplayFormat(formStyleManager.getTimeDisplayFormat());
	}

	@Override
	public boolean setValue(Object value) {
		if (value instanceof List) {
			List list = (List) value;
			if (list.size() == 2 && list.get(0) instanceof Number && list.get(1) instanceof Number) {
				Long l1 = Caster_Long.INSTANCE.cast(list.get(0));
				Long l2 = Caster_Long.INSTANCE.cast(list.get(1));
				getField().setValue(new Tuple2<Long, Long>(l1, l2));
				return true;
			}
		} else if (value == null) {
			getField().setValue(null);
			return true;
		}

		return false;
	}
	@Override
	public int getVarsCount() {
		return 2;
	}

	@Override
	public String getSuffixNameAt(int i) {
		if (i == 0)
			return AmiWebQueryFormPortlet.SUFFIX_START;
		else
			return AmiWebQueryFormPortlet.SUFFIX_END;
	}
	@Override
	public Class<?> getVarTypeAt(int i) {
		return DateMillis.class;
	}
	@Override
	public Object getValue() {
		ArrayList<DateMillis> list = new ArrayList<DateMillis>();
		list.add(this.getValue(0));
		list.add(this.getValue(1));
		return list;
	}
	@Override
	public Class getValueType() {
		return List.class;
	}
	@Override
	public DateMillis getValue(int i) {
		final Long value;
		Tuple2<Long, Long> t = getField().getValue();
		if (t == null)
			return null;
		if (i == 1) {
			value = t.getB();
		} else
			value = t.getA();
		return value == null ? null : new DateMillis(value);
	}
	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		FormPortletTimeChooserField field = getField();
		return super.getJson(sink);
	}

	public Integer getMinuteStart() {
		return getField().getMinuteStart();
	}
	public Integer getMinuteStart(String timeZoneId) {
		return getField().getMinuteStart(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getHourStart() {
		return getField().getHourStart();
	}
	public Integer getHourStart(String timeZoneId) {
		return getField().getHourStart(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getHourOfDayStart() {
		return getField().getHourOfDayStart();
	}
	public Integer getHourOfDayStart(String timeZoneId) {
		return getField().getHourOfDayStart(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getSecondStart() {
		return getField().getSecondStart();
	}
	public Integer getSecondStart(String timeZoneId) {
		return getField().getSecondStart(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getMillisStart() {
		return getField().getMillisStart();
	}
	public Integer getMillisStart(String timeZoneId) {
		return getField().getMillisStart(TimeZone.getTimeZone(timeZoneId));
	}

	public Integer getMinuteEnd() {
		return getField().getMinuteEnd();
	}
	public Integer getMinuteEnd(String timeZoneId) {
		return getField().getMinuteEnd(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getHourEnd() {
		return getField().getHourEnd();
	}
	public Integer getHourEnd(String timeZoneId) {
		return getField().getHourEnd(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getHourOfDayEnd() {
		return getField().getHourOfDayEnd();
	}
	public Integer getHourOfDayEnd(String timeZoneId) {
		return getField().getHourOfDayEnd(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getSecondEnd() {
		return getField().getSecondEnd();
	}
	public Integer getSecondEnd(String timeZoneId) {
		return getField().getSecondEnd(TimeZone.getTimeZone(timeZoneId));
	}
	public Integer getMillisEnd() {
		return getField().getMillisEnd();
	}
	public Integer getMillisEnd(String timeZoneId) {
		return getField().getMillisEnd(TimeZone.getTimeZone(timeZoneId));
	}
	public boolean getIsPmStart(String timeZoneId) {
		return getField().getIsPmStart(TimeZone.getTimeZone(timeZoneId));
	}
	public boolean getIsPmStart() {
		return getField().getIsPmStart();
	}
	public boolean getIsPmEnd(String timeZoneId) {
		return getField().getIsPmEnd(TimeZone.getTimeZone(timeZoneId));
	}
	public boolean getIsPmEnd() {
		return getField().getIsPmEnd();
	}

	@Override
	public void init(Map<String, Object> initArgs) {
		PortletStyleManager_Form formStyleManager = this.getForm().getEditableForm().getStyleManager();
		getField().setTimeDisplayFormat(formStyleManager.getTimeDisplayFormat());
		super.init(initArgs);
	}

}
