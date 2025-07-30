/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.f1.base.Action;

/**
 * manages the lifecycle of {@link Partition}s and their {@link State}s. Please note that there is one partition controller per {@link Container}. As partitions are created and
 * removed from the container, their existence is maintained within this partition controller, such that A collection of active partitions is maintained, organized by the partition
 * id via {@link Partition#getPartitionId()}. One can access the set of existing partition ids by calling {@link #getPartitions()}<BR>
 * The auto-creation of new states and partitions is also managed by registering {@link PartitionGenerator}s and {@link StateGenerator}s. As such, if a {@link PartitionResolver}
 * were to return a new (aka unknown) partitionId then the various said generators would be called upon to auto-create the partition and state.
 * <P>
 * Note: the partition controller is completely thread safe<BR>
 * Note: all partitions within a controller must have a unique partitionid<BR>
 * Note: see {@link Container#getPartitionController()}<BR>
 */
public interface PartitionController extends ContainerScope {

	/**
	 * returns the partition associated with supplied id, or null if none exists.
	 * 
	 * @param partitionId
	 *            partition id, {@link Partition#getPartitionId()}
	 * @return the partition id associated with t he supplied id
	 */
	Partition getPartition(Object partitionId);

	/**
	 * all partition ids registered with this container scope
	 * 
	 * @return set, do not modify
	 */
	Collection<Object> getPartitions();

	/**
	 * remove the partition assoicated with supplied if one exists.
	 * 
	 * @param partionId
	 *            the partition id of the partition to remove see {@link Partition#getPartitionId()}
	 * @return the associated partition or null
	 */
	Partition removePartition(Object partionId);

	/**
	 * return the partition associated with the partitionid. If non exists create a new one using the {@link PartitionGenerator#createPartition(Object)}
	 * 
	 * @param partitionId
	 *            the id of the partition to return (potentially creating)
	 * @return the partition
	 */
	Partition getOrCreatePartition(Object partitionId);

	/**
	 * return the associated partition generators (for use in {@link #getOrCreatePartition(Object)}
	 * 
	 * @return associated partition generators
	 */
	List<PartitionGenerator> getPartitionGenerators();

	/**
	 * add an associated partition generator (for use in {@link #getOrCreatePartition(Object)}. Later generators get a <B>higher</B> priority when determining which generator will
	 * be used for a given partition id
	 * 
	 * @param partitionGenerator
	 *            the generator, must not be null
	 */
	void registerPartitionGenerator(PartitionGenerator partitionGenerator);

	/**
	 * return all of the state generators registered, keyed by the type of state they can handle
	 * 
	 * @return map of state generators, keyed by associated state type. do not modify
	 */
	Map<Object, StateGenerator> getStateGenerators();

	/**
	 * add a new state generator to this. will throw an exception if a different state generator (see {@link StateGenerator#areEqual(StateGenerator)} ) with the same state type
	 * (see {@link StateGenerator#getStateType()})
	 * 
	 * @param stateGenerator
	 *            the state generator to add
	 */
	void registerStateGenerator(StateGenerator stateGenerator);

	/**
	 * add a new state generator to this. will replace, if a different state generator (see {@link StateGenerator#areEqual(StateGenerator)} ) with the same state type (see
	 * {@link StateGenerator#getStateType()})
	 * 
	 * @param stateGenerator
	 *            the state generator to add
	 */
	StateGenerator overrideStateGenerator(StateGenerator stateGenerator_);

	/**
	 * return a state for the supplied partitionId, with the supplied stateType. if the partition or state doesn't exist, return null
	 * 
	 * @param partitionId
	 *            the partition id of which the state is a member of
	 * @param stateType
	 *            the type of state
	 * @return the existing state, or null
	 * @see StateGenerator
	 * @see PartitionGenerator
	 */
	<T extends State> T getState(Object partitionid, Class<T> type);

	/**
	 * return a state for the supplied action, and processor. if the partition doesn't exist, create a new partition first.Then, if none exists create a new state.
	 * 
	 * @param action
	 * @param processor
	 * @return the existing or newely created state
	 * @see StateGenerator
	 * @see PartitionGenerator
	 */
	<A extends Action> State getOrCreateState(A action, Processor<A, ?> processor);

	/**
	 * create a new state and add to the supplied partition
	 * 
	 * @param partition
	 *            the partition that the newly state will be added to (must not already have a state of the state type)
	 * @param action
	 *            the initial action which inspired the creation of this state.
	 * @param processor
	 *            the processor which the supplied action will be processed on
	 * @return the newly created state
	 */
	State createState(Partition partition, Action action, Processor processor);

	void putPartition(Partition partition);

	void putState(String string, State state);

}
