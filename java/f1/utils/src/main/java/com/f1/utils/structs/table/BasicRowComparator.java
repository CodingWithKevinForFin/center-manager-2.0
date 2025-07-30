/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs.table;

import java.io.Serializable;
import java.util.Comparator;

import com.f1.base.Row;
import com.f1.utils.AH;
import com.f1.utils.OH;

public class BasicRowComparator implements Comparator<Row>, Serializable {

	final private int[] columns;
	final private boolean[] ascending;

	public BasicRowComparator(int[] columns, boolean[] ascending) {
		this.columns = columns;
		this.ascending = ascending;
	}

	public BasicRowComparator(BasicRowComparator rowComparator, int[] columnIds, boolean[] ascendings) {
		this.columns = AH.insertArray(rowComparator.getColumns(), rowComparator.getColumns().length, columnIds);
		this.ascending = AH.insertArray(rowComparator.getAscending(), rowComparator.getAscending().length, ascendings);
	}

	@Override
	public int compare(Row o1, Row o2) {
		for (int i = 0; i < columns.length; i++) {
			int r = OH.compare((Comparable<?>) o1.getAt(columns[i]), (Comparable<?>) o2.getAt(columns[i]));
			if (r != 0)
				return r > 0 == ascending[i] ? 1 : -1;
		}
		return 0;
	}

	public int[] getColumns() {
		return columns;
	}

	public boolean[] getAscending() {
		return ascending;
	}

}
