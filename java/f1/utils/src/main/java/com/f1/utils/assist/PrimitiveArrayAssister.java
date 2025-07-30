/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import com.f1.utils.AH;
import com.f1.utils.IndentedStringBuildable;
import com.f1.utils.structs.PrimitiveArrayIterator;

public class PrimitiveArrayAssister implements Assister<Object> {

	private static final int MAX_PRINT_SIZE = 2000;
	final private RootAssister rootAssister;

	public PrimitiveArrayAssister(RootAssister rootAssister) {
		this.rootAssister = rootAssister;
	}

	@Override
	public Object getNestedValue(Object o, String value, boolean throwOnError) {
		int i = Integer.parseInt(value);
		if (!throwOnError && i < 0 || i > Array.getLength(o))
			return null;
		else
			return Array.get(o, i);
	}

	@Override
	public void toString(Object o, StringBuilder sb, IdentityHashMap<Object, Object> visisted) {
		boolean first = true;
		sb.append(o.getClass().getComponentType().getSimpleName());
		sb.append('[');
		int cnt = 0;
		for (Object i : iterate(o)) {
			if (first)
				first = false;
			else
				sb.append(',');
			if (cnt++ > MAX_PRINT_SIZE) {
				sb.append("<suppressing remaining>");
				break;
			}
			sb.append(i);
		}
		sb.append(']');
	}

	private Iterable<?> iterate(Object o) {
		return new PrimitiveArrayIterator(o);
	}

	@Override
	public Object clone(Object o, IdentityHashMap<Object, Object> visisted) {
		return AH.cloneArray(o);
	}

	@Override
	public void toLegibleString(Object o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visisted, int maxlength) {
		boolean first = true;
		sb.append(o.getClass().getComponentType().getSimpleName()).append('[').append(Array.getLength(o)).append("] {").appendNewLine();
		if (sb.length() >= maxlength)
			return;
		sb.indent();
		int cnt = 0;
		for (Object i : iterate(o)) {
			if (cnt > 0) {
				sb.append(", ");
				if (cnt % 100 == 0)
					sb.appendNewLine();
			}
			cnt++;
			sb.append(i);
			if (sb.length() >= maxlength)
				return;
		}
		sb.appendNewLine();
		sb.outdent();
		sb.append('}');
	}

	@Override
	public void toJson(Object o, StringBuilder sb) {
		boolean first = true;
		sb.append('[');
		for (Object i : iterate(o)) {
			if (first)
				first = false;
			else
				sb.append(',');
			rootAssister.toJson(i, sb);
		}
		sb.append(']');
	}

	@Override
	public Object toMapList(Object o, boolean storeNulls, String keyForClassNameOrNull) {
		List<Object> r = new ArrayList<Object>(Array.getLength(o));
		for (Object v : iterate(o))
			r.add(v);
		return r;
	}

}
