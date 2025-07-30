package com.f1.persist.structs;

import java.util.Map;
import com.f1.persist.Persistable;

public interface PersistableMap<K, V> extends Persistable<K, V>, Map<K, V> {

	public Map<K, V> getCopyThreadsafe();
}
