package com.f1.suite.web.portal.impl.form;

import java.util.Map;
import java.util.TimeZone;

import com.f1.base.Day;
import com.f1.utils.BasicDay;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.Tuple2;

public class FormPortletDayChooserField extends FormPortletAbsctractCalendarField<Tuple2<Day, Day>> {

	private int maxLength = 255;
	private TimeZone tz;

	public FormPortletDayChooserField(String title, TimeZone tz, boolean isRange) {
		super((Class) Tuple2.class, title, isRange);
		setValueNoFire(new Tuple2<Day, Day>());
		this.tz = tz;
	}
	@Override
	public String getjsClassName() {
		return "DayChooserField";
	}

	public TimeZone getTimeZone() {
		return this.tz;
	}
	public void setTimeZone(TimeZone tz) {
		this.tz = tz;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public FormPortletDayChooserField setMaxChars(int maxLength) {
		this.maxLength = maxLength;
		flagConfigChanged();
		return this;
	}

	@Override
	public FormPortletDayChooserField setValue(Tuple2<Day, Day> startAndEnd) {
		super.setValue(startAndEnd);
		return this;
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		final String start = CH.getOrThrow(Caster_String.INSTANCE, attributes, "ymd");
		final String end = CH.getOr(Caster_String.INSTANCE, attributes, "ymd2", null);
		Tuple2<Day, Day> value = new Tuple2<Day, Day>(parseDate(start), parseDate(end));
		if (OH.eq(getValue(), value))
			return false;
		setValueNoFire(value);
		return true;
	}

	private Day parseDate(String yyyymmdd) {
		if (SH.isnt(yyyymmdd))
			return null;
		yyyymmdd = yyyymmdd.trim();
		if (yyyymmdd.length() != 8 || !SH.areBetween(yyyymmdd, '0', '9'))
			return null;
		return new BasicDay(this.tz, (short) SH.parseInt(yyyymmdd.substring(0, 4)), (byte) SH.parseInt(SH.trimStart('0', yyyymmdd.substring(4, 6))),
				(byte) SH.parseInt(SH.trimStart('0', yyyymmdd.substring(6, 8))));
	}
	public String getJsValue() {
		Tuple2<Day, Day> val = getValue();
		if (val == null)
			return "null,null";
		return formatDate(val.getA()) + "," + formatDate(val.getB());
	}

	private String formatDate(Day a) {
		if (a == null)
			return "null";
		final StringBuilder sb = new StringBuilder(8);
		SH.rightAlign('0', SH.toString(a.getYear()), 4, false, sb);
		SH.rightAlign('0', SH.toString(a.getMonth()), 2, false, sb);
		SH.rightAlign('0', SH.toString(a.getDay()), 2, false, sb);
		return sb.toString();
	}

	@Override
	public FormPortletDayChooserField setName(String name) {
		super.setName(name);
		return this;
	}

}
