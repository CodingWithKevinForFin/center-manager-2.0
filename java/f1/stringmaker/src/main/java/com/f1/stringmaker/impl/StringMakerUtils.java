package com.f1.stringmaker.impl;

import java.util.Map;
import com.f1.stringmaker.StringMaker;

public class StringMakerUtils {

	public static String toString(String template, Map<String, Object> rootObject) {
		return toString(toMaker(template), rootObject);
	}

	private static final BasicStringMakerFactory DEFAULT = new BasicStringMakerFactory();

	public static StringMaker toMaker(String template) {
		return DEFAULT.get(template);
	}

	public static String toString(StringMaker maker, Map<String, Object> rootObject) {
		final BasicStringMakerSession session = new BasicStringMakerSession(rootObject);
		maker.toString(session);
		final String result = session.getSink().toString();
		return result;
	}

}
