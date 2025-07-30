/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.f1.utils.formatter.BasicDateFormatter;
import com.f1.utils.formatter.BasicNumberFormatter;
import com.f1.utils.formatter.PercentFormatter;
import com.f1.utils.formatter.ThreadLocalFormatter;
import com.f1.utils.impl.BasicBundledTextFormatter;

public class BasicLocaleFormatter implements LocaleFormatter {
	public static final int MAX_DECIMALS = 13;
	static private Map<String, Integer> dateFormatterCodes = new HashMap<String, Integer>();
	static {
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_DATE, DATE);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_TIME, TIME);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_TIME_MILLIS, TIME_MILLIS);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_DATETIME, DATETIME);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_YYYYMMDDHHMMSSSSS, YYYYMMDDHHMMSSSSS);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_MMDDYYYY_HHMMSS, MMDDYYYY_HHMMSS);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_MMDDYYYY_HHMMSSA, MMDDYYYY_HHMMSSA);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_DATETIME_LONG, DATETIME_LONG);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_DATE_DAYS, DATE_DAYS);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_DATETIME_FULL, DATETIME_FULL);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_DATETIME_LONG_FULL, DATETIME_LONG_FULL);
		CH.putOrThrow(dateFormatterCodes, DATEFORMAT_MMDDYYYY, MMDDYYYY);
	}

	public static final String YYYYMMDD_FORMAT = "yyyyMMdd";
	public static final String MMDDYYYY_HHMMSS_FORMAT = "MM/dd/yyyy HH:mm:ss";
	public static final String MMDDYYYY_HHMMSSA_FORMAT = "MM/dd/yyyy hh:mm:ss a";
	public static final String MMDDYYYY_FORMAT = "MM/dd/yyyy";
	public static final String DATE_FORMAT = "yyyyMMdd";
	public static final String DATE_DAYS_FORMAT = "yyDDD";
	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String TIME_MILLIS_FORMAT = "HH:mm:ss.SSS";
	public static final String HHMMSS_FORMAT = "HHmmss";
	public static final String DATETIME_FORMAT = "yyyyMMdd-HH:mm:ss";
	public static final String DATETIME_FULL_FORMAT = "yyyyMMdd-HH:mm:ss.SSS";
	public static final String DATETIME_LONG_FORMAT = "yyyyMMdd-HH:mm:ss zzz";
	public static final String DATETIME_LONG_FULL_FORMAT = "yyyyMMdd-HH:mm:ss.SSS zzz";
	public static final String YYYYMMDDHHMMSSSSS_FORMAT = "yyyyMMddHHmmssSSS";
	private static final long DEFAULT_CHECK_FREQUENCY = 1000L;
	private final Formatter[] numberFormatters = new Formatter[MAX_DECIMALS + 1];
	private final Formatter[] priceFormatters = new Formatter[MAX_DECIMALS + 1];
	private final Formatter[] percentFormatters = new Formatter[MAX_DECIMALS + 1];
	private final Formatter[] dateFormatters = new Formatter[MAX_DECIMALS + 1];
	private final Currency currency;
	private final TimeZone timeZone;
	private final Locale locale;
	private final boolean threadSafe;
	private final BundledTextFormatter messageFormatter;
	private File[] files = new File[0];
	private final HashMap<String, Formatter> customFormatters;
	private List<LocalizedLocale> locales;
	private List<LocalizedTimeZone> timeZones;
	private final Locale numberSeparatorLocale;

	public BasicLocaleFormatter(Locale locale, TimeZone timeZone, boolean threadSafe, File[] messageFiles, Map<String, Formatter> customFormatters) {
		this(locale, timeZone, threadSafe, messageFiles, customFormatters, null);
	}
	public BasicLocaleFormatter(Locale locale, TimeZone timeZone, boolean threadSafe, File[] messageFiles, Map<String, Formatter> customFormatters, Locale numberSeparatorLocale) {
		this(locale, timeZone, threadSafe, messageFiles, customFormatters, numberSeparatorLocale, BundledTextFormatter.OPTION_ON_KEY_MISSING_DEBUG);
	}
	public BasicLocaleFormatter(Locale locale, TimeZone timeZone, boolean threadSafe, File[] messageFiles, Map<String, Formatter> customFormatters,
			int bundledTextFormatterOptions) {
		this(locale, timeZone, threadSafe, messageFiles, customFormatters, null, BundledTextFormatter.OPTION_ON_KEY_MISSING_DEBUG);
	}
	public BasicLocaleFormatter(Locale locale, TimeZone timeZone, boolean threadSafe, File[] messageFiles, Map<String, Formatter> customFormatters, Locale numberSeparatorLocale,
			int bundledTextFormatterOptions) {

		this.files = OH.noNull(messageFiles, OH.EMPTY_FILE_ARRAY);
		this.locale = locale;
		this.timeZone = timeZone;
		this.numberSeparatorLocale = numberSeparatorLocale;
		Currency currency;
		try {
			currency = Currency.getInstance(locale);
		} catch (Exception e) {
			currency = null;
		}
		this.currency = currency;
		this.threadSafe = threadSafe;
		this.customFormatters = new HashMap<String, Formatter>();
		if (customFormatters != null)
			this.customFormatters.putAll(customFormatters);
		if (threadSafe) {
			for (Map.Entry<String, Formatter> e : customFormatters.entrySet())
				e.setValue(wrap(e.getValue()));
		}
		this.messageFormatter = new BasicBundledTextFormatter(DEFAULT_CHECK_FREQUENCY, files, this, bundledTextFormatterOptions);
	}

	@Override
	public Formatter getNumberFormatter(int decimals) {
		Formatter r = decimals >= MAX_DECIMALS ? null : numberFormatters[decimals];
		if (r == null) {
			NumberFormat nf = NumberFormat.getInstance(this.numberSeparatorLocale == null ? getLocale() : this.numberSeparatorLocale);
			nf.setMinimumFractionDigits(decimals);
			nf.setMaximumFractionDigits(decimals);
			nf.setGroupingUsed(true);
			r = wrap(new BasicNumberFormatter(nf));
			if (decimals < MAX_DECIMALS)
				numberFormatters[decimals] = r;
		}
		return r;
	}

	@Override
	public Formatter getPriceFormatter(int decimals) {
		Formatter r = decimals >= MAX_DECIMALS ? null : priceFormatters[decimals];
		if (r == null) {
			NumberFormat nf = NumberFormat.getInstance(this.numberSeparatorLocale == null ? getLocale() : this.numberSeparatorLocale);
			nf.setMinimumFractionDigits(decimals);
			nf.setMaximumFractionDigits(decimals);
			r = wrap(new BasicNumberFormatter(nf, getCurrency() == null ? "" : getCurrency().getSymbol(), null));
			if (decimals < MAX_DECIMALS)
				priceFormatters[decimals] = r;
		}
		return r;
	}

	@Override
	public Currency getCurrency() {
		return this.currency;
	}

	@Override
	public Formatter getPercentFormatter(int decimals) {
		Formatter r = decimals >= MAX_DECIMALS ? null : percentFormatters[decimals];
		if (r == null) {
			NumberFormat nf = NumberFormat.getInstance(this.numberSeparatorLocale == null ? getLocale() : this.numberSeparatorLocale);
			nf.setMinimumFractionDigits(decimals);
			nf.setMaximumFractionDigits(decimals);
			nf.setGroupingUsed(true);
			r = wrap(new PercentFormatter(new BasicNumberFormatter(nf), "%"));
			if (decimals < MAX_DECIMALS)
				percentFormatters[decimals] = r;
		}
		return r;
	}

	@Override
	public int getDateFormatterCode(String type) {
		if (SH.is(type) && type.charAt(0) > '0' && type.charAt(0) <= '9')
			return Integer.parseInt(type);
		return CH.getOrThrow(dateFormatterCodes, type, "invalid date formatter code");
	}

	@Override
	public Formatter getDateFormatter(int type) {
		return getDateFormatter(type, "");
	}

	public Formatter getDateFormatter(int type, String notNull) {
		Formatter r = dateFormatters[type];
		if (r == null) {
			switch (type) {
				case LocaleFormatter.DATE:
					r = wrap(new BasicDateFormatter(DATE_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.TIME:
					r = wrap(new BasicDateFormatter(TIME_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.TIME_MILLIS:
					r = wrap(new BasicDateFormatter(TIME_MILLIS_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.HHMMSS:
					r = wrap(new BasicDateFormatter(HHMMSS_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.DATETIME:
					r = wrap(new BasicDateFormatter(DATETIME_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.DATETIME_LONG:
					r = wrap(new BasicDateFormatter(DATETIME_LONG_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.DATE_DAYS:
					r = wrap(new BasicDateFormatter(DATE_DAYS_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.DATETIME_FULL:
					r = wrap(new BasicDateFormatter(DATETIME_FULL_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.DATETIME_LONG_FULL:
					r = wrap(new BasicDateFormatter(DATETIME_LONG_FULL_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.YYYYMMDDHHMMSSSSS:
					r = wrap(new BasicDateFormatter(YYYYMMDDHHMMSSSSS_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.MMDDYYYY:
					r = wrap(new BasicDateFormatter(MMDDYYYY_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.MMDDYYYY_HHMMSS:
					r = wrap(new BasicDateFormatter(MMDDYYYY_HHMMSS_FORMAT, timeZone, notNull));
					break;
				case LocaleFormatter.MMDDYYYY_HHMMSSA:
					r = wrap(new BasicDateFormatter(MMDDYYYY_HHMMSSA_FORMAT, timeZone, notNull));
					break;
				default:
					throw new RuntimeException("unknown date format type: " + type);
			}
		}
		return r;
	}

	private DateFormat initTz(SimpleDateFormat df) {
		df.setTimeZone(timeZone);
		return df;
	}

	protected Formatter wrap(Formatter formatter) {
		if (threadSafe)
			return new ThreadLocalFormatter(formatter);
		return formatter;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public boolean isThreadSafe() {
		return threadSafe;
	}

	@Override
	public BundledTextFormatter getBundledTextFormatter() {
		messageFormatter.checkForUpdate();
		return messageFormatter;
	}

	@Override
	public Formatter getCustomFormatter(String key) {
		return CH.getOrThrow(customFormatters, key, "unknown custom formatter");
	}

	@Override
	public List<LocalizedLocale> getAvailableLocales() {
		if (locales == null) {
			final Locale locales[] = Locale.getAvailableLocales();
			final List<LocalizedLocale> r = new ArrayList<LocalizedLocale>(locales.length);
			for (final Locale locale : Locale.getAvailableLocales())
				r.add(new LocalizedLocale(locale, getLocale()));
			Collections.sort(r);
			this.locales = Collections.unmodifiableList(r);
		}
		return locales;
	}
	@Override
	public List<LocalizedTimeZone> getAvailableTimeZones() {
		if (timeZones == null) {
			final String[] ids = TimeZone.getAvailableIDs();
			final List<LocalizedTimeZone> r = new ArrayList<LocalizedTimeZone>(ids.length);
			for (final String id : ids)
				r.add(new LocalizedTimeZone(TimeZone.getTimeZone(id), getLocale(), getTimeZone()));
			Collections.sort(r);
			this.timeZones = Collections.unmodifiableList(r);
		}
		return timeZones;
	}
}
