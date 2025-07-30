/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import com.f1.base.Action;
import com.f1.container.ContainerRuntimeListener;
import com.f1.container.Partition;
import com.f1.container.PartitionController;
import com.f1.container.PartitionGenerator;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.StateGenerator;
import com.f1.container.exceptions.ContainerException;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class BasicPartitionController extends AbstractContainerScope implements PartitionController {

	private final ConcurrentMap<Object, Partition> partitions = new ConcurrentHashMap<Object, Partition>();
	private final List<PartitionGenerator> partitionGenerators;
	private HashMap<Object, StateGenerator> stateGenerators = new HashMap<Object, StateGenerator>();
	private Level perfLoggingLevel;
	private Iterable<ContainerRuntimeListener> listeners;

	@Override
	public Partition getPartition(Object partitionId) {
		return partitions.get(partitionId);
	}

	public BasicPartitionController() {
		this.partitionGenerators = new ArrayList<PartitionGenerator>();
		this.registerPartitionGenerator(new BasicPartitionGenerator());
	}

	@Override
	public Collection<Object> getPartitions() {
		return partitions.keySet();
	}

	@Override
	public Partition removePartition(Object partitionId) {
		final Partition r = partitions.remove(partitionId);
		if (r != null)
			getContainer().getDispatchController().onPartitionRemoved(r);
		return r;
	}

	@Override
	public Partition getOrCreatePartition(Object partitionId) {
		Partition r = partitions.get(partitionId);
		if (r == null) {
			Partition existing = partitions.putIfAbsent(partitionId, r = createPartition(partitionId));
			if (existing != null)
				r = existing;
			else
				fireOnNewPartition(r);

			if (perfLoggingLevel != null && log.isLoggable(perfLoggingLevel))
				LH.log(log, perfLoggingLevel, "PERFORMANCE: CreatePartition: pid=" + r.getPartitionId() + ", type=" + OH.getSimpleClassName(r));
		}
		return r;

	}

	private void fireOnNewPartition(Partition r) {
		Iterable<ContainerRuntimeListener> l = listeners;
		if (l == null)
			l = getContainer().getRuntimeListeners();
		for (ContainerRuntimeListener listener : l)
			try {
				listener.onPartitionCreated(this, r);
			} catch (Exception e) {
				LH.severe(log, "Error firing new partition notification on listener: ", listener, e);
			}
	}

	protected Partition createPartition(Object partitionId) {
		for (int i = 0, l = partitionGenerators.size(); i < l; i++) {
			Partition r = partitionGenerators.get(i).createPartition(partitionId);
			if (r != null) {
				fireOnNewPartition(r);
				return r;
			}
		}
		throw new ContainerException("couldn't create partition for partition id: " + partitionId);
	}

	@Override
	public List<PartitionGenerator> getPartitionGenerators() {
		return partitionGenerators;
	}

	@Override
	public void registerPartitionGenerator(PartitionGenerator partitionGenerator) {
		assertNotStarted();
		this.partitionGenerators.add(0, partitionGenerator);
		addChildContainerScope(partitionGenerator);
	}

	@Override
	public Map<Object, StateGenerator> getStateGenerators() {
		return this.stateGenerators;
	}

	@Override
	public void registerStateGenerator(StateGenerator stateGenerator) {
		StateGenerator existing = this.stateGenerators.get(stateGenerator);
		if (OH.eq(existing, stateGenerator))
			return;
		if (isStarted()) {
			synchronized (this) {
				this.stateGenerators = CH.copyAndPut(this.stateGenerators, stateGenerator.getStateType(), stateGenerator);
				addChildContainerScope(stateGenerator);
			}
		} else {
			CH.putOrThrow(this.stateGenerators, stateGenerator.getStateType(), stateGenerator);
			addChildContainerScope(stateGenerator);
		}
	}

	@Override
	public StateGenerator overrideStateGenerator(StateGenerator stateGenerator) {
		assertNotStarted();
		StateGenerator r = this.stateGenerators.put(stateGenerator.getStateType(), stateGenerator);
		addChildContainerScope(stateGenerator);
		if (r != null)
			removeChildContainerScope(r);
		return r;
	}

	@Override
	public State getState(Object partitionid, Class type) {
		Partition partition = getPartition(partitionid);
		return partition == null ? null : partition.getState(type);
	}

	@Override
	public State createState(Partition partition, Action action, Processor processor) {
		Class type = processor.getStateType();
		StateGenerator stateGenerator = stateGenerators.get(type);
		State r;
		if (stateGenerator == null) {
			try {
				if (type == State.class) {
					r = nw(BasicState.class);
				} else
					r = (State) nw(type);
			} catch (Exception e) {
				throw new RuntimeException("For action: " + action + ", and processor: " + processor + ", and partition: " + partition.getPartitionId(), e);
			}
		} else {
			r = stateGenerator.createState(partition, action, processor);
			if (r == null)
				return null;
			if (perfLoggingLevel != null && log.isLoggable(perfLoggingLevel))
				LH.log(log, perfLoggingLevel, "PERFORMANCE: CreateState: pid=", partition.getPartitionId(), ", type=", OH.getSimpleClassName(r), ", action=",
						OH.getSimpleClassName(action), "^", BasicDispatcherController.identity(action));
		}
		r.setType(type);
		partition.putState(r);
		fireOnNewState(r);
		getContainer().getPersistenceController().onLocalStateCreated(r, false);
		return r;
	}

	private void fireOnNewState(State r) {
		Iterable<ContainerRuntimeListener> l = listeners;
		if (l == null)
			l = getContainer().getRuntimeListeners();
		for (ContainerRuntimeListener listener : l)
			try {
				listener.onStateCreated(this, r);
			} catch (Exception e) {
				LH.severe(log, "Error firing new state notification on listener: " + listener, e);
			}
	}

	private <A extends Action> State getOrCreateState(Object partitionId, Object stateType, A action, Processor<A, ?> processor) {
		Partition partition = getOrCreatePartition(partitionId);
		State state = partition.getState(stateType);
		return state != null ? state : createState(partition, action, processor);
	}

	@Override
	public <A extends Action> State getOrCreateState(A action, Processor<A, ?> processor) {
		return getOrCreateState(processor.getPartitionResolver().getPartitionId(action), processor.getStateType(), action, processor);
	}

	@Override
	public void putPartition(Partition partition) {
		CH.putOrThrow(partitions, partition.getPartitionId(), partition);
		if (perfLoggingLevel != null && log.isLoggable(perfLoggingLevel))
			LH.log(log, perfLoggingLevel, "PERFORMANCE: CreatePartition: pid=", partition.getPartitionId(), ", type=", OH.getSimpleClassName(partition));
	}

	@Override
	public void putState(String partitionId, State state) {
		getOrCreatePartition(partitionId).putState(state);
		fireOnNewState(state);
		if (isStarted())
			getContainer().getPersistenceController().onLocalStateCreated(state, false);
	}

	@Override
	public void start() {
		super.start();
		this.perfLoggingLevel = getContainer().getDispatchController().getPerformanceLoggingLevel();
		this.listeners = CH.noEmpty(getContainer().getRuntimeListeners(), null);
	}

}
