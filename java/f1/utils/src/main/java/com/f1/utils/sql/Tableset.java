package com.f1.utils.sql;

import java.util.Set;

import com.f1.base.Table;

public interface Tableset {

	public Table getTable(String name);
	public Table getTableNoThrow(String name);
	public Table removeTable(String name);
	public void putTable(String name, Table table);
	public void putTable(Table table);
	public Iterable<String> getTableNamesSorted();
	public Set<String> getTableNames();
	public void clearTables();
}
