package com.f1.ami.center.hdb;

import com.f1.base.CalcFrame;
import com.f1.base.Table;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.structs.table.ColumnPositionMapping;

public class AmiHdbSqlFlowControl_Insert2 extends AmiHdbSqlFlowControl {

	final private int startRow;
	final private int rowsCount;
	final private Table data;
	final private ColumnPositionMapping posMapping;

	public AmiHdbSqlFlowControl_Insert2(DerivedCellCalculatorSql query, AmiHdbTableRep targetTable, ColumnPositionMapping posMapping, int startRow, int rowsCount, Table data,
			CalcFrame vars, SqlPlanListener sqlPlanListener) {
		super(query, targetTable.getHistoricalTable(), vars, sqlPlanListener);
		this.posMapping = posMapping;
		this.startRow = startRow;
		this.rowsCount = rowsCount;
		this.data = data;
	}

	@Override
	public void run() {
		AmiHdbTable historicalTable = getTable();
		historicalTable.addRows(posMapping, startRow, rowsCount, data);
		this.tableReturn = new TableReturn(rowsCount);
	}

}
