package com.f1.persist.structs;

import java.util.Set;
import com.f1.persist.Persistable;

public interface PersistableSet<K> extends Persistable<K, Boolean>, Set<K> {

	Set getCopyThreadsafe();

}
