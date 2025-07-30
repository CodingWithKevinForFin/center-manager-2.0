package com.f1.utils.structs.table.columnar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Getter;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableListenable;
import com.f1.base.TableListener;
import com.f1.base.ToStringable;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.IntArrayList;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.TableHelper;
import com.f1.utils.ToDoException;
import com.f1.utils.structs.table.TableTypeMapping;

final public class ColumnarTable implements ToStringable, TableListenable, ColumnsTable {

	private static final int MAX_GROWTH_PADDING = 10000000;
	private static final int DEFAULT_LENGTH = 10;
	final private Map<String, ColumnarColumn> columnsMap;
	ColumnarColumn[] columns;
	private List<ColumnarColumn> columnsList = null;
	private int columnsSize;
	protected int uid = 1;
	private ColumnarTableList rows;
	private List<TableListener> tableListeners = new ArrayList<TableListener>();
	private int tableListenersCount = 0;

	private String title;

	private int columnRowsCapacity = 10;
	private IntArrayList availableIndexes = new IntArrayList();
	private int nextAvailableIndex = 0;
	boolean isMangled;

	public ColumnarTable(Class col1Class, String col1Id, Object... moreColumns) {
		rows = initRows(DEFAULT_LENGTH);
		int colsCount = moreColumns.length / 2 + 1;
		columns = new ColumnarColumn[colsCount];
		this.columnsSize = 0;
		this.columnsMap = new HashMap<String, ColumnarColumn>(colsCount);

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

	public ColumnarTable(Class<?>[] colTypes, String[] columnIds) {
		this(colTypes, columnIds, DEFAULT_LENGTH);
	}
	public ColumnarTable(String[] columnIds) {
		this(null, columnIds, DEFAULT_LENGTH);
	}

	public ColumnarTable() {
		this(OH.EMPTY_STRING_ARRAY);
	}
	public ColumnarTable(Column[] columns) {
		this(columns, DEFAULT_LENGTH);
	}
	public ColumnarTable(Column[] columns, int size) {
		this.rows = initRows(size);
		this.columns = new ColumnarColumn[columns.length];
		this.columnsSize = 0;
		this.columnsMap = new HashMap<String, ColumnarColumn>(columns.length);
		Class<?>[] colTypes = new Class[columns.length];
		String[] columnIds = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			colTypes[i] = columns[i].getType();
			columnIds[i] = columns[i].getId();
		}
		init(colTypes, columnIds);
	}
	public ColumnarTable(List<Column> columns) {
		this(columns, DEFAULT_LENGTH);
	}
	public ColumnarTable(List<Column> columns, int size) {
		this.rows = initRows(size);
		this.columns = new ColumnarColumn[columns.size()];
		this.columnsSize = 0;
		this.columnsMap = new HashMap<String, ColumnarColumn>(columns.size());
		Class<?>[] colTypes = new Class[columns.size()];
		String[] columnIds = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			colTypes[i] = columns.get(i).getType();
			columnIds[i] = columns.get(i).getId();
		}
		init(colTypes, columnIds);
	}

	protected ColumnarTableList initRows(int size) {
		this.setRowsCapacity(size);
		return new ColumnarTableList(this, size);
	}
	public ColumnarTable(Class<?>[] colTypes, String[] columnIds, int size) {
		if (colTypes == null) {
			colTypes = new Class[columnIds.length];
			for (int i = 0; i < columnIds.length; i++)
				colTypes[i] = Object.class;
		}
		this.columns = new ColumnarColumn[colTypes.length];
		this.columnsSize = 0;
		this.columnsMap = new HashMap<String, ColumnarColumn>(colTypes.length);
		rows = initRows(size);
		init(colTypes, columnIds);
	}

	protected void init(final Class<?>[] colTypes, String columnIds[]) {
		OH.assertEq(colTypes.length, columnIds.length);
		this.columnsList = null;
		if (columnsSize + columnIds.length > this.columns.length)
			columns = Arrays.copyOf(columns, columnsSize + columnIds.length);
		for (int i = 0, l = columnIds.length; i < l; i++) {
			final String c = columnIds[i];
			if (colTypes[i] == null)
				throw new NullPointerException("class at index: " + i);
			if (c == null)
				throw new IllegalArgumentException("id is null at index: " + i);
			ColumnarColumn col = newColumnarColumnObject(this, colTypes[i], c, this.columnRowsCapacity, true);
			col.setLocation(i);
			CH.putOrThrow(columnsMap, col.getId(), col, "duplicate column name in table");
			columns[columnsSize++] = col;
		}
	}
	public ColumnarTable(Table source) {
		this(source, 0, source.getSize());
	}
	public ColumnarTable(Table source, int rowStart, int rowEnd) {
		int length = rowEnd - rowStart;
		rows = initRows(length);
		List<Column> cols = source.getColumns();
		setTitle(source.getTitle());
		Class<?>[] colTypes = new Class<?>[cols.size()];
		String[] columnIds = new String[cols.size()];
		for (int i = 0; i < cols.size(); i++) {
			colTypes[i] = cols.get(i).getType();
			columnIds[i] = cols.get(i).getId();
		}
		this.columnsMap = new HashMap<String, ColumnarColumn>(colTypes.length);
		this.columns = new ColumnarColumn[source.getColumnsCount()];
		this.columnsSize = 0;
		init(colTypes, columnIds);
		while (source instanceof ReadonlyTable)
			source = ((ReadonlyTable) source).getInner();
		if (source instanceof ColumnarTable) {
			ColumnarTable source2 = (ColumnarTable) source;
			if (source2.isMangled) {
				for (Row row : source.getRows())
					this.rows.addRow(row.getValuesCloned());
			} else {
				int columnsCount = this.getColumnsCount();
				for (int i = rowStart; i < rowEnd; i++) {
					this.rows.add(newEmptyRow());
				}
				if (rowStart == 0 && rowEnd == source.getSize()) {
					for (int i = 0; i < columnsCount; i++) {
						ColumnarColumn<?> src = source2.getColumnAt(i);
						long[] nulls = src.getValueNullsMasks();
						this.columns[i].setValuesAndNulls(src.getValuesCloned(), nulls == null ? null : nulls.clone());
					}
				} else {
					for (int i = 0; i < columnsCount; i++) {
						ColumnarColumn<?> src = source2.getColumnAt(i);
						long[] nulls = src.getValueNullsMasks();
						Object values = AH.arraycopy(src.getValues(), rowStart, length);
						long nulls2[];
						if (nulls != null) {
							nulls2 = new long[(length + 7) / 8];
							for (int n = 0; n < length; n++) {
								boolean isNull = ColumnarColumnPrimitive.isNullAtArrayIndex(nulls, n + rowStart);
								ColumnarColumnPrimitive.setNullAtArrayIndex(nulls2, n, isNull);
							}
						} else
							nulls2 = null;
						this.columns[i].setValuesAndNulls(values, nulls2);
					}
				}
			}
		} else {
			for (int i = rowStart; i < rowEnd; i++)
				this.rows.addRow(source.getRow(i).getValues());
		}
	}

	@Override
	public List<Column> getColumns() {
		if (this.columnsList == null) {
			ArrayList<ColumnarColumn> t = new ArrayList<ColumnarColumn>(this.columnsSize);
			for (int i = 0; i < this.columnsSize; i++)
				t.add(this.columns[i]);
			this.columnsList = t;
		}
		return (List) columnsList;
	}

	@Override
	public Map<String, Column> getColumnsMap() {
		return (Map) columnsMap;
	}

	@Override
	public ColumnarColumn getColumnAt(int location) {
		return columns[location];
	}

	@Override
	public ColumnarColumn getColumn(String id) {
		return CH.getOrThrow(columnsMap, id, "column not found");
	}

	@Override
	public int getColumnsCount() {
		return columnsSize;
	}

	@Override
	public Object getAt(int row, int column) {
		return columns[column].getValue(row);
	}

	@Override
	public Object get(int row, String column) {
		return getColumn(column).getValue(row);
	}

	@Override
	public <T> ColumnarColumn<T> addColumn(int location, Class<T> clazz, String id, T defaultValue) {
		return this.addColumn(location, clazz, id, defaultValue, true);
	}
	@Override
	public <T> ColumnarColumn<T> addColumn(int location, Caster<T> clazz, String id, T defaultValue) {
		return this.addColumn(location, clazz.getCastToClass(), id, defaultValue, true);
	}
	@Override
	public <T> ColumnarColumn<T> addColumn(Caster<T> clazz, String id) {
		return this.addColumn(clazz.getCastToClass(), id);
	}
	public <T> ColumnarColumn<T> addColumn(int location, Class<T> clazz, String id, T defaultValue, boolean allowNull) {
		ColumnarColumn<T> col = newColumnarColumnObject(this, clazz, id, this.columnRowsCapacity, allowNull);
		T val = col.getTypeCaster().cast(defaultValue, false, false);
		if (val != null) {
			if (isMangled)
				for (int i = 0; i < this.getSize(); i++)
					col.setValueAtArrayIndex(getRow(i).getArrayIndex(), val);
			else
				for (int i = 0; i < this.getSize(); i++)
					col.setValueAtArrayIndex(i, val);
		}
		addColumn(location, col);
		return col;
	}
	public <T extends ColumnarColumn> T addColumn(T col) {
		return addColumn(this.getColumnsCount(), col);
	}
	public <T extends ColumnarColumn> T addColumn(int location, T col) {
		OH.assertBetween(location, 0, getColumnsCount());
		OH.assertEqIdentity(col.getTable(), this);
		CH.putOrThrow(columnsMap, col.getId(), col);
		this.columns = AH.insert(this.columns, location, col);
		this.columnsSize++;
		this.columnsList = null;
		col.ensureCapacity(this.getRowsCapacity());
		for (int i = location; i < columnsSize; i++)
			this.columns[i].setLocation(i);
		if (tableListenersCount > 0)
			for (int i = 0; i < tableListenersCount; i++)
				tableListeners.get(i).onColumnAdded(col);
		return col;
	}

	@Override
	public void renameColumn(int location, String newId) {
		ColumnarColumn old = getColumnAt(location);
		Object tmp = old.getValues();
		long[] nulls = old.getValueNullsMasks();
		if (old.getId().equals(newId))
			return;
		ColumnarColumn c;
		columnsMap.remove(old.getId());
		c = newColumnarColumnObject(this, old.getType(), newId, this.columnRowsCapacity, old.getAllowNull());
		c.setLocation(location);
		CH.putOrThrow(columnsMap, newId, c);
		if (this.columnsList == null) {
			getColumns();
		}
		columnsList.set(location, c);
		columns[location] = c;
		c.setValues(tmp, nulls);
		if (tableListenersCount > 0)
			for (int i = 0; i < tableListenersCount; i++)
				tableListeners.get(i).onColumnChanged(old, c);
	}
	@Override
	public void removeColumn(int location) {
		Column col = this.columns[location];
		this.columns = AH.remove(this.columns, location);
		this.columnsSize--;
		this.columnsList = null;
		columnsMap.remove(col.getId());
		for (int i = location; i < columnsSize; i++)
			((ColumnarColumn) columns[i]).setLocation(i);
		if (tableListenersCount > 0)
			for (int i = 0; i < tableListenersCount; i++)
				tableListeners.get(i).onColumnRemoved(col);
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
	public ColumnarTableList getRows() {
		return rows;
	}

	@Override
	public int getSize() {
		return rows.size();
	}

	@Override
	public void addTableListener(TableListener listener) {
		CH.addIdentityOrThrow(tableListeners, listener);
		this.tableListenersCount = this.tableListeners.size();
	}

	@Override
	public void removeTableListener(TableListener listener) {
		tableListeners.remove(listener);
		this.tableListenersCount = this.tableListeners.size();
	}

	@Override
	public ColumnarRow newRow(Object... values) {
		OH.assertEq(getColumnsCount(), values.length);
		ColumnarRow r = newEmptyRow();
		int location = r.getLocation();
		for (int i = 0; i < values.length; i++)
			setAt(location, i, values[i]);
		return r;
	}

	@Override
	public ColumnarRow newEmptyRow() {
		final int t = this.availableIndexes.size();
		final int index;
		if (t > 0)
			index = this.availableIndexes.removeAt(t - 1);
		else {
			index = this.nextAvailableIndex++;
			if (index >= this.columnRowsCapacity)
				setRowsCapacity(MH.getArrayGrowth(this.columnRowsCapacity, index + 1));
		}
		if (this.rowFactory != null)
			return this.rowFactory.newColumnarRow(this, uid++, index);
		return new ColumnarRow(this, uid++, index);
	}

	private void setRowsCapacity(int i) {
		if (this.columnsSize > 0)
			for (int n = 0; n < this.columnsSize; n++)
				this.columns[n].ensureCapacity(i);
		this.columnRowsCapacity = i;
	}

	public void fireCellChanged(Row basicRow, int col, Object oldValue, Object newValue) {
		if (tableListenersCount > 0)
			for (int i = 0; i < tableListenersCount; i++)
				tableListeners.get(i).onCell(basicRow, col, oldValue, newValue);
	}
	public void fireCellChangedAtIndex(int rowNum, int col, Object oldValue, Object newValue) {
		if (tableListenersCount > 0)
			for (int i = 0; i < tableListenersCount; i++) {
				tableListeners.get(i).onCell(this.getRows().get(rowNum), col, oldValue, newValue);
			}
	}

	protected void fireRowAdd(Row row) {

		if (tableListenersCount > 0)
			for (int i = 0; i < tableListenersCount; i++)
				tableListeners.get(i).onRowAdded(row);
	}

	protected void fireRowRemove(Row row, int location) {
		if (tableListenersCount > 0)
			for (int i = 0; i < tableListenersCount; i++)
				tableListeners.get(i).onRowRemoved(row, location);
	}

	@Override
	public Object setAt(int row, int column, Object value) {
		return columns[column].setValueAtArrayIndex(mapRowNumToIndex(row), value);
	}

	@Override
	public Object set(int row, String column, Object value) {
		return setAt(row, columnsMap.get(column).getLocation(), value);
	}

	@Override
	final public <T> ColumnarColumn<T> addColumn(Class<T> clazz, String id, T defaultValue) {
		return addColumn(getColumnsCount(), clazz, id, defaultValue);
	}

	final public <T> ColumnarColumn<T> addColumn(Class<T> clazz, String id, T defaultValue, boolean allowNull) {
		return addColumn(getColumnsCount(), clazz, id, defaultValue, allowNull);
	}
	final public ColumnarColumnEnum addColumnEnum(String id, String defaultValue, boolean allowNull, ColumnarColumnEnumMapper mapper) {
		int location = getColumnsCount();
		ColumnarColumnEnum col = new ColumnarColumnEnum(mapper, this, location, id, columnRowsCapacity, allowNull);
		addColumn(location, col);
		return col;
	}
	final public ColumnarColumnEnum addColumnEnum(int location, String id, String defaultValue, boolean allowNull, ColumnarColumnEnumMapper mapper) {
		ColumnarColumnEnum col = new ColumnarColumnEnum(mapper, this, location, id, columnRowsCapacity, allowNull);
		addColumn(location, col);
		return col;
	}

	@Override
	final public <T> ColumnarColumn<T> addColumn(Class<T> clazz, String id) {
		return addColumn(getColumnsCount(), clazz, id, null);
	}
	public <T> ColumnarColumn<T> addColumnWithValues(Class<T> type, String id, Object valuesArray, long[] nullsMask, boolean allowNull) {
		return addColumnWithValues(type, id, valuesArray, nullsMask, allowNull, getColumnsCount());
	}
	public <T> ColumnarColumn<T> addColumnWithValues(Class<T> type, String id, Object valuesArray, long[] nullsMask, boolean allowNull, int colpos) {
		int len;
		if (valuesArray != null) {
			if (!valuesArray.getClass().isArray())
				throw new ClassCastException("Not an array: " + valuesArray);
			len = Array.getLength(valuesArray);
			if (this.columnsSize != 0) {
				if (len != getSize())
					throw new IndexOutOfBoundsException("Array length mismatch, expecting " + getSize() + " value(s), not " + len);
			} else
				newRows(len);
		} else
			len = 0;
		ColumnarColumn<T> col = newColumnarColumnObject(this, type, id, len, allowNull);
		if (len > 0) {
			if (isMangled)
				throw new ToDoException("Handle inserts of mangled tables");
			this.columnRowsCapacity = len;
			col.setValues(valuesArray, nullsMask);
		}
		addColumn(colpos, col);
		return col;
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
		return table == this || (table != null && table.getClass() == getClass() && equals((ColumnarTable) table));
	}
	public boolean equals(ColumnarTable table) {
		if (table == null)
			return false;
		return table == this || (OH.eq(title, table.title) && columnsMap.equals(table.columnsMap) && getRows().equals(table.getRows()));
	}

	@Override
	public boolean removeRow(Row row) {
		if (row.getTable() != this)
			return false;

		int location = row.getLocation();
		if (location == -1)
			return onRowRemoved(row, location) != null;
		else
			getRows().remove(location);
		return true;
	}
	public void removeRow(int location) {
		getRows().remove(location);

	}

	@Override
	public void clear() {
		for (ColumnarColumn c : this.columns)
			c.clearData();
		if (this.tableListenersCount > 0) {
			for (int i = 0, l = this.rows.size(); i < l; i++)
				fireRowRemove(this.rows.get(i), i);
		}
		this.rows.clear();
		nextAvailableIndex = 0;
		this.availableIndexes.clear();
		this.isMangled = false;
	}
	@Override
	public int hashCode() {
		return OH.hashCode(columns, this.rows);
	}

	@Override
	public int mapRowNumToIndex(int location) {
		if (!isMangled)
			return location;
		return ((ColumnarRow) this.rows.get(location)).getArrayIndex();
	}

	@Override
	public int mapRowNumToIndex(ColumnarRow location) {
		return location.getArrayIndex();
	}

	protected Row onRowRemoved(Row row, int location) {
		OH.assertEqIdentity(row.getTable(), this);
		if (this.tableListenersCount > 0)
			for (int i = 0; i < tableListenersCount; i++)
				tableListeners.get(i).onRowRemoved(row, location);
		if (row != null) {
			ColumnarRow r = (ColumnarRow) row;
			int index = r.getArrayIndex();
			if (!this.isMangled && index == this.nextAvailableIndex - 1) {
				this.nextAvailableIndex--;
			} else {
				this.isMangled = true;
				this.availableIndexes.add(index);
			}
			for (ColumnarColumn c : this.columns)
				if (c.allowNull)
					c.setNullAtArrayIndex(index);
			r.setArrayIndex(-1);
		}

		return row;
	}
	public static <T> ColumnarColumn<T> newColumnarColumnObject(ColumnarTable table, Class<T> type, String id, int capacity, boolean allowNull) {
		if (type == Double.class || type == double.class)
			return (ColumnarColumn<T>) new ColumnarColumnDouble(table, 0, id, capacity, allowNull);
		else if (type == Float.class || type == float.class)
			return (ColumnarColumn<T>) new ColumnarColumnFloat(table, 0, id, capacity, allowNull);
		else if (type == Long.class || type == long.class)
			return (ColumnarColumn<T>) new ColumnarColumnLong(table, 0, id, capacity, allowNull);
		else if (type == Integer.class || type == int.class)
			return (ColumnarColumn<T>) new ColumnarColumnInt(table, 0, id, capacity, allowNull);
		else if (type == Short.class || type == short.class)
			return (ColumnarColumn<T>) new ColumnarColumnShort(table, 0, id, capacity, allowNull);
		else if (type == Byte.class || type == byte.class)
			return (ColumnarColumn<T>) new ColumnarColumnByte(table, 0, id, capacity, allowNull);
		else if (type == Boolean.class || type == boolean.class)
			return (ColumnarColumn<T>) new ColumnarColumnBoolean(table, 0, id, capacity, allowNull);
		else if (type == Character.class || type == char.class)
			return (ColumnarColumn<T>) new ColumnarColumnChar(table, 0, id, capacity, allowNull);
		else if (type == DateNanos.class)
			return (ColumnarColumn<T>) new ColumnarColumnDateNanos(table, 0, id, capacity, allowNull);
		else if (type == DateMillis.class)
			return (ColumnarColumn<T>) new ColumnarColumnDateMillis(table, 0, id, capacity, allowNull);
		else
			return new ColumnarColumnObject<T>(table, 0, type, id, capacity, allowNull);
	}

	protected int getRowsCapacity() {
		return this.columnRowsCapacity;
	}

	private NameSpaceCalcTypes tableTypeMapping;
	private ColumnarRowFactory rowFactory;

	@Override
	public NameSpaceCalcTypes getColumnTypesMapping() {
		if (tableTypeMapping == null)
			this.tableTypeMapping = new TableTypeMapping(this);
		return this.tableTypeMapping;
	}

	@Override
	public <F, T> void setColumnType(int location, Class<F> fromType, Class<T> type, Getter<? super F, ? extends T> caster) {//TODO: allow null?
		if (fromType == type)
			return;
		final ColumnarColumn<?> col = getColumnAt(location);
		final String currentId = col.getId();
		final ColumnarColumn<T> newCol = newColumnarColumnObject(this, type, currentId, this.columnRowsCapacity, col.getAllowNull());
		removeColumn(location);
		addColumn(location, newCol);
		Caster<T> caster2 = newCol.getTypeCaster();
		for (int i = 0, size = this.getSize(); i < size; i++)
			if (!col.isNullAtArrayIndex(i))
				newCol.setValueAtArrayIndex(i, caster2.castNoThrow(col.getValueAtArrayIndex(i)));
	}

	public void setRowFactory(ColumnarRowFactory rowFactory) {
		this.rowFactory = rowFactory;
	}

	public ColumnarRowFactory getRowFactory() {
		return this.rowFactory;
	}

	public void ensureCapacity(int initialCapacity) {
		if (this.columnRowsCapacity < initialCapacity) {
			setRowsCapacity(MH.getArrayGrowth(this.columnRowsCapacity, initialCapacity));
		}
	}

	public int newRows(int count) {
		int r = getSize();
		for (int i = 0; i < count; i++)
			getRows().addRow(OH.EMPTY_OBJECT_ARRAY);
		return r;
	}

	public ColumnarRow getRow(int i) {
		return (ColumnarRow) this.rows.get(i);
	}

	public boolean isMangled() {
		return this.isMangled;
	}

	@Override
	public boolean onModify() {
		return false;
	}

}
