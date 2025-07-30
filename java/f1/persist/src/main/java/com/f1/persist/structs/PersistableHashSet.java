package com.f1.persist.structs;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.ValuedListenable;
import com.f1.base.ValuedListener;
import com.f1.persist.PersistableListener;
import com.f1.utils.EmptyCollection;
import com.f1.utils.concurrent.ConcurrentCopyMap;
import com.f1.utils.structs.Tuple2;

public class PersistableHashSet<V> extends AbstractSet<V> implements PersistableSet<V> {

	private final ConcurrentCopyMap<V, V> inner;
	private List<PersistableListener> listeners = new ArrayList<PersistableListener>();

	public PersistableHashSet(int length) {
		inner = new ConcurrentCopyMap<V, V>(length);
	}

	public PersistableHashSet() {
		inner = new ConcurrentCopyMap<V, V>();
	}

	@Override
	public boolean addPersistableListener(PersistableListener persistableListener) {
		if (listeners.contains(persistableListener))
			return false;
		listeners.add(persistableListener);
		return true;
	}

	@Override
	public boolean removePersistableListener(PersistableListener persistableListener) {
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
		return EmptyCollection.INSTANCE;
	}

	@Override
	public void askChildValuedListenables(List<ValuedListenable> sink) {
		return;
	}

	@Override
	public void addKeyedParam(V key, Boolean value) {
		add(key);
	}

	@Override
	public void updateKeyedParam(V key, Boolean value) {
		add(key);
	}

	@Override
	public void clearKeyedParams() {
		clear();
	}

	@Override
	public void removeKeyedParam(V key) {
		remove(key);
	}

	@Override
	public void clear() {
		inner.clear();
		List<Map.Entry<Object, Object>> l = new ArrayList<Map.Entry<Object, Object>>(size());
		for (Object o : this)
			l.add(new Tuple2<Object, Object>(o, Boolean.TRUE));
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).onKeyedParamsCleared(this, l);
	}

	@Override
	public Boolean askKeyedParam(V key) {
		return inner.containsKey(key);
	}

	@Override
	public boolean add(V value) {
		if (inner.put(value, value) != null)
			return false;
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).onKeyedParamAdded(this, value, Boolean.TRUE);
		return true;
	}

	@Override
	public boolean remove(Object value) {
		if (inner.remove(value) == null)
			return false;
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).onKeyedParamRemoved(this, value, Boolean.TRUE);
		return true;
	}

	@Override
	public boolean contains(Object value) {
		return inner.containsKey(value);
	}

	@Override
	public Iterator<V> iterator() {
		return inner.keySet().iterator();
	}

	@Override
	public int size() {
		return inner.size();
	}

	@Override
	public Set getCopyThreadsafe() {
		Map<V, V> r = new HashMap<V, V>();
		inner.getEntriesCopy(r);
		return r.keySet();
	}
}
