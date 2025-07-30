package com.f1.ami.center.table;

import com.f1.ami.center.hdb.AmiHdbSqlFlowControl_Update;
import com.f1.ami.center.hdb.AmiHdbTableRep;
import com.f1.base.Caster;
import com.f1.base.Table;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.SqlProcessorTableMutator;
import com.f1.utils.sql.SqlProcessor_Update;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterSqlProcessor_Update extends SqlProcessor_Update {

	public AmiCenterSqlProcessor_Update(SqlProcessor owner) {
		super(owner);
	}

	@Override
	protected FlowControl doUpdate(DerivedCellCalculatorSql node, SqlProcessorTableMutator mutator, Table table, int offset, int limit, int tableNamePos,
			int[] targetColumnPositions, Caster<?>[] targetColumnCasters, DerivedCellCalculator[] sourceExpressions, DerivedCellCalculator whereClause, CalcFrameStack sf) {
		if (table instanceof AmiHdbTableRep) {
			AmiCenterSqlProcessor_Select.processInnerQueries(whereClause, sf);
			return new AmiHdbSqlFlowControl_Update(node, mutator, (AmiHdbTableRep) table, offset, limit, tableNamePos, targetColumnPositions, targetColumnCasters,
					sourceExpressions, whereClause, DerivedHelper.toFrame(sf), sf.getSqlPlanListener());
		}
		return super.doUpdate(node, mutator, table, offset, limit, tableNamePos, targetColumnPositions, targetColumnCasters, sourceExpressions, whereClause, sf);
	}

}
