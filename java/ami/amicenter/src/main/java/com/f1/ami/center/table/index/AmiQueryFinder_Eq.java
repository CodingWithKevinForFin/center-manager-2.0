package com.f1.ami.center.table.index;

import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiQueryFinder_Eq implements AmiQueryFinder, AmiPreparedQueryCompareClause {

	private Comparable value;
	final private AmiColumnImpl column;
	final private AmiPreparedQuery query;

	private AmiQueryFinder next;
	private boolean isPossible;

	public AmiQueryFinder_Eq(AmiPreparedQuery query, AmiColumnImpl column) {
		this.query = query;
		this.column = column;
	}

	@Override
	public boolean getRows(AmiIndexMap_Hash map, AmiQueryFinderVisitor finderVisitor) {
		if (!isPossible)
			return true;
		AmiIndexMap m = map.getIndex(value);
		return m == null || m.getRows(next, finderVisitor);
	}

	@Override
	public boolean getRows(AmiIndexMap_Tree map, AmiQueryFinderVisitor finderVisitor) {
		if (!isPossible)
			return true;
		AmiIndexMap m = map.get(value);
		return m == null || m.getRows(next, finderVisitor);
	}
	@Override
	public boolean getRows(AmiIndexMap_Series map, AmiQueryFinderVisitor finderVisitor) {
		if (!isPossible)
			return true;
		long lvalue = ((Number) value).longValue();
		AmiIndexMap m = map.getIndex(lvalue);
		return m == null || m.getRows(next, finderVisitor);
	}
	@Override
	public Comparable getValue() {
		return value;
	}

	@Override
	public void setValue(Comparable value) {
		this.value = (Comparable) column.getColumn().getTypeCaster().castNoThrow(value);
		if (this.value == null && value != null) {
			this.isPossible = false;
			this.value = value;
		} else
			this.isPossible = true;
	}

	@Override
	public byte getCompareType() {
		return EQ;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		sb.append(column.getName()).append(" ").append("==").append(" ");
		if (value instanceof String) {
			SH.quoteToJavaConst('\"', (String) value, sb);
		} else
			sb.append(value);
		return sb;

	}

	@Override
	public boolean matches(Comparable value) {
		return isPossible && OH.eq(value, this.value);
	}

	@Override
	public AmiColumnImpl getColumn() {
		return this.column;
	}

	@Override
	public int getScore() {
		return SCORE_EQ;
	}
	@Override
	public AmiPreparedQuery getAmiPreparedQuery() {
		return this.query;
	}
	@Override
	public AmiQueryFinder getNext() {
		return next;
	}

	@Override
	public void setNext(AmiQueryFinder next) {
		this.next = next;
	}

}
