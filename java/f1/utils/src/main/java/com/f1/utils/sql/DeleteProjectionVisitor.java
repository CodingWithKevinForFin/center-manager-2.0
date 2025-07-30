package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.IntArrayList;
import com.f1.utils.structs.IntSet;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.TablesCalcFrame;

public class DeleteProjectionVisitor implements SqlProjectionVisitor {

	IntArrayList toDelete = null;
	IntSet intSet = null;

	@Override
	public void visit(TablesCalcFrame tg, int firstTablePos, ReusableCalcFrameStack sf) {
		if (toDelete == null) {
			toDelete = new IntArrayList();
			intSet = new IntSet();
		}
		Row src = tg.getRows()[firstTablePos];
		int location = src.getLocation();
		if (intSet.add(location))
			toDelete.add(location);
	}

	public List<Row> getRows(Table table) {
		if (toDelete == null)
			return Collections.EMPTY_LIST;
		int size = toDelete.size();
		ArrayList<Row> r = new ArrayList<Row>(size);
		for (int i = 0; i < size; i++)
			r.add(table.getRows().get(toDelete.getInt(size - 1 - i)));
		return r;
	}

	@Override
	public void trimTable(int limitOffset, int limit) {
		throw new UnsupportedOperationException("Shouldn't hit this, because ORDER BY and UNPACK not supported in UPDATE clause");
	}

	public int getDeleteCount() {
		return toDelete == null ? 0 : toDelete.size();
	}
}
