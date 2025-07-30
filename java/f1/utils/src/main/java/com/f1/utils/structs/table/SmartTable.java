/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs.table;

import java.util.BitSet;
import java.util.Comparator;
import java.util.Set;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.base.TableListener;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public interface SmartTable extends TableListenable {
	public static byte STATUS_CELLS_CHANGED = 1;//data changed
	public static byte STATUS_ROWS_CHANGED = 2;//rows were added or removed, note that if this is set, CELLS_CHANGED bit can also be set
	public static byte STATUS_SCHEMA_CHANGED = 4;
	public static byte STATUS_SORT_CHANGED = 8;
	public static byte STATUS_FILTER_CHANGED = 16;

	byte ensureUpToDateAndGetStatus();
	public int[] getCellsChanged(int top, int bottom);
	public void resetStatus();

	public void setTableFilter(RowFilter filter);

	public RowFilter getTableFilter();

	public void sortRows(Comparator<Row> rowComparator, boolean keepSorting);

	public boolean getKeepSorting();

	public Iterable<Row> getFiltered();

	public void addTableListener(TableListener tableListener);

	public void removeTableListener(TableListener tableListener);

	void clear();

	public Column addDerivedColumn(String id, DerivedCellCalculator calc);

	public void setExternalFilterIndex(String columnId, Set<Object> values);

	public Object getExternalFilterIndexColumnId();
	public Set<Object> getExternalFilterIndexValues();

	public void redoRows();

	public void debugState();
	int getMinRowChanged();
	public BitSet getCellsChangedAtRow(int row);
	void setListenForCellsRowRange(int top, int bottom);
	void setListenForCellsColumns(boolean[] columns);

}
