package com.f1.ami.web;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Row;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;

public class AmiWebObject_AggregateWrapper implements AmiWebObject, CalcTypes {

	private long id;
	private Row row;
	private OneToOne<String, Integer> positions;

	public AmiWebObject_AggregateWrapper(long id, OneToOne<String, Integer> downstreamPosition) {
		this.id = id;
		this.positions = downstreamPosition;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(row.toString());
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return positions.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		HashMap<String, Object> r = new HashMap<String, Object>();
		for (Entry<String, Integer> i : positions.getEntries())
			r.put(i.getKey(), this.row.getAt(i.getValue().intValue()));
		return r.entrySet();
	}

	@Override
	public Object get(Object key) {
		Integer pos = positions.getValue((String) key);
		return pos == null ? null : this.row.getAt(pos.intValue());
	}

	public String getName(int position) {
		return this.positions.getKey(position);
	}

	@Override
	public boolean isEmpty() {
		return this.row.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return Collections.unmodifiableSet(positions.getKeys());
	}

	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return this.row.size();
	}
	@Override
	public int getVarsCount() {
		return this.row.size();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getParam(String param) {
		return get(param);
	}

	@Override
	public long getUniqueId() {
		return id;
	}

	@Override
	public String getObjectId() {
		return null;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getTypeName() {
		return null;
	}

	public void setRow(Row row) {
		this.row = row;
	}

	@Override
	public void fill(Map<String, Object> sink) {
		for (final Object key : this.row.keySet()) {
			String derivedKey = (String) key;
			if (derivedKey.startsWith("@")) {
				int position = SH.parseInt(SH.afterFirst(derivedKey, "@")) + 1;
				derivedKey = this.positions.getKey(position);
			}
			sink.put(derivedKey, row.get(key));
		}
	}
	@Override
	public void fill(CalcFrame sink) {
		for (final Object key : this.row.keySet()) {
			String derivedKey = (String) key;
			if (derivedKey.startsWith("@")) {
				int position = SH.parseInt(SH.afterFirst(derivedKey, "@")) + 1;
				derivedKey = this.positions.getKey(position);
			}
			sink.putValue(derivedKey, row.get(key));
		}
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
		Integer pos = positions.getValue((String) key);
		return pos == null ? null : this.row.getTable().getColumnAt(pos).getType();
	}

	@Override
	public Iterable<String> getVarKeys() {
		return this.positions.getKeys();
	}

	@Override
	public boolean isVarsEmpty() {
		return false;
	}
}
