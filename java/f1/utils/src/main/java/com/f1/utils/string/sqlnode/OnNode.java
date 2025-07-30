package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;

final public class OnNode implements Node {
	private static final byte CODE = Node.ON;
	private final Node value;
	private final Node on;
	private final int position;

	public OnNode(int position, Node value, Node on) {
		this.position = position;
		this.value = value;
		this.on = on;
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
		value.toString(sink);
		sink.append(" ON ");
		return on.toString(sink);
	}

	public Node getValue() {
		return value;
	}

	public Node getOn() {
		return on;
	}
	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return 2;
	}

	@Override
	public Node getInnerNode(int n) {
		return n == 0 ? value : on;
	}
}
