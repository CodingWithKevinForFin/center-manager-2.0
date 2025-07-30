package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class WhileNode implements Node {

	public static final byte CODE = Node.WHILE;
	private final Node conditions;
	private final Node block;

	public WhileNode(int position, Node conditions, Node block) {
		this.position = position;
		this.conditions = conditions;
		this.block = block;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	final private int position;

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("while( ");
		conditions.toString(sink);
		sink.append(" ) ");
		return block.toString(sink);
	}

	public Node getConditions() {
		return conditions;
	}

	public Node getBlock() {
		return block;
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
		return n == 0 ? conditions : block;
	}

}
