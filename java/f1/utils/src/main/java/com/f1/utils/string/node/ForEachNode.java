package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class ForEachNode implements Node {

	public static final byte CODE = Node.FOREACH;
	private final DeclarationNode var;
	private final Node array;
	private final Node block;

	public ForEachNode(int position, DeclarationNode var, Node array, Node block) {
		if (var.getParam() != null)
			throw new IllegalArgumentException();
		this.position = position;
		this.var = var;
		this.array = array;
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
		sink.append("for( ");
		var.toString(sink);
		sink.append(" : ");
		array.toString(sink);
		sink.append(" ) ");
		return block.toString(sink);
	}

	public DeclarationNode getVar() {
		return var;
	}

	public Node getArray() {
		return array;
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
		int n = 0;
		if (this.var != null)
			n++;
		if (this.array != null)
			n++;
		if (this.block != null)
			n++;
		return n;
	}

	@Override
	public Node getInnerNode(int n) {
		if (this.var != null && n-- == 0)
			return this.var;
		if (this.array != null && n-- == 0)
			return this.array;
		if (this.block != null && n-- == 0)
			return this.block;
		throw new IndexOutOfBoundsException();
	}
}
