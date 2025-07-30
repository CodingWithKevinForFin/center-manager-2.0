package com.f1.utils.sql;

import com.f1.base.Table;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.sqlnode.InsertNode;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorSqlInsert extends DerivedCellCalculatorSql {

	private InsertNode node;
	private DerivedCellCalculatorSql from;

	public DerivedCellCalculatorSqlInsert(InsertNode insertNode, SqlDerivedCellParser dcp, DerivedCellCalculatorSql from) {
		super(insertNode, dcp);
		this.from = from;
		this.node = insertNode;
	}

	@Override
	public FlowControl get(CalcFrameStack lcvs) {
		SqlPlanListener listener = lcvs.getSqlPlanListener();
		if (listener != null)
			listener.onStart(this.node.toString());
		FlowControl o = from == null ? null : from.get(lcvs);
		return get(lcvs, o);
	}

	private FlowControl get(CalcFrameStack lcvs, FlowControl o) {
		Table table;

		if (from == null)
			table = null;
		else if (o instanceof FlowControlPause) {
			return ((FlowControlPause) o).push(this, lcvs, 0);
		} else if (o instanceof TableReturn)
			table = DerivedHelper.toTableOrThrow(this.from.getPosition(), o);
		else
			throw new ExpressionParserException(from.getPosition(), "EXPECTING TABLE");
		FlowControl r = getProcessor().getSqlProcessor().getInsertProcessor().processInsert(lcvs, this, node, table);
		if (r instanceof FlowControlPause)
			return ((FlowControlPause) r).push(this, lcvs, 0);
		else
			return r;

	}

	@Override
	public FlowControl resume(PauseStack paused) {
		if (paused.getState() == 0 && paused.getNext() != null) {
			FlowControl o = (FlowControl) paused.getNext().resume();
			return get(paused.getLcvs(), o);
		} else {
			return ((FlowControlPauseTableReturn) paused.getFlowControlPause()).getTableReturn();
		}
	}
}
