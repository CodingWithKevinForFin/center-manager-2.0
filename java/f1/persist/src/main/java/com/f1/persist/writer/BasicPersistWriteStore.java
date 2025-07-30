package com.f1.persist.writer;

import java.util.Map;

import com.f1.persist.PersistWriteStore;
import com.f1.utils.concurrent.ConcurrentCopyMap;
import com.f1.utils.impl.IdentityHasher;

public class BasicPersistWriteStore implements PersistWriteStore {
	final private ConcurrentCopyMap<Object, Long> object2id = new ConcurrentCopyMap<Object, Long>();
	private long nextId;

	public BasicPersistWriteStore(long firstId) {
		this.nextId = firstId + 1;
		object2id.setHasher(IdentityHasher.INSTANCE);
	}

	@Override
	public Long getIdByObject(Object object) {
		return object2id.get(object);
	}

	@Override
	public long registerObject(Object object) {
		Long existing = object2id.get(object);
		if (existing != null)
			return -existing;
		object2id.put(object, nextId);
		return nextId++;
	}

	@Override
	public Long removeObject(Object target) {
		Long id = object2id.remove(target);
		return id;
	}

	@Override
	public void getObjects(Map<Object, Long> sink) {
		object2id.getEntriesCopy(sink);
	}

}
