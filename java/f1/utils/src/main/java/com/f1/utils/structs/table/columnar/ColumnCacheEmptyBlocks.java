package com.f1.utils.structs.table.columnar;

import java.util.logging.Logger;

import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.structs.LongKeyMap;

/**
 * Each EmptyBlock represents a region of unused disk space. As blocks are added, checks are done to see and merge wit adjacent blocks free blocks.
 * <P>
 * When a free block is requested by size, an approximation is used to find a block big enough. See {@link #getLowerBoundSizeBucket(int)}
 * 
 * @author rcooke
 * 
 */
public class ColumnCacheEmptyBlocks {
	private static final Logger log = LH.get();

	final private LongKeyMap<EmptyBlock> emptyBlocks = new LongKeyMap<EmptyBlock>();

	public static class EmptyBlock {
		public EmptyBlock(long start, long end) {
			this.start = start;
			this.end = end;
		}

		private int largeBlocksubspaceRemaining = -1;//only used for large blocks, those over 2^31
		long start;
		long end;

		public long size() {
			return (end - start);
		}

		private EmptyBlock tail, head;

		public void insertAfter(EmptyBlock older) {
			older.head = this;
			older.tail = this.tail;
			if (this.tail != null)
				this.tail.head = older;
			this.tail = older;
		}
		public void remove() {
			if (this.head != null)
				this.head.tail = tail;
			if (this.tail != null)
				this.tail.head = head;
			this.head = this.tail = null;
		}

		@Override
		public String toString() {
			return start + " - " + end;
		}
		public int getLargeBlockSubspaceRemaining() {
			return this.largeBlocksubspaceRemaining;
		}
		public void setLargeBlockSubspaceRemaining(int lbCheckpoint) {
			this.largeBlocksubspaceRemaining = lbCheckpoint;
		}
	}

	private int minBucket = -1;
	private int maxBucket = -1;
	private EmptyBlock[] buckets = new EmptyBlock[getUpperBoundSizeBucket(0xFFFFFFFFFFL) + 1];//1TB
	private int size = 0;

	public EmptyBlock add(long start, long end) {
		EmptyBlock sblock = this.emptyBlocks.get(start);
		EmptyBlock eblock = this.emptyBlocks.get(end);
		final EmptyBlock block;
		if (sblock == null && eblock == null) {
			block = new EmptyBlock(start, end);
			this.emptyBlocks.put(block.start, block);
			this.emptyBlocks.put(block.end, block);
			addToBuckets(block);
		} else if (sblock != null && eblock != null) {
			removeFromBuckets(eblock);
			removeFromBuckets(sblock);
			this.emptyBlocks.remove(eblock.start);
			this.emptyBlocks.remove(eblock.end);
			this.emptyBlocks.remove(sblock.end);
			sblock.end = eblock.end;
			this.emptyBlocks.put(sblock.end, sblock);
			addToBuckets(sblock);
			block = sblock;
		} else if (sblock != null) {
			block = sblock;
			if (sblock.end < end) {
				removeFromBuckets(block);
				this.emptyBlocks.remove(block.end);
				block.end = end;
				this.emptyBlocks.put(block.end, block);
				addToBuckets(block);
			}
		} else {
			block = eblock;
			if (eblock.start > start) {
				removeFromBuckets(block);
				this.emptyBlocks.remove(block.start);
				block.start = start;
				block.largeBlocksubspaceRemaining = -1;
				this.emptyBlocks.put(block.start, block);
				addToBuckets(block);
			}
		}
		return block;
	}

	public EmptyBlock findGe(int size) {
		if (this.size == 0)
			return null;
		int bucket = getUpperBoundSizeBucket(size);
		if (bucket > this.maxBucket)
			return null;
		if (bucket < this.minBucket)
			return this.buckets[minBucket];
		int cnt = 0;
		for (;;) {
			EmptyBlock entry = this.buckets[bucket++];
			if (entry != null)
				return entry;
			if (++cnt == FIND_CHECK)
				return null;
		}

	}
	public EmptyBlock findEq(int size) {
		if (this.size == 0)
			return null;
		int bucket = getLowerBoundSizeBucket(size);
		int cnt = 0;
		for (EmptyBlock entry = this.buckets[bucket]; entry != null; entry = entry.tail) {
			if (entry.size() == size)
				return entry;
			if (++cnt == FIND_CHECK)
				break;
		}
		return null;

	}
	public void clear() {
		this.emptyBlocks.clear();
		AH.fill(this.buckets, null);
		this.minBucket = this.maxBucket = -1;
		this.size = 0;
	}
	private void addToBuckets(EmptyBlock b) {
		OH.assertGt(b.end, b.start);
		int bucket = getLowerBoundSizeBucket(b.size());
		this.size++;
		if (size == 1) {
			this.minBucket = this.maxBucket = bucket;
			this.buckets[bucket] = b;
		} else {
			if (bucket < this.minBucket)
				this.minBucket = bucket;
			else if (bucket > this.maxBucket)
				this.maxBucket = bucket;
			EmptyBlock r = this.buckets[bucket];
			if (r == null) {
				this.buckets[bucket] = b;
			} else
				r.insertAfter(b);
		}

	}
	private void removeFromBuckets(EmptyBlock b) {
		int bucket = getLowerBoundSizeBucket(b.size());
		this.size--;
		OH.assertGe(size, 0);
		if (b.head == null) {
			this.buckets[bucket] = b.tail;
			if (size > 0) {
				if (b.tail == null) {
					if (bucket == this.minBucket)
						for (;;)
							if (this.buckets[++this.minBucket] != null)
								break;
					if (bucket == this.maxBucket)
						for (;;)
							if (this.buckets[--this.maxBucket] != null)
								break;
				}
			} else
				this.minBucket = this.maxBucket = -1;
		}
		b.remove();
	}
	public void updateStart(EmptyBlock block, long start) {
		this.removeFromBuckets(block);
		this.emptyBlocks.remove(block.start);
		block.start = start;
		this.emptyBlocks.put(block.start, block);
		this.addToBuckets(block);
	}

	private static int getUpperBoundSizeBucket(long n) {
		return getLowerBoundSizeBucket(n - 1) + 1;
	}

	private static final int SIZE = 5;
	private static final int FIND_CHECK = 64;

	/**
	 * returns the bucket that holds blocks on n size. The bucket is determined by looking at only the the most significant bits (as specified by SIZE). This scheme allows for
	 * Logarithmic distribution, while not requiring a huge array<BR>
	 * For example, consider the number: 00101101, this would fall into the 00100000 bucket (because the most significant 2 digits are bits 4 and 3 which are 10. And in fact, all
	 * numbers of the form 0010xxxx would fall into this bucket. Another example would all number like 011xxxxx would fall into the same bucket
	 * 
	 * @param n
	 * @return
	 */

	private static int getLowerBoundSizeBucket(long n) {
		if (n <= (1 << SIZE) - 1)
			return (int) n;
		final int bit = 63 - Long.numberOfLeadingZeros(n) - SIZE;
		return (int) (bit * (1 << SIZE) + (n >> bit));
	}

	public void remove(EmptyBlock block) {
		this.emptyBlocks.remove(block.start);
		this.emptyBlocks.remove(block.end);
		this.removeFromBuckets(block);
	}

	public int getEmptyBlocksCount() {
		return this.size;
	}

	public static void main(String a[]) {
		System.out.println(getUpperBoundSizeBucket(0xFFFFFFFFFFL) + 1);
		int last = -1;
		int lastI = 0;
		for (int i = 0; i < 1000000; i++) {
			int t = getLowerBoundSizeBucket(i);
			if (t != last) {
				System.out.println(i + " ==> " + t + " (" + (i - lastI) + ")");
				last = t;
				lastI = i;

			}
		}

	}

	/*
	public void ensureBlockAt(long start, long end) {
		EmptyBlock existing = this.emptyBlocks.get(start);
		if (existing == null)
			throw new RuntimeException("block start not found at " + start);
		EmptyBlock existing2 = this.emptyBlocks.get(end);
		if (existing2 == null)
			throw new RuntimeException("block end  found at " + end);
		if (existing.start != start || existing.end != end)
			throw new RuntimeException("block inconcistent at " + start + " - " + end);
	}
	
	public void ensureNoBlockAt(long pointer) {
		EmptyBlock existing = this.emptyBlocks.get(pointer);
		if (existing != null && existing.end != pointer)
			throw new RuntimeException("Empty block at " + pointer + " " + existing);
	}
	
	public void ensureValid() {
		OH.assertEq(size, this.emptyBlocks.size() / 2);
		for (EmptyBlock eb : this.emptyBlocks.values()) {
			OH.assertFalse(eb.isRemoved);
		}
		for (EmptyBlock eb : this.buckets) {
			for (; eb != null; eb = eb.tail) {
				OH.assertFalse(eb.isRemoved);
				OH.assertEqIdentity(eb, this.emptyBlocks.get(eb.start));
				OH.assertEqIdentity(eb, this.emptyBlocks.get(eb.end));
			}
		}
	}
	public IterableAndSize<EmptyBlock> getEmptyBlocks() {
		return this.emptyBlocks.values();
	}
	public void debug() {
		if (true)
			return;
		LH.info(log, "---" + this.size + " block(s) ranging buckets: " + this.minBucket + " - " + this.maxBucket);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.buckets.length; i++) {
			EmptyBlock e = this.buckets[i];
			if (e == null)
				continue;
			sb.setLength(0);
			sb.append("Bucket: ").append(i);
			OH.assertNull(e.head);
			for (; e != null; e = e.tail) {
				OH.assertEq(i, getLowerBoundSizeBucket(e.size()));
				sb.append(" (").append(e.start).append(" @ ").append(e.size()).append(" bytes)");
			}
			LH.info(log, "     ", sb);
		}
	}
	*/
}
