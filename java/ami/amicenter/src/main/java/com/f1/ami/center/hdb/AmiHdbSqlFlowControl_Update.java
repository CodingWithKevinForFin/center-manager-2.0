package com.f1.ami.center.hdb;

import java.util.List;

import com.f1.ami.center.hdb.qry.AmiHdbQueryImpl;
import com.f1.ami.center.table.AmiCenterSqlProcessorMutator;
import com.f1.ami.center.table.AmiCenterSqlProcessorMutator.Or;
import com.f1.ami.center.table.AmiCenterSqlProcessor_Select;
import com.f1.base.CalcFrame;
import com.f1.base.Caster;
import com.f1.base.Row;
import com.f1.utils.OH;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProcessorTableMutator;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.columnar.ColumnarColumnLong;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorCast;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiHdbSqlFlowControl_Update extends AmiHdbSqlFlowControl {

	final private DerivedCellCalculator where;
	final private int offset;
	final private int limit;
	final private DerivedCellCalculator[] sourceExpressions;
	final private int[] targetColumnPositions;
	final private int tableNamePos;

	public AmiHdbSqlFlowControl_Update(DerivedCellCalculatorSql node, SqlProcessorTableMutator mutator, AmiHdbTableRep table, int offset, int limit, int tableNamePos,
			int[] targetColumnPositions, Caster<?>[] targetColumnCasters, DerivedCellCalculator[] sourceExpressions, DerivedCellCalculator whereClause, CalcFrame vars,
			SqlPlanListener sqlPlanListener) {
		super(node, table.getHistoricalTable(), vars, sqlPlanListener);
		CalcFrameStack vars2 = new TopCalcFrameStack(vars);
		this.where = DerivedHelper.replaceVarsWithConsts(whereClause, vars2, table.getColumnTypesMapping());
		this.limit = limit;
		this.offset = offset;
		this.sourceExpressions = sourceExpressions;
		this.targetColumnPositions = targetColumnPositions;
		this.tableNamePos = tableNamePos;
	}

	@Override
	public void run() throws Exception {
		AmiHdbTable ht = getTable();
		//		ht.lock(getTimeoutController());
		//		try {
		AmiHdbQueryImpl q = new AmiHdbQueryImpl(ht);
		Or ors = new Or();
		CalcFrameStack sf = createStackFrame();
		AmiCenterSqlProcessorMutator.reduce(sf, where, ors, ht.getTable().getColumnIds());
		List<DerivedCellCalculator[]> ands = ors.getAnds();
		int setsCount = targetColumnPositions.length;
		DerivedCellCalculator[] selects = new DerivedCellCalculator[setsCount * 2];
		String[] selectNames = new String[setsCount * 2];
		for (int i = 0; i < setsCount; i++) {
			AmiHdbColumn c = ht.getColumnAt(targetColumnPositions[i]);
			if (c instanceof AmiHdbColumn_Partition)
				throw new ExpressionParserException(tableNamePos, "Can not update values in PARTITION column '" + c.getName() + "'");
			selectNames[i] = c.getName();
			selects[i] = new DerivedCellCalculatorCast(0, c.getType(), sourceExpressions[i], c.getTypeCaster());
			selectNames[i + setsCount] = "!!" + c.getName();
			selects[i + setsCount] = new DerivedCellCalculatorRef(0, c.getType(), c.getName());
		}
		q.setSelects(selects, selectNames);
		q.setRowNumColumn("!!R");
		q.setLimit(offset, limit);
		AmiCenterSqlProcessor_Select.fillQuery(q, where, sf);
		ColumnarTable rs = q.query(sf);
		ColumnarColumnLong rows = (ColumnarColumnLong) rs.getColumn("!!R");
		for (int i = rs.getSize() - 1; i >= 0; i--) {
			Row r = rs.getRow(i);
			int cntSame = 0;
			for (int x = 0; x < setsCount; x++) {
				if (OH.eq(r.getAt(x + 1), r.getAt(x + setsCount + 1))) {
					cntSame++;
				}
			}
			if (cntSame == setsCount)
				rs.removeRow(i);

		}
		long[] rowsToUpdate = new long[rs.getSize()];
		for (int i = 0; i < rowsToUpdate.length; i++)
			rowsToUpdate[i] = rows.getLong(i);
		rs.removeColumn("!!R");
		ht.updateRows(rowsToUpdate, rs);
		this.tableReturn = new TableReturn(rowsToUpdate.length);

	}
}
