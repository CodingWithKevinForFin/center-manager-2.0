package com.f1.utils.sql;

import com.f1.utils.OH;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorSqlTableRef extends DerivedCellCalculatorSql {

	final private String tableName;
	final private int scope;

	public DerivedCellCalculatorSqlTableRef(Node node, SqlDerivedCellParser dcp, String tableName, int scope) {
		super(node, dcp);
		this.tableName = tableName;
		this.scope = scope;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (scope != SqlExpressionParser.ID_INVALID)
			sink.append(SqlExpressionParser.toOperationString(scope)).append(" ");
		sink.append(tableName);
		return sink;

	}

	@Override
	public FlowControl get(CalcFrameStack sf) {
		return new TableReturn(getProcessor().getSqlProcessor().getMutator().getTable(sf, getPosition(), tableName, scope));
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorSqlTableRef(super.getNode(), getProcessor(), tableName, scope);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public int getInnerCalcsCount() {
		return 0;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return null;
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other == this)
			return true;
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorSqlTableRef o = (DerivedCellCalculatorSqlTableRef) other;
		return OH.eq(o.tableName, tableName) && OH.eq(o.scope, scope);
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

}
