package com.f1.utils.structs.table.derived;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.utils.CH;

public class BasicExternFactoryManager implements ExternFactoryManager, Lockable {

	private Map<String, Extern> externs = new HashMap<String, Extern>();
	private boolean locked;

	@Override
	public Extern getExternNoThrow(String language) {
		this.locked = true;
		return externs.get(language);
	}

	@Override
	public Set<String> getSupportedLanguages() {
		this.locked = true;
		return externs.keySet();
	}

	public void addLanguage(String name, Extern ext) {
		LockedException.assertNotLocked(this);
		CH.putOrThrow(this.externs, name, ext);
	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

}
