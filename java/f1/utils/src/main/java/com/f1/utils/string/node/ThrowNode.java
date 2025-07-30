package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class ThrowNode implements Node {

	public static final byte CODE = Node.THROW;
	final private int position;
	final private Node node;

	public ThrowNode(int position, Node node) {

		this.position = position;
		this.node = node;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("throw ");
		node.toString(sink);
		return sink;
	}

	@Override
	public int getPosition() {
		return position;
	}

	public Node getNode() {
		return node;
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
		return node;
	}
}
