package com.f1.utils.structs;

import com.f1.utils.OH;

public class SkipList<T extends SkipListEntry> extends SparseList<T> {

	public SkipList(int expectedSize) {
		super(expectedSize);
	}

	@Override
	protected SparseList<T>.Block newBlock(int blockNumber, int offset, SparseList<T>.Block n, int dataOffset, int dataLength) {
		return new Block(blockNumber, offset, n, dataOffset, dataLength);
	};

	public class Block extends SparseList<T>.Block {

		private int firstDirtyLeafLoc = Integer.MAX_VALUE;

		@Override
		public void clear() {
			for (int i = 0; i < size(); i++)
				get(i).setParentToNull(this);
			super.clear();
		}
		@Override
		public T set(int i, T o) {
			T r = super.set(i, o);
			o.setParent(this, i);
			return r;
		}
		@Override
		protected int removeAll(int start, int end) {
			end = Math.min(end, this.nodesSize);
			for (int i = start; i < end; i++) {
				T t = get(i);
				if (t.getBlock() == this)
					t.setParentToNull(this);
			}
			if (firstDirtyLeafLoc > start)
				firstDirtyLeafLoc = start;
			return super.removeAll(start, end);
		}

		@Override
		public int addAll(int i, Object[] data, int dataStart, int dataEnd) {
			int r = super.addAll(i, data, dataStart, dataEnd);
			for (int j = i; j < i + r; j++) {
				T t = get(j);
				t.setParent(this, j);
			}
			if (firstDirtyLeafLoc > i)
				firstDirtyLeafLoc = i;
			return r;
		}

		@Override
		public T remove(int i) {
			T r = super.remove(i);
			if (r.getBlock() == this)
				r.setParentToNull(this);
			if (firstDirtyLeafLoc > i)
				firstDirtyLeafLoc = i;
			return r;
		}

		@Override
		public void add(int i, T data) {
			super.add(i, data);
			data.setParent(this, i);
			if (firstDirtyLeafLoc > i)
				firstDirtyLeafLoc = i;
		}
		@Override
		public void add(T data) {
			data.setParent(this, size());
			super.add(data);
		}

		protected Block(int blockNumber, int offset, SparseList<T>.Block data, int dataOffset, int dataLength) {
			super(blockNumber, offset, data, dataOffset, dataLength);
			for (int i = 0; i < dataLength; i++)
				((T) get(i)).setParent(this, i);
		}

		void ensureOffsetOkay(SkipListEntry leaf) {
			int fdl = this.firstDirtyLeafLoc;
			if (leaf.offset < fdl)
				return;
			for (int i = fdl; i < size(); i++) {
				T t = get(i);
				t.setOffset(i);
			}
			firstDirtyLeafLoc = Integer.MAX_VALUE - 2;
		}

		public SkipList getSkipList() {
			return SkipList.this;
		}

		protected void assertCorrect() {
			Object[] n = this.nodes;
			for (int i = 0; i < n.length; i++) {
				T t = (T) n[i];
				if (i < this.nodesSize)
					OH.assertEqIdentity(t.getBlock(), this);
				else
					OH.assertNull(t);
			}
		}
	}

	@Override
	public int indexOf(Object o) {
		if (o instanceof SkipListEntry) {
			SkipListEntry sle = (SkipListEntry) o;
			if (sle.getSkipList() == this)
				return sle.getLocation();
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return indexOf(o);
	}

}
