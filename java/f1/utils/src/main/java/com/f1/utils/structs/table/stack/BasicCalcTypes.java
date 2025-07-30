package com.f1.utils.structs.table.stack;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.CalcTypes;
import com.f1.base.ToStringable;

public class BasicCalcTypes implements CalcTypes, ToStringable {

	final private Map<String, Class<?>> types;

	public BasicCalcTypes() {
		types = new HashMap<String, Class<?>>();
	}
	public BasicCalcTypes(CalcTypes types) {
		this.types = new HashMap<String, Class<?>>();
		for (String s : types.getVarKeys())
			this.types.put(s, types.getType(s));
	}

	public BasicCalcTypes(int size) {
		types = new HashMap<String, Class<?>>(size);
	}
	@Override
	public Class<?> getType(String name) {
		return types.get(name);
	}

	@Override
	public boolean isVarsEmpty() {
		return types.isEmpty();
	}

	@Override
	public Iterable<String> getVarKeys() {
		return types.keySet();
	}

	public Class<?> putType(String name, Class<?> caster) {
		return this.types.put(name, caster);
	}

	public BasicCalcTypes putAll(CalcTypes types) {
		for (String s : types.getVarKeys())
			this.types.put(s, types.getType(s));
		return this;
	}
	public Class<?> getTypeCasterOrThrow(String field) {
		Class<?> r = this.types.get(field);
		if (r == null)
			throw new RuntimeException("Field not found: " + field);
		return r;
	}
	public void clear() {
		this.types.clear();
	}
	public Class<?> removeType(String name) {
		return this.types.remove(name);
	}
	@Override
	public int getVarsCount() {
		return this.types.size();
	}
	public BasicCalcTypes putAllIfAbsent(CalcTypes types) {
		for (String s : types.getVarKeys())
			if (!this.types.containsKey(s))
				this.types.put(s, types.getType(s));
		return this;
	}
	public BasicCalcTypes putIfAbsent(String name, Class<Map> c) {
		if (!this.types.containsKey(name))
			this.types.put(name, c);
		return this;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('{');
		boolean first = true;
		for (Entry<String, Class<?>> i : types.entrySet()) {
			if (first)
				first = false;
			else
				sink.append(',');
			sink.append(i.getKey()).append('=').append(i.getValue() == null ? "null" : i.getValue().getSimpleName());
		}
		sink.append('}');
		return sink;
	}
	public Map<String, Class<?>> getTypes() {
		return this.types;
	}
}
