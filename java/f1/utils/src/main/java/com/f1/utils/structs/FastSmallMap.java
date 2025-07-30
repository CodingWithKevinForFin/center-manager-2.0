package com.f1.utils.structs;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.f1.base.MappingEntry;
import com.f1.base.ToStringable;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;

@SuppressWarnings("unchecked")
public class FastSmallMap<K, V> implements Map<K, V>, ToStringable {

	// Strategy: uses very little memory for small maps by implementing linked list
	// Operations run in N time so this map should be used when N is expected to be VERY small. 
	// If the map grows more that MAX_LINKED_MAP_SIZE switches to Map
	private static final int MAX_LINKED_MAP_SIZE = 4;
	private Object innerMap;
	private int size = 0;

	public FastSmallMap() {
	}

	public FastSmallMap(Map<K, V> inner) {
		buildLinkedMap(inner);
	}

	private void buildLinkedMap(Map<? extends K, ? extends V> m) {
		this.size = m.size();
		if (size == 0) {
			this.innerMap = null;
		} else if (size > MAX_LINKED_MAP_SIZE)
			this.innerMap = new HasherMap<K, V>(m);
		else {
			Iterator<Entry<? extends K, ? extends V>> i = (Iterator) m.entrySet().iterator();
			Entry<? extends K, ? extends V> e1 = i.next();
			LinkedMap<K, V> n = new LinkedMap<K, V>(e1.getKey(), e1.getValue());
			innerMap = n;
			while (i.hasNext()) {
				e1 = i.next();
				n.next = new LinkedMap<K, V>(e1.getKey(), e1.getValue());
				n = n.next;
			}
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public V get(Object key) {
		switch (size) {
			case 0:
				return null;
			case 1: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key))
					return entry.value;
				return null;
			}
			case 2: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key))
					return entry.value;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry.value;
				return null;
			}
			case 3: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key))
					return entry.value;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry.value;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry.value;
				return null;
			}
			case 4: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key))
					return entry.value;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry.value;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry.value;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry.value;
				return null;
			}
			default:
				return ((Map<K, V>) innerMap).get(key);
		}
	}

	@Override
	public V remove(Object key) {
		switch (size) {
			case 0:
				return null;
			case 1: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key)) {
					this.innerMap = entry.next;
					size--;
					return entry.value;
				}
				return null;
			}
			case 2: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key)) {
					this.innerMap = entry.next;
					size--;
					return entry.value;
				}
				LinkedMap<K, V> next = entry.next;
				if (OH.eq(key, next.key)) {
					V r = entry.next.value;
					entry.next = next.next;
					size--;
					return r;
				}
				return null;
			}
			case 3: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key)) {
					this.innerMap = entry.next;
					size--;
					return entry.value;
				}
				LinkedMap<K, V> next = entry.next;
				if (OH.eq(key, next.key)) {
					V r = entry.next.value;
					entry.next = next.next;
					size--;
					return r;
				}
				entry = next;
				next = entry.next;
				if (OH.eq(key, next.key)) {
					V r = entry.next.value;
					entry.next = next.next;
					size--;
					return r;
				}
				return null;
			}
			case 4: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key)) {
					this.innerMap = entry.next;
					size--;
					return entry.value;
				}
				LinkedMap<K, V> next = entry.next;
				if (OH.eq(key, next.key)) {
					V r = entry.next.value;
					entry.next = next.next;
					size--;
					return r;
				}
				entry = next;
				next = entry.next;
				if (OH.eq(key, next.key)) {
					V r = entry.next.value;
					entry.next = next.next;
					size--;
					return r;
				}
				entry = next;
				next = entry.next;
				if (OH.eq(key, next.key)) {
					V r = entry.next.value;
					entry.next = next.next;
					size--;
					return r;
				}
				return null;
			}
			default: {
				Map<K, V> m = (Map<K, V>) innerMap;
				V r = m.remove(key);
				size = m.size();
				if (m.size() == MAX_LINKED_MAP_SIZE) {
					Iterator<Entry<K, V>> i = m.entrySet().iterator();
					LinkedMap<K, V> lm = new LinkedMap<K, V>(i.next());
					this.innerMap = lm;
					lm.next = lm = new LinkedMap<K, V>(i.next());
					lm.next = lm = new LinkedMap<K, V>(i.next());
					lm.next = lm = new LinkedMap<K, V>(i.next());

				}
				return r;
			}

		}
	}

	@Override
	public V put(K key, V value) {
		switch (size) {
			case 0: {
				innerMap = new LinkedMap(key, value);
				size = 1;
				return null;
			}
			case 1: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key)) {
					V r = entry.value;
					entry.value = value;
					return r;
				}
				entry.next = new LinkedMap(key, value);
				size = 2;
				return null;
			}
			case 2: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key)) {
					V r = entry.value;
					entry.value = value;
					return r;
				}
				entry = entry.next;
				if (OH.eq(key, entry.key)) {
					V r = entry.value;
					entry.value = value;
					return r;
				}
				entry.next = new LinkedMap(key, value);
				size = 3;
				return null;
			}
			case 3: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key)) {
					V r = entry.value;
					entry.value = value;
					return r;
				}
				entry = entry.next;
				if (OH.eq(key, entry.key)) {
					V r = entry.value;
					entry.value = value;
					return r;
				}
				entry = entry.next;
				if (OH.eq(key, entry.key)) {
					V r = entry.value;
					entry.value = value;
					return r;
				}
				entry.next = new LinkedMap(key, value);
				size = 4;
				return null;
			}
			case 4: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key)) {
					V r = entry.value;
					entry.value = value;
					return r;
				}
				entry = entry.next;
				if (OH.eq(key, entry.key)) {
					V r = entry.value;
					entry.value = value;
					return r;
				}
				entry = entry.next;
				if (OH.eq(key, entry.key)) {
					V r = entry.value;
					entry.value = value;
					return r;
				}
				entry = entry.next;
				if (OH.eq(key, entry.key)) {
					V r = entry.value;
					entry.value = value;
					return r;
				}
				Map<K, V> m = new HasherMap<K, V>(MAX_LINKED_MAP_SIZE);
				for (LinkedMap<K, V> t2 = (LinkedMap<K, V>) innerMap; t2 != null; t2 = t2.next)
					m.put(t2.key, t2.value);
				m.put(key, value);
				this.innerMap = m;
				size = m.size();
				return null;
			}

			default: {
				Map<K, V> m = (Map<K, V>) innerMap;
				V r = m.put(key, value);
				size = m.size();
				return r;
			}
		}
	}

	@Override
	public void clear() {
		this.innerMap = null;
		this.size = 0;
	}

	@Override
	public boolean containsKey(Object key) {
		switch (size) {
			case 0:
				return false;
			case 1:
			case 2:
			case 3:
			case 4:
				for (LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap; t != null; t = t.next)
					if (OH.eq(t.key, key))
						return true;
				return false;
			default:
				return ((Map<K, V>) innerMap).containsKey(key);
		}
	}
	private boolean isNull(K key) {
		switch (size) {
			case 0:
				return false;
			case 1:
			case 2:
			case 3:
			case 4:
				for (LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap; t != null; t = t.next)
					if (OH.eq(t.key, key))
						return t.value == null;
				return false;
			default:
				return ((HasherMap<K, V>) innerMap).isNull(key);
		}
	}

	@Override
	public boolean containsValue(Object value) {
		switch (size) {
			case 0:
				return false;
			case 1:
			case 2:
			case 3:
			case 4:
				for (LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap; t != null; t = t.next)
					if (OH.eq(t.value, value))
						return true;
				return false;
			default:
				return ((Map<K, V>) innerMap).containsValue(value);
		}
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		if (innerMap == null)
			return Collections.EMPTY_SET;
		else if (innerMap instanceof LinkedMap) {
			LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap;
			if (t.next == null)
				return Collections.singleton((Map.Entry<K, V>) t);
			Set<Entry<K, V>> r = new HashSet<Entry<K, V>>(MAX_LINKED_MAP_SIZE);//TODO: make special structure
			for (; t != null; t = t.next)
				r.add(t);
			return r;
		} else
			return ((Map<K, V>) innerMap).entrySet();
	}

	@Override
	public boolean isEmpty() {
		return innerMap == null;
	}

	@Override
	public Set<K> keySet() {
		if (innerMap == null)
			return Collections.EMPTY_SET;
		else if (innerMap instanceof LinkedMap) {
			LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap;
			if (t.next == null)
				return Collections.singleton(t.key);
			HashSet<K> r = new HashSet<K>(MAX_LINKED_MAP_SIZE);//TODO: make special structure
			for (; t != null; t = t.next)
				r.add(t.key);
			return r;
		} else
			return ((Map<K, V>) innerMap).keySet();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (innerMap == null) {
			buildLinkedMap(m);
		} else if (innerMap instanceof LinkedMap) {
			for (Entry<? extends K, ? extends V> i : m.entrySet())
				put(i.getKey(), i.getValue());
		} else
			((Map<K, V>) innerMap).putAll(m);
	}

	@Override
	public Collection<V> values() {
		if (innerMap == null)
			return Collections.EMPTY_SET;
		else if (innerMap instanceof LinkedMap) {
			LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap;
			if (t.next == null)
				return Collections.singleton(t.value);
			ArrayList<V> r = new ArrayList<V>(MAX_LINKED_MAP_SIZE);
			for (; t != null; t = t.next)
				r.add(t.value);
			return r;
		} else
			return ((Map<K, V>) innerMap).values();
	}

	private static class LinkedMap<K, V> implements Map.Entry<K, V>, ToStringable {

		private K key;
		private V value;
		private LinkedMap<K, V> next;

		public LinkedMap(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public LinkedMap(Map.Entry<K, V> e) {
			this.key = e.getKey();
			this.value = e.getValue();
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V arg0) {
			V r = this.value;
			this.value = arg0;

			return r;
		}

		@Override
		public final String toString() {
			return getKey() + "=" + getValue();
		}

		public StringBuilder toString(StringBuilder sb) {
			return sb.append(getKey()).append('=').append(getValue());
		}

	}

	public Entry<K, V> getOrCreateEntry(K key) {
		switch (size) {
			case 0: {
				LinkedMap lm = new LinkedMap(key, null);
				this.innerMap = lm;
				size = 1;
				return lm;
			}
			case 1: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key))
					return entry;
				entry.next = new LinkedMap(key, null);
				size = 2;
				return entry.next;
			}
			case 2: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key))
					return entry;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry;
				entry.next = new LinkedMap(key, null);
				size = 3;
				return entry.next;
			}
			case 3: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key))
					return entry;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry;
				entry.next = new LinkedMap(key, null);
				size = 4;
				return entry.next;
			}
			case 4: {
				LinkedMap<K, V> entry = (LinkedMap<K, V>) innerMap;
				if (OH.eq(key, entry.key))
					return entry;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry;
				entry = entry.next;
				if (OH.eq(key, entry.key))
					return entry;
				HasherMap<K, V> m = new HasherMap<K, V>(MAX_LINKED_MAP_SIZE);
				for (LinkedMap<K, V> t2 = (LinkedMap<K, V>) innerMap; t2 != null; t2 = t2.next)
					m.put(t2.key, t2.value);
				Entry<K, V> r = m.getOrCreateEntry(key);
				this.innerMap = m;
				size = m.size();
				return r;
			}

			default: {
				HasherMap<K, V> m = (HasherMap<K, V>) innerMap;
				Entry<K, V> r = m.getOrCreateEntry(key);
				size = m.size();
				return r;
			}
		}
	}

	public V[] valuesArray(Class<V> type) {
		V r[] = (V[]) Array.newInstance(type, size());
		if (this.innerMap == null) {
		} else if (innerMap instanceof LinkedMap) {
			int pos = 0;
			for (LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap; t != null; t = t.next)
				r[pos++] = t.value;
		} else {
			HasherMap<K, V> m = (HasherMap<K, V>) innerMap;
			m.values().toArray(r);
		}
		return r;
	}
	public K[] keysArray(Class<K> type) {
		K r[] = (K[]) Array.newInstance(type, size());
		if (this.innerMap == null) {
		} else if (innerMap instanceof LinkedMap) {
			int pos = 0;
			for (LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap; t != null; t = t.next)
				r[pos++] = t.key;
		} else {
			HasherMap<K, V> m = (HasherMap<K, V>) innerMap;
			m.keySet().toArray(r);
		}
		return r;
	}
	public Entry<K, V>[] entriesArray() {
		Entry r[] = new Entry[size()];
		if (this.innerMap == null) {
		} else if (innerMap instanceof LinkedMap) {
			int pos = 0;
			for (LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap; t != null; t = t.next)
				r[pos++] = t;
		} else {
			HasherMap<K, V> m = (HasherMap<K, V>) innerMap;
			m.entrySet().toArray(r);
		}
		return r;
	}

	@Override
	public String toString() {
		if (this.innerMap == null)
			return "{}";
		else if (innerMap instanceof LinkedMap) {
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			boolean first = true;
			for (LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap; t != null; t = t.next) {
				if (first)
					first = false;
				else
					sb.append(", ");
				t.toString(sb);
			}
			sb.append('}');
			return sb.toString();
		} else
			return ((HasherMap) innerMap).toString();
	}
	@Override
	public StringBuilder toString(StringBuilder sb) {
		if (this.innerMap == null)
			return sb.append("{}");
		else if (innerMap instanceof LinkedMap) {
			sb.append('{');
			boolean first = true;
			for (LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap; t != null; t = t.next) {
				if (first)
					first = false;
				else
					sb.append(", ");
				t.toString(sb);
			}
			sb.append('}');
			return sb;
		} else
			return ((HasherMap) innerMap).toString(sb);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj.getClass() != this.getClass())
			return false;
		FastSmallMap other = (FastSmallMap) obj;
		if (other.size != size)
			return false;
		if (this.innerMap == null) {
			return true;
		} else if (innerMap instanceof LinkedMap) {
			for (LinkedMap<K, V> t = (LinkedMap<K, V>) innerMap; t != null; t = t.next)
				if (t.getValue() == null ? !other.isNull(t.getKey()) : OH.ne(other.get(t.getKey()), t.getValue()))
					return false;
		} else {
			HasherMap<K, V> m = (HasherMap<K, V>) innerMap;
			for (MappingEntry<K, V> t : m.entries())
				if (t.getValue() == null ? !other.isNull(t.getKey()) : OH.ne(other.get(t.getKey()), t.getValue()))
					return false;
		}
		return true;
	}

}
