package com.f1.utils.sql;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.f1.base.Table;
import com.f1.utils.concurrent.HasherMap;

public class TablesetWrapper implements Tableset {

	final private Tableset inner;
	private HasherMap<String, Table> tables = new HasherMap<String, Table>();
	//	private List<String> namesSorted = null;

	public TablesetWrapper(Tableset inner) {
		this.inner = inner;
		//		this.namesSorted = new ArrayList<String>(inner.getTableNamesSorted());
	}

	@Override
	public Table getTable(String name) {
		Table r = tables.get(name);
		if (r != null)
			return r;
		return inner.getTable(name);
	}

	@Override
	public Table getTableNoThrow(String name) {
		Table r = tables.get(name);
		if (r != null)
			return r;
		return inner.getTableNoThrow(name);
	}

	@Override
	public Table removeTable(String name) {
		Table r = tables.remove(name);
		if (r == null)
			r = inner.removeTable(name);
		return r;
	}

	@Override
	public void putTable(String name, Table table) {
		this.tables.put(name, table);
	}

	@Override
	public void putTable(Table table) {
		this.tables.put(table.getTitle(), table);
	}

	@Override
	public Iterable<String> getTableNamesSorted() {
		Set<String> names = new TreeSet<String>(tables.keySet());
		names.addAll(inner.getTableNames());
		return names;
	}

	@Override
	public Set<String> getTableNames() {
		Set<String> names = new HashSet<String>(tables.keySet());
		names.addAll(inner.getTableNames());
		return names;
	}

	@Override
	public void clearTables() {
		this.tables.clear();
	}

}
