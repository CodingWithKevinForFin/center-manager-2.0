package com.f1.utils.structs.table.online;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.structs.table.BasicTable;

public class OnlineTable extends BasicTable implements Closeable {

	private OnlineTableList onlineTableList;
	private List<Closeable> iterators = new ArrayList<Closeable>();

	public OnlineTable() {
		super();
	}

	public OnlineTable(Class col1Class, String col1Id, Object... moreColumns) {
		super(col1Class, col1Id, moreColumns);
	}

	public OnlineTable(Class<?>[] colTypes, String[] columnIds) {
		super(colTypes, columnIds);
	}

	public OnlineTable(Column[] columns) {
		super(columns);
	}

	public OnlineTable(Table source) {
		super(source);
	}

	public OnlineTable(List<Column> columns) {
		super(columns);
	}

	public OnlineTable(String[] columnIds) {
		super(columnIds);
	}

	protected TableList initRows(int defaultLength) {
		return this.onlineTableList = new OnlineTableList();
	}

	public void init(Iterable<Row> factory) {
		onlineTableList.init(factory);
	}

	public void onIteratorOpened(Closeable it) {
		this.iterators.add(it);
	}

	public void onIteratorClosed(Closeable it) {
		this.iterators.remove(it);

	}

	@Override
	public void close() {
		if (this.iterators.size() > 0) {
			for (Closeable i : CH.l(this.iterators))
				IOH.close(i);
		}
		onlineTableList = null;
	}
}
