package com.f1.ami.center.hdb;

import java.io.IOException;

import com.f1.ami.center.hdb.qry.AmiHdbQueryImpl;
import com.f1.ami.center.table.AmiCenterSqlProcessor_Select;
import com.f1.base.CalcFrame;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.utils.AH;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiHdbSqlFlowControl_Select extends AmiHdbSqlFlowControl {

	final private String tableAsName;
	final private String[] names;
	final private DerivedCellCalculator[] selectCalcs;
	final private DerivedCellCalculator where;
	final private DerivedCellCalculator[] groupby;
	final private DerivedCellCalculator having;
	final private DerivedCellCalculator[] orderByCalcs;
	final private boolean[] orderByAsc;
	final private int limitOffset;
	final private int limit;
	final private AggregateFactory factory;
	final private DerivedCellCalculatorSql query;

	public AmiHdbSqlFlowControl_Select(DerivedCellCalculatorSql query, AmiHdbTableRep table, String tableAsName, String[] names, DerivedCellCalculator[] selectCalcs,
			DerivedCellCalculator where, DerivedCellCalculator[] groupby, DerivedCellCalculator having, DerivedCellCalculator[] orderByCalcs, boolean[] orderByAsc, int limitOffset,
			int limit, AggregateFactory factory, CalcFrame vars, SqlPlanListener sqlPlanListener) {
		super(query, table.getHistoricalTable(), vars, sqlPlanListener);
		this.query = query;
		this.tableAsName = tableAsName;
		this.names = names;
		CalcFrameStack vars2 = new TopCalcFrameStack(vars);
		NameSpaceCalcTypes tf = table.getColumnTypesMapping();
		this.selectCalcs = DerivedHelper.replaceVarsWithConsts(selectCalcs, vars2, tf);
		this.where = DerivedHelper.replaceVarsWithConsts(where, vars2, tf);
		this.groupby = DerivedHelper.replaceVarsWithConsts(groupby, vars2, tf);
		this.having = DerivedHelper.replaceVarsWithConsts(having, vars2, tf);
		this.orderByCalcs = DerivedHelper.replaceVarsWithConsts(orderByCalcs, vars2, tf);

		this.orderByAsc = orderByAsc;
		this.limitOffset = limitOffset;
		this.limit = limit;
		this.factory = factory;
	}

	@Override
	public void run() throws IOException {
		AmiHdbTable ht = getTable();
		ht.assertLocked();
		AmiHdbQueryImpl q = new AmiHdbQueryImpl(ht);
		//		Or ors = new Or();
		//		AmiCenterSqlProcessorMutator.reduce(where, ors, globalVars, ht.getTable().getColumnIds());
		//		List<DerivedCellCalculator[]> ands = ors.getAnds();
		q.setSelects(selectCalcs, names);
		q.setGroupBys(groupby, having, AH.toArray(factory.getAggregates(), AggCalculator.class));
		q.setLimit(limitOffset, limit);
		q.setOrderBys(orderByAsc, orderByCalcs);
		CalcFrameStack sf = super.createStackFrame();
		AmiCenterSqlProcessor_Select.fillQuery(q, where, sf);
		ColumnarTable rs = q.query(sf);
		rs.setTitle(tableAsName);
		this.tableReturn = new TableReturn(rs);

	}

}
