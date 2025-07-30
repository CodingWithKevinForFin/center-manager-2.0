package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class CastNode implements Node {

	public static final byte CODE = Node.CAST;
	private final String castTo;
	private final Node param;
	private final int position;

	public CastNode(int position, String castTo, Node param) {
		this.position = position;
		this.castTo = castTo;
		this.param = param;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('(').append(castTo).append(") ");
		return param.toString(sink);
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public String getCastTo() {
		return castTo;
	}

	public Node getParam() {
		return param;
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
		return param;
	}
}
