package com.f1.utils.structs;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.f1.utils.SH;

public class MapsBackedMap<K, V> implements Map<K, V> {

	private Map<K, V>[] maps;
	private SetsBackedSet<Entry<K, V>> entrySet;
	private SetsBackedSet<K> keySet;
	private CollectionsBackedCollection<V> values;
	private boolean suppressDups;

	public MapsBackedMap(boolean suppressDups, Map<K, V>... maps) {
		this.maps = maps;
		this.suppressDups = suppressDups;
	}
	public void setMaps(Map<K, V> maps[]) {
		this.maps = maps;
	}

	@Override
	public int size() {
		int r = 0;
		if (suppressDups)
			for (K m : keySet())
				r++;
		else
			for (Map<K, V> m : maps)
				r += m.size();
		return r;
	}

	@Override
	public boolean isEmpty() {
		for (Map<K, V> m : maps)
			if (!m.isEmpty())
				return false;
		return true;
	}

	@Override
	public boolean containsKey(Object key) {
		for (Map<K, V> m : maps)
			if (m.containsKey(key))
				return true;
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		for (Map m : maps)
			if (m.containsValue(value))
				return true;
		return false;
	}

	@Override
	public V get(Object key) {
		for (Map<K, V> m : maps)
			if (m.containsKey(key))
				return m.get(key);
		return null;
	}

	@Override
	public V put(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<K> keySet() {
		if (keySet == null) {
			Set<K>[] a = new Set[maps.length];
			for (int i = 0; i < maps.length; i++)
				a[i] = maps[i].keySet();
			keySet = new SetsBackedSet<K>(suppressDups, a);
		}
		return keySet;
	}

	@Override
	public Collection<V> values() {
		if (values == null) {
			Collection<V>[] a = new Collection[maps.length];
			for (int i = 0; i < maps.length; i++)
				a[i] = maps[i].values();
			values = new CollectionsBackedCollection<V>(a);
		}
		return values;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		if (entrySet == null) {
			Set<java.util.Map.Entry<K, V>>[] a = new Set[maps.length];
			for (int i = 0; i < maps.length; i++)
				a[i] = maps[i].entrySet();
			entrySet = new SetsBackedSet<Map.Entry<K, V>>(suppressDups, a);
		}
		return entrySet;
	}
	public int getMapsCount() {
		return this.maps.length;
	}
	public Map<K, V> getMap(int i) {
		return this.maps[i];
	}

	public void replaceMap(int i, Map<K, V> nuw) {
		this.maps[i] = nuw;
		if (this.entrySet != null)
			this.entrySet.replaceSet(i, nuw.entrySet());
		if (this.keySet != null)
			this.keySet.replaceSet(i, nuw.keySet());
		if (this.values != null)
			this.values.replaceCollection(i, nuw.values());
	}

	@Override
	public String toString() {
		if (this.entrySet == null) {
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			for (Map m : this.maps) {
				if (sb.length() > 1 && !m.isEmpty())
					sb.append(',');
				SH.joinMap(',', '=', m, sb);
			}
			return sb.append('}').toString();
		}
		return SH.join(",", this.entrySet, new StringBuilder().append('{')).append('}').toString();
	}

}
