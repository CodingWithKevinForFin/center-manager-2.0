package com.f1.utils.structs.table.columnar;

import java.util.Collection;
import java.util.List;

import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.utils.structs.ListWrapper;
import com.f1.utils.structs.SkipList;
import com.f1.utils.structs.table.BasicRow;

public class ColumnarTableList extends ListWrapper<Row> implements TableList {

	private ColumnarTable table;

	public ColumnarTableList(ColumnarTable table, int size) {
		super((List) new SkipList<BasicRow>(size));
		this.table = table;
	}

	@Override
	public ColumnarRow addRow(Object... values) {
		ColumnarRow r = table.newEmptyRow();
		add(r);
		r.setValues(values);
		this.table.fireRowAdd(r);
		return r;
	}

	@Override
	public Row insertRow(int i, Object... values) {
		Row r = table.newEmptyRow();
		add(i, r);
		r.setValues(values);
		this.table.fireRowAdd(r);
		return r;
	}

	@Override
	public Row remove(int index) {
		table.onRowRemoved(get(index), index);
		Row row = super.remove(index);
		return row;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean r = false;
		for (Object i : c)
			r = remove(i) || r;
		return r;
	}
	@Override
	public boolean remove(Object o) {
		int location = ((Row) o).getLocation();
		this.table.onRowRemoved((Row) o, location);
		super.remove(location);
		return true;
	}

	@Override
	public Row set(int index, Row element) {
		Row r = super.set(index, element);
		if (r != element)
			table.isMangled = true;
		return r;
		//		return table.onRowRemoved(super.set(index, element), index);
	}

	@Override
	public void addAll(Iterable<? extends Row> values) {
		for (Row row : values)
			this.add(row);

	}

	@Override
	public boolean add(Row e) {
		//TODO: we need to validate nonulls
		if (e.getLocation() != -1)
			throw new IllegalStateException("Already in table: " + e);

		this.table.fireRowAdd(e);
		super.add(e);
		if (!this.table.isMangled && (((ColumnarRow) e).getArrayIndex() != e.getLocation()))
			this.table.isMangled = true;
		return true;
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
