package com.f1.persist;

import java.util.Map;

public interface PersistWriteStore {

	/**
	 * if the object already exists, then the negative value of the existing value will be returned
	 * 
	 * @param object
	 * @return id (multiplied by -1 if already existed), never null or zero
	 */
	long registerObject(Object object);

	Long getIdByObject(Object object);

	Long removeObject(Object target);

	/**
	 * Should be thread safe, as this may be called by other threads (during snapshot specifically)
	 * 
	 * @param sink
	 *            data structure to have objects and there id's added to
	 */
	void getObjects(Map<Object, Long> sink);

}
