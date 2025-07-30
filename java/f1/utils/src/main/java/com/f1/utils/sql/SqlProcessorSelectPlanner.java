package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class SqlProcessorSelectPlanner {

	public static void main(String a[]) {
		SqlProcessor sp = new SqlProcessor(null);
		Tableset tables = new TablesetImpl();
		Table tableA = new BasicTable(Integer.class, "a1", Integer.class, "a2");
		Table tableB = new BasicTable(Integer.class, "b1", Integer.class, "b2");
		Table tableC = new BasicTable(Integer.class, "c1", Integer.class, "c2");
		Table tableD = new BasicTable(Integer.class, "d1", Integer.class, "d2");
		tableA.getRows().addRow(1, 10);
		tableA.getRows().addRow(1, 12);
		tableA.getRows().addRow(1, 17);
		tableA.getRows().addRow(7, 14);

		tableB.getRows().addRow(1, 14);
		tableB.getRows().addRow(1, 12);
		tableB.getRows().addRow(6, 14);
		tableB.getRows().addRow(6, 14);
		tableB.getRows().addRow(1, 1);
		tableB.getRows().addRow(2, 15);

		tableC.getRows().addRow(14, 1);
		tableC.getRows().addRow(1, 12);

		tableD.getRows().addRow(1, 14);
		tableD.getRows().addRow(1, 12);

		tables.putTable("a", tableA);
		tables.putTable("b", tableB);
		tables.putTable("c", tableC);
		tables.putTable("d", tableD);
		Table r = sp.process("Select * from a as asddf,b where (a1==b1) and a2==b2", new TopCalcFrameStack(tables, new BasicMethodFactory(), EmptyCalcFrame.INSTANCE));
		System.out.println(r);
	}
	// Returns the portions of the where clause that are table specific. The remaining parts of the where table (those that have 
	// cross/mixed table references) are returned as the null entry. If forPostJoin is true, then only basic column <operator> non-nullconstant expressions are included
	public static Map<String, DerivedCellCalculator> findPreJoinWheres(Map<String, Table> asTables, DerivedCellCalculator where, CalcFrameStack sf, boolean forPostJoin) {
		if (where == null)
			return Collections.EMPTY_MAP;
		Map<String, String> columnsToTables = new HashMap<String, String>();
		Map<String, DerivedCellCalculator> r = new HashMap<String, DerivedCellCalculator>();
		for (Entry<String, Table> i : asTables.entrySet()) {
			for (Object col : i.getValue().getColumnIds())
				if (columnsToTables.containsKey((String) col)) {
					columnsToTables.put((String) col, null);
				} else
					columnsToTables.put((String) col, i.getKey());
		}
		List<DerivedCellCalculator> ands = new ArrayList<DerivedCellCalculator>();
		toAnds(where, ands);
		Set<Object> sink = new HashSet<Object>();
		for (DerivedCellCalculator i : ands) {
			if (forPostJoin && !canPrefilterForPostJoin(i))
				continue;
			sink.clear();
			String associatedTable = null;
			DerivedHelper.getDependencyIds(i, sink);
			for (Object o : sink) {
				String table;
				if (o instanceof NameSpaceIdentifier)
					table = ((NameSpaceIdentifier) o).getNamespace();
				else
					table = columnsToTables.get((String) o);
				if (table == null) {
					if (DerivedHelper.getType(sf, (String) o) != null)
						continue;
					throw new RuntimeException("bad column: " + o);
				}
				if (associatedTable == null)
					associatedTable = table;
				else if (OH.ne(associatedTable, table)) {
					associatedTable = null;
					break;
				}
			}
			DerivedCellCalculator existing = r.put(associatedTable, i);

			if (existing != null)
				r.put(associatedTable, DerivedCellCalculatorMath.valueOf(i.getPosition(), OperationNode.OP_AMP_AMP, existing, i));
		}
		return r;
	}
	private static boolean canPrefilterForPostJoin(DerivedCellCalculator i) {
		if (i instanceof DerivedCellCalculatorMath) {
			DerivedCellCalculatorMath dccm = (DerivedCellCalculatorMath) i;
			if (dccm.getLeft() instanceof DerivedCellCalculatorRef && dccm.getRight().isConst() && dccm.getRight().get(null) != null)
				return true;
			if (dccm.getRight() instanceof DerivedCellCalculatorRef && dccm.getLeft().isConst() && dccm.getLeft().get(null) != null)
				return true;
		} else if (i instanceof DerivedCellCalculator_SqlIn || i instanceof DerivedCellCalculator_SqlInSingle) {
			return true;
		}
		return false;
	}

	public static void toAnds(DerivedCellCalculator calc, List<DerivedCellCalculator> sink) {
		if (calc instanceof DerivedCellCalculatorMath && ((DerivedCellCalculatorMath) calc).getOperationNodeCode() == OperationNode.OP_AMP_AMP) {
			final DerivedCellCalculatorMath c = (DerivedCellCalculatorMath) calc;
			final DerivedCellCalculator left = c.getLeft();
			final DerivedCellCalculator rght = c.getRight();
			toAnds(left, sink);
			toAnds(rght, sink);
		} else if (calc != null)
			sink.add(calc);
	}
	private static DerivedCellCalculator toAndsForIndex(DerivedCellCalculator calc, List<DerivedCellCalculatorMath> sink) {
		if (calc instanceof DerivedCellCalculatorMath) {
			final DerivedCellCalculatorMath c = (DerivedCellCalculatorMath) calc;
			if (c.getOperationNodeCode() == OperationNode.OP_AMP_AMP) {
				final DerivedCellCalculator left = c.getLeft();
				final DerivedCellCalculator rght = c.getRight();
				DerivedCellCalculator l = toAndsForIndex(left, sink);
				DerivedCellCalculator r = toAndsForIndex(rght, sink);
				if (l == null)
					return r;
				if (r == null)
					return l;
				if (l == left && r == rght)
					return c;
				return DerivedCellCalculatorMath.valueOf(calc.getPosition(), OperationNode.OP_AMP_AMP, l, r);
			} else if (c.getOperationNodeCode() == OperationNode.OP_EQ_EQ) {
				if (c.getLeft() instanceof DerivedCellCalculatorRef && c.getRight() instanceof DerivedCellCalculatorRef) {
					sink.add(c);
					return null;
				}
			}
		}
		return calc;
	}
	//Each Entry is: smaller_table -> [smaller_column,larger_table,larger_column]

	public static DerivedCellCalculator determineIndexes(Map<String, Table> asTables, DerivedCellCalculator where, IndexedList<String, List<IndexDef>> indexesSink,
			boolean forceOrdering) {
		final Map<String, String> columnsToTables = getUniqueColumnNames(asTables);
		final List<DerivedCellCalculatorMath> ands = new ArrayList<DerivedCellCalculatorMath>();
		where = toAndsForIndex(where, ands);

		final Map<Tuple2<String, String>, TempIndex> indexes = new LinkedHashMap<Tuple2<String, String>, SqlProcessorSelectPlanner.TempIndex>();
		final Map<String, TableNode> tableNodes = new LinkedHashMap<String, TableNode>();

		for (DerivedCellCalculatorMath i : ands) {
			Object id1 = ((DerivedCellCalculatorRef) i.getLeft()).getId();
			String col1, table1;
			if (id1 instanceof NameSpaceIdentifier) {
				NameSpaceIdentifier ns = (NameSpaceIdentifier) id1;
				col1 = ns.getVarName();
				table1 = ns.getNamespace();
			} else {
				col1 = (String) id1;
				table1 = columnsToTables.get(col1);
			}
			if (table1 == null)
				throw new RuntimeException("bad column: " + id1);

			Object id2 = ((DerivedCellCalculatorRef) i.getRight()).getId();
			String col2, table2;
			if (id2 instanceof NameSpaceIdentifier) {
				NameSpaceIdentifier ns = (NameSpaceIdentifier) id2;
				col2 = ns.getVarName();
				table2 = ns.getNamespace();
			} else {
				col2 = (String) id2;
				table2 = columnsToTables.get(col2);
			}
			if (table2 == null)
				throw new RuntimeException("bad column: " + id2);
			TableNode t1 = tableNodes.get(table1);
			if (t1 == null)
				tableNodes.put(table1, t1 = new TableNode(asTables.get(table1), table1));
			TableNode t2 = tableNodes.get(table2);
			if (t2 == null)
				tableNodes.put(table2, t2 = new TableNode(asTables.get(table2), table2));
			OH.assertNeIdentity(t1, t2);

			TempIndex index = new TempIndex(t1, t2, i);
			TempIndex existing = indexes.get(index.getKey());
			if (existing != null)
				index = existing;
			else
				indexes.put(index.getKey(), index);
			if (index.addColumns(t1, col1, t2, col2, i)) {
				t1.addIndex(index);
				t2.addIndex(index);
			}
		}
		TableNode smallestTable = null;
		if (forceOrdering && tableNodes.size() > 0) {
			smallestTable = tableNodes.get(CH.first(asTables.keySet()));
		} else {
			for (TableNode i : tableNodes.values())
				if (smallestTable == null || i.size > smallestTable.size)
					smallestTable = i;
		}

		if (smallestTable != null)
			walk(smallestTable, tableNodes, indexesSink, new HashSet<String>());
		for (TempIndex i : indexes.values())
			if (!i.used) {
				for (DerivedCellCalculator dcc : i.getOrigCalc())
					where = where == null ? dcc : DerivedCellCalculatorMath.valueOf(dcc.getPosition(), OperationNode.OP_AMP_AMP, where, dcc);
			}
		return where;
	}

	private static Map<String, String> getUniqueColumnNames(Map<String, Table> asTables) {
		Map<String, String> columnsToTables = new LinkedHashMap<String, String>();
		for (Entry<String, Table> i : asTables.entrySet())
			for (Object col : i.getValue().getColumnIds())
				columnsToTables.put((String) col, columnsToTables.containsKey((String) col) ? null : i.getKey());
		return columnsToTables;
	}

	private static void walk(TableNode smallestTable, Map<String, TableNode> unvisitedTables, IndexedList<String, List<IndexDef>> indexesSink, Set<String> cantBeTargetTables) {
		if (unvisitedTables.remove(smallestTable.name) == null)
			return;
		smallestTable.sortIndexesByOtherSize();

		List<IndexDef> indexDefs = new ArrayList<IndexDef>(smallestTable.indexes.size());
		for (TempIndex i : smallestTable.indexes) {
			String otherName = i.getOtherTable(smallestTable).name;
			if (unvisitedTables.containsKey(otherName) && !cantBeTargetTables.contains(otherName)) {
				indexDefs.add(i.toIndexDef(smallestTable));
				cantBeTargetTables.add(otherName);
			}
		}
		if (!indexDefs.isEmpty()) {
			indexesSink.add(smallestTable.name, indexDefs);
			cantBeTargetTables.add(smallestTable.name);
		}
		for (TempIndex i : smallestTable.indexes)
			walk(i.getOtherTable(smallestTable), unvisitedTables, indexesSink, cantBeTargetTables);

	}

	private static class TempIndex {
		private static final DerivedCellCalculator[] EMPTY = new DerivedCellCalculator[0];
		private TableNode table1, table2;
		final private Tuple2<String, String> key;
		private String[] columns1 = OH.EMPTY_STRING_ARRAY, columns2 = OH.EMPTY_STRING_ARRAY;
		private DerivedCellCalculator[] origCalc = EMPTY;
		private boolean used;

		public TempIndex(TableNode targetTable, TableNode sourceTable, DerivedCellCalculator origCalc) {
			if (OH.gt(targetTable.name, sourceTable.name)) {
				this.table1 = targetTable;
				this.table2 = sourceTable;
			} else {
				this.table1 = sourceTable;
				this.table2 = targetTable;
			}
			this.key = new Tuple2<String, String>(table1.name, table2.name);
		}
		public DerivedCellCalculator[] getOrigCalc() {
			return origCalc;
		}
		public IndexDef toIndexDef(TableNode sourceTable) {
			if (used)
				throw new IllegalStateException();
			this.used = true;
			return sourceTable == table1 ? new IndexDef(table1.name, columns1, table2.name, columns2) : new IndexDef(table2.name, columns2, table1.name, columns1);
		}
		public boolean addColumns(TableNode t1, String c1, TableNode t2, String c2, DerivedCellCalculator dcc) {
			String tc, sc;
			if (t1 == table1 && t2 == table2) {
				tc = c1;
				sc = c2;
			} else if (t2 == table1 && t1 == table2) {
				tc = c2;
				sc = c1;
			} else
				throw new IllegalStateException();
			for (int i = 0; i < columns1.length; i++)
				if (OH.eq(columns1[i], tc) && OH.eq(columns2[i], sc))
					return false;
			columns1 = AH.append(columns1, tc);
			columns2 = AH.append(columns2, sc);
			origCalc = AH.append(origCalc, dcc);
			return columns1.length == 1;
		}

		public Tuple2<String, String> getKey() {
			return this.key;
		}
		@Override
		public String toString() {
			return table2.name + '.' + SH.join(',', columns2) + " <==> " + table1.name + '.' + SH.join(',', columns1);
		}

		public TableNode getOtherTable(TableNode t) {
			if (t == table1)
				return table2;
			else if (t == table2)
				return table1;
			else
				throw new IllegalStateException();
		}

	}

	public static class IndexDef {
		final String sourceTable;
		final String sourceColumns[];
		final String targetTable;
		final String targetColumns[];

		public IndexDef(String sourceTable, String[] sourceColumns, String targetTable, String[] targetColumns) {
			this.sourceTable = sourceTable;
			this.sourceColumns = sourceColumns;
			this.targetTable = targetTable;
			this.targetColumns = targetColumns;
		}
		public String getSourceTable() {
			return sourceTable;
		}
		public String[] getSourceColumns() {
			return sourceColumns;
		}
		public String getTargetTable() {
			return targetTable;
		}
		public String[] getTargetColumns() {
			return targetColumns;
		}
		@Override
		public String toString() {
			return sourceTable + '.' + SH.join(',', sourceColumns) + " <==> " + targetTable + '.' + SH.join(',', targetColumns);
		}

	}

	private static class TableNode implements Comparator<TempIndex> {

		public List<TempIndex> indexes = new ArrayList<TempIndex>();
		public String name;
		public int size;

		public TableNode(Table t, String name) {
			this.name = name;
			this.size = t.getSize();
		}
		public void sortIndexesByOtherSize() {
			Collections.sort(indexes, this);
		}
		public void addIndex(TempIndex index) {
			this.indexes.add(index);
		}
		@Override
		public int compare(TempIndex o1, TempIndex o2) {
			return OH.compare(o1.getOtherTable(this).size, o2.getOtherTable(this).size);
		}
		@Override
		public String toString() {
			return name + "(" + size + ")";
		}

	}
}
