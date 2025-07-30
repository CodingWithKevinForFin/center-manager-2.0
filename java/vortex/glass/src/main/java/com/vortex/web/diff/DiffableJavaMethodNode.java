package com.vortex.web.diff;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.bytecode.ByteCodeMethod;

public class DiffableJavaMethodNode extends AbstractDiffableNode {

	private ByteCodeMethod method;

	public DiffableJavaMethodNode(ByteCodeMethod method) {
		super(DIFF_TYPE_JAVA_METHOD, toName(method));
		this.method = method;
	}

	private static String toName(ByteCodeMethod method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getReturnTypeText()).append(' ');
		sb.append(method.getNameText());
		return SH.join(',', method.getArgumentsText(), sb.append('(')).append(')').toString();
	}

	public ByteCodeMethod getMethod() {
		return this.method;
	}

	@Override
	public boolean isEqualToNode(DiffableNode node) {
		DiffableJavaMethodNode n = (DiffableJavaMethodNode) node;
		ByteCodeMethod m2 = n.getMethod();
		if (method.getAccessFlags() != m2.getAccessFlags())
			return false;
		if (OH.ne(method.getDescriptorText(), m2.getDescriptorText()))
			return false;
		if (OH.ne(method.getCodeText(), m2.getCodeText()))
			return false;
		return true;
	}

	@Override
	public String getIcon() {
		return "portlet_icon_db_object";
	}

	@Override
	public String getContents() {
		return method.toJavaString();
	}

}
