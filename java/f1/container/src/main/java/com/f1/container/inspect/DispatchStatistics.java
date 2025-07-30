package com.f1.container.inspect;

import com.f1.container.ContainerUid;

public interface DispatchStatistics extends DispatchInspector {

	long getStatistics(ContainerUid cs);
}
