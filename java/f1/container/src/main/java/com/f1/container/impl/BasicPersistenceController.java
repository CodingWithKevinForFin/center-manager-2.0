package com.f1.container.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import com.f1.base.Factory;
import com.f1.base.Transactional;
import com.f1.base.ValuedListenable;
import com.f1.base.ValuedListener;
import com.f1.container.Partition;
import com.f1.container.PartitionController;
import com.f1.container.PersistenceController;
import com.f1.container.State;
import com.f1.utils.AbstractValuedListener;
import com.f1.utils.LH;

public class BasicPersistenceController extends AbstractContainerScope implements PersistenceController {

	private volatile boolean isActive;
	private List<Factory<Object, ValuedListener>> valuedListenerFactories = new ArrayList<Factory<Object, ValuedListener>>();

	private List<PersistenceRoot> replicatingStates = new ArrayList<PersistenceRoot>();

	private ValuedListener warmListener = new ValuedListenerImpl();
	private boolean isAutoCommit = true;
	private boolean hasListeners = false;

	@Override
	public void onLocalStateCreated(State state, boolean isRecovering) {
		assertStarted();
		PersistenceRoot pr = nw(PersistenceRoot.class);
		if (!isRecovering)
			state.initPersisted(false);
		state.lockPersistedRoot();
		if (isRecovering)
			state.initPersisted(true);
		ValuedListenable persisted = state.getPersistedRoot();
		if (persisted == null)
			return;
		if (state.getType() == null)
			throw new RuntimeException("state type can not be null");
		pr.setType(state.getType());
		pr.setStateType(state.getClass());
		pr.setPartitionId(state.getPartition().getPartitionId());
		pr.setPersistedRoot(state.getPersistedRoot());
		if (log.isLoggable(Level.FINER))
			LH.finer(log, "Sending persisted state image: ", pr.getPartitionId(), "-", pr.getType());
		for (Factory<Object, ValuedListener> factory : valuedListenerFactories) {
			ValuedListener listener = factory.get(null);
			listener.onValuedAdded(pr);
		}
		commitState(state);
	}

	@Override
	public void commitState(State state) {
		assertStarted();
		if (!hasListeners || state == null)
			return;
		ValuedListenable persisted = state.getPersistedRoot();
		if (persisted == null)
			return;
		for (ValuedListener l : persisted.getValuedListeners()) {
			if (l instanceof Transactional)
				((Transactional) l).commitTransaction();
		}
	}

	@Override
	public Collection<Factory<Object, ValuedListener>> getValueListenerFactories() {
		return valuedListenerFactories;
	}

	@Override
	public void addValueListenerFactory(Factory<Object, ValuedListener> factory) {
		assertNotStarted();
		hasListeners = true;
		valuedListenerFactories.add(factory);
	}

	private class ValuedListenerImpl extends AbstractValuedListener {

		@Override
		public void onValued(ValuedListenable target, String name, byte pid, Object old, Object value) {

		}

		@Override
		public void onValuedAdded(ValuedListenable target) {
		}

		public ValuedListener getWarmListener() {
			return warmListener;
		}

		@Override
		public void onValuedRemoved(ValuedListenable target) {
			if (target instanceof PersistenceRoot)
				replicatingStates.add((PersistenceRoot) target);
		}

	}

	public void start() {
		super.start();
		getServices().getGenerator().register(PersistenceRoot.class);
		PartitionController pc = this.getContainer().getPartitionController();
		for (Object i : pc.getPartitions()) {
			Partition p = pc.getPartition(i);
			for (Class c : p.getStateTypes())
				p.getState(c).initPersisted(false);
		}

	}

	@Override
	public void startDispatching() {
		assertNotDispatchingStarted();
		PartitionController pc = getContainer().getPartitionController();
		for (PersistenceRoot pr : replicatingStates) {
			Partition partition = pc.getOrCreatePartition(pr.getPartitionId());
			State state = nw(pr.getStateType());
			state.setType(pr.getType());
			state.setPersistedRoot(pr.getPersistedRoot());
			partition.putState(state);
		}
		replicatingStates.clear();
		super.startDispatching();
	}

	public List<PersistenceRoot> getReplicatingStates() {
		return new ArrayList<PersistenceRoot>(replicatingStates);
	}

	@Override
	public void addForReplication(PersistenceRoot root) {
		State state = nw(root.getStateType());
		state.setType(root.getType());
		state.setPersistedRoot(root.getPersistedRoot());
		Partition partition = getContainer().getPartitionController().getOrCreatePartition(root.getPartitionId());
		partition.putState(state);
		onLocalStateCreated(state, true);
	}

	@Override
	public boolean getIsAutoCommit() {
		return isAutoCommit;
	}

	@Override
	public void setIsAutoCommit(boolean isAutoCommit) {
		assertNotStarted();
		this.isAutoCommit = isAutoCommit;

	}

}
