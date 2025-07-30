package com.f1.ami.center.table.index;

import java.util.Map.Entry;

import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryBetweenClause;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiQueryFinder_Between implements AmiPreparedQueryBetweenClause {

	private Comparable min;
	private Comparable max;
	final private boolean minInc;
	final private boolean maxInc;
	final private AmiColumnImpl column;
	final private AmiPreparedQuery query;
	private AmiQueryFinder next;
	private boolean isPossible;

	public AmiQueryFinder_Between(AmiPreparedQuery query, AmiColumnImpl column, boolean minInclusive, boolean maxInclusive) {
		this.query = query;
		this.column = column;
		this.minInc = minInclusive;
		this.maxInc = maxInclusive;
	}

	@Override
	public boolean getRows(AmiIndexMap_Hash map, AmiQueryFinderVisitor finderVisitor) {
		if (!isPossible)
			return true;
		if (minInc) {
			if (maxInc) {
				for (Entry<Comparable, AmiIndexMap> i : map.entrySet())
					if (OH.ge(i.getKey(), min, true) && OH.le(i.getKey(), max, true))
						if (!i.getValue().getRows(next, finderVisitor))
							return false;
			} else {
				for (Entry<Comparable, AmiIndexMap> i : map.entrySet())
					if (OH.ge(i.getKey(), min, true) && OH.lt(i.getKey(), max, true))
						if (!i.getValue().getRows(next, finderVisitor))
							return false;
			}
		} else {
			if (maxInc) {
				for (Entry<Comparable, AmiIndexMap> i : map.entrySet())
					if (OH.gt(i.getKey(), min, true) && OH.le(i.getKey(), max, true))
						if (!i.getValue().getRows(next, finderVisitor))
							return false;
			} else {
				for (Entry<Comparable, AmiIndexMap> i : map.entrySet())
					if (OH.gt(i.getKey(), min, true) && OH.lt(i.getKey(), max, true))
						if (!i.getValue().getRows(next, finderVisitor))
							return false;
			}
		}
		return true;

	}
	@Override
	public boolean getRows(AmiIndexMap_Tree map, AmiQueryFinderVisitor finderVisitor) {
		if (!isPossible)
			return true;
		for (AmiIndexMap i : map.subMap(min, minInc, max, maxInc).values())
			if (!i.getRows(next, finderVisitor))
				return false;
		return true;
	}
	@Override
	public boolean getRows(AmiIndexMap_Series map, AmiQueryFinderVisitor finderVisitor) {
		if (!isPossible)
			return true;
		int i = map.getIndexCeiling(((Number) min).longValue(), minInc);
		int hgh = map.getIndexFloor(((Number) max).longValue(), maxInc);
		if (i == -1 || hgh == -1)
			return true;
		while (i <= hgh)
			if (!map.getAt(i++).getRows(next, finderVisitor))
				return false;
		return true;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		sb.append(column.getName()).append(minInc ? " >= " : " > ");
		if (min instanceof String) {
			SH.quoteToJavaConst('\"', (String) min, sb);
		} else
			sb.append(min);
		sb.append(" && ").append(column.getName()).append(maxInc ? " <= " : " < ");
		if (max instanceof String) {
			SH.quoteToJavaConst('\"', (String) max, sb);
		} else
			sb.append(max);

		return sb;
	}

	@Override
	public boolean matches(Comparable o) {
		if (!isPossible)
			return true;
		if (minInc) {
			if (maxInc)
				return OH.ge(o, min, true) && OH.le(o, max, true);
			else
				return OH.ge(o, min, true) && OH.lt(o, max, true);
		} else {
			if (maxInc)
				return OH.gt(o, min, true) && OH.le(o, max, true);
			else
				return OH.gt(o, min, true) && OH.lt(o, max, true);
		}
	}

	@Override
	public void setMinMax(Comparable min, Comparable max) {
		this.min = (Comparable) column.getColumn().getTypeCaster().castNoThrow(min);
		this.max = (Comparable) column.getColumn().getTypeCaster().castNoThrow(max);
		this.isPossible = true;
		if (this.min == null && min != null) {
			this.isPossible = false;
			this.min = min;
		}
		if (this.max == null && max != null) {
			this.isPossible = false;
			this.max = max;
		}
		if (isPossible && OH.compare(this.min, this.max) > 0)
			isPossible = false;
	}

	@Override
	public Comparable getMin() {
		return this.min;
	}
	@Override
	public AmiColumnImpl getColumn() {
		return this.column;
	}

	@Override
	public Comparable getMax() {
		return max;
	}

	@Override
	public int getScore() {
		return isPossible ? SCORE_BETWEEN : 10;
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
