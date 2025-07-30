package com.f1.utils.structs.table.online;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.utils.AH;
import com.f1.utils.structs.table.BasicRow;

public class MapBackedRowIterable implements Iterable<Row> {

	public class It implements Iterator<Row>, Closeable {

		private Row row;
		private Row nextRow;
		private int currentlineNumber = -1;
		private boolean hasNextLine;
		private Iterator<Map> input;

		public It(OnlineTable table, Iterator<Map> input) {
			table.onIteratorOpened(this);
			this.row = new BasicRow(table, 0, new Object[table.getColumnsCount()]);
			this.nextRow = new BasicRow(table, 0, new Object[table.getColumnsCount()]);
			this.input = input;
			progress();
		}
		@Override
		public boolean hasNext() {
			return this.hasNextLine;
		}

		@Override
		public Row next() {
			Row t = this.nextRow;
			this.nextRow = this.row;
			this.row = t;
			progress();
			return this.row;
		}
		private void progress() {
			try {
				for (;;) {
					if (Thread.interrupted())
						throw new RuntimeException("Thread Interrupted");
					Map m = this.input.next();
					this.currentlineNumber++;
					this.hasNextLine = m != null;
					if (!hasNextLine) {
						close();
						break;
					} else if (resetRow(this.currentlineNumber, this.nextRow, m))
						break;
				}
			} catch (IOException e) {
				this.hasNextLine = false;
			}
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public void close() throws IOException {
			//todo: this.input.close();
			table.onIteratorClosed(this);
		}
	}

	private OnlineTable table;
	private Iterator<Map> input;
	private Column[] columns;

	public MapBackedRowIterable(OnlineTable table, Iterator<Map> input) {
		this.table = table;
		this.input = input;
		this.columns = AH.toArray(this.table.getColumns(), Column.class);
	}

	@Override
	public Iterator<Row> iterator() {
		return new It(table, this.input);
	}
	protected boolean resetRow(int currentlineNumber, Row row, Map m) {
		for (Column col : columns)
			row.putAt(col.getLocation(), col.getTypeCaster().cast(m.get(col.getId())));
		return true;
	}

}
