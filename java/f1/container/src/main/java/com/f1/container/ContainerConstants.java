package com.f1.container;

import com.f1.utils.F1GlobalProperties;

public interface ContainerConstants {

	int DEFAULT_THREAD_POOL_SIZE = 8;
	String DEFAULT_THREAD_POOL_KEY = "F1POOL";

	char NAME_SEPERATOR = '/';
	char NAME_PORT_SEPERATOR = '/';
	int MAX_DEBUG_LENGTH = F1GlobalProperties.getMaxDebugStringLength();

}
