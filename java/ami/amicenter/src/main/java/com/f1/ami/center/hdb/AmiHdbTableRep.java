package com.f1.ami.center.hdb;

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

public class AmiHdbTableRep implements Table {

	private AmiHdbTable inner;

	public AmiHdbTableRep(AmiHdbTable inner) {
		this.inner = inner;
	}

	@Override
	public List<Column> getColumns() {
		return (List) inner.getColumns().valueList();
	}

	@Override
	public Map<String, Column> getColumnsMap() {
		return (Map) inner.getColumns().map();
	}

	@Override
	public Column getColumnAt(int location) {
		return inner.getColumns().getAt(location);
	}

	@Override
	public Column getColumn(String id) {
		return inner.getColumns().get((String) id);
	}

	@Override
	public Set<String> getColumnIds() {
		return inner.getColumns().keySet();
	}

	@Override
	public int getColumnsCount() {
		return inner.getColumns().getSize();
	}

	@Override
	public Object getAt(int row, int column) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(int row, String column) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C> C getAt(int row, int column, Class<C> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C> C getAt(int row, int column, Caster<C> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C> C get(int row, String column, Class<C> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C> C get(int row, String column, Caster<C> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object setAt(int row, int column, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object set(int row, String column, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Column addColumn(int location, Class<T> clazz, String id, T defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Column addColumn(Class<T> clazz, String id, T defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Column addColumn(Class<T> clazz, String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void renameColumn(String oldId, String newId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void renameColumn(int location, String newId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <F, T> void setColumnType(int location, Class<F> fromType, Class<T> type, Getter<? super F, ? extends T> caster) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeColumn(int location) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeColumn(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTitle() {
		return inner.getName();
	}

	@Override
	public void setTitle(String title) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		this.getHistoricalTable().clearRows();
	}

	@Override
	public NameSpaceCalcTypes getColumnTypesMapping() {
		return inner.getColumnTypes();
	}

	@Override
	public int getSize() {
		long r = inner.getRowsCountThreadSafe();
		return r > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) r;
	}

	@Override
	public int newRows(int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append("AMI HISTORICAL TABLE ").append(getTitle());
	}

	@Override
	public TableList getRows() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Row newRow(Object... values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Row newEmptyRow() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeRow(Row removed) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fireCellChanged(Row basicRow, int i, Object old, Object value) {
		throw new UnsupportedOperationException();
	}

	protected AmiHdbTable getHistoricalTable() {
		return this.inner;
	}

	@Override
	public void removeRow(int position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Row getRow(int position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Column addColumn(int location, Caster<T> clazz, String id, T defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Column addColumn(Caster<T> clazz, String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onModify() {
		return false;
	}

}
