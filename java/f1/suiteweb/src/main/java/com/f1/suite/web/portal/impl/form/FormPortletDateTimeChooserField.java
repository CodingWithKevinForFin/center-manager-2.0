package com.f1.suite.web.portal.impl.form;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.formatter.BasicDateFormatter;
import com.f1.utils.structs.Tuple2;

public class FormPortletDateTimeChooserField extends FormPortletAbsctractCalendarField<Tuple2<Long, Long>> {

	private static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("UTC");
	private int maxLength = 255;
	private TimeZone tz;

	private final GregorianCalendar calendar = new GregorianCalendar();
	private Integer js_startYear;
	private Integer js_startMonth;
	private Integer js_startDay;
	private Integer js_startHours;
	private Integer js_startMinutes;
	private Integer js_startSeconds;
	private Integer js_startMillis;

	public FormPortletDateTimeChooserField(String title, TimeZone tz, boolean isRange) {
		super((Class) Tuple2.class, title, isRange);
		setValueNoFire(new Tuple2<Long, Long>());
		this.tz = tz;
		calendar.setTimeZone(tz);
		calendar.set(GregorianCalendar.ERA, GregorianCalendar.AD);
	}
	@Override
	public String getjsClassName() {
		return "DateTimeChooserField";
	}
	public void setTimeZone(TimeZone tz) {
		this.tz = tz;
	}
	public TimeZone getTimeZone() {
		return this.tz;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public FormPortletDateTimeChooserField setMaxChars(int maxLength) {
		this.maxLength = maxLength;
		flagConfigChanged();
		return this;
	}

	@Override
	public FormPortletDateTimeChooserField setValue(Tuple2<Long, Long> startAndEnd) {
		super.setValue(startAndEnd);
		// use calendar to convert millis to datetime, then store in vars
		// this handles amiscript setValue, as it stores the ms as first arg
		this.calendar.setTimeInMillis(startAndEnd.getA());
		this.js_startYear = this.calendar.get(Calendar.YEAR);
		this.js_startMonth = this.calendar.get(Calendar.MONTH) + 1;
		this.js_startDay = this.calendar.get(Calendar.DAY_OF_MONTH);
		this.js_startHours = this.calendar.get(Calendar.HOUR_OF_DAY);
		this.js_startMinutes = this.calendar.get(Calendar.MINUTE);
		this.js_startSeconds = this.calendar.get(Calendar.SECOND);
		this.js_startMillis = this.calendar.get(Calendar.MILLISECOND);
		return this;
	}

	private String getDate() {
		// convert date in long to string
		if (js_startYear != null && js_startMonth != null && js_startDay != null) {
			final StringBuilder sb = new StringBuilder();
			SH.rightAlign('0', SH.toString(js_startYear), 4, false, sb);
			SH.rightAlign('0', SH.toString(js_startMonth), 2, false, sb);
			SH.rightAlign('0', SH.toString(js_startDay), 2, false, sb);
			return sb.toString();
		}
		return null;
	}

	private String getTime() {
		// convert time in long to string
		if (js_startHours != null && js_startMinutes != null && js_startSeconds != null && js_startMillis != null) {
			final StringBuilder sb = new StringBuilder();
			sb.append(js_startHours * 3600000 + js_startMinutes * 60000 + js_startSeconds * 1000 + js_startMillis);
			return sb.toString();
		}
		return null;
	}
	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		final Boolean clearAll = CH.getOr(Caster_Boolean.INSTANCE, attributes, "clearAll", false);
		// clear if only one field is set
		if (clearAll) {
			this.js_startYear = null; // lazy set
			this.js_startHours = null; // lazy set
		} else {
			final Integer startYear = CH.getOr(Caster_Integer.INSTANCE, attributes, "s_yy", null);
			final Integer startMonth = CH.getOr(Caster_Integer.INSTANCE, attributes, "s_MM", null);
			final Integer startDay = CH.getOr(Caster_Integer.INSTANCE, attributes, "s_dd", null);
			final Integer startHours = CH.getOr(Caster_Integer.INSTANCE, attributes, "s_HH", null);
			final Integer startMinutes = CH.getOr(Caster_Integer.INSTANCE, attributes, "s_mm", null);
			final Integer startSeconds = CH.getOr(Caster_Integer.INSTANCE, attributes, "s_ss", null);
			final Integer startMillis = CH.getOr(Caster_Integer.INSTANCE, attributes, "s_SSS", null);
			this.js_startYear = startYear;
			this.js_startMonth = startMonth;
			this.js_startDay = startDay;
			this.js_startHours = startHours;
			this.js_startMinutes = startMinutes;
			this.js_startSeconds = startSeconds;
			this.js_startMillis = startMillis;
		}

		// long value that represents both date and time. Parsed in getJsValue()
		Tuple2<Long, Long> value = new Tuple2<Long, Long>(this.getStartLongValue(), null);

		setValueNoFire(value);
		return true;
	}
	public String getJsValue() {
		StringBuilder sb = new StringBuilder();
		String date = this.getDate();
		String time = this.getTime();
		sb.append(SH.is(date) ? date : "null");
		sb.append(",");
		sb.append(SH.is(time) ? time : "null");
		return sb.toString();
	}

	@Override
	public FormPortletDateTimeChooserField setName(String name) {
		super.setName(name);
		return this;
	}

	private Long getStartLongValue() {
		if (js_startYear == null || js_startMonth == null || js_startDay == null || js_startHours == null || js_startMinutes == null || js_startSeconds == null
				|| js_startMillis == null)
			return null;
		this.calendar.setTimeZone(this.tz);
		this.calendar.set(GregorianCalendar.ERA, GregorianCalendar.AD);
		this.calendar.set(GregorianCalendar.YEAR, js_startYear);
		this.calendar.set(GregorianCalendar.MONTH, js_startMonth - 1);
		this.calendar.set(GregorianCalendar.DAY_OF_MONTH, js_startDay);
		this.calendar.set(GregorianCalendar.HOUR_OF_DAY, js_startHours);
		this.calendar.set(GregorianCalendar.MINUTE, js_startMinutes);
		this.calendar.set(GregorianCalendar.SECOND, js_startSeconds);
		this.calendar.set(GregorianCalendar.MILLISECOND, js_startMillis);

		return calendar.getTimeInMillis();
	}

	public Integer getMinute() {
		return getMinute(TIME_ZONE_UTC);
	}
	public Integer getMinute(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("m", tz).format(getValue().getA()));
	}
	public Integer getHour() {
		return getHour(TIME_ZONE_UTC);
	}
	public Integer getHour(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("h", tz).format(getValue().getA()));
	}
	public Integer getHourOfDay() {
		return getHourOfDay(TIME_ZONE_UTC);
	}
	public Integer getHourOfDay(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("H", tz).format(getValue().getA()));
	}
	public boolean getIsPM() {
		return getIsPM(TIME_ZONE_UTC);
	}
	public boolean getIsPM(TimeZone tz) {
		return "pm".equalsIgnoreCase(new BasicDateFormatter("a", tz).format(getValue().getA()));
	}
	public Integer getSecond() {
		return getSecond(TIME_ZONE_UTC);
	}
	public Integer getSecond(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("s", tz).format(getValue().getA()));
	}
	public Integer getMillis() {
		return getMillis(TIME_ZONE_UTC);
	}
	public Integer getMillis(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("S", tz).format(getValue().getA()));
	}
	public Integer getDay() {
		return getDay(TIME_ZONE_UTC);
	}
	public Integer getDay(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("d", tz).format(getValue().getA()));
	}
	public Integer getMonth() {
		return getMonth(TIME_ZONE_UTC);
	}
	public Integer getMonth(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("M", tz).format(getValue().getA()));
	}
	public Integer getYear() {
		return getYear(TIME_ZONE_UTC);
	}
	public Integer getYear(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("yyyy", tz).format(getValue().getA()));
	}

}
