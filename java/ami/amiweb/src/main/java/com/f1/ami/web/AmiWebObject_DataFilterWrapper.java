package com.f1.ami.web;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.f1.base.CalcFrame;
import com.f1.utils.OH;

class AmiWebObject_DataFilterWrapper implements AmiWebObject {

	private AmiWebObject inner;
	private AmiWebObjectFieldsImpl changes;

	public Object getParam(String param) {
		return inner.getParam(param);
	}

	public long getUniqueId() {
		return inner.getUniqueId();
	}

	public String getObjectId() {
		return inner.getObjectId();
	}

	public long getId() {
		return inner.getId();
	}

	public String getTypeName() {
		return inner.getTypeName();
	}

	public void fill(Map<String, Object> sink) {
		throw new UnsupportedOperationException();
	}

	public Set<Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	public Set<String> keySet() {
		return inner.keySet();
	}

	public StringBuilder toString(StringBuilder sink) {
		return inner.toString(sink);
	}

	public int size() {
		return inner.size();
	}

	public boolean isEmpty() {
		return inner.isEmpty();
	}

	public boolean containsKey(Object key) {
		return inner.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return inner.containsValue(value);
	}

	public Object get(Object key) {
		return inner.get(key);
	}

	public Object put(String key, Object value) {
		Object old = inner.put(key, value);
		if (OH.ne(value, old))
			this.changes.addChangeIfNotExists(key, old);
		return old;
	}

	public Object remove(Object key) {
		return inner.remove(key);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		for (final Map.Entry<? extends String, ? extends Object> entry : m.entrySet())
			this.put(entry.getKey(), entry.getValue());
	}

	public void clear() {
		inner.clear();
	}

	public Collection<Object> values() {
		return inner.values();
	}

	public boolean remove(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	public void reset(AmiWebObject inner, AmiWebObjectFields changes) {
		this.inner = inner;
		this.changes = (AmiWebObjectFieldsImpl) changes;
	}

	@Override
	public void fill(CalcFrame sink) {
		inner.fill(sink);
	}

	@Override
	public Object getValue(String key) {
		return get(key);
	}

	@Override
	public Object putValue(String key, Object value) {
		return put(key, value);
	}

	@Override
	public Class<?> getType(String key) {
		return inner.getType(key);
	}

	@Override
	public Iterable<String> getVarKeys() {
		return inner.getVarKeys();
	}

	@Override
	public int getVarsCount() {
		return inner.getVarsCount();
	}
	@Override
	public boolean isVarsEmpty() {
		return inner.isVarsEmpty();
	}

}