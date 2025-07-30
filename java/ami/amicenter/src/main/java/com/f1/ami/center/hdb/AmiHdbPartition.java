package com.f1.ami.center.hdb;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.center.hdb.idx.AmiHdbPartitionIndex;
import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.table.ColumnPositionMapping;
import com.f1.utils.structs.table.columnar.ColumnarColumnPrimitive;

public class AmiHdbPartition {

	private static final Logger log = LH.get();
	private static final AmiHdbPartitionIndex[] EMPTY_INDEXES = new AmiHdbPartitionIndex[0];
	final private int key;
	final private File directory;
	final private File partitionFile;
	final private BasicIndexedList<String, AmiHdbPartitionColumn> columns = new BasicIndexedList<String, AmiHdbPartitionColumn>();
	final private AmiHdbTable owner;
	final private Map<String, AmiHdbPartitionIndex> indexesByName = new HashMap<String, AmiHdbPartitionIndex>();
	final private Map<String, AmiHdbPartitionIndex> indexesByColName = new HashMap<String, AmiHdbPartitionIndex>();
	private AmiHdbPartitionIndex[] indexes = EMPTY_INDEXES;
	private int rowsCount;
	private AmiHdbPartition priorPartition, nextPartition;
	private Comparable[] partitionsKey;
	private boolean inAppendMode;

	public AmiHdbPartition(AmiHdbTable owner, int key, File directory, Comparable[] partitionValues, AmiHdbPartition prior) {
		try {
			IOH.ensureDir(directory);
		} catch (Exception e) {
			throw handle(e);
		}
		this.priorPartition = prior;
		if (prior != null)
			this.priorPartition.nextPartition = this;
		this.owner = owner;
		this.key = key;
		this.directory = directory;
		this.partitionFile = AmiHdbUtils.newFile(directory, AmiHdbUtils.FILE_KEY, AmiHdbUtils.FILE_EXT_HKEY);
		try {
			if (partitionValues != null) {
				writePartitionKeys(owner, partitionValues, this.partitionFile);
				this.partitionsKey = partitionValues;
				this.inAppendMode = true;
			} else {
				this.inAppendMode = false;
				if (!partitionFile.isFile())
					throw new AmiHdbException("Partition key file not found: " + IOH.getFullPath(partitionFile));
				this.partitionsKey = readPartitionKeys(owner, this.partitionFile);
			}
		} catch (Exception e) {
			throw handle(e);
		}

		int minRowsCount = -1;
		for (Entry<String, AmiHdbColumn> i : this.owner.getColumns()) {
			if (i.getValue().getMode() == AmiHdbUtils.MODE_PARTITION)
				continue;
			final AmiHdbPartitionColumn col = new AmiHdbPartitionColumn(this, i.getValue(), false);
			if (!col.isMissing()) {
				if (minRowsCount == -1)
					minRowsCount = col.getRowCount();
				else
					minRowsCount = MH.min(minRowsCount, col.getRowCount());
			}
			this.columns.add(i.getKey(), col);
			i.getValue().incTotalSize(col.getSizeOnDisk());
		}
		if (minRowsCount == -1)
			minRowsCount = 0;
		for (AmiHdbPartitionColumn i : this.columns.values()) {
			if (!i.isMissing() && i.getRowCount() != minRowsCount) {
				LH.warning(log, "Exit during Partial write for PARTITION COLUMN " + i.describe() + ". Truncating from ", i.getRowCount(), " to ", minRowsCount, " row(s)");
				i.backup();
				i.setRowCount(minRowsCount);
			}
		}
		this.rowsCount = minRowsCount;

	}

	public AmiHdbPartitionColumn addColumn(AmiHdbColumn hc) {
		AmiHdbPartitionColumn r = new AmiHdbPartitionColumn(this, hc, true);
		r.appendNulls(this.rowsCount);
		r.flush();
		this.columns.add(hc.getName(), r);
		hc.incTotalSize(r.getSizeOnDisk());
		return r;
	}
	public void removeColumn(AmiHdbColumn hc) {
		AmiHdbPartitionColumn col = this.columns.remove(hc.getName());
		col.clear();
	}

	public void addIndex(AmiHdbIndex value, boolean isCreate) {
		File target = AmiHdbUtils.newFile(this.directory, value.getName(), AmiHdbUtils.FILE_EXT_HIDX);
		AmiHdbPartitionIndex t = new AmiHdbPartitionIndex(value, this, target, this.getTable().getBlockSize(), 10000, isCreate);
		indexes = AH.append(indexes, t);
		this.indexesByName.put(t.getIndexName(), t);
		this.indexesByColName.put(t.getColumnName(), t);
	}
	public void removeIndex(AmiHdbIndex value) {
		AmiHdbPartitionIndex idx = this.indexesByName.remove(value.getName());
		this.indexesByColName.remove(value.getColumn().getId());
		indexes = AH.remove(indexes, idx);
		idx.clearRows();
	}

	protected void removeRows(int[] toRemove) {
		if (toRemove.length == 0)
			return;
		for (AmiHdbPartitionColumn hcol : this.columns.values()) {
			AmiHdbPartitionIndex idx = this.indexesByColName.get((String) hcol.getName());
			hcol.removeRows(toRemove);
			if (idx != null)
				idx.removeValue();
			hcol.flush();
		}
		incRowsCount(-toRemove.length);
	}
	protected void updateRows(int[] toUpdate, Table t, int valuesStart) {
		int count = toUpdate.length;
		int update2[] = new int[toUpdate.length];
		Comparable[] values = new Comparable[count];
		int colCount = t.getColumnsCount() / 2;
		HashSet<String> changedColumns = new HashSet<String>();
		for (int x = 0; x < colCount; x++) {
			Column col = t.getColumnAt(x);
			Column origCol = t.getColumnAt(x + colCount);
			AmiHdbPartitionIndex idx = this.indexesByColName.get((String) col.getId());
			if (idx != null)
				idx.ensureInMemory();
			int changedCount = 0;
			for (int i = 0; i < count; i++) {
				Comparable nVal = (Comparable) col.getValue(i + valuesStart);
				Comparable oVal = (Comparable) origCol.getValue(i + valuesStart);
				if (OH.ne(nVal, oVal)) {
					int rowToUpdate = toUpdate[i];
					update2[changedCount] = rowToUpdate;
					values[changedCount] = nVal;
					changedCount++;
					if (idx != null)
						idx.updateValue(oVal, nVal, rowToUpdate);
				}
			}
			if (changedCount > 0) {
				AmiHdbPartitionColumn hcol = this.columns.getNoThrow((String) col.getId());
				if (hcol == null) {//updating an old partition that doesn't have a newly declared column
					hcol = addColumn(this.owner.getColumn((String) col.getId()));
				}
				hcol.updateRows(update2, values, changedCount);
				hcol.flush();
				changedColumns.add(hcol.getName());
			}
		}
	}

	public void addRows(Object[][] values, int[] rows, int rowsStart, int rowsCount) {
		int oldRowsCount = this.rowsCount;
		Comparable[] buf = new Comparable[rows.length];
		for (AmiHdbPartitionIndex idx : indexes)
			idx.ensureInMemory();
		for (AmiHdbPartitionColumn hcol : columns.values()) {
			int col = hcol.getHistoryColumn().getLocation();
			int n = 0;
			for (int i = 0; i < rowsCount; i++)
				buf[n++] = (Comparable) (values[rows[rowsStart + i]][col]);
			hcol.appendValues(buf, 0, rowsCount);
			hcol.flush();
			col++;
		}
		for (AmiHdbPartitionIndex idx : indexes) {
			int col = getColumn(idx.getColumnName()).getHistoryColumn().getLocation();
			int n = oldRowsCount;
			for (int i = 0; i < rowsCount; i++) {
				Comparable value = (Comparable) (values[rows[rowsStart + i]][col]);
				idx.addValue(value, n++);
			}
		}
		incRowsCount(rowsCount);
	}
	public void addRows(Table table, int[] rows, int rowsStart, int rowsCount, ColumnPositionMapping posMapping) {
		int oldRowsCount = this.rowsCount;
		Comparable[] buf = null;
		long[] bufLongs = null;
		double[] bufDoubles = null;
		boolean[] bufNulls = null;
		for (AmiHdbPartitionIndex idx : indexes)
			idx.ensureInMemory();
		for (AmiHdbPartitionColumn hcol : columns.values()) {
			AmiHdbPartitionIndex idx = this.indexesByColName.get(hcol.getName());
			int j = posMapping.getSourcePosForTargetPos(hcol.getHistoryColumn().getLocation());
			if (j == -1) {
				hcol.appendNulls(rowsCount);
				if (idx != null) {
					int n = oldRowsCount;
					for (int i = 0; i < rowsCount; i++)
						idx.addValue(null, n++);
				}
			} else {
				Column col = table.getColumnAt(j);
				if (col instanceof ColumnarColumnPrimitive && hcol.isPrimitive()) {
					ColumnarColumnPrimitive p = (ColumnarColumnPrimitive) col;
					if (!p.isFloat()) {
						if (bufLongs == null) {
							bufLongs = this.owner.getLongBuffer(rowsCount);
							bufNulls = this.owner.getBooleanBuffer(rowsCount);
						}
						for (int i = 0; i < rowsCount; i++) {
							int loc = rows[rowsStart + i];
							if (!(bufNulls[i] = p.isNull(loc)))
								bufLongs[i] = p.getLong(loc);
						}
						hcol.appendValuesPrimitive(bufLongs, bufNulls, 0, rowsCount);
					} else {
						if (bufDoubles == null) {
							bufDoubles = this.owner.getDoubleBuffer(rowsCount);
							bufNulls = this.owner.getBooleanBuffer(rowsCount);
						}
						for (int i = 0; i < rowsCount; i++) {
							int loc = rows[rowsStart + i];
							if (!(bufNulls[i] = p.isNull(loc)))
								bufDoubles[i] = p.getDouble(loc);
						}
						hcol.appendValuesPrimitive(bufDoubles, bufNulls, 0, rowsCount);
					}
				} else {
					if (buf == null)
						buf = this.owner.getComparableBuffer(rowsCount);
					for (int i = 0; i < rowsCount; i++)
						buf[i] = (Comparable) col.getValue(rows[rowsStart + i]);
					hcol.appendValues(buf, 0, rowsCount);
					if (idx != null) {
						int n = oldRowsCount;
						for (int i = 0; i < rowsCount; i++) {
							Comparable value = buf[i];
							idx.addValue(value, n++);
						}
					}
				}
			}
			hcol.flush();
		}
		incRowsCount(rowsCount);

	}

	public File getDirectory() {
		return directory;
	}

	public AmiHdbPartitionColumn getColumn(String column) {
		return this.columns.getNoThrow(column);
	}

	public AmiHdbPartitionIndex getIndexByName(String name) {
		return this.indexesByName.get(name);
	}
	public AmiHdbPartitionIndex getIndexByColumnName(String name) {
		return this.indexesByColName.get(name);
	}

	public int getRowCount() {
		return this.rowsCount;
	}

	public int getId() {
		return this.key;
	}

	public void optimize(long endTime) {
		for (AmiHdbPartitionColumn column : this.columns.values()) {
			if (System.currentTimeMillis() > endTime)
				return;
			//try {
			column.optimize();
			//} catch (Exception e) {
			//	LH.severe(log, "Error with column ", column.getName(), " for partition ", SH.join(",", this.partitionsKey),
			//			"Column has been backed up (added .bad extension), and values will be set to null. Please contact support@3forge.com", e);
			//	column.markBad();
			//}
		}
		for (AmiHdbPartitionIndex i : this.indexes) {
			if (System.currentTimeMillis() > endTime)
				return;
			i.optimize();
		}
		this.inAppendMode = false;
	}

	public void unoptimize() {
		this.inAppendMode = true;
		for (AmiHdbPartitionIndex i : this.indexes)
			i.ensureInMemory();
	}

	public boolean isInAppendMode() {
		return this.inAppendMode;
	}

	public AmiHdbTable getTable() {
		return this.owner;
	}

	public void clearRows() {
		for (AmiHdbPartitionColumn i : this.columns.values())
			i.clear();
		for (AmiHdbPartitionIndex i : this.indexes)
			i.clearRows();

		try {
			IOH.deleteForce(directory);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	private AmiHdbException handle(Exception e) {
		if (e instanceof AmiHdbException)
			return (AmiHdbException) e;
		return new AmiHdbException("Critical error with historical partition at " + describe(), e);
	}
	public String describe() {
		return IOH.getFullPath(directory);
	}

	private long firstRow = -1;

	public long getFirstRow() {
		if (firstRow == -1) {
			AmiHdbPartition p = this;
			while (p.firstRow == -1)
				if (p.priorPartition == null)
					p.firstRow = 0;
				else
					p = p.priorPartition;
			while (p != this) {
				final long n = p.firstRow + p.getRowCount();
				p = p.nextPartition;
				p.firstRow = n;
			}
		}
		return firstRow;
	}

	private void incRowsCount(int rowsCount) {
		this.rowsCount += rowsCount;
		for (AmiHdbPartition n = nextPartition; n != null && n.firstRow != -1; n = n.nextPartition)
			n.firstRow = -1;
	}
	public AmiHdbPartitionColumn removeColumn(String s) {
		AmiHdbPartitionColumn r = this.columns.removeNoThrow(s);
		return r;
	}
	public void renameColumn(String name, String newName) {
		AmiHdbPartitionColumn col = this.columns.getNoThrow(name);
		if (col != null) {
			//			col.alterColumn(atype, mode, newName);
			final int pos = this.columns.getPositionNoThrow(name);
			if (OH.ne(name, newName)) {
				col.setName(newName);
				this.columns.removeAt(pos);
				this.columns.add(newName, col, pos);
			}
		}
		AmiHdbPartitionIndex idx = this.indexesByColName.remove(name);
		if (idx != null)
			this.indexesByColName.put(newName, idx);
	}
	public List<AmiHdbPartitionColumn> getColumns() {
		return this.columns.valueList();
	}

	public Comparable[] getPartitionsKey() {
		return this.partitionsKey;
	}
	private static Comparable[] readPartitionKeys(AmiHdbTable table, File file) throws IOException {

		StringCharReader cr = new StringCharReader(IOH.readText(file));
		cr.expectSequence(AmiHdbUtils.HEADER_KEY + "1\n");
		StringBuilder sink = new StringBuilder();
		Comparable[] r = new Comparable[table.getPartitionColumns().size()];
		while (!cr.isEof()) {
			cr.readUntilSkipEscaped(':', '\\', sink);
			cr.expect(':');
			String name = SH.toStringAndClear(sink);
			String value;
			if (cr.peakOrEof() == '"') {
				cr.expect('"');
				cr.readUntilSkipEscaped('"', '\\', sink);
				cr.expect('"');
				cr.expect('\n');
				value = SH.toStringAndClear(sink);
			} else {
				cr.readUntilSkipEscaped('\n', '\\', sink);
				cr.expect('\n');
				value = SH.toStringAndClear(sink);
				if ("null".equals(value))
					value = null;
			}
			AmiHdbColumn col = table.getColumnNoThrow(name);
			if (col instanceof AmiHdbColumn_Partition) {
				AmiHdbColumn_Partition pcol = (AmiHdbColumn_Partition) col;
				r[pcol.getPartionIndex()] = pcol.cast(value);
			} else
				throw new AmiHdbException(
						"Critical error with historical partition KEY file " + IOH.getFullPath(file) + ": Contains entry for non-partition column '" + name + "'");
		}
		return r;
	}

	private static void writePartitionKeys(AmiHdbTable table, Comparable[] partitionValues, File file) throws IOException {
		List<AmiHdbColumn_Partition> partitionColumns = table.getPartitionColumns();
		StringBuilder sb = new StringBuilder();
		sb.append(AmiHdbUtils.HEADER_KEY + "1\n");

		Map<String, Object> entries = new HashMap<String, Object>();
		for (int i = 0; i < partitionValues.length; i++) {
			AmiHdbColumn_Partition col = partitionColumns.get(i);
			Comparable val = col.cast(partitionValues[i]);
			SH.escape(col.getName(), ':', '\\', sb);
			sb.append(':');
			if (val == null)
				sb.append("null");
			else if (val instanceof CharSequence)
				SH.quoteToJavaConst('"', (CharSequence) val, sb);
			else
				sb.append(val).toString();
			sb.append('\n');
		}
		ObjectToJsonConverter.INSTANCE_COMPACT_SORTING.objectToString(entries);
		IOH.writeText(file, sb.toString());
	}

	public void onRemoved() {
		for (AmiHdbPartition n = nextPartition; n != null && n.firstRow != -1; n = n.nextPartition)
			n.firstRow = -1;
		if (this.nextPartition != null)
			this.nextPartition.priorPartition = this.priorPartition;
		if (this.priorPartition != null)
			this.priorPartition.nextPartition = this.nextPartition;
	}

	public void close() {
		for (AmiHdbPartitionIndex i : this.indexes)
			i.close();
		for (AmiHdbPartitionColumn i : this.columns.values())
			i.close();
	}

	public AmiHdbPartitionIndex[] getIndexes() {
		return this.indexes;
	}

	public void appendRows(AmiHdbPartition t) {
		int rowsCount = t.getRowCount();
		int oldRowsCount = this.rowsCount;
		for (AmiHdbPartitionIndex idx : indexes)
			idx.ensureInMemory();
		Comparable[] sink = new Comparable[rowsCount];
		for (AmiHdbPartitionColumn srcColumn : t.getColumns()) {
			AmiHdbPartitionColumn tgtColumn = getColumn(srcColumn.getName());
			if (!srcColumn.isMissing()) {
				srcColumn.readValues(0, rowsCount, 0, sink);
				tgtColumn.appendValues(sink, 0, rowsCount);
			} else {
				tgtColumn.appendNulls(rowsCount);
			}
		}
		for (AmiHdbPartitionIndex idx : indexes) {
			AmiHdbPartitionColumn col = t.getColumn(idx.getColumnName());
			int n = oldRowsCount;
			if (col == null) {
				for (int i = 0; i < rowsCount; i++)
					idx.addValue(null, n++);
			} else {
				col.readValues(0, rowsCount, 0, sink);
				for (int i = 0; i < rowsCount; i++) {
					Comparable value = sink[i];
					idx.addValue(value, n++);
				}
			}
		}
		incRowsCount(rowsCount);
	}

	public boolean isOptimized() {
		for (AmiHdbPartitionColumn i : this.columns.values())
			if (!i.isOptimized())
				return false;
		for (AmiHdbPartitionIndex i : this.indexes)
			if (!i.isOptimized())
				return false;
		return true;
	}

	private static final KeyComparator KEY_COMPARATORS[] = new KeyComparator[32];

	private static class KeyComparator implements Comparator<AmiHdbPartition> {
		private int pos;

		public KeyComparator(int pos) {
			this.pos = pos;
		}

		@Override
		public int compare(AmiHdbPartition o1, AmiHdbPartition o2) {
			return OH.compare(o1.getPartitionsKey()[pos], o2.getPartitionsKey()[pos]);
		}

	}

	public static Comparator<AmiHdbPartition> getKeyComparator(int partionIndex) {
		final KeyComparator r = KEY_COMPARATORS[partionIndex];
		return r != null ? r : (KEY_COMPARATORS[partionIndex] = new KeyComparator(partionIndex));
	}

	public String toString() {
		return "[" + SH.join(", ", (Object[]) this.partitionsKey) + "] at " + directory.getName();
	}
}
