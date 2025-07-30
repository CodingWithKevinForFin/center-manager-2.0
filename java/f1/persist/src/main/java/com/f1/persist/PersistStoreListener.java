package com.f1.persist;

public interface PersistStoreListener {

	void onObjectAdded(Long id, Object object);

	void onObjectRemoved(Long id, Object object);

}
