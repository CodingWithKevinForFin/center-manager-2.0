package com.f1.utils.structs.table.columnar;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.utils.ToDoException;

public class ReadonlyTableList implements TableList {

	final private TableList inner;
	final private ReadonlyTable table;

	public ReadonlyTableList(TableList inner, ReadonlyTable table) {
		this.inner = inner;
		this.table = table;
	}
	//TODO:
	public Row[] toRowsArray() {
		throw new ToDoException("TODO");//TOD: 
	}

	public Row get(int index) {
		return new ReadonlyRow(inner.get(index), table);
	}

	//TODO:
	public ListIterator<Row> listIterator() {
		throw new ToDoException();
	}

	public ListIterator<Row> listIterator(int index) {
		throw new ToDoException();
	}
	public Row addRow(Object... values) {
		throw readonly();
	}

	public Row insertRow(int rowPos, Object... values) {
		throw readonly();
	}

	public void addAll(Iterable<? extends Row> values) {
		throw readonly();
	}

	public Row set(int index, Row element) {
		throw readonly();
	}

	public int size() {
		return inner.size();
	}

	public boolean isEmpty() {
		return inner.isEmpty();
	}

	public boolean contains(Object o) {
		return inner.contains(o);
	}

	public Iterator<Row> iterator() {
		return inner.iterator();
	}

	public Object[] toArray() {
		return inner.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return inner.toArray(a);
	}

	public boolean add(Row e) {
		throw readonly();
	}

	public boolean remove(Object o) {
		throw readonly();
	}

	public boolean containsAll(Collection<?> c) {
		return inner.containsAll(c);
	}

	public boolean addAll(Collection<? extends Row> c) {
		throw readonly();
	}

	public boolean addAll(int index, Collection<? extends Row> c) {
		throw readonly();
	}

	public boolean removeAll(Collection<?> c) {
		throw readonly();
	}

	public boolean retainAll(Collection<?> c) {
		throw readonly();
	}

	public void clear() {
		throw readonly();
	}

	public boolean equals(Object o) {
		return inner.equals(o);
	}

	public int hashCode() {
		return inner.hashCode();
	}

	public void add(int index, Row element) {
		throw readonly();
	}

	public Row remove(int index) {
		throw readonly();
	}

	public int indexOf(Object o) {
		return inner.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return inner.lastIndexOf(o);
	}

	public List<Row> subList(int fromIndex, int toIndex) {
		throw new ToDoException("TODO");//TOD: 
	}

	private RuntimeException readonly() {
		return new RuntimeException("Readonly Table: " + table.getTitle());
	}
	@Override
	public long getLongSize() {
		return size();
	}
}
