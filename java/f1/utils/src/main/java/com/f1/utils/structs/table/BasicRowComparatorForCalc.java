/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs.table;

import java.util.Comparator;

import com.f1.base.Row;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class BasicRowComparatorForCalc implements Comparator<Row> {

	final private DerivedCellCalculator[] columns;
	final private boolean[] ascending;
	final private ReusableCalcFrameStack sf;

	public BasicRowComparatorForCalc(DerivedCellCalculator[] columns, boolean[] ascending, ReusableCalcFrameStack sf) {
		this.columns = columns;
		this.ascending = ascending;
		this.sf = sf;
	}

	@Override
	public int compare(Row o1, Row o2) {
		for (int i = 0; i < columns.length; i++) {
			int r = OH.compare((Comparable<?>) columns[i].get(this.sf.reset(o1)), (Comparable<?>) columns[i].get(this.sf.reset(o2)));
			if (r != 0)
				return r > 0 == ascending[i] ? 1 : -1;
		}
		return 0;
	}

	public DerivedCellCalculator[] getColumns() {
		return columns;
	}

	public boolean[] getAscending() {
		return ascending;
	}

}
