package com.f1.ami.center.table.index;

import java.util.Map.Entry;

import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiQueryFinder_Comparator implements AmiQueryFinder, AmiPreparedQueryCompareClause {

	private Comparable value;
	final private byte type;
	final private AmiColumnImpl column;
	final private AmiPreparedQuery query;
	final private int score;
	private AmiQueryFinder next;
	private boolean isPossible;

	public AmiQueryFinder_Comparator(AmiPreparedQuery query, AmiColumnImpl column, byte b) {
		this.query = query;
		this.column = column;
		if (b == -1)
			throw new IllegalArgumentException();
		this.type = b;
		switch (type) {
			case EQ:
				score = SCORE_EQ;
				break;
			case NE:
				score = SCORE_NE;
				break;
			case LE:
			case GE:
				score = SCORE_GELE;
				break;
			case GT:
			case LT:
				score = SCORE_GTLT;
				break;
			default:
				score = 0;
		}
	}

	@Override
	public boolean getRows(AmiIndexMap_Hash map, AmiQueryFinderVisitor finderVisitor) {
		if (!isPossible)
			return true;
		switch (type) {
			case EQ:
				return visit(map.get(value), finderVisitor);
			case NE:
				for (Entry<Comparable, AmiIndexMap> i : map.entrySet())
					if (OH.ne(i.getKey(), value))
						if (!visit(i.getValue(), finderVisitor))
							return false;
				break;
			case LE:
				for (Entry<Comparable, AmiIndexMap> i : map.entrySet())
					if (OH.le(i.getKey(), value, true))
						if (!visit(i.getValue(), finderVisitor))
							return false;
				break;
			case GE:
				for (Entry<Comparable, AmiIndexMap> i : map.entrySet())
					if (OH.ge(i.getKey(), value, true))
						if (!visit(i.getValue(), finderVisitor))
							return false;
				break;
			case GT:
				for (Entry<Comparable, AmiIndexMap> i : map.entrySet())
					if (OH.gt(i.getKey(), value, true))
						if (!visit(i.getValue(), finderVisitor))
							return false;
				break;
			case LT:
				for (Entry<Comparable, AmiIndexMap> i : map.entrySet())
					if (OH.lt(i.getKey(), value, true))
						if (!visit(i.getValue(), finderVisitor))
							return false;
				break;
		}
		return true;
	}

	@Override
	public boolean getRows(AmiIndexMap_Tree map, AmiQueryFinderVisitor finderVisitor) {
		if (!isPossible)
			return true;
		switch (type) {
			case EQ:
				return visit(map.get(value), finderVisitor);
			case NE:
				for (Entry<Comparable, AmiIndexMap> i : map.entrySet())
					if (OH.ne(i.getKey(), value))
						if (!visit(i.getValue(), finderVisitor))
							return false;
				break;
			case LE:
				for (Entry<Comparable, AmiIndexMap> i : map.headMap(value, true).entrySet())
					if (!visit(i.getValue(), finderVisitor))
						return false;
				break;
			case GE:
				for (Entry<Comparable, AmiIndexMap> i : map.tailMap(value, true).entrySet())
					if (!visit(i.getValue(), finderVisitor))
						return false;
				break;
			case GT:
				for (Entry<Comparable, AmiIndexMap> i : map.tailMap(value, false).entrySet())
					if (!visit(i.getValue(), finderVisitor))
						return false;
				break;
			case LT:
				for (Entry<Comparable, AmiIndexMap> i : map.headMap(value, false).entrySet())
					if (!visit(i.getValue(), finderVisitor))
						return false;
				break;
		}
		return true;
	}
	@Override
	public boolean getRows(AmiIndexMap_Series map, AmiQueryFinderVisitor finderVisitor) {
		if (!isPossible)
			return true;
		long lvalue = ((Number) value).longValue();
		switch (type) {
			case EQ:
				return visit(map.getIndex(value), finderVisitor);
			case NE:
				for (int i = 0, size = map.size(); i < size; i++)
					if (map.getKeyAt(i) != lvalue)
						if (!visit(map.getAt(i), finderVisitor))
							return false;
				break;
			case LE:
				for (int i = map.getIndexFloor(lvalue, true); i >= 0; i--)
					if (!visit(map.getAt(i), finderVisitor))
						return false;
				break;
			case GE:
				for (int i = map.getIndexCeiling(lvalue, true), size = map.size(); i < size; i++)
					if (!visit(map.getAt(i), finderVisitor))
						return false;
				break;
			case GT:
				for (int i = map.getIndexCeiling(lvalue, false), size = map.size(); i < size; i++)
					if (!visit(map.getAt(i), finderVisitor))
						return false;
				break;
			case LT:
				for (int i = map.getIndexFloor(lvalue, false); i >= 0; i--)
					if (!visit(map.getAt(i), finderVisitor))
						return false;
				break;
		}
		return true;
	}
	private boolean visit(AmiIndexMap map, AmiQueryFinderVisitor finderVisitor) {
		if (map == null)
			return true;
		return map.getRows(next, finderVisitor);
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
		return type;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		sb.append(column.getName()).append(" ").append(toString(type)).append(" ");
		if (value instanceof String) {
			SH.quoteToJavaConst('\"', (String) value, sb);
		} else
			sb.append(value);
		return sb;

	}
	public static byte parseType(String type) {
		switch (type.length()) {
			case 1:
				switch (type.charAt(0)) {
					case '<':
						return LT;
					case '>':
						return GT;
				}
				break;
			case 2:
				switch (type.charAt(0) | (type.charAt(1) << 8)) {
					case '=' | ('=' << 8):
						return EQ;
					case '<' | ('=' << 8):
						return LE;
					case '>' | ('=' << 8):
						return GE;
					case '!' | ('=' << 8):
						return NE;
				}
				break;
		}
		throw new RuntimeException("Unknown comparator: " + type + " Must be either: ==, !=, <, >, >=, <= ");
	}
	public String toString(byte type) {
		switch (type) {
			case NE:
				return "!=";
			case EQ:
				return "==";
			case GT:
				return ">";
			case GE:
				return ">=";
			case LT:
				return "<";
			case LE:
				return "<=";
			default:
				throw new RuntimeException("Unknown type: " + type);
		}
	}

	@Override
	public boolean matches(Comparable value) {
		switch (type) {
			case EQ:
				return OH.eq(value, this.value);
			case NE:
				return OH.ne(value, this.value);
			case LE:
				return OH.le(value, this.value, true);
			case GE:
				return OH.ge(value, this.value, true);
			case GT:
				return OH.gt(value, this.value, true);
			case LT:
				return OH.lt(value, this.value, true);
		}
		return false;
	}

	@Override
	public AmiColumnImpl getColumn() {
		return this.column;
	}

	@Override
	public int getScore() {
		return score;
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
