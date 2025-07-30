package com.f1.ami.center.table.index;

import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiPreparedQuery;

public class AmiQueryFinder_All implements AmiQueryFinder {
	final private AmiColumnImpl column;
	private AmiQueryFinder next;
	final private AmiPreparedQuery preparedQuery;

	public AmiQueryFinder_All(AmiPreparedQuery query, AmiColumnImpl column) {
		this.column = column;
		this.preparedQuery = query;
	}

	@Override
	public boolean getRows(AmiIndexMap_Hash map, AmiQueryFinderVisitor finderVisitor) {
		for (AmiIndexMap i : map.values())
			if (!i.getRows(next, finderVisitor))
				return false;
		return true;
	}

	@Override
	public boolean getRows(AmiIndexMap_Tree map, AmiQueryFinderVisitor finderVisitor) {
		for (AmiIndexMap i : map.values())
			if (!i.getRows(next, finderVisitor))
				return false;
		return true;
	}
	@Override
	public boolean getRows(AmiIndexMap_Series map, AmiQueryFinderVisitor finderVisitor) {
		for (int i = 0, n = map.size(); i < n; i++) {
			if (!map.getAt(i).getRows(next, finderVisitor))
				return false;
		}
		return true;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return sb.append("true");
	}

	@Override
	public AmiColumnImpl getColumn() {
		return this.column;
	}

	@Override
	public boolean matches(Comparable o) {
		return true;
	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public AmiQueryFinder getNext() {
		return next;
	}

	@Override
	public void setNext(AmiQueryFinder next) {
		this.next = next;
	}

	@Override
	public AmiPreparedQuery getAmiPreparedQuery() {
		return this.preparedQuery;
	}

}
