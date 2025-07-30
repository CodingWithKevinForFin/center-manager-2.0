package com.f1.utils.structs;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.structs.SkipList.Block;

public class SkipListEntry {
	private static final Logger log = LH.get();

	int offset;
	private SkipList<?>.Block parent;

	protected void setParent(SkipList<?>.Block parent, int offset) {
		this.offset = offset;
		this.parent = parent;
	}
	protected void setParentToNull(SkipList<?>.Block current) {
		if (this.parent != current)
			throw new RuntimeException();
		this.offset = -1;
		this.parent = null;
	}
	public int getLocation() {
		if (parent == null)
			return -1;
		parent.ensureOffsetOkay(this);
		return offset + parent.getOffset();
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Block getBlock() {
		return parent;
	}

	public SkipList getSkipList() {
		return parent == null ? null : parent.getSkipList();
	}

}