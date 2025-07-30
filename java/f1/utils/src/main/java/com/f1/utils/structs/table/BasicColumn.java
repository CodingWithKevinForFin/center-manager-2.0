/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs.table;

import java.util.Iterator;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.utils.OH;

public class BasicColumn implements Column {

	final private Table table;
	private int location;
	final private Caster type;
	final private String id;
	final private int uid;

	public BasicColumn(Class type, String id) {
		this(null, -1, -1, type, id);
	}

	public BasicColumn(Table table, int uid, int location, Caster type, String id) {
		this.table = table;
		this.location = location;
		this.type = type;
		if (id == null)
			throw new NullPointerException("id");
		this.id = id;
		this.uid = uid;
	}
	public BasicColumn(Table table, int uid, int location, Class type, String id) {
		this.table = table;
		this.location = location;
		if (type.isPrimitive())
			this.type = OH.getCaster(OH.getBoxed(type));
		else
			this.type = OH.getCaster(type);
		if (id == null)
			throw new NullPointerException("id");
		this.id = id;
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "BasicColumn [id=" + id + ", location=" + location + ", type=" + type + "]";
	}

	@Override
	public Class getType() {
		return type.getCastToClass();
	}

	@Override
	public Table getTable() {
		return table;
	}

	@Override
	public int getLocation() {
		return location;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Object getValue(int row) {
		return table.getAt(row, location);
	}

	@Override
	public Iterator iterator() {
		return new ColumnIterator(this);
	}

	public int getUid() {
		return uid;
	}

	public static class ColumnIterator implements Iterator<Object> {

		private int cursor = 0;
		final private int columnLoc;
		final private Table table;

		public ColumnIterator(Column col) {
			this.table = col.getTable();
			columnLoc = col.getLocation();
		}

		@Override
		public boolean hasNext() {
			return cursor < this.table.getSize();
		}

		@Override
		public Object next() {
			return this.table.getAt(cursor++, columnLoc);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	@Override
	public boolean equals(Object o) {
		return o == this || (o != null && o.getClass() == getClass() && equals((BasicColumn) o));
	}
	public boolean equals(BasicColumn o) {
		return o == this || (o != null && location == o.location && OH.eq(type, o.type) && OH.eq(id, o.id));
	}

	@Override
	public int hashCode() {
		return OH.hashCode(this.id, this.type.getName(), this.location);
	}

	public void setLocation(int i) {
		this.location = i;
	}

	@Override
	public int size() {
		return getTable().getSize();
	}

	@Override
	public Caster<?> getTypeCaster() {
		return this.type;
	}

	@Override
	public void setValue(int row, Object value) {
		table.setAt(row, location, value);

	}

}
