/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Getter;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.base.TableListenable;
import com.f1.base.TableListener;
import com.f1.base.ToStringable;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.DetailedException;
import com.f1.utils.OH;
import com.f1.utils.TableHelper;

public class BasicTable implements TableListenable, ToStringable {
	private static final int DEFAULT_LENGTH = 10;
	private static final TableListener[] EMPTY = new TableListener[0];
	final private Map<String, Column> columnsMap;
	final private List<Column> columns;
	protected int uid = 1;
	private TableList rows;
	public boolean hasListeners = false;
	private TableListener[] tableListeners = EMPTY;
	private String title;

	public BasicTable(Class col1Class, String col1Id, Object... moreColumns) {
		rows = initRows(DEFAULT_LENGTH);
		int colsCount = moreColumns.length / 2 + 1;
		columns = new ArrayList<Column>(colsCount);
		this.columnsMap = new HashMap<String, Column>(colsCount);

		Class<?>[] columnTypes = new Class<?>[colsCount];
		String[] columnIds = new String[colsCount];
		columnTypes[0] = col1Class;
		columnIds[0] = col1Id;
		if (moreColumns.length % 2 == 1)
			throw new IndexOutOfBoundsException("expecting even number of objects (class/id pairs): " + Arrays.toString(moreColumns));
		for (int i = 1; i < colsCount; i++) {
			try {
				columnTypes[i] = (Class<?>) moreColumns[(i - 1) * 2];
			} catch (Exception e) {
				throw new ClassCastException("expecting class type at " + (i - 1) * 2);
			}
			columnIds[i] = (String) moreColumns[1 + (i - 1) * 2];
		}
		init(columnTypes, columnIds);
	}

	public BasicTable(Class<?>[] colTypes, String[] columnIds) {
		this(colTypes, columnIds, DEFAULT_LENGTH);
	}
	public BasicTable(String[] columnIds) {
		this(null, columnIds, DEFAULT_LENGTH);
	}

	public BasicTable() {
		this(OH.EMPTY_STRING_ARRAY);
	}
	public BasicTable(Column[] columns) {
		this(columns, DEFAULT_LENGTH);
	}
	public BasicTable(Column[] columns, int size) {
		this.rows = initRows(size);
		this.columns = new ArrayList<Column>(columns.length);
		this.columnsMap = new HashMap<String, Column>(columns.length);
		Class<?>[] colTypes = new Class[columns.length];
		String[] columnIds = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			colTypes[i] = columns[i].getType();
			columnIds[i] = columns[i].getId();
		}
		init(colTypes, columnIds);
	}
	public BasicTable(List<Column> columns) {
		this(columns, DEFAULT_LENGTH);
	}
	public BasicTable(List<Column> columns, int size) {
		this.rows = initRows(size);
		this.columns = new ArrayList<Column>(columns.size());
		this.columnsMap = new HashMap<String, Column>(columns.size());
		Class<?>[] colTypes = new Class[columns.size()];
		String[] columnIds = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			colTypes[i] = columns.get(i).getType();
			columnIds[i] = columns.get(i).getId();
		}
		init(colTypes, columnIds);
	}

	protected TableList initRows(int defaultLength) {
		return new BasicTableList(this, DEFAULT_LENGTH);
	}

	public BasicTable(Class<?>[] colTypes, String[] columnIds, int size) {
		if (colTypes == null) {
			colTypes = new Class[columnIds.length];
			for (int i = 0; i < columnIds.length; i++)
				colTypes[i] = Object.class;
		}
		rows = initRows(size);
		columns = new ArrayList<Column>(colTypes.length);
		this.columnsMap = new HashMap<String, Column>(colTypes.length);
		init(colTypes, columnIds);
	}

	protected void init(final Class<?>[] colTypes, String columnIds[]) {
		OH.assertEq(colTypes.length, columnIds.length);
		for (int i = 0, l = columnIds.length; i < l; i++) {
			final String c = columnIds[i];
			if (colTypes[i] == null)
				throw new NullPointerException("class at index: " + i);
			if (c == null)
				throw new IllegalArgumentException("id is null at index: " + i);
			BasicColumn col = new BasicColumn(this, uid++, i, colTypes[i], c);
			CH.putOrThrow(columnsMap, col.getId(), col, "duplicate column name in table");
			columns.add(col);
		}
	}

	public BasicTable(Table source) {
		rows = initRows(source.getSize());
		List<Column> cols = source.getColumns();
		setTitle(source.getTitle());
		Class<?>[] colTypes = new Class<?>[cols.size()];
		String[] columnIds = new String[cols.size()];
		for (int i = 0; i < cols.size(); i++) {
			colTypes[i] = cols.get(i).getType();
			columnIds[i] = cols.get(i).getId();
		}
		this.columnsMap = new HashMap<String, Column>(colTypes.length);
		this.columns = new ArrayList<Column>(source.getColumns().size());
		init(colTypes, columnIds);
		int size = source.getSize();
		for (int i = 0; i < size; i++) {
			Row row = newEmptyRow();
			Object[] values = row.getValues();
			for (int col = 0; col < values.length; col++)
				values[col] = source.getAt(i, col);
			rows.add(row);
		}
	}

	@Override
	public List<Column> getColumns() {
		return columns;
	}

	@Override
	public Map<String, Column> getColumnsMap() {
		return columnsMap;
	}

	@Override
	public Column getColumnAt(int location) {
		return columns.get(location);
	}

	@Override
	public Column getColumn(String id) {
		return CH.getOrThrow(columnsMap, id);
	}

	@Override
	public int getColumnsCount() {
		return columns.size();
	}

	@Override
	public Object getAt(int row, int column) {
		return rows.get(row).getAt(column);
	}

	@Override
	public Object get(int row, String column) {
		return rows.get(row).getAt(getColumn(column).getLocation());
	}

	@Override
	public <T> Column addColumn(int location, Class<T> clazz, String id, T defaultValue) {
		OH.assertBetween(location, 0, getColumnsCount());
		BasicColumn col = new BasicColumn(this, uid++, location, clazz, id);
		addColumn(location, col, defaultValue);
		return col;
	}
	@Override
	public <T> Column addColumn(int location, Caster<T> clazz, String id, T defaultValue) {
		OH.assertBetween(location, 0, getColumnsCount());
		BasicColumn col = new BasicColumn(this, uid++, location, clazz, id);
		addColumn(location, col, defaultValue);
		return col;
	}
	protected void addColumn(int location, BasicColumn col, Object defaultValue) {
		CH.putOrThrow(columnsMap, col.getId(), col, "Duplicate column name");
		columns.add(location, col);
		for (int i = location + 1; i < columns.size(); i++)
			((BasicColumn) this.columns.get(i)).setLocation(i);
		for (int i = 0, l = getSize(); i < l; i++) {
			BasicRow row = (BasicRow) rows.get(i);
			row.setValues(AH.insert(row.getValues(), location, defaultValue));

		}
		if (hasListeners) {
			for (TableListener listener : this.tableListeners)
				listener.onColumnAdded(col);
		}
	}

	@Override
	public void renameColumn(int location, String newId) {
		Column old = getColumnAt(location);
		if (old.getId().equals(newId))
			return;
		BasicColumn c;
		columnsMap.remove(old.getId());
		c = new BasicColumn(this, uid++, location, old.getType(), newId);
		CH.putOrThrow(columnsMap, newId, c);
		columns.set(location, c);
		if (hasListeners)
			for (TableListener listener : this.tableListeners)
				listener.onColumnChanged(old, c);
	}

	@Override
	public <F, T> void setColumnType(int location, Class<F> fromType, Class<T> type, Getter<? super F, ? extends T> caster) {
		Column old = getColumnAt(location);
		if (OH.ne(old.getType(), fromType))
			throw new RuntimeException("bad from type: " + fromType + ", expecting" + old.getType());
		if (OH.eq(fromType, type))
			return;

		T[] values = (T[]) new Object[getSize()];
		int pos = 0;
		Caster<F> fcaster = OH.getCaster(fromType);
		for (Row row : this.getRows())
			values[pos++] = caster.get(row.getAt(location, fcaster));
		BasicColumn c;
		columnsMap.remove(old.getId());
		c = new BasicColumn(this, uid++, location, type, old.getId());
		CH.putOrThrow(columnsMap, old.getId(), c);
		columns.set(location, c);
		pos = 0;
		for (Row row : this.getRows())
			row.putAt(location, values[pos++]);
		if (hasListeners)
			for (TableListener listener : this.tableListeners)
				listener.onColumnChanged(old, c);
	}

	@Override
	public void removeColumn(int location) {
		Column col = columns.remove(location);
		columnsMap.remove(col.getId());
		for (int i = location; i < columns.size(); i++)
			((BasicColumn) columns.get(i)).setLocation(i);
		for (int i = 0, l = getSize(); i < l; i++) {
			Row row = (Row) rows.get(i);
			row.setValues(AH.remove(row.getValues(), location));
		}
		if (hasListeners)
			for (TableListener listener : this.tableListeners)
				listener.onColumnRemoved(col);
	}

	@Override
	final public void renameColumn(String oldId, String newId) {
		renameColumn(getColumn(oldId).getLocation(), newId);
	}

	@Override
	final public void removeColumn(String id) {
		removeColumn(getColumn(id).getLocation());
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public StringBuilder toString(StringBuilder sb) {
		return TableHelper.toString(this, "", TableHelper.SHOW_ALL, sb);
	}

	@Override
	public <C> C getAt(int row, int column, Class<C> c) {
		return OH.cast(getAt(row, column), c);
	}
	@Override
	public <C> C getAt(int row, int column, Caster<C> c) {
		return c.cast(getAt(row, column));
	}

	@Override
	public <C> C get(int row, String column, Class<C> c) {
		return OH.cast(get(row, column), c);
	}
	@Override
	public <C> C get(int row, String column, Caster<C> c) {
		return c.cast(get(row, column));
	}

	@Override
	public TableList getRows() {
		return rows;
	}

	@Override
	public int getSize() {
		return rows.size();
	}

	@Override
	public void addTableListener(TableListener listener) {
		if (AH.indexOfByIdentity(listener, this.tableListeners) != -1)
			throw new DetailedException("value already exists in list").set("supplied value", listener);
		this.tableListeners = AH.append(this.tableListeners, listener);
		hasListeners = true;
	}

	@Override
	public void removeTableListener(TableListener listener) {
		int i = AH.indexOfByIdentity(listener, this.tableListeners);
		if (i == -1)
			return;
		if (tableListeners.length == 1) {
			this.tableListeners = EMPTY;
			hasListeners = false;
		} else
			this.tableListeners = AH.remove(tableListeners, i);
	}

	@Override
	public Row newRow(Object... values) {
		if (values.length != getColumnsCount())
			throw new RuntimeException("Expecting " + getColumnsCount() + " cells not " + values.length);
		return new BasicRow(this, uid++, values);
	}

	@Override
	public Row newEmptyRow() {
		return new BasicRow(this, uid++, new Object[getColumnsCount()]);
	}
	public void fireCellChanged(Row basicRow, int col, Object oldValue, Object newValue) {
		if (hasListeners)
			for (TableListener tableListener : this.tableListeners)
				tableListener.onCell(basicRow, col, oldValue, newValue);
	}

	protected void fireRowAdd(Row row) {
		if (hasListeners)
			for (TableListener tableListener : this.tableListeners)
				tableListener.onRowAdded(row);
	}

	protected void fireRowRemove(Row row, int rowPosition) {
		if (hasListeners)
			for (TableListener tableListener : this.tableListeners)
				tableListener.onRowRemoved(row, rowPosition);
	}

	@Override
	public Object setAt(int row, int column, Object value) {
		return rows.get(row).putAt(column, value);
	}

	@Override
	public Object set(int row, String column, Object value) {
		return rows.get(row).put(column, value);
	}

	@Override
	final public <T> Column addColumn(Class<T> clazz, String id, T defaultValue) {
		return addColumn(getColumnsCount(), clazz, id, defaultValue);
	}

	@Override
	final public <T> Column addColumn(Class<T> clazz, String id) {
		return addColumn(getColumnsCount(), clazz, id, null);
	}
	@Override
	final public <T> Column addColumn(Caster<T> clazz, String id) {
		return addColumn(getColumnsCount(), clazz, id, null);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Set<String> getColumnIds() {
		return columnsMap.keySet();
	}

	@Override
	public boolean equals(Object table) {
		return table == this || (table != null && table.getClass() == getClass() && equals((BasicTable) table));
	}
	public boolean equals(BasicTable table) {
		if (table == null)
			return false;
		return table == this || (OH.eq(title, table.title) && columnsMap.equals(table.columnsMap) && getRows().equals(table.getRows()));
	}

	@Override
	public boolean removeRow(Row row) {
		return this.rows.remove(row);
	}

	@Override
	public void clear() {
		this.rows.clear();
	}

	@Override
	public int hashCode() {
		return OH.hashCode(columns, this.rows);
	}

	private NameSpaceCalcTypes tableTypeMapping;

	@Override
	public NameSpaceCalcTypes getColumnTypesMapping() {
		if (tableTypeMapping == null)
			this.tableTypeMapping = new TableTypeMapping(this);
		return this.tableTypeMapping;
	}

	@Override
	public int newRows(int count) {
		int r = getSize();
		while (count-- > 0)
			getRows().addRow(OH.EMPTY_OBJECT_ARRAY);
		return r;
	}

	@Override
	public void removeRow(int position) {
		rows.remove(position);
	}

	@Override
	public Row getRow(int position) {
		return rows.get(position);
	}

	@Override
	public boolean onModify() {
		return false;
	}

}
