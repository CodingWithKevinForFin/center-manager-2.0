package com.f1.utils;

import java.io.File;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.f1.utils.formatter.BasicNumberFormatter;
import com.f1.utils.formatter.PercentFormatter;

public class BasicLocaleFormatterTests {

	private File[] files = new File[0];

	@Test
	public void testBasicLocaleFormatter() {
		Locale[] locales = Locale.getAvailableLocales();
		String[] zones = TimeZone.getAvailableIDs();
		BasicLocaleFormatter format;
		for (int i = 0; i < locales.length; i++) {
			for (int j = 0; j < zones.length; j++) {
				if (!locales[i].getCountry().equals("")) {
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), true, files, new HashMap());
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), false, files, new HashMap());
				}
			}
		}
	}

	@Test
	public void testBasicLocaleFormatterFail() {
		Locale[] locales = Locale.getAvailableLocales();
		String[] zones = TimeZone.getAvailableIDs();
		BasicLocaleFormatter format;
		for (int i = 0; i < locales.length; i++) {
			for (int j = 0; j < zones.length; j++) {
				if (locales[i].getCountry().equals("")) {
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), true, files, new HashMap());
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), false, files, new HashMap());
				}
			}
		}
	}

	// TODO
	@Test
	public void testGetNumberFormatter() {
		Locale[] locales = Locale.getAvailableLocales();
		String[] zones = TimeZone.getAvailableIDs();
		BasicLocaleFormatter format;
		for (int i = 0; i < locales.length; i++) {
			for (int j = 0; j < zones.length; j++) {
				if (!locales[i].getCountry().equals("")) {
					NumberFormat nf = NumberFormat.getInstance(locales[i]);
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), true, files, new HashMap());
					for (int k = 1; k < 6; k++)
						OH.assertEq(format.wrap(new BasicNumberFormatter(nf)).format(k), format.getNumberFormatter(0).format(k));
				}
			}
		}
	}

	// TODO
	@Test
	public void testGetPriceFormatter() {
		Locale[] locales = Locale.getAvailableLocales();
		String[] zones = TimeZone.getAvailableIDs();
		BasicLocaleFormatter format;
		for (int i = 0; i < locales.length; i++) {
			for (int j = 0; j < zones.length; j++) {
				if (!locales[i].getCountry().equals("")) {
					NumberFormat nf = NumberFormat.getInstance(locales[i]);
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), true, files, new HashMap());
					for (int k = 1; k < 6; k++)
						OH.assertEq(format.wrap(new BasicNumberFormatter(nf, format.getCurrency().getSymbol(), null)).format(k), format.getPriceFormatter(0).format(k));
				}
			}
		}
	}

	@Test
	public void testGetCurrency() {
		Locale[] locales = Locale.getAvailableLocales();
		String[] zones = TimeZone.getAvailableIDs();
		BasicLocaleFormatter format;
		Currency c;
		for (int i = 0; i < locales.length; i++) {
			for (int j = 0; j < zones.length; j++) {
				if (!locales[i].getCountry().equals("")) {
					c = Currency.getInstance(locales[i]);
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), true, files, new HashMap());
					OH.assertEq(c, format.getCurrency());
				}
			}
		}
	}

	// TODO
	@Test
	public void testGetPercentFormatter() {
		Locale[] locales = Locale.getAvailableLocales();
		String[] zones = TimeZone.getAvailableIDs();
		BasicLocaleFormatter format;
		for (int i = 0; i < locales.length; i++) {
			for (int j = 0; j < zones.length; j++) {
				if (!locales[i].getCountry().equals("")) {
					NumberFormat nf = NumberFormat.getInstance(locales[i]);
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), true, files, new HashMap());
					for (int k = 1; k < 6; k++)
						OH.assertEq(format.wrap(new PercentFormatter(new BasicNumberFormatter(nf), "%")).format(k), format.getPercentFormatter(0).format(k));
				}
			}
		}
	}

	@Test
	public void testWrap() {
		Locale[] locales = Locale.getAvailableLocales();
		String[] zones = TimeZone.getAvailableIDs();
		BasicLocaleFormatter format;
		for (int i = 0; i < locales.length; i++) {
			for (int j = 0; j < zones.length; j++) {
				if (!locales[i].getCountry().equals("")) {
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), true, files, new HashMap());
				}
			}
		}
	}

	@Test
	public void testGetTimeZone() {
		Locale[] locales = Locale.getAvailableLocales();
		String[] zones = TimeZone.getAvailableIDs();
		BasicLocaleFormatter format;
		for (int i = 0; i < locales.length; i++) {
			for (int j = 0; j < zones.length; j++) {
				if (!locales[i].getCountry().equals("")) {
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), true, files, new HashMap());
					OH.assertEq(TimeZone.getTimeZone(zones[j]), format.getTimeZone());
				}
			}
		}
	}

	@Test
	public void testGetLocale() {
		Locale[] locales = Locale.getAvailableLocales();
		String[] zones = TimeZone.getAvailableIDs();
		BasicLocaleFormatter format;
		for (int i = 0; i < locales.length; i++) {
			for (int j = 0; j < zones.length; j++) {
				if (!locales[i].getCountry().equals("")) {
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), true, files, new HashMap());
					OH.assertEq(locales[i], format.getLocale());
				}
			}
		}
	}

	@Test
	public void testIsThreadSafe() {
		Locale[] locales = Locale.getAvailableLocales();
		String[] zones = TimeZone.getAvailableIDs();
		BasicLocaleFormatter format;
		for (int i = 0; i < locales.length; i++) {
			for (int j = 0; j < zones.length; j++) {
				if (!locales[i].getCountry().equals("")) {
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), true, files, new HashMap());
					OH.assertTrue(format.isThreadSafe());
					format = new BasicLocaleFormatter(locales[i], TimeZone.getTimeZone(zones[j]), false, files, new HashMap());
					OH.assertFalse(format.isThreadSafe());
				}
			}
		}
	}

}
