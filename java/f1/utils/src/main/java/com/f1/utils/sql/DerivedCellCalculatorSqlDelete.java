package com.f1.utils.sql;

import com.f1.base.Table;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorSqlDelete extends DerivedCellCalculatorSql {

	private SqlColumnsNode node;
	private DerivedCellCalculatorSql from;

	public DerivedCellCalculatorSqlDelete(SqlColumnsNode updateNode, SqlDerivedCellParser dcp, DerivedCellCalculatorSql from) {
		super(updateNode, dcp);
		this.from = from;
		this.node = updateNode;
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
		else if (o instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) o, this, lcvs, 0, null);
		else if (o instanceof TableReturn)
			table = DerivedHelper.toTableOrThrow(this.from.getPosition(), o);
		else
			throw new ExpressionParserException(from.getPosition(), "EXPECTING TABLE");
		FlowControl r = getProcessor().getSqlProcessor().getDeleteProcessor().processDelete(lcvs, this, node, table);
		if (r instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) r, this, lcvs, 1, null);
		else
			return r;

	}

	@Override
	public FlowControl resume(PauseStack paused) {
		if (paused.getState() == 0) {
			FlowControl o = (FlowControl) paused.getNext().resume();
			return get(paused.getLcvs(), o);
		} else {
			return ((FlowControlPauseTableReturn) paused.getFlowControlPause()).getTableReturn();
		}
	}

}
