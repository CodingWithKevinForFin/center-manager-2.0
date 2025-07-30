/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;

import com.f1.utils.IndentedStringBuildable;
import com.f1.utils.OH;
import com.f1.utils.structs.Tuple;

public class TupleAssister implements Assister<Tuple> {

	final private RootAssister rootAssister;

	public TupleAssister(RootAssister rootAssister) {
		this.rootAssister = rootAssister;
	}

	@Override
	public Object getNestedValue(Tuple o, String value, boolean throwOnError) {
		return null;
	}

	@Override
	public void toString(Tuple o, StringBuilder sb, IdentityHashMap<Object, Object> visisted) {
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
	public Tuple clone(Tuple o, IdentityHashMap<Object, Object> visisted) {
		try {
			Tuple r = o.getClass().newInstance();
			for (int i = 0; i < o.getSize(); i++)
				r.setAt(i, rootAssister.clone(o.getAt(i), visisted));
			return r;
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public void toLegibleString(Tuple o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visisted, int maxlength) {
		boolean first = true;
		if (sb.length() >= maxlength)
			return;
		sb.append(o.getClass().getSimpleName()).append('[').appendNewLine();
		sb.indent();
		for (Object i : o) {
			rootAssister.toLegibleString(i, sb, visisted, maxlength);
			sb.append(',').appendNewLine();
			if (sb.length() >= maxlength)
				return;
		}
		sb.outdent();
		sb.append(']');
	}

	@Override
	public void toJson(Tuple o, StringBuilder sb) {
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
	public Object toMapList(Tuple o, boolean storeNulls, String keyForClassNameOrNull) {
		Collection r = new ArrayList(o.getSize());
		for (Object i : o)
			r.add(rootAssister.toMapList(i, storeNulls, keyForClassNameOrNull));
		return r;
	}

}
