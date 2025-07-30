package com.vortex.web.diff;

public class BasicDiffableNode extends AbstractDiffableNode {

	public BasicDiffableNode(String name) {
		super(DIFF_TYPE_BASIC, name);
	}

	@Override
	public boolean isEqualToNode(DiffableNode node) {
		return true;
	}

	@Override
	public String getContents() {
		return "";
	}

}
