/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 2-d data structure where the one dimention is {@link Column} and the other dimension is {@link Row}s. Columns are typed and h
 */
public interface Table extends ToStringable {

	public List<Column> getColumns();

	public Map<String, Column> getColumnsMap();

	public Column getColumnAt(int location);

	public Column getColumn(String id);

	public Set<String> getColumnIds();

	public int getColumnsCount();

	public Object getAt(int row, int column);

	public Object get(int row, String column);

	public <C> C getAt(int row, int column, Class<C> c);
	public <C> C getAt(int row, int column, Caster<C> c);

	public <C> C get(int row, String column, Class<C> c);
	public <C> C get(int row, String column, Caster<C> c);

	public Object setAt(int row, int column, Object value);

	public Object set(int row, String column, Object value);

	public <T> Column addColumn(int location, Class<T> clazz, String id, T defaultValue);
	public <T> Column addColumn(int location, Caster<T> clazz, String id, T defaultValue);

	public <T> Column addColumn(Class<T> clazz, String id, T defaultValue);

	public <T> Column addColumn(Class<T> clazz, String id);
	public <T> Column addColumn(Caster<T> clazz, String id);

	public void renameColumn(String oldId, String newId);

	public void renameColumn(int location, String newId);

	public <F, T> void setColumnType(int location, Class<F> fromType, Class<T> type, Getter<? super F, ? extends T> caster);

	public void removeColumn(int location);

	public void removeColumn(String id);

	public String getTitle();

	public void setTitle(String title);

	public void clear();

	public NameSpaceCalcTypes getColumnTypesMapping();

	public int getSize();

	int newRows(int count);
	public TableList getRows();

	public Row newRow(Object... values);

	public Row newEmptyRow();
	public boolean removeRow(Row removed);
	public void removeRow(int position);
	public Row getRow(int position);

	public void fireCellChanged(Row basicRow, int i, Object old, Object value);
	public boolean onModify();//returns true if calling this caused internals to change
}
