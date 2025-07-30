package com.f1.ami.center.table;

import java.util.List;

import com.f1.ami.center.hdb.AmiHdbSqlFlowControl_Insert;
import com.f1.ami.center.hdb.AmiHdbSqlFlowControl_Insert2;
import com.f1.ami.center.hdb.AmiHdbTableRep;
import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.SqlProcessor_Insert;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.sqlnode.InsertNode;
import com.f1.utils.structs.table.ColumnPositionMapping;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterSqlProcessor_Insert extends SqlProcessor_Insert {

	public AmiCenterSqlProcessor_Insert(SqlProcessor owner) {
		super(owner);
	}

	@Override
	public FlowControl doInsert(CalcFrameStack sf, DerivedCellCalculatorSql query, InsertNode node, Table table, int tableNamePos, List<Column> columns, int[] syncOnTargetPos,
			Object[][] rows) {
		if (table instanceof AmiHdbTableRep) {
			if (node.getOperation() == SqlExpressionParser.ID_SYNC)
				throw new ExpressionParserException(0, "SYNC not supported for HISTORICAL tables");
			return new AmiHdbSqlFlowControl_Insert(query, (AmiHdbTableRep) table, columns, rows, DerivedHelper.toFrame(sf), sf.getSqlPlanListener());
		}
		return super.doInsert(sf, query, node, table, tableNamePos, columns, syncOnTargetPos, rows);
	}

	@Override
	public FlowControl doInsert(CalcFrameStack sf, DerivedCellCalculatorSql query, Table targetTable, int tableNamePos, ColumnPositionMapping posMapping, int startRow,
			int rowsCount, Table data, boolean returnGeneratedIds) {
		if (targetTable instanceof AmiHdbTableRep)
			return new AmiHdbSqlFlowControl_Insert2(query, (AmiHdbTableRep) targetTable, posMapping, startRow, rowsCount, data, DerivedHelper.toFrame(sf), sf.getSqlPlanListener());
		return super.doInsert(sf, query, targetTable, tableNamePos, posMapping, startRow, rowsCount, data, returnGeneratedIds);
	}
}
