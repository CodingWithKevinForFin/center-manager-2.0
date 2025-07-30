package com.f1.utils.fix.impl;

import java.util.Iterator;

import com.f1.utils.SH;
import com.f1.utils.fix.FixGroup;
import com.f1.utils.fix.FixMap;
import com.f1.utils.structs.ArrayIterator;

public class BasicFixGroup implements FixGroup {

	public static final BasicFixGroup EMPTY = new BasicFixGroup(0);

	private int size;
	final private FixMap[] groups;

	public BasicFixGroup(int totalSize) {
		this.groups = new FixMap[totalSize];
		this.size = 0;
	}

	@Override
	public Iterator<FixMap> iterator() {
		return new ArrayIterator<FixMap>(groups);
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return SH.join(',', groups, sb);
	}

	@Override
	public FixMap get(int offset) {
		return groups[offset];
	}

	@Override
	public int size() {
		return groups.length;
	}

	public void addGroup(FixMap group) {
		if (group == null)
			throw new NullPointerException();
		groups[size++] = group;
	}

}
