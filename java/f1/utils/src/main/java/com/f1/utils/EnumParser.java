package com.f1.utils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.ToStringable;
import com.f1.utils.structs.LongKeyMap;

public class EnumParser implements Lockable, ToStringable {

	private LongKeyMap<String> names = new LongKeyMap<String>();
	private Map<String, Long> values = new HashMap<String, Long>();
	private boolean locked;
	private String description;
	private Set<String> namesSorted;
	public EnumParser(String description) {
		this.description = description;

	}

	public String getDescription() {
		return this.description;
	}

	public EnumParser define(long key, String value) {
		LockedException.assertNotLocked(this);
		this.names.putOrThrow(key, value);
		CH.putOrThrow(values, value, key);
		return this;
	}

	public String toString(long value) {
		String r = names.get(value);
		if (r != null)
			return r;
		throw new RuntimeException(description + " must be one of [" + SH.join(", ", values.values()) + "], Unknown: " + value);
	}

	public long parse(String key) {
		if (SH.isnt(key))
			throw new RuntimeException("Missing " + description);
		Long r = values.get(key);
		if (r != null)
			return r.longValue();
		throw new RuntimeException(description + " must be one of [" + SH.join(", ", values.keySet()) + "], Unknown: " + key);
	}
	public String toStringNoThrow(long value) {
		final String r = names.get(value);
		return (r == null) ? SH.toString(value) : r;
	}

	public Long parseNoThrow(String key) {
		return values.get(key);
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return names.toString(sink);
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public Set<String> getKeys() {
		LockedException.assertLocked(this);
		return this.namesSorted;
	}

	@Override
	public void lock() {
		this.locked = true;
		this.namesSorted = new LinkedHashSet<String>(CH.sort(this.values.keySet()));
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

}
