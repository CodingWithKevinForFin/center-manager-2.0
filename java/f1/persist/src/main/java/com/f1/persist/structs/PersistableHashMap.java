package com.f1.persist.structs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.ValuedListenable;
import com.f1.base.ValuedListener;
import com.f1.persist.PersistableListener;
import com.f1.utils.concurrent.ConcurrentCopyMap;

public class PersistableHashMap<K, V> extends ConcurrentCopyMap<K, V> implements PersistableMap<K, V> {

	@Override
	public void updateKeyedParam(K key, V value) {
		put(key, value);
	}

	@Override
	public void addKeyedParam(K key, V value) {
		put(key, value);
	}

	@Override
	public V askKeyedParam(K key) {
		return get(key);
	}

	private List<PersistableListener> listeners = new ArrayList<PersistableListener>();
	private List<ValuedListener> valuedListeners = new ArrayList<ValuedListener>();

	@Override
	public boolean addPersistableListener(PersistableListener persistableListener) {
		if (listeners.contains(persistableListener))
			return false;
		listeners.add(persistableListener);
		if (persistableListener instanceof ValuedListener) {
			valuedListeners.add((ValuedListener) persistableListener);
		}

		return true;
	}

	@Override
	public boolean removePersistableListener(PersistableListener persistableListener) {
		if (persistableListener instanceof ValuedListener) {
			valuedListeners.remove((ValuedListener) persistableListener);
		}
		return listeners.remove(persistableListener);
	}

	@Override
	public boolean addListener(ValuedListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeListener(ValuedListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(byte field, ValuedListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeListener(byte field, ValuedListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(String field, ValuedListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeListener(String field, ValuedListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<ValuedListener> getValuedListeners() {
		return valuedListeners;
	}

	@Override
	public void askChildValuedListenables(List<ValuedListenable> sink) {
		for (V v : values())
			if (v instanceof ValuedListenable)
				sink.add((ValuedListenable) v);
	}

	@Override
	public V put(K key, V value) {
		V r = super.put(key, value);
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).onKeyedParamChanged(this, key, r, value);
		return r;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}

	@Override
	public V remove(Object key) {
		Map.Entry<K, V> e = super.removeEntry(key);
		if (e == null)
			return null;
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).onKeyedParamRemoved(this, key, e.getValue());
		return e.getValue();
	}

	@Override
	public void clear() {
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).onKeyedParamsCleared(this, (Collection) this.entrySet());
		super.clear();
	}

	@Override
	public Map<K, V> getCopyThreadsafe() {

		Map<K, V> r = new HashMap<K, V>();
		super.getEntriesCopy(r);
		return r;
	}

	@Override
	public void clearKeyedParams() {
		clear();
	}

	@Override
	public void removeKeyedParam(K key) {
		remove(key);
	}

}
