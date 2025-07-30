package com.f1.ami.center.hdb.idx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.center.hdb.AmiHdbException;
import com.f1.ami.center.hdb.AmiHdbIndex;
import com.f1.ami.center.hdb.AmiHdbPartition;
import com.f1.ami.center.hdb.AmiHdbPartitionColumn;
import com.f1.ami.center.hdb.AmiHdbUtils;
import com.f1.ami.center.hdb.col.AmiHdbMarshaller;
import com.f1.ami.center.hdb.col.AmiHdbMarshallers;
import com.f1.utils.AH;
import com.f1.utils.EmptyIterator;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastRandomAccessFile;
import com.f1.utils.IOH;
import com.f1.utils.IntArrayList;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.structs.ArrayIterator;
import com.f1.utils.structs.CompactLongKeyMap;
import com.f1.utils.structs.ComparableComparator;

public class AmiHdbPartitionIndex<T extends Comparable<?>> {
	private static final Logger log = LH.get();
	public static final int POINTER_SIZE = 5;
	public static final long HEADER_SIZE = 5;
	final private File file;
	private AmiHdbMarshaller<T> marshaller;
	private long rootPosition;
	private long nullsPosition;
	private FastRandomAccessFile raf;
	final private int cacheSize;
	private byte columnType;
	private String columnName;
	final private int blockSize;
	private AmiHdbPartition partition;
	private int keysCount;
	final private AmiHdbIndex index;

	public AmiHdbPartitionIndex(AmiHdbIndex index, AmiHdbPartition partition, File target, int blockSize, int cacheSize, boolean isCreate) {
		this.index = index;
		this.partition = partition;
		this.file = target;
		this.cacheSize = cacheSize;
		this.blockSize = blockSize;
		cache = new CompactLongKeyMap<Block>("", KEYGETTER, cacheSize);
		try {

			if (isCreate) {
				if (file.exists())
					throw new IllegalStateException("File already exists: " + IOH.getFullPath(file));
				this.columnType = index.getColumn().getAmiType();
				this.columnName = index.getColumn().getName();
				this.marshaller = (AmiHdbMarshaller<T>) AmiHdbMarshallers.getMarshaller(this.columnType);
				buildCachedValuesFromColumnOnDisk();
			} else {
				if (file.exists()) {
					this.raf = partition.getTable().getFilePool().open(file, "rw", blockSize);
					boolean error = false;
					try {
						FastDataInput in = this.raf.getInput();
						int version = AmiHdbUtils.readHeader(in, AmiHdbUtils.HEADER_INDEX);
						OH.assertEq(in.readByte(), '\n');
						OH.assertEq(version, 1);
						this.columnType = AmiHdbUtils.readType(in);
						OH.assertEq(in.readByte(), '\n');
						this.columnName = in.readUTF();
						this.marshaller = (AmiHdbMarshaller<T>) AmiHdbMarshallers.getMarshaller(this.columnType);
						this.rootPosition = in.readLong5();
						this.nullsPosition = in.readLong5();
						this.keysCount = in.readInt();
						if (keysCount == -1) {
							error = true;
							LH.warning(log, "HISTORICAL INDEX at ", describe(), " has partial write. Auto-recreating. ");
						}
					} catch (Exception e) {
						LH.warning(log, "HISTORICAL INDEX at ", describe(), " is corrupt, Auto-recreating.", e);
						error = true;
					}
					if (error) {
						this.columnType = index.getColumn().getAmiType();
						this.columnName = index.getColumn().getName();
						this.marshaller = (AmiHdbMarshaller<T>) AmiHdbMarshallers.getMarshaller(this.columnType);
						IOH.close(raf);
						raf = null;
						buildCachedValuesFromColumnOnDisk();
						writeToDisk();
					}

				} else {
					this.columnType = index.getColumn().getAmiType();
					this.columnName = index.getColumn().getName();
					this.marshaller = (AmiHdbMarshaller<T>) AmiHdbMarshallers.getMarshaller(this.columnType);
					buildCachedValuesFromColumnOnDisk();
					writeToDisk();
				}
			}
		} catch (Exception e) {
			throw handle(e);
		} finally {
			IOH.close(this.raf);
		}
	}

	private void buildCachedValuesFromColumnOnDisk() {
		this.cache.clear();
		this.writableRows = new TreeMap<T, IntArrayList>((Comparator) ComparableComparator.INSTANCE);
		this.rootPosition = -1;
		this.nullsPosition = -1;
		OH.assertNull(raf);
		AmiHdbPartitionColumn col = this.partition.getColumn(this.columnName);
		if (!col.isMissing()) {
			if (col.getRowCount() > 0) {
				LH.info(log, "Building in-memory HISTORICAL INDEX ", IOH.getFullPath(this.file) + " using COLUMN '" + this.columnName + "'");
				int rowCount = col.getRowCount();
				Comparable[] values = new Comparable[rowCount];
				col.readValues(0, rowCount, 0, values);
				for (int i = 0; i < values.length; i++) {
					T value = (T) values[i];
					IntArrayList rows = writableRows.get(value);
					if (rows == null)
						writableRows.put(value, rows = new IntArrayList());
					rows.add(i);
				}
			}
		} else if (partition.getRowCount() > 0) {
			LH.info(log, "Building in-memory HISTORICAL INDEX ", IOH.getFullPath(this.file) + " using missing COLUMN '" + this.columnName + "'");
			int rowCount = partition.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				IntArrayList rows = writableRows.get(null);
				if (rows == null)
					writableRows.put(null, rows = new IntArrayList());
				rows.add(i);
			}
		}
		file.delete();
		this.keysCount = this.writableRows.size();
	}

	public Iterator<Map.Entry<T, int[]>> findBetween(boolean ascending, Comparable min, boolean minInclusive, Comparable max, boolean maxInclusive) {
		if (min != null && max != null && OH.compare(min, max) > 0)
			return EmptyIterator.INSTANCE;
		if (raf == null) {
			NavigableMap<T, IntArrayList> t;
			if (min != null && max != null)
				t = this.getWriteableRows().subMap((T) min, minInclusive, (T) max, maxInclusive);
			else if (min != null)
				t = this.getWriteableRows().tailMap((T) min, minInclusive);
			else if (max != null)
				t = this.getWriteableRows().headMap((T) max, maxInclusive);
			else
				t = null;
			if (!ascending && t != null)
				t = t.descendingMap();
			List<AmiHdbIndexEntry<T>> r = new ArrayList<AmiHdbIndexEntry<T>>(t.size());
			for (Entry<T, IntArrayList> i : t.entrySet())
				r.add(new AmiHdbIndexEntry<T>(i.getKey(), i.getValue().toIntArray()));
			return (Iterator) r.iterator();
		}
		if (keysCount == (this.nullsPosition == -1 ? 0 : 1)) {
			if (ascending && min == null && nullsPosition != -1 && minInclusive)
				return new ArrayIterator<Map.Entry<T, int[]>>(getNullRows().children);
			return EmptyIterator.INSTANCE;
		}
		if (ascending) {
			if (min != null) {
				final AmiHdbIndexNode<T> keyMin = toIndexNode(min);
				final LeafBlock<T> lb = findLeaf(keyMin);
				if (lb == null)
					return EmptyIterator.INSTANCE;
				final int n = minInclusive ? lb.findGe(keyMin) : lb.findGt(keyMin);
				return n == -1 ? EmptyIterator.INSTANCE : new ForwardsIterator(lb, n, toIndexNode(max), maxInclusive, marshaller);
			} else {
				LeafBlock first = minInclusive && nullsPosition != -1 ? getNullRows() : getFistLeaf();
				Iterator<Map.Entry<T, int[]>> r = new ForwardsIterator(first, 0, toIndexNode(max), maxInclusive, marshaller);
				return r;
			}
		} else {
			if (max != null) {
				final AmiHdbIndexNode<T> keyMax = toIndexNode(max);
				final LeafBlock<T> lb = findLeaf(keyMax);
				if (lb == null)
					return EmptyIterator.INSTANCE;
				final int n = maxInclusive ? lb.findLe(keyMax) : lb.findLt(keyMax);
				Iterator<Map.Entry<T, int[]>> r = n == -1 ? EmptyIterator.INSTANCE : new BackwardsIterator(lb, n, toIndexNode(min), minInclusive, marshaller);
				return r;
			} else {
				LeafBlock last = getLastLeaf();
				Iterator<Map.Entry<T, int[]>> r = new BackwardsIterator(last, last.children.length - 1, toIndexNode(min), minInclusive, marshaller);
				return r;
			}
		}
	}

	private TreeMap<T, IntArrayList> getWriteableRows() {
		OH.assertNull(raf);
		if (this.writableRows == null)
			buildCachedValuesFromColumnOnDisk();
		return this.writableRows;
	}

	private AmiHdbIndexNode<T> toIndexNode(Comparable value) {
		if (value == null)
			return null;
		AmiHdbIndexNode<T> r = new AmiHdbIndexNode<T>();
		r.setIndexValue((T) value);
		return r;
	}

	private static final CompactLongKeyMap.KeyGetter<Block> KEYGETTER = new CompactLongKeyMap.KeyGetter<Block>() {

		@Override
		public long getKey(Block object) {
			return object.getPositionOnDisk();
		}
	};
	private CompactLongKeyMap<Block> cache;

	private Block<T> readBlock(long position) {
		if (position == -1L)
			return null;
		Block r = cache.get(position);
		if (r != null)
			return r;
		try {
			raf.seek(position);
			FastDataInput in = raf.getInput();
			byte mask = in.readByte();
			boolean isLeaf = MH.allBits(mask, 0x01);
			boolean hasNext = MH.allBits(mask, 0x02);
			if (isLeaf)
				r = new LeafBlock<T>(hasNext, raf, marshaller);
			else
				r = new ParentBlock<T>(hasNext, raf, marshaller);
			r.setPositionOnDisk(position);
			if (cache.size() < cacheSize)
				cache.put(r);
			return r;
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}

	}

	public int[] find(T value) {
		if (raf == null) {
			IntArrayList r = this.getWriteableRows().get(value);
			return (r == null) ? OH.EMPTY_INT_ARRAY : r.toIntArray();
		}
		if (value == null) {
			if (this.nullsPosition == -1)
				return OH.EMPTY_INT_ARRAY;
			return getNullRows().children[0].rows;
		}
		if (keysCount == (this.nullsPosition == -1 ? 0 : 1))
			return OH.EMPTY_INT_ARRAY;
		AmiHdbIndexNode<T> key = new AmiHdbIndexNode<T>();
		key.setIndexValue(value);
		LeafBlock<T> lb = findLeaf(key);
		if (lb == null)
			return OH.EMPTY_INT_ARRAY;
		int[] r = lb.find(key);
		return r == null ? OH.EMPTY_INT_ARRAY : r;
	}

	private LeafBlock getNullRows() {
		LeafBlock lb = (LeafBlock) readBlock(this.nullsPosition);
		lb.setIndexValue(null);
		return lb;
	}
	private int[] toints(IntArrayList r) {
		return r == null ? OH.EMPTY_INT_ARRAY : r.toIntArray();

	}

	private LeafBlock<T> findLeaf(AmiHdbIndexNode<T> keyMin) {
		Block<T> r = getRoot();
		while (r instanceof ParentBlock) {
			long pos = ((ParentBlock<T>) r).find(keyMin, marshaller);
			if (pos == -1)
				return null;
			else
				r = readBlock(pos);
		}
		return (LeafBlock<T>) r;
	}

	//	private boolean isEmpty() {
	//		return root.isEmpty();
	//	}

	private LeafBlock<T> getFistLeaf() {
		Block<T> r = getRoot();
		while (r instanceof ParentBlock) {
			long pos = ((ParentBlock<T>) r).childPositions[0];
			if (pos == -1)
				return null;
			r = readBlock(pos);
		}
		return (LeafBlock<T>) r;
	}
	private Block<T> getRoot() {
		return readBlock(this.rootPosition);
	}

	private LeafBlock<T> getLastLeaf() {
		Block<T> r = getRoot();
		while (r instanceof ParentBlock) {
			long pos = AH.last(((ParentBlock<T>) r).childPositions, -1L);
			if (pos == -1)
				return null;
			r = readBlock(pos);
		}
		return (LeafBlock<T>) r;
	}

	private static class LeafBlock<T extends Comparable<?>> extends Block<T> {
		AmiHdbIndexEntry<T>[] children;
		private long priorPosition;
		private long nextPosition;

		public LeafBlock(boolean hasNext, FastRandomAccessFile raf, AmiHdbMarshaller<T> marshaller) throws IOException {
			super();
			FastDataInput in = raf.getInput();
			children = new AmiHdbIndexEntry[in.readUnsignedShort()];
			this.priorPosition = in.readLong5();
			for (int i = 0; i < children.length; i++) {
				AmiHdbIndexEntry<T> child = (AmiHdbIndexEntry<T>) new AmiHdbIndexEntry();
				child.setIndexValue(marshaller.read(in));
				int[] rows = new int[in.readInt()];
				for (int n = 0; n < rows.length; n++) {
					rows[n] = in.readInt();
				}
				child.setValue(rows);
				children[i] = child;
			}
			this.setIndexValue(children[0].getIndexValue());
			this.nextPosition = hasNext ? raf.getPosition() : -1L;
		}

		public int[] find(AmiHdbIndexNode<T> key) {
			int r = AH.indexOfSorted(key, children);
			return r == -1 ? null : children[r].rows;
		}
		public int findGt(AmiHdbIndexNode<T> key) {
			return AH.indexOfSortedGreaterThan(key, children);
		}
		public int findGe(AmiHdbIndexNode<T> key) {
			return AH.indexOfSortedGreaterThanEqualTo(key, children);
		}
		public int findLt(AmiHdbIndexNode<T> key) {
			return AH.indexOfSortedLessThan(key, children);
		}
		public int findLe(AmiHdbIndexNode<T> key) {
			return AH.indexOfSortedLessThanEqualTo(key, children);
		}

		public long getPrior() {
			return priorPosition;
		}
		public long getNext() {
			return nextPosition;
		}
		@Override
		protected boolean isEmpty() {
			return children.length == 0;
		}
	}

	public class ParentBlock<T extends Comparable<?>> extends Block<T> {
		AmiHdbIndexNode<T>[] childValues;
		long[] childPositions;

		public ParentBlock(boolean hasNext, FastRandomAccessFile raf, AmiHdbMarshaller<T> marshaller) throws IOException {
			super();
			FastDataInput in = raf.getInput();
			childValues = (AmiHdbIndexNode[]) new AmiHdbIndexNode[in.readUnsignedShort()];
			childPositions = new long[childValues.length];
			for (int i = 0; i < childValues.length; i++) {
				AmiHdbIndexNode<T> child = (AmiHdbIndexNode<T>) new AmiHdbIndexNode();
				child.setIndexValue(marshaller.read(in));
				childValues[i] = child;
				childPositions[i] = in.readLong5();
			}
			this.setIndexValue(childValues[0].getIndexValue());
		}

		public long find(AmiHdbIndexNode<T> keyMax, AmiHdbMarshaller<T> marshaller) {
			int idx = AH.indexOfSortedLessThanEqualTo(keyMax, childValues);
			return idx == -1 ? -1 : childPositions[idx];
		}

		@Override
		protected boolean isEmpty() {
			return childValues.length == 0;
		}

	}

	private abstract static class Block<T extends Comparable<?>> extends AmiHdbIndexNode<T> {
		protected abstract boolean isEmpty();

		private long positionOnDisk;

		protected void setPositionOnDisk(long positionOnDisk) {
			this.positionOnDisk = positionOnDisk;
		}
		public long getPositionOnDisk() {
			return this.positionOnDisk;
		}

	}

	public class ForwardsIterator implements Iterator<Entry<T, int[]>> {

		private LeafBlock<T> block;
		private int pos;
		private AmiHdbIndexEntry<T> next;
		private AmiHdbIndexNode<T> max;
		private boolean maxInclusive;
		private AmiHdbMarshaller<T> marshaller;

		public ForwardsIterator(LeafBlock<T> block, int pos, AmiHdbIndexNode<T> max, boolean maxInclusive, AmiHdbMarshaller<T> marshaller) {
			this.block = block;
			this.pos = pos;
			this.max = max;
			this.maxInclusive = maxInclusive;
			this.marshaller = marshaller;
			iterate();
		}

		private void iterate() {
			if (block == null) {
				next = null;
				return;
			}
			next = block.children[pos++];
			if (max != null) {
				if (maxInclusive) {
					if (OH.compare(next, max) > 0) {
						next = null;
						pos = -1;
						block = null;
						return;
					}
				} else {
					if (OH.compare(next, max) >= 0) {
						next = null;
						pos = -1;
						block = null;
						return;
					}
				}
			}
			if (pos == block.children.length) {
				block = (LeafBlock<T>) readBlock(block.getNext());
				pos = block == null ? -1 : 0;
			}
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public AmiHdbIndexEntry<T> next() {
			AmiHdbIndexEntry<T> r = next;
			iterate();
			return r;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public class BackwardsIterator implements Iterator<Entry<T, int[]>> {

		private LeafBlock<T> block;
		private int pos;
		private AmiHdbIndexEntry<T> next;
		final private boolean minInclusive;
		final private AmiHdbMarshaller<T> marshaller;
		final private AmiHdbIndexNode<T> min;

		public BackwardsIterator(LeafBlock<T> block, int pos, AmiHdbIndexNode<T> min, boolean minInclusive, AmiHdbMarshaller<T> marshaller) {
			this.block = block;
			this.pos = pos;
			this.min = min;
			this.minInclusive = minInclusive;
			this.marshaller = marshaller;
			iterate();
		}

		private void iterate() {
			if (block == null) {
				next = null;
				return;
			}
			next = block.children[pos--];
			if (min != null) {
				if (minInclusive) {
					if (OH.compare(next, min) < 0) {
						next = null;
						pos = -1;
						block = null;
						return;
					}
				} else {
					if (OH.compare(next, min) <= 0) {
						next = null;
						pos = -1;
						block = null;
						return;
					}
				}
			}
			if (pos == -1) {
				block = (LeafBlock<T>) readBlock(block.getPrior());
				pos = block == null ? -1 : block.children.length - 1;
			}
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public AmiHdbIndexEntry<T> next() {
			AmiHdbIndexEntry<T> r = next;
			iterate();
			return r;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public AmiHdbMarshaller<T> getMarshaller() {
		return this.marshaller;
	}

	public File getFile() {
		return this.file;
	}

	public String getColumnName() {
		return this.columnName;
	}
	public byte getColumnType() {
		return this.columnType;
	}

	private TreeMap<T, IntArrayList> writableRows;// = new TreeMap<T, IntArrayList>((Comparator) ComparableComparator.INSTANCE);

	public void addValue(T value, int n) {
		value = this.marshaller.cast(value);
		OH.assertNull(this.raf);
		if (writableRows == null)
			return;
		IntArrayList rows = writableRows.get(value);
		if (rows == null) {
			writableRows.put(value, rows = new IntArrayList());
			keysCount++;
		}
		rows.add(n);
	}
	public void removeValue() {
		if (raf != null) {
			IOH.close(raf);
			raf = null;
			file.delete();
		}
		this.writableRows = null;
	}

	public void updateValue(T old, T nuw, int n) {
		nuw = this.marshaller.cast(nuw);
		OH.assertNull(this.raf);
		if (this.writableRows == null)
			return;
		IntArrayList row = this.writableRows.get(old);
		if (row == null)
			throw new IllegalStateException("Index " + describe() + " missing key: " + old);
		int idx = row.indexOf(n);
		if (idx == -1)
			throw new IllegalStateException("Index " + describe() + " Missing row for key: " + old + " ==> " + n);
		row.removeAt(idx);
		if (row.isEmpty()) {
			this.writableRows.remove(old);
			keysCount--;
		}
		addValue(nuw, n);
	}

	public void optimize() {
		if (isInMemory()) {
			writeToDisk();
		}
	}

	private void writeToDisk() {
		OH.assertNull(raf);
		try {
			TreeMap<T, IntArrayList> writeableRows = this.getWriteableRows();
			this.raf = partition.getTable().getFilePool().open(file, "rw", blockSize);
			AmiHdbIndexWriter.write(writeableRows, this.blockSize, this);
			LH.info(log, "Wrote Index to ", IOH.getFullPath(file));
			this.writableRows = null;
		} catch (Exception e) {
			throw handle(e);
		}
	}
	public void ensureInMemory() {
		if (raf == null)
			return;
		IOH.close(raf);
		raf = null;
		buildCachedValuesFromColumnOnDisk();

	}

	protected FastRandomAccessFile getRandomAccessFile() {
		return this.raf;
	}

	public void setRootPosition(long rootPosition, long nullPosition, int keysCount) {
		this.rootPosition = rootPosition;
		this.nullsPosition = nullPosition;
		this.keysCount = keysCount;
	}

	public int getKeysCount() {
		return keysCount;
	}

	public String getIndexName() {
		return this.index.getName();
	}

	public void clearRows() {
		this.cache.clear();
		this.writableRows = null;
		this.rootPosition = -1;
		this.nullsPosition = -1;
		this.keysCount = 0;
		IOH.close(this.raf);
		this.file.delete();
		this.raf = null;
	}
	private AmiHdbException handle(Exception e) {
		if (e instanceof AmiHdbException)
			return (AmiHdbException) e;
		return new AmiHdbException("Critical error with historical index for partition at " + describe(), e);
	}
	public String describe() {
		return IOH.getFullPath(this.file);
	}

	public void close() {
		IOH.close(this.raf);
		this.raf = null;
	}

	public boolean isOptimized() {
		return !isInMemory();
	}
	public boolean isInMemory() {
		return this.raf == null;
	}

	public Iterable<T> getKeys() {
		if (isInMemory())
			return this.getWriteableRows().keySet();
		else {
			List<T> sink = new ArrayList<T>(getKeysCount());
			for (LeafBlock<T> fl = getFistLeaf(); fl != null; fl = (LeafBlock) readBlock(fl.nextPosition))
				for (AmiHdbIndexEntry<T> i : fl.children)
					sink.add(i.getKey());
			return sink;
		}

	}

	public Comparable getMaxKey() {
		if (isInMemory())
			return this.getWriteableRows().lastKey();
		LeafBlock<T> lb = getLastLeaf();
		return AH.last(lb.children).getKey();

	}

	public Comparable getMinKey() {
		if (isInMemory()) {
			return this.getWriteableRows().firstKey();
		} else {
			if (nullsPosition != -1)
				return null;
			LeafBlock<T> lb = getFistLeaf();
			return lb.children[0].getKey();

		}
	}

}
