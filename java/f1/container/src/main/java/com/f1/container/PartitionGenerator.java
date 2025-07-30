package com.f1.container;

/**
 * Responsible for creating new partitions, and associating with a particular
 * partition id.
 */
public interface PartitionGenerator extends ContainerScope {

	/**
	 * create a partition with the supplied id. if unable to create a partition
	 * for the supplied id, return null
	 * 
	 * @param partitionId
	 *            the partition id of the new partition
	 * @return the newely created partition, or null if can not create
	 *         partitions for supplied configuration
	 */
	Partition createPartition(Object partitionId);

}
