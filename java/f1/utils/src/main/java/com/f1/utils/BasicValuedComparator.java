/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Comparator;
import com.f1.base.Valued;

public class BasicValuedComparator implements Comparator<Valued> {

	final private String[] columns;
	final private boolean[] ascending;

	public BasicValuedComparator(String[] columns, boolean[] ascending) {
		OH.assertEq(columns.length, ascending.length);
		this.columns = columns;
		this.ascending = ascending;
	}

	public BasicValuedComparator(BasicValuedComparator rowComparator, String[] columnIds, boolean[] ascendings) {
		this.columns = AH.insertArray(rowComparator.getColumns(), rowComparator.getColumns().length, columnIds);
		this.ascending = AH.insertArray(rowComparator.getAscending(), rowComparator.getAscending().length, ascendings);
	}

	@Override
	public int compare(Valued o1, Valued o2) {
		for (int i = 0; i < columns.length; i++) {
			int r = OH.compare((Comparable<?>) o1.ask(columns[i]), (Comparable<?>) o2.ask(columns[i]));
			if (r != 0)
				return r > 0 == ascending[i] ? 1 : -1;
		}
		return 0;
	}

	public String[] getColumns() {
		return columns;
	}

	public boolean[] getAscending() {
		return ascending;
	}

	public static BasicValuedComparator createAssending(String... columns) {
		return new BasicValuedComparator(columns, AH.fill(new boolean[columns.length], true));
	}

	public static BasicValuedComparator createDescending(String... columns) {
		return new BasicValuedComparator(columns, AH.fill(new boolean[columns.length], false));
	}
}
