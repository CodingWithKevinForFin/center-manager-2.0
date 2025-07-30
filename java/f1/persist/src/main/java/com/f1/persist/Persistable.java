package com.f1.persist;

import com.f1.base.ValuedListenable;

public interface Persistable<K, V> extends ValuedListenable {

	public void addKeyedParam(K key, V value);

	public void updateKeyedParam(K key, V value);

	public void clearKeyedParams();

	public void removeKeyedParam(K key);

	public V askKeyedParam(K key);

	public boolean addPersistableListener(PersistableListener persistableListener);

	public boolean removePersistableListener(PersistableListener persistableListener);

}
