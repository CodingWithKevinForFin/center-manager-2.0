package com.f1.utils.sql;

import java.util.Collections;
import java.util.Set;

import com.f1.base.Table;
import com.f1.utils.EmptyIterable;

public class EmptyTableset implements Tableset {

	public static final EmptyTableset INSTANCE = new EmptyTableset();

	private EmptyTableset() {
	}

	@Override
	public Table getTable(String name) {
		return null;
	}

	@Override
	public Table getTableNoThrow(String name) {
		return null;
	}

	@Override
	public Table removeTable(String name) {
		return null;
	}

	@Override
	public void putTable(String name, Table table) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putTable(Table table) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<String> getTableNamesSorted() {
		return EmptyIterable.INSTANCE;
	}

	@Override
	public Set<String> getTableNames() {
		return Collections.EMPTY_SET;
	}

	@Override
	public void clearTables() {
	}

}
