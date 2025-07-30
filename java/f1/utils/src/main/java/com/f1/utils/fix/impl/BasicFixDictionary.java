package com.f1.utils.fix.impl;

import com.f1.utils.fix.FixDictionary;
import com.f1.utils.fix.FixTag;
import com.f1.utils.structs.IntKeyMap;

public class BasicFixDictionary implements FixDictionary {

	private IntKeyMap<FixTag> tags = new IntKeyMap<FixTag>(4096);

	@Override
	public FixTag getFixTag(int id) {
		return tags.get(id);
	}

	@Override
	public void putFixTag(FixTag tag) {
		if (tags.get(tag.getTag()) != null)
			throw new RuntimeException("tag already exists: " + tag);
		tags.put(tag.getTag(), tag);
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return tags.toString(sb);
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
}
