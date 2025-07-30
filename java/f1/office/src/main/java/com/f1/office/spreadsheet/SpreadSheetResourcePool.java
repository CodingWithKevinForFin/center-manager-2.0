package com.f1.office.spreadsheet;

import java.util.LinkedHashMap;

import com.f1.base.LockedException;
import com.f1.utils.OH;

public abstract class SpreadSheetResourcePool<T extends SpreadSheetResource> {

	private T tmp;
	private boolean isBorrowed = false;
	private LinkedHashMap<T, T> pool = new LinkedHashMap<T, T>();

	public T borrowTmp() {
		OH.assertFalse(isBorrowed);
		isBorrowed = true;
		final T r = tmp;
		if (r == null)
			return nw();
		this.tmp = null;
		return r;
	}

	public abstract T nw();

	public T share(T tmp) {
		LockedException.assertNotLocked(tmp);
		OH.assertTrue(isBorrowed);
		isBorrowed = false;
		T existing = pool.get(tmp);
		if (existing != null) {
			tmp.clear();
			this.tmp = tmp;
			return existing;
		}
		int id = pool.size();
		tmp.setId(id);
		tmp.lock();
		pool.put(tmp, tmp);
		return tmp;
	}

	public Iterable<T> getResourcesInOrder() {
		return this.pool.values();
	}

	public int getResourcesCount() {
		return this.pool.size();
	}
	
	public T getById(Integer id) {
		if (id == null) 
			return null;
		for (T t: pool.values()) {
			if (t.getId() == id)
				return t;
		}
		return null;
	}
}
