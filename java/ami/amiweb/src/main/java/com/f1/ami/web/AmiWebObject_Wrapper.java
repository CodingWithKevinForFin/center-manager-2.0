package com.f1.ami.web;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.f1.base.CalcFrame;

public class AmiWebObject_Wrapper implements AmiWebObject {

	final private long id;
	final private AmiWebObject inner;

	public AmiWebObject_Wrapper(AmiWebObject inner, long id) {

		this.inner = inner;
		this.id = id;
	}

	public Object getParam(String param) {
		return inner.getParam(param);
	}

	public long getUniqueId() {
		return id;
	}

	public String getObjectId() {
		return inner.getObjectId();
	}

	public long getId() {
		return id;
	}

	public String getTypeName() {
		return inner.getTypeName();
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
		throw new UnsupportedOperationException();
	}

	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public Set<String> keySet() {
		return inner.keySet();
	}

	public Collection<Object> values() {
		return inner.values();
	}

	public Set<Entry<String, Object>> entrySet() {
		return inner.entrySet();
	}

	public long getInnerUniqueId() {
		return inner.getUniqueId();
	}

	@Override
	public void fill(Map<String, Object> sink) {
		inner.fill(sink);
	}
	@Override
	public void fill(CalcFrame sink) {
		inner.fill(sink);
	}

	@Override
	public Object getValue(String key) {
		return inner.getValue(key);
	}

	@Override
	public Object putValue(String key, Object value) {
		throw new UnsupportedOperationException();
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
