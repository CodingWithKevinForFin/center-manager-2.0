package com.f1.persist.writer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.persist.PersistWriteStore;

public class SnapshotPersistStore implements PersistWriteStore {
	private static final Logger log = Logger.getLogger(SnapshotPersistStore.class.getName());

	private Map<Object, Long> ids;
	final private Map<Object, Long> object2id = new HashMap<Object, Long>();

	private TransactionalPersistWriterFactory persistWriter;

	public SnapshotPersistStore(Map<Object, Long> ids, TransactionalPersistWriterFactory persistWriter) {
		this.ids = ids;
		this.persistWriter = persistWriter;
	}

	public int getIdsCount() {
		return object2id.size();
	}

	@Override
	public long registerObject(Object object) {
		Long existing = object2id.get(object);
		if (existing != null)
			return existing;
		Long id = ids.get(object);
		if (id == null) {
			id = persistWriter.getId(object);// fall back to the original store (this is a nested object)
			if (id == null)
				throw new RuntimeException("id missing for: " + object);
		}
		object2id.put(object, id);
		return id;
	}

	@Override
	public Long getIdByObject(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long removeObject(Object target) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getObjects(Map<Object, Long> sink) {
		throw new UnsupportedOperationException();

	}

}
