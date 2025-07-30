package com.vortex.web.diff;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class DiffableProperty extends AbstractDiffableNode {

	private String value;
	private String description;

	public DiffableProperty(String name, String value, String description) {
		super(DIFF_TYPE_PROPERTY, name);
		this.value = value;
		this.description = description;
	}

	@Override
	public boolean isEqualToNode(DiffableNode node) {
		DiffableProperty o = (DiffableProperty) node;
		return OH.eq(value, o.value);
	}
	@Override
	public String getContents() {
		if (SH.is(description))
			return getDiffName() + ": " + value + SH.NEWLINE + description;
		else
			return getDiffName() + ": " + value;
	}

}
