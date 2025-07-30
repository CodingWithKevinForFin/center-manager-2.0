package com.f1.ami.web.form.queryfield;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormDateRangeFieldFactory;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Day;
import com.f1.suite.web.portal.impl.form.FormPortletAbsctractCalendarField;
import com.f1.suite.web.portal.impl.form.FormPortletDayChooserField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.BasicDay;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.Tuple2;

public class DateRangeQueryField extends QueryField<FormPortletDayChooserField> {

	public DateRangeQueryField(AmiWebFormDateRangeFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletDayChooserField("", form.getService().getVarsManager().getTimeZone(), true));
		PortletStyleManager_Form formStyleManager = form.getEditableForm().getStyleManager();
		getField().setValueDisplayFormat(formStyleManager.getDateDisplayFormat(), formStyleManager.getTimeDisplayFormat());
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
	public boolean setValue(Object value) {
		TimeZone tz = getField().getTimeZone();
		if (value instanceof List) {
			List list = (List) value;
			if (list.size() == 2 && list.get(0) instanceof Long && list.get(1) instanceof Long) {
				BasicDay bds = new BasicDay(tz, new Date((Long) list.get(0)));
				BasicDay bde = new BasicDay(tz, new Date((Long) list.get(1)));
				if ((long) (list.get(0)) < (long) (list.get(1))) {
					getField().setValue(new Tuple2<Day, Day>(bds, bde));
					return true;
				}
			} else if (list.size() == 2 && list.get(0) instanceof DateMillis && list.get(1) instanceof DateMillis) {
				DateMillis obj1 = (DateMillis) list.get(0);
				DateMillis obj2 = (DateMillis) list.get(1);

				BasicDay bds = new BasicDay(tz, new Date(obj1.getDate()));
				BasicDay bde = new BasicDay(tz, new Date(obj2.getDate()));

				if (obj1.getDate() < obj2.getDate()) {
					getField().setValue(new Tuple2<Day, Day>(bds, bde));
					return true;
				}
			} else if (list.size() == 2 && list.get(0) instanceof DateNanos && list.get(1) instanceof DateNanos) {
				DateNanos obj1 = (DateNanos) list.get(0);
				DateNanos obj2 = (DateNanos) list.get(1);

				BasicDay bds = new BasicDay(tz, new Date(obj1.getTimeNanos()));
				BasicDay bde = new BasicDay(tz, new Date(obj2.getTimeNanos()));

				if (obj1.getTimeNanos() < obj2.getTimeNanos()) {
					getField().setValue(new Tuple2<Day, Day>(bds, bde));
					return true;
				}
			}
		} else if (value == null) {
			getField().setValue(null);
			return true;
		}
		return false;
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
		final Day value;
		Tuple2<Day, Day> value2 = getField().getValue();
		if (value2 == null)
			return null;
		if (i == 1) {
			value = value2.getB();
		} else
			value = value2.getA();
		return value == null ? null : new DateMillis(value.getStartMillis());
	}
	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		CH.putNoNull(sink, FormPortletAbsctractCalendarField.ENABLE_LAST_N_DAYS_L, getField().getEnableLastNDays().getA());
		CH.putNoNull(sink, FormPortletAbsctractCalendarField.ENABLE_LAST_N_DAYS_R, getField().getEnableLastNDays().getB());
		CH.putNoNull(sink, FormPortletAbsctractCalendarField.DISABLE_FUTURE_DATES_L, getField().getDisableFutureDates().getA());
		CH.putNoNull(sink, FormPortletAbsctractCalendarField.DISABLE_FUTURE_DATES_R, getField().getDisableFutureDates().getB());
		return super.getJson(sink);
	}

	public Day getStartDayValue() {
		return getField().getValue().getA();
	}
	public Day getEndDayValue() {
		return getField().getValue().getB();
	}
	public Byte getStartDay() {
		Day d = getStartDayValue();
		return d == null ? null : d.getDay();
	}
	public Byte getEndDay() {
		Day d = getEndDayValue();
		return d == null ? null : d.getDay();
	}
	public Byte getStartMonth() {
		Day d = getStartDayValue();
		return d == null ? null : d.getMonth();
	}
	public Byte getEndMonth() {
		Day d = getEndDayValue();
		return d == null ? null : d.getMonth();
	}
	public Short getStartYear() {
		Day d = getStartDayValue();
		return d == null ? null : d.getYear();
	}
	public Short getEndYear() {
		Day d = getEndDayValue();
		return d == null ? null : d.getYear();
	}

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
