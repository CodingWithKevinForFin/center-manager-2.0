package com.f1.container;

import com.f1.container.impl.ContainerHelper;

/**
 * indicates that the object instance can be uniquely identified (for the life of the JVM) using an id. Typically, this should be for objects that aren't 'highly transient'. The
 * intent is that the number should be relatively 'small'.
 * 
 * @see ContainerHelper#nextContainerScopeUid()
 * 
 * @author rcooke
 * 
 */
public interface ContainerUid {

	/**
	 * @return the unique id which represents this object. Must be zero or a positive number.
	 */
	long getContainerScopeUid();
}
