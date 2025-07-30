package com.f1.ami.center.table;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.center.table.index.AmiQueryFinder;
import com.f1.ami.center.table.index.AmiQueryFinder_All;
import com.f1.ami.center.table.index.AmiQueryFinder_Between;
import com.f1.ami.center.table.index.AmiQueryFinder_Comparator;
import com.f1.ami.center.table.index.AmiQueryFinder_Eq;
import com.f1.ami.center.table.index.AmiQueryFinder_In;
import com.f1.ami.center.table.index.AmiQueryFinder_Matcher;
import com.f1.ami.center.table.index.AmiQueryScanner;
import com.f1.base.ToStringable;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiPreparedQueryImpl implements AmiPreparedQuery, AmiQueryScanner, ToStringable {

	final private AmiTableImpl table;
	final private List<AmiQueryFinder> clauses = new ArrayList<AmiQueryFinder>();
	private AmiQueryFinder[] finders;
	private AmiIndexImpl index;
	private AmiQueryFinder[] scanners;
	private boolean prepared = false;
	private DerivedCellCalculator[] expressions = null;
	final private ReusableCalcFrameStack scf;

	public AmiPreparedQueryImpl(AmiTableImpl table) {
		this.table = table;
		scf = new ReusableCalcFrameStack(EmptyCalcFrameStack.INSTANCE);
	}

	@Override
	public AmiPreparedQueryCompareClause addCompare(AmiColumn column, byte type) {
		OH.assertEqIdentity(column.getAmiTable(), table, "Column not member of same table as index");
		AmiPreparedQueryCompareClause r;
		if (type == AmiQueryFinder_Comparator.EQ) {
			r = new AmiQueryFinder_Eq(this, (AmiColumnImpl) column);
		} else {
			r = new AmiQueryFinder_Comparator(this, (AmiColumnImpl) column, type);
		}
		clauses.add(r);
		reset();
		return r;
	}

	@Override
	public void addExpression(DerivedCellCalculator dcc) {
		if (expressions == null)
			this.expressions = new DerivedCellCalculator[] { dcc };
		else
			this.expressions = AH.append(this.expressions, dcc);
	}

	@Override
	public AmiPreparedQueryBetweenClause addBetween(AmiColumn column, boolean minInclusive, boolean maxInclusive) {
		OH.assertEqIdentity(column.getAmiTable(), table, "Column not member of same table as index");
		AmiQueryFinder_Between r = new AmiQueryFinder_Between(this, (AmiColumnImpl) column, minInclusive, maxInclusive);
		clauses.add(r);
		reset();
		return r;
	}

	@Override
	public AmiPreparedQueryInClause addIn(AmiColumn column) {
		OH.assertEqIdentity(column.getAmiTable(), table, "Column not member of same table as index");
		AmiQueryFinder_In r = new AmiQueryFinder_In(this, (AmiColumnImpl) column);
		clauses.add(r);
		reset();
		return r;
	}
	@Override
	public AmiPreparedQueryMatcherClause addMatcher(AmiColumn column) {
		OH.assertEqIdentity(column.getAmiTable(), table, "Column not member of same table as index");
		AmiQueryFinder_Matcher r = new AmiQueryFinder_Matcher(this, (AmiColumnImpl) column);
		clauses.add(r);
		reset();
		return r;
	}

	public AmiQueryFinder[] getFinders() {
		return finders;
	}

	public AmiQueryScanner getScanner() {
		return scanners == null && expressions == null ? null : this;
	}

	public AmiIndexImpl getIndex() {
		prepare();
		return this.index;
	}

	private void prepare() {
		if (prepared)
			return;
		this.scanners = null;
		index = determineBestIndex(table, clauses);
		if (index != null) {
			this.finders = new AmiQueryFinder[this.index.getColumnsCount()];
			for (int i = 0; i < index.getColumnsCount(); i++) {
				AmiColumnImpl<?> col = index.getColumn(i);
				int bestClausePos = determineBestClause(clauses, col);
				if (bestClausePos == -1) {
					finders[i] = new AmiQueryFinder_All(this, col);
				} else {
					finders[i] = clauses.get(bestClausePos);
				}
			}
			for (int j = 0; j < clauses.size(); j++)
				if (AH.indexOfByIdentity(clauses.get(j), this.finders) == -1)
					scanners = scanners == null ? new AmiQueryFinder[] { clauses.get(j) } : AH.append(scanners, clauses.get(j));
		} else {
			this.finders = new AmiQueryFinder[0];
			scanners = AH.toArray(clauses, AmiQueryFinder.class);
		}
		for (int i = 0; i < finders.length - 1; i++)
			finders[i].setNext(finders[i + 1]);
		prepared = true;
	}

	private void reset() {
		this.prepared = false;
	}

	@Override
	public boolean matches(AmiRowImpl row) {
		if (scanners != null)
			for (AmiQueryFinder scanner : scanners)
				if (!scanner.matches(row.getIsNull(scanner.getColumn()) ? null : scanner.getColumn().getComparable(row)))
					return false;
		if (expressions != null) {
			scf.reset(row);
			for (DerivedCellCalculator i : expressions)
				if (!Boolean.TRUE.equals(i.get(scf)))
					return false;
		}
		return true;
	}
	//helpers
	static private int determineBestClause(List<AmiQueryFinder> clauses, AmiColumnImpl<?> col) {
		int bestScore = 0;
		int r = -1;
		for (int j = 0; j < clauses.size(); j++) {
			AmiQueryFinder c = clauses.get(j);
			if (c.getColumn() == col && c.getScore() > bestScore) {
				bestScore = c.getScore();
				r = j;
			}
		}
		return r;
	}
	static private int getIndexScore(AmiIndexImpl index, List<AmiQueryFinder> clauses) {
		int r = 0;
		for (int i = 0; i < index.getColumnsCount(); i++) {
			int t = determineBestClause(clauses, index.getColumn(i));
			if (t != -1)
				r += clauses.get(t).getScore();
			else
				break;
		}
		return (r * 100) / index.getColumnsCount();
	}
	static private AmiIndexImpl determineBestIndex(AmiTableImpl table, List<AmiQueryFinder> clauses) {
		int bestScore = 0;
		AmiIndexImpl bestIndex = null;
		for (AmiIndexImpl i : table.getIndexes()) {
			int score = getIndexScore(i, clauses);
			if (score > 0 && (bestIndex == null || score > bestScore || (score == bestScore && i.isHigherCardinality(bestIndex)))) {
				bestScore = score;
				bestIndex = i;
			}
		}
		return bestIndex;
	}

	@Override
	public AmiTable getAmiTable() {
		return this.table;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		prepare();
		sink.append("SELECT * FROM ").append(table.getName());
		if (index != null) {
			sink.append(" USING INDEX ");
			index.toString(sink);
		} else
			sink.append(" USING FORWARD SCAN");
		sink.append(" WHERE ");

		findersToString(sink);
		return sink;
	}

	public StringBuilder findersToString(StringBuilder sink) {
		for (AmiQueryFinder i : this.finders)
			i.toString(sink).append(" AND ");
		sink.append("(");
		if (scanners == null)
			sink.append("true");
		else
			SH.join(" AND ", this.scanners, sink);
		sink.append(")");
		return sink;
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public AmiPreparedQueryCompareClause addEq(AmiColumn column) {
		return addCompare(column, AmiQueryFinder_Comparator.EQ);
	}

	@Override
	public AmiPreparedQueryCompareClause addNe(AmiColumn column) {
		return addCompare(column, AmiQueryFinder_Comparator.NE);
	}

	@Override
	public AmiPreparedQueryCompareClause addGt(AmiColumn column) {
		return addCompare(column, AmiQueryFinder_Comparator.GT);
	}

	@Override
	public AmiPreparedQueryCompareClause addLt(AmiColumn column) {
		return addCompare(column, AmiQueryFinder_Comparator.LT);
	}

	@Override
	public AmiPreparedQueryCompareClause addGe(AmiColumn column) {
		return addCompare(column, AmiQueryFinder_Comparator.GE);
	}

	@Override
	public AmiPreparedQueryCompareClause addLe(AmiColumn column) {
		return addCompare(column, AmiQueryFinder_Comparator.LE);
	}

	public AmiQueryFinder_Between collapseComparesToBetween() {
		int size = clauses.size();
		if (size < 2)
			return null;
		for (int i = 0; i < size; i++) {
			AmiQueryFinder c1 = clauses.get(i);
			if (!(c1 instanceof AmiPreparedQueryCompareClause))
				continue;
			AmiPreparedQueryCompareClause cc1 = (AmiPreparedQueryCompareClause) c1;
			AmiColumnImpl col = cc1.getColumn();
			for (int j = i + 1; j < size; j++) {
				AmiQueryFinder c2 = clauses.get(j);
				if (c2.getColumn() != col)
					continue;
				if (!(c2 instanceof AmiPreparedQueryCompareClause))
					continue;
				AmiPreparedQueryCompareClause cc2 = (AmiPreparedQueryCompareClause) c2;
				AmiQueryFinder_Between r;
				int op1 = cc1.getCompareType() | (cc2.getCompareType() << 8);
				switch (op1) {
					case AmiPreparedQueryCompareClause.GT | (AmiPreparedQueryCompareClause.LT << 8):
						r = new AmiQueryFinder_Between(this, col, false, false);
						r.setMinMax(cc1.getValue(), cc2.getValue());
						break;
					case AmiPreparedQueryCompareClause.GT | (AmiPreparedQueryCompareClause.LE << 8):
						r = new AmiQueryFinder_Between(this, col, false, true);
						r.setMinMax(cc1.getValue(), cc2.getValue());
						break;
					case AmiPreparedQueryCompareClause.GE | (AmiPreparedQueryCompareClause.LT << 8):
						r = new AmiQueryFinder_Between(this, col, true, false);
						r.setMinMax(cc1.getValue(), cc2.getValue());
						break;
					case AmiPreparedQueryCompareClause.GE | (AmiPreparedQueryCompareClause.LE << 8):
						r = new AmiQueryFinder_Between(this, col, true, true);
						r.setMinMax(cc1.getValue(), cc2.getValue());
						break;
					case AmiPreparedQueryCompareClause.LT | (AmiPreparedQueryCompareClause.GT << 8):
						r = new AmiQueryFinder_Between(this, col, false, false);
						r.setMinMax(cc2.getValue(), cc1.getValue());
						break;
					case AmiPreparedQueryCompareClause.LT | (AmiPreparedQueryCompareClause.GE << 8):
						r = new AmiQueryFinder_Between(this, col, true, false);
						r.setMinMax(cc2.getValue(), cc1.getValue());
						break;
					case AmiPreparedQueryCompareClause.LE | (AmiPreparedQueryCompareClause.GT << 8):
						r = new AmiQueryFinder_Between(this, col, false, true);
						r.setMinMax(cc2.getValue(), cc1.getValue());
						break;
					case AmiPreparedQueryCompareClause.LE | (AmiPreparedQueryCompareClause.GE << 8):
						r = new AmiQueryFinder_Between(this, col, true, true);
						r.setMinMax(cc2.getValue(), cc1.getValue());
						break;
					default:
						continue;
				}
				clauses.remove(j);
				clauses.remove(i);
				clauses.add(0, r);
				return r;
			}
		}
		return null;
	}

}
