package com.f1.base;

public class BaseHelper {
	public static String toSimpleString(Object o) {
		return o.getClass().getSimpleName() + '[' + o.toString() + ']';
	}
}
