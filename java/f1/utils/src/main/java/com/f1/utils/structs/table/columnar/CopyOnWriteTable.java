package com.f1.utils.structs.table.columnar;

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

/**
 * A wrapper class for a Table. It holds the inner table as a reference but will turn it into a copied table BEFORE modification occurs. Will return underlying Rows and Columns.
 * 
 * You should use the inner class for any instanceof checks.
 *
 * Columns methods here are used by amiscript
 */
public class CopyOnWriteTable implements Table {

	private Table inner;
	private boolean copied = false;

	public CopyOnWriteTable(Table inner) {
		// reference
		if (inner instanceof CopyOnWriteTable) // rewrap inner in new COWT, otherwise we would be modifying upstream data
			this.inner = ((CopyOnWriteTable) inner).getInner();
		else
			this.inner = inner;
	}

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
	public Set<String> getColumnIds() {
		return inner.getColumnIds();
	}

	@Override
	public TableList getRows() {
		return inner.getRows();
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
		onModify();
		return inner.newRow(values);
	}

	@Override
	public Object getAt(int row, int column) {
		return inner.getAt(row, column);
	}

	@Override
	public Row newEmptyRow() {
		onModify();
		return inner.newEmptyRow();
	}

	@Override
	public Object get(int row, String column) {
		return inner.get(row, column);
	}

	@Override
	public boolean removeRow(Row removed) {
		onModify();
		return inner.removeRow(removed);
	}

	@Override
	public <C> C getAt(int row, int column, Class<C> c) {
		return inner.getAt(row, column, c);
	}

	@Override
	public void fireCellChanged(Row basicRow, int i, Object old, Object value) {
		// onModify should have been called at this point
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
		onModify();
		return inner.setAt(row, column, value);
	}

	@Override
	public Object set(int row, String column, Object value) {
		onModify();
		return inner.set(row, column, value);
	}

	@Override
	public <T> Column addColumn(int location, Class<T> clazz, String id, T defaultValue) {
		onModify();
		return inner.addColumn(location, clazz, id, defaultValue);
	}

	@Override
	public <T> Column addColumn(Class<T> clazz, String id, T defaultValue) {
		onModify();
		return inner.addColumn(clazz, id, defaultValue);
	}

	@Override
	public <T> Column addColumn(Class<T> clazz, String id) {
		onModify();
		return inner.addColumn(clazz, id);
	}

	@Override
	public void renameColumn(String oldId, String newId) {
		onModify();
		inner.renameColumn(oldId, newId);
	}

	@Override
	public void renameColumn(int location, String newId) {
		onModify();
		inner.renameColumn(location, newId);
	}

	@Override
	public <F, T> void setColumnType(int location, Class<F> fromType, Class<T> type, Getter<? super F, ? extends T> caster) {
		onModify();
		inner.setColumnType(location, fromType, type, caster);
	}

	@Override
	public void removeColumn(int location) {
		onModify();
		inner.removeColumn(location);
	}

	@Override
	public void removeColumn(String id) {
		onModify();
		inner.removeColumn(id);
	}

	@Override
	public String getTitle() {
		return inner.getTitle();
	}

	@Override
	public void setTitle(String title) {
		onModify();
		inner.setTitle(title);
	}

	@Override
	public void clear() {
		onModify();
		inner.clear();
	}

	@Override
	public int getSize() {
		return inner.getSize();
	}

	@Override
	public int newRows(int count) {
		onModify();
		return inner.newRows(count);
	}

	public Table getInner() {
		return this.inner;
	}

	@Override
	public void removeRow(int position) {
		onModify();
		inner.removeRow(position);
	}

	@Override
	public Row getRow(int position) {
		return inner.getRow(position);
	}

	@Override
	public <T> Column addColumn(int location, Caster<T> clazz, String id, T defaultValue) {
		onModify();
		return inner.addColumn(location, clazz, id, defaultValue);
	}

	@Override
	public <T> Column addColumn(Caster<T> clazz, String id) {
		onModify();
		return inner.addColumn(clazz, id);
	}

	@Override
	public boolean onModify() {
		if (isCopied())
			return false;
		this.inner = new ColumnarTable(inner);
		setCopied(true);
		return true;
	}

	public boolean isCopied() {
		return copied;
	}

	public void setCopied(boolean copied) {
		this.copied = copied;
	}
}
