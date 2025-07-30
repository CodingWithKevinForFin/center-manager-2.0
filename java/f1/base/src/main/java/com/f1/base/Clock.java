/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Used to get access to the current time and locale. This is prefered over accessing directly from the {@link System} instance because time dialation / shifting and unit testing
 * can be more effectively simulated
 */
public interface Clock {

	/**
	 * see {@link System#currentTimeMillis()}
	 * 
	 * @return current time in millis
	 */
	long getNow();

	/**
	 * @return the locale.
	 */
	Locale getLocale();

	/**
	 * @return the time zone
	 */
	TimeZone getTimeZone();

	/**
	 * see {@link System#currentTimeMillis()}
	 * 
	 * @return current time in nanos
	 */
	long getNowNano();

	/**
	 * see {@link System#currentTimeMillis()}
	 * 
	 * @return current time in nanos as an object
	 */
	DateNanos getNowNanoDate();

	/**
	 * see {@link System#currentTimeMillis()}
	 * 
	 * @return current time in millis as a VERY EXPENSIVE
	 */
	java.util.Date getNowDate();

}
