package com.f1.ami.center.table.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.Duration;
import com.f1.utils.EH;
import com.f1.utils.LongArrayList;
import com.f1.utils.OH;

final public class AmiIndexMap_Series implements AmiIndexMap {

	private int size = 0;
	private int deleted = 0;
	private long[] keys = new long[8];
	private AmiIndexMap[] values = new AmiIndexMap[8];
	private long max = Long.MIN_VALUE;

	@Override
	public long getMemorySize() {
		long r = 16 + 2 * EH.ADDRESS_SIZE;
		r += keys.length * 8 + EH.ESTIMATED_GC_OVERHEAD;
		r += values.length * EH.ADDRESS_SIZE + EH.ESTIMATED_GC_OVERHEAD;
		for (AmiIndexMap i : values)
			if (i != null)
				r += i.getMemorySize();
		return r;
	}
	@Override
	public void removeIndex(Comparable key) {
		//TODO: think about max value and also, the ceiling /floor needs to be smarter
		int i = AH.indexOfSorted(toKey(key), keys, size);
		if (i == -1)
			return;
		if (values[i] == null)
			return;
		values[i] = null;
		deleted++;
	}

	@Override
	public AmiIndexMap getIndex(Comparable key) {
		return get(toKey(key));
	}
	public AmiIndexMap get(long key) {
		int i = AH.indexOfSorted(key, keys, size);
		if (i == -1)
			return null;
		return values[i];
	}

	@Override
	public void putIndex(Comparable key, AmiIndexMap value) {
		long k = toKey(key);
		if (size == keys.length) {
			keys = Arrays.copyOf(keys, keys.length << 1);
			values = Arrays.copyOf(values, values.length << 1);
		}
		if (max >= k) {
			int pos = AH.indexOfSortedGreaterThanEqualTo(k, keys, size);
			if (keys[pos] == k) {
				if (values[pos] == null)
					deleted--;
				values[pos] = value;
			} else {
				System.arraycopy(keys, pos, keys, pos + 1, size - pos);
				System.arraycopy(values, pos, values, pos + 1, size - pos);
				keys[pos] = k;
				values[pos] = value;
				size++;
			}
		} else {
			keys[size] = max = k;
			values[size] = value;
			size++;
		}
	}

	@Override
	public boolean isIndexEmpty() {
		return size == deleted;
	}

	private long toKey(Comparable key) {
		return key == null ? Long.MIN_VALUE : ((Number) key).longValue();
	}

	public int getIndexCeiling(long k, boolean inclusive) {
		if (k > max)
			return -1;
		final int i = AH.indexOfSortedGreaterThanEqualTo(k, keys, size);//TODO:size
		if (!inclusive && i != -1 && keys[i] == k)
			return i == size - 1 ? -1 : i + 1;
		return i;
	}
	public int getIndexFloor(long k, boolean inclusive) {
		if (k > max)
			return size - 1;
		final int i = AH.indexOfSortedLessThanEqualTo(k, keys, size);//TODO:size
		if (!inclusive && i != -1 && keys[i] == k)
			return i - 1;
		return i;
	}

	public AmiIndexMap getAt(int n) {
		return n == -1L ? null : this.values[n];
	}
	public long getKeyAt(int n) {
		return n == -1L ? Long.MIN_VALUE : this.keys[n];
	}

	public static void main(String a[]) {
		TreeSet<Long> ts = new TreeSet<Long>();
		long n = 1;
		AmiIndexMap_Series s = new AmiIndexMap_Series();
		Random r = new Random(123);
		for (int i = 0; i < 1000; i++) {
			n += 1 + r.nextInt(7);
			ts.add(n);
			s.putIndex(n, null);
		}
		System.out.println(ts.subSet(20L, true, 30L, false));
		for (int j = 0; j < 10000; j++) {
			long start = r.nextInt((int) n * 2) - n;
			long end = r.nextInt((int) n * 2);
			if (start > end) {
				long t = end;
				end = start;
				start = t;
			}
			boolean sInclusive = r.nextBoolean();
			boolean eInclusive = r.nextBoolean();
			List<Long> values = CH.l(ts.subSet(start, sInclusive, end, eInclusive));
			int low = s.getIndexCeiling(start, sInclusive);
			int hgh = s.getIndexFloor(end, eInclusive);
			for (int i = low; i <= hgh; i++) {
				OH.assertEq(values.get(i - low), (Long) s.getKeyAt(i), " for " + i + " and " + j + " and " + values.size());
			}

		}

		Duration d = new Duration();
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 1000 * 1000; i++) {
				n += 1 + r.nextInt(7);
				ts.add(n);
				s.putIndex(n, null);
			}
			d.stampStdout();
		}
		System.out.println();

		d = new Duration();
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 1000 * 1000; i++) {
				s.getIndex(r.nextInt((int) n));
			}
			d.stampStdout();
		}

	}

	public int size() {
		return size;
	}
	@Override
	public Iterable<Comparable> getKeysForDebug() {
		LongArrayList r = new LongArrayList(this.keys, 0, this.size);
		return (Iterable) r;
	}
	@Override
	public boolean getRows(AmiQueryFinder finder, AmiQueryFinderVisitor visitor) {
		return finder.getRows(this, visitor);
	}
	@Override
	public Iterable<AmiIndexMap> getValuesForDebug() {
		ArrayList r = new ArrayList(values.length);
		for (AmiIndexMap i : this.values)
			if (i != null)
				r.add(i);
		return r;
	}
	@Override
	public int getKeysCount() {
		return size;
	}

}
