package com.f1.utils.sql;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Caster;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.ToStringable;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.sql.AnalyzeClause.WindowDef;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.SqlUnionNode;
import com.f1.utils.string.sqlnode.WhereNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class SqlProcessor_Analyze {

	public static final String ROWNUM = "__rownum";
	final private SqlProcessor owner;

	public SqlProcessor_Analyze(SqlProcessor sqlProcessor) {
		this.owner = sqlProcessor;
	}
	static public AnalyzeClause buildAnalyzeClause(SqlColumnsNode an) {

		//-- Break the clause into various parts
		AsNode[] columns = SqlProcessorUtils.toAsNode(an.getColumns());
		if (AH.isEmpty(columns))
			throw new ExpressionParserException(an.getPosition(), "Must specify at least one column");
		SqlColumnsNode from = (SqlColumnsNode) SqlExpressionParser.castNextToSqlNode(an, SqlExpressionParser.ID_FROM);
		if (from.getColumnsCount() != 1)
			throw new ExpressionParserException(from.getPosition(), "FROM clause must have one table: " + SH.join(',', from.getColumns()));
		WhereNode where;
		SqlNode next = (SqlNode) from.getNext();
		if (next != null && next.getOperation() == SqlExpressionParser.ID_WHERE) {
			throw new ExpressionParserException(next.getPosition(), "ANALYZE does not support WHERE at this time");
			//			where = JavaExpressionParser.castNode(next, WhereNode.class);
			//			next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlColumnsNode.class);
		} else
			where = null;
		Node ft = from.getColumnAt(0);
		//-- process each WINDOW clause and store in windows map
		Map<String, AnalyzeClause.WindowDef> windows = new LinkedHashMap<String, AnalyzeClause.WindowDef>();
		int nextIndexId = 0;
		while (next instanceof SqlNode && ((SqlNode) next).getOperation() == SqlExpressionParser.ID_WINDOW) {
			SqlColumnsNode windowNode = (SqlColumnsNode) next;
			if (windowNode.getColumnsCount() != 1)
				throw new ExpressionParserException(from.getPosition(), "WINDOW clause must have one name: " + SH.join(',', from.getColumns()));
			String name = windowNode.getColumnAt(0).toString();
			Node n = windowNode.getNext();
			Node onNode = null;
			Node[] orderBys = null;
			Node[] partitionBys = null;
			next = null;
			SqlNode n2 = n == null ? null : SqlExpressionParser.castNode(n, SqlNode.class);
			if (n2 != null && n2.getOperation() == SqlExpressionParser.ID_ON) {
				SqlColumnsNode onNodes = (SqlColumnsNode) SqlExpressionParser.castNextToSqlNode(windowNode, SqlExpressionParser.ID_ON);
				if (onNodes.getColumnsCount() != 1)
					throw new ExpressionParserException(from.getPosition(), "ON clause must have one expression: " + onNode);
				onNode = onNodes.getColumnAt(0);
				n = n2.getNext();
				n2 = n == null ? null : SqlExpressionParser.castNode(n, SqlNode.class);
			}
			if (n2 != null && n2.getOperation() == SqlExpressionParser.ID_PARTITIONBY) {
				partitionBys = ((SqlColumnsNode) n2).getColumns();
				n = n2.getNext();
				n2 = n == null ? null : SqlExpressionParser.castNode(n, SqlNode.class);
			}
			if (n2 != null && n2.getOperation() == SqlExpressionParser.ID_ORDERBY) {
				orderBys = ((SqlColumnsNode) n2).getColumns();
				n = n2.getNext();
				n2 = n == null ? null : SqlExpressionParser.castNode(n, SqlNode.class);
			}
			if (n2 != null)
				next = n2;
			if (windows.containsKey(name))
				throw new ExpressionParserException(windowNode.getColumnAt(0).getPosition(), "Duplicate WINDOW name: " + name);
			windows.put(name, new AnalyzeClause.WindowDef(name, onNode, orderBys, partitionBys, nextIndexId));
		}
		if (windows.size() == 0)
			throw new ExpressionParserException(from.getPosition(), "ANALYZE clause requires at least one WINDOW clause");
		SqlUnionNode union = null;
		if (next != null && next.getOperation() == SqlExpressionParser.ID_LIMIT)
			throw new ExpressionParserException(next.getPosition(), "ANALYZE does not support LIMIT at this time");
		if (next != null && next.getOperation() == SqlExpressionParser.ID_UNION) {
			union = JavaExpressionParser.castNode(next, SqlUnionNode.class);
			next = null;
		}
		if (next != null)
			throw new ExpressionParserException(next.getPosition(), "Unexpected operation: " + next);
		AsNode[] tables = new AsNode[] { SqlProcessorUtils.toAsNode(ft) };
		if (union == null) {
			return new AnalyzeClause(an, tables, columns, windows, null, false);
		} else {
			QueryClause qc = SqlProcessor.buildQueryClause(union.getNext());
			return new AnalyzeClause(an, tables, columns, windows, qc, union.isByName());
		}

	}
	public TableReturn processAnalyze(AnalyzeClause ac, CalcFrameStack sf, Map<String, Table> tables) {
		AsNode[] columns = ac.getSelects();
		Table table = tables.get(ac.getTables()[0].getValue().toString());

		//-- process each WINDOW clause and store in windows map
		columns = SqlProcessor_Select.expandWildCards(columns, Collections.singletonMap(table.getTitle(), table));
		Map<String, Window> windows = new LinkedHashMap<String, Window>();
		CalcTypes columnTypesMapping = table.getColumnTypesMapping();//SqlProcessorUtils.toTypes(table, sf.getGlobalVars().getTypes());
		int nextIndexId = 0;
		for (WindowDef window : ac.getWindows().values()) {
			Node onNode = window.getOnNode();
			Node[] orderBys = window.getOrderBys();
			Node[] partitionBys = window.getPartitionBys();
			String name = window.getName();
			final WindowMapper wm = new WindowMapper(name, columnTypesMapping);
			final DerivedCellCalculator on;
			if (onNode != null) {
				on = owner.getParser().toCalc(onNode, new ChildCalcTypesStack(sf, wm));
				if (on.getReturnType() != Boolean.class)
					throw new ExpressionParserException(onNode.getPosition(), "ON clause must return a boolean: " + on);
			} else
				on = null;

			Window w = new Window(owner.getParser(), table, name, on, orderBys, partitionBys, nextIndexId, sf);
			if (w.index != null)
				nextIndexId++;
			if (null != windows.put(name, w))
				throw new ExpressionParserException(on.getPosition(), "Duplicate WINDOW name: " + name);
		}
		if (windows.size() == 0)
			throw new ExpressionParserException(ac.getPosition(), "Must have at least one WINDOW");

		//-- Process each column to select in the analyze clause. 
		WindowsMapper wm = new WindowsMapper(windows.keySet(), columnTypesMapping);
		Class<?>[] types = new Class<?>[columns.length];
		String[] names = new String[columns.length];
		DerivedCellCalculator[] colCalcs = new DerivedCellCalculator[columns.length];
		Window[] windowForColumn = new Window[columns.length];
		AggregateFactory[] aggForColumn = new AggregateFactory[columns.length];
		AggregateFactory af = new AggregateFactory(sf.getFactory());
		Set<String> unusedWindows = new HashSet<String>(windows.keySet());
		HashSet<Object> tmpDeps = new HashSet<Object>();
		for (int i = 0; i < columns.length; i++) {
			wm.reset();
			AsNode n = columns[i];
			String name = n.getAs().toString();
			DerivedCellCalculator t = owner.getParser().toCalc(n.getValue(), new ChildCalcTypesStack(sf, true, wm, af));
			if (wm.hasMultipleReferences())
				throw new ExpressionParserException(ac.getPosition(), "Can only reference one window: " + t);
			if (!af.getAggregates().isEmpty()) {
				aggForColumn[i] = af;
				for (AggCalculator agg : af.getAggregates()) {
					if (!containsWindowVar(DerivedHelper.getDependencyIds(agg, CH.clear(tmpDeps))))
						throw new ExpressionParserException(agg.getPosition(), "Aggregate must reference at least one windowed variable: " + agg);
				}
				af = new AggregateFactory(sf.getFactory());
			} else if (containsWindowVar(DerivedHelper.getDependencyIds(t, CH.clear(tmpDeps)))) {
				for (Object s : tmpDeps)
					if (s.toString().indexOf('.') != -1)
						throw new ExpressionParserException(t.getPosition(), "window variable must be inside an aggregate function: " + s);
			}
			types[i] = t.getReturnType();
			names[i] = name;
			colCalcs[i] = t;
			String ref = wm.getReferencedWindow();
			if (ref != null) {
				windowForColumn[i] = windows.get(ref);
				unusedWindows.remove(ref);
			}
		}

		//-- Remove any windows that arent participating in any of the columns
		for (String s : unusedWindows)
			windows.remove(s);

		//-- For each unique PARTITION, break the rows up into partitions and store in 'partitions' map
		HasherMap<DerivedCellCalculator[], Map<Object[], Partition>> partitions = new HasherMap<DerivedCellCalculator[], Map<Object[], Partition>>(ArrayHasher.INSTANCE);
		ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
		for (Window w : windows.values()) {
			Entry<DerivedCellCalculator[], Map<Object[], Partition>> node = partitions.getOrCreateEntry(w.partitionBys);
			if (node.getValue() == null) {
				if (w.partitionBys == null) {
					node.setValue(Collections.singletonMap((Object[]) null, new Partition(table.getRows())));
				} else {
					DerivedCellCalculator[] groupby = w.partitionBys;
					HasherMap<Object[], Partition> groupedRows = new HasherMap<Object[], Partition>(ArrayHasher.INSTANCE);
					Object[] tmp = new Object[groupby.length];

					for (Row row : table.getRows()) {
						rsf.reset(row);
						for (int i = 0; i < tmp.length; i++)
							tmp[i] = groupby[i].get(rsf);
						HasherMap.Entry<Object[], Partition> entry = groupedRows.getOrCreateEntry(tmp);
						Partition rows = entry.getValue();
						if (rows == null) {
							entry.setValue(rows = new Partition(new ArrayList<Row>()));
							tmp = new Object[groupby.length];
						}
						rows.addRow(row);
					}
					node.setValue(groupedRows);
				}
			}
		}

		//-- For each PARTITION in above step, sort the results once for each unique ORDER BY clause. The resulting PARTITION+ORDERBY combinations are stored.
		//-- Note: this step and the above are broken up to avoid repartitioning in the case that multiple windows have equivalent PARTITION clauses with different ORDER BY clauses
		//-- Each window is associated with a partition, windows that have equivalent PARTITION-ORDERBY clauses will both point to the same partition
		HasherMap<Window, Partitioning> sortAndPartitions = new HasherMap<Window, Partitioning>();
		for (Window w : windows.values()) {
			Partitioning existing = sortAndPartitions.get(w);
			if (existing == null) {
				Map<Object[], Partition> rowsByPartition = partitions.get(w.partitionBys);
				if (w.orderBys != null) {
					Map<Object[], Partition> rowsByPartitionAfterSort = new HasherMap<Object[], Partition>(ArrayHasher.INSTANCE, rowsByPartition.size());
					for (java.util.Map.Entry<Object[], Partition> rows : rowsByPartition.entrySet()) {
						ArrayList<Row> rowsSorted = new ArrayList<Row>(rows.getValue().rows);
						Collections.sort(rowsSorted, w);
						rowsByPartitionAfterSort.put(rows.getKey(), new Partition(rowsSorted));
					}
					rowsByPartition = rowsByPartitionAfterSort;
				}
				existing = new Partitioning(w.partitionBys, rowsByPartition);
				sortAndPartitions.put(w, existing);
			}
			w.setPartitions(existing);
		}

		//-- Build table, for each row: (1) move each partitioning to the right partition (2) For each window, apply the ON clause (3) run aggs (4) process columns
		Table r = SqlProjector.newBasicTable(types, names);
		RowJoiner mapper = new RowJoiner(null, null, table.getColumnTypesMapping());
		Partitioning[] sortAndPartitionsArray = AH.toArray((Collection<Partitioning>) sortAndPartitions.values(), Partitioning.class);
		Window[] windowsArray = AH.toArray(windows.values(), Window.class);
		final TimeoutController timeoutController = sf.getTimeoutController();
		for (Row row : table.getRows()) {
			rsf.reset(row);
			if (timeoutController != null)
				timeoutController.throwIfTimedout();
			for (Partitioning i : sortAndPartitionsArray)
				i.setCurrentPartition(rsf);
			for (Window i : windowsArray)
				i.applyOnExpression(row);
			Object[] values = new Object[colCalcs.length];
			for (int i = 0; i < colCalcs.length; i++) {
				Window w = windowForColumn[i];
				AggregateFactory a = aggForColumn[i];
				if (w != null && a != null) {
					for (AggCalculator ag : a.getAggregates()) {
						ag.reset();
						ag.visitRows(rsf, w.getRows());
					}
				}
				mapper.resetRow(row);
				values[i] = colCalcs[i].get(rsf);
			}
			r.getRows().addRow(values);
		}
		return new TableReturn(r);
	}
	private static boolean containsWindowVar(Set<Object> dependencyIds) {
		for (Object o : dependencyIds)
			if (o.toString().indexOf('.') != -1)
				return true;
		return false;
	}

	public static class Partition {
		public List<Row> rows;
		private SortedValues indexed[] = new SortedValues[1];

		public Partition(List<Row> rows) {
			this.rows = rows;
		}

		public void addRow(Row row) {
			this.rows.add(row);
		}

		public List<Row> getForIndex(Index index, CalcFrame row, ReusableCalcFrameStack sf) {
			if (index == null || indexed == null)
				return rows;
			SortedValues values = indexed[index.indexPosition];//this should be faster
			return index.extract(values, sf.reset(row));
		}

		public void prepareIndex(RowJoiner rowsMapper, Index index, CalcFrameStack sf) {
			if (indexed == null)
				return;

			final ArrayList<KeyRow> values = new ArrayList<SqlProcessor_Analyze.KeyRow>(this.rows.size());
			ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf, rowsMapper);
			for (Row i : this.rows) {
				rowsMapper.resetWindowRow(i);
				Comparable<?> val = index.cast(index.name.get(rsf));
				values.add(new KeyRow(val, i));
			}
			if (index.indexPosition >= this.indexed.length)
				this.indexed = Arrays.copyOf(indexed, index.indexPosition + 1);
			indexed[index.indexPosition] = new SortedValues(values);
		}
	}

	private static class KeyRow implements Comparable<KeyRow> {

		public final Comparable<?> key;
		public final Row row;

		public KeyRow(Comparable<?> c, Row row) {
			this.key = c;
			this.row = row;
		}
		@Override
		public int compareTo(KeyRow o) {
			return OH.compare(this.key, o.key);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof KeyRow && compareTo((KeyRow) obj) == 0;
		}

	}

	public static class SortedValues {
		final private Comparable<?>[] keys;
		final private int[] keysToFirstRow;
		final private Row[] rows;
		final private int keysSize;

		public SortedValues(ArrayList<KeyRow> values) {
			Collections.sort(values);
			int size = values.size();
			this.keys = new Comparable[size];
			this.keysToFirstRow = new int[size];
			this.rows = new Row[size];
			Comparable last = null;
			int keysSize = 0;
			for (int i = 0; i < size; i++) {
				KeyRow t = values.get(i);
				if (i == 0 || OH.ne(t.key, last)) {
					this.keys[keysSize] = t.key;
					this.keysToFirstRow[keysSize] = i;
					last = t.key;
					keysSize++;
				}
				this.rows[i] = t.row;
			}
			this.keysSize = keysSize;
		}
		public List get(Comparable<?> value) {
			int start = AH.indexOfSorted(value, keys, this.keysSize);
			if (start == -1)
				return empty();
			return lw(start, start);
		}
		public List<Row> getSubmap(Comparable lower, boolean lowerInclusive, Comparable upper, boolean upperInclusive) {
			int upperPos = upperInclusive ? AH.indexOfSortedLessThanEqualTo(upper, keys, this.keysSize) : AH.indexOfSortedLessThan(upper, keys, this.keysSize);
			if (upperPos == -1)
				return empty();
			int lowerPos = lowerInclusive ? AH.indexOfSortedGreaterThanEqualTo(lower, keys, upperPos + 1) : AH.indexOfSortedGreaterThan(lower, keys, upperPos + 1);
			if (lowerPos == -1)
				return empty();
			return lw(lowerPos, upperPos);
		}
		public List<Row> getTail(Comparable lower, boolean lowerInclusive) {
			int lowerPos = lowerInclusive ? AH.indexOfSortedGreaterThanEqualTo(lower, keys, this.keysSize) : AH.indexOfSortedGreaterThan(lower, keys, this.keysSize);
			if (lowerPos == -1)
				return empty();
			return lw(lowerPos, this.keysSize - 1);
		}
		public List<Row> getHead(Comparable upper, boolean upperInclusive) {
			int upperPos = upperInclusive ? AH.indexOfSortedLessThanEqualTo(upper, keys, this.keysSize) : AH.indexOfSortedLessThan(upper, keys, this.keysSize);
			if (upperPos == -1)
				return empty();
			return lw(0, upperPos);
		}
		private List<Row> empty() {
			return Collections.emptyList();
		}
		private List<Row> lw(int head, int tail) {
			return new ListWrapper(keysToFirstRow(head), keysToFirstRow(tail + 1), this.rows);
		}
		private int keysToFirstRow(int start) {
			return start == keysSize ? rows.length : keysToFirstRow[start];
		}

	}

	public static class ListWrapper extends AbstractList<Row> {

		final private Row[] inner;
		final private int start, size;

		private ListWrapper(int start, int end, Row[] inner) {
			this.inner = inner;
			this.start = start;
			this.size = end - start;
		}

		@Override
		public Row get(int index) {
			return inner[index + start];
		}

		@Override
		public int size() {
			return size;
		}

	}

	public static class Partitioning {
		public final Map<Object[], Partition> partitions;
		public Partition currentPartition;
		private Object[] tmp;
		private DerivedCellCalculator[] partitionBys;

		public Partitioning(DerivedCellCalculator partitionBys[], Map<Object[], Partition> partitions) {
			this.partitions = partitions;
			this.partitionBys = partitionBys;
			if (partitionBys == null)
				this.currentPartition = partitions.get(null);
			else
				tmp = new Object[partitionBys.length];
		}
		public void setCurrentPartition(ReusableCalcFrameStack sf) {
			if (partitionBys != null) {
				for (int i = 0; i < partitionBys.length; i++)
					tmp[i] = partitionBys[i].get(sf);
				this.currentPartition = partitions.get(tmp);
			}
			OH.assertNotNull(this.currentPartition);
		}

		public Partition getCurrentPartition() {
			return currentPartition;
		}
		public List<Row> getForIndex(Index index, CalcFrame row, ReusableCalcFrameStack rsf) {
			return currentPartition.getForIndex(index, row, rsf);
		}
		public void prepareIndex(RowJoiner rowsMapper, Index index, CalcFrameStack sf) {
			if (index != null) {
				for (Partition i : partitions.values()) {
					i.prepareIndex(rowsMapper, index, sf);
				}
			}
		}

	}

	public static class Window implements ToStringable, Comparator<Row> {

		final public String name;
		final public DerivedCellCalculator[] orderBys;
		final public DerivedCellCalculator[] partitionBys;
		final public boolean[] orderBysAsc;
		final private DerivedCellCalculator on;
		final private int hc;
		public Partitioning partitioning;
		final private RowsListMapper rowsWrapper;
		final private List<Row> rows = new ArrayList<Row>();
		final private RowJoiner wrapper;
		private String nameDot;
		private Index index;
		private ReusableCalcFrameStack rsf;

		public Window(SqlDerivedCellParser p, Table table, String name, DerivedCellCalculator on, Node[] orderBys, Node[] partitionBys, int indexId, CalcFrameStack sf) {
			this.wrapper = new RowJoiner(name, (Set) table.getColumnIds(), table.getColumnTypesMapping());
			rowsWrapper = new RowsListMapper(name, (Set) table.getColumnIds(), table.getColumnTypesMapping());
			this.rsf = new ReusableCalcFrameStack(sf);
			this.name = name;
			this.nameDot = name + ".";
			if (on != null) {
				this.on = on;
				List<DerivedCellCalculator> sink = new ArrayList<DerivedCellCalculator>();
				SqlProcessorSelectPlanner.toAnds(on, sink);
				if (!sink.isEmpty()) {
					Set<DerivedCellCalculatorMath> sink2 = new HashSet<DerivedCellCalculatorMath>();
					for (DerivedCellCalculator i : sink)
						determineIndex(i, nameDot, sink2);
					if (!sink2.isEmpty()) {
						this.index = findBestIndex(sink2, indexId, sink.size() == sink2.size());
					}
				}
			} else
				this.on = null;
			CalcTypesStack context2 = new ChildCalcTypesStack(sf, true, table.getColumnTypesMapping());
			if (partitionBys != null) {
				this.partitionBys = new DerivedCellCalculator[partitionBys.length];
				for (int i = 0; i < partitionBys.length; i++)
					this.partitionBys[i] = p.toCalc(partitionBys[i], context2);
			} else
				this.partitionBys = null;
			if (orderBys != null) {
				this.orderBys = new DerivedCellCalculator[orderBys.length];
				this.orderBysAsc = new boolean[orderBys.length];
				for (int i = 0; i < orderBys.length; i++) {
					SqlColumnsNode node = (SqlColumnsNode) orderBys[i];
					orderBysAsc[i] = node.getOperation() == SqlExpressionParser.ID_ASC;
					this.orderBys[i] = p.toCalc(node.getNext(), context2);
				}
			} else {
				this.orderBys = null;
				this.orderBysAsc = null;
			}
			hc = OH.hashCode(Arrays.hashCode(orderBysAsc), Arrays.hashCode(orderBys), Arrays.hashCode(partitionBys));

		}
		public RowsListMapper getRows() {
			return this.rowsWrapper;
		}
		public void applyOnExpression(final Row currentRow) {
			this.rows.clear();
			wrapper.resetRow(currentRow);
			rsf.reset(wrapper);
			if (on == null) {
				rows.addAll(this.partitioning.getForIndex(null, null, rsf));
			} else if (on.isConst()) {
				if (Boolean.TRUE.equals(on.get(null)))
					CH.l(rows, this.partitioning.getForIndex(null, null, rsf));
			} else if (this.index != null) {
				wrapper.resetRow(currentRow);
				List<Row> rows = this.partitioning.getForIndex(this.index, wrapper, rsf);
				if (this.index.isFullyIndexed()) {
					this.rows.addAll(rows);
				} else {
					for (int i = 0, n = rows.size(); i < n; i++) {
						Row row = rows.get(i);
						wrapper.resetWindowRow(row);
						if (Boolean.TRUE.equals(on.get(rsf))) {
							this.rows.add(row);
						}
					}
				}
			} else {
				List<Row> rows = this.partitioning.getForIndex(null, null, rsf);
				for (int i = 0, n = rows.size(); i < n; i++) {
					Row row = rows.get(i);
					wrapper.resetWindowRow(row);
					if (Boolean.TRUE.equals(on.get(rsf))) {
						this.rows.add(row);
					}
				}
			}
			this.rowsWrapper.reset(currentRow, this.rows);
		}
		public void setPartitions(Partitioning partitioning) {
			this.partitioning = partitioning;
			if (this.index != null) {
				this.partitioning.prepareIndex(this.rowsWrapper.rowsMapper, this.index, rsf);
			}
		}
		@Override
		public StringBuilder toString(StringBuilder sink) {
			sink.append(name).append(" ON ").append(on);
			if (partitionBys != null)
				SH.join(", ", partitionBys, sink.append(" PARTITION BY "));
			if (orderBys != null)
				SH.join(", ", orderBys, sink.append(" ORDER BY "));
			return sink;
		}
		public String toString() {
			return toString(new StringBuilder()).toString();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != Window.class)
				return false;
			Window o = (Window) obj;
			return AH.eq(orderBysAsc, o.orderBysAsc) && AH.eq(orderBys, o.orderBys) && AH.eq(partitionBys, o.partitionBys);
		}
		@Override
		public int hashCode() {
			return hc;
		}
		@Override
		public int compare(Row o1, Row o2) {
			for (int i = 0; i < orderBys.length; i++) {
				DerivedCellCalculator o = orderBys[i];
				int t = OH.compare((Comparable) o.get(this.rsf.reset(o1)), (Comparable) o.get(this.rsf.reset(o2)));
				if (t != 0)
					return t > 0 == orderBysAsc[i] ? 1 : -1;
			}
			return 0;
		}
	}

	//	static private DerivedCellCalculator[] toCalcs(SqlDerivedCellParser p, Node[] n, Table table) {
	//		if (n == null)
	//			return null;
	//		DerivedCellCalculator[] r = new DerivedCellCalculator[n.length];
	//		for (int i = 0; i < n.length; i++)
	//			r[i] = p.toCalc(n[i], table.getColumnTypesMapping(), p.getSqlProcessor().getMethodFactory());
	//		return r;
	//	}

	public static class WindowMapper implements CalcTypes {

		private Map<String, String> mappings = new HashMap<String, String>();
		private CalcTypes inner;
		private String rownumName;

		public WindowMapper(String name, CalcTypes columnTypesMapping) {
			for (String s : columnTypesMapping.getVarKeys()) {
				mappings.put(name + "." + s, s);
				mappings.put(s, s);
			}
			this.rownumName = name + "." + ROWNUM;
			this.inner = columnTypesMapping;
		}

		@Override
		public Class<?> getType(String key) {
			if (rownumName.equals(key) || ROWNUM.equals(key))
				return Integer.class;
			String k = mappings.get(key);
			return inner.getType(k == null ? key : k);
		}

		@Override
		public Iterable<String> getVarKeys() {
			return mappings.keySet();
		}

		@Override
		public boolean isVarsEmpty() {
			return mappings.isEmpty();
		}

		@Override
		public int getVarsCount() {
			return mappings.size();
		}

	}

	public static class WindowsMapper implements CalcTypes {

		private Map<String, String> mappings = new HashMap<String, String>();
		private Map<String, String> referenced = new HashMap<String, String>();
		private CalcTypes inner;
		private Set<String> rowNumNames = new HashSet<String>();
		private String referencedWindow;
		private boolean hasMultipleReferences;

		public WindowsMapper(Iterable<String> windows, CalcTypes inner) {
			for (String windowName : windows) {
				for (String s : inner.getVarKeys()) {
					String fullname = windowName + "." + s;
					mappings.put(fullname, s);
					referenced.put(fullname, windowName);
					mappings.put(s, s);
				}
				this.rowNumNames.add(windowName + "." + ROWNUM);
			}
			this.rowNumNames.add(ROWNUM);
			this.inner = inner;
		}

		@Override
		public Class<?> getType(String key) {
			if (rowNumNames.contains(key))
				return Integer.class;
			String k = mappings.get(key);
			Class<?> r = inner.getType(k == null ? key : k);
			if (r != null) {
				String ref = referenced.get(key);
				if (ref != null) {
					if (this.referencedWindow != null && OH.ne(this.referencedWindow, ref))
						hasMultipleReferences = true;
					else
						this.referencedWindow = ref;
				}
			}
			return r;
		}
		@Override
		public Iterable<String> getVarKeys() {
			return mappings.keySet();
		}

		public void reset() {
			this.referencedWindow = null;
			this.hasMultipleReferences = false;
		}

		public String getReferencedWindow() {
			return this.referencedWindow;
		}

		public boolean hasMultipleReferences() {
			return hasMultipleReferences;
		}

		@Override
		public boolean isVarsEmpty() {
			return false;
		}

		@Override
		public int getVarsCount() {
			return mappings.size();
		}

	}

	public static class RowsListMapper extends AbstractList<CalcFrame> {

		private List<Row> rows;
		private RowJoiner rowsMapper;

		public RowsListMapper(String name, Iterable<String> cols, CalcTypes columnTypes) {
			this.rowsMapper = new RowJoiner(name, cols, columnTypes);
		}

		public void reset(Row row1, List<Row> rows) {
			this.rowsMapper.resetRow(row1);
			this.rows = rows;
		}
		@Override
		public CalcFrame get(int index) {
			this.rowsMapper.resetWindowRow(this.rows.get(index));
			return this.rowsMapper;
		}

		@Override
		public int size() {
			return this.rows.size();
		}

	}

	public static class RowJoiner implements CalcFrame {
		private Row windowedRow;
		private Map<String, String> mapping = new HashMap<String, String>();
		private String otherRownumName;
		private BasicCalcTypes types;
		private Row row;

		public RowJoiner(String name, Iterable<String> cols, CalcTypes columnTypes) {
			this.types = new BasicCalcTypes(columnTypes);
			for (String e : columnTypes.getVarKeys())
				this.types.putType(name + "." + e, columnTypes.getType(e));
			if (name != null) {
				for (String col : cols)
					mapping.put(name + "." + col, col);
				this.otherRownumName = name + "." + ROWNUM;
			}
		}
		void resetRow(Row row) {
			this.row = row;
		}
		void resetWindowRow(Row windowedRow) {
			this.windowedRow = windowedRow;
		}
		@Override
		public Object getValue(String key) {
			String t = mapping.get(key);
			if (t != null)
				return windowedRow.get(t);
			else {
				if (ROWNUM.equals(key))
					return OH.valueOf(row.getLocation());
				else if (otherRownumName != null && otherRownumName.equals(key))
					return OH.valueOf(windowedRow.getLocation());
				else
					return row.get(key);
			}
		}

		@Override
		public Object putValue(String key, Object value) {
			throw new UnsupportedOperationException();
		}
		@Override
		public Class<?> getType(String key) {
			return types.getType(key);
		}
		@Override
		public boolean isVarsEmpty() {
			return types.isVarsEmpty();
		}
		@Override
		public Iterable<String> getVarKeys() {
			return types.getVarKeys();
		}
		@Override
		public int getVarsCount() {
			return types.getVarsCount();
		}
	}

	//sink will always be: windowname.indexcolname operation expression
	static private void determineIndex(DerivedCellCalculator calc, String windowNameDot, Set<DerivedCellCalculatorMath> sink) {
		if (!(calc instanceof DerivedCellCalculatorMath))
			return;
		final DerivedCellCalculatorMath c = (DerivedCellCalculatorMath) calc;
		byte op = c.getOperationNodeCode();
		byte revOp;
		switch (op) {
			case OperationNode.OP_EQ_EQ:
				revOp = OperationNode.OP_EQ_EQ;
				break;
			case OperationNode.OP_GT_EQ:
				revOp = OperationNode.OP_LT_EQ;
				break;
			case OperationNode.OP_LT_EQ:
				revOp = OperationNode.OP_GT_EQ;
				break;
			case OperationNode.OP_GT:
				revOp = OperationNode.OP_LT;
				break;
			case OperationNode.OP_LT:
				revOp = OperationNode.OP_GT;
				break;
			default:
				return;
		}
		if (onlyDependsOnTable(c.getLeft(), windowNameDot) && !dependOnTable(c.getRight(), windowNameDot))
			sink.add(c);
		if (onlyDependsOnTable(c.getRight(), windowNameDot) && !dependOnTable(c.getLeft(), windowNameDot)) {
			DerivedCellCalculator t;
			t = (DerivedCellCalculatorMath.valueOf(c.getPosition(), revOp, c.getRight(), c.getLeft()));
			if (t instanceof DerivedCellCalculatorMath)
				sink.add((DerivedCellCalculatorMath) t);
		}
	}
	private static boolean onlyDependsOnTable(DerivedCellCalculator left, String windowNameDot) {
		Set<Object> t = DerivedHelper.getDependencyIds(left);
		if (t.isEmpty())
			return false;
		for (Object i : t)
			if (!i.toString().startsWith(windowNameDot))
				return false;
		return true;
	}
	private static boolean dependOnTable(DerivedCellCalculator c, String windowNameDot) {
		for (Object i : DerivedHelper.getDependencyIds(c))
			if (i.toString().startsWith(windowNameDot))
				return true;
		return false;
	}
	static private Index findBestIndex(Set<DerivedCellCalculatorMath> sink, int indexId, boolean mightBeFullyIndexable) {
		if (sink.isEmpty())
			return null;
		Map<DerivedCellCalculator, DerivedCellCalculatorMath> upperBound = new HashMap<DerivedCellCalculator, DerivedCellCalculatorMath>();
		Map<DerivedCellCalculator, DerivedCellCalculatorMath> lowerBound = new HashMap<DerivedCellCalculator, DerivedCellCalculatorMath>();
		for (DerivedCellCalculatorMath i : sink)
			if (i.getOperationNodeCode() == OperationNode.OP_EQ_EQ)
				return new Index(i, indexId, mightBeFullyIndexable && sink.size() == 1);
		for (DerivedCellCalculatorMath i : sink) {
			DerivedCellCalculator candidateIndex = i.getLeft();
			boolean lower = i.getOperationNodeCode() == OperationNode.OP_GT_EQ || i.getOperationNodeCode() == OperationNode.OP_GT;
			if (lower) {
				DerivedCellCalculatorMath t = upperBound.get(candidateIndex);
				if (t != null)
					return new Index(i, t, indexId, mightBeFullyIndexable && sink.size() == 2);
				lowerBound.put(candidateIndex, i);
			} else {
				DerivedCellCalculatorMath t = lowerBound.get(candidateIndex);
				if (t != null)
					return new Index(t, i, indexId, mightBeFullyIndexable && sink.size() == 2);
				upperBound.put(candidateIndex, i);
			}
		}
		if (!lowerBound.isEmpty()) {
			DerivedCellCalculatorMath t = CH.first(lowerBound.values());
			return new Index(t, null, indexId, mightBeFullyIndexable && sink.size() == 1);
		}
		if (!upperBound.isEmpty()) {
			DerivedCellCalculatorMath t = CH.first(upperBound.values());
			return new Index(null, t, indexId, mightBeFullyIndexable && sink.size() == 1);
		}
		return null;
	}

	public static class Index implements ToStringable {
		final public DerivedCellCalculator lower;
		final public DerivedCellCalculator upper;
		final public DerivedCellCalculator equals;
		final public boolean lowerInclusive;
		final public boolean upperInclusive;
		final public DerivedCellCalculator name;
		final private Caster<? extends Comparable> caster;
		final private int indexPosition;
		final private boolean fullyIndexed;

		public Index(DerivedCellCalculatorMath equals, int indexId, boolean fullyIndexed) {
			this.fullyIndexed = fullyIndexed;
			this.name = equals.getLeft();
			if (!Comparable.class.isAssignableFrom(name.getReturnType()))
				throw new ExpressionParserException(name.getPosition(), "Can only index on ordinals: " + this.name);
			this.equals = equals.getRight();
			this.caster = OH.getCaster((Class<? extends Comparable>) getWidestIgnoreNull(name.getReturnType(), this.equals.getReturnType()));
			this.lower = null;
			this.upper = null;
			this.lowerInclusive = false;
			this.upperInclusive = false;
			this.indexPosition = indexId;
		}
		public boolean isFullyIndexed() {
			return this.fullyIndexed;
		}
		public List<Row> extract(SortedValues values, ReusableCalcFrameStack sf) {//TODO: Performance, we should flatten map out first
			if (this.equals != null) {
				Comparable value = (Comparable) this.equals.get(sf);
				List r = values.get(value);
				if (r == null)
					return Collections.EMPTY_LIST;
				return r;
			} else if (this.lower != null && this.upper != null) {
				return values.getSubmap(cast(lower.get(sf)), lowerInclusive, cast(upper.get(sf)), upperInclusive);
			} else if (this.lower != null) {
				return values.getTail(cast(lower.get(sf)), lowerInclusive);
			} else {
				return values.getHead(cast(upper.get(sf)), upperInclusive);
			}
		}
		private Comparable cast(Object object) {
			return this.caster.cast(object);
		}
		public Index(DerivedCellCalculatorMath lower, DerivedCellCalculatorMath upper, int indexId, boolean fullyIndexed) {
			if (lower != null && upper != null)
				OH.assertEq(lower.getLeft(), upper.getLeft());
			this.fullyIndexed = fullyIndexed;
			this.name = lower == null ? upper.getLeft() : lower.getLeft();
			if (!Comparable.class.isAssignableFrom(name.getReturnType()))
				throw new ExpressionParserException(name.getPosition(), "Can only index on ordinals: " + this.name);
			this.equals = null;
			this.lower = lower == null ? null : lower.getRight();
			this.upper = upper == null ? null : upper.getRight();
			this.lowerInclusive = lower != null && lower.getOperationNodeCode() == OperationNode.OP_GT_EQ;
			this.upperInclusive = upper != null && upper.getOperationNodeCode() == OperationNode.OP_LT_EQ;
			this.indexPosition = indexId;
			this.caster = OH.getCaster((Class<? extends Comparable>) getWidestIgnoreNull(name.getReturnType(),
					getWidestIgnoreNull(lower == null ? null : this.lower.getReturnType(), upper == null ? null : this.upper.getReturnType())));
		}

		public String toString() {
			return toString(new StringBuilder()).toString();
		}
		@Override
		public StringBuilder toString(StringBuilder sink) {
			final StringBuilder r = new StringBuilder();
			name.toString(r);
			r.append(" ON ");
			if (lower != null) {
				r.append(lowerInclusive ? " >= " : " > ");
				lower.toString(r);
			}
			if (upper != null) {
				if (lower != null)
					r.append(" AND ");
				r.append(upperInclusive ? " <= " : " < ");
				upper.toString(r);
			}
			if (equals != null) {
				r.append(" == ");
				equals.toString(r);
			}

			return r;
		}

	}

	private static Class getWidestIgnoreNull(Class i, Class j) {
		if (i == null)
			return j;
		if (j == null)
			return i;
		if (i == String.class | j == String.class)
			return String.class;
		if (i == Double.class | j == Double.class)
			return Double.class;
		if (i == Float.class | j == Float.class)
			return Double.class;
		if (i == Long.class | j == Long.class)
			return Long.class;
		if (i == Integer.class | j == Integer.class)
			return Integer.class;
		if (i == Integer.class | j == Integer.class)
			return Integer.class;
		if (i == Short.class | j == Short.class)
			return Short.class;
		if (i == Byte.class | j == Byte.class)
			return Short.class;
		if (i == Character.class | j == Character.class)
			return Short.class;
		throw new RuntimeException("Unknown types: " + i + " and " + j);

	}
}
