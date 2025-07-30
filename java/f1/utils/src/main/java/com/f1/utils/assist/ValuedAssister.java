/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.HashMap;
import java.util.IdentityHashMap;

import com.f1.base.PartialMessage;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.IndentedStringBuildable;
import com.f1.utils.OH;

public class ValuedAssister implements Assister<Valued> {

	final private RootAssister rootAssister;
	private byte skipTransience = 0;

	public ValuedAssister(RootAssister rootAssister) {
		super();
		this.rootAssister = rootAssister;
	}

	@Override
	public Object getNestedValue(Valued o, String value, boolean throwOnError) {
		ValuedSchema<? extends Valued> schema = o.askSchema();
		if (OH.isBetween(value.charAt(0), '0', '9')) {
			byte b;
			try {
				b = Byte.parseByte(value);
			} catch (RuntimeException e) {
				if (throwOnError)
					throw e;
				return null;
			}
			if (!throwOnError && (!schema.askSupportsPids() || !schema.askPidValid(b)))
				return null;
			return o.ask(b);
		} else {
			if (!throwOnError && !schema.askParamValid(value))
				return null;
			return o.ask(value);
		}
	}

	@Override
	public void toString(Valued o, StringBuilder sb, IdentityHashMap<Object, Object> visited) {
		ValuedSchema<? extends Valued> schema = o.askSchema();
		sb.append(schema.askOriginalType().getName());
		sb.append("{");
		boolean first = true;
		if (o instanceof PartialMessage) {
			for (ValuedParam p : ((PartialMessage) o).askExistingValuedParams()) {
				if ((p.getTransience() & skipTransience) != 0)
					continue;
				if (first)
					first = false;
				else
					sb.append(',');
				sb.append(p.getName()).append('=');
				if (p.isPrimitiveOrBoxed())
					p.append(o, sb);
				else
					rootAssister.toString(p.getValue(o), sb, visited);
			}
		} else {
			for (ValuedParam p : schema.askValuedParams()) {
				if ((p.getTransience() & skipTransience) != 0)
					continue;
				if (first)
					first = false;
				else
					sb.append(',');
				sb.append(p.getName()).append('=');
				if (p.isPrimitiveOrBoxed())
					p.append(o, sb);
				else
					rootAssister.toString(p.getValue(o), sb, visited);
			}
		}
		sb.append("}");
	}

	@Override
	public Valued clone(Valued o, IdentityHashMap<Object, Object> visisted) {
		Valued r = o.nw();
		for (ValuedParam vp : r.askSchema().askValuedParams()) {
			if ((vp.getTransience() & skipTransience) != 0)
				continue;
			if (vp.isImmutable())
				vp.copy(o, r);
			else
				vp.setValue(r, rootAssister.clone(vp.getValue(o), visisted));
		}
		return r;
	}

	@Override
	public void toLegibleString(Valued o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visited, int maxlength) {
		ValuedSchema<? extends Valued> schema = o.askSchema();
		if (sb.length() > maxlength)
			return;
		sb.append(schema.askOriginalType().getName());
		sb.append("{").appendNewLine();
		sb.indent();
		if (sb.length() > maxlength)
			return;
		boolean first = true;
		for (ValuedParam p : schema.askValuedParams()) {
			if ((p.getTransience() & skipTransience) != 0)
				continue;
			if (first)
				first = false;
			else {
				sb.append(',');
				sb.appendNewLine();
			}
			sb.append(p.getName()).append('=');
			if (p.isPrimitiveOrBoxed()) {
				p.append(o, sb);
			} else
				rootAssister.toLegibleString(p.getValue(o), sb, visited, maxlength);
			if (sb.length() > maxlength)
				return;
		}
		sb.appendNewLine();
		sb.outdent();
		sb.append("}");
	}

	@Override
	public void toJson(Valued o, StringBuilder sb) {
		ValuedSchema<? extends Valued> schema = o.askSchema();
		sb.append(schema.askOriginalType().getName());
		sb.append("{");
		boolean first = true;
		for (ValuedParam p : schema.askValuedParams()) {
			if ((p.getTransience() & skipTransience) != 0)
				continue;
			if (first)
				first = false;
			else
				sb.append(',');
			sb.append(p.getName()).append(':');
			if (p.isPrimitiveOrBoxed() && p.getReturnType() != char.class && p.getReturnType() != Character.class)
				p.append(o, sb);
			else
				rootAssister.toJson(p.getValue(o), sb);
		}
		sb.append("}");
	}

	public void setSkipTransience(byte mode) {
		this.skipTransience = mode;
	}

	public byte getSkipTransience(byte mode) {
		return skipTransience;
	}

	@Override
	public Object toMapList(Valued valued, boolean storeNulls, String keyForClassNameOrNull) {

		final HashMap<String, Object> r = new HashMap<String, Object>();
		if (keyForClassNameOrNull != null)
			r.put(keyForClassNameOrNull, valued.askSchema().askOriginalType().getName());
		for (ValuedParam<Valued> vs : valued.askSchema().askValuedParams()) {
			final Object value = vs.getValue(valued);
			if ((vs.getTransience() & skipTransience) != 0)
				continue;
			if (value != null) {
				if (vs.isPrimitiveOrBoxed())
					r.put(vs.getName(), value);
				else
					r.put(vs.getName(), rootAssister.toMapList(value, storeNulls, keyForClassNameOrNull));
			} else if (storeNulls)
				r.put(vs.getName(), null);
		}
		return r;
	}
}
