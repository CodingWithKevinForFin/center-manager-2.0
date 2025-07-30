/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A centralized manager for all types of "real world" formatting, such as dates, times, percents and numbers and language specific text. Each instance of the locale formatter is
 * tide to a locale specific configuration, including things like time zone, currency and locale. see {@link Formatter}
 */
public interface LocaleFormatter {
	static final String DATEFORMAT_TIME = "time";
	static final String DATEFORMAT_HHMMSS = "hhmmss";
	static final String DATEFORMAT_TIME_MILLIS = "timeMillis";
	static final String DATEFORMAT_DATE = "date";
	static final String DATEFORMAT_MMDDYYYY = "mmddyyyy";
	static final String DATEFORMAT_MMDDYYYY_HHMMSS = "mmddyyyy_hhmmss";
	static final String DATEFORMAT_MMDDYYYY_HHMMSSA = "mmddyyyy_hhmmssa";
	static final String DATEFORMAT_YYYYMMDDHHMMSSSSS = "yyyymmddhhmmsssss";
	static final String DATEFORMAT_DATETIME = "datetime";
	static final String DATEFORMAT_DATETIME_LONG = "longdatetime";
	static final String DATEFORMAT_DATE_DAYS = "days";
	static final String DATEFORMAT_DATETIME_FULL = "fulldatetime";
	static final String DATEFORMAT_DATETIME_LONG_FULL = "longfulldatetime";

	/**
	 * general format: HH:MM:SS
	 */
	static final int TIME = 1;

	/**
	 * general format: YYYYMMDD
	 */
	static final int DATE = 2;

	/**
	 * general format: yyyyMMdd-HH:mm:ss
	 */
	static final int DATETIME = 3;

	/**
	 * general format: yyyyMMdd zzz-HH:mm:ss
	 */
	static final int DATETIME_LONG = 4;

	/**
	 * general format: YYYDDD
	 */
	static final int DATE_DAYS = 5;

	/**
	 * general format: yyyyMMdd-HH:mm:ss.SSS
	 */
	static final int DATETIME_FULL = 6;

	/**
	 * general format: yyyyMMdd zzz-HH:mm:ss
	 */
	static final int DATETIME_LONG_FULL = 7;

	/**
	 * general format: HHMMSS
	 */
	static final int HHMMSS = 8;

	/**
	 * general format: HH:MM:SS.SSS
	 */
	static final int TIME_MILLIS = 11;

	/**
	 * general format: MM/DD/YYYY
	 */
	static final int MMDDYYYY = 9;

	/**
	 * general format: YYYYMMDDHHMMSSsss
	 */
	static final int YYYYMMDDHHMMSSSSS = 10;

	/**
	 * general format: MM/DD/YYYY HH:mm:ss
	 */
	static final int MMDDYYYY_HHMMSS = 12;

	/**
	 * general format: MM/DD/YYYY hh:mm:ss am
	 */
	static final int MMDDYYYY_HHMMSSA = 13;
	/**
	 * @param decimals
	 *            fixed number of digits following(to the right of) the local specific decimal. Zero indicates no decimal
	 * @return locale specific formatter for creating string representations of {@link Number}, meeting supplied properties
	 */
	Formatter getNumberFormatter(int decimals);

	/**
	 * @param decimals
	 *            fixed number of digits following(to the right of) the local specific decimal. Zero indicates no decimal
	 * @return locale specific formatter (taking into consideration the currency) for creating a string representations of {@link Number} as a price, meeting supplied properties
	 */
	Formatter getPriceFormatter(int decimals);

	/**
	 * @param decimals
	 *            fixed number of digits following(to the right of) the local specific decimal. Zero indicates no decimal
	 * @return locale specific formatter for creating a string representations of {@link Number} as a percentage, meeting supplied properties
	 */
	Formatter getPercentFormatter(int decimals);

	/**
	 * @param type
	 *            see DATEFORMAT_* constants
	 * @return locale specific formatter (for creating a string representations of {@link Number} as a date, meeting supplied properties
	 */
	Formatter getDateFormatter(int type);

	/**
	 * @param type
	 *            see DATEFORMAT_* constants notNull String to replace invalid dates
	 * @return locale specific formatter (for creating a string representations of {@link Number} as a date, meeting supplied properties
	 */
	Formatter getDateFormatter(int type, String notNull);

	/**
	 * 
	 * @param key
	 *            name of the registered custom formatters
	 * @return a custom formatter, never null
	 * @throws Exception
	 *             if the supplied key is not associated w/ a formatter
	 */
	Formatter getCustomFormatter(String key);

	/**
	 * Each date formatter constants has an associated text peer.
	 * 
	 * @param type
	 *            the name
	 * @return the code associated
	 */
	int getDateFormatterCode(String type);

	/**
	 * @return the timezone associated with this locale formatter
	 */
	TimeZone getTimeZone();

	/**
	 * @return the locale associated with this locale formatter
	 */
	Locale getLocale();

	/**
	 * @return the currency associated with this locale formatter
	 */
	Currency getCurrency();

	/**
	 * @return true iff the formatters can be used concurrently by multiple threads
	 */
	boolean isThreadSafe();

	/**
	 * @return the Bundle text formatter for doing complex formatting (of things like locale specific sentences)
	 */
	BundledTextFormatter getBundledTextFormatter();

	List<LocalizedLocale> getAvailableLocales();

	List<LocalizedTimeZone> getAvailableTimeZones();

}
