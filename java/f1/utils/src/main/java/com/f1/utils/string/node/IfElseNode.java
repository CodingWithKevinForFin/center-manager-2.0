package com.f1.utils.string.node;

import com.f1.utils.OH;
import com.f1.utils.string.Node;

final public class IfElseNode implements Node {

	public static final byte CODE = Node.IFELSE;
	final private Node ifClause;
	final private Node ifBlock;
	final private Node elseBlock;

	public IfElseNode(int position, Node ifClause, Node ifBlock, Node elseBlock) {
		this.position = position;
		OH.assertNotNull(ifClause);
		OH.assertNotNull(ifBlock);
		this.ifClause = ifClause;
		this.ifBlock = ifBlock;
		this.elseBlock = elseBlock;
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
		sink.append("if ( ").append(ifClause).append(" ) ");
		if (ifBlock != null) {
			ifBlock.toString(sink);
			if (elseBlock != null) {
				sink.append(" else ");
				elseBlock.toString(sink);
			}
		}
		return sink;
	}

	public Node getIfClause() {
		return ifClause;
	}

	public Node getIfBlock() {
		return ifBlock;
	}

	public Node getElseBlock() {
		return elseBlock;
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return elseBlock == null ? 2 : 3;
	}

	@Override
	public Node getInnerNode(int n) {
		switch (n) {
			case 0:
				return this.ifClause;
			case 1:
				return this.ifBlock;
			default:
				return this.elseBlock;
		}
	}

}
