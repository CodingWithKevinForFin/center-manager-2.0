package com.f1.utils.fix.impl;

import java.util.Arrays;

import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.fix.FixTag;

public class BasicFixTag implements FixTag {

	private static final int NO_TAG = -1;
	final private int tag;
	final private String name;
	final private boolean isGroup;
	final private int startTag;
	final private int[] permissibleTags;

	public BasicFixTag(int tag, String name, int startTag, int permissibleTags[]) {
		this.tag = tag;
		this.name = name;
		this.isGroup = true;
		this.startTag = startTag;
		Arrays.sort(Arrays.copyOf(permissibleTags, permissibleTags.length));
		this.permissibleTags = permissibleTags;
	}

	public BasicFixTag(int tag, String name) {
		this.tag = tag;
		this.name = name;
		this.isGroup = false;
		this.startTag = NO_TAG;
		this.permissibleTags = OH.EMPTY_INT_ARRAY;
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public boolean isGroup() {
		return isGroup;
	}

	@Override
	public int getStartTag() {
		return startTag;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return sb.append(name).append('(').append(tag).append(')');
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public boolean getIsInGroup(int fix) {
		return AH.indexOfSorted(fix, permissibleTags) != -1;
	}
}
