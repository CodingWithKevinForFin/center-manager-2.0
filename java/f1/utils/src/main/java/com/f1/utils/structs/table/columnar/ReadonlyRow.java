package com.f1.utils.structs.table.columnar;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Row;
import com.f1.base.Table;

public class ReadonlyRow implements Row {

	final private Row inner;
	final private ReadonlyTable table;

	public ReadonlyRow(Row inner, ReadonlyTable table) {
		this.inner = inner;
		this.table = table;
	}

	public Table getTable() {
		return table;
	}

	public Object[] getValues() {
		return inner.getValues();
	}

	public Iterator<Object> iterator() {
		return inner.iterator();
	}

	public Object[] getValuesCloned() {
		return inner.getValuesCloned();
	}

	public void setValues(Object[] values) {
		throw readonly();
	}

	public Object getAt(int i) {
		return inner.getAt(i);
	}

	public <T> T get(Object key, Class<T> clazz) {
		return inner.get(key, clazz);
	}

	public <T> T get(Object key, Caster<T> caster) {
		return inner.get(key, caster);
	}

	public <T> T getAt(int i, Class<T> clazz) {
		return inner.getAt(i, clazz);
	}

	public <T> T getAt(int i, Caster<T> clazz) {
		return inner.getAt(i, clazz);
	}

	public Object putAt(int i, Object value) {
		throw readonly();
	}

	public int getLocation() {
		return inner.getLocation();
	}

	public int getUid() {
		return inner.getUid();
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
		throw readonly();
	}

	public Object remove(Object key) {
		throw readonly();
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		throw readonly();
	}

	public void clear() {
		inner.clear();
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

	public boolean equals(Object o) {
		return inner.equals(o);
	}

	public int hashCode() {
		return inner.hashCode();
	}

	public Object putIfAbsent(String key, Object value) {
		throw readonly();
	}

	public boolean remove(Object key, Object value) {
		throw readonly();
	}

	public boolean replace(String key, Object oldValue, Object newValue) {
		throw readonly();
	}

	public Object replace(String key, Object value) {
		throw readonly();
	}

	private RuntimeException readonly() {
		return new RuntimeException("Readonly Table: " + table.getTitle());
	}

	@Override
	public Object getValue(String key) {
		return inner.getValue(key);
	}

	@Override
	public Object putValue(String key, Object value) {
		throw readonly();
	}

	@Override
	public Object getValue(NameSpaceIdentifier id) {
		return inner.getValue(id);
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

	@Override
	public Class<?> getType(NameSpaceIdentifier key) {
		return inner.getType(key);
	}

	@Override
	public Class<?> getTypeAt(int n) {
		return inner.getTypeAt(n);
	}

	@Override
	public int getPosition(String key) {
		return inner.getPosition(key);
	}

	@Override
	public int getPosition(NameSpaceIdentifier key) {
		return inner.getPosition(key);
	}
}
