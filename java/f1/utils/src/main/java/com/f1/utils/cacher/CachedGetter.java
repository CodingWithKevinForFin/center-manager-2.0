package com.f1.utils.cacher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.f1.base.Clock;
import com.f1.base.Getter;
import com.f1.base.ToStringable;
import com.f1.utils.SH;
import com.f1.utils.structs.AbstractDoubleLinkedListEntry;
import com.f1.utils.structs.BasicDoubleLinkedList;

/**
 * 
 * Implements a basic LRU caching data structure by wrapping an underlying datasource which implements the {@link Getter} interface. All values in the cache must have a unique
 * identifying key. To get a value from the cache call {@link #get(Object)}. In the case of a cache miss (for example the first time you call {@link #get(Object)} for a particular
 * key) the LRU will call get() on the underlying store (passed into the constructor).
 * <P>
 * 
 * For the LRU to be effective call {@link setMaxSize(int size)} and pass in the maximum number of elements to cache. Be sure to set autoEviction to true in the constructor (this
 * will cause objects to be evicted automatically once the max size is reached)
 * <P>
 * 
 * Along with LRU it can also be configured to evict 'older' objects. Use {@link #setTimeToLiveMs(long)} to configure the maximum amount of time in millis that an object will
 * remain in the cache. The {@link Clock} passed into the constructor is used to determine current time.
 * <P>
 * 
 * Note that cache invalidation for a particular key is done using {@link #evict(K key)}
 * <P>
 * 
 * Additional Note: If autoEviction is set to false then you must manually evict old events by periodically calling {@link #evict()}
 * <P>
 * 
 * @author rcooke
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * 
 */
public class CachedGetter<K, V> implements Getter<K, V> {
	private static final Object NULL = new Object();
	public static final int OPTION_MANUAL_EVICTION = 1;
	public static final int OPTION_RETAIN_NEWEST_IF_MAX_REACHED = 2;
	public static final long NO_TTL = -1;
	public static final int NO_MAX = -1;

	public BasicDoubleLinkedList<Entry> cachedByTime = new BasicDoubleLinkedList<Entry>();

	final private Getter<K, V> inner;
	final private Map<K, Entry> cachedValues;
	final private Clock clock;
	private long ttl = NO_TTL;
	private int maxSize = NO_MAX;
	final private boolean autoEviction;
	private int hit, update, miss;

	public CachedGetter(Getter<K, V> inner, boolean autoEviction, Clock clock) {
		this.autoEviction = autoEviction;
		cachedValues = new HashMap<K, Entry>();
		this.inner = inner;
		this.clock = clock;
	}

	public void setTimeToLiveMs(long ttl) {
		if (this.ttl == NO_TTL && ttl != NO_TTL) {
			long now = clock.getNow();
			for (Entry e : cachedByTime)
				e.storedTimeMs = now;
		}
		this.ttl = ttl;
		evict();
	}

	public V evict(K key) {
		Entry r = cachedValues.remove(key);
		if (r == null)
			return null;
		cachedByTime.remove(r);
		return r.value;
	}

	public int getCacheSize() {
		return cachedValues.size();
	}

	public boolean isInCache(K key) {
		return cachedValues.containsKey(key);
	}
	public boolean isInCacheAndNotExpired(K key) {
		Entry t = cachedValues.get(key);
		return t != null && !t.hasExpired(this.clock.getNow());
	}

	public long getTimeToLiveMs() {
		return ttl;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
		evict();
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void evict() {
		evictOld(clock.getNow());
		evictBeyondMax();
	}

	private void evictOld(long now) {
		if (ttl == NO_TTL)
			return;
		boolean changes = false;
		for (Entry oldest = cachedByTime.getHead();; oldest = (Entry) oldest.getNext()) {
			if (oldest == null) {
				if (changes)
					cachedByTime.clear();
				break;
			}
			if (!oldest.hasExpired(now)) {
				if (changes)
					cachedByTime.setHead(oldest);
				break;
			}
			cachedValues.remove(oldest.key);
			changes = true;
		}
	}

	private void evictBeyondMax() {
		if (maxSize == NO_MAX)
			return;
		int purgeSize = cachedValues.size() - maxSize;
		if (purgeSize > 0) {
			Iterator<Entry> i = cachedByTime.iterator();
			while (purgeSize-- > 0)
				cachedValues.remove(i.next().key);
			cachedByTime.setHead(i.next());
		}
	}

	@Override
	public V get(K key) {
		final long now = clock.getNow();
		final Entry result = cachedValues.get(key);

		// is it already in the cache?
		if (result != null) {
			// if it is old, update it
			if (result.hasExpired(now)) {
				update++;
				if (inner == null) {
					cachedValues.remove(key);
					cachedByTime.remove(result);
					return null;
				}
				result.setValue(inner.get(key));
			} else
				hit++;

			// if proactively evicting, evict old records. no need to check for
			// max size because cache didn't grow
			if (autoEviction)
				evictOld(now);

			return result.value;
		} else {
			miss++;
			// if proactively evicting then evict old records. This might be
			// necessary to make room for adding to cache
			if (autoEviction)
				evictOld(now);

			if (inner == null) {
				evictBeyondMax();
				return null;
			}
			final V r = inner.get(key);

			// add the value to the cache & evict older records if
			// necessary(because we have reached the max
			cachedValues.put(key, new Entry(key).setValue(r));
			evictBeyondMax();
			return r;
		}
	}

	public V put(K key, V value) {
		final long now = clock.getNow();
		final Entry result = cachedValues.get(key);

		// is it already in the cache?
		if (result != null) {
			update++;
			final V r = result.value;
			result.setValue(value);
			// if proactively evicting, evict old records. no need to check for
			// max size because cache didn't grow
			if (autoEviction)
				evictOld(now);
			return r;
		} else {
			update++;
			// if proactively evicting then evict old records. This might be
			// necessary to make room for adding to cache
			if (autoEviction)
				evictOld(now);
			// add the value to the cache & evict older records if
			// necessary(because we have reached the max
			cachedValues.put(key, new Entry(key).setValue(value));
			evictBeyondMax();
			return null;
		}
	}

	public int getHit() {
		return hit;
	}

	public int getUpdate() {
		return update;
	}

	public int getMiss() {
		return miss;
	}

	private class Entry extends AbstractDoubleLinkedListEntry implements ToStringable {

		V value;
		long storedTimeMs;
		private K key;

		public Entry(K key) {
			this.key = key;
			cachedByTime.add(this);
		}

		public Entry setValue(V value) {
			this.value = value;
			if (ttl != NO_TTL)
				this.storedTimeMs = clock.getNow();
			cachedByTime.remove(this);
			cachedByTime.add(this);
			return this;
		}

		public boolean hasExpired(long now) {
			return ttl != NO_TTL && now > storedTimeMs + ttl;
		}

		@Override
		public StringBuilder toString(StringBuilder sb) {
			SH.s(key, sb);
			sb.append(':');
			SH.s(value, sb);
			sb.append('@');
			SH.s(storedTimeMs, sb);
			return sb;
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}
	}

	@Override
	public String toString() {
		return cachedByTime.toString();
	}

}
