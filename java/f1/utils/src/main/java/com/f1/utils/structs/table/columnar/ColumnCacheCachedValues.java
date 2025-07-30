package com.f1.utils.structs.table.columnar;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMap.Node;

/**
 * A simple LRU cache which employees a double-linked list for fast deletes, newest cached elements are always moved to the head of the list and old elements are deleted from the
 * tail
 * 
 * @author rcooke
 * 
 * @param <T>
 */
public class ColumnCacheCachedValues<T> {

	public static class Cache<T> {
		public Cache(long pointer, T value, int size) {
			this.pointer = pointer;
			this.value = value;
			this.size = size;
		}

		private Cache<T> older, newer;
		private int size;
		private long pointer;
		private T value;

		public void insertAfter(Cache<T> older) {
			OH.assertNull(older.newer);
			OH.assertNull(older.older);
			older.newer = this;
			older.older = this.older;
			this.older.newer = older;
			this.older = older;
		}
		public void remove() {
			this.newer.older = older;
			this.older.newer = newer;
			this.newer = this.older = null;
		}
		public int getSize() {
			return this.size;
		}
		public T getValue() {
			return this.value;
		}

	}

	private static final Logger log = LH.get();
	final private Cache<T> oldest = new Cache<T>(0, null, 0), newest = new Cache<T>(0, null, 0);
	private LongKeyMap<Cache<T>> pointersCache = new LongKeyMap<Cache<T>>();
	private long cachedBytes;
	final private long maxInMemCacheSize;

	public ColumnCacheCachedValues(long maxInMemCacheSize2) {
		this.newest.older = oldest;
		this.oldest.newer = newest;
		this.maxInMemCacheSize = maxInMemCacheSize2;
	}

	public void clear() {
		this.pointersCache.clear();
		this.cachedBytes = 0;
	}

	//returns olds size or -1 if not found;
	public int cache(long pointer, T value, int newSize) {
		if (value == null || newSize > maxInMemCacheSize) {
			Cache<T> cache = pointersCache.remove(pointer);
			if (cache != null) {
				cachedBytes -= cache.size;
				cache.remove();
				return cache.size;
			}
			return -1;
		} else {
			Node<Cache<T>> node = pointersCache.getNodeOrCreate(pointer);
			Cache<T> cache = node.getValue();
			final int r;
			if (cache == null) {
				cachedBytes += newSize;
				node.setValue(cache = new Cache<T>(pointer, value, newSize));
				r = -1;
			} else {
				r = cache.size;
				cachedBytes += newSize - cache.size;
				cache.value = value;
				cache.pointer = pointer;
				cache.size = newSize;
				cache.remove();
			}
			newest.insertAfter(cache);
			while (cachedBytes > maxInMemCacheSize && oldest.newer != newest) {
				Cache toRemove = oldest.newer;
				cachedBytes -= toRemove.size;
				toRemove.remove();
				this.pointersCache.remove(toRemove.pointer);
			}
			return r;
		}
	}

	public long getCachedBytes() {
		return this.cachedBytes;
	}

	public Cache<T> get(long pointer) {
		Cache<T> r = this.pointersCache.get(pointer);
		if (r != null) {
			r.remove();
			this.newest.insertAfter(r);
		}
		return r;
	}

	public long getMaxInMemCacheSize() {
		return maxInMemCacheSize;
	}
}
