package com.f1.utils.sql;

import java.util.List;

import com.f1.base.Table;
import com.f1.utils.ToDoException;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.sqlnode.CreateTableNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorSqlCreateTable extends DerivedCellCalculatorSql {

	final private CreateTableNode createTableNode;
	final private DerivedCellCalculator as;

	public DerivedCellCalculatorSqlCreateTable(CreateTableNode createTableNode, SqlDerivedCellParser dcp, DerivedCellCalculator as) {
		super(createTableNode, dcp);
		this.as = as;
		this.createTableNode = createTableNode;
	}

	@Override
	public FlowControl get(CalcFrameStack lcvs) {
		SqlPlanListener listener = lcvs.getSqlPlanListener();
		if (listener != null)
			listener.onStart(this.createTableNode.toString());
		Object o = as == null ? null : as.get(lcvs);
		if (o instanceof FlowControl || o == null)
			return get(lcvs, (FlowControl) o);
		else if (o instanceof Table)
			return get(lcvs, new TableReturn((Table) o));
		else
			throw new ExpressionParserException(getPosition(), "Unexpected return value: " + o.getClass());
	}

	private FlowControl get(CalcFrameStack lcvs, FlowControl o) {

		List<Table> tables;
		if (o == null)
			tables = null;
		else if (o instanceof FlowControlPause) {
			return ((FlowControlPause) o).push(this, lcvs, 0);
		} else if (o instanceof TableReturn)
			tables = ((TableReturn) o).getTables();
		else
			throw new ToDoException();
		TableReturn tr = getProcessor().getSqlProcessor().getAdminProcessor().processCreateTable(lcvs, this, createTableNode, tables, getProcessor());
		return tr;
	}

	@Override
	public FlowControl resume(PauseStack paused) {
		FlowControl o = (FlowControl) paused.getNext().resume();
		return get(paused.getLcvs(), o);
	}
}
