package com.f1.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class FormatterHelper {

	public static <T extends Enum<T>> Map<T, String> formatEnum(Formatter formatter, Class<T> enm) {
		final Map<T, String> r = new HashMap<T, String>();
		final String prefix = enm.getSimpleName() + ".";
		for (T o : EnumSet.allOf(enm))
			r.put(o, prefix + o.name());
		return r;
	}

}
