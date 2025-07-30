package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;

final public class AsNode implements Node {

	public static final byte CODE = Node.AS;
	private Node value;
	private final Node as;
	private final int position;
	private final boolean explicit;

	public AsNode(int position, Node value, Node as, boolean explicit) {
		this.position = position;
		this.value = value;
		this.explicit = explicit;
		this.as = as;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (value != null)
			value.toString(sink);
		sink.append(" AS ");
		return as.toString(sink);
	}

	public Node getValue() {
		return value;
	}

	public void setValue(Node value) {
		this.value = value;
	}

	public Node getAs() {
		return as;
	}

	public boolean isExplicit() {
		return explicit;
	}
	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return as == null ? 1 : 2;
	}

	@Override
	public Node getInnerNode(int n) {
		return n == 0 ? value : as;
	}
}
