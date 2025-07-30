/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.IdentityHashMap;

import com.f1.utils.IndentedStringBuildable;

public class NullAssister implements Assister<Object> {

	@Override
	public Object getNestedValue(Object o, String value, boolean throwOnError) {
		if (throwOnError)
			throw new NullPointerException();
		return null;
	}

	@Override
	public void toString(Object o, StringBuilder sb, IdentityHashMap<Object, Object> visisted) {
		sb.append("null");

	}

	@Override
	public void toLegibleString(Object o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visisted, int maxlength) {
		sb.append((String) null);
	}

	@Override
	public Object clone(Object o, IdentityHashMap<Object, Object> visisted) {
		return null;
	}

	@Override
	public void toJson(Object o, StringBuilder sb) {
		sb.append("null");
	}

	@Override
	public Object toMapList(Object o_, boolean storeNulls_, String keyForClassNameOrNull_) {
		return null;
	}

}
