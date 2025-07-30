package com.f1.ami.web;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.impl.AmiWebHtmlImageCellFormatter;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.CheckboxWebCellFormatter;
import com.f1.suite.web.table.impl.ColorWebCellFormatter;
import com.f1.suite.web.table.impl.MaskedWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.suite.web.table.impl.ToggleButtonCellFormatter;
import com.f1.suite.web.tree.impl.FormatterManager;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.DateFormatNano;
import com.f1.utils.EH;
import com.f1.utils.Formatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.formatter.BasicDateFormatter;
import com.f1.utils.formatter.BasicNumberFormatter;
import com.f1.utils.formatter.MemoryFormatter;

public class AmiWebFormatterManager implements FormatterManager {
	private static final int MAX_DATEFORMATS_CACHE = 100;

	final private AmiWebService service;
	private BasicWebCellFormatter memoryWebCellFormatter;
	private BasicWebCellFormatter integerWebCellFormatter;
	private BasicWebCellFormatter symbolWebCellFormatter;
	private BasicWebCellFormatter showButtonWebCellFormatter;
	private BasicWebCellFormatter paramsWebCellFormatter;
	private BasicWebCellFormatter imageWebCellFormatter;
	private BasicWebCellFormatter checkboxWebCellFormatter;
	private BasicWebCellFormatter maskedWebCellFormatter;

	private BasicWebCellFormatter timeWebCellFormatter;
	private BasicWebCellFormatter timeSecsWebCellFormatter;
	private BasicWebCellFormatter timeMillisWebCellFormatter;
	private BasicWebCellFormatter timeMicrosWebCellFormatter;
	private BasicWebCellFormatter timeNanosWebCellFormatter;
	private BasicWebCellFormatter dateWebCellFormatter;
	private BasicWebCellFormatter datetimeWebCellFormatter;
	private BasicWebCellFormatter datetimeSecsWebCellFormatter;
	private BasicWebCellFormatter datetimeMillisWebCellFormatter;
	private BasicWebCellFormatter datetimeMicrosWebCellFormatter;
	private BasicWebCellFormatter datetimeNanosWebCellFormatter;
	private AmiWebHtmlFormatter htmlWebCellFormatter;

	private BasicWebCellFormatter basicFormatter;
	private AmiWebDddFormatter dddFormatter;

	private BasicDateFormatter timeFormatter;
	private BasicDateFormatter timeSecsFormatter;
	private BasicDateFormatter timeMillisFormatter;
	private BasicDateFormatter timeMicrosFormatter;
	private BasicDateFormatter timeNanosFormatter;
	private BasicDateFormatter dateFormatter;
	private BasicDateFormatter datetimeFormatter;
	private BasicDateFormatter datetimeSecsFormatter;
	private BasicDateFormatter datetimeMillisFormatter;
	private BasicDateFormatter datetimeMicrosFormatter;
	private BasicDateFormatter datetimeNanosFormatter;
	private BasicWebCellFormatter colorWebCellFormatter;

	private Formatter integerFormatter;

	private Formatter decimalFormatter;
	private Formatter scientificFormatter;

	private BasicWebCellFormatter decimalWebCellFormatter;

	private Formatter[] decimalFormatters = new Formatter[BasicLocaleFormatter.MAX_DECIMALS + 1];
	private Formatter[] percentFormatters = new Formatter[BasicLocaleFormatter.MAX_DECIMALS + 1];
	private Formatter[] priceFormatters = new Formatter[BasicLocaleFormatter.MAX_DECIMALS + 1];

	private BasicLocaleFormatter localeFormatter;

	public final static Map<String, Locale> NUMBER_FORMATS_2_LOCALES = new HashMap<String, Locale>();
	static {
		Double dummy = 1234567890.123;
		Locale[] availableLocales = Locale.getAvailableLocales();
		Locale loc;
		for (int i = 0; i < availableLocales.length; i++) {
			loc = availableLocales[i];
			NUMBER_FORMATS_2_LOCALES.put(NumberFormat.getInstance(loc).format(dummy), loc);
			NUMBER_FORMATS_2_LOCALES.put(NumberFormat.getIntegerInstance(loc).format(dummy), loc);
		}
	}

	public AmiWebFormatterManager(AmiWebService service) {
		this.service = service;
	}

	public void createFormatters() {
		AmiWebVarsManager varsManager = this.service.getVarsManager();
		this.localeFormatter = new BasicLocaleFormatter(varsManager.getLocale(), varsManager.getTimeZone(), false, OH.EMPTY_FILE_ARRAY, Collections.EMPTY_MAP,
				NUMBER_FORMATS_2_LOCALES.get(varsManager.getNumberSeparator()));

		this.memoryWebCellFormatter = new NumberWebCellFormatter(new MemoryFormatter()).setDefaultWidth(80).lockFormatter();
		this.basicFormatter = new AmiWebBasicWebCellFormatter().setDefaultWidth(100).setComparator(SH.COMPARATOR_CASEINSENSITIVE).lockFormatter();
		this.integerWebCellFormatter = new NumberWebCellFormatter(this.integerFormatter = localeFormatter.getNumberFormatter(0)).setDefaultWidth(80).lockFormatter();
		this.decimalFormatter = localeFormatter.getNumberFormatter(varsManager.getNumberDecimalPrecision());
		for (int i = 0; i <= BasicLocaleFormatter.MAX_DECIMALS; i++) {
			this.decimalFormatters[i] = localeFormatter.getNumberFormatter(i);
			this.percentFormatters[i] = localeFormatter.getPercentFormatter(i);
			this.priceFormatters[i] = localeFormatter.getPriceFormatter(i);
		}
		this.decimalWebCellFormatter = new NumberWebCellFormatter(this.decimalFormatter).setDefaultWidth(80).lockFormatter();
		this.symbolWebCellFormatter = new AmiWebBasicWebCellFormatter().setCssClass("bold").setDefaultWidth(60).lockFormatter();
		DecimalFormat df = new DecimalFormat("0.######E0");
		df.setMaximumFractionDigits(varsManager.getNumberDecimalPrecision());
		df.setMinimumFractionDigits(0);
		df.setMaximumIntegerDigits(varsManager.getNumberDecimalPrecision());
		df.setMinimumIntegerDigits(0);
		this.scientificFormatter = new BasicNumberFormatter(df);

		this.timeFormatter = new BasicDateFormatter(service.getVarsManager().getTimeFormat(), this.getTimezone());
		this.timeSecsFormatter = new BasicDateFormatter(service.getVarsManager().getTimeWithSecondsFormat(), this.getTimezone());
		this.timeMillisFormatter = new BasicDateFormatter(service.getVarsManager().getTimeWithMillisecondsFormat(), this.getTimezone());
		this.timeMicrosFormatter = new BasicDateFormatter(service.getVarsManager().getTimeWithMicrosecondsFormat(), this.getTimezone());
		this.timeNanosFormatter = new BasicDateFormatter(service.getVarsManager().getTimeWithNanosecondsFormat(), this.getTimezone());
		this.dateFormatter = new BasicDateFormatter(service.getVarsManager().getDateFormat(), this.getTimezone());
		this.datetimeFormatter = new BasicDateFormatter(service.getVarsManager().getDateTimeFormat(), this.getTimezone());
		this.datetimeSecsFormatter = new BasicDateFormatter(service.getVarsManager().getDateTimeWithSecondsFormat(), this.getTimezone());
		this.datetimeMillisFormatter = new BasicDateFormatter(service.getVarsManager().getDateTimeWithMillisecondsFormat(), this.getTimezone());
		this.datetimeMicrosFormatter = new BasicDateFormatter(service.getVarsManager().getDateTimeWithMicrosecondsFormat(), this.getTimezone());
		this.datetimeNanosFormatter = new BasicDateFormatter(service.getVarsManager().getDateTimeWithNanosecondsFormat(), this.getTimezone());
		this.colorWebCellFormatter = new ColorWebCellFormatter();

		this.timeWebCellFormatter = new NumberWebCellFormatter(this.timeFormatter).setDefaultWidth(80).lockFormatter();
		this.timeSecsWebCellFormatter = new NumberWebCellFormatter(timeSecsFormatter).setDefaultWidth(60).lockFormatter();
		this.timeMillisWebCellFormatter = new NumberWebCellFormatter(this.timeMillisFormatter).setDefaultWidth(80).lockFormatter();
		this.timeMicrosWebCellFormatter = new NumberWebCellFormatter(this.timeMicrosFormatter).setDefaultWidth(80).lockFormatter();
		this.timeNanosWebCellFormatter = new NumberWebCellFormatter(this.timeNanosFormatter).setDefaultWidth(80).lockFormatter();
		this.datetimeWebCellFormatter = new NumberWebCellFormatter(datetimeFormatter).setDefaultWidth(120).lockFormatter();
		this.datetimeSecsWebCellFormatter = new NumberWebCellFormatter(datetimeSecsFormatter).setDefaultWidth(120).lockFormatter();
		this.datetimeMillisWebCellFormatter = new NumberWebCellFormatter(datetimeMillisFormatter).setDefaultWidth(120).lockFormatter();
		this.datetimeMicrosWebCellFormatter = new NumberWebCellFormatter(datetimeMicrosFormatter).setDefaultWidth(120).lockFormatter();
		this.datetimeNanosWebCellFormatter = new NumberWebCellFormatter(datetimeNanosFormatter).setDefaultWidth(120).lockFormatter();
		this.dateWebCellFormatter = new NumberWebCellFormatter(dateFormatter).setDefaultWidth(80).lockFormatter();
		this.showButtonWebCellFormatter = new ToggleButtonCellFormatter("image_show_dn", "image_show_up", "shown", "hidden").setDefaultWidth(30).lockFormatter();
		this.imageWebCellFormatter = new AmiWebHtmlImageCellFormatter();
		this.checkboxWebCellFormatter = new CheckboxWebCellFormatter();
		this.htmlWebCellFormatter = new AmiWebHtmlFormatter(this.service);
		this.dddFormatter = new AmiWebDddFormatter(100);
		this.dddFormatter.lockFormatter();
		this.paramsWebCellFormatter = new AmiWebParamsWebCellFormatter();
		this.maskedWebCellFormatter = new MaskedWebCellFormatter();
	}
	public BasicWebCellFormatter getTimeWebCellFormatter() {
		return timeWebCellFormatter;
	}
	public BasicWebCellFormatter getTimeSecsWebCellFormatter() {
		return timeSecsWebCellFormatter;
	}
	public BasicWebCellFormatter getTimeMillisWebCellFormatter() {
		return timeMillisWebCellFormatter;
	}
	public BasicWebCellFormatter getTimeMicrosWebCellFormatter() {
		return timeMicrosWebCellFormatter;
	}
	public BasicWebCellFormatter getTimeNanosWebCellFormatter() {
		return timeNanosWebCellFormatter;
	}

	public BasicWebCellFormatter getDateWebCellFormatter() {
		return dateWebCellFormatter;
	}
	public BasicWebCellFormatter getDateTimeWebCellFormatter() {
		return datetimeWebCellFormatter;
	}
	public BasicWebCellFormatter getDateTimeSecsWebCellFormatter() {
		return datetimeSecsWebCellFormatter;
	}
	public BasicWebCellFormatter getDateTimeMillisWebCellFormatter() {
		return datetimeMillisWebCellFormatter;
	}
	public BasicWebCellFormatter getDateTimeMicrosWebCellFormatter() {
		return datetimeMicrosWebCellFormatter;
	}
	public BasicWebCellFormatter getDateTimeNanosWebCellFormatter() {
		return datetimeNanosWebCellFormatter;
	}

	public BasicWebCellFormatter getShowButtonWebCellFormatter() {
		return showButtonWebCellFormatter;
	}
	public BasicWebCellFormatter getMemoryFormatter() {
		return memoryWebCellFormatter;
	}
	public BasicWebCellFormatter getBasicFormatter() {
		return basicFormatter;
	}
	public BasicWebCellFormatter getIntegerWebCellFormatter() {
		return integerWebCellFormatter;
	}
	public BasicWebCellFormatter getDecimalWebCellFormatter() {
		return decimalWebCellFormatter;
	}
	public Formatter getIntegerFormatter() {
		return integerFormatter;
	}
	public Formatter getDecimalFormatter() {
		return decimalFormatter;
	}
	public Formatter getDecimalFormatter(int decimals) {
		if (decimals == AmiConsts.DEFAULT)
			decimals = this.service.getVarsManager().getNumberDecimalPrecision();
		if (decimals >= BasicLocaleFormatter.MAX_DECIMALS)
			return localeFormatter.getNumberFormatter(decimals);
		return this.decimalFormatters[Math.max(decimals, 0)];
	}
	public Formatter getPercentFormatter(int decimals) {
		if (decimals == AmiConsts.DEFAULT)
			decimals = this.service.getVarsManager().getNumberDecimalPrecision();
		if (decimals >= BasicLocaleFormatter.MAX_DECIMALS)
			return localeFormatter.getPercentFormatter(decimals);
		return this.percentFormatters[Math.max(decimals, 0)];
	}
	public Formatter getPriceFormatter(int decimals) {
		if (decimals == AmiConsts.DEFAULT)
			decimals = this.service.getVarsManager().getNumberDecimalPrecision();
		if (decimals >= BasicLocaleFormatter.MAX_DECIMALS)
			return localeFormatter.getPriceFormatter(decimals);
		return this.priceFormatters[Math.max(decimals, 0)];
	}
	public BasicWebCellFormatter getSymbolWebCellFormatter() {
		return symbolWebCellFormatter;
	}
	public BasicWebCellFormatter getImageWebCellFormatter() {
		return imageWebCellFormatter;
	}
	public BasicWebCellFormatter getCheckboxWebCellFormatter() {
		return checkboxWebCellFormatter;
	}
	public BasicWebCellFormatter getMaskedWebCellFormatter() {
		return maskedWebCellFormatter;
	}
	public WebCellFormatter getParamsWebCellFormatter() {
		return paramsWebCellFormatter;
	}
	public WebCellFormatter getHtmlWebCellFormatter() {
		return htmlWebCellFormatter;
	}
	public WebCellFormatter getDddFormatter() {
		return this.dddFormatter;
	}
	public WebCellFormatter getFormatter(Class<?> type) {
		if (type == DateMillis.class)
			return this.getDateTimeMillisWebCellFormatter();
		else if (type == DateNanos.class)
			return this.getDateTimeNanosWebCellFormatter();
		else if (type == Double.class || type == Float.class)
			return this.getDecimalWebCellFormatter();
		else if (type == Integer.class || type == Long.class)
			return this.getIntegerWebCellFormatter();
		else if (type == Color.class)
			return this.getColorWebCellFormatter();
		return this.getBasicFormatter();
	}

	public WebCellFormatter getColorWebCellFormatter() {
		return this.colorWebCellFormatter;
	}

	private DateFormatNano ERROR_FORMATTER = new DateFormatNano("");
	private Map<String, DateFormatNano> cachedFormatters = new HashMap<String, DateFormatNano>();
	private Date tmp = new Date();

	private String lastTimezone;
	private String lastFormat;
	private DateFormatNano lastSimpleDateFormat;

	public String getformatDate(String format, long time, String timezone) {
		if (OH.eq(lastFormat, format) && OH.eq(lastTimezone, timezone) && lastTimezone != null) {
			tmp.setTime(time);
			return lastSimpleDateFormat.format(tmp);
		}
		DateFormatNano cached;
		cached = cachedFormatters.get(format);
		if (cached == null)
			try {
				cached = new DateFormatNano(format);
				if (cachedFormatters.size() < MAX_DATEFORMATS_CACHE)
					cachedFormatters.put(format, cached);
			} catch (Exception e) {
				cachedFormatters.put(format, ERROR_FORMATTER);
				return null;
			}
		else if (cached == ERROR_FORMATTER)
			return null;
		TimeZone tz = timezone == null ? getTimezone() : EH.getTimeZoneNoThrow(timezone);
		if (tz == null)
			return null;
		cached.setTimeZone(tz);
		tmp.setTime(time);
		lastFormat = format;
		lastTimezone = timezone;
		lastSimpleDateFormat = cached;
		return cached.format(tmp);
	}
	public String getformatDate(String format, DateMillis time, String timezone) {
		if (OH.eq(lastFormat, format) && OH.eq(lastTimezone, timezone) && lastTimezone != null) {
			return lastSimpleDateFormat.format(time);
		}
		DateFormatNano cached;
		cached = cachedFormatters.get(format);
		if (cached == null)
			try {
				cached = new DateFormatNano(format);
				if (cachedFormatters.size() < MAX_DATEFORMATS_CACHE)
					cachedFormatters.put(format, cached);
			} catch (Exception e) {
				cachedFormatters.put(format, ERROR_FORMATTER);
				return null;
			}
		else if (cached == ERROR_FORMATTER)
			return null;
		TimeZone tz = timezone == null ? getTimezone() : EH.getTimeZoneNoThrow(timezone);
		if (tz == null)
			return null;
		cached.setTimeZone(tz);
		lastFormat = format;
		lastTimezone = timezone;
		lastSimpleDateFormat = cached;
		return cached.format(time);
	}
	public String getformatDate(String format, DateNanos time, String timezone) {
		if (OH.eq(lastFormat, format) && OH.eq(lastTimezone, timezone) && lastTimezone != null) {
			return lastSimpleDateFormat.format(time);
		}
		DateFormatNano cached;
		cached = cachedFormatters.get(format);
		if (cached == null)
			try {
				cached = new DateFormatNano(format);
				if (cachedFormatters.size() < MAX_DATEFORMATS_CACHE)
					cachedFormatters.put(format, cached);
			} catch (Exception e) {
				cachedFormatters.put(format, ERROR_FORMATTER);
				return null;
			}
		else if (cached == ERROR_FORMATTER)
			return null;
		TimeZone tz = timezone == null ? getTimezone() : EH.getTimeZoneNoThrow(timezone);
		if (tz == null)
			return null;
		cached.setTimeZone(tz);
		lastFormat = format;
		lastTimezone = timezone;
		lastSimpleDateFormat = cached;
		return cached.format(time);
	}
	private TimeZone getTimezone() {
		return this.service.getVarsManager().getTimeZone();
	}

	public BasicDateFormatter getTimeFormatter() {
		return timeFormatter;
	}
	public BasicDateFormatter getTimeSecsFormatter() {
		return timeSecsFormatter;
	}
	public BasicDateFormatter getTimeMillisFormatter() {
		return timeMillisFormatter;
	}
	public BasicDateFormatter getTimeMicrosFormatter() {
		return timeMicrosFormatter;
	}
	public BasicDateFormatter getTimeNanosFormatter() {
		return timeNanosFormatter;
	}
	public BasicDateFormatter getDateFormatter() {
		return dateFormatter;
	}

	public BasicDateFormatter getDatetimeFormatter() {
		return datetimeFormatter;
	}
	public BasicDateFormatter getDatetimeSecsFormatter() {
		return datetimeSecsFormatter;
	}
	public BasicDateFormatter getDatetimeMillisFormatter() {
		return datetimeMillisFormatter;
	}
	public BasicDateFormatter getDatetimeMicrosFormatter() {
		return datetimeMicrosFormatter;
	}
	public BasicDateFormatter getDatetimeNanosFormatter() {
		return datetimeNanosFormatter;
	}

	public Formatter getScientificFormatter() {
		return this.scientificFormatter;
	}

}
