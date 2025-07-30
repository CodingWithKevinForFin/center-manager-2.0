package com.f1.persist.impl;

import java.util.IdentityHashMap;
import java.util.Map;

import com.f1.base.Transactional;
import com.f1.base.Transient;
import com.f1.base.Valued;
import com.f1.base.ValuedListenable;
import com.f1.persist.Persistable;
import com.f1.persist.writer.TransactionalPersistWriter;
import com.f1.utils.OH;

public class BasicPersistValuedListener extends RefCountingPersistValuedListener implements Transactional {

	final private TransactionalPersistWriter writer;
	final Map<ValuedListenable, Integer> extraRefCounts = new IdentityHashMap<ValuedListenable, Integer>();

	public BasicPersistValuedListener(TransactionalPersistWriter writer) {
		this.writer = writer;
	}

	public boolean commitTransaction() {
		return this.writer.commitTransaction();
	}

	@Override
	public void onValued(ValuedListenable target, String name, byte pid, Object old, Object value) {
		if (OH.eq(old, value))
			return;
		if (pid != Valued.NO_PID) {
			if (!(target instanceof Valued) || (((Valued) target).askSchema().askValuedParam(pid).getTransience() & Transient.PERSIST) == 0)
				this.writer.writeParam(target, pid, value);
		} else {
			if (!(target instanceof Valued) || (((Valued) target).askSchema().askValuedParam(name).getTransience() & Transient.PERSIST) == 0)
				this.writer.writeParam(target, name, value);
		}
		super.onValued(target, name, pid, old, value);
	}

	@Override
	public void onKeyedParamChanged(Persistable target, Object key, Object oldValue, Object newValue) {
		if (!OH.isImmutable(key))
			throw new RuntimeException("key must be immutable: " + key);
		if (OH.eq(oldValue, newValue))
			return;
		this.writer.writeKeyedParamChanged(target, key, newValue);
		super.onKeyedParamChanged(target, key, oldValue, newValue);
	}

	@Override
	public void onKeyedParamRemoved(Persistable target, Object key, Object oldValue) {
		this.writer.writeKeyedParamRemoved(target, key);
		super.onKeyedParamRemoved(target, key, oldValue);
	}

	@Override
	public void onKeyedParamAdded(Persistable target, Object key, Object newValue) {
		if (!OH.isImmutable(key))
			throw new RuntimeException("key must be immutable: " + key);
		this.writer.writeKeyedParamAdded(target, key, newValue);
		super.onKeyedParamAdded(target, key, newValue);
	}

	@Override
	public void onKeyedParamsCleared(Persistable target, Iterable<Map.Entry<Object, Object>> oldEntries) {
		this.writer.writeKeyedParamsCleared(target);
	}

	@Override
	protected void onRefCount(ValuedListenable target, int count) {
		switch (count) {
		case 1:
			writer.writeValued(target);
			break;
		case 0:
			writer.writeValuedRemoved(target);
			break;
		}

	}

}
