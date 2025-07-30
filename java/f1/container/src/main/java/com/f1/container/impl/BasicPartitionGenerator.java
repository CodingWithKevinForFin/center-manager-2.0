package com.f1.container.impl;

import com.f1.container.Partition;
import com.f1.container.PartitionGenerator;
import com.f1.utils.EH;

public class BasicPartitionGenerator extends AbstractContainerScope implements PartitionGenerator {

	@Override
	public Partition createPartition(Object partitionId) {
		String threadpoolKey = getContainer().getThreadPoolController().getDefaultThreadPoolKey();
		return new BasicPartition(getContainer(), partitionId, threadpoolKey, EH.currentTimeMillis(), Partition.OPTION_SUPPORT_CONFLATION | Partition.OPTION_SUPPORT_HIGH_PRIORITY
				| Partition.OPTION_SUPPORT_LOW_PRIORITY);
	}

}
