/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.utils.structs.ListWrapper;
import com.f1.utils.structs.SkipList;

public class BasicTableList extends ListWrapper<Row> implements TableList {

	final private BasicTable table;

	public BasicTableList(BasicTable table, int size) {
		super((List) new SkipList<BasicRow>(size));
		this.table = table;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public boolean add(Row e) {
		if (e.getTable() != table)
			throw new RuntimeException("not a member of this table");
		boolean r = super.add(e);
		table.fireRowAdd(e);
		return r;
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Row) {
			Row r = (Row) o;
			if (r.getTable() != table)
				return false;
			remove(r.getLocation());
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Row> c) {
		for (Row row : c) {
			if (row.getTable() != table)
				throw new RuntimeException("not a member of this table");
		}
		if (!super.addAll(c))
			return false;

		if (table.hasListeners)
			for (Row row : c) {
				table.fireRowAdd(row);
			}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Row> c) {
		for (Row row : c)
			if (row.getTable() != table)
				throw new RuntimeException("not a member of this table");

		if (!super.addAll(index, c))
			return false;
		if (table.hasListeners)
			for (Row r : c) {
				table.fireRowAdd(r);
			}
		return true;
	}

	public void addAll(Iterable<? extends Row> c) {
		for (Row row : c)
			if (row.getTable() != table)
				throw new RuntimeException("not a member of this table");

		for (Row c2 : c)
			super.add(c2);
		if (table.hasListeners)
			for (Row r : c) {
				table.fireRowAdd(r);
			}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean r = false;
		for (Object i : c)
			r = remove(i) || r;
		return r;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		if (table.hasListeners && size() > 0) {
			List<Row> rows = new ArrayList<Row>(this);
			super.clear();
			for (int i = 0, l = rows.size(); i < l; i++)
				table.fireRowRemove(rows.get(i), i);
		} else
			super.clear();
	}

	@Override
	public Row set(int index, Row element) {
		Row r = super.set(index, element);
		if (r != element && table.hasListeners) {
			table.fireRowRemove(r, index);
			table.fireRowAdd(element);
		}
		return r;
	}

	@Override
	public void add(int index, Row element) {
		super.add(index, element);
		if (table.hasListeners)
			table.fireRowAdd(element);
	}

	@Override
	public Row remove(int index) {
		Row r = super.remove(index);
		table.fireRowRemove(r, index);
		return r;
	}

	@Override
	public Row addRow(Object... values) {
		Row r = table.newRow(values);
		add(r);
		return r;
	}

	@Override
	public Row insertRow(int i, Object... values) {
		Row r = table.newRow(values);
		add(i, r);
		return r;

	}
	@Override
	public Row[] toRowsArray() {
		return toArray(new Row[this.size()]);
	}

	@Override
	public long getLongSize() {
		return size();
	}
}
