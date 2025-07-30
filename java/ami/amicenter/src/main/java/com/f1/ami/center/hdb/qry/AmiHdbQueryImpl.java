package com.f1.ami.center.hdb.qry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.ami.center.hdb.AmiHdbColumn_Partition;
import com.f1.ami.center.hdb.AmiHdbIndex;
import com.f1.ami.center.hdb.AmiHdbPartition;
import com.f1.ami.center.hdb.AmiHdbPartitionColumn;
import com.f1.ami.center.hdb.AmiHdbTable;
import com.f1.base.CalcFrame;
import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.LinkedHasherMap;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProjector;
import com.f1.utils.sql.aggs.AbstractAggCalculator;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.sql.aggs.CountAggCalculator;
import com.f1.utils.sql.aggs.CountUniqueAggCalculator;
import com.f1.utils.sql.aggs.FirstAggCalculator;
import com.f1.utils.sql.aggs.MaxAggCalculator;
import com.f1.utils.sql.aggs.MinAggCalculator;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicRowComparator;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorWithDependencies;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;

public class AmiHdbQueryImpl {

	public static class PartitionSorter2 implements Comparator<Entry<Map, List<AmiHdbPartition>>> {

		final private DerivedCellCalculator dcc;
		final private boolean asc;
		final private ReusableCalcFrameStack sf;

		public PartitionSorter2(ReusableCalcFrameStack sf, DerivedCellCalculator dcc, boolean asc) {
			this.sf = sf;
			this.dcc = dcc;
			this.asc = asc;
		}

		@Override
		public int compare(Entry<Map, List<AmiHdbPartition>> o1, Entry<Map, List<AmiHdbPartition>> o2) {
			Comparable v1 = (Comparable) this.dcc.get(this.sf.reset(DerivedHelper.toFrame(o1.getKey())));
			Comparable v2 = (Comparable) this.dcc.get(this.sf.reset(DerivedHelper.toFrame(o2.getKey())));
			return asc ? OH.compare(v1, v2) : OH.compare(v2, v1);
		}

	}

	public static class PartitionSorter implements Comparator<AmiHdbPartition> {

		private int[] idx = OH.EMPTY_INT_ARRAY;
		private boolean[] asc = OH.EMPTY_BOOLEAN_ARRAY;

		public void add(int i, boolean b) {
			this.idx = AH.append(this.idx, i);
			this.asc = AH.append(this.asc, b);
		}

		@Override
		public int compare(AmiHdbPartition o1, AmiHdbPartition o2) {
			for (int j = 0; j < idx.length; j++) {
				int i = idx[j];
				final int n = OH.compare(o1.getPartitionsKey()[i], o2.getPartitionsKey()[i]);
				if (n != 0)
					return asc[j] ? n : -n;
			}
			return 0;
		}

	}

	public static final DerivedCellCalculator[] CONST_GROUPBYS = new DerivedCellCalculator[] { new DerivedCellCalculatorConst(0, Boolean.TRUE) };

	private static final Comparator<AmiHdbQueryPart> SCORE_SORTER = new Comparator<AmiHdbQueryPart>() {

		@Override
		public int compare(AmiHdbQueryPart o1, AmiHdbQueryPart o2) {
			return OH.compare(o2.getScore(), o1.getScore());
		}
	};

	final private BasicMultiMap.List<String, AmiHdbQueryPart> partsByColumn = new BasicMultiMap.List<String, AmiHdbQueryPart>();

	private AmiHdbTable table;
	//	private Set<Object> dependencies = new HashSet<Object>();

	private DerivedCellCalculator additionalClause;

	private long limitOffset;
	private int limit;

	private boolean[] orderByAsc;

	private DerivedCellCalculator[] orderByCalcs = DerivedHelper.EMPTY_ARRAY;//never null

	private DerivedCellCalculator[] selectCalcs;

	private String[] selectNames;

	private DerivedCellCalculator[] groupBys = DerivedHelper.EMPTY_ARRAY;//never null

	private AggCalculator[] aggregates = new AggCalculator[0];

	private DerivedCellCalculator having;

	private String rowNumColumn;

	private String partitionColumn;

	public AmiHdbQueryImpl(AmiHdbTable table) {
		this.table = table;
	}

	public void addPart(AmiHdbQueryPart part) {
		this.partsByColumn.putMulti(part.getColumn().getName(), part);
	}
	public void clearParts() {
		this.partsByColumn.clear();
	}
	public void setSelects(DerivedCellCalculator[] calc, String[] columnName) {
		this.selectCalcs = calc;
		this.selectNames = columnName;
	}

	public ColumnarTable query(CalcFrameStack sf) throws IOException {
		SqlPlanListener planListener = sf.getSqlPlanListener();
		if ((additionalClauseIsFalse() || limit == 0 || limitOffset >= this.table.getRowsCountThreadSafe()) || (this.limitOffset > 0 && isImplicitGroupBy()))
			return emptyTable(sf);
		AmiHdbQueryPart partForPartion = null;
		Map<String, AmiHdbQueryPart> parts = reduce(planListener);
		if (parts == null)//The where clause evaluated to false so just return an empty table
			return emptyTable(sf);

		{
			AmiHdbQueryPart best = null;
			for (AmiHdbColumn_Partition pcol : table.getPartitionColumns()) {
				AmiHdbQueryPart i = parts.get(pcol.getName());
				if (i != null) {
					if (best == null || best.getScore() < i.getScore())
						best = i;
				}
			}
			partForPartion = best;
		}

		List<AmiHdbPartition> partitions = new ArrayList<AmiHdbPartition>();
		if (partForPartion == null) {
			CH.addAll(partitions, table.getAllPartitions());
			if (planListener != null)
				planListener.onStep("PARTION_BY", "FORWARD SCAN ON " + partitions.size() + " PARTITION(S)");
		} else {
			boolean asc = true;
			String column = partForPartion.getColumn().getName();
			if (partForPartion instanceof AmiHdbQuery_Between) {
				AmiHdbQuery_Between between = (AmiHdbQuery_Between) partForPartion;
				Iterator<Entry<Comparable, AmiHdbPartition[]>> i = table.getPartitionsBetween(column, asc, between.getMin(), between.getMinInc(), between.getMax(),
						between.getMaxInc());
				while (i.hasNext())
					CH.addAll(partitions, i.next().getValue());
			} else if (partForPartion instanceof AmiHdbQuery_In) {
				AmiHdbQuery_In in = (AmiHdbQuery_In) partForPartion;
				for (Comparable value : in.getValues())
					CH.addAll(partitions, table.getPartitions(column, value));
			} else if (partForPartion instanceof AmiHdbQuery_Compare) {
				AmiHdbQuery_Compare comp = (AmiHdbQuery_Compare) partForPartion;
				if (comp.getType() == AmiHdbQuery_Compare.EQ) {
					CH.addAll(partitions, table.getPartitions(column, comp.getValue()));
				} else if (comp.getType() == AmiHdbQuery_Compare.NE) {
					Iterator<Entry<Comparable, AmiHdbPartition[]>> i = table.getPartitionsBetween(column, asc, null, true, null, true);
					while (i.hasNext()) {
						Entry<Comparable, AmiHdbPartition[]> val = i.next();
						if (OH.ne(comp.getValue(), val.getKey()))
							CH.addAll(partitions, val.getValue());
					}
				} else {
					Iterator<Entry<Comparable, AmiHdbPartition[]>> i;
					switch (comp.getType()) {
						case AmiHdbQuery_Compare.GE:
							i = table.getPartitionsBetween(column, asc, comp.getValue(), true, null, true);
							break;
						case AmiHdbQuery_Compare.GT:
							i = table.getPartitionsBetween(column, asc, comp.getValue(), false, null, true);
							break;
						case AmiHdbQuery_Compare.LE:
							i = table.getPartitionsBetween(column, asc, null, true, comp.getValue(), true);
							break;
						case AmiHdbQuery_Compare.LT:
							i = table.getPartitionsBetween(column, asc, null, true, comp.getValue(), false);
							break;
						default: {
							List<Entry<Comparable, AmiHdbPartition[]>> i2 = new ArrayList<Map.Entry<Comparable, AmiHdbPartition[]>>();
							for (Iterator<Entry<Comparable, AmiHdbPartition[]>> it = table.getPartitions(column); it.hasNext();) {
								Entry<Comparable, AmiHdbPartition[]> val = it.next();
								if (comp.matches(val.getKey()))
									i2.add(val);
							}
							i = i2.iterator();
							break;
						}
					}
					while (i.hasNext())
						CH.addAll(partitions, i.next().getValue());
				}
			} else {
				for (Iterator<Entry<Comparable, AmiHdbPartition[]>> it = table.getPartitions(column); it.hasNext();) {
					Entry<Comparable, AmiHdbPartition[]> val = it.next();
					if (partForPartion.matches(val.getKey()))
						CH.addAll(partitions, val.getValue());
				}
			}
			if (planListener != null)
				planListener.onStep("PARTITION_BY", "CLAUSE (" + partForPartion + ") REDUCED TO " + partitions.size() + " PARTITION(S)");
			if (partitions.size() == 0)
				return emptyTable(sf);
			if (partForPartion != null)
				parts.remove(partForPartion.getColumn().getId());
			if (table.getPartitionColumns().size() > 1) {
				for (AmiHdbColumn_Partition pcol : table.getPartitionColumns()) {
					AmiHdbQueryPart i = parts.get(pcol.getName());
					if (i == null || i == partForPartion)
						continue;
					List<AmiHdbPartition> old = partitions;
					partitions = new ArrayList<AmiHdbPartition>();
					for (AmiHdbPartition partition : old) {
						Comparable value = partition.getPartitionsKey()[pcol.getPartionIndex()];
						if (i.matches(value))
							partitions.add(partition);
					}
					if (planListener != null)
						planListener.onStep("PARTITION_BY", "CLAUSE (" + i + ") FURTHER REDUCED TO " + partitions.size() + " PARTITION(S)");
					parts.remove(pcol.getName());
				}
			}
		}
		AmiHdbQueryPart partForIndex = null;
		AmiHdbIndex indexForIndex = null;
		{
			AmiHdbIndex bestIndex = null;
			AmiHdbQueryPart best = null;
			for (AmiHdbIndex index : table.getIndexes().values()) {
				AmiHdbQueryPart i = parts.get(index.getColumn().getName());
				if (i != null) {
					if (best == null || best.getScore() < i.getScore() || (best.getScore() == i.getScore() && bestIndex.getTotalDiskSize() > index.getTotalDiskSize())) {
						bestIndex = index;
						best = i;
					}
				}
			}
			indexForIndex = bestIndex;
			partForIndex = best;
			if (partForIndex != null)
				parts.remove(partForIndex.getColumn().getId());
		}
		Mutable.Long tLimitOffset = new Mutable.Long(limitOffset);
		Mutable.Int tLimit = new Mutable.Int(limit);
		reduceOrderBys(partitions, partForIndex, planListener, tLimitOffset, tLimit, parts, sf);
		prepareOrderBys();

		ColumnarTable r = new ColumnarTable();
		for (int i = 0; i < selectNames.length; i++)
			r.addColumn(selectCalcs[i].getReturnType(), selectNames[i]);
		if (rowNumColumn != null)
			r.addColumn(Long.class, rowNumColumn);
		if (partitionColumn != null)
			r.addColumn(Integer.class, partitionColumn);
		if (!queryByPartitionAndCombine(partitions, partForPartion, partForIndex, indexForIndex, parts, tLimitOffset, tLimit, r, sf)) {
			List<AmiHdbColumn> dependencyColumns = getDependencies(groupBys, having);
			if (partForIndex != null)
				parts.remove(partForIndex.getColumn().getName());
			if (partForPartion != null)
				parts.remove(partForPartion.getColumn().getName());
			List<AmiHdbQueryPart> l = CH.l(parts.values());
			Collections.sort(l, SCORE_SORTER);
			final AmiHdbResultSet rs = createResultSet(planListener, partForIndex, indexForIndex);
			rs.init(partitions);

			AmiHdbResultSetWalker.queryFull(this, this.additionalClause, rs, l, groupBys, having, tLimitOffset, tLimit.value, true, dependencyColumns, r, sf);
		}
		if (afterOrderByColumns.length > 0) {
			System.out.println(r);
			Collections.sort(r.getRows(), new BasicRowComparator(afterOrderByColumnsPos, afterOrderByColumnsAscending));
			System.out.println(r);
			if (planListener != null)
				planListener.onStep("ORDER_BY", "ORDERED " + r.getSize() + " ROW(S)");
			for (int i = afterOrderByColumnsExtraPos.length - 1; i >= 0; i--)
				r.removeColumn(afterOrderByColumnsExtraPos[i]);
		}
		if (SqlProjector.trimTable(r, (int) tLimitOffset.value, tLimit.value))
			if (planListener != null)
				planListener.onStep("BATCHES_LIMIT", "CUT TABLE DOWN TO " + r.getSize() + " ROW(S)");
		if (r.getSize() == 0 && aggregates.length > 0 && isImplicitGroupBy()) {
			ColumnarRow row = r.newEmptyRow();
			ChildCalcFrameStack ccfs = new ChildCalcFrameStack(null, true, sf, new BasicCalcFrame(r.getColumnTypesMapping()));
			for (int i = 0; i < this.selectCalcs.length; i++)
				row.putAt(i, this.selectCalcs[i].get(ccfs));//r.getColumnTypesMapping()));
			if (having == null) {
				r.getRows().add(row);
			} else {
				if (Boolean.TRUE.equals(having.get(new ReusableCalcFrameStack(sf, row))))
					r.getRows().add(row);
			}
		}
		return r;
	}

	private String extractGroupByCol(DerivedCellCalculator i) {
		return (i instanceof DerivedCellCalculatorRef) ? (String) ((DerivedCellCalculatorRef) i).getId() : null;
	}

	private void reduceOrderBys(List<AmiHdbPartition> partitions, AmiHdbQueryPart partForIndex, SqlPlanListener planListener, com.f1.utils.mutable.Mutable.Long limitOffset,
			com.f1.utils.mutable.Mutable.Int limit, Map<String, AmiHdbQueryPart> parts, CalcFrameStack sf) {
		if (AH.isntEmpty(groupBys)) {
			if (having == null && CH.isEmpty(parts) && (limit.value != -1 || limitOffset.value > 0)) {
				for (int n = 0; n < groupBys.length; n++) {
					String col = extractGroupByCol(groupBys[n]);
					AmiHdbColumn col2 = table.getColumnNoThrow(col);
					if (!(col2 instanceof AmiHdbColumn_Partition))
						return;
				}
				//At this point we have a LIMIT and GROUP BY is completely covered by partition columns 
				if (orderByCalcs.length > 0) { //If there are ORDER BYs confirm that they are only referencing GROUP BYs
					Set<String> groupByDeps2 = new HashSet<String>();
					Set<Object> orderbyDeps = new HashSet<Object>();
					for (int n = 0; n < groupBys.length; n++)
						groupByDeps2.add(extractGroupByCol(groupBys[n]));
					for (int i = orderByCalcs.length - 1; i >= 0; i--)
						DerivedHelper.getDependencyIds(orderByCalcs[i], orderbyDeps);
					if (!groupByDeps2.containsAll(orderbyDeps))
						return;
				}
				List<Entry<Map, List<AmiHdbPartition>>> partitionsByGroupBy = organizePartitionsByGroup(partitions, groupBys);
				for (int i = orderByCalcs.length - 1; i >= 0; i--) {
					if (planListener != null)
						planListener.onStep("PREAPPLY_LIMIT_TO_PARTITION_CANDIDATES", "ORDERING PARTITIONS BY " + orderByCalcs[i]);
					Collections.sort(partitionsByGroupBy, new PartitionSorter2(new ReusableCalcFrameStack(sf), orderByCalcs[i], orderByAsc[i]));
				}
				int psize = partitions.size();

				//Lets apply the limit to the partitions now and clear out the limit.
				trimList(partitionsByGroupBy, limitOffset, limit);
				partitions.clear();
				for (Entry<Map, List<AmiHdbPartition>> i : partitionsByGroupBy)
					for (AmiHdbPartition j : i.getValue())
						partitions.add(j);
				if (planListener != null)
					planListener.onStep("PREAPPLY_LIMIT_TO_PARTITION_CANDIDATES", "REDUCED " + psize + " PARTITIONS TO " + partitions.size());
				//We have a GROUP BY X,Y,Z ORDER BY X,Y,Z LIMIT ...   (such that X,Y,Z are all partition columns).
			}
			return;
		}

		//There is no GROUP BY, but maybe we are doing an ORDER BY on a partition column.
		PartitionSorter partitionSorter = null;
		for (int i = 0; i < this.orderByCalcs.length; i++) {
			DerivedCellCalculator calc = this.orderByCalcs[i];
			if (calc instanceof DerivedCellCalculatorRef) {
				String id = (String) ((DerivedCellCalculatorRef) calc).getId();
				AmiHdbColumn col = this.table.getColumnNoThrow(id);
				if (col instanceof AmiHdbColumn_Partition) {
					if (partitionSorter == null)
						partitionSorter = new PartitionSorter();
					if (planListener != null)
						planListener.onStep("PRESORT_BY_PARTITION", "PRESORTING USING PARTITION " + col.getName());
					partitionSorter.add(((AmiHdbColumn_Partition) col).getPartionIndex(), this.orderByAsc[i]);
				}
			}
		}
		if (partitionSorter != null) {
			Collections.sort(partitions, partitionSorter);
			if (partitionSorter.idx.length == this.orderByCalcs.length) {//order bys are completely covered by partitions.
				this.orderByCalcs = DerivedHelper.EMPTY_ARRAY;
				this.orderByAsc = OH.EMPTY_BOOLEAN_ARRAY;
			}
		}

	}

	public static <T> void trimList(List<T> rows, Mutable.Long limitOffset, Mutable.Int limit) {
		if (limitOffset.value >= rows.size() || limit.value == 0) {
			limitOffset.value = 0;
			limit.value = 0;
			rows.clear();
			return;
		}
		if (limitOffset.value == 0 && limit.value != -1) {
			for (int i = rows.size() - 1; i >= limit.value; i--)
				rows.remove(i);
			limit.value = -1;
			return;
		}
		if (limit.value == -1) {
			int keep = (int) (rows.size() - limitOffset.value);
			for (int i = 0; i < keep; i++)
				rows.set(i, rows.get((int) (i + limitOffset.value)));
			for (int i = rows.size() - 1; i >= keep; i--)
				rows.remove(i);
			return;
		}
		if (limitOffset.value + limit.value > rows.size())
			limit.value = (int) (rows.size() - limitOffset.value);
		for (int i = 0; i < limit.value; i++)
			rows.set(i, rows.get((int) (i + limitOffset.value)));
		for (int i = rows.size() - 1; i >= limit.value; i--)
			rows.remove(i);
		limitOffset.value = 0;
		limit.value = -1;
	}

	private List<Entry<Map, List<AmiHdbPartition>>> organizePartitionsByGroup(List<AmiHdbPartition> partitions, DerivedCellCalculator[] groupByes) {
		List<AmiHdbColumn_Partition> cols = new ArrayList<AmiHdbColumn_Partition>();
		for (int n = 0; n < groupBys.length; n++) {
			String col = extractGroupByCol(groupBys[n]);
			AmiHdbColumn col2 = table.getColumnNoThrow(col);
			cols.add((AmiHdbColumn_Partition) col2);
		}
		LinkedHashMap<Map, List<AmiHdbPartition>> r = new LinkedHashMap<Map, List<AmiHdbPartition>>();
		for (AmiHdbPartition i : partitions) {
			Map m = new HashMap<String, Comparable>();
			for (AmiHdbColumn_Partition col : cols) {
				Comparable<?> value = i.getPartitionsKey()[col.getPartionIndex()];
				m.put(col.getName(), value);
			}
			List<AmiHdbPartition> l = r.get(m);
			if (l == null)
				r.put(m, l = new ArrayList<AmiHdbPartition>());
			l.add(i);
		}
		return CH.l(r.entrySet());
	}

	private ColumnarTable emptyTable(CalcFrameStack sf) throws IOException {
		DerivedCellCalculator selectCalcs[] = getSelectCalcs();
		String selectNames[] = getSelectNames();
		ColumnarTable r = new ColumnarTable();
		for (int i = 0; i < selectNames.length; i++)
			r.addColumn(selectCalcs[i].getReturnType(), selectNames[i]);
		if (rowNumColumn != null)
			r.addColumn(Long.class, rowNumColumn);
		if (partitionColumn != null)
			r.addColumn(Integer.class, partitionColumn);
		if (aggregates.length > 0 && limitOffset == 0 && limit != 0 && isImplicitGroupBy()) {
			ColumnarRow row = r.newEmptyRow();
			for (int i = 0; i < this.selectCalcs.length; i++)
				row.putAt(i, this.selectCalcs[i].get(sf));
			if (having == null) {
				r.getRows().add(row);
			} else {
				if (Boolean.TRUE.equals(having.get(new ReusableCalcFrameStack(sf, row))))
					r.getRows().add(row);
			}
		}
		return r;
	}

	private boolean isImplicitGroupBy() {
		return AH.length(groupBys) == 1 && OH.eq(groupBys[0], CONST_GROUPBYS[0]);
	}
	private boolean canShortCircuitAggs(DerivedCellCalculator[] tGroupBys) {
		if (additionalClause != null || tGroupBys.length != 1 || !tGroupBys[0].isConst())
			return false;

		for (int n = 0; n < this.aggregates.length; n++) {
			AggCalculator i = this.aggregates[n];
			if (i.getInnerCalcsCount() == 1) {
				if (i instanceof CountAggCalculator) {
					if (i.getInnerCalcAt(0).isConst())
						continue;
				} else {
					DerivedCellCalculator calc = i.getInnerCalcAt(0);
					if (calc instanceof DerivedCellCalculatorRef) {
						DerivedCellCalculatorRef ref = (DerivedCellCalculatorRef) calc;
						String col = (String) ((DerivedCellCalculatorRef) ref).getId();
						AmiHdbColumn hcol = table.getColumnNoThrow(col);
						if (hcol != null) {
							if (i instanceof FirstAggCalculator) {
								continue;
							} else if (i instanceof MinAggCalculator || i instanceof MaxAggCalculator || i instanceof CountUniqueAggCalculator) {
								if (hcol instanceof AmiHdbColumn_Partition || table.getIndexForColumn(hcol.getName()) != null)
									continue;
							}
						}
					}
				}
			}
			return false;
		}
		return true;
	}
	private void shortCircuitAggs(List<AmiHdbPartition> partitions, SqlPlanListener planListener, TimeoutController timeoutController, DerivedCellCalculator[] tGroupBys,
			ColumnarTable r, CalcFrameStack sf) throws IOException {

		Comparable[] sink = new Comparable[1];
		for (int n = 0; n < this.aggregates.length; n++) {
			if (timeoutController != null)
				timeoutController.throwIfTimedout();
			AggCalculator i = this.aggregates[n];
			if (i instanceof CountAggCalculator) {
				if (i.getInnerCalcAt(0).get(null) == null) {
					if (planListener != null)
						planListener.onStep("FAST_AGG_VIA_COUNT", "IS NULL: " + i);
					i.setValue(0L);
				} else if (partitions.size() == table.getAllPartitions().length) {
					if (planListener != null)
						planListener.onStep("FAST_AGG_VIA_COUNT", "USING TABLE COUNT: " + i);
					i.setValue(table.getRowsCountThreadSafe());
				} else {
					if (planListener != null)
						planListener.onStep("FAST_AGG_VIA_COUNT", "SUM ROW COUNT ACROSS " + partitions.size() + " PARTITION(S): " + i);
					long tot = 0;
					for (int j = 0; j < partitions.size(); j++)
						tot += partitions.get(j).getRowCount();
					i.setValue(tot);
				}
			} else {
				DerivedCellCalculatorRef ref = (DerivedCellCalculatorRef) i.getInnerCalcAt(0);
				String col = (String) ((DerivedCellCalculatorRef) ref).getId();
				AmiHdbColumn hcol = table.getColumnNoThrow(col);
				if (i instanceof FirstAggCalculator) {
					if (planListener != null)
						planListener.onStep("FAST_AGG_VIA_COLUMN_HEAD", "USING FIRST OF " + partitions.size() + " PARTITION(S): " + i);
					if (hcol instanceof AmiHdbColumn_Partition) {
						i.setValue(partitions.get(0).getPartitionsKey()[((AmiHdbColumn_Partition) hcol).getPartionIndex()]);
					} else {
						AmiHdbPartitionColumn column = partitions.get(0).getColumn(col);
						column.readValues(0, 1, 0, sink);
						i.setValue(sink[0]);
					}
				} else if (i instanceof MinAggCalculator || i instanceof MaxAggCalculator || i instanceof CountUniqueAggCalculator) {
					if (hcol instanceof AmiHdbColumn_Partition) {
						if (planListener != null)
							planListener.onStep("FAST_AGG_VIA_PARTITION", "COMPARING ACROSS " + partitions.size() + " PARTITION(S): " + i);
						AmiHdbColumn_Partition pcol = (AmiHdbColumn_Partition) hcol;
						if (i instanceof MinAggCalculator) {
							int pos = CH.minIndex(partitions, AmiHdbPartition.getKeyComparator(pcol.getPartionIndex()));
							i.setValue(partitions.get(pos).getPartitionsKey()[pcol.getPartionIndex()]);
						} else if (i instanceof MaxAggCalculator) {
							int pos = CH.maxIndex(partitions, AmiHdbPartition.getKeyComparator(pcol.getPartionIndex()));
							i.setValue(partitions.get(pos).getPartitionsKey()[pcol.getPartionIndex()]);
						} else if (i instanceof CountUniqueAggCalculator) {
							Set<Comparable> t = new HashSet<Comparable>();
							for (AmiHdbPartition p : partitions)
								t.add(p.getPartitionsKey()[pcol.getPartionIndex()]);
							i.setValue((long) t.size());
						}
					} else {
						if (planListener != null)
							planListener.onStep("FAST_AGG_VIA_INDEDX", "COMPARING ACROSS " + partitions.size() + " PARTITION INDEX(S): " + i);
						AmiHdbIndex indexForColumn = table.getIndexForColumn(hcol.getName());
						if (indexForColumn != null) {
							Comparable val = null;
							boolean first = true;
							if (i instanceof MinAggCalculator) {
								for (AmiHdbPartition p : partitions) {
									Comparable t = p.getIndexByName(indexForColumn.getName()).getMinKey();
									if (first) {
										val = t;
										first = false;
									} else if (OH.compare(val, t) > 0)
										val = t;
								}
							} else if (i instanceof MaxAggCalculator) {
								for (AmiHdbPartition p : partitions) {
									Comparable t = p.getIndexByName(indexForColumn.getName()).getMaxKey();
									if (first) {
										val = t;
										first = false;
									} else if (OH.compare(val, t) < 0)
										val = t;
								}
							} else if (i instanceof CountUniqueAggCalculator) {
								Set<Comparable> t = new HashSet<Comparable>();
								for (AmiHdbPartition p : partitions)
									for (Object c : p.getIndexByName(indexForColumn.getName()).getKeys())
										t.add((Comparable) c);
								val = (long) t.size();
							}
							i.setValue(val);
						}
					}
				}
			}
		}
		Set<String> needsFirstRow = new HashSet<String>();//Have they directly referenced column names (not in agg function) in which case we return the first value
		for (DerivedCellCalculator i : selectCalcs)
			getNonAggDependencyIds(i, (Set) needsFirstRow);

		if (!needsFirstRow.isEmpty()) {
			CalcFrame firstRow = new BasicCalcFrame(table.getColumnTypes());
			AmiHdbPartition p = partitions.get(0);
			for (String varname : needsFirstRow) {
				AmiHdbPartitionColumn i = p.getColumn(varname);
				if (i != null) {
					i.readValues(0, 1, 0, sink);
					firstRow.putValue(varname, sink[0]);
				} else {
					AmiHdbColumn col = table.getColumnNoThrow(varname);
					if (col instanceof AmiHdbColumn_Partition)
						firstRow.putValue(varname, p.getPartitionsKey()[((AmiHdbColumn_Partition) col).getPartionIndex()]);
				}
			}
			sf = new ReusableCalcFrameStack(sf, firstRow);
		}
		Row row = r.newEmptyRow();
		for (int j = 0; j < selectCalcs.length; j++)
			row.putAt(j, selectCalcs[j].get(sf));
		if (having == null) {
			r.getRows().add(row);
		} else {
			//			RowGetter outRg = new RowGetter(r, globalVars);
			if (Boolean.TRUE.equals(having.get(new ReusableCalcFrameStack(sf, row))))
				r.getRows().add(row);
		}
	}

	public static Set<Object> getNonAggDependencyIds(DerivedCellCalculator calc, Set<Object> sink) {
		if (calc != null && !(calc instanceof AggCalculator)) {
			if (calc instanceof DerivedCellCalculatorWithDependencies)
				((DerivedCellCalculatorWithDependencies) calc).getDependencyIds(sink);
			for (int i = 0, l = calc.getInnerCalcsCount(); i < l; i++)
				getNonAggDependencyIds(calc.getInnerCalcAt(i), sink);
		}
		return sink;
	}

	private boolean additionalClauseIsFalse() {
		return (this.additionalClause != null && this.additionalClause.isConst() && Boolean.FALSE.equals(this.additionalClause.get(null)));
	}

	private boolean queryByPartitionAndCombine(List<AmiHdbPartition> partitions, AmiHdbQueryPart partForPartion, AmiHdbQueryPart partForIndex, AmiHdbIndex indexForIndex,
			Map<String, AmiHdbQueryPart> parts, com.f1.utils.mutable.Mutable.Long tLimitOffset, Mutable.Int tLimit, ColumnarTable r, CalcFrameStack sf) throws IOException {
		SqlPlanListener planListener = sf.getSqlPlanListener();
		TimeoutController timeoutController = sf.getTimeoutController();
		if (groupBys.length == 0 || beforeOrderByColumns.length > 0)
			return false;
		DerivedCellCalculator[] tGroupBys = groupBys;
		String splitByPartition = null;
		List<AmiHdbColumn> dependencyColumns = getDependencies(tGroupBys, having);
		final boolean canInlineLimit = orderByCalcs.length == 0 && (tLimit.value != -1 || tLimitOffset.value > 0);
		for (int n = 0; n < tGroupBys.length; n++) {
			String col = extractGroupByCol(tGroupBys[n]);
			if (col != null && table.getColumnNoThrow(col) instanceof AmiHdbColumn_Partition) {
				if (planListener != null)
					planListener.onStep("SPLIT_ON_GROUPBY", "SPLIT BY PARTITION " + col);
				splitByPartition = col;
				tGroupBys = AH.remove(tGroupBys, n);
				AmiHdbColumn_Partition hcol = (AmiHdbColumn_Partition) table.getColumn(col);
				dependencyColumns.remove(hcol);
				break;
			}
		}
		String splitByIndex = null;
		if (indexForIndex != null) {
			String idxColName = indexForIndex.getColumn().getName();
			for (int n = 0; n < tGroupBys.length; n++) {
				String col = extractGroupByCol(tGroupBys[n]);
				if (OH.eq(idxColName, col)) {
					if (planListener != null)
						planListener.onStep("SPLIT_ON_GROUPBY", "SPLIT BY INDEX " + col + " PARTICIPATING IN WHERE ");
					splitByIndex = col;
					tGroupBys = AH.remove(tGroupBys, n);
					break;
				}
			}
		} else {//There are no indexes participating in the where clause, but there are still 3 reasons to split up by an index referend to in hte group by:
			// (1) Can limit in line, meaning its possible to stop before going through the whole limit
			// (2) Have partitions that are so large we don't want to bring all data for a given partition into memory
			// (3) Can do a queryJustCount
			boolean largetPartition = false;
			if (!canInlineLimit)
				for (AmiHdbPartition i : partitions)
					if (i.getRowCount() > 10000000) {
						largetPartition = true;
						break;
					}
			for (int n = 0; n < tGroupBys.length; n++) {
				String col = extractGroupByCol(tGroupBys[n]);
				if (col != null) {
					AmiHdbIndex idx = table.getIndexForColumn(col);
					if (idx != null) {//Tough call, sometimes its not faster to split on index if the index isn't used in the where clause
						if (largetPartition) {
							if (planListener != null)
								planListener.onStep("SPLIT_ON_GROUPBY", "SPLIT BY INDEX " + col + " DUE TO LARGE_PARTITION");
						} else if (canInlineLimit) {
							if (planListener != null)
								planListener.onStep("SPLIT_ON_GROUPBY", "SPLIT BY INDEX " + col + " DUE TO LIKELY_LIMIT");
						} else if (additionalClause == null && (dependencyColumns.isEmpty() || (dependencyColumns.size() == 1 && dependencyColumns.get(0) == idx.getColumn()))
								&& parts.isEmpty() && tGroupBys.length == 1) {
							if (planListener != null)
								planListener.onStep("SPLIT_ON_GROUPBY", "SPLIT BY INDEX DUE TO NO_READ_REQUIRED " + col);
						} else
							continue;
						splitByIndex = col;
						tGroupBys = AH.remove(tGroupBys, n);
						indexForIndex = idx;
						partForIndex = new AmiHdbQuery_All(table.getColumn(col));
					}
				}
			}
		}
		if (tGroupBys.length == 0)
			tGroupBys = CONST_GROUPBYS;
		if (splitByPartition == null && splitByIndex == null && (tGroupBys.length != 1 || !tGroupBys[0].isConst()))
			return false;

		BasicMultiMap.List<Comparable<?>, AmiHdbPartition> partitionsByGroupBy = new BasicMultiMap.List<Comparable<?>, AmiHdbPartition>();
		partitionsByGroupBy.setInnerMap(new LinkedHashMap<Comparable<?>, List<AmiHdbPartition>>());
		if (splitByPartition != null) {
			AmiHdbColumn_Partition hcol = (AmiHdbColumn_Partition) table.getColumn(splitByPartition);
			for (AmiHdbPartition p : partitions) {
				Comparable<?> key = p.getPartitionsKey()[hcol.getPartionIndex()];
				partitionsByGroupBy.putMulti(key, p);
			}
		} else {
			partitionsByGroupBy.put(true, partitions);
		}
		if (splitByIndex == null) {
			final boolean canShortCircuitAggs = partForIndex == null && CH.isEmpty(parts) && canShortCircuitAggs(tGroupBys);
			if (canShortCircuitAggs) {
				if (splitByPartition != null) {
					SingletonCalcFrame singletopFrame = new SingletonCalcFrame(splitByPartition, table.getColumn(splitByPartition).getType());
					ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf, singletopFrame);
					for (Entry<Comparable<?>, List<AmiHdbPartition>> e : partitionsByGroupBy.entrySet()) {
						singletopFrame.setValue(e.getKey());
						shortCircuitAggs(e.getValue(), planListener, timeoutController, tGroupBys, r, rsf);
						if (canInlineLimit && inlineLimit(tLimitOffset, tLimit, r))
							break;
					}
				} else {
					for (Entry<Comparable<?>, List<AmiHdbPartition>> e : partitionsByGroupBy.entrySet()) {
						shortCircuitAggs(e.getValue(), planListener, timeoutController, tGroupBys, r, sf);
						if (canInlineLimit && inlineLimit(tLimitOffset, tLimit, r))
							break;
					}
				}
			} else {
				List<AmiHdbQueryPart> l = CH.l(parts.values());
				Collections.sort(l, SCORE_SORTER);
				boolean queryJustCount = additionalClause == null && dependencyColumns.isEmpty() && l.isEmpty() && tGroupBys.length == 1 && tGroupBys[0].isConst();
				final AmiHdbResultSet rs = createResultSet(planListener, partForIndex, indexForIndex);
				if (splitByPartition != null) {
					SingletonCalcFrame singletopFrame = new SingletonCalcFrame(splitByPartition, table.getColumn(splitByPartition).getType());
					ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf, singletopFrame);
					for (Entry<Comparable<?>, List<AmiHdbPartition>> e : partitionsByGroupBy.entrySet()) {
						singletopFrame.setValue(e.getKey());
						rs.init(e.getValue());
						if (queryJustCount)
							AmiHdbResultSetWalker.queryJustCount(this, rs, l, planListener, timeoutController, having, r, rsf);
						else
							AmiHdbResultSetWalker.queryFull(this, this.additionalClause, rs, l, tGroupBys, having, tLimitOffset, limit, false, dependencyColumns, r, rsf);
						if (canInlineLimit && inlineLimit(tLimitOffset, tLimit, r))
							break;
					}
				} else {
					ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf, EmptyCalcFrame.INSTANCE);
					for (Entry<Comparable<?>, List<AmiHdbPartition>> e : partitionsByGroupBy.entrySet()) {
						rs.init(e.getValue());
						if (queryJustCount)
							AmiHdbResultSetWalker.queryJustCount(this, rs, l, planListener, timeoutController, having, r, rsf);
						else
							AmiHdbResultSetWalker.queryFull(this, this.additionalClause, rs, l, tGroupBys, having, tLimitOffset, limit, false, dependencyColumns, r, rsf);
						if (canInlineLimit && inlineLimit(tLimitOffset, tLimit, r))
							break;
					}
				}
			}
		} else {
			List<AmiHdbQueryPart> l = CH.l(parts.values());
			Collections.sort(l, SCORE_SORTER);
			dependencyColumns.remove(partForIndex.getColumn());
			boolean queryJustCount = additionalClause == null && dependencyColumns.isEmpty() && l.isEmpty() && tGroupBys.length == 1 && tGroupBys[0].isConst();
			final AmiHdbResultSet rs = createResultSet(planListener, partForIndex, indexForIndex);
			AmiHdbResultSet_WrapIndex rs2 = new AmiHdbResultSet_WrapIndex(rs.getIndexColumn());
			MutableCalcFrame vars = new MutableCalcFrame();
			if (splitByPartition != null)
				vars.putType(splitByPartition, table.getColumn(splitByPartition).getType());
			String indexVarName = indexForIndex.getColumn().getName();
			vars.putType(indexVarName, indexForIndex.getColumn().getType());
			ReusableCalcFrameStack sf2 = new ReusableCalcFrameStack(sf, vars);

			outer: for (Entry<Comparable<?>, List<AmiHdbPartition>> e : partitionsByGroupBy.entrySet()) {
				rs.init(e.getValue());
				LinkedHasherMap<Comparable, List<Tuple2<AmiHdbPartition, int[]>>> keys2partitionsAndRows = new LinkedHasherMap<Comparable, List<Tuple2<AmiHdbPartition, int[]>>>();
				while (rs.nextOnlyCount() > 0) {
					if (timeoutController != null)
						timeoutController.throwIfTimedout();
					for (Entry<Comparable, int[]> i : rs.getKeys().entrySet()) {
						Entry<Comparable, List<Tuple2<AmiHdbPartition, int[]>>> t = keys2partitionsAndRows.getOrCreateEntry(i.getKey());
						if (t.getValue() == null)
							t.setValue(new ArrayList<Tuple2<AmiHdbPartition, int[]>>(e.getValue().size()));
						t.getValue().add(new Tuple2<AmiHdbPartition, int[]>(rs.getCurrentPartition(), i.getValue()));
					}
				}
				if (splitByPartition != null)
					vars.putValue(splitByPartition, e.getKey());
				for (Entry<Comparable, List<Tuple2<AmiHdbPartition, int[]>>> i : keys2partitionsAndRows.entrySet()) {
					vars.putValue(indexVarName, i.getKey());
					rs2.reset(i.getKey(), i.getValue());
					if (queryJustCount)
						AmiHdbResultSetWalker.queryJustCount(this, rs2, l, planListener, timeoutController, having, r, sf2);
					else
						AmiHdbResultSetWalker.queryFull(this, this.additionalClause, rs2, l, tGroupBys, having, tLimitOffset, limit, false, dependencyColumns, r, sf2);
					if (canInlineLimit && inlineLimit(tLimitOffset, tLimit, r))
						break outer;
				}
			}
		}
		return true;

	}

	private boolean inlineLimit(com.f1.utils.mutable.Mutable.Long tLimitOffset, Mutable.Int tLimit, ColumnarTable r) {
		if (tLimitOffset.value >= r.getSize()) {//offset is bigger than the table, limit does not matter
			tLimitOffset.value -= r.getSize();
			r.clear();
		} else {
			SqlProjector.trimTable(r, (int) tLimitOffset.value, tLimit.value);
			tLimitOffset.value = 0;
			if (tLimit.value != -1 && r.getSize() == tLimit.value)
				return true;
		}
		return false;
	}

	private DerivedCellCalculator[] beforeOrderByColumns = DerivedHelper.EMPTY_ARRAY;
	private DerivedCellCalculator[] afterOrderByColumns = DerivedHelper.EMPTY_ARRAY;
	private DerivedCellCalculator[] afterOrderByColumnsExtra = DerivedHelper.EMPTY_ARRAY;
	private int[] afterOrderByColumnsExtraPos = OH.EMPTY_INT_ARRAY;
	private int[] afterOrderByColumnsPos = OH.EMPTY_INT_ARRAY;
	private boolean[] beforeOrderByColumnsAscending = OH.EMPTY_BOOLEAN_ARRAY;
	private boolean[] afterOrderByColumnsAscending = OH.EMPTY_BOOLEAN_ARRAY;

	private void prepareOrderBys() {
		final Table r;
		if (orderByCalcs.length > 0) {
			for (int i = 0; i < orderByCalcs.length; i++) {
				DerivedCellCalculator ob = orderByCalcs[i];
				boolean oba = orderByAsc[i];
				int idx = AH.indexOf(ob, this.selectCalcs);
				if (idx == -1 && ob instanceof DerivedCellCalculatorRef) {
					Object name = ((DerivedCellCalculatorRef) ob).getId();
					idx = AH.indexOf(name, this.selectNames);
				}

				//if it doesn't reference an ouput column, contain an aggregate or use a group by.
				if (idx == -1 && DerivedHelper.findFirst(ob, AbstractAggCalculator.class) == null) {
					beforeOrderByColumns = AH.append(beforeOrderByColumns, ob);
					beforeOrderByColumnsAscending = AH.append(beforeOrderByColumnsAscending, oba);
				} else {
					if (idx == -1) {
						idx = this.selectCalcs.length;
						afterOrderByColumnsExtra = AH.append(afterOrderByColumnsExtra, ob);
						this.selectCalcs = AH.append(this.selectCalcs, ob);
						this.selectNames = AH.append(this.selectNames, "!!sort" + i);
						afterOrderByColumnsExtraPos = AH.append(afterOrderByColumnsExtraPos, idx);
					}
					afterOrderByColumns = AH.append(afterOrderByColumns, ob);
					afterOrderByColumnsPos = AH.append(afterOrderByColumnsPos, idx);
					afterOrderByColumnsAscending = AH.append(afterOrderByColumnsAscending, oba);
				}
			}
		}

	}

	private AmiHdbResultSet createResultSet(SqlPlanListener planListener, AmiHdbQueryPart partForIndex, AmiHdbIndex indexForIndex) {
		final AmiHdbResultSet r;
		if (partForIndex == null) {
			if (planListener != null)
				planListener.onStep("INDEX_ON", "NO INDEX, FORWARD SCAN");
			r = new AmiHdbResultSet_All();
		} else {
			if (planListener != null)
				planListener.onStep("INDEX_ON", "USING INDEX '" + indexForIndex.getName() + "' ON COLUMNS " + indexForIndex.getColumn() + " WITH CLAUSE (" + partForIndex + ")");
			boolean asc = true;//TODO: if should be optimized on the direction of between clause
			if (partForIndex instanceof AmiHdbQuery_Between) {
				AmiHdbQuery_Between between = (AmiHdbQuery_Between) partForIndex;
				r = new AmiHdbResultSet_Between(indexForIndex, asc, between.getMin(), between.getMinInc(), between.getMax(), between.getMaxInc());
			} else if (partForIndex instanceof AmiHdbQuery_Compare) {
				AmiHdbQuery_Compare comp = (AmiHdbQuery_Compare) partForIndex;
				switch (comp.getType()) {
					case AmiHdbQuery_Compare.GE:
						r = new AmiHdbResultSet_Between(indexForIndex, asc, comp.getValue(), true, null, true);
						break;
					case AmiHdbQuery_Compare.GT:
						r = new AmiHdbResultSet_Between(indexForIndex, asc, comp.getValue(), false, null, true);
						break;
					case AmiHdbQuery_Compare.LE:
						r = new AmiHdbResultSet_Between(indexForIndex, asc, null, true, comp.getValue(), true);
						break;
					case AmiHdbQuery_Compare.LT:
						r = new AmiHdbResultSet_Between(indexForIndex, asc, null, true, comp.getValue(), false);
						break;
					case AmiHdbQuery_Compare.EQ:
						r = new AmiHdbResultSet_Eq(indexForIndex, comp.getValue());
						break;
					default:
						r = new AmiHdbResultSet_Scan(indexForIndex, comp);
						break;
				}
			} else if (partForIndex instanceof AmiHdbQuery_In) {
				AmiHdbQuery_In in = (AmiHdbQuery_In) partForIndex;
				r = new AmiHdbResultSet_In(indexForIndex, in.getValues());
			} else
				r = new AmiHdbResultSet_Scan(indexForIndex, partForIndex);
		}
		return r;
	}

	private List<AmiHdbColumn> getDependencies(DerivedCellCalculator[] tGroupBys, DerivedCellCalculator tHaving) {
		Set<Object> dependencies = new HashSet<Object>();
		for (DerivedCellCalculator dcc : selectCalcs)
			DerivedHelper.getDependencyIds(dcc, dependencies);
		for (DerivedCellCalculator dcc : orderByCalcs)
			DerivedHelper.getDependencyIds(dcc, dependencies);
		for (DerivedCellCalculator dcc : tGroupBys)
			DerivedHelper.getDependencyIds(dcc, dependencies);
		if (aggregates != null)
			for (DerivedCellCalculator dcc : aggregates)
				DerivedHelper.getDependencyIds(dcc, dependencies);
		DerivedHelper.getDependencyIds(tHaving, dependencies);
		DerivedHelper.getDependencyIds(additionalClause, dependencies);
		List<AmiHdbColumn> dependencyColumns = new ArrayList<AmiHdbColumn>(dependencies.size());
		for (Object i : dependencies) {
			String col;
			if (i instanceof NameSpaceIdentifier) {
				col = ((NameSpaceIdentifier) i).getVarName();
				if (dependencies.contains(col))
					continue;
			} else
				col = (String) i;
			AmiHdbColumn column = this.table.getColumnNoThrow(col);
			if (column != null)
				dependencyColumns.add(column);
		}
		return dependencyColumns;
	}

	public Map<String, AmiHdbQueryPart> reduce(SqlPlanListener planListener) {
		Map<String, AmiHdbQueryPart> m = new HashMap<String, AmiHdbQueryPart>();
		for (List<AmiHdbQueryPart> parts : this.partsByColumn.values()) {
			if (parts.size() == 0)
				continue;
			AmiHdbQueryPart part = parts.get(0);
			for (int n = 1; n < parts.size(); n++) {
				final AmiHdbQueryPart c2 = parts.get(n);
				AmiHdbQueryPart c1 = part;
				part = reduceParts(c1, c2);
				if (planListener != null) {
					if (part == null)
						planListener.onStep("REDUCED", c1 + " AND " + c2 + " TO EMPTY RESULTSET");
					else
						planListener.onStep("REDUCED", c1 + " AND " + c2 + " TO " + part);
				}
				if (part == null)
					return null;
			}
			m.put(part.getColumn().getName(), part);
		}
		return m;
	}

	private AmiHdbQueryPart reduceParts(final AmiHdbQueryPart c1, final AmiHdbQueryPart c2) {
		if (c1 instanceof AmiHdbQuery_Compare) {
			if (c2 instanceof AmiHdbQuery_Compare)
				return reduce((AmiHdbQuery_Compare) c1, (AmiHdbQuery_Compare) c2);
			else if (c2 instanceof AmiHdbQuery_Between)
				return reduce((AmiHdbQuery_Compare) c1, (AmiHdbQuery_Between) c2);
			else if (c2 instanceof AmiHdbQuery_In)
				return reduce((AmiHdbQuery_Compare) c1, (AmiHdbQuery_In) c2);
			else
				throw new RuntimeException("Unhandled scenario: " + c1 + " and " + c2);
		} else if (c1 instanceof AmiHdbQuery_Between) {
			if (c2 instanceof AmiHdbQuery_Compare)
				return reduce((AmiHdbQuery_Compare) c2, (AmiHdbQuery_Between) c1);
			else if (c2 instanceof AmiHdbQuery_Between)
				return reduce((AmiHdbQuery_Between) c1, (AmiHdbQuery_Between) c2);
			else if (c2 instanceof AmiHdbQuery_In)
				return reduce((AmiHdbQuery_Between) c1, (AmiHdbQuery_In) c2);
			else
				throw new RuntimeException("Unhandled scenario: " + c1 + " and " + c2);
		} else if (c1 instanceof AmiHdbQuery_In) {
			if (c2 instanceof AmiHdbQuery_Compare)
				return reduce((AmiHdbQuery_Compare) c2, (AmiHdbQuery_In) c1);
			else if (c2 instanceof AmiHdbQuery_Between)
				return reduce((AmiHdbQuery_Between) c2, (AmiHdbQuery_In) c1);
			else if (c2 instanceof AmiHdbQuery_In)
				return reduce((AmiHdbQuery_In) c1, (AmiHdbQuery_In) c2);
			else
				throw new RuntimeException("Unhandled scenario: " + c1 + " and " + c2);
		} else if (c1 instanceof AmiHdbQuery_And || c2 instanceof AmiHdbQuery_And)
			return new AmiHdbQuery_And(c1, c2);
		else
			throw new RuntimeException("Unhandled scenario: " + c1 + " and " + c2);
	}

	private AmiHdbQueryPart reduce(AmiHdbQuery_Between c1, AmiHdbQuery_In c2) {
		Set<Comparable> vals = new HashSet<Comparable>();
		int minCutoff = c1.getMinInc() ? 0 : 1;
		int maxCutoff = c1.getMaxInc() ? 0 : -1;
		Comparable min = c1.getMin(), max = c1.getMax();
		for (Comparable i : c2.getValues())
			if (OH.compare(i, min) >= minCutoff && OH.compare(i, max) <= maxCutoff)
				vals.add(i);
		if (vals.size() == 0)
			return null;
		if (vals.size() == c2.getValues().size())//all values were kept, can use original
			return c2;
		return new AmiHdbQuery_In(c1.getColumn(), vals);
	}
	private AmiHdbQueryPart reduce(AmiHdbQuery_Compare c1, AmiHdbQuery_In c2) {
		Comparable val = c1.getValue();
		if (c1.getType() == AmiHdbQuery_Compare.EQ) {
			if (c2.getValues().contains(val))
				return c1;
			else
				return null;
		}
		Set<Comparable> vals = new HashSet<Comparable>();
		switch (c1.getType()) {
			case AmiHdbQuery_Compare.GE:
				for (Comparable i : c2.getValues())
					if (OH.compare(i, val) >= 0)
						vals.add(i);
				break;
			case AmiHdbQuery_Compare.GT:
				for (Comparable i : c2.getValues())
					if (OH.compare(i, val) > 0)
						vals.add(i);
				break;
			case AmiHdbQuery_Compare.LE:
				for (Comparable i : c2.getValues())
					if (OH.compare(i, val) <= 0)
						vals.add(i);
				break;
			case AmiHdbQuery_Compare.LT:
				for (Comparable i : c2.getValues())
					if (OH.compare(i, val) < 0)
						vals.add(i);
				break;
			default:
				for (Comparable i : c2.getValues())
					if (c1.matches(i))
						vals.add(i);
				break;
		}
		if (vals.size() == 0)
			return null;
		if (vals.size() == c2.getValues().size())//all values were kept, can use original
			return c2;
		return new AmiHdbQuery_In(c1.getColumn(), vals);
	}
	private AmiHdbQueryPart reduce(AmiHdbQuery_Compare c1, AmiHdbQuery_Between c2) {
		Comparable v = c1.getValue();
		boolean below = c2.getMinInc() ? OH.compare(v, c2.getMin()) < 0 : OH.compare(v, c2.getMin()) <= 0;
		boolean above = c2.getMaxInc() ? OH.compare(v, c2.getMax()) > 0 : OH.compare(v, c2.getMin()) >= 0;

		switch (c1.getType()) {
			case AmiHdbQuery_Compare.EQ:
				if (below || above)
					return null;
				return c1;
			case AmiHdbQuery_Compare.GE:
				if (below)
					return c2;
				if (above)
					return null;
				return new AmiHdbQuery_Between(c1.getColumn(), v, true, c2.getMax(), c2.getMaxInc());
			case AmiHdbQuery_Compare.GT:
				if (below)
					return c2;
				if (above)
					return null;
				return new AmiHdbQuery_Between(c1.getColumn(), v, false, c2.getMax(), c2.getMaxInc());
			case AmiHdbQuery_Compare.LE:
				if (above)
					return c2;
				if (below)
					return null;
				return new AmiHdbQuery_Between(c1.getColumn(), c2.getMin(), c2.getMinInc(), v, true);
			case AmiHdbQuery_Compare.LT:
				if (above)
					return c2;
				if (below)
					return null;
				return new AmiHdbQuery_Between(c1.getColumn(), c2.getMin(), c2.getMinInc(), v, false);
			default:
				return new AmiHdbQuery_And(c1, c2);
		}
	}

	private AmiHdbQueryPart reduce(AmiHdbQuery_Between c1, AmiHdbQuery_Between c2) {
		final int minc = OH.compare(c1.getMin(), c2.getMin());
		final Comparable min;
		final boolean minInc;
		if (minc > 0) {
			min = c1.getMin();
			minInc = c1.getMinInc();
		} else if (minc < 0) {
			min = c2.getMin();
			minInc = c2.getMinInc();
		} else {
			min = c1.getMin();
			minInc = c1.getMinInc() && c2.getMinInc();
		}
		final int maxc = OH.compare(c1.getMax(), c2.getMax());
		final Comparable max;
		final boolean maxInc;
		if (maxc > 0) {
			max = c1.getMax();
			maxInc = c1.getMaxInc();
		} else if (minc < 0) {
			max = c2.getMax();
			maxInc = c2.getMaxInc();
		} else {
			max = c1.getMax();
			maxInc = c1.getMaxInc() && c2.getMaxInc();
		}
		return new AmiHdbQuery_Between(c1.getColumn(), min, minInc, max, maxInc);
	}
	private AmiHdbQueryPart reduce(AmiHdbQuery_In c1, AmiHdbQuery_In c2) {
		Set<Comparable> vals = CH.comm(c1.getValues(), c2.getValues(), false, false, true);
		if (vals.size() == 0)
			return null;
		return new AmiHdbQuery_In(c1.getColumn(), vals);
	}
	private AmiHdbQueryPart reduce(AmiHdbQuery_Compare c1, AmiHdbQuery_Compare c2) {
		final AmiHdbColumn col = c1.getColumn();
		final AmiHdbQueryPart r;
		final int op1 = c1.getType() | (c2.getType() << 8);
		final Comparable v1 = c1.getValue();
		final Comparable v2 = c2.getValue();
		switch (op1) {
			case AmiHdbQuery_Compare.GT | (AmiHdbQuery_Compare.LT << 8):
				if (OH.compare(v1, v2) >= 0)
					return null;
				return new AmiHdbQuery_Between(col, v1, false, v2, false);
			case AmiHdbQuery_Compare.GT | (AmiHdbQuery_Compare.LE << 8):
				if (OH.compare(v1, v2) >= 0)
					return null;
				return new AmiHdbQuery_Between(col, v1, false, v2, true);
			case AmiHdbQuery_Compare.GE | (AmiHdbQuery_Compare.LT << 8):
				if (OH.compare(v1, v2) >= 0)
					return null;
				return new AmiHdbQuery_Between(col, v1, true, v2, false);
			case AmiHdbQuery_Compare.GE | (AmiHdbQuery_Compare.LE << 8):
				if (OH.compare(v1, v2) > 0)
					return null;
				return new AmiHdbQuery_Between(col, v1, true, v2, true);
			case AmiHdbQuery_Compare.LT | (AmiHdbQuery_Compare.GT << 8):
				if (OH.compare(v1, v2) <= 0)
					return null;
				return new AmiHdbQuery_Between(col, v2, false, v1, false);
			case AmiHdbQuery_Compare.LT | (AmiHdbQuery_Compare.GE << 8):
				if (OH.compare(v1, v2) <= 0)
					return null;
				return new AmiHdbQuery_Between(col, v2, true, v1, false);
			case AmiHdbQuery_Compare.LE | (AmiHdbQuery_Compare.GT << 8):
				if (OH.compare(v1, v2) <= 0)
					return null;
				return new AmiHdbQuery_Between(col, v2, false, v1, true);
			case AmiHdbQuery_Compare.LE | (AmiHdbQuery_Compare.GE << 8):
				if (OH.compare(v1, v2) < 0)
					return null;
				return new AmiHdbQuery_Between(col, v2, true, v1, true);
			case AmiHdbQuery_Compare.LE | (AmiHdbQuery_Compare.LE << 8):
				return new AmiHdbQuery_Le(col, OH.min(v1, v2));
			case AmiHdbQuery_Compare.LT | (AmiHdbQuery_Compare.LT << 8):
				return new AmiHdbQuery_Lt(col, OH.min(v1, v2));
			case AmiHdbQuery_Compare.LE | (AmiHdbQuery_Compare.LT << 8):
				if (OH.compare(v1, v2) < 0)
					return new AmiHdbQuery_Le(col, v1);
				else
					return new AmiHdbQuery_Lt(col, v2);
			case AmiHdbQuery_Compare.LT | (AmiHdbQuery_Compare.LE << 8):
				if (OH.compare(v1, v2) < 0)
					return new AmiHdbQuery_Lt(col, v1);
				else
					return new AmiHdbQuery_Le(col, v2);
			case AmiHdbQuery_Compare.GE | (AmiHdbQuery_Compare.GE << 8):
				return new AmiHdbQuery_Ge(col, OH.max(v1, v2));
			case AmiHdbQuery_Compare.GT | (AmiHdbQuery_Compare.GT << 8):
				return new AmiHdbQuery_Gt(col, OH.max(v1, v2));
			case AmiHdbQuery_Compare.GE | (AmiHdbQuery_Compare.GT << 8):
				if (OH.compare(v1, v2) > 0)
					return new AmiHdbQuery_Ge(col, v1);
				else
					return new AmiHdbQuery_Gt(col, v2);
			case AmiHdbQuery_Compare.GT | (AmiHdbQuery_Compare.GE << 8):
				if (OH.compare(v1, v2) > 0)
					return new AmiHdbQuery_Gt(col, v1);
				else
					return new AmiHdbQuery_Ge(col, v2);
			case AmiHdbQuery_Compare.LE | (AmiHdbQuery_Compare.EQ << 8):
				if (OH.compare(v1, v2) < 0)
					return null;
				return c2;
			case AmiHdbQuery_Compare.LT | (AmiHdbQuery_Compare.EQ << 8):
				if (OH.compare(v1, v2) <= 0)
					return null;
				return c2;
			case AmiHdbQuery_Compare.GE | (AmiHdbQuery_Compare.EQ << 8):
				if (OH.compare(v1, v2) > 0)
					return null;
				return c2;
			case AmiHdbQuery_Compare.GT | (AmiHdbQuery_Compare.EQ << 8):
				if (OH.compare(v1, v2) >= 0)
					return null;
				return c2;
			case AmiHdbQuery_Compare.EQ | (AmiHdbQuery_Compare.LE << 8):
				if (OH.compare(v1, v2) > 0)
					return null;
				return c1;
			case AmiHdbQuery_Compare.EQ | (AmiHdbQuery_Compare.LT << 8):
				if (OH.compare(v1, v2) >= 0)
					return null;
				return c1;
			case AmiHdbQuery_Compare.EQ | (AmiHdbQuery_Compare.GE << 8):
				if (OH.compare(v1, v2) < 0)
					return null;
				return c1;
			case AmiHdbQuery_Compare.EQ | (AmiHdbQuery_Compare.GT << 8):
				if (OH.compare(v1, v2) <= 0)
					return null;
				return c1;
			case AmiHdbQuery_Compare.EQ | (AmiHdbQuery_Compare.EQ << 8):
				if (OH.compare(v1, v2) != 0)
					return null;
				return c1;
			default:
				return new AmiHdbQuery_And(c1, c2);
			//				throw new RuntimeException("Unknown scenario: " + op1);
		}
	}

	public void setAdditionalClause(DerivedCellCalculator dcc) {
		if (dcc == null)
			return;
		OH.assertEq(dcc.getReturnType(), Boolean.class);
		if (dcc.isConst())
			if (Boolean.TRUE.equals(dcc.get(null)))
				return;
		this.additionalClause = dcc;
		//		DerivedHelper.getDependencyIds(this.additionalClause, dependencies);
	}

	public void setLimit(long limitOffset, int limit) {
		this.limitOffset = limitOffset;
		this.limit = limit;
	}

	public void setOrderBys(boolean[] orderByAsc, DerivedCellCalculator[] orderByCalcs) {
		if (AH.isEmpty(orderByCalcs)) {
			this.orderByAsc = OH.EMPTY_BOOLEAN_ARRAY;
			this.orderByCalcs = DerivedHelper.EMPTY_ARRAY;
		} else {
			this.orderByAsc = orderByAsc;
			this.orderByCalcs = orderByCalcs;
			//			for (DerivedCellCalculator dcc : orderByCalcs)
			//				DerivedHelper.getDependencyIds(dcc, dependencies);
		}
	}

	public AmiHdbTable getTable() {
		return this.table;
	}

	public DerivedCellCalculator getAdditionalClause() {
		return this.additionalClause;
	}

	public DerivedCellCalculator[] getOrderBysAfter() {
		return this.afterOrderByColumns;
	}
	public boolean[] getOrderBysAscAfter() {
		return this.afterOrderByColumnsAscending;
	}
	public DerivedCellCalculator[] getOrderBysBefore() {
		return this.beforeOrderByColumns;
	}
	public boolean[] getOrderBysAscBefore() {
		return this.beforeOrderByColumnsAscending;
	}
	public DerivedCellCalculator[] getOrderBysAfterExtra() {
		return this.afterOrderByColumnsExtra;
	}
	public int[] getOrderBysAscAfterExtra() {
		return this.afterOrderByColumnsExtraPos;
	}
	public int[] getOrderBysAscAfterPos() {
		return this.afterOrderByColumnsPos;
	}

	public String[] getSelectNames() {
		return this.selectNames;
	}

	public DerivedCellCalculator[] getSelectCalcs() {
		return this.selectCalcs;
	}

	public void setGroupBys(DerivedCellCalculator[] groupby, DerivedCellCalculator having, AggCalculator[] aggregates) {
		this.groupBys = groupby;
		this.having = having;
		this.aggregates = aggregates;
		if (AH.isEmpty(this.groupBys))
			this.groupBys = DerivedHelper.EMPTY_ARRAY;
	}

	public AggCalculator[] getAggregates() {
		return this.aggregates;
	}

	public void setRowNumColumn(String string) {
		this.rowNumColumn = string;
	}
	public String getRowNumColumn() {
		return rowNumColumn;
	}

	public void setPartitionColumn(String string) {
		this.partitionColumn = string;
	}

	public String getPartitionColumn() {
		return this.partitionColumn;
	}

}
