package com.f1.persist.structs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.f1.base.ValuedListenable;
import com.f1.base.ValuedListener;
import com.f1.persist.PersistableListener;
import com.f1.utils.EmptyCollection;
import com.f1.utils.structs.Tuple2;

public class PersistableArrayList<V> extends ArrayList<V> implements PersistableList<V> {

	private List<PersistableListener> listeners = new ArrayList<PersistableListener>();

	public PersistableArrayList() {
		super();
	}

	public PersistableArrayList(int length) {
		super(length);
	}

	@Override
	public void updateKeyedParam(Integer key, V value) {
		set((int) key, value);
	}

	@Override
	public void addKeyedParam(Integer key, V value) {
		add((int) key, value);
	}

	@Override
	public V askKeyedParam(Integer key) {
		return get(key);
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
		for (int i = 0, l = size(); i < l; i++) {
			V v = get(i);
			if (v instanceof ValuedListenable)
				sink.add((ValuedListenable) v);
		}
	}

	@Override
	public boolean remove(Object obj) {
		int index = super.indexOf(obj);
		if (index == -1)
			return false;
		super.remove(index);
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).onKeyedParamRemoved(this, index, obj);
		return true;
	}

	@Override
	public void clear() {
		List<Map.Entry<Object, Object>> l = new ArrayList<Map.Entry<Object, Object>>(size());
		for (int i = 0, s = size(); i < s; i++)
			l.add(new Tuple2<Object, Object>(i, get(i)));
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).onKeyedParamsCleared(this, l);
		super.clear();
	}

	@Override
	public List<V> getCopyThreadsafe() {

		Object[] values = super.toArray();
		List<V> r = new ArrayList<V>(values.length);
		for (int i = 0; i < values.length; i++)
			if (values[i] != null)
				r.add((V) values[i]);
		return r;
	}

	@Override
	public void clearKeyedParams() {
		clear();
	}

	@Override
	public void removeKeyedParam(Integer key) {
		remove((int) key);
	}

	@Override
	public V set(int index, V element) {

		V r = super.set(index, element);
		for (PersistableListener l : listeners)
			l.onKeyedParamChanged(this, index, r, element);
		return r;
	}

	@Override
	public boolean add(V element) {
		if (element == null)
			throw new NullPointerException("null values not supported at this time");
		int location = size();
		super.add(element);
		for (PersistableListener l : listeners)
			l.onKeyedParamAdded(this, location, element);
		return true;
	}

	@Override
	public void add(int index, V element) {
		if (element == null)
			throw new NullPointerException("null values not supported at this time");
		super.add(index, element);
		for (PersistableListener l : listeners)
			l.onKeyedParamAdded(this, index, element);
	}

	@Override
	public V remove(int index) {
		V r = super.remove(index);
		for (PersistableListener l : listeners)
			l.onKeyedParamRemoved(this, index, r);
		return r;
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		boolean r = false;
		for (V o : c)
			r = add(o) || r;
		return r;
	}

	@Override
	public boolean addAll(int index, Collection<? extends V> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean r = false;
		for (Object o : c)
			r = remove(o) || r;
		return r;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

}
