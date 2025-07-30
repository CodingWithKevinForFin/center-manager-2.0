package com.f1.ami.amicommon;

import java.util.Map;

import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlDerivedCellParser;
import com.f1.utils.sql.SqlProcessorUtils.Limits;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.ValuesNode;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiDerivedCellCalculatorSql_UseInsertValues extends DerivedCellCalculatorSql {

	private String[] targetColumnNames;
	private String targetTableName;
	private ValuesNode values;
	private Map<String, DerivedCellCalculator> use;
	private Limits limits;

	public AmiDerivedCellCalculatorSql_UseInsertValues(ContainerTools tools, SqlDerivedCellParser dcp, SqlNode node, Map<String, DerivedCellCalculator> use,
			String[] targetColumnNames, String targetTableName, ValuesNode vn, Limits limits) {
		super(node, dcp);
		this.use = use;
		this.targetColumnNames = targetColumnNames;
		this.targetTableName = targetTableName;
		this.values = vn;
		this.limits = limits;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("BULK INSERT INTO ").append(this.targetTableName);
		if (this.targetColumnNames != null) {
			sink.append('(');
			SH.join(',', this.targetColumnNames, sink);
			sink.append(')');
		}
		return sink;
	}
	@Override
	final public FlowControl get(CalcFrameStack sf) {
		Object[][] rows = super.getProcessor().getSqlProcessor().getInsertProcessor().processValues(sf, values, values.getColCount(), null, null,
				this.limits.getLimit(super.getProcessor(), sf));
		ColumnarTable rowsAsTable = new ColumnarTable();
		for (int i = 0; i < values.getColCount(); i++)
			rowsAsTable.addColumn(Object.class, "_" + SH.toString(i));
		for (Object[] row : rows)
			rowsAsTable.getRows().addRow(row);

		return get(sf, rowsAsTable);
	}

	private FlowControl get(CalcFrameStack sf, Table table) {
		//		Table table;
		//		if (o instanceof FlowControlPause)
		//			return DerivedHelper.onFlowControl((FlowControlPause) o, this, sf, 0, null);
		//		else if (o instanceof TableReturn) {
		//			table = DerivedHelper.toTableOrThrow(values.getPosition(), o);
		//		} else
		//			throw new ExpressionParserException(values.getPosition(), "Expecting table");
		return new AmiFlowControlPauseSql_UseInsert(this, this.use, sf, this.targetTableName, this.targetColumnNames, table).push(this, sf, 0);
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (!super.isSame(other))
			return false;
		AmiDerivedCellCalculatorSql_UseInsertValues o = (AmiDerivedCellCalculatorSql_UseInsertValues) other;
		return OH.eq(this.targetColumnNames, o.targetColumnNames) && OH.eq(this.targetTableName, o.targetTableName);
	}
	@Override
	public FlowControl resume(PauseStack paused) {
		if (paused.getNext() == null) {
			AmiFlowControlPauseSql_UseInsert pause = (AmiFlowControlPauseSql_UseInsert) paused.getFlowControlPause();
			pause.throwIfError(this);
			return pause.getTableReturn();
		}
		FlowControl o = (FlowControl) paused.getNext().resume();
		//		if (o instanceof FlowControlPause)
		return DerivedHelper.onFlowControl((FlowControlPause) o, this, paused.getLcvs(), paused.getState(), paused.getAttachment());

		//		return get(paused.getLcvs(), o);
	}
	@Override
	public boolean isPausable() {
		return true;
	}
}
