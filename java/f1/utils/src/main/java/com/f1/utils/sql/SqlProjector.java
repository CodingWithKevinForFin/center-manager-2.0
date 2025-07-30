package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.BasicTypes;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Pointer;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.AH;
import com.f1.utils.BasicPointer;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Int;
import com.f1.utils.sql.SqlProcessorSelectPlanner.IndexDef;
import com.f1.utils.sql.aggs.AbstractAggCalculator;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.sqlnode.OnNode;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicRowComparator;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarColumnBoolean;
import com.f1.utils.structs.table.columnar.ColumnarColumnByte;
import com.f1.utils.structs.table.columnar.ColumnarColumnChar;
import com.f1.utils.structs.table.columnar.ColumnarColumnDouble;
import com.f1.utils.structs.table.columnar.ColumnarColumnFloat;
import com.f1.utils.structs.table.columnar.ColumnarColumnInt;
import com.f1.utils.structs.table.columnar.ColumnarColumnLong;
import com.f1.utils.structs.table.columnar.ColumnarColumnShort;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.columnar.ColumnarTableList;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorAssignment;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.online.OnlineTableList;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcFrameTuple2;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.TablesCalcFrame;

public class SqlProjector {

	public static final String EMPTY_TABLE_AGG = System.getProperty("f1.sqlprojector.empty.table.agg", "");
	public static final boolean EmptyTableAggIsLegacy = "legacy".equals(EMPTY_TABLE_AGG);

	public static Table project(DerivedCellCalculatorSql query, Map<String, Table> asTables, String names[], DerivedCellCalculator select[], int joinType,
			DerivedCellCalculator join, DerivedCellCalculator joinNearest, DerivedCellCalculator where, DerivedCellCalculator[] groupby, DerivedCellCalculator having,
			OnNode[] unpacks, DerivedCellCalculator[] orderBys, boolean[] isAscending, int limitOffset, int limit, SqlProcessor processor, CalcFrameStack sf,
			CalcTypesStack context) {
		boolean implicitGroupby;
		AggregateFactory factory2 = (AggregateFactory) context.getFactory();
		SqlPlanListener planListener = sf.getSqlPlanListener();
		if (groupby == null && factory2.getAggregatesCount() > 0) {
			groupby = new DerivedCellCalculator[] { new DerivedCellCalculatorConst(0, true) };
			implicitGroupby = true;
		} else
			implicitGroupby = false;

		if (groupby == null) {
			if (having != null)
				throw new ExpressionParserException(having.getPosition(), "having clause requires group-by clause");
			return SqlProjector.project(query, asTables, names, select, joinType, join, joinNearest, where, unpacks, orderBys, isAscending, limitOffset, limit, processor, null,
					sf);
		} else {
			ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
			boolean hasHavingOrOrderBy = orderBys != null || having != null;
			//////////////////////////
			// DETERMINE UNDERLYING DEPENDENCIES
			//////////////////////////
			final Object[] names2;
			AggCalculator[] aggregates = AH.toArray(factory2.getAggregates(), AggCalculator.class);
			{
				Set<Object> dependencies = new HashSet<Object>();
				if (orderBys != null)
					for (int i = 0; i < orderBys.length; i++)
						DerivedHelper.getDependencyIds(orderBys[i], dependencies);
				if (having != null)
					DerivedHelper.getDependencyIds(having, dependencies);

				//we may have references to post-aggregate columns, those don't count as dependencies to underlying table
				for (String name : names)
					dependencies.remove(name);

				//however, anything referenced inside an aggregate must use the underlying table
				for (AggCalculator ac : aggregates) {
					DerivedHelper.getDependencyIds(ac, dependencies);
				}

				for (DerivedCellCalculator i : select)
					if (i instanceof DerivedCellCalculatorAssignment)
						DerivedHelper.getDependencyIds(((DerivedCellCalculatorAssignment) i).getRight(), dependencies);
					else
						DerivedHelper.getDependencyIds(i, dependencies);

				for (DerivedCellCalculator i : groupby)
					DerivedHelper.getDependencyIds(i, dependencies);

				names2 = AH.toArray((Collection) dependencies, Object.class);
			}
			if (where != null) {
				AbstractAggCalculator agg = DerivedHelper.findFirst(where, AbstractAggCalculator.class);
				if (agg != null)
					throw new ExpressionParserException(agg.getPosition(), "Invalid use of aggregation function: " + agg.getMethodName() + "(....)");
			}

			// PROCESS ORDER BY
			//////////////////////////
			DerivedCellCalculator[] beforeOrderByColumns = DerivedHelper.EMPTY_ARRAY;
			DerivedCellCalculator[] afterOrderByColumns = DerivedHelper.EMPTY_ARRAY;
			DerivedCellCalculator[] afterOrderByColumnsExtra = DerivedHelper.EMPTY_ARRAY;
			int[] afterOrderByColumnsExtraPos = OH.EMPTY_INT_ARRAY;
			int[] afterOrderByColumnsPos = OH.EMPTY_INT_ARRAY;
			boolean[] beforeOrderByColumnsAscending = OH.EMPTY_BOOLEAN_ARRAY;
			boolean[] afterOrderByColumnsAscending = OH.EMPTY_BOOLEAN_ARRAY;
			final Table r;
			if (orderBys != null) {
				for (int i = 0; i < orderBys.length; i++) {
					DerivedCellCalculator ob = orderBys[i];
					boolean oba = isAscending[i];
					int idx = AH.indexOf(ob, select);
					if (idx == -1 && ob instanceof DerivedCellCalculatorRef) {
						Object name = ((DerivedCellCalculatorRef) ob).getId();
						idx = AH.indexOf(name, names);
					}

					//if it doesn't reference an ouput column, contain an aggregate or use a group by.
					if (idx == -1 && DerivedHelper.findFirst(ob, AbstractAggCalculator.class) == null) {
						beforeOrderByColumns = AH.append(beforeOrderByColumns, ob);
						beforeOrderByColumnsAscending = AH.append(beforeOrderByColumnsAscending, oba);
					} else {
						if (idx == -1) {
							idx = select.length + afterOrderByColumnsExtra.length;
							afterOrderByColumnsExtra = AH.append(afterOrderByColumnsExtra, ob);
							afterOrderByColumnsExtraPos = AH.append(afterOrderByColumnsExtraPos, idx);
						}
						afterOrderByColumns = AH.append(afterOrderByColumns, ob);
						afterOrderByColumnsPos = AH.append(afterOrderByColumnsPos, idx);
						afterOrderByColumnsAscending = AH.append(afterOrderByColumnsAscending, oba);
					}
				}
			}

			//////////////////////////
			// BUILD UNDERLYING TABLE AND PROJECT DEPENDENCIES
			//////////////////////////
			boolean canShortCircuit;
			final Table underlyingTable;
			{
				DerivedCellCalculator select2[] = new DerivedCellCalculator[names2.length];
				Table firstTable = CH.first(asTables.values());
				canShortCircuit = beforeOrderByColumns.length == 0 && !(firstTable.getRows() instanceof OnlineTableList) && asTables.size() == 1
						&& (where == null || (where.isConst() && Boolean.TRUE.equals(where.get(null))));
				for (int i = 0; i < names2.length; i++) {
					Column col = findColumn(sf, asTables, names2[i]);
					if (col == null) {
						select2[i] = processor.toCalc((String) names2[i], sf);
						continue;
						//						throw new ExpressionParserException(query.getPosition(), "Unknown column name: " + names2[i]);
					}
					if (canShortCircuit && findColumn(firstTable, names2[i]) == null) {
						canShortCircuit = false;
					}
					select2[i] = new DerivedCellCalculatorRef(0, col.getType(), names2[i]);
				}
				List<DerivedCellCalculatorRef> sink = new ArrayList<DerivedCellCalculatorRef>();
				for (int i = 0; i < select.length; i++) {
					DerivedHelper.find(select[i], DerivedCellCalculatorRef.class, sink);
				}
				if (canShortCircuit) {
					underlyingTable = firstTable;

				} else
					underlyingTable = SqlProjector.project(query, asTables, names2, select2, joinType, join, joinNearest, where, null,
							beforeOrderByColumns.length == 0 ? null : beforeOrderByColumns, beforeOrderByColumnsAscending.length == 0 ? null : beforeOrderByColumnsAscending, 0, -1,
							processor, null, sf);
			}

			//////////////////////////
			// PROCESS GROUP BY
			//////////////////////////
			List<DerivedCellCalculatorRef> sink2 = new ArrayList<DerivedCellCalculatorRef>();
			for (DerivedCellCalculator gb : groupby)
				DerivedHelper.find(gb, DerivedCellCalculatorRef.class, sink2);
			if (canShortCircuit) {
				for (DerivedCellCalculatorRef i : sink2) {
					Object id = i.getId();
					Column col;
					if (id instanceof NameSpaceIdentifier)
						col = underlyingTable.getColumn(((NameSpaceIdentifier) id).getVarName());
					else
						col = underlyingTable.getColumn((String) id);
					i.setLoc(col.getLocation());
				}
			} else {
				for (DerivedCellCalculatorRef i : sink2) {
					int loc = AH.indexOf(i.getId(), names2);
					OH.assertNe(loc, -1);
					i.setLoc(loc);
				}
			}
			List<List<Row>> groupedRowsList = groupBy(groupby, limitOffset, limit, hasHavingOrOrderBy, underlyingTable, sf);
			if (planListener != null)
				planListener.onStep("GROUPING", "Grouped " + underlyingTable.getSize() + " rows into " + groupedRowsList.size() + " buckets");

			//////////////////////////
			{
				final String[] names3 = new String[select.length + afterOrderByColumnsExtra.length];
				final Class[] types3 = new Class[names3.length];
				List<DerivedCellCalculatorRef> sink = new ArrayList<DerivedCellCalculatorRef>();
				for (int i = 0; i < select.length; i++) {
					DerivedHelper.find(select[i], DerivedCellCalculatorRef.class, sink);
					types3[i] = select[i].getReturnType();
					names3[i] = names[i];
				}
				for (int i = 0; i < afterOrderByColumnsExtra.length; i++) {
					int idx = afterOrderByColumnsExtraPos[i];
					types3[idx] = afterOrderByColumnsExtra[i].getReturnType();
					names3[idx] = "!!sort" + i;
				}
				if (canShortCircuit) {
					for (DerivedCellCalculatorRef i : sink) {
						Object id = i.getId();
						Column col;
						if (id instanceof NameSpaceIdentifier)
							col = underlyingTable.getColumn(((NameSpaceIdentifier) id).getVarName());
						else
							col = underlyingTable.getColumn((String) id);
						i.setLoc(col.getLocation());
					}
				} else {
					for (DerivedCellCalculatorRef i : sink) {
						int loc = AH.indexOf(i.getId(), names2);
						OH.assertNe(loc, -1);
						i.setLoc(loc);
					}
				}
				r = newBasicTable(types3, names3);
			}
			if (r != null) {
				if (asTables.size() > 1)
					r.setTitle(SH.join("_", asTables.keySet()));
				else
					r.setTitle(CH.first(asTables.keySet()));
			}

			//////////////////////////
			// BUILD AGGREGATE TABLE
			//////////////////////////
			final boolean canLimitInline = orderBys == null && unpacks == null;
			{
				final TableList rRows = r.getRows();
				if (limit == 0 || limitOffset >= groupedRowsList.size() || underlyingTable.getSize() == 0) {
					if (EmptyTableAggIsLegacy != true)
						if (implicitGroupby && (limit != 0) && underlyingTable.getSize() == 0) {
							Row row = r.newEmptyRow();
							for (AggCalculator ac : aggregates)
								ac.visitRows(rsf, Collections.EMPTY_LIST);
							for (int j = 0; j < select.length; j++)
								row.putAt(j, select[j].get(sf));
							r.getRows().add(row);
						}
					return removeTempSortingColumns(r, names);
				}

				//				RowGetter rg = new RowGetter(underlyingTable, globalVars);
				if (hasHavingOrOrderBy) {
					CalcFrameTuple2 mbm = new CalcFrameTuple2(null, null);
					//					Frame[] maps = new Frame[2];
					//					MapsBackedMap<String, Object> mbm = new MapsBackedMap<String, Object>(false, maps);
					Row row = null;
					outer: for (int i = 0, l = groupedRowsList.size(); i < l; i++) {
						List<Row> groupingRows = groupedRowsList.get(i);
						for (AggCalculator ac : aggregates)
							ac.visitRows(rsf, groupingRows);
						Row firstRow = groupingRows.get(0);
						rsf.reset(firstRow);
						if (row == null)
							row = r.newEmptyRow();
						for (int j = 0; j < select.length; j++)
							row.putAt(j, select[j].get(rsf));
						mbm.setFrame(row, firstRow);
						rsf.reset(mbm);
						if (having != null && !Boolean.TRUE.equals(having.get(rsf)))
							continue;
						for (int j = 0; j < afterOrderByColumnsExtra.length; j++)
							row.putAt(afterOrderByColumnsExtraPos[j], afterOrderByColumnsExtra[j].get(rsf));
						if (limitOffset > 0 && canLimitInline) {
							limitOffset--;
							continue;
						}
						rRows.add(row);
						row = null;
						if (canLimitInline && limit != -1 && --limit == 0)
							break outer;
					}
					if (row != null) { //this is a hack to remove the unused new record.
						rRows.add(row);
						rRows.remove(row);
					}
				} else {
					int i = 0, l = groupedRowsList.size();
					if (canLimitInline) {
						if (limitOffset > 0) {
							i = Math.min(l, i + limitOffset);
							limitOffset = Math.max(0, limitOffset - l);
						}
						if (limit != -1) {
							int remaining = l - i;
							l = Math.min(i + limit, l);
							limit = Math.max(0, limit - remaining);
						}
					}
					for (; i < l; i++) {
						List<Row> groupingRows = groupedRowsList.get(i);
						for (AggCalculator ac : aggregates)
							ac.visitRows(rsf, groupingRows);
						Row firstRow = groupingRows.get(0);
						rsf.reset(firstRow);
						Row row = r.newEmptyRow();
						for (int j = 0; j < select.length; j++)
							row.putAt(j, select[j].get(rsf));
						rRows.add(row);
					}
				}

			}
			if (unpacks != null)
				unpack(r, unpacks, orderBys != null ? limit : -1, limitOffset);
			if (afterOrderByColumns.length != 0) {
				if (planListener != null)
					planListener.onStep("ORDER_GROUP_RESULTS_BY", "Sorting " + r.getRows().size() + " rows on " + SH.join(',', afterOrderByColumns) + " (needed "
							+ (r.getColumnsCount() - names.length) + " temp columns)");
				TableHelper.sort(r.getRows(), new BasicRowComparator(afterOrderByColumnsPos, afterOrderByColumnsAscending));
				removeTempSortingColumns(r, names);
			}
			if (!canLimitInline)
				trimTable(r, limitOffset, limit);
			return r;
		}

	}

	public static List<List<Row>> groupBy(DerivedCellCalculator[] groupby, long limitOffset, int limit, boolean ignoreLimit, final Table underlyingTable, CalcFrameStack sf) {
		List<List<Row>> groupedRowsList = new ArrayList<List<Row>>();
		{
			ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
			boolean isConst = true;
			for (int i = 0; i < groupby.length && isConst; i++)
				isConst = groupby[i].isConst();
			if (isConst) {
				groupedRowsList.add(underlyingTable.getRows());
			} else if (groupby.length == 1) {
				final HasherMap<Object, List<Row>> groupedRows = new HasherMap<Object, List<Row>>();
				long remaining = (limit == -1 || ignoreLimit) ? Integer.MAX_VALUE : (limit + limitOffset);
				DerivedCellCalculator gb = groupby[0];
				//				RowGetter rg = new RowGetter(underlyingTable, globalVars);
				for (Row row : underlyingTable.getRows()) {
					rsf.reset(row);
					Object tmp = gb.get(rsf);
					final HasherMap.Entry<Object, List<Row>> entry;
					if (remaining == 0) {
						entry = groupedRows.getEntry(tmp);
						if (entry == null)
							continue;
					} else
						entry = groupedRows.getOrCreateEntry(tmp);
					List<Row> rows = entry.getValue();
					if (rows == null) {
						remaining--;
						entry.setValue(rows = new ArrayList<Row>());
						groupedRowsList.add(rows);
					}
					rows.add(row);
				}
			} else {
				final HasherMap<Object, List<Row>> groupedRows = new HasherMap<Object, List<Row>>(ArrayHasher.INSTANCE);
				long remaining = (limit == -1 || ignoreLimit) ? Integer.MAX_VALUE : (limit + limitOffset);
				Object[] tmp = new Object[groupby.length];
				for (Row row : underlyingTable.getRows()) {
					rsf.reset(row);
					for (int i = 0; i < tmp.length; i++)
						tmp[i] = groupby[i].get(rsf);
					final HasherMap.Entry<Object, List<Row>> entry;
					if (remaining == 0) {
						entry = groupedRows.getEntry(tmp);
						if (entry == null)
							continue;
					} else
						entry = groupedRows.getOrCreateEntry(tmp);
					List<Row> rows = entry.getValue();
					if (rows == null) {
						remaining--;
						entry.setValue(rows = new ArrayList<Row>());
						groupedRowsList.add(rows);
						tmp = new Object[groupby.length];
					}
					rows.add(row);
				}
			}
		}
		return groupedRowsList;
	}

	public static Table project(final DerivedCellCalculatorSql query, final Map<String, Table> asTables, final Object names[], final DerivedCellCalculator select[],
			final int joinType, final DerivedCellCalculator joinTo, final DerivedCellCalculator joinNearest, final DerivedCellCalculator origWhere, final OnNode[] unpacks,
			DerivedCellCalculator orderBys[], final boolean[] isAscending, final int origLimitOffset, final int origLimit, final SqlProcessor processor, SqlProjectionVisitor pv,
			final CalcFrameStack sf) {

		final SqlProcessorTableMutator mutator = processor.getMutator();
		final SqlPlanListener planListener = sf.getSqlPlanListener();

		//////////////////////////
		//ORDER BY CLAUSE
		//////////////////////////
		int orderByIdx[];
		final ColumnarTable r;
		final int extraColumnsForOrderBySize;
		final Object namesIncludingExtraSortColumns[];
		final DerivedCellCalculator[] extraColumnsForOrderByArray;
		final Iterable<Row> inlineSort;
		if (orderBys != null && orderBys.length == 1 && orderBys[0] instanceof DerivedCellCalculatorRef && asTables.size() == 1 && joinTo == null) {
			final Table tgtTable = asTables.values().iterator().next();
			final Object id = ((DerivedCellCalculatorRef) orderBys[0]).getId();
			final String col;
			String tableAsName = CH.first(asTables.keySet());
			if (id instanceof NameSpaceIdentifier) {
				NameSpaceIdentifier nsi = (NameSpaceIdentifier) id;
				if (OH.eq(tableAsName, nsi.getNamespace()))
					col = nsi.getVarName();
				else
					col = null;
			} else
				col = (String) id;
			Set<Object> dependencyIds = DerivedHelper.getDependencyIds(origWhere);
			if (col != null) {
				boolean whereUsesIndex = false;
				for (Object o : dependencyIds) {
					String col2;
					if (o instanceof NameSpaceIdentifier) {
						NameSpaceIdentifier nsi = (NameSpaceIdentifier) o;
						if (OH.eq(tableAsName, nsi.getNamespace()))
							col2 = nsi.getVarName();
						else
							col2 = null;
					} else
						col2 = (String) o;
					if (col2 != null && mutator.hasIndex(sf, tgtTable, col2)) {
						whereUsesIndex = true;
						break;
					}
				}
				if (!whereUsesIndex) {//If the where uses an index fall back to the original index strategy
					Iterable<Row> idx = mutator.findSortIndex(sf, tgtTable, col, isAscending[0]);
					if (idx != null) {
						orderBys = null;
						inlineSort = idx;
						if (planListener != null)
							planListener.onStep("ORDER_BY_USING_INDEX", "On '" + id + "'");
					} else
						inlineSort = null;
				} else
					inlineSort = null;
			} else
				inlineSort = null;
		} else
			inlineSort = null;
		{
			final List<DerivedCellCalculator> extraColumnsForOrderBy;
			if (orderBys != null) {
				extraColumnsForOrderBy = new ArrayList<DerivedCellCalculator>(orderBys.length);
				orderByIdx = new int[orderBys.length];
				for (int i = 0; i < orderBys.length; i++) {
					int idx = AH.indexOf(orderBys[i].toString(), names);
					if (idx == -1) {
						idx = select.length + extraColumnsForOrderBy.size();
						extraColumnsForOrderBy.add(orderBys[i]);
					}
					if (!Comparable.class.isAssignableFrom(orderBys[i].getReturnType()))
						throw new ExpressionParserException(orderBys[i].getPosition(), "ORDER BY expression must evaluate to ordinal type");
					orderByIdx[i] = idx;
				}
			} else {
				orderByIdx = null;
				extraColumnsForOrderBy = Collections.EMPTY_LIST;
			}
			extraColumnsForOrderBySize = extraColumnsForOrderBy.size();
			//build table
			namesIncludingExtraSortColumns = new Object[select.length + extraColumnsForOrderBySize];
			final Class<?>[] types = new Class[namesIncludingExtraSortColumns.length];
			for (int i = 0; i < select.length; i++) {
				types[i] = select[i].getReturnType();
				if (types[i] == Object.class)
					throw new ExpressionParserException(select[i].getPosition(), "Explicit cast required: " + select[i]);
				namesIncludingExtraSortColumns[i] = names[i];
			}
			for (int i = 0; i < extraColumnsForOrderBySize; i++) {
				types[i + names.length] = extraColumnsForOrderBy.get(i).getReturnType();
				namesIncludingExtraSortColumns[i + names.length] = "!!sort" + i;
			}
			r = pv == null ? newBasicTable(types, namesIncludingExtraSortColumns) : null;
			extraColumnsForOrderByArray = new DerivedCellCalculator[extraColumnsForOrderBy.size()];
			for (int i = 0; i < extraColumnsForOrderByArray.length; i++)
				extraColumnsForOrderByArray[i] = extraColumnsForOrderBy.get(i);
		}
		final int tablesCount = asTables.size();
		if (tablesCount == 0 && r != null) {
			Object row[] = new Object[select.length];
			for (int i = 0; i < select.length; i++)
				row[i] = select[i].get(sf);
			r.getRows().addRow(row);
			r.setTitle("_");
			return r;
		}
		if (r != null) {
			if (tablesCount > 1)
				r.setTitle(SH.join("_", asTables.keySet()));
			else
				r.setTitle(CH.first(asTables.keySet()));
		}
		if (origLimit == 0)
			return removeTempSortingColumns(r, names);

		//////////////////////////
		// DETERMING DEFAULT TABLE NAME
		//////////////////////////

		//////////////////////////
		//JOIN CLAUSE
		//////////////////////////
		final DerivedCellCalculator whereAfterJoin;
		final boolean includeOuterLeft;
		final boolean includeOuterRight;
		final boolean includeInner;
		DerivedCellCalculator where;
		{
			if (joinTo != null) {
				whereAfterJoin = origWhere;
				where = joinTo;
			} else {
				whereAfterJoin = null;
				where = origWhere;
			}
			switch (joinType) {
				case SqlExpressionParser.ID_JOIN: {
					includeInner = true;
					includeOuterLeft = false;
					includeOuterRight = false;
					break;
				}
				case SqlExpressionParser.ID_LEFT_JOIN: {
					includeInner = true;
					includeOuterLeft = true;
					includeOuterRight = false;
					break;
				}
				case SqlExpressionParser.ID_OUTER_JOIN: {
					includeInner = true;
					includeOuterLeft = true;
					includeOuterRight = true;
					break;
				}
				case SqlExpressionParser.ID_RIGHT_JOIN: {
					flipRightLeftTables(asTables);
					includeInner = true;
					includeOuterLeft = true;//remember, we swap left and right
					includeOuterRight = false;
					break;
				}
				case SqlExpressionParser.ID_LEFT_ONLY_JOIN: {
					includeInner = false;
					includeOuterLeft = true;
					includeOuterRight = false;
					break;
				}
				case SqlExpressionParser.ID_OUTER_ONLY_JOIN: {
					includeInner = false;
					includeOuterLeft = true;
					includeOuterRight = true;
					break;
				}
				case SqlExpressionParser.ID_RIGHT_ONLY_JOIN: {
					flipRightLeftTables(asTables);
					includeInner = false;
					includeOuterLeft = true;//remember, we swap left and right
					includeOuterRight = false;
					break;
				}
				default: {
					includeInner = true;
					includeOuterLeft = false;
					includeOuterRight = false;
					break;
				}

			}
		}

		//////////////////////////
		// QUERY PLAN STEP 1 - PREFILTER 
		//////////////////////////

		Pointer<DerivedCellCalculator> wherePointer = where == null ? null : new BasicPointer<DerivedCellCalculator>(where);
		final TimeoutController timeoutController = sf.getTimeoutController();

		List<Tuple2<String, Iterable<Row>>> tableRows;
		BasicMultiMap.List<String, TempIndex> tableNamesToIndexes;
		if (inlineSort != null) {
			tableRows = new ArrayList<Tuple2<String, Iterable<Row>>>(1);
			tableRows.add(new Tuple2<String, Iterable<Row>>(CH.first(asTables.keySet()), inlineSort));
			tableNamesToIndexes = new BasicMultiMap.List<String, SqlProjector.TempIndex>();
		} else {
			List<Tuple2<String, List<Row>>> tableRows2;
			tableRows2 = (List) prefilter(query, origLimit, unpacks, orderByIdx, asTables, origLimitOffset, wherePointer, select, joinTo, processor, mutator, whereAfterJoin, sf);
			if (tableRows2 == null)
				return removeTempSortingColumns(r, names);

			//////////////////////////
			// QUERY PLAN STEP 2 - Determine indexes and order of loops
			//////////////////////////
			tableNamesToIndexes = determingIndexes(sf, asTables, wherePointer, tableRows2, processor, joinNearest != null || joinTo != null, !includeOuterLeft);
			tableRows = (List) tableRows2;
		}

		if (joinNearest != null) {

			final Nearest nearest = processNearest(joinNearest, asTables, sf);
			Table targetTable = asTables.get(nearest.rightTable);
			DerivedCellCalculator sourceDcc = nearest.left;
			DerivedCellCalculator targetDcc = nearest.right;
			//			setColumnRefLocs(sourceDcc, nearest.leftTable, sourceTable, sf);
			//			setColumnRefLocs(targetDcc, nearest.rightTable, targetTable, sf);

			//			Column srcColumn = sourceTable.getColumn(nearest.leftColumn);
			//			Column tgtColumn = targetTable.getColumn(nearest.rightColumn);
			//			int srcColumnLocation = nearest.leftColumn == null ? -1 : sourceTable.getColumn(nearest.leftColumn).getLocation();
			//			int tgtColumnLocation = nearest.rightColumn == null ? -1 : targetTable.getColumn(nearest.rightColumn).getLocation();
			Caster caster = OH.getCaster(getBaseType(nearest.left.getReturnType(), nearest.right.getReturnType()));
			if (!Comparable.class.isAssignableFrom(caster.getCastToClass()))
				throw new ExpressionParserException(where.getPosition(), "NEAREST JOIN ON only supports sortable data, not " + caster.getCastToClass().getSimpleName() + "s");
			if (nearest.type == DerivedCellCalculatorMath.TYPE_STRING_EQ && !Number.class.isAssignableFrom(caster.getCastToClass()))
				throw new ExpressionParserException(where.getPosition(), "NEAREST JOIN ON == only supports numeric ordinals, not " + caster.getCastToClass().getSimpleName() + "s");
			TempIndex innerIndex;
			if (tableNamesToIndexes == null || tableNamesToIndexes.isEmpty()) {
				innerIndex = null;
			} else if (tableNamesToIndexes.size() == 1) {
				List<TempIndex> t = tableNamesToIndexes.get(nearest.leftTable);
				if (t != null && t.size() == 1)
					innerIndex = t.get(0);
				else
					throw new ExpressionParserException(where.getPosition(), "JOIN with complex ON clauses does not support NEAREST");
			} else
				throw new ExpressionParserException(where.getPosition(), "JOIN with complex ON clauses does not support NEAREST");
			final TempIndex tmpIndex;
			tableNamesToIndexes = new BasicMultiMap.List<String, SqlProjector.TempIndex>();
			if (innerIndex == null)
				tmpIndex = new TempNearestIndex(query, nearest.type, targetTable.getRows(), 1, nearest.rightTable, targetDcc, nearest.leftTable, sourceDcc, caster, sf);
			else
				tmpIndex = new TempNearestIndexWithOn(query, nearest.type, innerIndex, 1, nearest.rightTable, targetDcc, nearest.leftTable, sourceDcc, caster, sf);
			tableNamesToIndexes.put(nearest.leftTable, Collections.singletonList(tmpIndex));
		}

		List<TempIndex>[] tableIndexes = new List[tableRows.size()];
		String firstTableName = asTables.keySet().iterator().next();
		int pos = 0;

		String[] tableNames = new String[tableRows.size()];
		Table[] tables = new Table[tableRows.size()];
		Iterable<Row>[] tableLists = new Iterable[tableRows.size()];

		int firstTablePos = -1;
		int secondTablePos = -1;
		for (Tuple2<String, Iterable<Row>> e : tableRows) {
			if (OH.eq(firstTableName, e.getA()))
				firstTablePos = pos;
			else
				secondTablePos = pos;
			tableNames[pos] = e.getA();
			Iterable<Row> b = e.getB();
			tableLists[pos] = b;
			tables[pos] = asTables.get(e.getA());

			List<TempIndex> idx = tableNamesToIndexes.get(e.getA());
			tableIndexes[pos] = idx;
			if (planListener != null)
				planListener.onStep("NESTING_QUERY", "#" + pos + " " + tableNames[pos] + (idx == null ? "" : " indexing to " + idx));
			pos++;
		}

		if (origLimit == 0 || (CH.isEmpty(tableLists[firstTablePos]) && includeOuterRight == false))
			return removeTempSortingColumns(r, names);
		if (wherePointer != null)
			where = wherePointer.get();

		if (planListener != null)
			planListener.onStep("NONINDEX_WHERE", where == null ? "<FULL SCAN>" : where.toString());

		//////////////////////////
		// QUERY PLAN STEP 3 - Build table getter and register necessary dependencies
		//////////////////////////
		final TablesCalcFrame tg = new TablesCalcFrame(tableNames, tables);
		List<DerivedCellCalculatorRef> sink = new ArrayList<DerivedCellCalculatorRef>();
		for (DerivedCellCalculator i : select)
			DerivedHelper.find(i, DerivedCellCalculatorRef.class, sink);
		if (where != null)
			DerivedHelper.find(where, DerivedCellCalculatorRef.class, sink);
		for (DerivedCellCalculatorRef i : sink) {
			Object id = i.getId();
			int pos2;
			if (id instanceof String) {
				pos2 = tg.getPosition((String) id);
			} else {
				pos2 = tg.getPosition((NameSpaceIdentifier) id);
			}
			if (pos2 != -1)
				i.setLoc(pos2);
		}

		//////////////////////////
		// QUERY PLAN STEP 4 - Initialize the iterators 
		//////////////////////////

		Iterator<Row>[] iterators = new Iterator[tablesCount];
		Row[] tgRows = tg.currentRows;
		outer: for (int x = 0; x < tablesCount;) {
			if (timeoutController != null)
				timeoutController.throwIfTimedout(query);
			final Row row;
			Iterator<Row> it = iterators[x];
			if (it == null)
				it = iterators[x] = tableLists[x].iterator();
			if (!it.hasNext()) {
				iterators[x] = null;
				if (x == 0)
					if (joinTo == null)
						return removeTempSortingColumns(r, names);
					else {
						iterators = null;
						break outer;
					}
				x--;
				row = null;
				continue;
			} else {
				row = it.next();
			}
			tgRows[x] = row;
			List<TempIndex> indexes = tableIndexes[x];
			if (indexes != null) {
				for (int j = 0; j < indexes.size(); j++) {
					TempIndex idx = indexes.get(j);
					List<Row> tRows = idx.getRows(row);
					int pos2 = idx.getTargetTablePosition();
					iterators[pos2] = null;
					tableLists[pos2] = tRows;
					if (tRows.isEmpty())
						continue outer;
				}
			}
			x++;
		}

		if (planListener != null && whereAfterJoin != null)
			planListener.onStep("POSTJOIN_FILTER", whereAfterJoin.toString());

		//////////////////////////
		// QUERY PLAN STEP 6 - Nested looping, building the output table
		//////////////////////////
		int limitOffset = origLimitOffset;
		int limit = origLimit;
		final boolean canApplyLimitOnline = unpacks == null && orderByIdx == null;
		final boolean canApplyLimit = canApplyLimitOnline && limit != -1;
		if (tableRows.size() == 1 && tableRows.get(0).getValue() instanceof ColumnarTableList) {
			ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
			rsf.reset(tg);
			List<ColumnarRow> in = (List) tableRows.get(0).getValue();
			if (pv != null) {
				for (Row input : in) {
					tg.getRows()[0] = input;
					pv.visit(tg, 0, rsf);
				}
			} else {
				int size = in.size();
				int offset;
				if (canApplyLimit) {
					if (size > limit)
						size = limit;
					if (size + limitOffset > in.size())
						size = in.size() - limitOffset;
					offset = limitOffset;
				} else
					offset = 0;
				if (planListener != null)
					planListener.onStep("FAST_MEMCOPY", "TABLE IS " + select.length + " x " + (size - offset));
				r.newRows(size);
				if (size > 0 && (select.length > 0 || extraColumnsForOrderBySize > 0)) {
					int j = 0;
					ColumnarTable table = (ColumnarTable) in.get(0).getTable();
					for (int i = 0; i < select.length; i++)
						copyColumn(tg, rsf, table, r, select[i], i, in, offset, size);
					for (int i = 0; i < extraColumnsForOrderBySize; i++)
						copyColumn(tg, rsf, table, r, extraColumnsForOrderByArray[i], i + select.length, in, offset, size);
				}
			}
		} else {
			RowUsedTracker usedInLeftJoin = includeOuterLeft ? newRowUsedTracker(tables[firstTablePos].getSize()) : null;//TODO: bitset is better
			RowUsedTracker usedInRightJoin = includeOuterRight ? newRowUsedTracker(tables[secondTablePos].getSize()) : null;//TODO: bitset is better
			TableList targetRows = r == null ? null : r.getRows();
			int x = tablesCount - 1;
			ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
			rsf.reset(tg);
			if (iterators != null) {
				outer: while (true) {
					if (timeoutController != null)
						timeoutController.throwIfTimedout(query);
					boolean joinPasses = where == null || Boolean.TRUE.equals(where.get(rsf));

					if (joinPasses) {
						if (includeOuterLeft)
							usedInLeftJoin.setUsed(tgRows[firstTablePos].getLocation());
						if (includeOuterRight)
							usedInRightJoin.setUsed(tgRows[secondTablePos].getLocation());
						if (includeInner) {
							boolean whereAfterJoinPasses = whereAfterJoin == null || Boolean.TRUE.equals(whereAfterJoin.get(rsf));
							if (whereAfterJoinPasses) {
								//the composite row evals to true
								if (canApplyLimitOnline && limitOffset > 0) {
									limitOffset--;
								} else {
									if (pv != null)
										pv.visit(tg, firstTablePos, rsf);
									else {
										Object[] row2 = new Object[namesIncludingExtraSortColumns.length];
										//process projected table
										for (int i = 0; i < select.length; i++)
											row2[i] = select[i].get(rsf);
										if (extraColumnsForOrderBySize > 0)
											for (int i = 0; i < extraColumnsForOrderBySize; i++)
												row2[i + select.length] = extraColumnsForOrderByArray[i].get(rsf);
										targetRows.addRow(row2);
									}
									if (canApplyLimit) {
										if (--limit == 0)
											break outer;
									}
								}
							}
						}
					}
					outer2: for (;;) {
						Iterator<Row> it = iterators[x];
						if (it == null)
							it = iterators[x] = tableLists[x].iterator();
						if (!it.hasNext()) {
							iterators[x] = null;
							if (x == 0)
								break outer;
							x--;
							continue;
						}
						final Row row = it.next();
						tgRows[x] = row;
						if (x == tablesCount - 1)
							break outer2;
						List<TempIndex> indexes = tableIndexes[x];
						if (indexes != null) {
							for (int j = 0; j < indexes.size(); j++) {
								TempIndex idx = indexes.get(j);
								List<Row> tRows = idx.getRows(row);
								int pos2 = idx.getTargetTablePosition();
								iterators[pos2] = null;
								tableLists[pos2] = tRows;
								if (tRows.isEmpty())
									continue outer2;
							}
						}
						x++;
					}
				}
			}
			if (usedInLeftJoin != null || usedInRightJoin != null) {
				rsf.reset(tg);
				Int pLimit = new Mutable.Int(limit);
				Int pLimitOffset = new Mutable.Int(limitOffset);
				if (includeOuterLeft && includeOuterRight) {
					if (usedInLeftJoin != null && limit != 0)
						addUnjoinedCheckAll(select, canApplyLimit, pLimitOffset, pLimit, extraColumnsForOrderByArray, whereAfterJoin, tables, firstTablePos, tg, tgRows, targetRows,
								usedInLeftJoin, pv, firstTablePos, rsf);
					if (usedInRightJoin != null && limit != 0)
						addUnjoinedCheckAll(select, canApplyLimit, pLimitOffset, pLimit, extraColumnsForOrderByArray, whereAfterJoin, tables, secondTablePos, tg, tgRows,
								targetRows, usedInRightJoin, pv, firstTablePos, rsf);
				} else {
					if (usedInLeftJoin != null && limit != 0)
						addUnjoinedCheckUsed(select, canApplyLimit, pLimitOffset, pLimit, extraColumnsForOrderByArray, whereAfterJoin, tables, tableLists, firstTablePos, tg,
								tgRows, targetRows, usedInLeftJoin, pv, firstTablePos, rsf);
					if (usedInRightJoin != null && limit != 0)
						addUnjoinedCheckUsed(select, canApplyLimit, pLimitOffset, pLimit, extraColumnsForOrderByArray, whereAfterJoin, tables, tableLists, secondTablePos, tg,
								tgRows, targetRows, usedInRightJoin, pv, firstTablePos, rsf);
				}
				limit = pLimit.value;
				limitOffset = pLimitOffset.value;
			}
		}

		//////////////////////////
		// UNPACK CLAUSE and ORDER BY 
		//////////////////////////
		if (!canApplyLimitOnline) {
			if (pv != null)
				pv.trimTable(limitOffset, limit);
			else {
				if (unpacks != null)
					if (orderByIdx != null)
						unpack(r, unpacks, -1, 0);
					else
						unpack(r, unpacks, limit, limitOffset);

				if (orderByIdx != null) {
					if (planListener != null)
						planListener.onStep("ORDER_BY",
								"Sorting " + r.getRows().size() + " rows on " + SH.join(',', orderBys) + " (needed " + (r.getColumnsCount() - names.length) + " temp columns)");
					TableHelper.sort(r.getRows(), new BasicRowComparator(orderByIdx, isAscending));
					removeTempSortingColumns(r, names);
				}

				trimTable(r, limitOffset, limit);
			}
		}
		return r;
	}
	private static void copyColumn(TablesCalcFrame tg, ReusableCalcFrameStack rsf, ColumnarTable in, ColumnarTable out, DerivedCellCalculator source, int outcol,
			List<ColumnarRow> inRows, int offset, int size) {
		ColumnarColumn outCol = out.getColumnAt(outcol);
		if (source instanceof DerivedCellCalculatorRef) {
			Object id = ((DerivedCellCalculatorRef) source).getId();
			if (id instanceof NameSpaceIdentifier) {
				NameSpaceIdentifier id2 = (NameSpaceIdentifier) id;
				//				OH.assertEq(id2.getNamespace(), in.getTitle());
				id = id2.getVarName();
			}
			OH.assertInstanceOf(id, String.class);
			ColumnarColumn col = (ColumnarColumn) in.getColumnsMap().get(id);
			if (col != null) {
				switch (col.getBasicType()) {
					case BasicTypes.LONG: {
						ColumnarColumnLong inCol = (ColumnarColumnLong) col;
						ColumnarColumnLong outCol2 = (ColumnarColumnLong) outCol;
						for (int j = 0; j < size; j++) {
							ColumnarRow n = inRows.get(j + offset);
							if (!inCol.isNull(n))
								outCol2.setLong(j, inCol.getLong(n));
						}
						return;
					}
					case BasicTypes.INT: {
						ColumnarColumnInt inCol = (ColumnarColumnInt) col;
						ColumnarColumnInt outCol2 = (ColumnarColumnInt) outCol;
						for (int j = 0; j < size; j++) {
							ColumnarRow n = inRows.get(j + offset);
							if (!inCol.isNull(n))
								outCol2.setInt(j, inCol.getInt(n));
						}
						int j = 0;
						return;
					}
					case BasicTypes.SHORT: {
						ColumnarColumnShort inCol = (ColumnarColumnShort) col;
						ColumnarColumnShort outCol2 = (ColumnarColumnShort) outCol;
						for (int j = 0; j < size; j++) {
							ColumnarRow n = inRows.get(j + offset);
							if (!inCol.isNull(n))
								outCol2.setShort(j, inCol.getShort(n));
						}
						return;
					}
					case BasicTypes.BYTE: {
						ColumnarColumnByte inCol = (ColumnarColumnByte) col;
						ColumnarColumnByte outCol2 = (ColumnarColumnByte) outCol;
						for (int j = 0; j < size; j++) {
							ColumnarRow n = inRows.get(j + offset);
							if (!inCol.isNull(n))
								outCol2.setByte(j, inCol.getByte(n));
						}
						return;
					}
					case BasicTypes.DOUBLE: {
						ColumnarColumnDouble inCol = (ColumnarColumnDouble) col;
						ColumnarColumnDouble outCol2 = (ColumnarColumnDouble) outCol;
						for (int j = 0; j < size; j++) {
							ColumnarRow n = inRows.get(j + offset);
							if (!inCol.isNull(n))
								outCol2.setDouble(j, inCol.getDouble(n));
						}
						return;
					}
					case BasicTypes.FLOAT: {
						ColumnarColumnFloat inCol = (ColumnarColumnFloat) col;
						ColumnarColumnFloat outCol2 = (ColumnarColumnFloat) outCol;
						for (int j = 0; j < size; j++) {
							ColumnarRow n = inRows.get(j + offset);
							if (!inCol.isNull(n))
								outCol2.setFloat(j, inCol.getFloat(n));
						}
						return;
					}
					case BasicTypes.CHAR: {
						ColumnarColumnChar inCol = (ColumnarColumnChar) col;
						ColumnarColumnChar outCol2 = (ColumnarColumnChar) outCol;
						for (int j = 0; j < size; j++) {
							ColumnarRow n = inRows.get(j + offset);
							if (!inCol.isNull(n))
								outCol2.setCharacter(j, inCol.getCharacter(n));
						}
						return;
					}
					case BasicTypes.BOOLEAN: {
						ColumnarColumnBoolean inCol = (ColumnarColumnBoolean) col;
						ColumnarColumnBoolean outCol2 = (ColumnarColumnBoolean) outCol;
						for (int j = 0; j < size; j++) {
							ColumnarRow n = inRows.get(j + offset);
							if (!inCol.isNull(n))
								outCol2.setBoolean(j, inCol.getBoolean(n));
						}
						return;
					}
					default: {
						for (int j = 0; j < size; j++) {
							ColumnarRow n = inRows.get(j + offset);
							outCol.setValue(j, col.getValue(n));
						}
						return;
					}

				}
			}
		} else if (source.isConst()) {
			Object value = source.get(null);
			for (int j = 0; j < size; j++) {
				outCol.setValue(j, value);
			}
		} else {
			Row[] rows = tg.getRows();
			for (int j = 0; j < size; j++) {
				rows[0] = inRows.get(j + offset);
				outCol.setValue(j, source.get(rsf));
			}
		}

	}

	//	private static void setColumnRefLocs(DerivedCellCalculator dcc, String tableName, Table sourceTable, Frame globalVars) {
	//		if (dcc instanceof DerivedCellCalculatorRef) {
	//			setColumnRefLocs((DerivedCellCalculatorRef) dcc, tableName, sourceTable, globalVars);
	//		} else {
	//			List<DerivedCellCalculatorRef> tmp = new ArrayList<DerivedCellCalculatorRef>();
	//			DerivedHelper.find(dcc, DerivedCellCalculatorRef.class, tmp);
	//			for (DerivedCellCalculatorRef ref : tmp)
	//				setColumnRefLocs(ref, tableName, sourceTable, globalVars);
	//		}
	//	}
	//	private static void setColumnRefLocs(DerivedCellCalculatorRef ref, String tableName, Table sourceTable, Frame globalVars) {
	//		Object o = ref.getId();
	//		Column c;
	//		if (o instanceof NameSpaceIdentifier) {
	//			NameSpaceIdentifier ns = (NameSpaceIdentifier) o;
	//			String tname = ns.getNamespace();
	//			if (OH.ne(tableName, tname))
	//				throw new ExpressionParserException(ref.getPosition(), "Table not found: " + tname);
	//			String cname = ns.getVarName();
	//			c = sourceTable.getColumnsMap().get(cname);
	//			if (c == null)
	//				throw new ExpressionParserException(ref.getPosition(), "column not found: " + ns);
	//		} else {
	//			String id = (String) o;
	//			c = sourceTable.getColumnsMap().get(id);
	//			if (c == null) {
	//				if (globalVars.getTypes().containsKey(id)) {
	//					ref.setConst(globalVars.getValue(id));
	//					return;
	//				} else
	//					throw new ExpressionParserException(ref.getPosition(), "column not found: " + id);
	//			}
	//		}
	//		ref.setLoc(c.getLocation());
	//	}
	private static RowUsedTracker newRowUsedTracker(int size) {
		return new RowUsedTracker(size);
	}

	private static class RowUsedTracker {
		private BitSet bs = new BitSet();

		public RowUsedTracker(int size) {
			this.bs = new BitSet(size);
		}
		public void setUsed(int row) {
			bs.set(row);

		}
		public int getNextUnused(int row) {
			return bs.nextClearBit(row);
		}
		public int size() {
			return this.bs.size();
		}

	}

	private static Nearest processNearest(DerivedCellCalculator where, Map<String, Table> asTables, CalcFrameStack sf) {
		if (where instanceof DerivedCellCalculatorMath) {
			DerivedCellCalculatorMath math = (DerivedCellCalculatorMath) where;
			//			if (math.getLeft() instanceof DerivedCellCalculatorRef && math.getRight() instanceof DerivedCellCalculatorRef) {
			byte type;
			switch (math.getOperationNodeCode()) {
				case OperationNode.OP_GT:
					type = DerivedCellCalculatorMath.TYPE_STRING_GT;
					break;
				case OperationNode.OP_LT:
					type = DerivedCellCalculatorMath.TYPE_STRING_LT;
					break;
				case OperationNode.OP_GT_EQ:
					type = DerivedCellCalculatorMath.TYPE_STRING_GE;
					break;
				case OperationNode.OP_LT_EQ:
					type = DerivedCellCalculatorMath.TYPE_STRING_LE;
					break;
				case OperationNode.OP_EQ:
					type = DerivedCellCalculatorMath.TYPE_STRING_EQ;
					break;
				default:
					type = -1;
			}
			if (type != -1)
				return new Nearest(type, math.getLeft(), math.getRight(), asTables, sf);
			//			}
		}
		throw new ExpressionParserException(where.getPosition(), "NEAREST JOIN expecting clause with 2 columns compared with one of: > , >=, <, <=");
	}
	private static Class<?> getBaseType(Class i, Class j) {
		return SqlProcessorUtils.getWidest(i, j);
	}
	private static void flipRightLeftTables(Map<String, Table> asTables) {
		OH.assertEq(asTables.size(), 2);
		Iterator<Entry<String, Table>> it = asTables.entrySet().iterator();
		Entry<String, Table> a = it.next(), b = it.next();
		asTables.clear();
		asTables.put(b.getKey(), b.getValue());
		asTables.put(a.getKey(), a.getValue());
	}
	private static Table removeTempSortingColumns(Table r, Object[] names) {
		if (r != null)
			while (r.getColumnsCount() > names.length)
				r.removeColumn(names.length);
		return r;
	}
	public static boolean trimTable(Table r, int limitOffset, int limit) {
		TableList rows = r.getRows();
		if (limitOffset == 0 && (limit == -1 || limit >= rows.size()))
			return false;
		if (limitOffset >= rows.size()) {
			r.clear();
			return true;
		}
		if (limitOffset == 0) {
			for (int i = rows.size() - 1; i >= limit; i--)
				rows.remove(i);
			return true;
		}
		if (limitOffset + limit > rows.size())
			limit = rows.size() - limitOffset;
		if (r instanceof ColumnarTable) {
			for (int i = 0; i < limit; i++) {
				ColumnarRow target = (ColumnarRow) rows.get(i);
				ColumnarRow source = (ColumnarRow) rows.get(i + limitOffset);
				for (int n = 0; n < r.getColumnsCount(); n++) {
					ColumnarColumn cc = (ColumnarColumn) r.getColumnAt(n);
					cc.copyValue(source, target);

				}
			}
			for (int i = rows.size() - 1; i >= limit; i--)
				rows.remove(i);
		} else {
			Row rows2[] = new Row[limit];
			for (int i = 0; i < limit; i++)
				rows2[i] = rows.get(i + limitOffset);
			r.clear();
			for (int i = 0; i < limit; i++)
				rows.add(rows2[i]);
		}
		return true;
	}

	//IMPORTANT: changes to this function should probably be maed to addUnjoinedCheckAll Too
	private static void addUnjoinedCheckUsed(DerivedCellCalculator[] selectOptimized, boolean canApplyLimit, Int pLimitOffset, Int pLimit,
			DerivedCellCalculator[] extraColumnsForOrderByOptimized, final DerivedCellCalculator whereAfterJoinOptimized, Table[] tables, Iterable<Row>[] tableLists,
			int tableForJoinPos, TablesCalcFrame tg, Row[] tgRows, TableList targetRows, RowUsedTracker usedInLeftJoin, SqlProjectionVisitor pv, int firstTablePos,
			ReusableCalcFrameStack tgsf) {
		int tablesCount = tgRows.length;
		int extraColumnsForOrderBySize = extraColumnsForOrderByOptimized.length;
		for (int j = 0; j < tablesCount; j++) {
			if (j != tableForJoinPos)
				tgRows[j] = new ColumnarTable(tables[j].getColumns()).newEmptyRow();//TODO: should be columnar
		}

		Iterable<Row> fTable = tableLists[tableForJoinPos];
		for (Row row : fTable) {
			if (usedInLeftJoin.bs.get(row.getLocation()))
				continue;
			tgRows[tableForJoinPos] = row;
			boolean whereAfterJoinPasses = whereAfterJoinOptimized == null || Boolean.TRUE.equals(whereAfterJoinOptimized.get(tgsf));
			if (whereAfterJoinPasses) {
				if (canApplyLimit && pLimitOffset.value > 0) {
					pLimitOffset.value--;
				} else {

					if (pv != null)
						pv.visit(tg, firstTablePos, tgsf);
					else {
						Object[] row2 = new Object[selectOptimized.length + extraColumnsForOrderBySize];
						//process projected table
						for (int i = 0; i < selectOptimized.length; i++)
							row2[i] = selectOptimized[i].get(tgsf);
						if (extraColumnsForOrderBySize > 0)
							for (int i = 0; i < extraColumnsForOrderBySize; i++)
								row2[i + selectOptimized.length] = extraColumnsForOrderByOptimized[i].get(tgsf);
						targetRows.addRow(row2);
					}
					if (canApplyLimit)
						if (--pLimit.value == 0)
							break;
				}
			}
		}
	}

	//IMPORTANT: changes to this function should probably be made to addUnjoinedCheckUsed Too
	private static void addUnjoinedCheckAll(DerivedCellCalculator[] selectOptimized, boolean canApplyLimit, Int pLimitOffset, Int pLimit,
			DerivedCellCalculator[] extraColumnsForOrderByOptimized, final DerivedCellCalculator whereAfterJoinOptimized, Table[] tables, int tableForJoinPos, TablesCalcFrame tg,
			Row[] tgRows, TableList targetRows, RowUsedTracker usedInRightJoin, SqlProjectionVisitor pv, int firstTablePos, ReusableCalcFrameStack tgsf) {
		TableList fTable = tables[tableForJoinPos].getRows();
		int size = Math.min(usedInRightJoin.size(), fTable.size());
		int rowNum = usedInRightJoin.getNextUnused(0);
		int tablesCount = tgRows.length;
		int extraColumnsForOrderBySize = extraColumnsForOrderByOptimized.length;
		if (rowNum < size) {
			for (int j = 0; j < tablesCount; j++) {
				if (j != tableForJoinPos)
					tgRows[j] = new ColumnarTable(tables[j].getColumns()).newEmptyRow();//TODO: should be columnar
			}
			for (; rowNum < size; rowNum = usedInRightJoin.getNextUnused(rowNum + 1)) {
				tgRows[tableForJoinPos] = fTable.get(rowNum);
				boolean whereAfterJoinPasses = whereAfterJoinOptimized == null || Boolean.TRUE.equals(whereAfterJoinOptimized.get(tgsf));
				if (whereAfterJoinPasses) {
					if (canApplyLimit && pLimitOffset.value > 0) {
						pLimitOffset.value--;
					} else {

						if (pv != null)
							pv.visit(tg, firstTablePos, tgsf);
						else {
							Object[] row2 = new Object[selectOptimized.length + extraColumnsForOrderBySize];
							//process projected table
							for (int i = 0; i < selectOptimized.length; i++)
								row2[i] = selectOptimized[i].get(tgsf);
							if (extraColumnsForOrderBySize > 0)
								for (int i = 0; i < extraColumnsForOrderBySize; i++)
									row2[i + selectOptimized.length] = extraColumnsForOrderByOptimized[i].get(tgsf);
							targetRows.addRow(row2);
						}
						if (canApplyLimit)
							if (--pLimit.value == 0)
								break;
					}
				}
			}
		}
	}
	private static Table unpack(Table t, OnNode[] unpacks, int limit, int limitOffset) {
		if (limit != -1)
			limit += limitOffset;
		List<Object[]> valuesIn = new ArrayList<Object[]>();
		List<Object[]> valuesOut = new ArrayList<Object[]>();
		final TableList rows = t.getRows();
		for (Row row : rows)
			valuesOut.add(row.getValuesCloned());
		t.clear();
		outer: for (OnNode on : unpacks) {
			List<Object[]> tmp = valuesIn;
			valuesIn = valuesOut;
			valuesOut = tmp;
			valuesOut.clear();
			final int pos = t.getColumn(on.getValue().toString()).getLocation();
			final String dlm = (String) ((ConstNode) on.getOn()).getValue();
			for (Object[] row : valuesIn) {
				if (limit != -1 && valuesOut.size() >= limit)
					break outer;
				valuesOut.add(row);
				final String val = SH.toStringOrNull(row[pos]);
				if (val != null) {
					final String[] values2 = SH.split(dlm, val);
					if (values2.length > 0) {
						row[pos] = values2[0];
						for (int i = 1; i < values2.length; i++) {
							final Object[] row2 = row.clone();
							row2[pos] = values2[i];
							if (limit != -1 && valuesOut.size() >= limit)
								break outer;
							valuesOut.add(row2);
						}
					}
				}
			}
		}
		for (Object[] row : valuesOut)
			rows.addRow(row);
		return t;
	}
	public static ColumnarTable newBasicTable(Class<?>[] types, Object[] names) {
		try {
			if (names.length > 1) {
				final HashSet<String> namesSet = new LinkedHashSet<String>(names.length);
				boolean dupName = false;
				for (Object o : names) {

					String name;
					if (o instanceof NameSpaceIdentifier) {
						NameSpaceIdentifier ns = (NameSpaceIdentifier) o;
						name = ns.getNamespace() + "_" + ns.getVarName();
						dupName = true;
					} else
						name = (String) o;
					if (!namesSet.add((String) name)) {
						dupName = true;
						namesSet.add(SH.getNextId((String) name, namesSet, 2));
					}
				}
				return new ColumnarTable(types, AH.toArray(namesSet, String.class)); //TODO: should be columnar
			} else if (names.length == 0)
				return new ColumnarTable(types, OH.EMPTY_STRING_ARRAY); //TODO: should be columnar
			else {
				Object o = names[0];
				String name;
				if (o instanceof NameSpaceIdentifier) {
					NameSpaceIdentifier ns = (NameSpaceIdentifier) o;
					name = ns.getNamespace() + "_" + ns.getVarName();
				} else
					name = (String) o;
				return new ColumnarTable(types, new String[] { name }); //TODO: should be columnar

			}

		} catch (Exception e) {
			throw new RuntimeException("SQL table definition is invalid", e);
		}
	}
	private static Column findColumn(Table t, Object name) {
		if (name instanceof NameSpaceIdentifier) {
			NameSpaceIdentifier ns = (NameSpaceIdentifier) name;
			if (OH.eq(ns.getNamespace(), t.getTitle()))
				return t.getColumnsMap().get(ns.getVarName());
			else
				return null;
		}
		return t.getColumnsMap().get(name);
	}
	private static Column findColumn(CalcFrameStack context, Map<String, Table> asTables, Object o) {
		if (o instanceof NameSpaceIdentifier) {
			NameSpaceIdentifier ns = (NameSpaceIdentifier) o;
			String beforeFirst = ns.getNamespace();
			String afterFirst = ns.getVarName();
			return asTables.get(beforeFirst).getColumn(afterFirst);
		} else {
			String name = (String) o;
			for (Table t : asTables.values()) {
				Column r = t.getColumnsMap().get(name);
				if (r != null)
					return r;
			}
		}
		return null;
	}

	//	public static class TablesGetterRef extends DerivedCellCalculatorRef {
	//
	//		private TablesGetter last;
	//		private int rowOffset;
	//		private int cellOffset;
	//
	//		public TablesGetterRef(int position, Class<?> type, Object id) {
	//			super(position, type, id);
	//		}
	//
	//		@Override
	//		public Object get(StackFrame key) {
	//			if (isConst())
	//				return super.get(key);
	//			if (key == last)
	//				return rowOffset == -1 ? super.get(key) : last.getAt(rowOffset, cellOffset);
	//			if (key instanceof TablesGetter) {
	//				last = (TablesGetter) key;
	//				Integer value = last.namesToPositions.get(this.getId());
	//				if (value != null) {
	//					rowOffset = value >> 16;
	//					cellOffset = value & 65535;
	//					return last.getAt(rowOffset, cellOffset);
	//				} else
	//					rowOffset = -1;
	//			}
	//			return super.get(key);
	//		}
	//		@Override
	//		public DerivedCellCalculator copy() {
	//			return new TablesGetterRef(getPosition(), getReturnType(), getId());
	//		}
	//
	//		@Override
	//		public int hashCode() {
	//			return OH.hashCode(this.getId(), this.getReturnType());
	//		}
	//		@Override
	//		public boolean equals(Object other) {
	//			if (other == null || other.getClass() != TablesGetterRef.class)
	//				return false;
	//			DerivedCellCalculatorRef o = (DerivedCellCalculatorRef) other;
	//			return OH.eq(getId(), o.getId()) && OH.eq(getReturnType(), o.getReturnType());
	//		}
	//		@Override
	//		public boolean isSame(DerivedCellCalculator other) {
	//			return super.isSame(other);
	//			//			if (!super.isSame(other))
	//			//				return false;
	//			//			TablesGetterRef o = (TablesGetterRef) other;
	//			//			return rowOffset == o.rowOffset && cellOffset == o.cellOffset;
	//		}
	//	}

	//	public static class TablesGetter implements NameSpaceFrame, Types, NameSpaceTypes {
	//
	//		final private Map<Object, Integer> namesToPositions = new HashMap<Object, Integer>();//16 bits for table, 16 bits for col
	//		final public Row[] currentRows;
	//		final private Table[] tables;
	//		final private String[] tableNames;
	//		private int position;
	//
	//		public TablesGetter(int position, int tablesCount, String tableNames[], Table[] tables) {
	//			this.position = position;
	//			this.currentRows = new Row[tablesCount];
	//			this.tableNames = tableNames;
	//			this.tables = tables;
	//		}
	//
	//		public void addDependencies(DerivedCellCalculator o) {
	//			for (Object i : DerivedHelper.getDependencyIds(o))
	//				addDependency(i);
	//		}
	//		public void addDependency(Object o) {
	//			if (o instanceof NameSpaceIdentifier)
	//				addDependency((NameSpaceIdentifier) o);
	//			else
	//				addDependency((String) o);
	//		}
	//
	//		private void addDependency(String name) {
	//			if (namesToPositions.containsKey(name))
	//				return;
	//			for (int i = 0; i < tables.length; i++) {
	//				Column col = tables[i].getColumnsMap().get(name);
	//				if (col != null) {
	//					namesToPositions.put(name, (i << 16) | col.getLocation());
	//					return;
	//				}
	//			}
	//		}
	//		private void addDependency(NameSpaceIdentifier id) {
	//			if (namesToPositions.containsKey(id))
	//				return;
	//			int i = AH.indexOf(id.getNamespace(), tableNames);
	//			if (i == -1) {
	//				throw new ExpressionParserException(this.position, "Undefined table: " + id.getNamespace());
	//			} else {
	//				Table table = tables[i];
	//				Column col = table.getColumnsMap().get(id.getVarName());
	//				if (col == null)
	//					throw new ExpressionParserException(this.position, "Table '" + id.getNamespace() + "' does not have column: " + id.getVarName());
	//				int j = col.getLocation();
	//				namesToPositions.put(id, (i << 16) | j);
	//			}
	//		}
	//
	//		public Row[] getRows() {
	//			return this.currentRows;
	//		}
	//
	//		@Override
	//		public Object getValue(NameSpaceIdentifier key) {
	//			final Integer value = namesToPositions.get(key);
	//			if (value == null)
	//				throw new IllegalStateException("unknown key: " + key);
	//			return getAt(value >> 16, value & 65535);
	//		}
	//
	//		@Override
	//		public Object getValue(String key) {
	//			final Integer value = namesToPositions.get(key);
	//			if (value == null)
	//				throw new IllegalStateException("unknown key: " + key);
	//			return getAt(value >> 16, value & 65535);
	//		}
	//		private Object getAt(int rowOffset, int cellOffset) {
	//			Row row = currentRows[rowOffset];
	//			return row == null ? null : row.getAt(cellOffset);
	//		}
	//		private Class<?> getTypeAt(int rowOffset, int cellOffset) {
	//			Table table = tables[rowOffset];
	//			return table.getColumnAt(cellOffset).getType();
	//		}
	//
	//		@Override
	//		public boolean isEmpty() {
	//			return false;
	//		}
	//
	//		@Override
	//		public Class<?> getType(String key) {
	//			final Integer value = namesToPositions.get(key);
	//			if (value == null)
	//				return null;
	//			return getTypeAt(value >> 16, value & 65535);
	//		}
	//
	//		@Override
	//		public Object putValue(String key, Object value) {
	//			throw new UnsupportedOperationException();
	//		}
	//
	//		@Override
	//		public boolean containsKey(String key) {
	//			return namesToPositions.containsKey(key);// || globalVars.getType(key) != null;
	//		}
	//
	//		@Override
	//		public NameSpaceTypes getTypes() {
	//			return this;
	//		}
	//
	//		@Override
	//		public Iterable<Entry<String, Class<?>>> entries() {
	//			throw new ToDoException();
	//		}
	//
	//		@Override
	//		public Iterable<String> getKeys() {
	//			return (Iterable) namesToPositions.keySet();
	//		}
	//
	//		@Override
	//		public int size() {
	//			return namesToPositions.size();
	//		}
	//
	//		@Override
	//		public boolean containsValue(String key) {
	//			return namesToPositions.containsKey(key);
	//		}
	//
	//		@Override
	//		public Class<?> getType(NameSpaceIdentifier key) {
	//			final Integer value = namesToPositions.get(key);
	//			if (value == null)
	//				return null;
	//			return getTypeAt(value >> 16, value & 65535);
	//		}
	//
	//	}

	//for a give key, what are all the rows that match to it

	public static interface TempIndex {
		public int getTargetTablePosition();
		public List<Row> getRows(Row sourceRow);
		public int getUniqueValuesCount();
		public boolean isEmpty();
		public Object getKey(Row sourceRow);
	}

	public static class TempNearestIndex implements TempIndex, Comparator<Row> {

		private final int targetTablePosition;
		private final String targetTable, sourceTable;
		DerivedCellCalculator sourceColumn;
		DerivedCellCalculator targetColumn;
		private final Caster targetType;
		private final byte sortType;
		private final NearestIndex inner;
		protected final List tmp = new ArrayList();
		protected final ReusableCalcFrameStack sf;

		public TempNearestIndex(DerivedCellCalculatorSql query, byte type, List<Row> rows, int targetTablePosition, String targetTable, DerivedCellCalculator targetColumn,
				String sourceTable, DerivedCellCalculator sourceColumn, Caster<?> targetType, CalcFrameStack stackFrame) {
			this.sortType = type;
			this.targetTablePosition = targetTablePosition;
			this.targetTable = targetTable;
			this.targetColumn = targetColumn;
			this.sourceTable = sourceTable;
			this.sourceColumn = sourceColumn;
			this.targetType = targetType;
			this.sf = new ReusableCalcFrameStack(stackFrame);
			this.inner = rows == null ? null : new NearestIndex(rows, this);
		}

		@Override
		public int compare(Row o1, Row o2) {
			return OH.compare((Comparable) targetColumn.get(this.sf.reset(o1)), (Comparable) targetColumn.get(this.sf.reset(o2)));
		}
		@Override
		public int getTargetTablePosition() {
			return targetTablePosition;
		}

		@Override
		public List<Row> getRows(Row sourceRow) {
			return this.inner.getRows(sourceRow, tmp, this);
		}
		@Override
		public String toString() {
			return sourceColumn + " ==> " + targetColumn;
		}

		@Override
		public boolean isEmpty() {
			return inner.keys.length == 0;
		}

		@Override
		public int getUniqueValuesCount() {
			return this.inner.keys.length;
		}

		@Override
		final public Object getKey(Row sourceRow) {
			return targetColumn.get(sf.reset(sourceRow));
		}

	}

	public static class TempNearestIndexWithOn extends TempNearestIndex {

		private TempIndex innerIndex;
		private HasherMap<Object, NearestIndex> cache = new HasherMap<Object, NearestIndex>();

		public TempNearestIndexWithOn(DerivedCellCalculatorSql query, byte type, TempIndex inner, int targetTablePosition, String targetTable, DerivedCellCalculator targetColumn,
				String sourceTable, DerivedCellCalculator sourceColumn, Caster<?> targetType, CalcFrameStack sf) {
			super(query, type, null, targetTablePosition, targetTable, targetColumn, sourceTable, sourceColumn, targetType, sf);
			this.innerIndex = inner;

		}

		@Override
		public List<Row> getRows(Row sourceRow) {
			Object k = this.innerIndex.getKey(sourceRow);
			HasherMap.Entry<Object, NearestIndex> entry = cache.getOrCreateEntry(k);
			NearestIndex val = entry.getValue();
			if (val == null) {
				List<Row> t = this.innerIndex.getRows(sourceRow);
				entry.setValue(val = new NearestIndex(t, this));
			}
			return val.getRows(sourceRow, tmp, this);
		}

		@Override
		public String toString() {
			return this.innerIndex.toString() + " NEAREST " + super.toString();
		}
		@Override
		public boolean isEmpty() {
			return this.innerIndex.isEmpty();
		}

		@Override
		public int getUniqueValuesCount() {
			return this.innerIndex.getUniqueValuesCount();
		}
	}

	public static class NearestIndex {

		private final Comparable[] keys;
		private final Row[] vals;
		private final int size;

		public NearestIndex(List<Row> rows, TempNearestIndex parent) {
			vals = AH.toArray(rows, Row.class);
			Arrays.sort(vals, parent);//TODO: handle dups
			keys = new Comparable[vals.length];
			if (vals.length > 0) {
				Comparable last = keys[0] = (Comparable<?>) parent.targetType.cast(parent.getKey(vals[0]));
				int tgt = 1;
				for (int src = 1; src < vals.length; src++) {
					final Comparable<?> key = (Comparable<?>) parent.targetType.cast(parent.getKey(vals[src]));
					if (OH.ne(key, last)) {
						last = keys[tgt] = key;
						vals[tgt] = vals[src];
						tgt++;
					}
				}
				this.size = tgt;
			} else
				this.size = 0;
		}

		public List<Row> getRows(Row sourceRow, List tmp, TempNearestIndex parent) {
			Comparable lookup = (Comparable) parent.targetType.cast(parent.sourceColumn.get(parent.sf.reset(sourceRow)));
			final int index;
			switch (parent.sortType) {
				case DerivedCellCalculatorMath.TYPE_STRING_LT:
					index = AH.indexOfSortedGreaterThan(lookup, this.keys, size);
					break;
				case DerivedCellCalculatorMath.TYPE_STRING_LE:
					index = AH.indexOfSortedGreaterThanEqualTo(lookup, this.keys, size);
					break;
				case DerivedCellCalculatorMath.TYPE_STRING_GT:
					index = AH.indexOfSortedLessThan(lookup, this.keys, size);
					break;
				case DerivedCellCalculatorMath.TYPE_STRING_GE:
					index = AH.indexOfSortedLessThanEqualTo(lookup, this.keys, size);
					break;
				case DerivedCellCalculatorMath.TYPE_STRING_EQ: {
					int idx1 = AH.indexOfSortedLessThanEqualTo(lookup, this.keys, size);
					int idx2 = AH.indexOfSortedGreaterThanEqualTo(lookup, this.keys, size);
					if (idx1 == -1)
						index = idx2;
					else if (idx2 == -1)
						index = idx1;
					else {
						Number val1 = (Number) keys[idx1];
						Number val2 = (Number) keys[idx2];
						if (val1 == null)
							index = idx2;
						else if (val2 == null)
							index = idx1;
						else {
							double n1 = ((Number) lookup).doubleValue();
							index = val2.doubleValue() - n1 < n1 - val1.doubleValue() ? idx2 : idx1;
						}
					}
					break;
				}
				default:
					throw new IllegalStateException("Unknown operation: " + parent.sortType);
			}
			if (index == -1) {
				tmp.clear();
			} else {
				if (tmp.size() == 0)
					tmp.add(vals[index]);
				else
					tmp.set(0, vals[index]);
			}
			return tmp;
		}
	}

	public static class TempSingleIndex implements TempIndex {

		private final com.f1.utils.structs.BasicMultiMap.List<Object, Row> mm;
		private final int sourceColumnLocation;
		private final int targetTablePosition;
		private final String targetTable, targetColumn, sourceTable, sourceColumn;
		private Caster targetType;

		public TempSingleIndex(int location, BasicMultiMap.List<Object, Row> mm, int targetTablePosition, String targetTable, String targetColumn, String sourceTable,
				String sourceColumn, Caster<?> targetType) {
			this.sourceColumnLocation = location;
			this.mm = mm.isEmpty() ? null : mm;
			this.targetTablePosition = targetTablePosition;
			this.targetTable = targetTable;
			this.targetColumn = targetColumn;
			this.sourceTable = sourceTable;
			this.sourceColumn = sourceColumn;
			this.targetType = targetType;
		}

		public int getTargetTablePosition() {
			return targetTablePosition;
		}

		public List<Row> getRows(Row sourceRow) {
			if (mm == null)
				return Collections.EMPTY_LIST;
			List<Row> r = mm.get(targetType.cast(sourceRow.getAt(sourceColumnLocation)));
			if (r == null)
				return Collections.EMPTY_LIST;
			return r;
		}
		@Override
		public String toString() {
			return sourceTable + '.' + sourceColumn + " ==> " + targetTable + '.' + targetColumn;
		}

		@Override
		public boolean isEmpty() {
			return mm == null;
		}

		@Override
		public int getUniqueValuesCount() {
			return this.mm == null ? 0 : this.mm.size();
		}
		@Override
		public Object getKey(Row sourceRow) {
			return sourceRow.getAt(sourceColumnLocation);
		}

	}

	public static class TempMultiIndex implements TempIndex {

		private final com.f1.utils.structs.BasicMultiMap.List<Object[], Row> mm;
		private final int[] souceLocations;
		private final int targetTablePosition;
		private final String targetTable, targetColumns[], sourceTable, sourceColumns[];
		private final Object[] tmpKey;
		private final Caster[] targetTypes;

		public TempMultiIndex(int[] sourceLocs, BasicMultiMap.List<Object[], Row> mm, int targetTablePosition, String targetTable, String[] targetColumns, String sourceTable,
				String[] sourceColumns, Caster<?>[] targetTypes) {
			this.souceLocations = sourceLocs;
			this.mm = mm.isEmpty() ? null : mm;
			this.targetTablePosition = targetTablePosition;
			this.targetTable = targetTable;
			this.targetColumns = targetColumns;
			this.sourceTable = sourceTable;
			this.sourceColumns = sourceColumns;
			this.tmpKey = new Object[targetColumns.length];
			this.targetTypes = targetTypes;
		}
		public int getTargetTablePosition() {
			return targetTablePosition;
		}

		public List<Row> getRows(Row sourceRow) {
			if (mm == null)
				return Collections.EMPTY_LIST;
			for (int i = 0; i < tmpKey.length; i++)
				tmpKey[i] = targetTypes[i].cast(sourceRow.getAt(souceLocations[i]));
			final List<Row> r = mm.get(tmpKey);
			if (r == null)
				return Collections.EMPTY_LIST;
			return r;
		}

		@Override
		public boolean isEmpty() {
			return mm == null;
		}
		@Override
		public String toString() {
			return sourceTable + '.' + SH.join('+', sourceColumns) + " ==> " + targetTable + '.' + SH.join('+', targetColumns);
		}

		@Override
		public int getUniqueValuesCount() {
			return this.mm == null ? 0 : this.mm.size();
		}
		@Override
		public Object getKey(Row sourceRow) {
			Object tmpKey[] = new Object[targetColumns.length];
			for (int i = 0; i < tmpKey.length; i++)
				tmpKey[i] = targetTypes[i].cast(sourceRow.getAt(souceLocations[i]));
			return tmpKey;
		}
	}

	public static List<Tuple2<String, List<Row>>> prefilter(DerivedCellCalculatorSql query, final int origLimit, final OnNode[] unpacks, final int[] orderByIdx,
			final Map<String, Table> asTables, final int origLimitOffset, final Pointer<DerivedCellCalculator> wherePointer, final DerivedCellCalculator[] select,
			final DerivedCellCalculator joinTo, final SqlProcessor processor, final SqlProcessorTableMutator mutator, DerivedCellCalculator whereAfterJoin, CalcFrameStack sf) {
		final SqlPlanListener planListener = sf.getSqlPlanListener();
		List<Tuple2<String, List<Row>>> tableRows = new ArrayList<Tuple2<String, List<Row>>>(asTables.size());
		final boolean canApplyLimitOnline = origLimit != -1 && unpacks == null && orderByIdx == null && asTables.size() == 1;
		int remaining = origLimit + origLimitOffset;
		if (canApplyLimitOnline && remaining == 0)
			return null;
		//Find any where conditions that apply to individual tables and process those first
		final Map<String, DerivedCellCalculator> preJoinFilters;
		if (wherePointer != null) {
			if (asTables.size() > 1) {
				preJoinFilters = SqlProcessorSelectPlanner.findPreJoinWheres(asTables, wherePointer.get(), sf, false);
				wherePointer.put(preJoinFilters.get(null));
			} else {
				preJoinFilters = CH.m(CH.first(asTables.keySet()), wherePointer.get());
				wherePointer.put(null);
			}
		} else
			preJoinFilters = Collections.EMPTY_MAP;
		final Map<String, DerivedCellCalculator> postJoinFilters;
		if (whereAfterJoin != null) {
			postJoinFilters = SqlProcessorSelectPlanner.findPreJoinWheres(asTables, whereAfterJoin, sf, true);
		} else
			postJoinFilters = Collections.EMPTY_MAP;

		for (Entry<String, Table> e : asTables.entrySet()) {
			String name = e.getKey();
			List<Row> rows = e.getValue().getRows();
			if (rows.isEmpty() && joinTo == null)
				return null;
			DerivedCellCalculator t1 = preJoinFilters.get(name);
			DerivedCellCalculator t2 = postJoinFilters.get(name);
			DerivedCellCalculator where2;
			if (t1 == null)
				where2 = t2;
			else if (t2 == null)
				where2 = t1;
			else
				where2 = DerivedCellCalculatorMath.valueOf(t1.getPosition(), OperationNode.OP_AMP_AMP, t1, t2);

			if (where2 != null) {
				TablesCalcFrame tg = new TablesCalcFrame(new String[] { name }, new Table[] { e.getValue() });
				ReusableCalcFrameStack tgsf = new ReusableCalcFrameStack(sf, tg);
				if (planListener != null && preJoinFilters != null)
					planListener.onStep("PREJOIN_FILTER", "For Table '" + name + "' ==> " + where2);
				List<Row> rows2 = new ArrayList<Row>();
				//				DerivedCellCalculator where3 = where2;
				Pointer<DerivedCellCalculator> pWhereClause = new BasicPointer<DerivedCellCalculator>(where2);
				List<Row> rowsAfterIndex = mutator.applyIndexes(tgsf, e.getKey(), e.getValue(), pWhereClause, canApplyLimitOnline ? remaining : Integer.MAX_VALUE);
				where2 = pWhereClause.get();
				boolean isOnline = rowsAfterIndex instanceof OnlineTableList;
				if (where2.isConst()) {
					if (Boolean.TRUE.equals(where2.get(null))) {
						if (isOnline) {
							for (Row row : rowsAfterIndex) {
								tg.currentRows[0] = row;//TODO: is this right?
								row = row.getTable().newRow(row.getValues().clone());
								rows2.add(row);
								if (canApplyLimitOnline && --remaining == 0)
									break;
							}
						} else {
							//ASSERT
							//							for (Row row : rowsAfterIndex) {
							//								tg.currentRows[0] = row;
							//								if (!Boolean.TRUE.equals(where3.get(tgsf)))
							//									throw new IllegalStateException("Bad row: " + where3 + " vs :+row");
							//							}
							rows2 = rowsAfterIndex;
						}
					}
				} else if (isOnline) {
					for (Row row : rowsAfterIndex) {
						tg.currentRows[0] = row;//TODO: is this right?
						if (Boolean.TRUE.equals(where2.get(tgsf))) {
							row = row.getTable().newRow(row.getValues().clone());
							rows2.add(row);
							if (canApplyLimitOnline && --remaining == 0)
								break;
						}
					}
				} else {
					if (remaining != 0)
						for (Row row : rowsAfterIndex) {
							tg.currentRows[0] = row;
							if (Boolean.TRUE.equals(where2.get(tgsf))) {
								rows2.add(row);
								if (canApplyLimitOnline && --remaining == 0)
									break;
							}
						}
				}
				if (rows2.isEmpty() && joinTo == null)
					return null;
				rows = rows2;
			}
			tableRows.add(new Tuple2<String, List<Row>>(name, rows));
		}
		return tableRows;
	}
	public static com.f1.utils.structs.BasicMultiMap.List<String, TempIndex> determingIndexes(CalcFrameStack sf, final Map<String, Table> asTables,
			final Pointer<DerivedCellCalculator> wherePointer, final List<Tuple2<String, List<Row>>> tableRows, final SqlProcessor processor, boolean ForceOrdering,
			boolean allowIndexOnRight) {
		final SqlPlanListener planListener = sf.getSqlPlanListener();
		BasicMultiMap.List<String, TempIndex> tableNamesToIndexes = new BasicMultiMap.List<String, TempIndex>();
		if (asTables.size() > 1) {
			final IndexedList<String, List<IndexDef>> indexes = new BasicIndexedList<String, List<IndexDef>>();
			if (wherePointer != null)
				wherePointer.put(SqlProcessorSelectPlanner.determineIndexes(asTables, wherePointer.get(), indexes, ForceOrdering));
			Collections.sort(tableRows, new Comparator<Tuple2<String, List<Row>>>() {
				@Override
				public int compare(Tuple2<String, List<Row>> o1, Tuple2<String, List<Row>> o2) {
					int pos1 = indexes.getPositionNoThrow(o1.getKey());
					int pos2 = indexes.getPositionNoThrow(o2.getKey());
					if (pos1 == -1)
						pos1 = Integer.MAX_VALUE;
					if (pos2 == -1)
						pos2 = Integer.MAX_VALUE;
					return OH.compare(pos1, pos2);
				}
			});
			Map<String, List<Row>> tableNamesToRows = new HashMap<String, List<Row>>();
			Map<String, Integer> tableNamesToPositions = new HashMap<String, Integer>();
			int pos = 0;
			for (Tuple2<String, List<Row>> i : tableRows) {
				tableNamesToRows.put(i.getA(), i.getB());
				tableNamesToPositions.put(i.getA(), pos++);
			}

			for (List<IndexDef> idxByTable : indexes.values()) {
				for (IndexDef i : idxByTable) {
					final String targetTableName = i.getTargetTable();
					final String sourceTableName = i.getSourceTable();
					final List<Row> targetRows = tableNamesToRows.get(targetTableName);
					final List<Row> sourceRows = tableNamesToRows.get(sourceTableName);
					final String[] targetColumns = i.getTargetColumns();//TODO: composite indexes
					final String[] sourceColumns = i.getSourceColumns();
					final int cnt = targetColumns.length;
					final Table targetTable = asTables.get(targetTableName);
					final Table sourceTable = asTables.get(sourceTableName);
					final TempIndex tmpIndex;
					boolean checkExistingTargets = targetRows.size() > 10 && sourceRows.size() / targetRows.size() < 4;//if the sourceRows is more than  4x the number of target rows, it's not worth checking source rows for existence
					int targetTablePos = tableNamesToPositions.get(targetTableName);
					TempIndex idx = processor.getMutator().findIndex(sf, targetTable, targetColumns, targetTablePos, sourceTable, sourceColumns, targetRows);
					if (idx != null) {
						tableNamesToIndexes.putMulti(sourceTableName, idx);
						if (planListener != null)
							planListener.onStep("USING_INDEX", "On '" + targetTableName + "." + SH.join('+', targetColumns) + "'");
						continue;
					}
					if (tableRows.size() == 2 && indexes.getSize() == 1 && idxByTable.size() == 1 && allowIndexOnRight) {
						int tmpPos = targetTablePos;//Note: we use the targetTablePos, because we will be reversing source/target tables if this works
						idx = processor.getMutator().findIndex(sf, sourceTable, sourceColumns, targetTablePos, targetTable, targetColumns, sourceRows);
						if (idx != null) {
							tableNamesToIndexes.putMulti(targetTableName, idx);
							Collections.reverse(tableRows);
							if (planListener != null)
								planListener.onStep("USING_LARGER_INDEX", "On '" + sourceTableName + "." + SH.join('+', sourceColumns) + "'");
							continue;
						}
					}
					if (cnt == 1) {
						Column srcColumn = sourceTable.getColumn(sourceColumns[0]);
						Column tgtColumn = targetTable.getColumn(targetColumns[0]);
						int targetLocs = tgtColumn.getLocation();
						Caster<?> targetType = OH.getCaster(getBaseType(srcColumn.getType(), tgtColumn.getType()));
						int sourceLocs = srcColumn.getLocation();
						BasicMultiMap.List<Object, Row> mm = new BasicMultiMap.List<Object, Row>();
						if (checkExistingTargets) {
							HashSet<Object> sourceValues = new HashSet<Object>();
							for (Row row : sourceRows)
								sourceValues.add(targetType.cast(row.getAt(sourceLocs)));
							for (Row row : targetRows) {
								Object cast = targetType.cast(row.getAt(targetLocs));
								if (sourceValues.contains(cast))
									mm.putMulti(cast, row);
							}
						} else {
							for (Row row : targetRows) {
								Object cast = targetType.cast(row.getAt(targetLocs));
								mm.putMulti(cast, row);
							}
						}
						tmpIndex = new TempSingleIndex(sourceLocs, mm, targetTablePos, targetTableName, targetColumns[0], sourceTableName, sourceColumns[0], targetType);
					} else {
						int targetLocs[] = new int[cnt];
						Caster<?>[] targetTypes = new Caster<?>[cnt];
						int sourceLocs[] = new int[cnt];
						for (int j = 0; j < cnt; j++) {
							Column srcColumn = sourceTable.getColumn(sourceColumns[j]);
							Column tgtColumn = targetTable.getColumn(targetColumns[j]);
							sourceLocs[j] = srcColumn.getLocation();
							targetLocs[j] = tgtColumn.getLocation();
							Class<?> type = getBaseType(srcColumn.getType(), tgtColumn.getType());
							targetTypes[j] = OH.getCaster(type);
						}
						BasicMultiMap.List<Object[], Row> mm = new BasicMultiMap.List<Object[], Row>();
						mm.setInnerMap(new HasherMap<Object[], List<Row>>(ArrayHasher.INSTANCE));

						if (checkExistingTargets) {
							Set<Object[]> sourceValues = new HasherSet<Object[]>(ArrayHasher.INSTANCE, Math.max(128, sourceRows.size()));
							Object[] key = new Object[cnt];
							for (Row row : sourceRows) {
								for (int j = 0; j < cnt; j++)
									key[j] = targetTypes[j].cast(row.getAt(sourceLocs[j]));
								if (sourceValues.add(key))
									key = new Object[cnt];
							}

							for (Row row : targetRows) {
								for (int j = 0; j < cnt; j++)
									key[j] = targetTypes[j].cast(row.getAt(targetLocs[j]));
								if (sourceValues.contains(key))
									if (mm.putMulti(key, row).size() == 1)//this was the first insert, for this key, so generate a new object
										key = new Object[cnt];

							}
						} else {
							Object[] key = new Object[cnt];
							for (Row row : targetRows) {
								for (int j = 0; j < cnt; j++)
									key[j] = targetTypes[j].cast(row.getAt(targetLocs[j]));
								if (mm.putMulti(key, row).size() == 1)//this was the first insert, for this key, so generate a new object
									key = new Object[cnt];
							}
						}
						tmpIndex = new TempMultiIndex(sourceLocs, mm, targetTablePos, targetTableName, targetColumns, sourceTableName, sourceColumns, targetTypes);
					}
					tableNamesToIndexes.putMulti(sourceTableName, tmpIndex);
					if (planListener != null)
						planListener.onStep("TEMP_INDEX", "On '" + targetTableName + "." + SH.join('+', targetColumns) + "'");
				}
			}
		}
		return tableNamesToIndexes;

	}

	public static class Nearest {

		final public String rightTable;
		final public String leftTable;
		final public DerivedCellCalculator left;
		final public DerivedCellCalculator right;
		//		private final String leftColumn;
		//		private final String rightColumn;
		private final byte type;

		public Nearest(byte type, DerivedCellCalculator left, DerivedCellCalculator right, Map<String, Table> asTables, CalcFrameStack globalVars) {
			OH.assertEq(asTables.size(), 2);
			Iterator<Entry<String, Table>> it = asTables.entrySet().iterator();
			String leftTable = it.next().getKey();
			NameSpaceIdentifier l = findTableColumn(asTables, left, globalVars);
			NameSpaceIdentifier r = findTableColumn(asTables, right, globalVars);
			if (OH.eq(l.getNamespace(), r.getNamespace()))
				throw new ExpressionParserException(right.getPosition(),
						"NEAREST JOIN ON Expression must reference columns from distinct tables: " + l.getNamespace() + " vs " + r.getNamespace());
			if (leftTable.equals(r.getNamespace())) {
				NameSpaceIdentifier t = l;
				l = r;
				r = t;
				DerivedCellCalculator t2 = left;
				left = right;
				right = t2;
				switch (type) {
					case DerivedCellCalculatorMath.TYPE_STRING_GT:
						type = DerivedCellCalculatorMath.TYPE_STRING_LT;
						break;
					case DerivedCellCalculatorMath.TYPE_STRING_GE:
						type = DerivedCellCalculatorMath.TYPE_STRING_LE;
						break;
					case DerivedCellCalculatorMath.TYPE_STRING_LT:
						type = DerivedCellCalculatorMath.TYPE_STRING_GT;
						break;
					case DerivedCellCalculatorMath.TYPE_STRING_LE:
						type = DerivedCellCalculatorMath.TYPE_STRING_GE;
						break;
				}
			}
			this.left = left;
			this.right = right;
			this.type = type;
			this.leftTable = l.getNamespace();
			//			this.leftColumn = l.getB();
			this.rightTable = r.getNamespace();
			//			this.rightColumn = r.getB();
		}
	}

	static private NameSpaceIdentifier findTableColumn(Map<String, Table> asTables, DerivedCellCalculator name, CalcFrameStack sf) {
		if (name instanceof DerivedCellCalculatorRef)
			return findTableColumn(asTables, (DerivedCellCalculatorRef) name, sf);
		List<DerivedCellCalculatorRef> sink = new ArrayList<DerivedCellCalculatorRef>();
		DerivedHelper.find(name, DerivedCellCalculatorRef.class, sink);
		NameSpaceIdentifier base = null;
		for (int i = 0; i < sink.size(); i++) {
			NameSpaceIdentifier other = findTableColumn(asTables, sink.get(i), sf);
			if (other == null)
				continue;
			if (base == null)
				base = other;
			else if (OH.ne(other.getNamespace(), base.getNamespace()))
				throw new ExpressionParserException(name.getPosition(), "NEAREST JOIN ON Expression must reference only one table's columns: " + base + " vs " + other);
		}
		if (base == null)
			throw new ExpressionParserException(name.getPosition(), "NEAREST JOIN ON Expression must reference a column");
		return base;
	}
	static private NameSpaceIdentifier findTableColumn(Map<String, Table> asTables, DerivedCellCalculatorRef name, CalcFrameStack sf) {
		Object o = name.getId();
		if (o instanceof NameSpaceIdentifier) {
			NameSpaceIdentifier ns = (NameSpaceIdentifier) o;
			String table = ns.getNamespace();
			String col = ns.getVarName();
			Table t = asTables.get(table);
			if (t != null && t.getColumnIds().contains(col))
				return ns;
			throw new ExpressionParserException(name.getPosition(), "Column not found in table " + table + ": " + col);
		} else {
			final String str = (String) o;
			NameSpaceIdentifier r = null;
			for (Entry<String, Table> i : asTables.entrySet())
				if (i.getValue().getColumnIds().contains(str)) {
					if (r != null)
						throw new ExpressionParserException(name.getPosition(), "Ambigous Column: " + str);
					r = new NameSpaceIdentifier(i.getKey(), str);
				}
			if (r != null)
				return r;
			if (DerivedHelper.getType(sf, str) != null)
				return null;
			throw new ExpressionParserException(name.getPosition(), "Column not found: " + str);
		}
	}
}
