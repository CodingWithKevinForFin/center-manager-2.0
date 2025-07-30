package com.f1.ami.center.hdb;

import java.util.List;

import com.f1.ami.center.hdb.qry.AmiHdbQueryImpl;
import com.f1.ami.center.table.AmiCenterSqlProcessorMutator;
import com.f1.ami.center.table.AmiCenterSqlProcessorMutator.Or;
import com.f1.ami.center.table.AmiCenterSqlProcessor_Select;
import com.f1.base.CalcFrame;
import com.f1.utils.OH;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.structs.table.columnar.ColumnarColumnLong;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiHdbSqlFlowControl_Delete extends AmiHdbSqlFlowControl {

	final private DerivedCellCalculator where;
	final private int offset;
	final private int limit;

	public AmiHdbSqlFlowControl_Delete(DerivedCellCalculatorSql query, AmiHdbTableRep table, int offset, int limit, DerivedCellCalculator where, CalcFrame vars,
			SqlPlanListener sqlPlanListener) {
		super(query, table.getHistoricalTable(), vars, sqlPlanListener);
		CalcFrameStack vars2 = new TopCalcFrameStack(vars);
		this.where = DerivedHelper.replaceVarsWithConsts(where, vars2, table.getColumnTypesMapping());
		this.offset = offset;
		this.limit = limit;

	}

	@Override
	public void run() throws Exception {
		AmiHdbTable ht = getTable();
		AmiHdbQueryImpl q = new AmiHdbQueryImpl(ht);
		Or ors = new Or();
		CalcFrameStack sf = createStackFrame();
		AmiCenterSqlProcessorMutator.reduce(sf, where, ors, ht.getTable().getColumnIds());
		List<DerivedCellCalculator[]> ands = ors.getAnds();
		q.setSelects(DerivedHelper.EMPTY_ARRAY, OH.EMPTY_STRING_ARRAY);
		q.setRowNumColumn("!!R");
		q.setLimit(offset, limit);
		AmiCenterSqlProcessor_Select.fillQuery(q, where, sf);
		ColumnarTable rs = q.query(sf);
		ColumnarColumnLong rows = (ColumnarColumnLong) rs.getColumn("!!R");
		long[] rowsToRemove = new long[rs.getSize()];
		for (int i = 0; i < rowsToRemove.length; i++)
			rowsToRemove[i] = rows.getLong(i);
		ht.removeRows(rowsToRemove);
		this.tableReturn = new TableReturn(rowsToRemove.length);

	}

}
