package com.f1.utils;

import java.util.Locale;

public class LocalizedLocale implements Comparable<LocalizedLocale> {

	final private Locale locale;
	final private Locale displayLocale;
	private String id;
	private String toString;

	public LocalizedLocale(Locale locale, Locale displayLocale) {
		this.locale = locale;
		this.displayLocale = displayLocale;
	}
	public String getId() {
		if (id == null)
			id = locale.getLanguage() + ":" + locale.getCountry() + ":" + locale.getVariant();
		return id;
	}

	public String toDisplayString() {
		if (toString == null) {
			String country = locale.getDisplayCountry(displayLocale);
			String variant = locale.getDisplayVariant(displayLocale);
			String language = locale.getDisplayLanguage(displayLocale);
			StringBuilder sb = new StringBuilder();
			if (SH.is(country))
				sb.append(country).append(" - ");
			sb.append(language);
			if (SH.is(variant))
				sb.append(" (").append(country).append(')');
			toString = sb.toString();
		}
		return toString;
	}

	@Override
	public String toString() {
		return getId();
	}

	public Locale getLocale() {
		return locale;
	}
	@Override
	public int compareTo(LocalizedLocale o) {
		return o == null ? 1 : SH.COMPARATOR_CASEINSENSITIVE.compare(this.getId(), o.getId());
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof LocalizedLocale && compareTo((LocalizedLocale) obj) == 0;

	}
	public boolean isLocalLocale() {
		return OH.eq(locale, displayLocale);
	}
}
