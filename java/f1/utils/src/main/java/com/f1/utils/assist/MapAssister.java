/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import com.f1.utils.IndentedStringBuildable;
import com.f1.utils.OH;

public class MapAssister implements Assister<Map<?, ?>> {

	final private RootAssister rootAssister;

	public MapAssister(RootAssister rootAssister) {
		this.rootAssister = rootAssister;
	}

	@Override
	public Object getNestedValue(Map o, String value, boolean throwOnError) {
		return o.get(value);
	}

	@Override
	public void toString(Map<?, ?> o, StringBuilder sb, IdentityHashMap<Object, Object> visited) {
		boolean first = true;
		sb.append(o.getClass().getSimpleName());
		sb.append('{');
		for (Map.Entry i : o.entrySet()) {
			if (first)
				first = false;
			else
				sb.append(',');
			rootAssister.toString(i.getKey(), sb, visited);
			sb.append('=');
			rootAssister.toString(i.getValue(), sb, visited);
		}
		sb.append('}');
	}

	@Override
	public void toLegibleString(Map<?, ?> o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visited, int maxlength) {
		if (sb.length() > maxlength)
			return;
		sb.append(o.getClass().getSimpleName());
		sb.append('{');
		if (sb.length() > maxlength)
			return;
		sb.appendNewLine();
		sb.indent();
		for (Map.Entry i : o.entrySet()) {
			rootAssister.toLegibleString(i.getKey(), sb, visited, maxlength);
			if (sb.length() > maxlength)
				return;
			sb.append('=');
			rootAssister.toLegibleString(i.getValue(), sb, visited, maxlength);
			if (sb.length() > maxlength)
				return;
			sb.append(',').appendNewLine();
		}
		sb.outdent();
		sb.append('}');
	}

	@Override
	public void toJson(Map<?, ?> o, StringBuilder sb) {
		boolean first = true;
		sb.append('{');
		for (Map.Entry i : o.entrySet()) {
			if (first)
				first = false;
			else
				sb.append(',');
			Object key = i.getKey();
			rootAssister.toJson(key == null ? null : i.getKey().toString(), sb);
			sb.append(':');
			rootAssister.toJson(i.getValue(), sb);
		}
		sb.append('}');

	}

	@Override
	public Map<?, ?> clone(Map<?, ?> o, IdentityHashMap<Object, Object> visited) {
		try {
			if (Collections.EMPTY_MAP == o)
				return o;
			Map r = o.getClass().newInstance();
			for (Map.Entry<?, ?> e : o.entrySet())
				r.put(rootAssister.clone(e.getKey(), visited), rootAssister.clone(e.getValue(), visited));
			return r;
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public Object toMapList(Map<?, ?> o, boolean storeNulls, String keyForClassNameOrNull) {
		try {
			Map r = o.getClass().newInstance();
			for (Map.Entry<?, ?> e : o.entrySet())
				r.put(rootAssister.toMapList(e.getKey(), storeNulls, keyForClassNameOrNull), rootAssister.toMapList(e.getValue(), storeNulls, keyForClassNameOrNull));
			return r;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
