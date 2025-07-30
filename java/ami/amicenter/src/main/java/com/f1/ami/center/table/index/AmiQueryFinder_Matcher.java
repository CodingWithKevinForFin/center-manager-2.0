package com.f1.ami.center.table.index;

import java.util.Map.Entry;

import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryMatcherClause;
import com.f1.utils.Matcher;
import com.f1.utils.SH;

public class AmiQueryFinder_Matcher implements AmiPreparedQueryMatcherClause {

	private Matcher<Comparable<?>> matcher;
	final private AmiColumnImpl column;
	final private AmiPreparedQuery query;
	private AmiQueryFinder next;

	public AmiQueryFinder_Matcher(AmiPreparedQuery query, AmiColumnImpl column) {
		this.column = column;
		this.query = query;
	}

	@Override
	public boolean getRows(AmiIndexMap_Hash map, AmiQueryFinderVisitor finderVisitor) {
		for (Entry<Comparable, AmiIndexMap> entry : map.entrySet())
			if (matches(entry.getKey()))
				if (!entry.getValue().getRows(next, finderVisitor))
					return false;
		return true;
	}
	@Override
	public boolean getRows(AmiIndexMap_Tree map, AmiQueryFinderVisitor finderVisitor) {
		for (Entry<Comparable, AmiIndexMap> entry : map.entrySet())
			if (matches(entry.getKey()))
				if (!entry.getValue().getRows(next, finderVisitor))
					return false;
		return true;
	}

	@Override
	public boolean getRows(AmiIndexMap_Series map, AmiQueryFinderVisitor finderVisitor) {
		for (int i = 0, s = map.size(); i < s; i++) {
			long key = map.getKeyAt(i);
			if (matches(key))
				if (!map.getAt(i).getRows(next, finderVisitor))
					return false;
		}
		return true;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return sb.append(column.getName()).append(" matches (").append(this.matcher).append(")");
	}
	@Override
	public AmiColumnImpl getColumn() {
		return this.column;
	}

	@Override
	public boolean matches(Comparable<?> o) {
		return matcher.matches(SH.toString(o));
	}

	@Override
	public int getScore() {
		return SCORE_IN;
	}

	@Override
	public AmiPreparedQuery getAmiPreparedQuery() {
		return this.query;
	}

	@Override
	public void setMatcher(Matcher<? extends Comparable<?>> value) {
		this.matcher = (Matcher<Comparable<?>>) value;
	}

	@Override
	public Matcher<? extends Comparable<?>> getMatcher() {
		return this.matcher;
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
