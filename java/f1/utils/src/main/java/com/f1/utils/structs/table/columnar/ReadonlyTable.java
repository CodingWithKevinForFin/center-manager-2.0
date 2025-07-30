package com.f1.utils.structs.table.columnar;

import java.util.ArrayList;
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
import com.f1.utils.CH;

public class ReadonlyTable implements Table {

	private Table inner;

	private List<Column> columns = new ArrayList<Column>();
	private Map<String, Column> columnsByName = new HashMap<String, Column>();

	private ReadonlyTableList tableList;

	public ReadonlyTable(Table inner) {
		this.inner = inner;
		this.tableList = new ReadonlyTableList(inner.getRows(), this);
		rebuildColumns();
	}

	private void rebuildColumns() {
		this.columns.clear();
		this.columnsByName.clear();
		for (Column c : inner.getColumns()) {
			ReadonlyColumn c2 = new ReadonlyColumn(c, this);
			this.columns.add(c2);
			this.columnsByName.put(c2.getId(), c2);
		}
	}

	@Override
	public List<Column> getColumns() {
		return columns;
	}

	@Override
	public Map<String, Column> getColumnsMap() {
		return columnsByName;
	}

	@Override
	public Column getColumnAt(int location) {
		return columns.get(location);
	}

	@Override
	public Column getColumn(String id) {
		return CH.getOrThrow(this.columnsByName, id);
	}

	@Override
	public Set<String> getColumnIds() {
		return columnsByName.keySet();
	}

	@Override
	public TableList getRows() {
		return this.tableList;
	}

	@Override
	public NameSpaceCalcTypes getColumnTypesMapping() {
		return inner.getColumnTypesMapping();
	}

	@Override
	public int getColumnsCount() {
		return inner.getColumnsCount();
	}

	@Override
	public Row newRow(Object... values) {
		throw readonly();
	}

	@Override
	public Object getAt(int row, int column) {
		return inner.getAt(row, column);
	}

	@Override
	public Row newEmptyRow() {
		throw readonly();
	}

	@Override
	public Object get(int row, String column) {
		return inner.get(row, column);
	}

	@Override
	public boolean removeRow(Row removed) {
		throw readonly();
	}

	@Override
	public <C> C getAt(int row, int column, Class<C> c) {
		return inner.getAt(row, column, c);
	}

	@Override
	public void fireCellChanged(Row basicRow, int i, Object old, Object value) {
		throw readonly();
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
	public StringBuilder toString(StringBuilder sink) {
		return inner.toString(sink);
	}

	@Override
	public <C> C get(int row, String column, Caster<C> c) {
		return inner.get(row, column, c);
	}

	@Override
	public Object setAt(int row, int column, Object value) {
		throw readonly();
	}

	@Override
	public Object set(int row, String column, Object value) {
		throw readonly();
	}

	@Override
	public <T> Column addColumn(int location, Class<T> clazz, String id, T defaultValue) {
		throw readonly();
	}

	@Override
	public <T> Column addColumn(Class<T> clazz, String id, T defaultValue) {
		throw readonly();
	}

	@Override
	public <T> Column addColumn(Class<T> clazz, String id) {
		throw readonly();
	}

	@Override
	public void renameColumn(String oldId, String newId) {
		throw readonly();
	}

	@Override
	public void renameColumn(int location, String newId) {
		throw readonly();
	}

	@Override
	public <F, T> void setColumnType(int location, Class<F> fromType, Class<T> type, Getter<? super F, ? extends T> caster) {
		throw readonly();
	}

	@Override
	public void removeColumn(int location) {
		throw readonly();
	}

	@Override
	public void removeColumn(String id) {
		throw readonly();
	}

	@Override
	public String getTitle() {
		return inner.getTitle();
	}

	@Override
	public void setTitle(String title) {
		throw readonly();
	}

	@Override
	public void clear() {
		throw readonly();
	}

	@Override
	public int getSize() {
		return inner.getSize();
	}

	@Override
	public int newRows(int count) {
		throw readonly();
	}

	private RuntimeException readonly() {
		return new RuntimeException("Readonly Table: " + getTitle());
	}

	public Table getInner() {
		return this.inner;
	}

	@Override
	public void removeRow(int position) {
		throw readonly();

	}

	@Override
	public Row getRow(int position) {
		return inner.getRow(position);
	}

	@Override
	public <T> Column addColumn(int location, Caster<T> clazz, String id, T defaultValue) {
		throw readonly();
	}

	@Override
	public <T> Column addColumn(Caster<T> clazz, String id) {
		throw readonly();
	}

	@Override
	public boolean onModify() {
		throw readonly();
	}
}
