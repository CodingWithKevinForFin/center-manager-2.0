package com.f1.utils.structs.table.stack;

import java.util.Iterator;
import java.util.Map;

import com.f1.base.CalcTypes;
import com.f1.utils.OH;
import com.f1.utils.SingletonIterator;

public class SingletonCalcTypes implements Map.Entry<String, Class<?>>, CalcTypes, Iterable<String> {

	final private String key;
	final private Class<?> type;

	public SingletonCalcTypes(String key, Class<?> type) {
		this.type = type;
		this.key = key;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public boolean isVarsEmpty() {
		return false;
	}

	@Override
	public Iterable<String> getVarKeys() {
		return this;
	}

	@Override
	public Iterator<String> iterator() {
		return new SingletonIterator<String>(key);
	}

	@Override
	public Class<?> getType(String key) {
		if (this.key.equals(key))
			return type;
		return null;
	}

	@Override
	public int getVarsCount() {
		return 1;
	}
	@Override
	public Class<?> getValue() {
		return this.type;
	}
	@Override
	public Class<?> setValue(Class<?> value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != SingletonCalcTypes.class)
			return false;
		final SingletonCalcTypes other = (SingletonCalcTypes) obj;
		return OH.eq(key, other.key) && OH.eq(type, other.type);
	}

	@Override
	public String toString() {
		return "{" + key + "=" + (type == null ? "null" : type.getSimpleName()) + "}";
	}

}
