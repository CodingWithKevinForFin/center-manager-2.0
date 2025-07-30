/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.IdentityHashMap;

import com.f1.utils.IndentedStringBuildable;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class ImmutableAssister implements Assister<Object> {

	@Override
	public Object getNestedValue(Object o, String value, boolean throwOnError) {
		if ("".equals(value))
			return o;
		if (throwOnError)
			throw new RuntimeException("value not found: " + o);
		return null;
	}

	@Override
	public void toString(Object o, StringBuilder sb, IdentityHashMap<Object, Object> visisted) {
		if (o != null && o.getClass() == String.class)
			SH.quote('"', o.toString(), sb);
		else
			sb.append(o);
	}

	@Override
	public Object clone(Object o, IdentityHashMap<Object, Object> visisted) {
		return o;
	}

	@Override
	public void toLegibleString(Object o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visisted, int maxlength) {
		if (sb.length() > maxlength)
			return;
		if (o != null && o.getClass() == String.class)
			SH.quote('"', o.toString(), sb);
		else
			sb.append(o);
	}

	@Override
	public void toJson(Object o, StringBuilder sb) {
		Class<? extends Object> c = o.getClass();
		if (c != Character.class && c != char.class && (OH.isBoxed(c) || c.isPrimitive()))
			sb.append(o);
		else
			SH.quote(o.toString(), sb);
	}

	@Override
	public Object toMapList(Object o_, boolean storeNulls_, String keyForClassNameOrNull_) {
		return o_;
	}

}
