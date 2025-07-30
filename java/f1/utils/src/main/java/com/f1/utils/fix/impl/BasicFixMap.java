package com.f1.utils.fix.impl;

import com.f1.base.IntIterator;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.fix.FixGroup;
import com.f1.utils.fix.FixMap;
import com.f1.utils.fix.FixParseException;
import com.f1.utils.fix.FixTag;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;

public class BasicFixMap implements FixMap, Lockable {
	public static final BasicFixMap EMPTY = new BasicFixMap();
	static {
		EMPTY.lock();
	}
	private IntKeyMap<String> values = new IntKeyMap<String>();
	private IntKeyMap<BasicFixGroup> groups = new IntKeyMap<BasicFixGroup>();
	private boolean isLocked;

	@Override
	public String get(int key) {
		String r = values.get(key);
		if (r == null)
			throw new RuntimeException("tag not found: " + key);
		return r;
	}

	@Override
	public void lock() {
		this.isLocked = true;
	}

	@Override
	public String get(int key, String dflt) {
		return OH.noNull(values.get(key), dflt);
	}

	@Override
	public <T> T getAs(int key, Class<T> type) {
		try {
			return OH.cast(values.get(key), type, true);
		} catch (Exception e) {
			throw new FixParseException("error converting required fix tag " + key + " to a " + type.getName() + ": " + e.getMessage(), e);
		}
	}

	@Override
	public <T> T getAs(int key, Class<T> type, T dflt) {
		try {
			return OH.noNull(OH.cast(values.get(key), type, false), dflt);
		} catch (Exception e) {
			throw new FixParseException("error converting fix tag " + key + " to a " + type.getName() + ": " + e.getMessage(), e);
		}
	}

	@Override
	public FixGroup getGroups(int key) {
		BasicFixGroup r = groups.get(key);
		if (r == null)
			throw new RuntimeException("group not found: " + key);
		return r;
	}

	@Override
	public FixGroup getGroupsNoThrow(int key) {
		return groups.get(key);
	}

	@Override
	public FixMap getGroupAt(int key, int offset) {
		return getGroups(key).get(offset);
	}

	@Override
	public int getGroupsCount(int key) {
		return getGroups(key).size();
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		for (IntKeyMap.Node<String> i : CH.sort(values)) {
			sb.append(i.getIntKey()).append('=').append(i.getValue()).append('|');
			Node<BasicFixGroup> groupNode = groups.getNode(i.getIntKey());
			if (groupNode != null) {
				sb.append('{');
				boolean first = true;
				for (FixMap m : groupNode.getValue()) {
					if (first)
						first = false;
					else
						sb.append(',');
					((BasicFixMap) m).toString(sb);
				}
				sb.append('}');
			}
		}
		return sb;
	}

	@Override
	public StringBuilder toLegibleString(StringBuilder sb) {
		return toLegibleString(sb, "");
	}

	@Override
	public StringBuilder toLegibleString(StringBuilder sb, String prefix_) {
		String prefix2 = prefix_ + "  ";
		for (IntKeyMap.Node<String> i : CH.sort(values)) {
			sb.append(prefix_).append(i.getIntKey()).append('=').append(i.getValue()).append(SH.NEWLINE);
			Node<BasicFixGroup> groupNode = groups.getNode(i.getIntKey());
			if (groupNode != null) {
				boolean first = true;
				for (FixMap m : groupNode.getValue()) {
					if (first)
						first = false;
					else
						sb.append(prefix2).append("---<break>---").append(SH.NEWLINE);
					((BasicFixMap) m).toLegibleString(sb, prefix2);
				}

			}
		}
		return sb;
	}

	public void put(int key, String value_) {
		if (isLocked)
			throw new LockedException();
		values.put(key, value_);
	}

	public void put(int key, FixMap grouped_) {
		if (isLocked)
			throw new LockedException();
		BasicFixGroup list = groups.get(key);
		if (list == null)
			throw new RuntimeException("Group not prepared: " + key);
		list.addGroup(grouped_);
	}

	public boolean containsKey(int key) {
		return values.get(key) != null;
	}

	public void prepareGroup(int key, int expectedSize) {
		if (isLocked)
			throw new LockedException();
		put(key, SH.toString(expectedSize));
		if (groups.get(key) != null)
			throw new RuntimeException("Group already exists: " + key);
		groups.put(key, new BasicFixGroup(expectedSize));

	}

	@Override
	public boolean isLocked() {
		return isLocked;
	}

	@Override
	public IntIterator getKeys() {
		return values.keyIterator();
	}

	@Override
	public boolean isGroup(int key) {
		return groups.getNode(key) != null;
	}

	@Override
	public String get(FixTag fixTag) {
		return get(fixTag.getTag());
	}

	@Override
	public String get(FixTag fixTag, String defaultValue) {
		return get(fixTag.getTag(), defaultValue);
	}

	@Override
	public <T> T getAs(FixTag fixTag, Class<T> type) {
		return getAs(fixTag.getTag(), type);
	}

	@Override
	public <T> T getAs(FixTag fixTag, Class<T> type, T defaultValue) {
		return getAs(fixTag.getTag(), type, defaultValue);
	}

	@Override
	public FixGroup getGroups(FixTag fixTag) {
		return getGroups(fixTag.getTag());
	}

	@Override
	public FixGroup getGroupsNoThrow(FixTag fixTag) {
		return getGroupsNoThrow(fixTag.getTag());
	}

	@Override
	public FixMap getGroupAt(FixTag fixTag, int offset) {
		return getGroupAt(fixTag.getTag(), offset);
	}

	@Override
	public int getGroupsCount(FixTag fixTag) {
		return getGroupsCount(fixTag.getTag());
	}

	@Override
	public boolean isGroup(FixTag fixTag) {
		return isGroup(fixTag.getTag());
	}

	@Override
	public String toLegibleString() {
		return toLegibleString(new StringBuilder()).toString();
	}
}
