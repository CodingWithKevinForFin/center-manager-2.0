package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class FlowControlNode implements Node {

	public static final byte CODE = Node.FLOWCONTROL;
	private final Node param;
	private final String statement;
	private final int position;

	public FlowControlNode(int position, String statement, Node param) {
		this.statement = statement;
		this.param = param;
		this.position = position;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(statement);
		if (param != null)
			param.toString(sink.append(' '));
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public int getPosition() {
		return position;
	}

	public Node getParam() {
		return param;
	}

	public String getStatement() {
		return statement;
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
