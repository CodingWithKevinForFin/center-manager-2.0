package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;
import com.f1.utils.string.node.StringTemplateNode;

final public class SqlDeferredNode implements Node {

	public static final byte CODE = Node.SQL_DEFFERED;
	final private StringTemplateNode node;

	public SqlDeferredNode(StringTemplateNode node) {
		this.node = node;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return node.toString(sink);
	}

	@Override
	public int getPosition() {
		return node.getPosition();
	}

	public StringTemplateNode getInner() {
		return this.node;
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return 1;
	}

	@Override
	public Node getInnerNode(int n) {
		return this.node;
	}

}
