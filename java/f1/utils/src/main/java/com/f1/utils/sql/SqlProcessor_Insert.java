package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.sql.SqlProcessorUtils.Limits;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.sqlnode.InsertNode;
import com.f1.utils.string.sqlnode.SqlForNode;
import com.f1.utils.string.sqlnode.ValuesNode;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.ColumnPositionMapping;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class SqlProcessor_Insert {
	private static final int MAX_LOOPSIZE = 1000000;

	final private SqlProcessor owner;

	private static final Logger log = LH.get();

	public SqlProcessor_Insert(SqlProcessor owner) {
		this.owner = owner;
	}
	public FlowControl processInsert(CalcFrameStack sf, DerivedCellCalculatorSql query, InsertNode node, Table toInsert) {
		final Table table = this.owner.getMutator().getTable(sf, node.getPosition(), node.getTablename(), node.getTablenameScope());
		final List<Column> columns;
		if (node.getColumnsCount() > 0) {
			columns = new ArrayList<Column>(node.getColumnsCount());
			Map<String, Column> columnsMap = table.getColumnsMap();
			Set<String> visited = new HashSet<String>(node.getColumnsCount());
			for (int i = 0; i < node.getColumnsCount(); i++) {
				Node c = node.getColumnAt(i);
				String name = c.toString();
				Column col = columnsMap.get(name);
				if (col == null)
					throw new ExpressionParserException(c.getPosition(), "Unknown column: " + name);
				if (!visited.add(name))
					throw new ExpressionParserException(c.getPosition(), "Duplicate column: " + name);
				columns.add(col);
			}
		} else {
			columns = table.getColumns();
		}
		Limits limits = new SqlProcessorUtils.Limits(node.getLimit());
		if (limits.getLimitOffsetNode() != null)
			throw new ExpressionParserException(node.getLimit().getPosition(), "LIMIT offset not Support for INSERT");
		int limit = limits.getLimit(this.owner.getParser(), sf);
		final int[] syncOnTargetPos = getSyncOnColumns(node, table, columns);
		Node values = node.getNext();
		boolean byName = node.isByName();
		Object[][] rows;
		int columnsCount = table.getColumnsCount();
		if (!(values instanceof ValuesNode)) {

			//			Object o = owner.processTableReturningClause(query, (SqlNode) values, this.owner.getParser(), sf, null);
			int totSize = 0;
			//			if (o instanceof DerivedCellCalculatorSqlFlowControl) {
			//				DerivedCellCalculatorSqlFlowControl fc = ((DerivedCellCalculatorSqlFlowControl) o);
			//				fc.attachResponseHandler(new SqlProcessorResponseHandler_InsertToTable(sf, this.owner, query, node, columns, table, syncOnTargetPos));
			//				return fc;
			//			} else {
			//			Table toInsert = (Table) o;
			if (!byName) {
				if (toInsert.getColumnsCount() != columns.size())
					throw new ExpressionParserException(node.getPosition(),
							"column count mismatch: values contain " + toInsert.getColumnsCount() + " columns vs. target table has " + columns.size() + " column(s)");
			}
			totSize += toInsert.getSize();
			//			}
			int target = 0;
			rows = new Object[totSize][];
			if (byName) {
				TableList rows2 = toInsert.getRows();
				Map<String, Column> c2 = toInsert.getColumnsMap();
				int matchingColumnsCount = 0;
				int mappingIn[] = new int[columnsCount];
				int mappingOut[] = new int[columnsCount];
				Caster<?> casters[] = new Caster[columnsCount];
				for (int i = 0; i < columns.size(); i++) {
					Column outCol = columns.get(i);
					Column inCol = c2.get(outCol.getId());
					if (inCol != null) {
						mappingIn[matchingColumnsCount] = inCol.getLocation();
						mappingOut[matchingColumnsCount] = outCol.getLocation();
						casters[matchingColumnsCount] = outCol.getTypeCaster();
						matchingColumnsCount++;
					}
				}
				for (int y = 0; y < rows.length; y++) {
					Row sourceRow = rows2.get(y);
					Object[] row = rows[target++] = new Object[columnsCount];
					for (int i = 0; i < matchingColumnsCount; i++) {
						Object value = sourceRow.getAt(mappingIn[i]);
						int x = mappingOut[i];
						row[x] = casters[i].cast(value, false, false);
					}
				}
			} else {
				int colsCount = columns.size();
				int colLocations[] = new int[colsCount];
				Caster<?>[] casters = new Caster<?>[colsCount];
				for (int x = 0; x < colsCount; x++) {
					Column col = columns.get(x);
					colLocations[x] = col.getLocation();
					casters[x] = col.getTypeCaster();
				}
				//				Table toInsert = (Table) o;
				TableList rows2 = toInsert.getRows();
				for (int y = 0; y < rows.length; y++) {
					Row sourceRow = rows2.get(y);
					Object[] row = rows[target++] = new Object[columnsCount];
					for (int x = 0; x < colsCount; x++) {
						Object value = sourceRow.getAt(x);
						row[colLocations[x]] = casters[x].cast(value, false, false);
					}
				}
			}
		} else {
			ValuesNode vn = JavaExpressionParser.castNode(values, ValuesNode.class);
			rows = processValues(sf, vn, columnsCount, columns, node.getForloop(), limit);
		}
		return doInsert(sf, query, node, table, node.getTablenamePosition(), columns, syncOnTargetPos, rows);
	}
	public Object[][] processValues(CalcFrameStack sf, ValuesNode vn, int columnsCount, List<Column> columns, SqlForNode forloop, int limit) {
		int valsColsCount = vn.getColCount();
		if (columns != null && valsColsCount != columns.size())
			throw new ExpressionParserException(vn.getPosition(),
					"column count mismatch: values contain " + valsColsCount + " columns vs. target table has " + columns.size() + " column(s)");
		final int valsCount = vn.getValuesCount();
		//		Node[] vals = vn.values;

		//		RowGetter vars = new RowGetter(null, EmptyTypes.INSTANCE, sf.getGlobalVars());
		int valsRows = valsCount / valsColsCount;
		DerivedCellCalculator[] valsCalcs = new DerivedCellCalculator[valsCount];

		Object[][] rows;
		if (forloop != null) {
			SqlForNode fl = forloop;
			List<ForLoop> forLoops = new ArrayList<ForLoop>();
			BasicTable m = new BasicTable();
			while (forloop != null) {
				Number start = toNumber(sf, forloop.getStart(), "START");
				Number end = toNumber(sf, forloop.getEnd(), "END");
				Number step = 1;
				if (forloop.getStep() != null)
					step = toNumber(sf, forloop.getStep(), "STEP");
				String varname = forloop.getVarname().getVarname();
				Class<?> type = SqlProcessorUtils.getWidest(SqlProcessorUtils.getWidest(start.getClass(), end.getClass()), step.getClass());
				PrimitiveMath math = PrimitiveMathManager.INSTANCE.get(type);
				if (math.compare(step, 0) <= 0)
					throw new ExpressionParserException(forloop.getStep().getPosition(), "STEP must be a positive number: " + step);
				ForLoop loop = new ForLoop(start, end, step, varname, math, forLoops.size());
				forLoops.add(loop);
				m.addColumn(loop.math.getReturnType(), loop.varname);
				forloop = ((forloop.getNext() instanceof SqlForNode) ? (SqlForNode) forloop.getNext() : null);
			}
			Row row = m.newEmptyRow();
			m.getRows().add(row);
			ReusableCalcFrameStack vars = new ReusableCalcFrameStack(sf, row);
			//			vars = new RowGetter(m, sf.getGlobalVars());
			//			vars.reset(row);
			for (int i = 0; i < valsCount; i++)
				valsCalcs[i] = this.owner.getParser().toCalc(vn.getValueAt(i), vars);
			int cnt = 1;
			final int maxDepth = forLoops.size() - 1;
			for (int i = 0; i <= maxDepth; i++) {
				final ForLoop loop = forLoops.get(i);
				int c = 0;
				while (loop.next() != null)
					if ((c += cnt) > MAX_LOOPSIZE)
						throw new ExpressionParserException(fl.getPosition(), "For loop execution failed: Too many cycles (" + MAX_LOOPSIZE + " max) in loop");
				cnt = c;
			}
			for (int depth = 0; depth < forLoops.size(); depth++)
				forLoops.get(depth).reset();
			int size = cnt * valsRows;
			if (limit != -1 && size > limit)
				size = limit;
			rows = new Object[size][];
			int offset = 0;
			ForLoop loop = forLoops.get(0);
			for (int depth = 0; offset < size;) {
				Number n = loop.next();
				boolean hasnext = n != null;
				if (hasnext) {
					row.putAt(loop.index, n);
					if (depth == maxDepth) {
						insertValues(vars, columnsCount, columns, offset, rows, valsColsCount, valsCalcs);
						offset += valsRows;
					} else
						loop = forLoops.get(++depth);
				} else {
					if (depth == 0)
						break;
					loop.reset();
					loop = forLoops.get(--depth);
				}
			}
		} else {
			for (int i = 0; i < valsCount; i++)
				valsCalcs[i] = owner.getParser().toCalc(vn.getValueAt(i), sf);
			int size = valsRows;
			if (limit != -1 && size > limit)
				size = limit;
			rows = new Object[size][];
			insertValues(sf, columnsCount, columns, 0, rows, valsColsCount, valsCalcs);
		}
		return rows;
	}
	public static int[] getSyncOnColumns(InsertNode node, Table target, final List<Column> columns) {
		final int[] syncOnTargetPos;
		if (node.getSynsOnsCount() > 0) {
			syncOnTargetPos = new int[node.getSynsOnsCount()];
			if (node.getOperation() != SqlExpressionParser.ID_SYNC)
				throw new ExpressionParserException(node.getPosition(), "ON(..) only support for SYNC operation");
			//			else if (node.syncOns.length == 0)
			//				throw new ExpressionParserException(node.getPosition(), "ON(..) must have at least one column");
			Map<Object, Integer> columnsMap = new HashMap<Object, Integer>();
			for (Column i : columns)
				columnsMap.put(i.getId(), i.getLocation());
			Set<String> visited = new HashSet<String>(node.getSynsOnsCount());
			for (int i = 0; i < syncOnTargetPos.length; i++) {
				Node c = node.getSyncOn(i);
				String name = c.toString();
				Integer col = columnsMap.get(name);
				if (col == null)
					throw new ExpressionParserException(c.getPosition(),
							"Unknown column in ON clause: " + name + " (available columns: " + SH.join(',', columnsMap.keySet()) + ")");
				if (!visited.add(name))
					throw new ExpressionParserException(c.getPosition(), "Duplicate column in ON clause: " + name);
				syncOnTargetPos[i] = col.intValue();
			}
		} else if (node.getOperation() == SqlExpressionParser.ID_SYNC) {
			syncOnTargetPos = new int[columns.size()];
			for (int i = 0; i < syncOnTargetPos.length; i++)
				syncOnTargetPos[i] = columns.get(i).getLocation();
		} else {
			syncOnTargetPos = null;
		}
		return syncOnTargetPos;
	}
	public FlowControl doInsert(CalcFrameStack sf, DerivedCellCalculatorSql query, InsertNode node, final Table table, int tableNamePos, final List<Column> columns,
			final int[] syncOnTargetPos, Object[][] rows) {
		SqlProcessorTableMutator mutator = owner.getMutator();
		int[] suppliedColPositions = new int[columns.size()];
		for (int i = 0; i < suppliedColPositions.length; i++)
			suppliedColPositions[i] = columns.get(i).getLocation();
		if (node.getOperation() == SqlExpressionParser.ID_SYNC) {
			List<Object[]> inserts = new ArrayList<Object[]>();
			List<Row> deletes = new ArrayList<Row>();
			List<Row> updateSources = new ArrayList<Row>();
			List<Object[]> updateTargets = new ArrayList<Object[]>();
			int colsCount = columns.size() - syncOnTargetPos.length;
			int colLocationsForUpdate[] = new int[colsCount];
			for (int x = 0, n = 0; x < columns.size(); x++) {
				int loc = columns.get(x).getLocation();
				if (AH.indexOf(loc, syncOnTargetPos) == -1)
					colLocationsForUpdate[n++] = loc;
			}
			processSync(syncOnTargetPos, table, rows, colLocationsForUpdate, inserts, deletes, updateSources, updateTargets);
			//						if (!deletes.isEmpty()) {
			if (deletes.size() == table.getSize())
				mutator.processRowRemoveAll(sf, table);
			else
				mutator.processRowRemoves(sf, table, deletes);
			//			}
			if (!inserts.isEmpty())
				mutator.processRowAdds(sf, table, tableNamePos, suppliedColPositions, AH.toArray(inserts, Object[].class), node.isReturnGeneratedKeys());
			if (!updateTargets.isEmpty())
				mutator.processRowUpdate(sf, table, tableNamePos, updateSources, colLocationsForUpdate, AH.toArray(updateTargets, Object[].class));
			if (LH.isFine(log))
				LH.fine(log, "SYNC for table ", table.getTitle(), ": ", deletes.size() + " DELETES, " + updateSources.size() + " UPDATES, " + inserts.size(), " INSERTS");
			return new TableReturn((long) deletes.size() + updateSources.size() + inserts.size());
		} else {
			return mutator.processRowAdds(sf, table, tableNamePos, suppliedColPositions, rows, node.isReturnGeneratedKeys());
		}
	}
	private Number toNumber(CalcFrameStack sf, Node node, String description) {
		Object r = owner.getParser().toCalc(node, sf).get(sf);
		if (r instanceof Number)
			return (Number) r;
		throw new ExpressionParserException(node.getPosition(), description + " value must be a constant number: " + r);
	}

	public void insertValues(CalcFrameStack sf, int tableColsCount, List<Column> columns, int offset, Object[][] rows, int valsCols, DerivedCellCalculator[] valsCalcs) {
		int valsCount = valsCalcs.length / valsCols;
		for (int y = 0; y < valsCount; y++) {
			if (y + offset >= rows.length)
				break;
			Object[] row = rows[y + offset] = new Object[tableColsCount];
			Column col;
			Caster<?> caster;
			if (columns == null) {
				for (int x = 0; x < row.length; x++) {
					DerivedCellCalculator c = valsCalcs[x + y * valsCols];
					row[x] = c.get(sf);
				}
			} else {
				for (int x = 0; x < columns.size(); x++) {
					col = columns.get(x);
					caster = col.getTypeCaster();
					DerivedCellCalculator c = valsCalcs[x + y * valsCols];
					Object v = c.get(sf);
					if (v != null) {
						Object v2 = caster.cast(v, false, false);
						if (v2 == null && !"null".equals(v))
							throw new ExpressionParserException(c.getPosition(),
									"Invalid value for column " + col.getId() + " of type " + sf.getFactory().forType(col.getType()) + " ==> " + v);
						row[col.getLocation()] = v2;
					} else
						row[col.getLocation()] = null;
				}
			}
		}
	}
	protected void processSync(int[] keyColPos, Table table, Object[][] rows, int updateColPos[], List<Object[]> insertSinks, List<Row> deleteSinks, List<Row> updateSourceSink,
			List<Object[]> updateTargetSink) {
		if (rows.length == 0) {
			deleteSinks.addAll(table.getRows());
		} else if (table.getSize() == 0) {
			for (Object[] o : rows)
				insertSinks.add(o);
		} else {
			boolean fullyIndexed = keyColPos.length == table.getColumnsCount();
			int[] cols = AH.appendArray(keyColPos, updateColPos);
			List<Sync> ordered = build(table.getRows(), CH.l(rows), cols);
			if (!fullyIndexed) {
				List<Row> oldList = new ArrayList<Row>();
				List<Object[]> nuwList = new ArrayList<Object[]>();
				for (Sync s : ordered) {
					int oldSize = s.getOldSize();
					int nuwSize = s.getNuwSize();
					int min = Math.min(oldSize, nuwSize);
					for (int n = min; n < nuwSize; n++)
						nuwList.add(s.getNuw(n));
					for (int n = min; n < oldSize; n++)
						oldList.add(s.getOld(n));
				}
				ordered = build(oldList, nuwList, keyColPos);
			}

			for (Sync s : ordered) {
				int oldSize = s.getOldSize();
				int nuwSize = s.getNuwSize();
				int min = Math.min(oldSize, nuwSize);
				if (min > 0 && !fullyIndexed) {
					for (int n = 0; n < min; n++) {
						Row sr = s.getOld(n);
						Object[] tr = s.getNuw(n);
						Object[] tg = new Object[updateColPos.length];
						for (int i = 0; i < updateColPos.length; i++)
							tg[i] = tr[updateColPos[i]];
						updateSourceSink.add(sr);
						updateTargetSink.add(tg);
					}
				}
				for (int n = min; n < nuwSize; n++)
					insertSinks.add(s.getNuw(n));
				for (int n = min; n < oldSize; n++)
					deleteSinks.add(s.getOld(n));
			}
		}
	}
	private List<Sync> build(List<Row> source, List<Object[]> rows, int[] syncOnTargetPos) {
		List<Sync> ordered = new ArrayList<Sync>();
		int keySize = syncOnTargetPos.length;
		Object tmpKey[] = new Object[keySize];

		HasherMap<Object[], Sync> map = new HasherMap<Object[], Sync>(ArrayHasher.INSTANCE);

		for (Row i : source) {
			for (int x = 0; x < keySize; x++)
				tmpKey[x] = i.getAt(syncOnTargetPos[x]);
			Entry<Object[], Sync> entry = map.getOrCreateEntry(tmpKey);
			Sync t = entry.getValue();
			if (t == null) {
				entry.setValue(t = new Sync());
				tmpKey = new Object[keySize];
				ordered.add(t);
			}
			t.add(i);
		}
		for (Object[] i : rows) {
			for (int x = 0; x < keySize; x++)
				tmpKey[x] = i[syncOnTargetPos[x]];
			Entry<Object[], Sync> entry = map.getOrCreateEntry(tmpKey);
			Sync t = entry.getValue();
			if (t == null) {
				entry.setValue(t = new Sync());
				tmpKey = new Object[keySize];
				ordered.add(t);
			}
			t.add(i);
		}
		return ordered;
	}

	public static class ForLoop {

		final private Number start, step, end;
		final String varname;
		final PrimitiveMath<?> math;
		final int index;
		final private boolean reverse;
		private Number n;

		public ForLoop(Number start, Number end, Number step, String varname, PrimitiveMath<?> math, int i) {
			this.start = start;
			this.n = start;
			this.end = end;
			this.varname = varname;
			this.math = math;
			this.index = i;
			this.reverse = math.compare(start, end) > 0;
			if (reverse)
				this.step = math.subtract(0, step);
			else
				this.step = step;
		}

		public Number next() {
			Number r = n;
			if (r != null) {
				n = math.add(n, step);
				if (math.compare(reverse ? end : n, reverse ? n : end) > 0) {
					n = null;
				}
			}
			return r;
		}
		public void reset() {
			n = math.cast(start);
		}

	}

	private static class Sync {
		private Row oldFirst = null;
		private Object[] nuwFirst = null;
		private List<Row> oldAdditional;
		private List<Object[]> nuwAdditional;

		public void add(Row i) {
			if (oldFirst == null)
				oldFirst = i;
			else {
				if (oldAdditional == null)
					oldAdditional = new ArrayList<Row>();
				oldAdditional.add(i);
			}

		}
		public void add(Object[] i) {
			if (nuwFirst == null)
				nuwFirst = i;
			else {
				if (nuwAdditional == null)
					nuwAdditional = new ArrayList<Object[]>();
				nuwAdditional.add(i);
			}
		}
		public int getOldSize() {
			return oldFirst == null ? 0 : (oldAdditional == null ? 1 : (oldAdditional.size() + 1));
		}
		public int getNuwSize() {
			return nuwFirst == null ? 0 : (nuwAdditional == null ? 1 : (nuwAdditional.size() + 1));
		}
		public Row getOld(int i) {
			return i == 0 ? oldFirst : oldAdditional.get(i - 1);
		}
		public Object[] getNuw(int i) {
			return i == 0 ? nuwFirst : nuwAdditional.get(i - 1);
		}

	}

	//	public void getDependencies(DerivedCellParserContext context, InsertNode node, Set<Object> sink) {
	//		Node values = node.getNext();
	//		if (values instanceof ValuesNode) {
	//			ValuesNode vn = JavaExpressionParser.castNode(values, ValuesNode.class);
	//			getDependencies2(context, vn, node.getForloop(), sink);
	//		} else {
	//			owner.getDependencyIds(context, (SqlNode) values, sink);
	//		}
	//	}
	//	public void getDependencies2(DerivedCellParserContext context, ValuesNode vn, SqlForNode forloop, Set<Object> sink) {
	//		//		Node[] vals = vn.values;
	//		int valsCount = vn.getValuesCount();
	//
	//		if (forloop != null) {
	//			while (forloop != null) {
	//				getDependenciesForNumber(context, forloop.getStart(), sink);
	//				getDependenciesForNumber(context, forloop.getEnd(), sink);
	//				if (forloop.getStep() != null)
	//					getDependenciesForNumber(context, forloop.getStep(), sink);
	//				forloop = ((forloop.getNext() instanceof SqlForNode) ? (SqlForNode) forloop.getNext() : null);
	//			}
	//			for (int i = 0; i < valsCount; i++)
	//				DerivedHelper.getDependencyIds(this.owner.getParser().toCalc(vn.getValueAt(i), context), sink);
	//		} else {
	//			for (int i = 0; i < valsCount; i++)
	//				DerivedHelper.getDependencyIds(owner.getParser().toCalc(vn.getValueAt(i), context), sink);
	//		}
	//	}
	//	private void getDependenciesForNumber(DerivedCellParserContext context, Node node, Set<Object> sink) {
	//		DerivedHelper.getDependencyIds(owner.getParser().toCalc(node, context), sink);
	//	}
	public FlowControl doInsert(CalcFrameStack sf, DerivedCellCalculatorSql node, Table targetTable, int tableNamePos, ColumnPositionMapping columnMapping, int startRow,
			int rowsCount, Table data, boolean returnGeneratedIds) {
		SqlProcessorTableMutator mutator = owner.getMutator();
		return mutator.processRowAdds(sf, targetTable, tableNamePos, columnMapping, startRow, rowsCount, data, returnGeneratedIds);
	}
}
