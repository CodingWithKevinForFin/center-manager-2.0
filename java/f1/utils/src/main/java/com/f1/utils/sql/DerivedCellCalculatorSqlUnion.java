package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Table;
import com.f1.utils.ToDoException;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorSqlUnion extends DerivedCellCalculatorSql {

	final private List<DerivedCellCalculatorSql> selects;
	final private List<Boolean> byNames;

	public DerivedCellCalculatorSqlUnion(SqlColumnsNode sn, SqlDerivedCellParser dcp, List<DerivedCellCalculatorSql> selects, List<Boolean> byNames) {
		super(sn, dcp);
		this.selects = selects;
		this.byNames = byNames;
	}

	@Override
	public FlowControl get(CalcFrameStack sf) {
		List<Table> results = new ArrayList<Table>(selects.size());
		SqlPlanListener listener = sf.getSqlPlanListener();
		if (listener != null)
			listener.onStart(this.getNode().toString());
		if (listener != null)
			listener.onStep("PROJECTION", this.getNode().toString());
		return get(sf, results, 0);
	}

	private FlowControl get(CalcFrameStack sf, List<Table> results, int i) {
		while (i < selects.size()) {
			FlowControl o = selects.get(i).get(sf);
			FlowControlPause fcp = processTable(sf, o, results, i);
			if (fcp != null)
				return fcp;
			i++;
		}
		Table r = SqlProcessor_Select.unionTables(results, byNames, sf);
		return new TableReturn(r);
	}

	private FlowControlPause processTable(CalcFrameStack sf, FlowControl o, List<Table> results, int i) {
		Table table;
		if (o instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) o, this, sf, i, results);
		table = DerivedHelper.toTableOrThrow(this.selects.get(i).getPosition(), o);
		if (i != 0 && !byNames.get(i)) {
			int expectedCount = results.get(0).getColumnsCount(), colsCount = table.getColumnsCount();
			if (colsCount != expectedCount)
				throw new ExpressionParserException(selects.get(i).getPosition(), "UNION column count mismatch. Table #1 has " + expectedCount + " column(s), table #" + (i + 1)
						+ " has " + colsCount + " column(s).  See BYNAME syntax for mapping columns by name instead of position");
		}
		results.add(table);
		return null;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorSqlUnion((SqlColumnsNode) this.getNode(), this.getProcessor(), selects, byNames);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public int getInnerCalcsCount() {
		return this.selects.size();
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.selects.get(n);
	}

	@Override
	public FlowControl resume(PauseStack paused) {
		final int i = paused.getState();
		final FlowControl o = (FlowControl) paused.getNext().resume();
		final List<Table> tables = (List<Table>) paused.getAttachment();
		final CalcFrameStack lcvs = paused.getLcvs();
		FlowControlPause fcp = processTable(lcvs, o, tables, i);
		if (fcp != null)
			return fcp;
		return get(lcvs, tables, i + 1);
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		throw new ToDoException();
	}

}
