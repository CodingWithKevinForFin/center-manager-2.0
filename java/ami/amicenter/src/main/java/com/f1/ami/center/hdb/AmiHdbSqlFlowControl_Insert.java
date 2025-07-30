package com.f1.ami.center.hdb;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.base.Column;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.TableReturn;

public class AmiHdbSqlFlowControl_Insert extends AmiHdbSqlFlowControl {

	private Object[][] rows;
	private List<Column> columns;

	public AmiHdbSqlFlowControl_Insert(DerivedCellCalculatorSql query, AmiHdbTableRep table, List<Column> columns, Object[][] rows, CalcFrame vars,
			SqlPlanListener sqlPlanListener) {
		super(query, table.getHistoricalTable(), vars, sqlPlanListener);
		this.columns = columns;
		this.rows = rows;
	}

	@Override
	public void run() {
		AmiHdbTable historicalTable = getTable();
		int[] suppliedColPositions = new int[columns.size()];
		for (int i = 0; i < suppliedColPositions.length; i++)
			suppliedColPositions[i] = columns.get(i).getLocation();
		historicalTable.addRows(suppliedColPositions, rows);
		this.tableReturn = new TableReturn(rows.length);
		historicalTable.flushPersisted();
	}

	public boolean isSameSchema(AmiHdbSqlFlowControl_Insert other) {
		int size = columns.size();
		if (size != other.columns.size())
			return false;
		for (int i = 0; i < other.columns.size(); i++)
			if (other.columns.get(i) != columns.get(i))
				return false;
		return true;
	}

	public List<Column> getColumns() {
		return this.columns;
	}

	public Object[][] getValues() {
		return this.rows;
	}

	public void setTableReturn(TableReturn tableReturn) {
		this.tableReturn = tableReturn;
	}

}
