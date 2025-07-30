package com.f1.ami.center.hdb.qry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Getter;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiHdbDummyTable implements Table, TableList {

	private long size = 0;
	private String title;

	@Override
	public TableList getRows() {
		return this;
	}
	@Override
	public List<Column> getColumns() {
		throw ue();
	}

	@Override
	public Map<String, Column> getColumnsMap() {
		throw ue();
	}

	@Override
	public Column getColumnAt(int location) {
		throw ue();
	}

	@Override
	public Column getColumn(String id) {
		throw ue();
	}

	@Override
	public Set<String> getColumnIds() {
		throw ue();
	}

	@Override
	public int getColumnsCount() {
		throw ue();
	}

	@Override
	public Object getAt(int row, int column) {
		throw ue();
	}

	@Override
	public Object get(int row, String column) {
		throw ue();
	}

	@Override
	public <C> C getAt(int row, int column, Class<C> c) {
		throw ue();
	}

	@Override
	public <C> C getAt(int row, int column, Caster<C> c) {
		throw ue();
	}

	@Override
	public <C> C get(int row, String column, Class<C> c) {
		throw ue();
	}

	@Override
	public <C> C get(int row, String column, Caster<C> c) {
		throw ue();
	}

	@Override
	public Object setAt(int row, int column, Object value) {
		throw ue();
	}

	@Override
	public Object set(int row, String column, Object value) {
		throw ue();
	}

	@Override
	public <T> Column addColumn(int location, Class<T> clazz, String id, T defaultValue) {
		throw ue();
	}

	@Override
	public <T> Column addColumn(Class<T> clazz, String id, T defaultValue) {
		throw ue();
	}

	@Override
	public <T> Column addColumn(Class<T> clazz, String id) {
		throw ue();
	}

	@Override
	public void renameColumn(String oldId, String newId) {
		throw ue();

	}

	@Override
	public void renameColumn(int location, String newId) {
		throw ue();

	}

	@Override
	public <F, T> void setColumnType(int location, Class<F> fromType, Class<T> type, Getter<? super F, ? extends T> caster) {
		throw ue();

	}

	@Override
	public void removeColumn(int location) {
		throw ue();

	}

	@Override
	public void removeColumn(String id) {
		throw ue();

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
	public void clear() {
		throw ue();

	}

	@Override
	public NameSpaceCalcTypes getColumnTypesMapping() {
		return EmptyCalcTypes.INSTANCE;
	}

	@Override
	public int getSize() {
		return (int) size;
	}

	@Override
	public int newRows(int count) {
		size += count;
		return count;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		throw ue();
	}

	@Override
	public Row newRow(Object... values) {
		throw ue();
	}

	@Override
	public Row newEmptyRow() {
		throw ue();
	}

	@Override
	public boolean removeRow(Row removed) {
		size--;
		throw ue();
	}

	@Override
	public void removeRow(int position) {
		throw ue();

	}

	@Override
	public Row getRow(int position) {
		throw ue();
	}

	@Override
	public void fireCellChanged(Row basicRow, int i, Object old, Object value) {
		throw ue();
	}

	private RuntimeException ue() {
		return new UnsupportedOperationException();
	}
	@Override
	public int size() {
		return (int) size;
	}
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	@Override
	public boolean contains(Object o) {
		throw ue();
	}
	@Override
	public Iterator<Row> iterator() {
		throw ue();
	}
	@Override
	public Object[] toArray() {
		throw ue();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		throw ue();
	}
	@Override
	public boolean add(Row e) {
		throw ue();
	}
	@Override
	public boolean remove(Object o) {
		throw ue();
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		throw ue();
	}
	@Override
	public boolean addAll(Collection<? extends Row> c) {
		throw ue();
	}
	@Override
	public boolean addAll(int index, Collection<? extends Row> c) {
		throw ue();
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		throw ue();
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		throw ue();
	}
	@Override
	public Row get(int index) {
		return null;
	}
	@Override
	public Row set(int index, Row element) {
		throw ue();
	}
	@Override
	public void add(int index, Row element) {
		throw ue();

	}
	@Override
	public Row remove(int index) {
		throw ue();
	}
	@Override
	public int indexOf(Object o) {
		throw ue();
	}
	@Override
	public int lastIndexOf(Object o) {
		throw ue();
	}
	@Override
	public ListIterator<Row> listIterator() {
		throw ue();
	}
	@Override
	public ListIterator<Row> listIterator(int index) {
		throw ue();
	}
	@Override
	public List<Row> subList(int fromIndex, int toIndex) {
		throw ue();
	}
	@Override
	public Row addRow(Object... values) {
		throw ue();
	}
	@Override
	public Row insertRow(int rowPos, Object... values) {
		throw ue();
	}
	@Override
	public void addAll(Iterable<? extends Row> values) {
		throw ue();

	}
	@Override
	public Row[] toRowsArray() {
		throw ue();
	}
	@Override
	public long getLongSize() {
		return size;
	}
	@Override
	public <T> Column addColumn(int location, Caster<T> clazz, String id, T defaultValue) {
		throw ue();
	}
	@Override
	public <T> Column addColumn(Caster<T> clazz, String id) {
		throw ue();
	}
	@Override
	public boolean onModify() {
		return false;
	}
}
