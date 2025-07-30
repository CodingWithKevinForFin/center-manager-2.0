package com.f1.utils.structs;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class SparseList<T> extends AbstractList<T> {

	private static final Logger log = Logger.getLogger(SparseList.class.getName());
	protected int size;
	final protected int blockSize;
	final private int suggestedSize;
	private Block[] blocks;
	private int blocksLength;
	private Block end, start;
	private Block lastFoundCache = null;
	private int firstDirtyLoc = Integer.MAX_VALUE;

	public SparseList(int expectedSize) {
		this.size = 0;
		this.blockSize = Math.max((int) Math.sqrt(expectedSize), 1000);
		this.suggestedSize = this.blockSize - (this.blockSize / 5);
		this.blocks = new SparseList.Block[8];
	}

	@Override
	public T set(int i, T o) {
		if (i > this.size)
			throw new IndexOutOfBoundsException("index " + i + " is greater than size " + size);
		Block block = findBlockForOffset(i);
		return block.set(i - block.offset, o);
	}

	private Block findBlockForOffset(int i) {
		if (i < start.size())
			return start;
		else if (i >= end.offset)
			return end;
		int top, bot, topLoc, botLoc;
		final SparseList<T>.Block lastFoundCache = this.lastFoundCache;
		if (lastFoundCache == null) {
			top = end.offset;
			topLoc = end.blockNumber;
			bot = start.size();
			botLoc = 1;
		} else if (i < lastFoundCache.offset) {
			top = lastFoundCache.offset;
			topLoc = lastFoundCache.blockNumber;
			bot = start.size();
			botLoc = 1;
		} else if (i >= lastFoundCache.offset + lastFoundCache.size()) {
			top = end.offset;
			topLoc = end.blockNumber;
			bot = lastFoundCache.offset + lastFoundCache.size();
			botLoc = lastFoundCache.blockNumber + 1;
		} else
			return lastFoundCache;
		long n = i;
		for (;;) {
			final int guess = (int) ((botLoc + ((n - bot) * (topLoc - botLoc) / (top - bot))));
			final Block block = getBlock(guess);
			final int blockOffset = block.offset;
			if (blockOffset > i) {
				top = blockOffset;
				topLoc = guess - 1;
			} else {
				final int blockEnd = blockOffset + block.size();
				if (blockEnd <= i) {
					bot = blockEnd;
					botLoc = guess + 1;
				} else {
					this.lastFoundCache = block;//TODO: handle multi-threaded reads
					return block;
				}
			}
		}
	}

	public boolean addAll(Iterable<? extends T> c) {
		boolean modified = false;
		for (T e : c)
			if (add(e))
				modified = true;
		return modified;
	}

	@Override
	public void clear() {
		for (int i = blocksLength - 1; i >= 0; i--) {
			blocks[i].clear();
			blocks[i] = null;
		}
		this.size = 0;
		this.lastFoundCache = null;
		blocksLength = 0;
		this.firstDirtyLoc = Integer.MAX_VALUE;
		this.end = null;
	}
	@Override
	public T remove(int i) {
		Block block = findBlockForOffset(i);
		int loc = block.blockNumber;
		if (block.size() == 1) {
			T r = (T) block.get(0);
			size--;
			if (size == 0)
				this.firstDirtyLoc = Integer.MAX_VALUE;
			else
				markDirtyAt(loc + 1, 1, true);
			removeBlock(loc);
			return r;
		} else {
			T r = block.remove(i - block.offset);
			size--;
			markDirtyAt(loc + 1, 1, true);
			return r;
		}
	}

	public void removeAll(int start, int end) {
		while (start < end)
			end -= removeAllInner(start, end);
	}
	private int removeAllInner(int start, int end) {
		Block block = findBlockForOffset(start);
		int loc = block.blockNumber;
		if (block.size() <= end - start && block.getOffset() == start) {
			int r = block.size();
			size -= r;
			if (size == 0)
				this.firstDirtyLoc = Integer.MAX_VALUE;
			else
				markDirtyAt(loc + 1, r, true);
			removeBlock(loc);
			return r;
		} else {
			int r = block.removeAll(start - block.offset, end - block.offset);
			size -= r;
			markDirtyAt(loc + 1, r, true);
			return r;
		}
	}

	public T get(int i) {
		if (i >= size)
			throw new IndexOutOfBoundsException("index >= size: " + i + " >= " + size);
		Block n = findBlockForOffset(i);
		return n.get(i - n.offset);
	}
	public void getAll(int start, int end, T[] sink, int destOffset) {
		while (start < end) {
			int n = getAllInner(start, end, sink, destOffset);
			start += n;
			destOffset += n;
		}
	}

	public int getAllInner(int start, int end, T[] sink, int destOffset) {
		Block n = findBlockForOffset(start);
		return n.getAll(start - n.offset, end - n.offset, sink, destOffset);
	}

	public void add(int index, T data) {
		if (index > size)
			throw new IndexOutOfBoundsException("index " + index + " is greater than size " + size);
		if (index == size)
			add(data);
		else {
			Block n = findBlockForOffset(index);
			int loc = n.blockNumber;
			int offset = index - n.offset;
			if (n.isFull()) {
				int split = n.size() / 2;
				int copySize = n.size() - split;
				Block newBlock = newBlock(loc + 1, n.offset + split, n, split, copySize);
				while (n.size() > split)
					n.remove(n.size() - 1);
				insertBlock(loc + 1, newBlock);
				if (offset > split) {
					newBlock.add(offset - split, data);
					markDirtyAt(loc + 2, 1, false);
				} else {
					n.add(offset, data);
					markDirtyAt(loc + 1, 1, false);
				}
			} else {
				n.add(offset, data);
				markDirtyAt(loc + 1, 1, false);
			}
			size++;
		}
	}

	public boolean add(T data) {
		if (blocksLength == 0) {
			start = end = newBlock(0, 0, null, 0, 0);
			blocks[0] = end;
			blocksLength++;
		} else {
			if (end.size() >= suggestedSize) {
				end = newBlock(blocksLength, size, null, 0, 0);
				if (blocks.length == blocksLength + 1)
					blocks = Arrays.copyOf(blocks, blocks.length << 1);
				blocks[blocksLength++] = end;
			}
		}
		end.add(data);
		size++;
		return true;
	}

	public void addAll(int index, T[] data, int dataStart, int dataEnd) {
		if (index > size)
			throw new IndexOutOfBoundsException("index " + index + " is greater than size " + size);
		OH.assertGe(index, 0);
		while (dataStart < dataEnd) {
			int n = addAllInner(index, data, dataStart, dataEnd);
			dataStart += n;
			index += n;
		}
	}

	private int addAllInner(int index, T[] data, int dataStart, int dataEnd) {
		if (index == size)
			return addAllInner(data, dataStart, dataEnd);
		else {
			Block n = findBlockForOffset(index);
			int loc = n.blockNumber;
			int offset = index - n.getOffset();
			if (offset < 0)
				throw new RuntimeException("index=" + index + ", offset=" + n.getOffset());
			int r;
			if (n.isFull()) {
				int split = n.size() / 2;
				int copySize = n.size() - split;
				Block newBlock = newBlock(loc + 1, n.offset + split, n, split, copySize);
				while (n.size() > split)
					n.remove(n.size() - 1);
				insertBlock(loc + 1, newBlock);
				if (offset > split) {
					r = newBlock.addAll(offset - split, data, dataStart, dataEnd);
					markDirtyAt(loc + 2, r, false);
				} else {
					r = n.addAll(offset, data, dataStart, dataEnd);
					markDirtyAt(loc + 1, r, false);
				}
			} else {
				r = n.addAll(offset, data, dataStart, dataEnd);
				markDirtyAt(loc + 1, r, false);
			}
			size += r;
			return r;
		}
	}

	private int addAllInner(T[] data, int dataStart, int dataEnd) {
		if (blocksLength == 0) {
			start = end = newBlock(0, 0, null, 0, 0);
			blocks[0] = end;
			blocksLength++;
		} else {
			if (end.size() >= suggestedSize) {
				end = newBlock(blocksLength, size, null, 0, 0);
				if (blocks.length == blocksLength + 1)
					blocks = Arrays.copyOf(blocks, blocks.length << 1);
				blocks[blocksLength++] = end;
			}
		}
		int size = end.addAll(end.nodesSize, data, dataStart, dataEnd);
		this.size += size;
		return size;
	}

	protected Block newBlock(int blockNumber, int offset, Block n, int dataOffset, int dataLength) {
		return new Block(blockNumber, offset, n, dataOffset, dataLength);
	}

	public int size() {
		return size;
	}

	public class Block {

		protected T[] nodes;
		protected int blockNumber;
		protected int nodesSize;
		protected int offset;

		public void clear() {
			while (nodesSize > 0) {
				nodes[--nodesSize] = null;
			}
		}
		public int getAll(int start, int end, Object[] sink, int destOffset) {
			int r = Math.min(end, this.nodesSize) - start;
			AH.arraycopy(nodes, start, sink, destOffset, r);
			return r;
		}
		public T set(int i, T o) {
			T r = (T) nodes[i];
			nodes[i] = o;
			return r;
		}

		protected T remove(int i) {
			if (i >= nodesSize)
				throw new IndexOutOfBoundsException(SH.toString(i));
			T r = (T) nodes[i];
			int toMove = nodesSize - i - 1;
			if (toMove > 0)
				AH.arraycopy(nodes, i + 1, nodes, i, toMove);

			nodes[--nodesSize] = null;
			return r;
		}
		protected int removeAll(int start, int end) {
			if (start >= nodesSize)
				throw new IndexOutOfBoundsException(SH.toString(start));
			end = Math.min(end, this.nodesSize);
			int r = end - start;
			int toMove = nodesSize - end;
			if (toMove > 0)
				AH.arraycopy(nodes, start + r, nodes, start, toMove);
			for (int i = 0; i < r; i++)
				nodes[--nodesSize] = null;
			return r;
		}

		public boolean isFull() {
			return size() >= blockSize;
		}

		public void add(int i, T data) {
			ensureAdditionalCapacity(1);
			if (nodesSize > i)
				AH.arraycopy(nodes, i, nodes, i + 1, nodesSize - i);
			nodesSize++;
			nodes[i] = data;
		}
		public void ensureAdditionalCapacity(int i) {
			if (this.nodes.length < nodesSize + i)
				this.nodes = Arrays.copyOf(this.nodes, SparseList.this.blockSize);
		}
		public int addAll(int i, Object[] data, int dataStart, int dataEnd) {
			int r = (dataEnd - dataStart);
			if (this.nodes.length < nodesSize + r) {
				this.nodes = Arrays.copyOf(this.nodes, SparseList.this.blockSize);
				r = Math.min(this.nodes.length - nodesSize, r);
			}
			if (this.nodesSize > i)
				AH.arraycopy(nodes, i, nodes, i + r, nodesSize - i);
			AH.arraycopy(data, dataStart, this.nodes, i, r);
			this.nodesSize += r;
			return r;
		}
		public void add(T data) {
			ensureAdditionalCapacity(1);
			nodes[nodesSize++] = data;
		}

		public T get(int loc) {
			return (T) nodes[loc];
		}
		protected Block(int blockNumber, int offset, Block data, int dataOffset, int dataLength) {
			this.blockNumber = blockNumber;
			this.offset = offset;
			nodes = (T[]) new Object[Math.max(SparseList.this.size == 0 ? 10 : blockSize, dataLength)];
			if (dataLength > 0) {
				this.nodesSize = dataLength;
				AH.arraycopy(data.nodes, dataOffset, this.nodes, 0, dataLength);
			}
		}
		public boolean contains(Object o) {
			return AH.contains(o, nodes, 0, nodesSize);
		}
		public int size() {
			return nodesSize;
		}

		public int getOffset() {
			ensureNotDirty(this.blockNumber);
			return offset;
		}
		protected void assertCorrect() {
		}

	}

	@Override
	public Iterator<T> iterator() {
		return new SkipListIterator();
	}

	@Override
	public boolean contains(Object o) {
		for (Block b : blocks)
			if (b.contains(o))
				return true;
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean r = false;
		for (Object o : c) {
			T t = (T) o;
			if (remove(t))
				r = true;
		}
		return r;
	}

	@Override
	public boolean remove(Object o) {
		int i = indexOf(o);
		if (i == -1)
			return false;
		remove(i);
		return true;
	}
	protected Block getBlock(int loc) {
		ensureNotDirty(loc);
		return blocks[loc];
	}
	protected void ensureNotDirty(int loc) {
		int firstDirtyLoc = this.firstDirtyLoc;
		if (loc < firstDirtyLoc)
			return;
		Block last;
		if (firstDirtyLoc == 0) {
			last = blocks[0];
			last.offset = 0;
			firstDirtyLoc = firstDirtyLoc + 1;
		} else
			last = blocks[firstDirtyLoc - 1];
		while (loc >= firstDirtyLoc) {
			Block b = blocks[firstDirtyLoc];
			b.offset = last.offset + last.size();
			firstDirtyLoc = firstDirtyLoc + 1;
			last = b;
		}
		this.firstDirtyLoc = firstDirtyLoc;
	}
	private void removeBlock(int loc) {

		Block removed = blocks[loc];
		removed.clear();
		AH.arraycopy(blocks, loc + 1, blocks, loc, blocksLength - loc - 1);
		blocks[--blocksLength] = null;
		int bsize = blocksLength;
		if (bsize == 0) {
			lastFoundCache = start = end = null;
			firstDirtyLoc = Integer.MAX_VALUE;
			return;
		}
		if (start == removed) {
			start = blocks[0];
			start.offset = 0;
		}
		if (end == removed) {
			end = blocks[bsize - 1];
			end.offset = size - end.size();
		}
		if (lastFoundCache == removed)
			lastFoundCache = null;
		for (int n = loc; n < bsize; n++)
			blocks[n].blockNumber = n;
		if (firstDirtyLoc > loc && firstDirtyLoc != Integer.MAX_VALUE)
			firstDirtyLoc--;
	}
	private void insertBlock(int i, Block newBlock) {
		if (blocks.length == blocksLength + 1)
			blocks = Arrays.copyOf(blocks, blocks.length << 1);
		AH.arraycopy(blocks, i, blocks, i + 1, blocksLength - i);
		blocks[i] = newBlock;
		blocksLength++;
		if (i == 0)
			start = newBlock;
		if (i == blocksLength - 1)
			end = newBlock;
		for (int n = i + 1; n < blocksLength; n++)
			blocks[n].blockNumber = n;
		if (firstDirtyLoc >= i && firstDirtyLoc != Integer.MAX_VALUE)
			firstDirtyLoc++;
	}
	private void markDirtyAt(int loc, int count, boolean isDelete) {
		if (loc < this.firstDirtyLoc)
			this.firstDirtyLoc = loc;
		if (blocksLength > loc) {
			if (isDelete) {
				end.offset -= count;
				if (this.lastFoundCache != null && end != lastFoundCache && this.lastFoundCache.blockNumber >= loc)
					this.lastFoundCache.offset -= count;
			} else {
				end.offset += count;
				if (this.lastFoundCache != null && end != lastFoundCache && this.lastFoundCache.blockNumber >= loc)
					this.lastFoundCache.offset += count;
			}
		}

	}

	public void assertCorrect() {
		int expected = 0;
		for (int i = 0; i < this.blocksLength; i++) {
			Block b = this.blocks[i];
			OH.assertEq(b.blockNumber, i);
			if (b == this.start || b == this.end || b.blockNumber < this.firstDirtyLoc)
				OH.assertEq(b.offset, expected, " at " + i);
			b.assertCorrect();
			expected += b.size();

		}
	}

	private class SkipListIterator implements Iterator<T> {

		private Block block = blocksLength == 0 ? null : blocks[0];
		private int offsetPos = 0;

		@Override
		public boolean hasNext() {
			return block != null;
		}

		@Override
		public T next() {
			T r = (T) block.get(offsetPos++);
			while (offsetPos == block.size()) {
				offsetPos = 0;
				if (block.blockNumber + 1 == blocksLength) {
					block = null;
					break;
				} else
					block = blocks[block.blockNumber + 1];
			}
			return r;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
