package com.f1.persist.reader;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.f1.persist.PersistReadStore;
import com.f1.utils.OH;

public class BasicPersistReadStore implements PersistReadStore {
	final private Map<Long, Object> id2object = new HashMap<Long, Object>();
	final private Map<Object, Long> object2id = new IdentityHashMap<Object, Long>();

	@Override
	public void addObject(Long id, Object object) {
		id2object.put(id, object);
		object2id.put(object, id);
	}

	@Override
	public Object removeObject(Long objectId) {
		Object object = id2object.remove(objectId);
		if (object != null)
			object2id.get(object);
		return object;
	}

	@Override
	public Object getObjectById(Long id) {
		return id2object.get(id);
	}

	@Override
	public Long getIdByObject(Object o) {
		return object2id.get(o);
	}

	@Override
	public void getObjects(Map<Long, Object> sink) {
		sink.putAll(id2object);
	}

	@Override
	public void clear() {
		id2object.clear();
		object2id.clear();
	}

}
