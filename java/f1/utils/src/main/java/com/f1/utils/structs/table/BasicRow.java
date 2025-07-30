/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs.table;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.structs.ArrayIterator;
import com.f1.utils.structs.MapEntry;
import com.f1.utils.structs.SkipListEntry;

public class BasicRow extends SkipListEntry implements Row {

	@Override
	public String toString() {
		return Arrays.toString(values);
	}

	private BasicTable table;
	protected Object[] values;
	final private int uid;

	public BasicRow(BasicTable table, int uid, Object[] values) {
		this.uid = uid;
		this.table = table;
		this.values = values;
	}

	public BasicRow(BasicTable table, int uid, Row row) {
		this.uid = uid;
		this.table = table;
		Object[] values = row.getValues();
		this.values = Arrays.copyOf(values, values.length);
	}

	@Override
	public int size() {
		return values.length;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return table.getColumnIds().contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null) {
			for (Object v : values)
				if (v == null)
					return true;
		} else {
			for (Object v : values)
				if (v.equals(value))
					return true;
		}
		return false;
	}

	@Override
	public Object get(Object key) {
		return getAt(table.getColumn((String) key).getLocation());
	}

	@Override
	public Object put(String key, Object value) {
		int location = table.getColumn(key).getLocation();
		return putAt(location, value);
	}
	public Object putNoFire(String key, Object value) {
		int location = table.getColumn(key).getLocation();
		return putAtNoFire(location, value);
	}

	@Override
	public Object putAt(int i, Object value) {
		Object old = values[i];
		if (OH.eq(old, value)) {
			values[i] = value;
			return old;
		}
		assertValue(i, value);
		values[i] = value;// CHANGE
		if (table.hasListeners)
			table.fireCellChanged(this, i, old, value);
		return old;
	}
	public Object putAtNoFire(int i, Object value) {
		Object old = values[i];
		if (OH.eq(old, value)) {
			values[i] = value;
			return old;
		}
		assertValue(i, value);
		values[i] = value;// CHANGE
		return old;
	}

	@Override
	public Object remove(Object key) {
		return put((String) key, null);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		for (Map.Entry<? extends String, ?> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}
	public void putAllNoFire(Map<String, ? extends Object> m) {
		for (Map.Entry<String, ?> e : m.entrySet())
			putNoFire(e.getKey(), e.getValue());
	}

	@Override
	public void clear() {
		for (int i = 0; i < values.length; i++)
			putAt(i, null);
	}

	@Override
	public Set<String> keySet() {
		return this.table.getColumnIds();
	}

	@Override
	public Collection<Object> values() {
		return CH.l(values);
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> r = new LinkedHasherSet<Map.Entry<String, Object>>();
		for (int i = 0; i < values.length; i++)
			r.add(new MapEntry<String, Object>(table.getColumnAt(i).getId(), values[i]));

		return r;
	}

	@Override
	public Table getTable() {
		return table;
	}

	@Override
	public Object[] getValues() {
		return values;
	}
	@Override
	public Object[] getValuesCloned() {
		return values.clone();
	}

	@Override
	public Object getAt(int i) {
		return values[i];
	}
	@Override
	public <T> T get(Object key, Caster<T> caster) {
		return caster.cast(get(key));
	}
	@Override
	public <T> T getAt(int i, Caster<T> caster) {
		return caster.cast(getAt(i));
	}

	private void assertValue(int i, Object value) {
		if (value != null && !table.getColumnAt(i).getType().isAssignableFrom(value.getClass()))
			throw new RuntimeException("invalid type for column '" + table.getColumnAt(i).getId() + "' of type " + table.getColumnAt(i).getType() + ": "
					+ value.getClass().getName() + ", value=" + value);
	}

	@Override
	public Iterator<Object> iterator() {
		return new ArrayIterator<Object>(values);
	}

	@Override
	public <T> T getAt(int i, Class<T> clazz) {
		return OH.cast(getAt(i), clazz);
	}

	@Override
	public <T> T get(Object id, Class<T> clazz) {
		return OH.cast(get(id), clazz);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(values);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicRow other = (BasicRow) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	@Override
	public int getUid() {
		return uid;
	}

	public void setValues(Object[] values) {
		this.values = values;
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
	public Object getValue(NameSpaceIdentifier id) {
		if (OH.eq(table.getTitle(), id.getNamespace()))
			return getValue(id.getVarName());
		throw new RuntimeException("Unknown Namespace Identifier: " + id);
	}

	@Override
	public Class<?> getType(String key) {
		return table.getColumnTypesMapping().getType(key);
	}

	@Override
	public Iterable<String> getVarKeys() {
		return table.getColumnTypesMapping().getVarKeys();
	}

	@Override
	public int getVarsCount() {
		return values.length;
	}
	@Override
	public boolean isVarsEmpty() {
		return isEmpty();
	}

	@Override
	public Class<?> getType(NameSpaceIdentifier key) {
		return table.getColumnTypesMapping().getType(key);
	}

	@Override
	public Class<?> getTypeAt(int n) {
		return table.getColumnTypesMapping().getTypeAt(n);
	}

	@Override
	public int getPosition(String key) {
		return table.getColumnTypesMapping().getPosition(key);
	}

	@Override
	public int getPosition(NameSpaceIdentifier key) {
		return table.getColumnTypesMapping().getPosition(key);
	}

}
