package com.f1.utils.sql;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.DetailedException;
import com.f1.utils.OH;
import com.f1.utils.TableHelper;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.columnar.CopyOnWriteTable;

public class TablesetImpl implements Tableset {

	private HasherMap<String, Table> tables = null;
	private List<String> namesSorted = null;

	public TablesetImpl(Map<String, Table> tables) {
		this.tables = new HasherMap<String, Table>(tables);
	}
	public TablesetImpl() {
	}
	public TablesetImpl(Tableset tableset) {
		for (String i : tableset.getTableNames()) {
			Table table = tableset.getTable(i);
			if (table instanceof CopyOnWriteTable)
				table = ((CopyOnWriteTable) table).getInner();
			if (table instanceof ColumnarTable)
				putTable(i, new ColumnarTable(table));
			else
				putTable(i, new BasicTable(table));
		}
	}
	@Override
	public Table getTable(String name) {
		if (tables == null)
			throw new DetailedException("Table not found:" + name).set(CH.SUPPLIED_KEY, name);
		return CH.getOrThrow(tables, name, "Table not found");
	}
	@Override
	public Table getTableNoThrow(String name) {
		return tables == null ? null : tables.get(name);
	}

	@Override
	public Table removeTable(String name) {
		if (tables == null)
			return null;
		Table r = this.tables.remove(name);
		if (r != null)
			this.namesSorted = null;
		return r;
	}

	@Override
	public void putTable(String name, Table table) {
		if (tables == null)
			tables = new HasherMap<String, Table>();
		this.namesSorted = null;
		if (OH.ne(table.getTitle(), name))
			table.setTitle(name);
		this.tables.put(name, table);
	}
	@Override
	public void putTable(Table table) {
		if (table.getTitle() == null)
			throw new NullPointerException("title");
		if (tables == null)
			tables = new HasherMap<String, Table>();
		this.namesSorted = null;
		this.tables.put(table.getTitle(), table);
	}
	@Override
	public List<String> getTableNamesSorted() {
		if (tables == null)
			return Collections.EMPTY_LIST;
		if (this.namesSorted == null)
			this.namesSorted = tables.getKeysSorted();
		return this.namesSorted;
	}

	@Override
	public Set<String> getTableNames() {
		return tables == null ? Collections.EMPTY_SET : tables.keySet();
	}

	@Override
	public void clearTables() {
		tables = null;
		this.namesSorted = null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String table : getTableNamesSorted())
			TableHelper.toString(getTable(table), "", TableHelper.SHOW_ALL, sb);
		return sb.toString();
	}

}
