package com.f1.ami.web.diff;

import java.util.Collection;
import java.util.Collections;

import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.OH;

public class AmiWebJsonDiffNodeObj extends AmiWebJsonDiffNode<Object> {

	final private byte isSame;

	public AmiWebJsonDiffNodeObj(AmiWebJsonDifferPortlet owner, String key, AmiWebJsonDiffNode parent, Object orig, Object left, Object right) {
		super(owner, key, parent, orig, left, right);
		byte isSame = 0;
		if (OH.eq(orig, left))
			isSame |= SAME_BL;
		if (OH.eq(orig, right))
			isSame |= SAME_BR;
		if (OH.eq(left, right))
			isSame |= SAME_LR;
		this.isSame = isSame;
	}

	@Override
	public byte getType() {
		return TYPE_OBJ;
	}

	@Override
	public byte getSameness() {
		return isSame;
	}

	@Override
	protected void addToTree(WebTreeNode root, boolean onlyChanges) {

	}

	@Override
	protected Object buildJsonFromChildren() {
		throw new IllegalStateException();
	}

	protected boolean canHaveChildren() {
		return false;//TODO:
	}

	@Override
	public Collection<AmiWebJsonDiffNode<?>> getChildren() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public byte getThisSameness() {
		return isSame;
	}
}
