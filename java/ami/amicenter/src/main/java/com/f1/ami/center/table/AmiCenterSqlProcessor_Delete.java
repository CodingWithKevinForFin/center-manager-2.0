package com.f1.ami.center.table;

import com.f1.ami.center.hdb.AmiHdbSqlFlowControl_Delete;
import com.f1.ami.center.hdb.AmiHdbTableRep;
import com.f1.base.Table;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.SqlProcessorTableMutator;
import com.f1.utils.sql.SqlProcessor_Delete;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterSqlProcessor_Delete extends SqlProcessor_Delete {

	public AmiCenterSqlProcessor_Delete(SqlProcessor owner) {
		super(owner);
	}

	@Override
	protected FlowControl doDelete(DerivedCellCalculatorSql query, SqlProcessorTableMutator mutator, Table table, int offset, int limit, DerivedCellCalculator whereClause,
			CalcFrameStack sf) {
		if (table instanceof AmiHdbTableRep) {
			AmiCenterSqlProcessor_Select.processInnerQueries(whereClause, sf);
			return new AmiHdbSqlFlowControl_Delete(query, (AmiHdbTableRep) table, offset, limit, whereClause, DerivedHelper.toFrame(sf), sf.getSqlPlanListener());
		}
		return super.doDelete(query, mutator, table, offset, limit, whereClause, sf);
	}

}
