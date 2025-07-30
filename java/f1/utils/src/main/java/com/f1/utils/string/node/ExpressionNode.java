package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class ExpressionNode implements Node {
	public static final byte CODE = Node.EXPRESSION;
	private final Node value;

	public ExpressionNode(int position, Node value) {
		this.position = position;
		this.value = value;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public final int position;

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('(');
		value.toString(sink);
		return sink.append(')');
	}

	public Node getValue() {
		return value;
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
		return value;
	}

}
