package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.LinkedHasherMap;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.sql.SqlProcessorUtils.Limits;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.sql.preps.AbstractPrepCalculator;
import com.f1.utils.sql.preps.AbstractPrepCalculator.PrepRows;
import com.f1.utils.sql.preps.AggregatePrepareFactory;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.SqlUnionNode;
import com.f1.utils.string.sqlnode.WhereNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class SqlProcessor_Prepare {

	final private SqlProcessor owner;

	public SqlProcessor_Prepare(SqlProcessor sqlProcessor) {
		this.owner = sqlProcessor;
	}

	public static PrepareClause buildPrepareClause(SqlColumnsNode node) {
		//deconstruct query
		SqlColumnsNode prep = (SqlColumnsNode) SqlExpressionParser.castToSqlNode(node, SqlExpressionParser.ID_PREPARE);
		AsNode[] selects = SqlProcessorUtils.toAsNode(prep.getColumns());
		final SqlColumnsNode from = (SqlColumnsNode) SqlExpressionParser.castNextToSqlNode(prep, SqlExpressionParser.ID_FROM);
		//		Node[] fromTables = from.columns;
		if (from.getColumnsCount() != 1)
			throw new ExpressionParserException(from.getPosition(), "FROM clause must have exactly one table");
		WhereNode where;
		SqlNode next = (SqlNode) from.getNext();
		if (next != null && next.getOperation() == SqlExpressionParser.ID_WHERE) {
			where = JavaExpressionParser.castNode(next, WhereNode.class);
			next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlColumnsNode.class);
		} else
			where = null;
		Node[] orderBys = null;
		if (next != null) {
			if (next.getOperation() == SqlExpressionParser.ID_ORDERBY) {
				orderBys = ((SqlColumnsNode) next).getColumns();
				next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlColumnsNode.class);
			}
		}
		final Node[] partitions;
		if (next != null) {
			if (next.getOperation() == SqlExpressionParser.ID_PARTITIONBY) {
				SqlColumnsNode partition = null;
				partition = (SqlColumnsNode) next;
				next = JavaExpressionParser.castNodeNotRequired(partition.getNext(), SqlColumnsNode.class);
				partitions = partition.getColumns();
			} else
				partitions = null;
		} else
			partitions = null;
		Limits limits = null;
		if (next != null) {
			if (next.getOperation() == SqlExpressionParser.ID_LIMIT) {
				limits = new SqlProcessorUtils.Limits(JavaExpressionParser.castNodeNotRequired(next, SqlColumnsNode.class));
				next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlNode.class);
			}
		}
		SqlUnionNode union = null;
		if (next != null && next.getOperation() == SqlExpressionParser.ID_UNION) {
			union = JavaExpressionParser.castNode(next, SqlUnionNode.class);
			next = null;
		}
		if (next != null)
			throw new ExpressionParserException(next.getPosition(), "Unexpected operation: " + next);
		Node ft = from.getColumnAt(0);
		AsNode[] tables = new AsNode[] { SqlProcessorUtils.toAsNode(ft) };//TODO: is this right?
		//prepare procs
		if (union == null)
			return new PrepareClause(prep, tables, selects, orderBys, partitions, limits, where, null, false);
		else {
			QueryClause qc = SqlProcessor.buildQueryClause(union.getNext());
			return new PrepareClause(prep, tables, selects, orderBys, partitions, limits, where, qc, union.isByName());
		}
	}
	public TableReturn processPrepare(PrepareClause pc, CalcFrameStack sf, Map<String, Table> tables) {
		AsNode[] selects = pc.getSelects();
		final Node[] orderBys = pc.getOrderBys();
		final Node[] partitions = pc.getPartitions();
		final Limits limits = pc.getLimits();
		final WhereNode where = pc.getWhere();
		final Table table = CH.getOrThrow(tables, pc.getTables()[0].getValue().toString());
		selects = SqlProcessor_Select.expandWildCards(selects, Collections.singletonMap(table.getTitle(), table));
		ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
		MethodFactoryManager mf = sf.getFactory();
		int colsCount = selects.length;
		DerivedCellCalculator whereCalc;
		if (where != null) {
			CalcTypes columnTypesMapping = SqlProcessorUtils.toTypes(sf, table);
			whereCalc = owner.getParser().toCalc(where.getCondition(), new ChildCalcTypesStack(sf, true, columnTypesMapping));
		} else
			whereCalc = null;

		DerivedCellCalculator[] columnCalcs = new DerivedCellCalculator[colsCount];
		Class<?>[] types = new Class<?>[colsCount];
		String[] names = new String[colsCount];
		AggregateFactory aggs = new AggregateFactory(mf);
		AggregatePrepareFactory preps = new AggregatePrepareFactory(aggs);
		CalcTypes columnTypesMapping = SqlProcessorUtils.toTypes(sf, table);
		for (int i = 0, p = 0; i < colsCount;) {
			AsNode n = selects[p++];
			String name = n.getAs().toString();
			if (name.equals("*")) {
				for (Column column : table.getColumns()) {
					types[i] = column.getType();
					names[i] = column.getId();
					columnCalcs[i] = new DerivedCellCalculatorRef(n.getPosition(), column.getType(), column.getId());
					i++;
				}
			} else {
				DerivedCellCalculator t = owner.getParser().toCalc(n.getValue(), new ChildCalcTypesStack(sf, true, columnTypesMapping, preps));
				types[i] = t.getReturnType();
				names[i] = name;
				columnCalcs[i] = t;
				i++;
			}
		}

		final DerivedCellCalculator[] partitionCalcs;
		if (partitions != null) {
			partitionCalcs = new DerivedCellCalculator[partitions.length];
			for (int i = 0; i < partitions.length; i++)
				partitionCalcs[i] = owner.getParser().toCalc(partitions[i], new ChildCalcTypesStack(sf, true, columnTypesMapping, mf));
		} else
			partitionCalcs = new DerivedCellCalculator[0];

		final DerivedCellCalculator[] orderByCalcs;
		final boolean[] orderByAsc;
		if (orderBys != null) {
			orderByCalcs = new DerivedCellCalculator[orderBys.length];
			orderByAsc = new boolean[orderBys.length];
			for (int i = 0; i < orderBys.length; i++) {
				SqlColumnsNode t = (SqlColumnsNode) orderBys[0];
				orderByAsc[i] = t.getOperation() == SqlExpressionParser.ID_ASC;
				orderByCalcs[i] = owner.getParser().toCalc(t.getNext(), new ChildCalcTypesStack(sf, true, columnTypesMapping, mf));
			}
		} else {
			orderByCalcs = null;
			orderByAsc = null;
		}

		//		RowGetter rg = new SqlProcessorUtils.RowGetter(table, sf.getGlobalVars());

		LinkedHasherMap<Object[], List<Row>> rowsByPartition = new LinkedHasherMap<Object[], List<Row>>(ArrayHasher.INSTANCE);
		List<Row> allRows = table.getRows();
		if (orderBys != null)
			allRows = CH.sort(allRows, new RowComparator(orderByCalcs, orderByAsc, rsf));
		if (partitions != null) {
			Object[] tmp = new Object[partitions.length];
			for (Row row : allRows) {
				rsf.reset(row);
				if (whereCalc != null && !Boolean.TRUE.equals(whereCalc.get(rsf)))
					continue;
				for (int i = 0; i < partitionCalcs.length; i++)
					tmp[i] = partitionCalcs[i].get(rsf);
				Entry<Object[], List<Row>> e = rowsByPartition.getOrCreateEntry(tmp);
				if (e.getValue() == null) {
					e.setValue(new ArrayList<Row>());
					tmp = new Object[partitions.length];
				}
				e.getValue().add(row);
			}
		} else if (whereCalc != null) {
			ArrayList<Row> l = new ArrayList<Row>();
			for (Row row : allRows) {
				if (where != null && !Boolean.TRUE.equals(whereCalc.get(rsf.reset(row))))
					continue;
				l.add(row);
			}
			rowsByPartition.put(null, l);
		} else {
			rowsByPartition.put(null, (List) allRows);
		}

		int firstRow = limits == null ? 0 : limits.getLimitOffset(this.owner.getParser(), sf);
		int limit = (limits == null || limits.getLimitNode() == null) ? table.getSize() : limits.getLimit(this.owner.getParser(), sf);

		Table r = SqlProjector.newBasicTable(types, names);
		Mutable.Int pos = new Mutable.Int(0);
		for (Entry<Object[], List<Row>> e : rowsByPartition) {
			List<Row> rows = e.getValue();
			PrepRows t = new AbstractPrepCalculator.PrepRows(pos, rows);
			for (AggCalculator i : aggs.getAggregates())
				i.visitRows(rsf, rows);
			for (AbstractPrepCalculator i : preps.getAggregates())
				i.visitRows(rsf, t, pos);
			int size = rows.size();
			if (firstRow > size) {
				firstRow -= size;
				continue;
			}
			int l = Math.min(firstRow + limit, size);
			for (int n = firstRow; n < l; n++) {
				pos.value = n;
				CalcFrame row = rows.get(n);
				Object[] values = new Object[columnCalcs.length];
				for (int i = 0; i < columnCalcs.length; i++) {
					values[i] = columnCalcs[i].get(rsf.reset(row));
				}
				r.getRows().addRow(values);
			}
			limit -= l - firstRow;
			firstRow = 0;
			if (limit <= 0)
				break;
		}
		return new TableReturn(r);
	}

	public static class RowComparator implements Comparator<CalcFrame> {

		final public DerivedCellCalculator[] orderBys;
		final private boolean[] orderBysAsc;
		final private ReusableCalcFrameStack sf;

		public RowComparator(DerivedCellCalculator[] orderBys, boolean orderByAsc[], ReusableCalcFrameStack stackFrame) {
			this.orderBys = orderBys;
			this.orderBysAsc = orderByAsc;
			this.sf = stackFrame;
		}
		@Override
		public int compare(CalcFrame o1, CalcFrame o2) {
			for (int i = 0; i < orderBys.length; i++) {
				DerivedCellCalculator o = orderBys[i];
				int t = OH.compare((Comparable) o.get(this.sf.reset(o1)), (Comparable) o.get(this.sf.reset(o2)));
				if (t != 0)
					return t > 0 == orderBysAsc[i] ? 1 : -1;
			}
			return 0;
		}
	}

}
