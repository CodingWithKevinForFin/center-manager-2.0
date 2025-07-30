package com.f1.ami.center.hdb.qry;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.ami.center.hdb.AmiHdbColumn_Partition;
import com.f1.ami.center.hdb.AmiHdbPartition;
import com.f1.ami.center.hdb.AmiHdbPartitionColumn;
import com.f1.ami.center.hdb.AmiHdbTable;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.CalcFrame;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProjector;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.structs.table.BasicRowComparatorForCalc;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarColumnInt;
import com.f1.utils.structs.table.columnar.ColumnarColumnLong;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.TablesCalcFrame;

public class AmiHdbResultSetWalker {

	private static class Entry implements Comparable<Entry> {

		final int row;
		final Comparable indexValue;
		Comparable[] filterValues;

		public Entry(int row, Comparable indexValue) {
			this.row = row;
			this.indexValue = indexValue;
		}

		@Override
		public int compareTo(Entry o) {
			return OH.compare(row, o.row);
		}
	}

	//	public static void query(AmiHdbQueryImpl query, DerivedCellCalculator additionalClause, AmiHdbResultSet inner, List<AmiHdbQueryPart> filters, SqlPlanListener planListener,
	//			DerivedCellTimeoutController timeoutController, Map<String, Object> globalVars, DerivedCellCalculator[] groupBys, DerivedCellCalculator having,
	//			Mutable.Long limitOffset, int limit, boolean onEmptyAggIncludeOneRow, List<AmiHdbColumn> dependencies, ColumnarTable sink) throws IOException {
	//
	//		if (additionalClause == null && dependencies.isEmpty() && filters.isEmpty() && groupBys.length == 1 && groupBys[0].isConst())
	//			queryJustCount(query, inner, filters, planListener, timeoutController, globalVars, having, sink);
	//		else {
	//			queryFull(query, additionalClause, inner, filters, planListener, timeoutController, globalVars, groupBys, having, limitOffset, limit, onEmptyAggIncludeOneRow,
	//					dependencies, sink);
	//		}
	//	}

	public static void queryFull(AmiHdbQueryImpl query, DerivedCellCalculator additionClause, AmiHdbResultSet inner, List<AmiHdbQueryPart> filters,
			DerivedCellCalculator[] groupBys, DerivedCellCalculator having, Mutable.Long limitOffset, int limit, boolean onEmptyAggIncludeOneRow, List<AmiHdbColumn> columns,
			ColumnarTable out, CalcFrameStack sf) throws IOException {
		final SqlPlanListener planListener = sf.getSqlPlanListener();
		final TimeoutController timeoutController = sf.getTimeoutController();
		AmiHdbTable innerTable = query.getTable();

		final DerivedCellCalculator[] orderBysAfter = query.getOrderBysAfter();
		final DerivedCellCalculator[] orderBysBefore = query.getOrderBysBefore();
		String[] names = new String[columns.size()];
		Class[] types = new Class[columns.size()];
		int[] filterAtCol = new int[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			AmiHdbColumn c = columns.get(i);
			names[i] = c.getName();
			types[i] = c.getType();
		}

		Table table = new ColumnarTable(types, names);
		table.setTitle(query.getTable().getName());
		ColumnarColumnLong rowNumCol = (ColumnarColumnLong) (query.getRowNumColumn() == null ? null : table.addColumn(Long.class, query.getRowNumColumn()));
		ColumnarColumnInt partitionCol = (ColumnarColumnInt) (query.getPartitionColumn() == null ? null : table.addColumn(Integer.class, query.getPartitionColumn()));
		AH.fill(filterAtCol, -1);
		for (int i = 0; i < filters.size(); i++) {
			Column column = table.getColumnsMap().get(filters.get(i).getColumn().getName());
			if (column != null)
				filterAtCol[column.getLocation()] = i;
		}
		String indexedColumn = inner.getIndexColumn();

		//TODO: this should be RowGetter
		final TablesCalcFrame tg;
		if (additionClause != null) {
			tg = new TablesCalcFrame(new String[] { innerTable.getTable().getTitle() }, new Table[] { table });
		} else
			tg = null;
		final boolean canLimitBeforeGrouping = orderBysAfter.length == 0 && groupBys.length == 0 && (limit != -1 || limitOffset.value > 0);
		final boolean canLimitInline = canLimitBeforeGrouping && additionClause == null;

		//## Go Through Resultset (Each iteration is an applicable partition... And if there is an applicable index, that gets used)
		outer: while (limit != 0 && inner.next(canLimitInline && limit != -1 && filters.isEmpty() ? toIntNoOverflow(limitOffset.value + limit) : Integer.MAX_VALUE)) {
			if (timeoutController != null)
				timeoutController.throwIfTimedout();
			final AmiHdbPartition ct = inner.getCurrentPartition();
			int rowsCount = inner.getCurrentRowsCount();
			final Entry[] entries = new Entry[rowsCount];
			for (int i = 0; i < rowsCount; i++)
				entries[i] = new Entry(inner.getCurrentRowAt(i), inner.getIndexValueAt(i));

			if (planListener != null) {
				if (inner instanceof AmiHdbResultSet_WrapIndex) {
					AmiHdbResultSet_WrapIndex wInner = (AmiHdbResultSet_WrapIndex) inner;
					planListener.onStep("VISIT_PARTTION_IDX_PAIR", ct.getId() + " (key=" + SH.join(',', ct.getPartitionsKey()) + ") ON " + wInner.getIndexColumn() + "="
							+ wInner.getCurrentKey() + " WITH " + rowsCount + " ROW(S)");
				} else
					planListener.onStep("VISIT_PARTITION", ct.getId() + " (key=" + SH.join(',', ct.getPartitionsKey()) + ") WITH " + rowsCount + " ROW(S)");
			}

			Arrays.sort(entries, 0, rowsCount);
			int[] rows = new int[rowsCount];
			for (int i = 0; i < rowsCount; i++)
				rows[i] = entries[i].row;
			Comparable[] sinkVals = new Comparable[rowsCount];
			int filtersCount = filters.size();
			//## Loop through all columns that participate in the where clause trying to reduce rows accordingly.
			for (int x = 0; x < filtersCount; x++) {
				final boolean canShortCircuit = canLimitInline && x == filtersCount - 1 && limit != -1;
				if (timeoutController != null)
					timeoutController.throwIfTimedout();
				AmiHdbQueryPart fl = filters.get(x);
				AmiHdbPartitionColumn col = ct.getColumn(fl.getColumn().getName());
				col.readValues(rows, 0, rowsCount, 0, sinkVals);
				int outY = 0;
				for (int y = 0; y < rowsCount; y++) {
					Comparable val = sinkVals[y];
					if (fl.matches(val)) {
						Entry entry = entries[y];
						entries[outY] = entry;
						rows[outY] = entry.row;
						if (x == 0)
							entry.filterValues = new Comparable[filtersCount];
						entry.filterValues[x] = val;
						outY++;
						if (canShortCircuit && outY >= limit + limitOffset.value) {
							if (planListener != null)
								planListener.onStep("COLUMN_SCAN_SATISFIED", "COLUMN SCAN SATISFIED LIMIT, NO NEED TO KEEP SEARCHING");
							break;
						}
					}
				}
				rowsCount = outY;
				if (planListener != null)
					planListener.onStep("COLUMN_SCAN", "COLUMN '" + col.getName() + "' CLAUSE (" + fl + ") REDUCED RESULT TO " + rowsCount + " ROW(S)");
			}

			//## If the limit can be applied, do so by skipping first N records
			int rowsStart = 0;
			if (canLimitInline) {
				if (limitOffset.value > 0) {
					final long remove = Math.min(limitOffset.value, rowsCount);
					rowsStart += remove;
					rowsCount -= remove;
					limitOffset.value -= remove;
				}
				if (limit >= 0) {
					if (limit > rowsCount) {
						limit -= rowsCount;
					} else {
						rowsCount = limit;
						limit = 0;
					}
				}
			}
			int pos = table.getSize();
			table.newRows(rowsCount);
			long firstRow = ct.getFirstRow();
			if (rowNumCol != null)
				for (int i = 0; i < rowsCount; i++)
					rowNumCol.setLong(pos + i, rows[i + rowsStart] + firstRow);
			if (partitionCol != null)
				for (int i = 0; i < rowsCount; i++)
					partitionCol.setInt(pos + i, ct.getId());
			//## Fill in the other columns (those that aren't simply parts of the where clause)
			for (int x = 0; x < names.length; x++) {
				if (timeoutController != null)
					timeoutController.throwIfTimedout();
				int filterPos = filterAtCol[x];
				ColumnarColumn col = (ColumnarColumn) table.getColumnAt(x);
				AmiHdbPartitionColumn hcol = ct.getColumn((String) col.getId());
				if (hcol == null) {
					AmiHdbColumn t = innerTable.getColumn((String) col.getId());
					if (t instanceof AmiHdbColumn_Partition) {
						Comparable<?> value = ct.getPartitionsKey()[((AmiHdbColumn_Partition) t).getPartionIndex()];
						//						if (planListener != null)
						//							planListener.onStep("COLUMN_FILL", "COLUMN '" + col.getId() + "'  FAST_FILL " + rowsCount + " ROW(S) FROM PARTITION CONST");
						for (int i = 0; i < rowsCount; i++)
							col.setValue(pos + i, value);
					}
					continue;
				}
				Caster<Comparable> caster = col.getTypeCaster();
				if (OH.eq(col.getId(), indexedColumn)) {
					//					if (planListener != null)
					//						planListener.onStep("COLUMN_FILL", "COLUMN '" + col.getId() + "' FAST_FILL " + rowsCount + " ROW(S) FROM INDEX RESULT");
					for (int i = 0; i < rowsCount; i++)
						col.setValue(pos + i, caster.castNoThrow(entries[rowsStart + i].indexValue));
				} else if (filterPos != -1) {
					//					if (planListener != null)
					//						planListener.onStep("COLUMN_FILL", "COLUMN '" + col.getId() + "' FAST_FILL " + rowsCount + " ROW(S) FROM PRIOR SCAN");
					for (int i = 0; i < rowsCount; i++)
						col.setValue(pos + i, caster.castNoThrow(entries[rowsStart + i].filterValues[filterPos]));
				} else {
					if (planListener != null)
						planListener.onStep("COLUMN_READ", "COLUMN '" + col.getId() + "' READ " + rowsCount + " ROW(S)");
					hcol.readValues(rows, rowsStart, rowsCount, 0, sinkVals);
					for (int i = 0; i < rowsCount; i++)
						col.setValue(pos + i, caster.castNoThrow(sinkVals[i]));
				}
			}
			//## Apply additional where clauses, for example clauses that consider multiple columns, ex: qty<filled
			if (additionClause != null) {
				ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf, tg);
				for (int i = pos; i < table.getSize(); i++) {
					Row row = table.getRow(i);
					tg.currentRows[0] = row;
					if (!Boolean.TRUE.equals(additionClause.get(rsf))) {
						table.removeRow(i--);
					} else if (canLimitBeforeGrouping) {
						if (limitOffset.value > 0) {
							table.removeRow(i--);
							limitOffset.value--;
						} else if (limit >= 0 && i > limit) {
							break outer;
						}
					}
				}

			}
		}

		//## apply any pre-groupby orderbys
		if (orderBysBefore.length > 0) {
			Collections.sort(table.getRows(), new BasicRowComparatorForCalc(orderBysBefore, query.getOrderBysAscBefore(), new ReusableCalcFrameStack(sf)));
			if (planListener != null)
				planListener.onStep("ORDER_BY", "Ordered " + table.getSize() + " rows");
		}

		//## Build teh final table based on the actual select clauses
		DerivedCellCalculator selectCalcs[] = query.getSelectCalcs();
		//		RowGetter rgIn = new RowGetter(table, globalVars);

		//## Group the data up or just project to output table
		ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
		if (groupBys.length > 0) {
			//			RowGetter rgOut = new RowGetter(out, globalVars);
			AggCalculator[] aggregates = query.getAggregates();
			List<List<Row>> groupedRowsList = SqlProjector.groupBy(groupBys, limitOffset.value, limit, orderBysAfter.length > 0 || having != null, table, sf);
			if (planListener != null)
				planListener.onStep("GROUPING", "GROUPED " + table.getSize() + " ROWS INTO " + groupedRowsList.size() + " ROWS");
			int i = 0, l = groupedRowsList.size();
			for (; i < l; i++) {
				List<Row> groupingRows = groupedRowsList.get(i);
				for (AggCalculator ac : aggregates)
					ac.visitRows(rsf, groupingRows);
				CalcFrame firstRow;
				if (groupingRows.size() > 0) {
					firstRow = groupingRows.get(0);
				} else {
					if (!onEmptyAggIncludeOneRow)
						break;
					firstRow = EmptyCalcFrame.INSTANCE;//TODO: I think this should actaully be a row with all nulls?
				}
				rsf = new ReusableCalcFrameStack(sf, firstRow);
				Row row = out.newEmptyRow();
				for (int j = 0; j < selectCalcs.length; j++)
					row.putAt(j, selectCalcs[j].get(rsf));
				if (having != null) {
					rsf.reset(row);
					if (!Boolean.TRUE.equals(having.get(rsf)))
						continue;
				}
				out.getRows().add(row);
			}
		} else {
			int size = table.getSize();
			if (!canLimitInline && canLimitBeforeGrouping && limit != -1 && limitOffset.value == 0 && limit < size)
				size = limit;
			int start = out.getSize();
			for (int i = 0; i < size; i++) {
				Object[] values = new Object[selectCalcs.length];
				Row row = table.getRow(i);
				for (int x = 0; x < selectCalcs.length; x++) {
					rsf.reset(row);
					values[x] = selectCalcs[x].get(rsf);
				}
				out.getRows().addRow(values);
			}
			if (rowNumCol != null) {
				ColumnarColumnLong rowNumColOut = (ColumnarColumnLong) out.getColumn(query.getRowNumColumn());
				for (int i = 0; i < size; i++)
					rowNumColOut.setLong(start + i, rowNumCol.getLong(i));
			}
			if (partitionCol != null) {
				ColumnarColumnInt parNumColOut = (ColumnarColumnInt) out.getColumn(query.getPartitionColumn());
				for (int i = 0; i < size; i++)
					parNumColOut.setInt(start + i, partitionCol.getInt(i));
			}
		}
	}
	private static int toIntNoOverflow(long l) {
		return l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) l;
	}

	public static void queryJustCount(AmiHdbQueryImpl query, AmiHdbResultSet inner, List<AmiHdbQueryPart> filters, SqlPlanListener planListener,
			TimeoutController timeoutController, DerivedCellCalculator having, ColumnarTable out, CalcFrameStack sf) throws IOException {
		long totRows = 0;
		while (true) {
			int rowsCount = inner.nextOnlyCount();
			if (rowsCount == 0)
				break;
			if (timeoutController != null)
				timeoutController.throwIfTimedout();
			if (planListener != null) {
				AmiHdbPartition ct = inner.getCurrentPartition();
				if (inner instanceof AmiHdbResultSet_WrapIndex) {
					AmiHdbResultSet_WrapIndex wInner = (AmiHdbResultSet_WrapIndex) inner;
					planListener.onStep("COUNT_PARTTION_IDX_PAIR", ct.getId() + " (key=" + SH.join(',', ct.getPartitionsKey()) + ") ON " + wInner.getIndexColumn() + "="
							+ wInner.getCurrentKey() + " WITH " + rowsCount + " ROW(S)");
				} else
					planListener.onStep("COUNT_PARTITION", ct.getId() + " (key=" + SH.join(',', ct.getPartitionsKey()) + ") WITH " + rowsCount + " ROW(S)");
			}
			totRows += rowsCount;
		}
		DerivedCellCalculator selectCalcs[] = query.getSelectCalcs();
		AggCalculator[] aggregates = query.getAggregates();
		if (totRows == 0)
			return;
		for (AggCalculator ac : aggregates)
			ac.visitRows(sf, totRows);
		Row row = out.newEmptyRow();
		for (int j = 0; j < selectCalcs.length; j++)
			row.putAt(j, selectCalcs[j].get(sf));
		if (having != null) {
			if (Boolean.TRUE.equals(having.get(new ReusableCalcFrameStack(sf, row))))
				out.getRows().add(row);
		} else
			out.getRows().add(row);

	}

}
