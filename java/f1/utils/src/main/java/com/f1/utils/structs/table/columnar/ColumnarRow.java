package com.f1.utils.structs.table.columnar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.ToStringable;
import com.f1.utils.OH;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.structs.MapEntry;
import com.f1.utils.structs.SkipListEntry;

public class ColumnarRow extends SkipListEntry implements Row, ToStringable {

	private ColumnarTable table;
	final private int uid;
	private int arrayIndex;

	protected ColumnarRow(ColumnarTable table, int uid, int arrayIndex) {
		this.table = table;
		this.uid = uid;
		this.arrayIndex = arrayIndex;
	}

	@Override
	public void clear() {
		for (int i = 0, len = table.getColumnsCount(); i < len; i++)
			table.getColumnAt(i).setValueAtArrayIndex(this.arrayIndex, null);
	}

	@Override
	public boolean containsKey(Object columnName) {
		return table.getColumnIds().contains(columnName);
	}

	@Override
	public boolean containsValue(Object value) {
		for (int i = 0, len = table.getColumnsCount(); i < len; i++)
			if (OH.eq(value, table.getColumnAt(i).getValueAtArrayIndex(arrayIndex)))
				return true;
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> r = new LinkedHasherSet<Map.Entry<String, Object>>();
		for (int i = 0, len = table.getColumnsCount(); i < len; i++)
			r.add(new MapEntry<String, Object>(table.getColumnAt(i).getId(), getAt(i)));
		return r;
	}

	@Override
	public Object get(Object key) {
		return table.getColumn((String) key).getValueAtArrayIndex(arrayIndex);
	}

	@Override
	public boolean isEmpty() {
		return table.getColumnsCount() == 0;
	}

	@Override
	public Set<String> keySet() {
		return table.getColumnIds();
	}

	@Override
	public Object put(String key, Object value) {
		return table.getColumn(key).setValueAtArrayIndex(this.arrayIndex, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		for (java.util.Map.Entry<? extends String, ? extends Object> e : map.entrySet())
			put(e.getKey(), e.getValue());
	}

	@Override
	public Object remove(Object key) {
		if (containsKey(key))
			return table.set(getLocation(), (String) key, null);
		else
			return null;

	}

	@Override
	public int size() {
		return this.table.getColumnsCount();
	}

	@Override
	public Collection<Object> values() {
		int size = this.table.getColumnsCount();
		ArrayList r = new ArrayList(size);
		for (int i = 0; i < size; i++)
			r.add(table.getColumnAt(i).getValueAtArrayIndex(this.arrayIndex));
		return r;
	}

	@Override
	public Iterator<Object> iterator() {
		return values().iterator();
	}

	@Override
	public Table getTable() {
		return this.table;
	}

	@Override
	public Object[] getValues() {
		throw new UnsupportedOperationException("Use getValuesCloned()");
	}

	@Override
	public Object[] getValuesCloned() {
		Object[] r = new Object[this.table.getColumnsCount()];
		for (int i = 0; i < r.length; i++)
			r[i] = table.getColumnAt(i).getValueAtArrayIndex(this.arrayIndex);
		return r;
	}

	@Override
	public void setValues(Object[] values) {
		for (int i = 0; i < values.length; i++)
			table.getColumnAt(i).setValueAtArrayIndex(this.arrayIndex, values[i]);
	}

	@Override
	public Object getAt(int i) {
		return table.columns[i].getValueAtArrayIndex(this.arrayIndex);
	}

	@Override
	public <T> T get(Object key, Class<T> clazz) {
		return OH.cast(get(key), clazz);
	}

	@Override
	public <T> T getAt(int i, Class<T> clazz) {
		return OH.cast(getAt(i), clazz);
	}

	@Override
	public <T> T get(Object key, Caster<T> caster) {
		return caster.cast(get(key));
	}
	@Override
	public <T> T getAt(int i, Caster<T> caster) {
		return caster.cast(getAt(i));
	}

	@Override
	public Object putAt(int i, Object value) {
		this.table.fireCellChanged(this, i, null, value);
		return table.getColumnAt(i).setValueAtArrayIndex(this.arrayIndex, value);
	}

	@Override
	public int getUid() {
		return this.uid;
	}

	public int getArrayIndex() {
		return arrayIndex;
	}
	public void setArrayIndex(int arrayIndex) {
		this.arrayIndex = arrayIndex;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (this.getArrayIndex() == -1)
			return sink.append("[not participating in table]");
		sink.append("[");
		List<Column> cols = table.getColumns();
		for (int i = 0; i < cols.size(); i++) {
			if (i > 0)
				sink.append(", ");
			ColumnarColumn col = (ColumnarColumn) cols.get(i);
			sink.append(col.getId()).append("=");
			col.toString(this, sink);
		}
		return sink.append("]");
	}

	@Override
	public Object getValue(String key) {
		return table.getColumn((String) key).getValueAtArrayIndex(arrayIndex);
	}

	@Override
	public Object putValue(String key, Object value) {
		return table.getColumn(key).setValueAtArrayIndex(this.arrayIndex, value);
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
		return this.table.getColumnsCount();
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
