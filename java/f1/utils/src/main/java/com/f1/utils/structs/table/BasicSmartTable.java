/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs.table;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Getter;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.base.TableListenable;
import com.f1.base.TableListener;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.LocalToolkit;
import com.f1.utils.MH;
import com.f1.utils.MergeSort;
import com.f1.utils.OH;
import com.f1.utils.ToDoException;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.ListWrapper;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedRow;
import com.f1.utils.structs.table.derived.DerivedTable;

public class BasicSmartTable implements SmartTable, TableListener {

	private static final Logger log = Logger.getLogger(BasicSmartTable.class.getName());

	private final TableListenable inner;
	private final IntKeyMap<Row> filtered = new IntKeyMap<Row>();
	private final SmartTableList tableList;
	private RowFilter filter = null;
	private Comparator<Row> rowComparator = null;
	private boolean keepSorting = false;
	private String title;
	private boolean sortChanged;
	private boolean filteredChanged;

	private LocalToolkit tk = new LocalToolkit();

	@Override
	public List<Column> getColumns() {
		return inner.getColumns();
	}

	@Override
	public Map<String, Column> getColumnsMap() {
		return inner.getColumnsMap();
	}

	@Override
	public Column getColumnAt(int location) {
		return inner.getColumnAt(location);
	}

	@Override
	public Column getColumn(String id) {
		return inner.getColumn(id);
	}

	@Override
	public int getColumnsCount() {
		return inner.getColumnsCount();
	}

	@Override
	public Object getAt(int row, int column) {
		return inner.getAt(row, column);
	}

	@Override
	public Object get(int row, String column) {
		return inner.get(row, column);
	}

	@Override
	public <C> C getAt(int row, int column, Class<C> c) {
		return inner.getAt(row, column, c);
	}
	@Override
	public <C> C getAt(int row, int column, Caster<C> c) {
		return inner.getAt(row, column, c);
	}

	@Override
	public <C> C get(int row, String column, Class<C> c) {
		return inner.get(row, column, c);
	}
	@Override
	public <C> C get(int row, String column, Caster<C> c) {
		return inner.get(row, column, c);
	}

	@Override
	public <T> Column addColumn(int location, Class<T> clazz, String id, T defaultValue) {
		TableList rows = (TableList) inner.getRows();
		int size = rows.size();
		rows.addAll(filtered.values());
		Column r = inner.addColumn(clazz, id, defaultValue);
		int last = rows.size();
		while (last > size)
			rows.remove(--last);
		return r;
	}

	@Override
	public Column addDerivedColumn(String id, DerivedCellCalculator calc) {
		DerivedTable derivedTable = (DerivedTable) inner;
		TableList rows = (TableList) derivedTable.getRows();
		int size = rows.size();
		rows.addAll(filtered.values());
		try {
			Column r = derivedTable.addDerivedColumn(id, calc);
			return r;
		} finally {
			int last = rows.size();
			while (last > size)
				rows.remove(--last);
		}
	}

	@Override
	public void renameColumn(String oldId, String newId) {
		inner.renameColumn(oldId, newId);
	}

	@Override
	public void renameColumn(int location, String newId) {
		inner.renameColumn(location, newId);
	}

	@Override
	public void removeColumn(int location) {
		TableList rows = inner.getRows();
		int size = rows.size();
		rows.addAll(filtered.values());
		inner.removeColumn(location);
		int last = rows.size();
		while (last > size)
			rows.remove(--last);
	}

	@Override
	public void removeColumn(String id) {
		TableList rows = inner.getRows();
		int size = rows.size();
		rows.addAll(filtered.values());
		inner.removeColumn(id);
		int last = rows.size();
		while (last > size)
			rows.remove(--last);
	}

	@Override
	public TableList getRows() {
		return tableList;
	}

	@Override
	public int getSize() {
		return inner.getSize();
	}

	public BasicSmartTable(TableListenable inner) {
		this.inner = (BasicTable) inner;
		tableList = new SmartTableList(inner.getRows());
		inner.addTableListener(this);
		this.filteredChanged = true;
		this.sortChanged = true;
	}

	private class SmartTableList extends ListWrapper<Row> implements TableList {

		@Override
		public Row[] toRowsArray() {
			return toArray(new Row[this.size()]);
		}
		public SmartTableList(List inner) {
			super(inner);
		}

		@Override
		public Row addRow(Object... values) {
			Row r = inner.newRow(values);
			add(r);
			return r;
		}

		@Override
		public boolean add(Row e) {
			addRowToIndex(e);
			if (!shouldKeep(e)) {
				addToFilterend(e);
			} else if (keepSorting) {
				CH.insertSorted(inner.getRows(), e, rowComparator, false);
			} else
				super.add(e);
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Row> c) {
			for (Row r : c)
				add(r);
			return true;
		}
		@Override
		public void addAll(Iterable<? extends Row> c) {
			for (Row r : c)
				add(r);
		}

		@Override
		public boolean addAll(int index, Collection<? extends Row> c) {
			if (keepSorting) {
				for (Row r : c)
					add(r);
			} else
				super.addAll(index, c);
			return true;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			for (Object r : c)
				remove(r);
			return true;
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Row)
				removeRowFromIndex((Row) o);
			return super.remove(o);
		}
		@Override
		public Row remove(int o) {
			Row r = super.remove(o);
			removeRowFromIndex(r);
			return r;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return super.retainAll(c);
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException("call SmartTable.clear() instead");
		}
		protected void clearRows() {
			super.clear();
		}

		@Override
		public Row set(int index, Row element) {
			if (!shouldKeep(element)) {
				addToFilterend(element);
				return null;
			} else if (keepSorting) {
				Row r = remove(index);
				add(element);
				return r;
			} else
				return super.set(index, element);
		}

		@Override
		public void add(int index, Row element) {
			if (!shouldKeep(element)) {
				addToFilterend(element);
			} else if (keepSorting)
				add(element);
			else
				super.add(index, element);
		}

		@Override
		public Row insertRow(int i, Object... values) {
			Row r = inner.newRow(values);
			add(i, r);
			return r;
		}
		@Override
		public long getLongSize() {
			return size();
		}

	}

	public void redoRows() {
		if (!filteredChanged && !sortChanged)
			return;
		inUpdate = true;
		BasicTableList tableRows = (BasicTableList) inner.getRows();
		List<Row> rows = (List<Row>) tableRows.getInner();
		if (filter != null || this.filterIndexColumnId != null || this.filterIndexValues != null) {
			if (filteredChanged) {
				final int totalSize = rows.size() + filtered.size();
				ArrayList<Row> hide = new ArrayList<Row>(totalSize);
				ArrayList<Row> show = new ArrayList<Row>(totalSize);
				if (this.pendingFilterIndexValues != null)
					this.filterIndexValues = this.pendingFilterIndexValues;
				if (this.filterIndexValues != null) {
					for (Map.Entry<Object, IdentityHashSet<Row>> e : this.rowsByFilterIndex.entrySet()) {
						if (this.filterIndexValues.contains(e.getKey())) {
							Set<Row> rowsByIndex = e.getValue();
							for (Row row : rowsByIndex) {
								if (shouldKeep(row))
									show.add(row);
								else
									hide.add(row);
							}
						} else {
							hide.addAll(e.getValue());
						}
					}
					rows.clear();
					filtered.clear();
					rows.addAll(show);
					for (Row h : hide)
						addToFilterend(h);
					if (rowComparator != null)
						sortChanged = true;
				} else {
					for (Row row : rows)
						(shouldKeep(row) ? show : hide).add(row);
					for (Row row : filtered.values())
						(shouldKeep(row) ? show : hide).add(row);
					rows.clear();
					filtered.clear();
					rows.addAll(show);
					for (Row h : hide)
						addToFilterend(h);
					if (rowComparator != null)
						sortChanged = true;
				}
			} else if (this.pendingFilterIndexValues != null) {
				Set<Object> shouldKeep;
				Set<Object> shouldHide;
				if (this.filterIndexValues == null) {
					shouldKeep = this.pendingFilterIndexValues;
					shouldHide = null;
				} else {
					shouldKeep = CH.comm(this.filterIndexValues, this.pendingFilterIndexValues, false, true, false);
					shouldHide = CH.comm(this.filterIndexValues, this.pendingFilterIndexValues, true, false, false);
				}
				this.filterIndexValues = this.pendingFilterIndexValues;
				this.pendingFilterIndexValues = null;
				boolean doSimpleFilter = false;
				if (shouldHide != null) {
					int countToHide = 0;
					int maxToHide = 25000;//If we are removing more than 20%, just remove everything and start over.
					outer: for (Object o : shouldHide) {
						Set<Row> rs = (Set) this.rowsByFilterIndex.get(o);
						if (rs == null)
							continue;
						for (Row r : rs)
							if (r.getLocation() != -1) {
								countToHide++;
								if (countToHide >= maxToHide) {
									doSimpleFilter = true;
									break outer;
								}

							}
					}
				}
				if (doSimpleFilter) {
					filteredChanged = true;
					final int totalSize = rows.size() + filtered.size();
					ArrayList<Row> hide = new ArrayList<Row>(totalSize);
					ArrayList<Row> show = new ArrayList<Row>(totalSize);
					for (java.util.Map.Entry<Object, IdentityHashSet<Row>> o : this.rowsByFilterIndex.entrySet()) {
						if (this.filterIndexValues.contains(o.getKey())) {
							if (this.filter == null)
								show.addAll((Collection) o.getValue());
							else
								for (Row row : o.getValue()) {
									if (this.filter.shouldKeep((Row) row, tk))
										show.add((Row) row);
									else
										hide.add((Row) row);
								}
						} else {
							hide.addAll((Collection) o.getValue());
						}

					}
					rows.clear();
					filtered.clear();
					rows.addAll(show);
					for (Row h : hide)
						addToFilterend(h);
				} else {
					for (Object o : shouldKeep) {
						Set<Row> rs = (Set) this.rowsByFilterIndex.get(o);
						if (rs == null)
							continue;
						for (Row r : rs)
							if (shouldKeep(r)) {
								Row removed = filtered.remove(r.getUid());
								if (removed != null)
									rows.add(removed);
							}
					}
					if (shouldHide != null) {
						for (Object o : shouldHide) {
							Set<Row> rs = (Set) this.rowsByFilterIndex.get(o);
							if (rs == null)
								continue;
							for (Row r : rs)
								if (r.getLocation() != -1) {
									rows.remove(r.getLocation());
									addToFilterend(r);
								}
						}
					}
				}
				if (rowComparator != null)
					sortChanged = true;
			}
		} else if (filtered.size() > 0) {
			for (Row i : filtered.values())
				rows.add(i);
			filtered.clear();
			if (rowComparator != null)
				sortChanged = true;
		}
		if (rowComparator != null && sortChanged) {
			ArrayList data = new ArrayList(inner.getRows());
			MergeSort.sort(data, rowComparator);
			inner.getRows().clear();
			inner.getRows().addAll(data);
		}
		this.sortChanged = false;
		this.filteredChanged = false;
		inUpdate = false;
	}

	@Override
	public void setTableFilter(RowFilter filter) {
		this.filter = filter;
		filteredChanged = true;
		status |= STATUS_FILTER_CHANGED;
		setMinRowChangedLocation(0);
	}

	@Override
	public RowFilter getTableFilter() {
		return filter;
	}

	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {

		inUpdate = true;
		if (!filteredChanged) {
			int loc = row.getLocation();
			boolean isFiltered = loc < 0;
			if (isFiltered == shouldKeep(row)) {
				if (isFiltered) {
					removedFromFiltered(row);
					tableList.add(row);
					setMinRowChangedLocation(row.getLocation());
				} else {
					setMinRowChangedLocation(row.getLocation());
					tableList.remove(row);
					addToFilterend(row);
				}
			} else if (loc != -1) {

				//Compare against it's two neighbors
				if (!sortChanged && rowComparator != null && keepSorting && ((loc > 0 && rowComparator.compare(tableList.get(loc - 1), row) > 0)
						|| (loc < tableList.size() - 1 && rowComparator.compare(tableList.get(loc + 1), row) < 0))) {
					final int origLocation = row.getLocation();
					tableList.remove(row);
					tableList.add(row);
					setMinRowChangedLocation(Math.min(origLocation, row.getLocation()));
				} else {//didn't cause row to move, so just a cell update
					if (OH.isBetween(loc, this.cellsToMonitorRowsRangeTop, this.cellsToMonitorRowsRangeBottom)) {
						if (this.cellsToMonitorColumns == null || cell >= this.cellsToMonitorColumns.length || this.cellsToMonitorColumns[cell]) {
							status |= STATUS_CELLS_CHANGED;
							Node<BitSet> node = rownumWithCellChanges.getNodeOrCreate(loc);
							BitSet val = node.getValue();
							if (val == null)
								node.setValue(val = newBitSet());
							val.set(cell);
						}
					}
				}
			}
		}
		if (this.filterIndexColumnLoc == cell) {
			Entry<Object, IdentityHashSet<Row>> e = this.rowsByFilterIndex.getEntry(oldValue);
			if (e != null) {
				e.getValue().remove(row);
				if (e.getValue().isEmpty())
					this.rowsByFilterIndex.remove(e.getKey());
			}
			if (newValue == DerivedRow.NOT_CACHED)
				newValue = row.getAt(cell);
			e = this.rowsByFilterIndex.getOrCreateEntry(newValue);

			if (e.getValue() == null)
				e.setValue(new IdentityHashSet<Row>());
			e.getValue().add(row);
		}
		inUpdate = false;
	}

	private BitSet[] bitsetPool = new BitSet[100];
	private int bitsetPoolInUse = 0;

	private BitSet newBitSet() {
		if (bitsetPoolInUse == bitsetPool.length)
			return new BitSet();
		int pos = this.bitsetPoolInUse++;
		BitSet r = this.bitsetPool[pos];
		if (r != null) {
			r.clear();
			return r;
		}
		return this.bitsetPool[pos] = new BitSet();
	}

	private void setMinRowChangedLocation(int location) {
		status |= STATUS_ROWS_CHANGED;
		if (location < 0)
			throw new IllegalArgumentException();
		this.minRowChanged = this.minRowChanged == -1 ? location : Math.min(this.minRowChanged, location);
	}

	@Override
	public void onColumnAdded(Column nuw) {
		status |= STATUS_SCHEMA_CHANGED;
	}

	@Override
	public void onColumnRemoved(Column old) {
		if (this.filterIndexColumnId != null && this.filterIndexColumnId.equals(old.getId()))
			throw new RuntimeException("can not remove column with active index filter: " + old.getId());
		status |= STATUS_SCHEMA_CHANGED;
		updateFilterIndexColumn();
	}

	private void addRowToIndex(Row row) {
		if (filterIndexColumnId != null && !inUpdate) {
			Entry<Object, IdentityHashSet<Row>> entry = rowsByFilterIndex.getOrCreateEntry(row.getAt(filterIndexColumnLoc));
			if (entry.getValue() == null)
				entry.setValue(new IdentityHashSet<Row>());
			entry.getValue().add(row);
		}
	}
	private void removeRowFromIndex(Row row) {
		if (this.filterIndexColumnId != null && !inUpdate) {
			Entry<Object, IdentityHashSet<Row>> e = this.rowsByFilterIndex.getEntry(row.getAt(this.filterIndexColumnLoc));
			try {
				if (e == null)
					throw new RuntimeException("missing from index: " + row);
				IdentityHashSet<Row> v = e.getValue();
				if (!v.remove(row))
					throw new RuntimeException("missing from index: " + row);
				if (v.isEmpty())
					this.rowsByFilterIndex.remove(e.getKey());
			} catch (Exception e2) {
				LH.warning(log, "Remove failed for row: ", row, e2);
			}
		}
	}

	private boolean inUpdate = false;

	@Override
	public void sortRows(Comparator<Row> rowComparator, boolean keepSorting) {
		this.rowComparator = rowComparator;
		this.keepSorting = keepSorting;
		if (rowComparator != null) {
			this.sortChanged = true;
			status |= STATUS_SORT_CHANGED;
			setMinRowChangedLocation(0);
		}
	}

	private boolean shouldKeep(Row row) {
		return (filter == null || filter.shouldKeep(row, tk)) && (this.filterIndexValues == null || this.filterIndexValues.contains(row.getAt(filterIndexColumnLoc)));
	}
	@Override
	public Row newRow(Object... values) {
		return inner.newRow(values);
	}

	@Override
	public void onColumnChanged(Column old, Column nuw) {
		status |= STATUS_SCHEMA_CHANGED;
		if (this.filterIndexColumnId != null && this.filterIndexColumnId.equals(old.getId()))
			throw new RuntimeException("can not update column with active index filter: " + old.getId());
		updateFilterIndexColumn();
	}

	private void updateFilterIndexColumn() {
		if (this.filterIndexColumnId != null)
			this.filterIndexColumnLoc = getColumn(this.filterIndexColumnId).getLocation();
	}

	@Override
	public Object setAt(int row, int column, Object value) {
		return inner.setAt(row, column, value);
	}

	@Override
	public Object set(int row, String column, Object value) {
		return inner.set(row, column, value);
	}

	@Override
	public boolean getKeepSorting() {
		return keepSorting;
	}

	@Override
	public String toString() {
		return inner.toString();
	}

	@Override
	public Iterable<Row> getFiltered() {
		return filtered.values();
	}

	@Override
	public void addTableListener(TableListener tableListener) {
		inner.addTableListener(tableListener);
	}

	@Override
	public void removeTableListener(TableListener tableListener) {
		inner.removeTableListener(tableListener);
	}

	@Override
	public <T> Column addColumn(Class<T> clazz, String id, T defaultValue) {
		return addColumn(getColumnsCount(), clazz, id, defaultValue);
	}

	@Override
	public String getTitle() {
		if (title == null)
			return inner.getTitle();
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Set<String> getColumnIds() {
		return inner.getColumnIds();
	}

	@Override
	public Row newEmptyRow() {
		return inner.newEmptyRow();
	}

	@Override
	public boolean removeRow(Row row) {
		if (row.getLocation() == -1 || row.getLocation() >= tableList.size()) {
			removedFromFiltered((Row) row);
			removeRowFromIndex(row);
		} else {
			Row removed = getRows().remove(row.getLocation());
			if (removed != row)
				throw new IllegalStateException("row at " + row.getLocation() + " !=" + (removed == null ? null : removed.getLocation()) + " ==> " + row + " != " + removed);
		}

		return true;

	}

	@Override
	public <T> Column addColumn(Class<T> clazz, String id) {
		return addColumn(clazz, id, null);
	}

	private void addToFilterend(Row e) {
		filtered.put(e.getUid(), e);
	}
	private void removedFromFiltered(Row row) {
		filtered.remove(row.getUid());
	}

	@Override
	public void clear() {
		this.tableList.clearRows();
		this.rowsByFilterIndex.clear();
		this.filtered.clear();
	}

	private String filterIndexColumnId;
	private final HasherMap<Object, IdentityHashSet<Row>> rowsByFilterIndex = new HasherMap<Object, IdentityHashSet<Row>>();
	private Set<Object> filterIndexValues;
	private Set<Object> pendingFilterIndexValues;

	private int filterIndexColumnLoc = -1;

	@Override
	public void setExternalFilterIndex(String columnId, Set<Object> values) {
		if (columnId == null) {
			if (!CH.isEmpty(values))
				throw new IllegalArgumentException("columnId is null, but values supplied");
			if (this.filterIndexColumnId == null)
				return;
			this.filterIndexColumnId = null;
			this.filterIndexColumnLoc = -1;
			this.filterIndexValues = null;
			this.pendingFilterIndexValues = null;
			this.rowsByFilterIndex.clear();
		} else if (OH.eq(filterIndexColumnId, columnId)) {
			if (OH.eq(filterIndexValues, values)) {
				this.pendingFilterIndexValues = null;
				return;//nothing changed
			}
			//same column, values have changed
			this.pendingFilterIndexValues = new HasherSet<Object>(values);
		} else {
			this.filterIndexColumnLoc = getColumn(columnId).getLocation();
			this.filterIndexColumnId = columnId;
			this.rowsByFilterIndex.clear();

			this.filterIndexValues = new HasherSet<Object>(values);
			this.pendingFilterIndexValues = null;

			for (Row row : this.getRows()) {
				final Entry<Object, IdentityHashSet<Row>> e = rowsByFilterIndex.getOrCreateEntry(row.getAt(this.filterIndexColumnLoc));
				if (e.getValue() == null)
					e.setValue(new IdentityHashSet<Row>());
				e.getValue().add(row);
			}
			for (Row row : this.getFiltered()) {
				final Entry<Object, IdentityHashSet<Row>> e = rowsByFilterIndex.getOrCreateEntry(row.getAt(this.filterIndexColumnLoc));
				if (e.getValue() == null)
					e.setValue(new IdentityHashSet<Row>());
				e.getValue().add(row);
			}

		}
		status |= STATUS_FILTER_CHANGED;
		this.filteredChanged = true;
		setMinRowChangedLocation(0);
	}
	@Override
	public Object getExternalFilterIndexColumnId() {
		return this.filterIndexColumnId;
	}

	@Override
	public Set<Object> getExternalFilterIndexValues() {
		if (this.pendingFilterIndexValues != null)
			return this.pendingFilterIndexValues;
		else
			return this.filterIndexValues;
	}

	private NameSpaceCalcTypes tableTypeMapping;

	@Override
	public NameSpaceCalcTypes getColumnTypesMapping() {
		if (tableTypeMapping == null)
			this.tableTypeMapping = new TableTypeMapping(this);
		return this.tableTypeMapping;
	}

	@Override
	public <F, T> void setColumnType(int location, Class<F> fromType, Class<T> type, Getter<? super F, ? extends T> caster) {
		throw new ToDoException();
	}

	@Override
	public void debugState() {
		LH.warning(log, "Filtered: ", this.filtered);
		LH.warning(log, "Table: ", this.inner);
	}
	@Override
	public int newRows(int count) {
		int r = getSize();
		while (count-- > 0)
			getRows().addRow(OH.EMPTY_OBJECT_ARRAY);
		return r;
	}

	@Override
	public void fireCellChanged(Row basicRow, int i, Object old, Object value) {
	}

	private byte status = 0;
	private IntKeyMap<BitSet> rownumWithCellChanges = new IntKeyMap<BitSet>();

	private int minRowChanged;

	@Override
	public void onRowAdded(Row add) {
		onRowsChanged(add.getLocation());
	}

	@Override
	public void onRowRemoved(Row remove, int location) {
		onRowsChanged(location);
	}
	private void onRowsChanged(int rowNum) {
		setMinRowChangedLocation(rowNum);
	}

	private int changedCellsTmp[] = new int[1];

	private int cellsToMonitorRowsRangeTop = 0;
	private int cellsToMonitorRowsRangeBottom = Integer.MAX_VALUE;
	private boolean[] cellsToMonitorColumns;

	//end of list marked by -1
	@Override
	public int[] getCellsChanged(int top, int bottom) {
		if (!MH.anyBits(status, STATUS_CELLS_CHANGED))
			throw new IllegalStateException("STATUS NOT CELLS_CHANGED");
		if (this.rownumWithCellChanges.isEmpty()) {
			this.changedCellsTmp[0] = -1;
		} else {
			int n = 0;
			int necessarySize = Math.min(bottom - top + 1, this.rownumWithCellChanges.size()) + 1;
			if (changedCellsTmp.length < necessarySize)
				changedCellsTmp = new int[necessarySize];

			for (Node<BitSet> e : this.rownumWithCellChanges) {
				if (OH.isBetween(e.getIntKey(), top, bottom))
					this.changedCellsTmp[n++] = e.getIntKey();
			}
			this.changedCellsTmp[n] = -1;
		}
		return this.changedCellsTmp;
	}
	@Override
	public BitSet getCellsChangedAtRow(int row) {
		return this.rownumWithCellChanges.get(row);
	}
	@Override
	public int getMinRowChanged() {
		if (!MH.anyBits(status, STATUS_ROWS_CHANGED))
			throw new IllegalStateException("STATUS NOT ROWS_CHANGED");
		return this.minRowChanged;
	}

	@Override
	public void resetStatus() {
		this.rownumWithCellChanges.clear();
		this.bitsetPoolInUse = 0;
		this.minRowChanged = -1;
		this.status = 0;
	}

	@Override
	public byte ensureUpToDateAndGetStatus() {
		redoRows();
		return this.status;
	}

	@Override
	public void setListenForCellsRowRange(int top, int bottom) {
		this.cellsToMonitorRowsRangeTop = top;
		this.cellsToMonitorRowsRangeBottom = bottom;
	}

	@Override
	public void setListenForCellsColumns(boolean[] columns) {
		this.cellsToMonitorColumns = columns;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return this.inner.toString(sink);
	}

	public Table getTable() {
		return this.inner;
	}

	@Override
	public Row getRow(int i) {
		return inner.getRow(i);
	}

	@Override
	public void removeRow(int i) {
		inner.removeRow(i);
	}

	@Override
	public <T> Column addColumn(int location, Caster<T> clazz, String id, T defaultValue) {
		return addColumn(location, clazz.getCastToClass(), id, defaultValue);
	}

	@Override
	public <T> Column addColumn(Caster<T> clazz, String id) {
		return addColumn(clazz.getCastToClass(), id);
	}

	@Override
	public boolean onModify() {
		return false;
	}

}
