package com.f1.persist.structs;

import java.util.List;
import com.f1.persist.Persistable;

public interface PersistableList<K> extends Persistable<Integer, K>, List<K> {

	public List<K> getCopyThreadsafe();
}
