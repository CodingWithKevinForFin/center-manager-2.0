package com.f1.persist.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.f1.base.ValuedListenable;
import com.f1.base.ValuedListener;
import com.f1.persist.Persistable;
import com.f1.persist.PersistableListener;
import com.f1.utils.AH;
import com.f1.utils.AbstractValuedListener;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.IdentityHasher;

public abstract class RefCountingPersistValuedListener extends AbstractValuedListener implements ValuedListener, PersistableListener {

	final HasherMap<ValuedListenable, Object[]> targetToSource = new HasherMap<ValuedListenable, Object[]>();

	public RefCountingPersistValuedListener() {
		targetToSource.setHasher(IdentityHasher.INSTANCE);
	}

	@Override
	public void onValuedAdded(ValuedListenable target) {
		incrementRefCount((ValuedListenable) target, this);
	}

	@Override
	public void onValuedRemoved(ValuedListenable target) {
		decrementRefCount((ValuedListenable) target, this);
	}

	@Override
	public void onValued(ValuedListenable target, String name, byte pid, Object old, Object value) {
		if (old == value)
			return;
		if (value instanceof ValuedListenable)
			incrementRefCount((ValuedListenable) value, target);
		if (old instanceof ValuedListenable)
			decrementRefCount((ValuedListenable) old, target);
	}

	private void incrementRefCount(ValuedListenable target, Object source) {

		Map.Entry<ValuedListenable, Object[]> entry = targetToSource.getEntry(target);
		if (entry == null) {
			if (!(target instanceof Persistable ? ((Persistable) target).addPersistableListener(this) : target.addListener(this)))
				throw new IllegalStateException("could not add listener");
			List<ValuedListenable> sink = new ArrayList<ValuedListenable>(4);
			if (entry == null)
				entry = targetToSource.putAndReturn(target, new Object[]{source});
			else
				entry.setValue(new Object[]{source});
			target.askChildValuedListenables(sink);
			for (int i = 0; i < sink.size(); i++)
				incrementRefCount(sink.get(i), target);
		} else {
			Object[] sources = entry.getValue();
			if (AH.indexOfByIdentity(source, sources) != -1)
				return;
			sources = Arrays.copyOf(sources, sources.length + 1);
			sources[sources.length - 1] = source;
			entry.setValue(sources);
		}
		onRefCount(target, entry.getValue().length);
	}

	abstract protected void onRefCount(ValuedListenable target, int count);

	private void decrementRefCount(ValuedListenable target, Object source) {
		Object[] sources = targetToSource.get(target);
		int ind = AH.indexOfByIdentity(source, sources);
		if (ind == -1)
			throw new IllegalStateException("atteming to remove of nonexistent reference");
		if (sources.length == 1) {
			targetToSource.remove(target);
			if (target instanceof Persistable)
				((Persistable) target).removePersistableListener(this);
			else
				target.removeListener(this);
			List<ValuedListenable> sink = new ArrayList<ValuedListenable>(4);
			target.askChildValuedListenables(sink);
			for (int i = 0; i < sink.size(); i++)
				decrementRefCount(sink.get(i), target);
		} else {
			final Object[] sources2 = new Object[sources.length - 1];
			for (int s = 0, t = 0; s < sources.length; s++)
				if (s != ind)
					sources2[t++] = sources[s];
			targetToSource.put(target, sources2);
		}
		onRefCount(target, sources.length - 1);
	}

	@Override
	public void onKeyedParamChanged(Persistable target, Object key, Object oldValue, Object newValue) {
		if (oldValue == newValue)
			return;
		if (newValue instanceof ValuedListenable)
			incrementRefCount((ValuedListenable) newValue, target);
		if (oldValue instanceof ValuedListenable)
			decrementRefCount((ValuedListenable) oldValue, target);
	}

	@Override
	public void onKeyedParamRemoved(Persistable target, Object key, Object oldValue) {
		if (oldValue instanceof ValuedListenable)
			decrementRefCount((ValuedListenable) oldValue, target);
	}

	@Override
	public void onKeyedParamAdded(Persistable target, Object key, Object newValue) {
		if (newValue instanceof ValuedListenable)
			incrementRefCount((ValuedListenable) newValue, target);
	}

	@Override
	public void onKeyedParamsCleared(Persistable target, Iterable<Map.Entry<Object, Object>> oldEntries) {
		for (Map.Entry<Object, Object> o : oldEntries)
			if (o.getValue() instanceof ValuedListenable)
				decrementRefCount((ValuedListenable) o.getValue(), target);
	}

}
