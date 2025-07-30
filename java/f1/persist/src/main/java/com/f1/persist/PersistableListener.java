package com.f1.persist;

import java.util.Map;

public interface PersistableListener {
	public void onKeyedParamChanged(Persistable target, Object key, Object oldValue, Object newValue);

	public void onKeyedParamAdded(Persistable target, Object key, Object newValue);

	public void onKeyedParamRemoved(Persistable target, Object key, Object oldValue);

	public void onKeyedParamsCleared(Persistable target, Iterable<Map.Entry<Object, Object>> oldEntries);
}
