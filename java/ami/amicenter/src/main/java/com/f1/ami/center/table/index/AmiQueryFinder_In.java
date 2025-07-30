package com.f1.ami.center.table.index;

import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryInClause;
import com.f1.utils.SH;

public class AmiQueryFinder_In implements AmiQueryFinder, AmiPreparedQueryInClause {

	private Set<Comparable> values;
	final private AmiColumnImpl column;
	final private AmiPreparedQuery query;
	private AmiQueryFinder next;

	public AmiQueryFinder_In(AmiPreparedQuery query, AmiColumnImpl column) {
		this.column = column;
		this.query = query;
	}

	@Override
	public boolean getRows(AmiIndexMap_Hash map, AmiQueryFinderVisitor finderVisitor) {
		if (this.values.size() < map.size()) {
			for (Comparable value : values) {
				AmiIndexMap e = map.get(value);
				if (e != null && !e.getRows(next, finderVisitor))
					return false;
			}
		} else {
			for (Entry<Comparable, AmiIndexMap> entry : map.entrySet())
				if (this.values.contains(entry.getKey())) {
					if (!entry.getValue().getRows(next, finderVisitor))
						return false;
				}
		}
		return true;
	}
	@Override
	public boolean getRows(AmiIndexMap_Tree map, AmiQueryFinderVisitor finderVisitor) {
		if (this.values.size() < map.size()) {
			for (Comparable value : values) {
				AmiIndexMap e = map.get(value);
				if (e != null && !e.getRows(next, finderVisitor))
					return false;
			}
		} else {
			for (Entry<Comparable, AmiIndexMap> entry : map.entrySet())
				if (this.values.contains(entry.getKey())) {
					if (!entry.getValue().getRows(next, finderVisitor))
						return false;
				}
		}
		return true;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		sb.append(column.getName()).append(" in (");
		boolean first = true;
		for (Object o : values) {
			if (first)
				first = false;
			else
				sb.append(", ");
			if (o instanceof String) {
				SH.quoteToJavaConst('\"', (String) o, sb);
			} else
				sb.append(o);

		}
		return sb.append(")");
	}
	@Override
	public boolean getRows(AmiIndexMap_Series map, AmiQueryFinderVisitor finderVisitor) {
		for (Comparable<?> o : values) {
			AmiIndexMap e = map.getIndex(o);
			if (e != null && !e.getRows(next, finderVisitor))
				return false;
		}
		return true;
	}
	@Override
	public AmiColumnImpl getColumn() {
		return this.column;
	}

	@Override
	public boolean matches(Comparable o) {
		return values.contains(o);
	}

	@Override
	public void setValues(Set<Comparable> values) {
		this.values = values;
	}

	@Override
	public Set<Comparable> getValues() {
		return this.values;
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
	public AmiQueryFinder getNext() {
		return next;
	}

	@Override
	public void setNext(AmiQueryFinder next) {
		this.next = next;
	}

}
