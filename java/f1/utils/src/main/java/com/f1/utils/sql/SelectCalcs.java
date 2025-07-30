package com.f1.utils.sql;

import com.f1.utils.string.sqlnode.OnNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class SelectCalcs {

	final private String[] names;
	final private DerivedCellCalculator[] selectCalcs;
	final private DerivedCellCalculator join;
	final private DerivedCellCalculator joinNearest;
	final private DerivedCellCalculator where;
	final private DerivedCellCalculator[] groupByCalcs;
	final private DerivedCellCalculator having;
	final private DerivedCellCalculator[] orderByCalcs;
	private int joinType;
	private OnNode[] unpacks;
	private boolean[] isAscending;
	private int limit;
	private int limitOffset;

	public SelectCalcs(String[] names, DerivedCellCalculator[] selectCalcs, DerivedCellCalculator join, DerivedCellCalculator joinNearest, DerivedCellCalculator where,
			DerivedCellCalculator[] groupByCalcs, DerivedCellCalculator having, DerivedCellCalculator[] orderByCalcs, int joinType, OnNode[] unpacks, boolean[] isAscending,
			int limitOffset, int limit) {
		this.names = names;
		this.selectCalcs = selectCalcs;
		this.join = join;
		this.joinNearest = joinNearest;
		this.where = where;
		this.groupByCalcs = groupByCalcs;
		this.having = having;
		this.orderByCalcs = orderByCalcs;
		this.joinType = joinType;
		this.unpacks = unpacks;
		this.isAscending = isAscending;
		this.limit = limit;
		this.limitOffset = limitOffset;
	}

	public String[] getNames() {
		return names;
	}

	public DerivedCellCalculator[] getSelectCalcs() {
		return selectCalcs;
	}

	public DerivedCellCalculator getJoin() {
		return join;
	}

	public DerivedCellCalculator getJoinNearest() {
		return joinNearest;
	}

	public DerivedCellCalculator getWhere() {
		return where;
	}

	public DerivedCellCalculator[] getGroupByCalcs() {
		return groupByCalcs;
	}

	public DerivedCellCalculator getHaving() {
		return having;
	}

	public DerivedCellCalculator[] getOrderByCalcs() {
		return orderByCalcs;
	}

	public int getJoinType() {
		return joinType;
	}

	public OnNode[] getUnpacks() {
		return unpacks;
	}

	public boolean[] getIsAscending() {
		return isAscending;
	}

	public int getLimitOffset() {
		return limitOffset;
	}

	public int getLimit() {
		return limit;
	}

}
