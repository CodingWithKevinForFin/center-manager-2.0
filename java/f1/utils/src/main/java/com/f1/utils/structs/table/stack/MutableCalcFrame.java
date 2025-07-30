package com.f1.utils.structs.table.stack;

import java.util.Map.Entry;
import java.util.TreeMap;

import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.ToStringable;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.Tuple2;

public class MutableCalcFrame implements CalcFrame, ToStringable {

	final private HasherMap<String, Object> values = new HasherMap<String, Object>();
	final private BasicCalcTypes types;

	public MutableCalcFrame() {
		this.types = new BasicCalcTypes();
	}

	@Override
	public Object getValue(String key) {
		return this.values.get(key);
	}

	@Override
	public Object putValue(String key, Object value) {
		return this.values.put(key, value);
	}

	@Override
	public int hashCode() {
		return values.hashCode() ^ types.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	@Override
	public int getVarsCount() {
		return values.size();
	}
	public void clear() {
		this.values.clear();
	}
	public Object removeValue(String i) {
		return this.values.remove(i);
	}
	public Entry<String, Object> getOrCreateEntry(String key) {
		return this.values.getOrCreateEntry(key);
	}
	public void putAllTypeValues(CalcFrame frame) {
		for (String i : frame.getVarKeys())
			putTypeValue(i, frame.getType(i), frame.getValue(i));
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		final Object UNDEF = new Object();
		TreeMap<String, Tuple2<Class<?>, Object>> t = new TreeMap<String, Tuple2<Class<?>, Object>>();
		for (Entry<String, Object> i : values.entrySet())
			t.put(i.getKey(), new Tuple2<Class<?>, Object>(this.types.getType(i.getKey()), i.getValue()));
		for (String s : types.getVarKeys()) {
			if (!t.containsKey(s))
				t.put(s, new Tuple2<Class<?>, Object>(this.types.getType(s), UNDEF));
		}
		sb.append('{');
		boolean first = true;
		for (Entry<String, Tuple2<Class<?>, Object>> i : t.entrySet()) {
			if (first)
				first = false;
			else
				sb.append(',');
			Object value = i.getValue().getValue();
			String type = i.getValue().getKey() == null ? "null" : i.getValue().getKey().getSimpleName();
			if (value != UNDEF)
				sb.append(type).append(' ').append(i.getKey()).append('=').append(value);
			else
				sb.append(type).append(' ').append(i.getKey());
		}
		sb.append('}');
		return sb;
	}

	@Override
	public Class<?> getType(String key) {
		return types.getType(key);
	}

	@Override
	public boolean isVarsEmpty() {
		return types.isVarsEmpty();
	}

	@Override
	public Iterable<String> getVarKeys() {
		return types.getVarKeys();
	}

	public void putTypeValue(String varName, Class<?> clz, Object value) {
		this.types.putType(varName, clz);
		this.putValue(varName, value);
	}

	public void putType(String varName, Class<?> clz) {
		this.types.putType(varName, clz);
	}

	public void putAllTypes(CalcTypes types) {
		for (String i : types.getVarKeys())
			putType(i, types.getType(i));
	}

	public Object removeTypeValue(String key) {
		if (this.types.removeType(key) == null)
			return null;
		return this.values.remove(key);
	}

}
