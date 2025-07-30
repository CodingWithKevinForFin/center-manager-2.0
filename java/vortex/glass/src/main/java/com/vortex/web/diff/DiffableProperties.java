package com.vortex.web.diff;

public class DiffableProperties extends AbstractDiffableNode {

	public DiffableProperties(String name) {
		super(DIFF_TYPE_PROPERTIES, name);
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
