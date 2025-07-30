package com.f1.utils.structs.table.online;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import com.f1.base.Generator;
import com.f1.base.Row;
import com.f1.utils.FastBufferedReader;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicRow;

public class FileBackedRowIterable implements Iterable<Row> {

	public class It implements Iterator<Row>, Closeable {

		private Row row;
		private Row nextRow;
		final private FastBufferedReader reader;
		final private StringBuilder line = new StringBuilder();
		private int currentlineNumber = -1;
		private boolean hasNextLine;
		private FastBufferedReader rdr;

		public It(OnlineTable table, Generator<Reader> input) {
			table.onIteratorOpened(this);
			this.row = new BasicRow(table, 0, new Object[table.getColumnsCount()]);
			this.nextRow = new BasicRow(table, 0, new Object[table.getColumnsCount()]);

			try {
				rdr = new FastBufferedReader(input.nw());
			} catch (RuntimeException e) {
				this.hasNextLine = false;
				rdr = null;
			}
			this.reader = rdr;
			if (this.reader != null)
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
					line.setLength(0);
					this.currentlineNumber++;
					this.hasNextLine = reader.readLine(line);
					if (!hasNextLine) {
						close();
						break;
					} else if (resetRow(this.currentlineNumber, this.nextRow, this.line))
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
			IOH.close(this.reader);
			table.onIteratorClosed(this);
		}
	}
	private OnlineTable table;
	private Generator<Reader> input;

	public FileBackedRowIterable(OnlineTable table, Generator<Reader> input) {
		this.table = table;
		this.input = input;
	}

	@Override
	public Iterator<Row> iterator() {
		return new It(table, this.input);
	}
	protected boolean resetRow(int currentlineNumber, Row row, CharSequence line) {
		row.putAt(0, SH.parseLong(line, 10));
		return true;
	}

}
