package com.f1.utils.structs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.f1.utils.MapWrapper;
import com.f1.utils.OH;

public class MapInMapInMap<K1, K2, K3, V> extends MapWrapper<K1, MapInMap<K2, K3, V>> {

	private Iterable<Tuple4<K1, K2, K3, V>> iterable = new MapInMapInMapIterable();
	private Iterable<Tuple3<K1, K2, Map<K3, V>>> iterable2 = new MapInMapIterable();
	private Iterable<V> valuesIterable = new MapInMapInMapValuesIterable();

	public MapInMapInMap() {
		super(new HashMap<K1, MapInMap<K2, K3, V>>());
	}
	public V putMulti(K1 k1, K2 k2, K3 k3, V value) {
		MapInMap<K2, K3, V> m = get(k1);
		if (m == null)
			put(k1, m = newMap());
		return m.putMulti(k2, k3, value);
	}

	private MapInMap<K2, K3, V> newMap() {
		return new MapInMap<K2, K3, V>();
	}

	public void putAllMulti(K1 k1, K2 k2, Map<K3, V> val) {
		MapInMap<K2, K3, V> m = get(k1);
		if (m == null)
			put(k1, m = newMap());
		m.putAllMulti(k2, val);
	}

	public Map<K3, V> getMulti(K1 k1, K2 k2) {
		MapInMap<K2, K3, V> m = get(k1);
		if (m == null)
			return Collections.EMPTY_MAP;
		return m.get(k2);
	}
	public Collection<V> getValues(K1 k1, K2 k2) {
		MapInMap<K2, K3, V> m1 = get(k1);
		if (m1 == null)
			return Collections.EMPTY_LIST;
		Map<K3, V> m2 = m1.get(k2);
		if (m2 == null)
			return Collections.EMPTY_LIST;
		return m2.values();
	}

	public V getMulti(K1 k1, K2 k2, K3 k3) {
		MapInMap<K2, K3, V> m = get(k1);
		return m == null ? null : m.getMulti(k2, k3);
	}

	public V removeMulti(K1 k1, K2 k2, K3 k3) {
		MapInMap<K2, K3, V> m = get(k1);
		if (m == null)
			return null;
		V r = m.removeMulti(k2, k3);
		if (m.size() == 0)
			remove(k1);
		return r;
	}

	//NOTE: the tuple4 is reused!!!
	public Iterable<Tuple4<K1, K2, K3, V>> entrySetMulti() {
		return iterable;
	}
	public Iterable<Tuple3<K1, K2, Map<K3, V>>> keysMulti() {
		return iterable2;
	}
	public Iterable<V> valuesMulti() {
		return valuesIterable;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof MapInMapInMap && OH.eq(getInnerMap(), ((MapInMapInMap) obj).getInnerMap());
	}

	private class MapInMapInMapIterable implements Iterable<Tuple4<K1, K2, K3, V>> {

		@Override
		public Iterator<Tuple4<K1, K2, K3, V>> iterator() {
			return new MapInMapInMapIterator();
		}
	}

	private class MapInMapInMapValuesIterable implements Iterable<V> {

		@Override
		public Iterator<V> iterator() {
			return new MapInMapInMapValuesIterator();
		}
	}

	private class MapInMapInMapValuesIterator implements Iterator<V> {

		private MapInMapInMapIterator inner = new MapInMapInMapIterator();

		@Override
		public boolean hasNext() {
			return inner.hasNext();
		}
		@Override
		public V next() {
			return inner.next().getD();
		}
		@Override
		public void remove() {
			inner.remove();
		}

	}

	private class MapInMapInMapIterator implements Iterator<Tuple4<K1, K2, K3, V>> {
		private Tuple4<K1, K2, K3, V> tmp = new Tuple4<K1, K2, K3, V>();

		//Iterator over the outer map
		private Iterator<Map.Entry<K1, MapInMap<K2, K3, V>>> iterator;

		//iterator for the inner map
		private Iterator<Tuple3<K2, K3, V>> currentIterator;

		//Current entry of the outer map which is being iterated over
		private java.util.Map.Entry<K1, MapInMap<K2, K3, V>> entry;

		private MapInMapInMapIterator() {
			this.iterator = entrySet().iterator();
			walkIterator();
		}

		@Override
		public Tuple4<K1, K2, K3, V> next() {
			final Tuple3<K2, K3, V> e = currentIterator.next();
			tmp.setABCD(entry.getKey(), e.getA(), e.getB(), e.getC());
			Tuple4<K1, K2, K3, V> r = tmp;
			if (!currentIterator.hasNext())
				walkIterator();
			return r;
		}

		private void walkIterator() {
			while (iterator.hasNext()) {
				entry = iterator.next();
				currentIterator = entry.getValue().entrySetMulti().iterator();
				if (currentIterator.hasNext()) {
					return;
				}
			}
			currentIterator = null;
			tmp = null;
		}

		@Override
		public boolean hasNext() {
			return currentIterator != null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private class MapInMapIterable implements Iterable<Tuple3<K1, K2, Map<K3, V>>> {

		@Override
		public MapInMapIterator iterator() {
			return new MapInMapIterator();
		}
	}

	private class MapInMapIterator implements Iterator<Tuple3<K1, K2, Map<K3, V>>> {
		private Tuple3<K1, K2, Map<K3, V>> tmp = new Tuple3<K1, K2, Map<K3, V>>();

		//Iterator over the outer map
		private Iterator<Entry<K1, MapInMap<K2, K3, V>>> iterator;

		//iterator for the inner map
		private Iterator<java.util.Map.Entry<K2, Map<K3, V>>> currentIterator;

		//Current entry of the outer map which is being iterated over
		private java.util.Map.Entry<K1, MapInMap<K2, K3, V>> entry;

		private MapInMapIterator() {
			this.iterator = entrySet().iterator();
			walkIterator();
		}

		@Override
		public Tuple3<K1, K2, Map<K3, V>> next() {
			final java.util.Map.Entry<K2, Map<K3, V>> e = currentIterator.next();
			tmp.setABC(entry.getKey(), e.getKey(), e.getValue());
			Tuple3<K1, K2, Map<K3, V>> r = tmp;
			if (!currentIterator.hasNext())
				walkIterator();
			return r;
		}

		private void walkIterator() {
			while (iterator.hasNext()) {
				entry = iterator.next();
				currentIterator = entry.getValue().entrySet().iterator();
				if (currentIterator.hasNext()) {
					return;
				}
			}
			currentIterator = null;
			tmp = null;
		}

		@Override
		public boolean hasNext() {
			return currentIterator != null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
