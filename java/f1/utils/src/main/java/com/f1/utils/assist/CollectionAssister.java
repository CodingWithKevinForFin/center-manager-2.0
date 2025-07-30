/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

import com.f1.utils.IndentedStringBuildable;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class CollectionAssister implements Assister<Collection<?>> {

	final private RootAssister rootAssister;

	public CollectionAssister(RootAssister rootAssister) {
		this.rootAssister = rootAssister;
	}

	@Override
	public Object getNestedValue(Collection<?> o, String value, boolean throwOnError) {
		int pos;
		try {
			pos = SH.parseInt(value);
		} catch (RuntimeException e) {
			if (throwOnError)
				throw e;
			return null;
		}
		if (pos < 0)
			return null;
		if (o instanceof List) {
			List l = (List) o;
			if (l.size() <= pos)
				return null;
			return l.get(pos);
		}
		for (Object i : o)
			if (pos-- == 0)
				return i;
		return null;
	}

	@Override
	public void toString(Collection<?> o, StringBuilder sb, IdentityHashMap<Object, Object> visisted) {
		boolean first = true;
		sb.append(o.getClass().getSimpleName());
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
	public Collection<?> clone(Collection<?> o, IdentityHashMap<Object, Object> visisted) {
		try {
			if (o == Collections.EMPTY_LIST || o == Collections.EMPTY_SET)
				return o;
			Collection r = o.getClass().newInstance();
			for (Object i : o)
				r.add(rootAssister.clone(i, visisted));
			return r;
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public void toLegibleString(Collection<?> o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visisted, int maxlength) {
		boolean first = true;
		if (sb.length() >= maxlength)
			return;
		sb.append(o.getClass().getSimpleName()).append('[').appendNewLine();
		if (sb.length() >= maxlength)
			return;
		sb.indent();
		for (Object i : o) {
			rootAssister.toLegibleString(i, sb, visisted, maxlength);
			if (sb.length() >= maxlength)
				return;
			sb.append(',').appendNewLine();
		}
		sb.outdent();
		sb.append(']');
	}

	@Override
	public void toJson(Collection<?> o, StringBuilder sb) {
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
	public Object toMapList(Collection<?> o, boolean storeNulls, String keyForClassNameOrNull) {
		Collection r = new ArrayList(o.size());
		for (Object i : o)
			r.add(rootAssister.toMapList(i, storeNulls, keyForClassNameOrNull));
		return r;
	}

}
