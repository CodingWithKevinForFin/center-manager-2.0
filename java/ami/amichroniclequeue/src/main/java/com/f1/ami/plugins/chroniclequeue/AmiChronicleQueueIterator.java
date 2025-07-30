package com.f1.ami.plugins.chroniclequeue;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.f1.base.Column;
import com.f1.base.Getter;
import com.f1.base.Table;
import com.f1.utils.SH;

import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.wire.DocumentContext;

public class AmiChronicleQueueIterator implements Iterator<Map> {

	final private File file;
	final private ChronicleQueue queue;
	final private ExcerptTailer tailer;
	private long entryCount;
	private long index;
	private long endIndex;
	private long startIndex;
	private Table table;
	private String delim;
	private Getter<Map, Object>[] gets;
	private String indexName;
	boolean hasIndexColumn = false;

	public AmiChronicleQueueIterator(File file, Table table, String delim, String indexName) {
		this.file = file;
		this.queue = ChronicleQueue.singleBuilder(file).build();
		this.tailer = queue.createTailer();
		this.endIndex = tailer.toEnd().index();
		this.startIndex = tailer.toStart().index();
		this.entryCount = ((SingleChronicleQueue) queue).entryCount();
		this.index = -1;
		this.indexName = indexName;
		this.hasIndexColumn = (SH.is(this.indexName) && !(SH.equals("", this.indexName)));

		this.table = table;
		this.delim = delim;
		int columnsCount = table.getColumnsCount();
		this.gets = new Getter[columnsCount];
		int j = 0;
		for (int i = 0; i < columnsCount; i++) {
			Column c = table.getColumnAt(i);
			Getter<Map, Object> nwGetter = toGetter(SH.s(c.getId()), this.delim);
			if (nwGetter != null) {
				this.gets[j++] = nwGetter;
			}
		}
	}
	@Override
	public boolean hasNext() {
		return this.index < (endIndex - 1);
	}

	@Override
	public Map next() {
		Map row = null;
		final DocumentContext dc = tailer.readingDocument();
		if (dc.isPresent()) {
			row = new HashMap<Object, Object>();
			dc.wire().readAllAsMap(Object.class, Object.class, row);
			this.index = dc.index();

			if (this.hasIndexColumn) {
				row.put(this.indexName, this.index);
			}

			for (int i = 0; i < this.gets.length; i++) {
				NestedMapGetter get = (NestedMapGetter) this.gets[i];
				if (get != null) {
					Object id = get.getName();
					Object value = get.get(row);
					row.put(id, value);
				}
			}

		}

		return row;
	}

	public static Getter<Map, Object> toGetter(String path, String delim) {
		if (delim.equals(path))
			return null;
		//		return ALL;
		if (path.indexOf(delim) == -1)
			return null;
		else
			return new NestedMapGetter(path, delim);

	}

	public static final Getter<Map, Object> ALL = new BasicMapGetter(null);

	public static class BasicMapGetter implements Getter<Map, Object> {
		private String path;

		public BasicMapGetter(String path) {
			this.path = path;
		}

		@Override
		public Object get(Map document) {
			return path == null ? document : document.get(path);
		}
	}

	public static class NestedMapGetter implements Getter<Map, Object> {
		private String name;
		private String path[];

		public NestedMapGetter(String path, String delim) {
			this.name = path;
			if (path.startsWith(delim))
				this.path = SH.split(delim, path.substring(2));
			else
				this.path = SH.split(delim, path);
		}

		@Override
		public Object get(Map document) {
			Object r = document.get(path[0]);
			for (int i = 1; i < path.length; i++)
				if (!(r instanceof Map))
					return null;
				else
					r = ((Map) r).get(path[i]);
			return r;
		}

		public String getName() {
			return name;
		}

	}

	@Override
	public void remove() {

	}
}
