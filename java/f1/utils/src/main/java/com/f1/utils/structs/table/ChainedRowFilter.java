/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs.table;

import java.util.List;

import com.f1.base.Row;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LocalToolkit;
import com.f1.utils.SH;

public class ChainedRowFilter implements RowFilter {

	final public boolean isOr;
	final private RowFilter[] children;

	public ChainedRowFilter(boolean isOr, RowFilter... children) {
		this.isOr = isOr;
		this.children = children;
	}

	public ChainedRowFilter(boolean isOr, List<RowFilter> filters) {
		this.isOr = isOr;
		this.children = filters.toArray(new RowFilter[filters.size()]);
	}

	@Override
	public boolean shouldKeep(Row row, LocalToolkit tk) {
		for (int i = 0; i < children.length; i++)
			if (children[i].shouldKeep(row, tk) == isOr)
				return isOr;
		return !isOr;
	}

	public static <T extends Row> RowFilter or(RowFilter... filters) {
		return or(CH.l(filters));
	}
	public static <T extends Row> RowFilter and(RowFilter... filters) {
		return and(CH.l(filters));
	}
	public static <T extends Row> RowFilter or(List<RowFilter> filters) {
		return create(true, filters);
	}
	public static <T extends Row> RowFilter and(List<RowFilter> filters) {
		return create(false, filters);
	}

	private static <T extends Row> RowFilter create(boolean isOr, List<RowFilter> filters) {
		if (filters.size() == 0)
			throw new RuntimeException("must have at least on filter!");
		return filters.size() == 1 ? filters.get(0) : new ChainedRowFilter(isOr, filters);
	}

	public String toString() {
		return "Chained Filter: " + SH.join(" ==> ", (Object[]) children);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != ChainedRowFilter.class)
			return false;
		ChainedRowFilter other = (ChainedRowFilter) obj;
		return other.isOr == isOr && AH.eq(children, other.children);
	}
}
