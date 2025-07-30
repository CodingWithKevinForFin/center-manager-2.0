package com.f1.utils.structs.table.columnar;

import java.util.Iterator;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Table;

public class ReadonlyColumn implements Column {

	private final Column inner;
	private final ReadonlyTable table;

	public ReadonlyColumn(Column inner, ReadonlyTable table) {
		this.inner = inner;
		this.table = table;
	}

	public int size() {
		return inner.size();
	}

	public Class<?> getType() {
		return inner.getType();
	}

	public Caster<?> getTypeCaster() {
		return inner.getTypeCaster();
	}

	public Table getTable() {
		return table;
	}

	public int getLocation() {
		return inner.getLocation();
	}

	public String getId() {
		return inner.getId();
	}

	public Object getValue(int location) {
		return inner.getValue(location);
	}

	public Iterator iterator() {
		return inner.iterator();
	}

	@Override
	public void setValue(int location, Object value) {
		throw new UnsupportedOperationException();
	}

}
