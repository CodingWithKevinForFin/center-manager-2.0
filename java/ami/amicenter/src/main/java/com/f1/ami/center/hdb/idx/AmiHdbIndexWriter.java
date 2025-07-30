package com.f1.ami.center.hdb.idx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.f1.ami.center.hdb.AmiHdbUtils;
import com.f1.ami.center.hdb.col.AmiHdbMarshaller;
import com.f1.ami.center.hdb.col.AmiHdbMarshallerFixedSize;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.FastDataOutput;
import com.f1.utils.FastRandomAccessFile;
import com.f1.utils.IntArrayList;
import com.f1.utils.OH;

public class AmiHdbIndexWriter {

	public static <T extends Comparable<?>> void write(TreeMap<T, IntArrayList> writableRows, int blockSize, AmiHdbPartitionIndex<T> index) throws IOException {
		AmiHdbMarshaller<T> marshaller = index.getMarshaller();
		List<Block<T>> blocksByLevel = new ArrayList<Block<T>>();
		List<AmiHdbIndexEntry<T>> entriesBuf = new ArrayList<AmiHdbIndexEntry<T>>();
		List<Block<T>> blocksBuf = new ArrayList<Block<T>>();
		IntArrayList nullRows = null;
		if (!writableRows.isEmpty()) {
			LeafBlock<T> leaf = new LeafBlock<T>(writableRows.firstEntry().getKey());
			blocksByLevel.add(leaf);
			for (Map.Entry<T, IntArrayList> e : writableRows.entrySet()) {
				if (e.getKey() == null) {
					nullRows = e.getValue();
					continue;
				}
				AmiHdbIndexEntry<T> i = new AmiHdbIndexEntry<T>(e.getKey(), e.getValue().toIntArray());
				if (!leaf.addEntry(i, entriesBuf, blockSize, marshaller)) {
					leaf.setChildren(entriesBuf);
					entriesBuf.clear();
					LeafBlock<T> nextChild = new LeafBlock<T>(i.getIndexValue());
					leaf.setNext(nextChild);
					leaf = nextChild;
					OH.assertTrue(leaf.addEntry(i, entriesBuf, blockSize, marshaller));
				}
			}
			leaf.setChildren(entriesBuf);
			entriesBuf.clear();
			for (;;) {
				Block<T> child = CH.last(blocksByLevel);
				if (child.getNext() == null)//The top level has more than one block so we need another level
					break;
				ParentBlock<T> parent = new ParentBlock<T>(child.getIndexValue());
				blocksByLevel.add(parent);
				for (Block<T> b = child; b != null; b = b.getNext()) {
					if (!parent.addEntry(b, blocksBuf, blockSize, marshaller)) {
						parent.setChildren(blocksBuf);
						blocksBuf.clear();
						ParentBlock<T> nextParent = new ParentBlock<T>(b.getIndexValue());
						parent.setNext(nextParent);
						parent = nextParent;
						OH.assertTrue(parent.addEntry(b, blocksBuf, blockSize, marshaller));
					}
				}
				parent.setChildren(blocksBuf);
				blocksBuf.clear();
			}
		}
		FastRandomAccessFile raf = index.getRandomAccessFile();///new FastRandomAccessFile(index.getFile(), "rw", 8192);
		FastDataOutput out = raf.getOutput();
		raf.setLength(0);
		raf.seek(0);
		AmiHdbUtils.writeHeader(out, AmiHdbUtils.HEADER_INDEX, 1);
		out.writeByte('\n');
		AmiHdbUtils.writeType(out, index.getColumnType());
		out.writeByte('\n');
		out.writeUTF(index.getColumnName());
		long headerPos = raf.getPosition();
		out.writeLong5(-1);//placeholder for root
		out.writeLong5(-1);//placeholder for nullValues
		out.writeInt(-1);//placeholder for KeysCount
		LeafBlock<T> nullsLeaf = null;
		Block<T> root = CH.last(blocksByLevel);
		if (CH.isntEmpty(nullRows)) {
			nullsLeaf = new LeafBlock<T>(null);
			AmiHdbIndexEntry<T> t = new AmiHdbIndexEntry<T>(null, nullRows.toIntArray());
			nullsLeaf.addEntry(t, entriesBuf, blockSize, marshaller);
			nullsLeaf.setChildren(entriesBuf);
			entriesBuf.clear();
			Block<T> old = blocksByLevel.set(0, nullsLeaf);
			if (old != null)
				nullsLeaf.setNext(old);
		}
		for (int n = 0; n < blocksByLevel.size(); n++) {
			for (Block<T> i = blocksByLevel.get(n); i != null; i = i.getNext()) {
				i.setPositionOnDisk(raf.getPosition());
				i.write(out, marshaller);
			}
		}
		raf.seek(headerPos);
		final long rootPosition = writableRows.isEmpty() ? -1 : root.getPositionOnDisk();
		final long nullsPosition = nullsLeaf == null ? -1 : nullsLeaf.getPositionOnDisk();
		final int keysCount = writableRows.size();
		out.writeLong5(rootPosition);
		out.writeLong5(nullsPosition);
		out.writeInt(keysCount);
		out.flush();
		index.setRootPosition(rootPosition, nullsPosition, keysCount);
	}

	static private class LeafBlock<T extends Comparable<?>> extends Block<T> {
		public LeafBlock(T value) {
			super(value);
		}

		AmiHdbIndexEntry<T>[] children;
		long sizeOnDisk = 1 + 2 + 5;//MASK[LEAF|HASNEXT],SHORT - Number of Entries,Long5 - PRIOR OFFSET
		private LeafBlock<T> prior;

		public boolean addEntry(AmiHdbIndexEntry<T> i, List<AmiHdbIndexEntry<T>> buffer, int blockSize, AmiHdbMarshaller<T> marshaller) {
			int sz = marshaller.getSize(i.getIndexValue()) + 4 + i.rows.length * AmiHdbPartitionIndex.POINTER_SIZE;
			if (buffer.size() > 0 && sizeOnDisk + sz > blockSize)
				return false;
			buffer.add(i);
			sizeOnDisk += sz;
			return true;
		}
		public void setChildren(List<AmiHdbIndexEntry<T>> buffer) {
			this.children = AH.toArray((List) buffer, AmiHdbIndexEntry.class);
		}
		@Override
		long getSizeOnDisk() {
			return sizeOnDisk;
		}
		@Override
		protected AmiHdbIndexEntry<T> getFirstEntry() {
			return children[0];
		}
		@Override
		protected void write(FastDataOutput out, AmiHdbMarshaller<T> marshaller) throws IOException {
			if (getNext() != null)
				out.writeByte(1 | 2);
			else
				out.writeByte(1);
			out.writeShort(children.length);
			out.writeLong5(prior == null ? -1L : prior.getPositionOnDisk());
			for (AmiHdbIndexEntry<T> entry : children) {
				writeEntry(marshaller, entry.getIndexValue(), out);
				out.writeInt(entry.rows.length);
				for (int l : entry.rows)
					out.writeInt(l);
			}
		}

		@Override
		public void setNext(Block<T> next) {
			super.setNext(next);
			getNext().prior = this;
		}

		@Override
		public LeafBlock<T> getNext() {
			return (LeafBlock<T>) super.next;
		}
		public LeafBlock<T> getPrior() {
			return prior;
		}
		@Override
		protected boolean isEmpty() {
			return children.length == 0;
		}
	}

	private static <T extends Comparable> void writeEntry(AmiHdbMarshaller<T> marshaller, T value, FastDataOutput out) throws IOException {
		if (value == null && marshaller instanceof AmiHdbMarshallerFixedSize)
			marshaller.write(out, ((AmiHdbMarshallerFixedSize<T>) marshaller).minValue());//this is just a place holder, when we read in it will be forced to null
		else
			marshaller.write(out, value);
	}

	static public class ParentBlock<T extends Comparable<?>> extends Block<T> {
		public ParentBlock(T value) {
			super(value);
		}

		Block<T>[] children;
		Block<T>[] childrenArray;
		long sizeOnDisk = 3;//MAS[PARENT|HASNEXT],SHORT - Number of Entries

		public boolean addEntry(Block<T> b, List<Block<T>> blocksBuf, int blockSize, AmiHdbMarshaller<T> marshaller) {
			int sz = marshaller.getSize(b.getFirstEntry().getIndexValue()) + AmiHdbPartitionIndex.POINTER_SIZE;
			if (sizeOnDisk + sz > blockSize && blocksBuf.size() >= 4)
				return false;
			blocksBuf.add(b);
			sizeOnDisk += sz;
			return true;
		}

		public void setChildren(List<Block<T>> blocksBuf) {
			this.children = (Block[]) AH.toArray((List) blocksBuf, Block.class);
		}

		@Override
		long getSizeOnDisk() {
			return sizeOnDisk;
		}

		@Override
		protected AmiHdbIndexEntry<T> getFirstEntry() {
			return children[0].getFirstEntry();
		}

		@Override
		protected void write(FastDataOutput out, AmiHdbMarshaller<T> marshaller) throws IOException {
			if (getNext() != null)
				out.writeByte(0 | 2);
			else
				out.writeByte(0);
			out.writeShort(children.length);
			for (Block<T> child : children) {
				writeEntry(marshaller, child.getFirstEntry().getIndexValue(), out);
				out.writeLong5(child.positionOnDisk);
			}
		}

		@Override
		protected boolean isEmpty() {
			return children.length == 0;
		}

	}

	private abstract static class Block<T extends Comparable<?>> extends AmiHdbIndexNode<T> {
		public Block(T value) {
			setIndexValue(value);
		}

		protected abstract boolean isEmpty();

		private Block<T> next;
		private long positionOnDisk;

		protected abstract void write(FastDataOutput out, AmiHdbMarshaller<T> marshaller) throws IOException;

		protected abstract AmiHdbIndexEntry<T> getFirstEntry();

		abstract long getSizeOnDisk();

		protected void setPositionOnDisk(long positionOnDisk) {
			this.positionOnDisk = positionOnDisk;
		}

		public long getPositionOnDisk() {
			return this.positionOnDisk;
		}

		public void setNext(Block<T> next) {
			this.next = next;
		}

		public Block<T> getNext() {
			return this.next;
		}

	}

}
