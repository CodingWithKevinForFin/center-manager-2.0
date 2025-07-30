package com.f1.persist;

import com.f1.base.ValuedListenable;

public interface PersistWriter {
	void writeValued(ValuedListenable valued);

	void writeValuedRemoved(ValuedListenable valued);

	void writeParam(ValuedListenable valued, byte pid, Object value);

	void writeParam(ValuedListenable valued, String pin, Object value);

	void writeKeyedParamChanged(Persistable persistable, Object key, Object value);

	void writeKeyedParamAdded(Persistable persistable, Object key, Object value);

	void writeKeyedParamRemoved(Persistable target, Object key);

	void writeKeyedParamsCleared(Persistable target);

}
