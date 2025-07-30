/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.IdentityHashMap;

import com.f1.utils.IndentedStringBuildable;

public class ArrayAssister implements Assister<Object[]> {

	final private RootAssister rootAssister;

	public ArrayAssister(RootAssister rootAssister) {
		this.rootAssister = rootAssister;
	}

	@Override
	public Object getNestedValue(Object[] o, String value, boolean throwOnError) {
		int i = Integer.parseInt(value);
		if (!throwOnError && i < 0 || i > o.length)
			return null;
		else
			return o[Integer.parseInt(value)];
	}

	@Override
	public void toString(Object[] o, StringBuilder sb, IdentityHashMap<Object, Object> visisted) {
		boolean first = true;
		sb.append(o.getClass().getComponentType().getSimpleName());
		sb.append('[');
		for (Object i : o) {
			if (first)
				first = false;
			else
				sb.append(',');
			rootAssister.toString(i, sb, visisted);
		}
		sb.append(']');
	}

	@Override
	public Object[] clone(Object[] o, IdentityHashMap<Object, Object> visisted) {
		Object[] r;
		try {
			r = o.getClass().newInstance();
		} catch (ClassCastException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		int j = 0;
		for (Object i : o)
			r[j++] = (rootAssister.clone(i, visisted));
		return r;
	}

	@Override
	public void toLegibleString(Object[] o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visisted, int maxlength) {
		boolean first = true;
		if (sb.length() >= maxlength)
			return;
		sb.append(o.getClass().getComponentType().getSimpleName()).append('[').appendNewLine();
		if (sb.length() >= maxlength)
			return;
		sb.indent();
		for (Object i : o) {
			rootAssister.toLegibleString(i, sb, visisted, maxlength);
			if (sb.length() >= maxlength)
				return;
			sb.appendNewLine();
		}
		sb.outdent();
		sb.append(']');
	}
	@Override
	public void toJson(Object[] o, StringBuilder sb) {
		boolean first = true;
		sb.append('[');
		for (Object i : o) {
			if (first)
				first = false;
			else
				sb.append(',');
			rootAssister.toJson(i, sb);
		}
		sb.append(']');
	}

	@Override
	public Object toMapList(Object[] o, boolean storeNulls, String keyForClassNameOrNull) {
		Object[] r = o.clone();
		for (int i = 0; i < r.length; i++)
			r[i] = rootAssister.toMapList(r[i], storeNulls, keyForClassNameOrNull);
		return r;
	}

}
