package com.f1.ami.center.hdb;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.center.hdb.events.AmiHdbTableState;
import com.f1.base.Column;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.FastRandomAccessFilePool;
import com.f1.utils.Hasher;
import com.f1.utils.IOH;
import com.f1.utils.IntArrayList;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.LinkedHasherMap;
import com.f1.utils.concurrent.LinkedHasherMap.Node;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.table.ColumnPositionMapping;
import com.f1.utils.structs.table.ColumnPositionMappingStraight;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiHdbTable {
	private static final Logger log = LH.get();

	public static final String F1PARTITION_PREFIX = "HDBP_";

	public static final long DEFAULT_TIMEOUT_MILLIS = 20000;
	final private FastRandomAccessFilePool filePool;

	public static byte TYPE_FLAT = 0;
	public static byte TYPE_BITMAP = 1;
	public static byte TYPE_OFFSET = 2;
	final private BasicIndexedList<Integer, AmiHdbPartition> partitions = new BasicIndexedList<Integer, AmiHdbPartition>();
	final private BasicIndexedList<Comparable[], AmiHdbPartition> writablePartitions = new BasicIndexedList<Comparable[], AmiHdbPartition>((Hasher) ArrayHasher.INSTANCE);
	private String tableName;
	final private BasicIndexedList<String, AmiHdbIndex> indexes = new BasicIndexedList<String, AmiHdbIndex>();
	final private HashMap<String, AmiHdbIndex> indexesByColumn = new HashMap<String, AmiHdbIndex>();
	final private BasicIndexedList<String, AmiHdbColumn> columns = new BasicIndexedList<String, AmiHdbColumn>();
	final private IntKeyMap<AmiHdbColumn> columnsByAmiKey = new IntKeyMap<AmiHdbColumn>();
	private File tableDir;
	final private byte definedByType;
	final private BasicIndexedList<String, AmiHdbColumn_Partition> partitionColumns = new BasicIndexedList<String, AmiHdbColumn_Partition>();
	private AmiHdbPartition[] partitionsArray;
	private AmiHdbTableRep tableWrapper;
	private int appendPartitionId;
	private ColumnarTable tableBuf;
	private int combineCutoff = 10 * 1000 * 1000;
	private String f1Partition;

	private volatile long rowsCount;

	private AmiHdbTableState state;

	private int maxOptimizeSeconds;
	final private int diskBlockSize;
	final private double optimizePctCutoff;

	public void lock(TimeoutController tc) {
		if (tc == null)
			this.state.getPartition().lockForWrite(DEFAULT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
		else
			this.state.getPartition().lockForWrite(tc.getTimeoutMillisRemaining(), TimeUnit.MILLISECONDS);
	}
	public void unlock() {
		state.getPartition().unlockForWrite();
	}
	public AmiHdbTable(File topDir, String tableName, List<AmiHdbColumnDef> types, Map<String, String> indexes, byte definedBy, int maxOpenCount, int maxOptimizeSeconds,
			int diskBlockSize, double optimizePctCutoff) {
		this.definedByType = definedBy;
		this.diskBlockSize = diskBlockSize;
		this.maxOptimizeSeconds = maxOptimizeSeconds;
		this.optimizePctCutoff = optimizePctCutoff;
		this.tableWrapper = new AmiHdbTableRep(this);
		this.tableName = tableName;
		this.f1Partition = F1PARTITION_PREFIX + tableName;
		this.tableBuf = new ColumnarTable();
		this.tableBuf.setTitle(tableName);
		this.tableDir = AmiHdbUtils.newFile(topDir, tableName, AmiHdbUtils.FILE_EXT_HTAB);
		maxOpenCount = Math.max(maxOpenCount, types.size() + indexes.size() + 2);
		this.filePool = new FastRandomAccessFilePool(maxOpenCount);
		buildColumnsAndIndexs(types, indexes);
		open();
		AmiHdbPartition last = AH.last(this.partitionsArray);
		this.rowsCount = (last == null) ? 0L : last.getFirstRow() + last.getRowCount();
	}

	private void buildColumnsAndIndexs(List<AmiHdbColumnDef> types, Map<String, String> indexes) {
		int partitionColumnIndex = 0;
		for (AmiHdbColumnDef i : types) {
			if (i.getMode() == AmiHdbUtils.MODE_PARTITION) {
				AmiHdbColumn_Partition col = new AmiHdbColumn_Partition(partitionColumnIndex++, this, this.columns.getSize(), i);
				this.partitionColumns.add(i.getName(), col);
				this.columns.add(i.getName(), col);
				this.columnsByAmiKey.put(i.getAmiKey(), col);
			} else {
				AmiHdbColumn col = new AmiHdbColumn(this, this.columns.getSize(), i);
				this.columns.add(i.getName(), col);
				this.columnsByAmiKey.put(i.getAmiKey(), col);
			}
			this.tableBuf.addColumn(i.getTypeClass(), i.getName());

		}
		for (Entry<String, String> i : indexes.entrySet()) {
			String c = i.getValue();
			AmiHdbColumn t = this.columns.get(c);
			if (t == null)
				throw new AmiHdbException("Column not found for index " + i.getKey() + ": " + c);
			if (t.getMode() == AmiHdbUtils.MODE_PARTITION)
				throw new AmiHdbException("Can not create INDEX on PARTITION column: " + c);
			AmiHdbIndex idx = new AmiHdbIndex(i.getKey(), definedByType, t);
			this.indexes.add(i.getKey(), idx);
			this.indexesByColumn.put(idx.getColumn().getName(), idx);
		}
		onSchemaChanged();
	}

	private void rebuildPartitionColumns() {
		OH.assertEq(this.partitionsArray.length, 0);

		List<AmiHdbColumnDef> types = new ArrayList(columns.getSize());
		Map<String, String> idx = new HashMap<String, String>();
		for (AmiHdbColumn i : columns.values()) {
			types.add(new AmiHdbColumnDef(i.getNameAmiKey(), i.getName(), i.getAmiType(), i.getMode()));
		}
		for (AmiHdbIndex i : this.indexes.values())
			idx.put(i.getName(), i.getColumn().getName());
		this.columns.clear();
		this.columnsByAmiKey.clear();
		this.partitionColumns.clear();
		this.indexes.clear();
		this.indexesByColumn.clear();
		for (int i = this.tableBuf.getColumnsCount() - 1; i >= 0; i--)
			this.tableBuf.removeColumn(i);
		buildColumnsAndIndexs(types, idx);

	}
	private void open() {
		try {
			IOH.ensureDir(tableDir);
		} catch (Exception e) {
			throw handle(e);
		}
		TreeMap<Integer, File> partition = new TreeMap<Integer, File>();
		for (File t : tableDir.listFiles()) {
			if (t.isDirectory()) {
				String name = t.getName();
				if (!name.endsWith(AmiHdbUtils.FILE_EXT_HPAR))
					continue;
				name = SH.stripSuffix(name, AmiHdbUtils.FILE_EXT_HPAR, true);
				if (name.length() > 0 && SH.areBetween(name, '0', '9')) {
					int n = SH.parseInt(name);
					if (n < 1)
						throw new AmiHdbException("Invalid index: " + IOH.getFullPath(t));
					if (partition.containsKey(n))
						throw new AmiHdbException("Duplicate index: " + IOH.getFullPath(t));
					partition.put(n, t);
				}
			}
		}
		int max = 1;
		AmiHdbPartition last = null;
		outer: for (Entry<Integer, File> i : partition.entrySet()) {
			File dir = i.getValue();
			LH.info(log, "Working on partition: ", IOH.getFullPath(dir));
			AmiHdbPartition p;
			try {
				p = new AmiHdbPartition(this, i.getKey(), dir, null, last);
			} catch (Exception e) {
				LH.warning(log, "Critical Error with partition, marking as bad: " + IOH.getFullPath(dir), e);
				File dir2 = IOH.appendExtension(dir, ".bad");
				LH.warning(log, "Moving " + IOH.getFullPath(dir) + " to ", IOH.getFullPath(dir2));
				dir.renameTo(dir2);
				continue;
			}
			if (p.getRowCount() == -1) {
				LH.warning(log, "Skipping Partition at " + IOH.getFullPath(p.getDirectory()) + " due to all columns missing");
				continue outer;
			}
			for (AmiHdbIndex idx : this.getIndexes().values())
				p.addIndex(idx, false);
			for (AmiHdbColumn_Partition pc : this.partitionColumns.values())
				pc.addPartition(p);
			if (i.getKey() >= max)
				max = i.getKey() + 1;
			partitions.add(i.getKey(), p);
			if (!p.isOptimized()) {
				if (!this.writablePartitions.containsKey(p.getPartitionsKey()))
					this.writablePartitions.add(p.getPartitionsKey(), p);
			}
			last = p;
		}
		this.appendPartitionId = max;
		this.partitionsArray = AH.toArray(this.partitions.values(), AmiHdbPartition.class);
	}

	public void close() {
		this.flushPersisted();
		for (AmiHdbPartition i : this.partitionsArray)
			i.close();
		this.partitionsArray = new AmiHdbPartition[0];
		this.partitions.clear();
		this.writablePartitions.clear();
		this.appendPartitionId = 0;
		for (AmiHdbColumn i : this.columns.values())
			i.onRowsCleared();
		for (AmiHdbIndex i : this.indexes.values())
			i.onRowsCleared();
	}

	private AmiHdbException handle(Exception e) {
		if (e instanceof AmiHdbException)
			return (AmiHdbException) e;
		return new AmiHdbException("Critical error with historical table at " + describe(), e);
	}
	private String describe() {
		return IOH.getFullPath(this.tableDir);
	}
	public void addIndex(byte definedBy, String idxName, String column) {
		AmiHdbColumn t = this.columns.get(column);
		if (t == null)
			throw new AmiHdbException("Column not found for index " + idxName + ": " + column);
		if (t.getMode() == AmiHdbUtils.MODE_PARTITION)
			throw new AmiHdbException("Can not create INDEX on PARTITION column: " + column);
		if (this.indexes.getNoThrow(idxName) != null)
			throw new AmiHdbException("Index exists: " + idxName);
		if (this.indexesByColumn.containsKey(column))
			throw new AmiHdbException("Duplicate Index on column: " + idxName + " on " + column);
		AmiHdbIndex idx = new AmiHdbIndex(idxName, definedBy, t);
		this.indexes.add(idxName, idx);
		this.indexesByColumn.put(idx.getColumn().getName(), idx);
		for (AmiHdbPartition p : this.partitionsArray)
			p.addIndex(idx, p.isInAppendMode());
		onSchemaChanged();
	}

	public void optimize() {
		long endTime = this.maxOptimizeSeconds == -1 ? Long.MAX_VALUE : (System.currentTimeMillis() + this.maxOptimizeSeconds * 1000);
		BasicMultiMap.List<Comparable[], AmiHdbPartition> forCombining = new BasicMultiMap.List<Comparable[], AmiHdbPartition>(new HasherMap((Hasher) ArrayHasher.INSTANCE));
		for (AmiHdbPartition i : this.partitionsArray)
			forCombining.putMulti(i.getPartitionsKey(), i);

		for (List<AmiHdbPartition> t2 : forCombining.values()) {
			if (System.currentTimeMillis() > endTime)
				break;
			combineLikePartitions(t2, endTime);
		}

		this.writablePartitions.clear();
		for (AmiHdbPartition p : this.partitionsArray)
			if (!p.isOptimized())
				if (!this.writablePartitions.containsKey(p.getPartitionsKey()))
					this.writablePartitions.add(p.getPartitionsKey(), p);
	}

	private void combineLikePartitions(List<AmiHdbPartition> t2, long endTime) {
		AmiHdbPartition largest = t2.get(0);
		if (t2.size() > 1) {
			for (int i = 1; i < t2.size(); i++) {
				AmiHdbPartition t = t2.get(i);
				if (t.getRowCount() > largest.getRowCount()) {
					largest = t;
				}
			}
			for (int i = 0; i < t2.size(); i++) {
				if (System.currentTimeMillis() > endTime)
					return;
				AmiHdbPartition t = t2.get(i);
				if (t == largest)
					continue;
				else if (t.getRowCount() < this.combineCutoff) {
					LH.info(log, "Combining like partitions: Appending " + t.getId() + " to " + largest.getId());
					largest.appendRows(t);
					t.clearRows();
					removePartition(t);
				} else {
					t.optimize(endTime);
				}
			}
		}
		largest.optimize(endTime);
	}

	public BasicIndexedList<String, AmiHdbColumn> getColumns() {
		return this.columns;
	}

	public void removeRows(long rows[]) {
		if (rows.length == 0)
			return;
		Arrays.sort(rows);
		for (int i = 1; i < rows.length; i++) {
			OH.assertGt(rows[i], rows[i - 1]);
		}
		int prior = rows.length - 1;
		int n = prior;
		for (int i = this.partitionsArray.length - 1; i >= 0; i--) {
			AmiHdbPartition p = this.partitionsArray[i];
			final long start = p.getFirstRow();
			while (n >= 0 && rows[n] >= start)
				n--;
			if (n < prior) {
				int prows[] = new int[prior - n];
				int rowsStart = n + 1;
				for (int j = 0; j < prows.length; j++)
					prows[j] = (int) (rows[j + rowsStart] - start);
				if (p.getRowCount() == prows.length) {
					p.clearRows();
					removePartition(p);
				} else
					p.removeRows(prows);
				prior = n;
			}
		}
		this.rowsCount -= rows.length;
	}
	private void removePartition(AmiHdbPartition p) {
		this.partitions.remove(p.getId());
		p.onRemoved();
		int n = AH.indexOf(p, this.partitionsArray);
		this.partitionsArray = AH.remove(this.partitionsArray, n);
		this.writablePartitions.removeNoThrow(p.getPartitionsKey());
		for (AmiHdbColumn_Partition pc : this.partitionColumns.values())
			pc.removePartition(p);
	}
	public void updateRows(long rows[], Table values) {
		if (rows.length == 0)
			return;
		Arrays.sort(rows);
		for (int i = 1; i < rows.length; i++) {
			OH.assertGt(rows[i], rows[i - 1]);
		}
		int prior = rows.length - 1;
		int n = prior;
		for (int i = this.partitionsArray.length - 1; i >= 0; i--) {
			AmiHdbPartition p = this.partitionsArray[i];
			final long start = p.getFirstRow();
			while (n >= 0 && rows[n] >= start)
				n--;
			if (n < prior) {
				int prows[] = new int[prior - n];
				int rowsStart = n + 1;
				for (int j = 0; j < prows.length; j++)
					prows[j] = (int) (rows[j + rowsStart] - start);
				p.updateRows(prows, values, rowsStart);
				prior = n;
			}
		}
	}

	public void addRows(Table table) {
		addRows(ColumnPositionMappingStraight.GET(table.getColumnsCount()), 0, table.getSize(), table);
	}

	public void addRows(ColumnPositionMapping posMapping, int startRow, int rowsCount, Table table) {
		Column cols[] = new Column[this.partitionColumns.getSize()];
		int n = 0;
		for (AmiHdbColumn_Partition i : this.partitionColumns.values()) {
			int pos = posMapping.getSourcePosForTargetPos(i.getLocation());
			if (pos == -1)
				throw new AmiHdbException("Missing Required Partition Column: " + i.getName());
			cols[n++] = table.getColumnAt(pos);
		}
		LinkedHasherMap<Comparable[], IntArrayList> rows = new LinkedHasherMap<Comparable[], IntArrayList>((Hasher) ArrayHasher.INSTANCE);
		Comparable[] key = new Comparable[cols.length];
		for (int j = 0; j < rowsCount; j++) {
			Row row = table.getRow(j + startRow);
			for (int i = 0; i < cols.length; i++) {
				key[i] = (Comparable) cols[i].getValue(row.getLocation());
			}
			Node<Comparable[], IntArrayList> entry = rows.getOrCreateEntry(key);
			IntArrayList val = entry.getValue();
			if (val == null) {
				entry.setValue(val = new IntArrayList());
				key = new Comparable[cols.length];
			}
			val.add(row.getLocation());
		}
		for (LinkedHasherMap<Comparable[], IntArrayList>.EntryIterator it = rows.entryIterator(); it.hasNext();) {
			Entry<Comparable[], IntArrayList> entry2 = it.next();
			AmiHdbPartition partition = getWritableParition(entry2.getKey());
			partition.addRows(table, entry2.getValue().getInner(), 0, entry2.getValue().size(), posMapping);
		}
		this.rowsCount += table.getSize();
	}

	private AmiHdbPartition getWritableParition(Comparable[] key) {
		AmiHdbPartition existing = this.writablePartitions.getNoThrow(key);
		if (existing == null) {
			File newFile;
			for (;;) {
				String idStr = SH.rightAlign('0', SH.toString(this.appendPartitionId), 4, false);
				newFile = AmiHdbUtils.newFile(tableDir, idStr, AmiHdbUtils.FILE_EXT_HPAR);
				if (!newFile.exists())
					break;
				LH.warning(log, "Directory found while creating partition, so trying next id: " + IOH.getFullPath(newFile));
				this.appendPartitionId++;
			}
			existing = new AmiHdbPartition(this, this.appendPartitionId++, newFile, key, CH.last(this.partitions.valueList()));
			for (AmiHdbIndex idx : this.getIndexes().values())
				existing.addIndex(idx, true);
			this.writablePartitions.add(key.clone(), existing);
			partitions.add(existing.getId(), existing);
			this.partitionsArray = AH.append(this.partitionsArray, existing);
			for (AmiHdbColumn_Partition pc : this.partitionColumns.values())
				pc.addPartition(existing);
		}
		return existing;

	}

	public AmiHdbColumn getColumn(String columnName) {
		return this.columns.get(columnName);
	}
	public AmiHdbColumn getColumnNoThrow(String columnName) {
		return this.columns.getNoThrow(columnName);
	}

	public AmiHdbPartition[] getPartitions(String columName, Comparable value) {
		return this.partitionColumns.get(columName).getPartitions(value);
	}
	public Iterator<Entry<Comparable, AmiHdbPartition[]>> getPartitionsBetween(String columName, boolean ascending, Comparable min, boolean minInclusive, Comparable max,
			boolean maxInclusive) {
		return this.partitionColumns.get(columName).getPartitions(ascending, min, minInclusive, max, maxInclusive);
	}
	public Iterator<Entry<Comparable, AmiHdbPartition[]>> getPartitions(String columnName) {
		return this.partitionColumns.get(columnName).getPartitions();
	}
	public List<AmiHdbColumn_Partition> getPartitionColumns() {
		return this.partitionColumns.valueList();
	}
	public BasicIndexedList<String, AmiHdbIndex> getIndexes() {
		return this.indexes;
	}
	public AmiHdbPartition[] getAllPartitions() {
		return this.partitionsArray;
	}

	public FastRandomAccessFilePool getFilePool() {
		return this.filePool;
	}

	public long getRowsCountThreadSafe() {
		return this.rowsCount;
	}
	public int getColumnsCountThreadSafe() {
		return this.columns.getSize();
	}

	public AmiHdbTableRep getTable() {
		return this.tableWrapper;
	}

	public String getName() {
		return this.tableName;
	}

	public NameSpaceCalcTypes getColumnTypes() {
		return this.tableBuf.getColumnTypesMapping();
	}

	public byte getDefType() {
		return this.definedByType;
	}

	public void addRows(int[] positions, Object[][] values) {
		int n = 0;
		LinkedHasherMap<Comparable[], IntArrayList> rows = new LinkedHasherMap<Comparable[], IntArrayList>((Hasher) ArrayHasher.INSTANCE);
		Comparable[] key = new Comparable[this.partitionColumns.getSize()];
		int rowNum = 0;
		for (Object[] row : values) {
			for (int i = 0; i < key.length; i++) {
				key[i] = (Comparable) row[this.partitionColumns.getAt(i).getLocation()];
			}
			Node<Comparable[], IntArrayList> entry = rows.getOrCreateEntry(key);
			IntArrayList val = entry.getValue();
			if (val == null) {
				entry.setValue(val = new IntArrayList());
				key = new Comparable[key.length];
			}
			val.add(rowNum++);
		}
		for (LinkedHasherMap<Comparable[], IntArrayList>.EntryIterator it = rows.entryIterator(); it.hasNext();) {
			Entry<Comparable[], IntArrayList> entry2 = it.next();
			AmiHdbPartition partition = getWritableParition(entry2.getKey());
			partition.addRows(values, entry2.getValue().getInner(), 0, entry2.getValue().size());
		}
		this.rowsCount += values.length;
	}

	public void clearRows() {
		for (AmiHdbPartition i : this.partitions.values())
			i.clearRows();
		this.partitions.clear();
		for (AmiHdbColumn_Partition i : this.partitionColumns.values())
			i.onRowsCleared();
		for (AmiHdbColumn i : this.columns.values())
			i.onRowsCleared();
		for (AmiHdbIndex i : this.getIndexes().values())
			i.onRowsCleared();
		this.writablePartitions.clear();
		this.partitionsArray = new AmiHdbPartition[0];
		this.appendPartitionId = 1;
		this.rowsCount = 0;
		// TODO Auto-generated method stub

	}

	public int getColumnsCount() {
		return this.columns.getSize();
	}

	public AmiHdbColumn getColumnAt(int i) {
		return this.columns.getAt(i);
	}
	public void removeIndex(String indexName) {
		AmiHdbIndex idx = this.indexes.remove(indexName);
		this.indexesByColumn.remove(idx.getColumn().getName());
		for (AmiHdbPartition i : partitionsArray)
			i.removeIndex(idx);
		onSchemaChanged();
	}
	public AmiHdbColumn addColumn(int position, AmiHdbColumnDef col) {
		boolean needsPartitionRebuild = col.getMode() == AmiHdbUtils.MODE_PARTITION;
		if (needsPartitionRebuild && rowsCount > 0)
			throw new ExpressionParserException(0, "Can only add/modify/drop PARTITION columns on empty tables. You must TRUNCATE TABLE first");

		AmiHdbColumn hc = new AmiHdbColumn(this, position, col);
		this.columns.add(col.getName(), hc, position);
		this.columnsByAmiKey.put(col.getAmiKey(), hc);
		this.tableBuf.addColumn(position, col.getTypeClass(), col.getName(), null);
		for (int i = position + 1; i < this.columns.getSize(); i++)
			this.columns.getAt(i).setLocation(i);
		for (AmiHdbPartition i : this.partitions.values())
			i.addColumn(hc);
		if (needsPartitionRebuild)
			rebuildPartitionColumns();
		onSchemaChanged();
		return hc;
	}

	public void removeColumn(String s) {
		AmiHdbIndex idx = this.indexesByColumn.get(s);
		if (idx != null)
			throw new ExpressionParserException(0, "Column " + s + " participates in index: " + idx.getName());
		AmiHdbColumn col = this.columns.get(s);
		int loc = col.getLocation();
		boolean needsPartitionRebuild = col.getMode() == AmiHdbUtils.MODE_PARTITION;
		if (needsPartitionRebuild && rowsCount > 0)
			throw new ExpressionParserException(0, "Can only add/modify/drop PARTITION columns on empty tables. You must TRUNCATE TABLE first");
		this.columns.remove(s);
		this.columnsByAmiKey.remove(col.getNameAmiKey());
		this.tableBuf.removeColumn(s);
		for (int i = loc; i < this.columns.getSize(); i++)
			this.columns.getAt(i).setLocation(i);
		for (AmiHdbPartition i : this.partitionsArray) {
			AmiHdbPartitionColumn c = i.removeColumn(s);
			if (c != null)
				c.clear();
		}
		col.onRowsCleared();
		if (needsPartitionRebuild)
			rebuildPartitionColumns();
		onSchemaChanged();
	}
	public ColumnarTable getTableBuffer() {
		return this.tableBuf;
	}
	public File getDirectory() {
		return this.tableDir;
	}
	public void alterColumn(int location, byte atype, byte mode, short newNameAmiKey, String newName) {
		AmiHdbColumn existing = this.getColumnAt(location);
		String origName = existing.getName();
		short origNameAmiKey = existing.getNameAmiKey();
		AmiHdbIndex idx = this.indexesByColumn.get(existing.getName());
		if (idx != null)
			throw new ExpressionParserException(0, "Column " + existing.getName() + " participates in index: " + idx.getName());

		boolean needsPartitionRebuild = existing.getMode() == AmiHdbUtils.MODE_PARTITION || mode == AmiHdbUtils.MODE_PARTITION;
		if (needsPartitionRebuild && rowsCount > 0)
			throw new ExpressionParserException(0, "Can only add/modify/drop PARTITION columns on empty tables. You must TRUNCATE TABLE first");
		existing.setAmiType(atype);
		existing.setMode(mode);
		if (SH.is(newName) && OH.ne(origName, newName)) {
			existing.setName(newNameAmiKey, newName);
			this.columns.remove(origName);
			this.columnsByAmiKey.remove(origNameAmiKey);
			this.columns.add(newName, existing, location);
			this.columnsByAmiKey.put(existing.getAmiType(), existing);
			this.tableBuf.removeColumn(location);
			this.tableBuf.addColumn(location, existing.getType(), existing.getId(), null);
			for (AmiHdbPartition i : this.partitionsArray) {
				i.renameColumn(origName, newName);
			}
			for (AmiHdbPartition i : this.partitionsArray) {
				i.renameColumn(origName, newName);
			}
		}
		if (needsPartitionRebuild)
			rebuildPartitionColumns();
		onSchemaChanged();
	}

	List<AmiHdbPartitionColumn> columnsToFlush = new ArrayList<AmiHdbPartitionColumn>();

	private boolean registeredForFlush;

	public void registerForFlush(AmiHdbPartitionColumn amiHistoryPartitionColumn) {
		registerForFlush();
		columnsToFlush.add(amiHistoryPartitionColumn);
	}

	private AmiHdbSchema_Table sqlSchema;

	private void registerForFlush() {
		registeredForFlush = true;
	}

	public void flushPersisted() {
		if (!registeredForFlush)
			return;
		registeredForFlush = false;
		if (columnsToFlush.size() > 0) {
			for (AmiHdbPartitionColumn i : columnsToFlush)
				i.flushPersisted();
			columnsToFlush.clear();
		}

	}
	public void renameTo(String to) {
		close();
		File tableDir2 = AmiHdbUtils.newFile(this.tableDir.getParentFile(), to, AmiHdbUtils.FILE_EXT_HTAB);
		if (!this.tableDir.renameTo(tableDir2)) {
			open();
			throw new AmiHdbException("Could not rename: " + IOH.getFullPath(this.tableDir) + " to " + IOH.getFullPath(tableDir2));
		}
		this.tableDir = tableDir2;
		this.tableName = to;
		this.tableBuf.setTitle(tableName);
		this.f1Partition = F1PARTITION_PREFIX + tableName;

		open();
		onSchemaChanged();
	}

	public AmiHdbPartition getPartitionById(int pid) {
		return this.partitions.getNoThrow(pid);
	}

	public boolean movePartitinToWritable(AmiHdbPartition partition) {
		AmiHdbPartition t = this.writablePartitions.getNoThrow(partition.getPartitionsKey());
		if (t != null)
			return false;
		this.writablePartitions.add(partition.getPartitionsKey(), partition);
		partition.unoptimize();
		return true;
	}
	public String getF1PartitionId() {
		return this.f1Partition;
	}
	public AmiHdbSchema_Table getSqlSchemaThreadSafe() {
		return sqlSchema;
	}

	private void onSchemaChanged() {
		this.sqlSchema = new AmiHdbSchema_Table(this);
	}
	public Column getColumnByAmiKey(short key) {
		return this.columnsByAmiKey.get(key);
	}
	public void setState(AmiHdbTableState state) {
		this.state = state;
	}
	public void assertLocked() {
		if (!this.state.getPartition().isWriteLockedByCurrentThread())
			if (this.state.getPartition().isWriteLocked())
				throw new RuntimeException("Locked by another thread");
			else
				throw new RuntimeException("Not Locked");
	}
	public AmiHdbIndex getIndexForColumn(String name) {
		return this.indexesByColumn.get(name);
	}
	public int getMaxOptimizeSeconds() {
		return this.maxOptimizeSeconds;
	}

	private static final int BUF_SIZE = 10000;
	private final long[] lBuffer = new long[BUF_SIZE];
	private final boolean[] bBuffer = new boolean[BUF_SIZE];
	private final double[] dBuffer = new double[BUF_SIZE];
	private final Comparable[] cBuffer = new Comparable[BUF_SIZE];

	final public long[] getLongBuffer(int size) {
		return (size < BUF_SIZE) ? lBuffer : new long[size];
	}
	final public boolean[] getBooleanBuffer(int size) {
		return (size < BUF_SIZE) ? bBuffer : new boolean[size];
	}
	final public double[] getDoubleBuffer(int size) {
		return (size < BUF_SIZE) ? dBuffer : new double[size];
	}
	final public Comparable[] getComparableBuffer(int size) {
		return (size < BUF_SIZE) ? cBuffer : new Comparable[size];
	}
	public int getBlockSize() {
		return this.diskBlockSize;
	}
	public double getOptimizePctCutoff() {
		return this.optimizePctCutoff;
	}
}
