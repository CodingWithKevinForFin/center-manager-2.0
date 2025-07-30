package com.f1.utils.structs.table.online;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.f1.base.Row;
import com.f1.base.TableList;

public class OnlineTableList implements TableList {

	private Iterable<Row> iterable;

	public void init(Iterable<Row> iterable) {
		this.iterable = iterable;
	}

	@Override
	public int size() {
		return -1;
	}
	@Override
	public boolean isEmpty() {
		return false;
	}
	@Override
	public Iterator<Row> iterator() {
		Iterator<Row> r = iterable.iterator();
		return r;
	}
	@Override
	public boolean contains(Object o) {
		throw unsupported();
	}
	private UnsupportedOperationException unsupported() {
		return new UnsupportedOperationException("Online table does not support random access");
	}
	@Override
	public Object[] toArray() {
		throw unsupported();
	}
	@Override
	public Row[] toRowsArray() {
		throw unsupported();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		throw unsupported();
	}
	@Override
	public boolean add(Row e) {
		throw unsupported();
	}
	@Override
	public boolean remove(Object o) {
		throw unsupported();
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		throw unsupported();
	}
	@Override
	public boolean addAll(Collection<? extends Row> c) {
		throw unsupported();
	}
	@Override
	public boolean addAll(int index, Collection<? extends Row> c) {
		throw unsupported();
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		throw unsupported();
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		throw unsupported();
	}
	@Override
	public void clear() {
		throw unsupported();
	}
	@Override
	public Row get(int index) {
		throw unsupported();
	}
	@Override
	public Row set(int index, Row element) {
		throw unsupported();
	}
	@Override
	public void add(int index, Row element) {
		throw unsupported();
	}
	@Override
	public Row remove(int index) {
		throw unsupported();
	}
	@Override
	public int indexOf(Object o) {
		throw unsupported();
	}
	@Override
	public int lastIndexOf(Object o) {
		throw unsupported();
	}
	@Override
	public ListIterator<Row> listIterator() {
		throw unsupported();
	}
	@Override
	public ListIterator<Row> listIterator(int index) {
		throw unsupported();
	}
	@Override
	public List<Row> subList(int fromIndex, int toIndex) {
		throw unsupported();
	}
	@Override
	public Row addRow(Object... values) {
		throw unsupported();
	}
	@Override
	public Row insertRow(int rowPos, Object... values) {
		throw unsupported();
	}
	@Override
	public void addAll(Iterable<? extends Row> values) {
		throw unsupported();
	}
	@Override
	public long getLongSize() {
		return size();
	}
}
