package com.vortex.web.diff;

import com.f1.utils.OH;
import com.f1.utils.bytecode.ByteCodeField;

public class DiffableJavaFieldNode extends AbstractDiffableNode {

	private ByteCodeField field;

	public DiffableJavaFieldNode(ByteCodeField field) {
		super(DIFF_TYPE_JAVA_FIELD, field.getNameText());
		this.field = field;
	}

	@Override
	public boolean isEqualToNode(DiffableNode node) {
		DiffableJavaFieldNode n = (DiffableJavaFieldNode) node;
		ByteCodeField f2 = n.getField();
		if (field.getAccessFlags() != f2.getAccessFlags())
			return false;
		if (OH.ne(field.getConstValue(), f2.getConstValue()))
			return false;
		if (OH.ne(field.getDescriptorText(), f2.getDescriptorText()))
			return false;
		//TODO: compare annotations
		return true;
	}

	private ByteCodeField getField() {
		return field;
	}

	@Override
	public String getIcon() {
		return "portlet_icon_field";
	}

	@Override
	public String getContents() {
		return field.toJavaString();
	}

}
