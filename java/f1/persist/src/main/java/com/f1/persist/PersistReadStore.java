package com.f1.persist;

import java.util.Map;

public interface PersistReadStore {

	void addObject(Long id, Object object);

	Object getObjectById(Long id);

	Long getIdByObject(Object o);

	Object removeObject(Long objectId);

	public void getObjects(Map<Long, Object> sink);

	void clear();

}
