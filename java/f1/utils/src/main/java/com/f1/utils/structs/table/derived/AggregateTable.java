package com.f1.utils.structs.table.derived;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Column;
import com.f1.base.Getter;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableListener;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.TableHelper;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.structs.IntKeyMap;

public class AggregateTable extends DerivedTable implements TableListener {

	private static final Column[] EMPTY_COLUMNS = new Column[0];

	private DerivedTable inner;
	private AggregateGroupByColumn[] groupbyColumns;
	private Map<Object, AggregateGroupByColumn> idToGroupbyColumns = new HashMap<Object, AggregateGroupByColumn>();
	private AggregateColumn[] aggregateColumns = new AggregateColumn[0];
	private Column innerLocationToColumns[][];//TODO:must be 2d-array to handle multiple aggs on a single column
	private Object[] tmp = null;

	protected IntKeyMap<AggregateRow> constituentsToRows = new IntKeyMap<AggregateRow>();
	protected Map<Object[], AggregateRow> groupingToRows = new HasherMap<Object[], AggregateRow>(ArrayHasher.INSTANCE);

	public AggregateTable(DerivedTable inner, String... groupingColumns) {
		super(inner.getStackFrame());
		this.inner = inner;
		groupbyColumns = new AggregateGroupByColumn[groupingColumns.length];
		for (int i = 0; i < groupingColumns.length; i++) {
			String id = groupingColumns[i];
			AggregateGroupByColumn col = new AggregateGroupByColumn(inner, uid++, i, inner.getColumn(id).getType(), id, id);
			super.addColumn(i, col, null);
			groupbyColumns[i] = col;
			idToGroupbyColumns.put(col.getId(), col);
		}
		this.tmp = new Object[this.groupbyColumns.length];
		deterimeAggregateColumns();
		repopulateRowsFromInner();
		this.inner.addTableListener(this);
	}

	public final AggregateGroupByColumn addGroupingColumn(String id, String innerColumnId) {
		return addGroupingColumn(getColumnsCount(), id, innerColumnId);
	}
	public final AggregateGroupByColumn addGroupingColumn(int location, String id, String innerColumnId) {
		if (isAggregateInnerColumn(innerColumnId))
			throw new RuntimeException("Can not have aggregate and groupby on same dependency: " + innerColumnId);
		if (isGroupByInnerColumn(innerColumnId))
			throw new RuntimeException("Duplicate groupby dependency: " + innerColumnId);
		AggregateGroupByColumn col = new AggregateGroupByColumn(inner, uid++, location, inner.getColumn(innerColumnId).getType(), id, innerColumnId);
		groupbyColumns = AH.append(groupbyColumns, col);
		this.tmp = new Object[this.groupbyColumns.length];
		idToGroupbyColumns.put(col.getId(), col);
		super.addColumn(location, col, null);
		clear();
		deterimeAggregateColumns();
		repopulateRowsFromInner();
		return col;
	}

	public void clear() {
		this.getRows().clear();
		this.constituentsToRows.clear();
		this.groupingToRows.clear();
	}
	public void clearUnderlyings() {
		clear();
		this.inner.removeTableListener(this);
		this.inner.getRows().clear();
		this.inner.addTableListener(this);
	}

	public void repopulateRowsFromInner() {
		clear();
		for (Row add : inner.getRows()) {
			AggregateRow aggRow = determineRow(add, true);
			constituentsToRows.put(add.getUid(), aggRow);
			aggRow.addConstituent(add);
		}
		for (AggregateRow aggRow : this.groupingToRows.values()) {
			for (AggregateColumn ac : aggregateColumns) {
				Object val = ac.recalc(aggRow.getConstituents().values());
				aggRow.putAt(ac.getLocation(), val);
			}
		}
	}

	public AggregateFirstColumn addFirstColumn(String id, String innerId) {
		return addFirstColumn(getColumnsCount(), id, innerId);
	}
	public AggregateSumColumn addSumColumn(String id, String innerId) {
		return addSumColumn(getColumnsCount(), id, innerId);
	}
	public AggregateSumColumn addSumColumn(int location, String id, String innerId) {
		return addAggregateColumn(new AggregateSumColumn(this, uid++, location, inner.getColumn(innerId).getType(), id, innerId));
	}
	public AggregateFirstColumn addFirstColumn(int location, String id, String innerId) {
		return addAggregateColumn(new AggregateFirstColumn(this, uid++, location, inner.getColumn(innerId).getType(), id, innerId));
	}

	public AggregateMinColumn addMinColumn(String id, String innerId) {
		return addMinColumn(getColumnsCount(), id, innerId);
	}
	public AggregateMinColumn addMinColumn(int location, String id, String innerId) {
		return addAggregateColumn(new AggregateMinColumn(this, uid++, location, inner.getColumn(innerId).getType(), id, innerId));
	}

	public AggregateMaxColumn addMaxColumn(String id, String innerId) {
		return addMaxColumn(getColumnsCount(), id, innerId);
	}
	public AggregateMaxColumn addMaxColumn(int location, String id, String innerId) {
		return addAggregateColumn(new AggregateMaxColumn(this, uid++, location, inner.getColumn(innerId).getType(), id, innerId));
	}

	public AggregateCountColumn addCountColumn(String id, String innerId) {
		return addCountColumn(getColumnsCount(), id, innerId);
	}
	public AggregateCountColumn addCountColumn(int location, String id, String innerId) {
		return addAggregateColumn(new AggregateCountColumn(this, uid++, location, id, innerId));
	}

	@Override
	public <T> DerivedColumn addDerivedColumn(int location, String id, DerivedCellCalculator calc) {
		return super.addDerivedColumn(location, id, calc);
	}

	private <T extends AggregateColumn> T addAggregateColumn(T col) {
		Column innercol = inner.getColumn(col.getInnerColumnId());
		super.addColumn(col.getLocation(), col, null);
		int innerloc = innercol.getLocation();
		col.setInnerColumnLocation(innerloc);
		this.innerLocationToColumns[innerloc] = AH.append(innerLocationToColumns[innerloc], col);
		aggregateColumns = AH.insert(aggregateColumns, aggregateColumns.length, col);
		repopulateRowsFromInner();
		return col;
	}

	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		final Column[] columns = innerLocationToColumns[cell];
		if (columns.length == 0)
			return;
		if (columns.length == 1) {
			Column column = columns[0];
			if (column instanceof AggregateGroupByColumn) {
				onRowRemoved(row, row.getLocation());
				onRowAdded(row);
			} else if (column instanceof AggregateColumn) {
				AggregateRow aggRow = constituentsToRows.get(row.getUid());
				if (newValue == DerivedRow.NOT_CACHED)
					newValue = row.getAt(cell);
				aggRow.updateValue(column.getLocation(), oldValue, newValue, (AggregateColumn) column);
			}
		} else {
			AggregateRow aggRow = constituentsToRows.get(row.getUid());
			for (int i = 0; i < columns.length; i++) {
				if (newValue == DerivedRow.NOT_CACHED)
					newValue = row.getAt(cell);
				Column column = columns[i];
				if (column instanceof AggregateColumn)
					aggRow.updateValue(column.getLocation(), oldValue, newValue, (AggregateColumn) column);
				else if (column instanceof AggregateGroupByColumn) {
					row.getValues()[cell] = oldValue;
					onRowRemoved(row, row.getLocation());
					row.getValues()[cell] = newValue;
					onRowAdded(row);
					break;
				}
			}
		}
	}

	@Override
	public void onColumnAdded(Column nuw) {
		deterimeAggregateColumns();
	}

	public boolean isGroupByInnerColumn(Object id) {
		for (AggregateGroupByColumn col : groupbyColumns)
			if (col.getEnabled() && OH.eq(id, col.getInnerColumnId()))
				return true;
		return false;
	}
	public boolean isAggregateInnerColumn(Object id) {
		for (AggregateColumn col : aggregateColumns)
			if (OH.eq(id, col.getInnerColumnId()))
				return true;
		return false;
	}

	@Override
	public void onColumnRemoved(Column old) {
		if (isGroupByInnerColumn(old) || isAggregateInnerColumn(old)) {
			for (int i = 0; i < groupbyColumns.length; i++) {
				AggregateGroupByColumn col = groupbyColumns[i];
				if (OH.eq(old, col.getInnerColumnId()))
					removeColumn(col.getId());
			}
			for (int i = 0; i < aggregateColumns.length; i++) {
				AggregateColumn col = aggregateColumns[i];
				if (OH.eq(old, col.getInnerColumnId()))
					removeColumn(col.getId());
			}
		}
		deterimeAggregateColumns();
	}

	@Override
	public void removeColumn(int location) {
		Column col = getColumnAt(location);
		if (col instanceof AggregateGroupByColumn) {
			this.groupbyColumns = AH.remove(this.groupbyColumns, AH.indexOf(col, this.groupbyColumns));
			this.tmp = new Object[this.groupbyColumns.length];
			this.idToGroupbyColumns.remove(col.getId());
		} else if (col instanceof AggregateColumn) {
			this.aggregateColumns = AH.remove(this.aggregateColumns, AH.indexOf(col, this.aggregateColumns));
		}
		clear();
		super.removeColumn(location);
		deterimeAggregateColumns();
		repopulateRowsFromInner();
	}

	@Override
	public void onColumnChanged(Column old, Column nuw) {
		if (isAggregateInnerColumn(old))
			throw new RuntimeException("can not change columns with aggregate dependencies: " + old);
		if (isGroupByInnerColumn(old))
			throw new RuntimeException("can not change columns with groupby dependencies: " + old);
		deterimeAggregateColumns();
	}

	private void deterimeAggregateColumns() {
		innerLocationToColumns = new Column[inner.getColumnsCount()][];
		for (int i = 0; i < innerLocationToColumns.length; i++)
			innerLocationToColumns[i] = EMPTY_COLUMNS;

		for (AggregateGroupByColumn col : groupbyColumns) {
			if (!col.getEnabled())
				continue;
			final Column innerColumn = inner.getColumn(col.getInnerColumnId());
			int loc = innerColumn.getLocation();
			col.setInnerColumnLocation(loc);
			this.innerLocationToColumns[loc] = AH.append(this.innerLocationToColumns[loc], col);
		}
		for (AggregateColumn col : aggregateColumns) {
			final Column innerColumn = inner.getColumn(col.getInnerColumnId());
			int loc = innerColumn.getLocation();
			col.setInnerColumnLocation(loc);
			this.innerLocationToColumns[loc] = AH.append(this.innerLocationToColumns[loc], col);
		}
	}

	@Override
	public void onRowAdded(Row add) {
		AggregateRow aggRow = determineRow(add, true);
		constituentsToRows.put(add.getUid(), aggRow);
		aggRow.addConstituent(add);
		for (AggregateColumn ac : aggregateColumns) {
			int innerLocation = ac.getInnerColumnLocation();
			int loc = ac.getLocation();
			aggRow.putAt(loc, ac.calculate(aggRow.getAt(loc), null, add.getAt(innerLocation)));
		}
	}
	@Override
	public void onRowRemoved(Row removed, int location) {
		AggregateRow aggRow = constituentsToRows.remove(removed.getUid());
		if (aggRow == null)
			throw new IllegalStateException();
		aggRow.removeConstituent(removed);
		if (aggRow.getConstituents().size() == 0) {
			getRows().remove(aggRow);
			CH.removeOrThrow(groupingToRows, aggRow.getGroupingKey());
		} else {
			for (AggregateColumn ac : aggregateColumns) {
				int innerLocation = ac.getInnerColumnLocation();
				int loc = ac.getLocation();
				Object currentValue = aggRow.getAt(loc);
				if (currentValue != AggregateRow.NOT_AGGEGATED)
					aggRow.putAt(loc, ac.calculate(currentValue, removed.getAt(innerLocation), null));
			}
		}
	}
	public AggregateRow determineRow(Row inner, boolean create) {
		for (int i = 0; i < groupbyColumns.length; i++) {
			AggregateGroupByColumn gbc = groupbyColumns[i];
			if (!gbc.getEnabled())
				tmp[i] = null;
			else
				tmp[i] = inner.getAt(gbc.getInnerColumnLocation());
		}
		AggregateRow row = groupingToRows.get(tmp);
		if (row != null || !create) {
			return row;
		}
		final Object[] values = new Object[getColumnsCount()];
		for (int i = 0; i < groupbyColumns.length; i++) {
			AggregateGroupByColumn aggregateGroupByColumn = groupbyColumns[i];
			values[aggregateGroupByColumn.getLocation()] = tmp[i];
		}
		row = newDerivedRow(values, true);
		row.setKey(tmp);
		getRows().add(row);
		groupingToRows.put(tmp, row);

		tmp = new Object[groupbyColumns.length];
		return row;
	}
	public AggregateRow getAggregateRow(Row inner) {
		return constituentsToRows.get(inner.getLocation());
	}

	protected Iterable<Row> getConstituentRows(Row row) {
		return ((AggregateRow) row).getConstituents().values();
	}

	@Override
	protected AggregateRow newDerivedRow(Object[] values, boolean b) {
		return new AggregateRow(this, uid++, values, b);
	}

	public AggregateColumn[] getAggregateColumns() {
		return aggregateColumns;
	}

	public static final Set<String> AGG_METHODS = CH.s("min", "max", "count", "sum", "first");

	//	public DerivedCellCalculator createDerivedCellCalculator(String expression, com.f1.base.Types types, MethodFactoryManager methodFactory, ExpressionParser ep) {
	//		DerivedCellParserAgg p2 = new DerivedCellParserAgg(ep);
	//		return p2.toCalc(expression, types, methodFactory);
	//	}

	@Override
	public DerivedColumn addDerivedColumn(String id, DerivedCellCalculator calc) {
		return addDerivedColumn(id, calc, COLUMN_ID_FACTORY);
	}

	public DerivedColumn addDerivedColumn(String id, DerivedCellCalculator calc, Getter<Table, String> columnIdFactory) {
		List<DerivedCellAgg> sink = getAggs(calc);
		if (sink != null)
			for (DerivedCellAgg e : sink) {
				if (e.getId() != null)
					continue;

				//can we reuse an existing inner column?, if so grab its col id and stuff in the subColId
				String subColId = null;
				if (e.subCalc instanceof DerivedCellCalculatorRef) {
					subColId = (String) ((DerivedCellCalculatorRef) e.subCalc).getId();
				} else
					for (int i : inner.getDerivedColumns()) {
						DerivedColumn existing = inner.getDerivedColumn(i);
						if (existing.getCalculator().equals(e.subCalc)) {
							subColId = existing.getId();
							break;
						}
					}

				String outerId = null;
				if (subColId == null) {
					subColId = columnIdFactory.get(inner);
					inner.addDerivedColumn(subColId, e.subCalc);
				} else {//so we're able to use a subid, how about an existing agg?
					for (AggregateColumn column : getAggregateColumns())
						if (subColId.equals(column.getInnerColumnId()) && e.methodName.equals(column.getMethodName())) {
							outerId = column.getId();
							break;
						}
				}
				if (outerId == null) {
					outerId = columnIdFactory.get(this);
					if ("min".equals(e.methodName)) {
						addMinColumn(outerId, subColId);
					} else if ("max".equals(e.methodName)) {
						addMaxColumn(outerId, subColId);
					} else if ("count".equals(e.methodName)) {
						addCountColumn(outerId, subColId);
					} else if ("sum".equals(e.methodName)) {
						addSumColumn(outerId, subColId);
					} else if ("first".equals(e.methodName)) {
						addFirstColumn(outerId, subColId);
					}
				}
				e.subCalc = null;
				e.methodName = null;
				e.setId(outerId);
			}
		return super.addDerivedColumn(id, calc);
	}
	public static <T extends DerivedCellCalculator> List<DerivedCellAgg> getAggs(DerivedCellCalculator calc) {
		return (List<DerivedCellAgg>) DerivedHelper.find(calc, DerivedCellAgg.class, null);
	}

	public static class DerivedCellAgg extends DerivedCellCalculatorRef {

		public DerivedCellCalculator subCalc;
		public String methodName;

		public DerivedCellAgg(int textPosition, String methodName, Class<?> type, DerivedCellCalculator subCalc) {
			super(textPosition, type, null);
			this.methodName = methodName;
			this.subCalc = subCalc;
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			if (methodName == null)
				return super.toString(sink);
			sink.append(methodName).append('(');
			subCalc.toString(sink);
			sink.append(')');
			return sink;
		}

		public String toString() {
			return this.toString(new StringBuilder()).toString();
		}

		@Override
		public Set<Object> getDependencyIds(Set<Object> sink) {
			if (getId() != null)
				sink.add(getId());
			return DerivedHelper.getDependencyIds(subCalc, sink);
		}
		@Override
		public boolean equals(Object other) {
			if (other == null || other.getClass() != DerivedCellAgg.class)
				return false;
			DerivedCellAgg o = (DerivedCellAgg) other;
			return OH.eq(getId(), o.getId()) && OH.eq(getReturnType(), o.getReturnType()) && OH.eq(methodName, o.methodName) && OH.eq(subCalc, o.subCalc);
		}

		@Override
		public int hashCode() {
			return OH.hashCode(getId(), getReturnType(), methodName);
		}

		@Override
		public boolean isSame(DerivedCellCalculator other) {
			if (!super.isSame(other))
				return false;
			DerivedCellAgg o = (DerivedCellAgg) other;
			return OH.eq(methodName, o.methodName) && OH.eq(subCalc, o.subCalc);
		}
	}

	public static final Getter<Table, String> COLUMN_ID_FACTORY = new Getter<Table, String>() {

		@Override
		public String get(Table table) {
			return TableHelper.generateId(table.getColumnIds(), "!");
		}
	};

	public Map<Object, AggregateGroupByColumn> getGroupbyColumns() {
		return this.idToGroupbyColumns;
	}

}
