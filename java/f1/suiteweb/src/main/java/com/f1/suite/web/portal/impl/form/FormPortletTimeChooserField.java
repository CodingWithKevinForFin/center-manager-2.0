package com.f1.suite.web.portal.impl.form;

import java.util.Map;
import java.util.TimeZone;

import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.formatter.BasicDateFormatter;
import com.f1.utils.structs.Tuple2;

public class FormPortletTimeChooserField extends FormPortletField<Tuple2<Long, Long>> {

	private static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("UTC");
	private TimeZone tz;
	private boolean isRange;
	private String timeDisplayFormat;

	public FormPortletTimeChooserField(String title, TimeZone tz, boolean isRange) {
		super((Class) Tuple2.class, title);
		setValueNoFire(new Tuple2<Long, Long>());
		this.tz = tz;
		this.isRange = isRange;
		setDefaultValue(null);
	}
	@Override
	public String getjsClassName() {
		return "TimeChooserField";
	}

	public TimeZone getTimeZone() {
		return this.tz;
	}
	public void setTimeZone(TimeZone tz) {
		this.tz = tz;
	}
	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_CONFIG)) {
			JsFunction js = new JsFunction(pendingJs, jsObjectName, "initCalendar").addParam(isRange()).end();
			js.reset(pendingJs, jsObjectName, "setTimeDisplayFormat").addParamQuoted(this.getTimeDisplayFormat()).end();
		}
		super.updateJs(pendingJs);
	}

	@Override
	public FormPortletTimeChooserField setValue(Tuple2<Long, Long> startAndEnd) {
		super.setValue(startAndEnd);
		return this;
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		final Long start = CH.getOr(Caster_Long.INSTANCE, attributes, "start", null);
		final Long end = CH.getOr(Caster_Long.INSTANCE, attributes, "end", null);

		Tuple2<Long, Long> value = new Tuple2<Long, Long>(start, end);
		if (OH.eq(getValue(), value))
			return false;
		setValueNoFire(value);
		return true;
	}
	public String getJsValue() {
		Tuple2<Long, Long> val = getValue();
		if (val == null)
			return "null,null";
		return val.getA() + "," + val.getB();
	}

	@Override
	public FormPortletTimeChooserField setName(String name) {
		super.setName(name);
		return this;
	}

	public boolean isRange() {
		return isRange;
	}
	public Integer getMinuteStart() {
		return getMinuteStart(TIME_ZONE_UTC);
	}
	public Integer getMinuteStart(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("m", tz).format(getValue().getA()));
	}
	public Integer getHourStart() {
		return getHourStart(TIME_ZONE_UTC);
	}
	public Integer getHourStart(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("h", tz).format(getValue().getA()));
	}
	public Integer getHourOfDayStart() {
		return getHourOfDayStart(TIME_ZONE_UTC);
	}
	public Integer getHourOfDayStart(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("H", tz).format(getValue().getA()));
	}
	public Integer getSecondStart() {
		return getSecondStart(TIME_ZONE_UTC);
	}
	public Integer getSecondStart(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("s", tz).format(getValue().getA()));
	}
	public Integer getMillisStart() {
		return getMillisStart(TIME_ZONE_UTC);
	}
	public Integer getMillisStart(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("S", tz).format(getValue().getA()));
	}

	public Integer getMinuteEnd() {
		return getMinuteEnd(TIME_ZONE_UTC);
	}
	public Integer getMinuteEnd(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("m", tz).format(getValue().getB()));
	}
	public Integer getHourEnd() {
		return getHourEnd(TIME_ZONE_UTC);
	}
	public Integer getHourEnd(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("h", tz).format(getValue().getB()));
	}
	public Integer getHourOfDayEnd() {
		return getHourOfDayEnd(TIME_ZONE_UTC);
	}
	public Integer getHourOfDayEnd(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("H", tz).format(getValue().getB()));
	}
	public Integer getSecondEnd() {
		return getSecondEnd(TIME_ZONE_UTC);
	}
	public Integer getSecondEnd(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("s", tz).format(getValue().getB()));
	}
	public Integer getMillisEnd() {
		return getMillisEnd(TIME_ZONE_UTC);
	}
	public Integer getMillisEnd(TimeZone tz) {
		return Caster_Integer.INSTANCE.cast(new BasicDateFormatter("S", tz).format(getValue().getB()));
	}
	public boolean getIsPmStart() {
		return getIsPmStart(TIME_ZONE_UTC);
	}
	public boolean getIsPmStart(TimeZone tz) {
		return "pm".equalsIgnoreCase(new BasicDateFormatter("a", tz).format(getValue().getA()));
	}
	public boolean getIsPmEnd() {
		return getIsPmEnd(TIME_ZONE_UTC);
	}
	public boolean getIsPmEnd(TimeZone tz) {
		return "pm".equalsIgnoreCase(new BasicDateFormatter("a", tz).format(getValue().getB()));
	}

	public String getTimeDisplayFormat() {
		return timeDisplayFormat;
	}

	public void setTimeDisplayFormat(String timeDisplayFormat) {
		if (OH.eq(this.timeDisplayFormat, timeDisplayFormat))
			return;
		this.timeDisplayFormat = timeDisplayFormat;
		flagConfigChanged();
	}
}
