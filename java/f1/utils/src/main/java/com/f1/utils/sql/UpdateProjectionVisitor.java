package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f1.base.Caster;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.IntArrayList;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.TablesCalcFrame;

public class UpdateProjectionVisitor implements SqlProjectionVisitor {

	private static final Object[][] EMPTY = new Object[0][0];

	IntArrayList toUpdate = null;
	List<Object[]> values = null;
	final private DerivedCellCalculator[] sourceExpressions;
	final private Caster[] casters;

	public UpdateProjectionVisitor(DerivedCellCalculator[] sourceExpressions, Caster[] casters) {
		this.sourceExpressions = sourceExpressions;
		this.casters = casters;
	}

	@Override
	public void visit(TablesCalcFrame tg, int firstTablePos, ReusableCalcFrameStack sf) {
		if (toUpdate == null) {
			toUpdate = new IntArrayList();
			values = new ArrayList<Object[]>();
		}
		Row src = tg.getRows()[firstTablePos];
		Object[] tmp = new Object[this.sourceExpressions.length];
		for (int i = 0; i < this.sourceExpressions.length; i++) {
			tmp[i] = casters[i].cast(this.sourceExpressions[i].get(sf.reset(tg)));
		}
		toUpdate.add(src.getLocation());
		values.add(tmp);
	}

	public Object[][] getValues() {
		return values == null ? EMPTY : AH.toArray(values, Object[].class);
	}
	public List<Row> getRows(Table table) {
		if (values == null)
			return Collections.EMPTY_LIST;
		int size = toUpdate.size();
		ArrayList<Row> r = new ArrayList<Row>(size);
		for (int i = 0; i < size; i++)
			r.add(table.getRows().get(toUpdate.getInt(i)));
		return r;
	}

	@Override
	public void trimTable(int limitOffset, int limit) {
		throw new UnsupportedOperationException("Shouldn't hit this, because ORDER BY and UNPACK not supported in UPDATE clause");
	}
}
