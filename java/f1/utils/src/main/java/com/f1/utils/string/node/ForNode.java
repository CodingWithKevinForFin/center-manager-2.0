package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class ForNode implements Node {

	public static final byte CODE = Node.FOR;
	private final Node inits;
	private final Node conditions;
	private final Node ops;
	private final Node block;

	public ForNode(int position, Node inits, Node conditions, Node ops, Node block) {
		this.position = position;
		this.inits = inits;
		this.conditions = conditions;
		this.ops = ops;
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
		if (inits != null)
			inits.toString(sink);
		sink.append(" ; ");
		if (conditions != null)
			conditions.toString(sink);
		sink.append(" ; ");
		if (ops != null)
			ops.toString(sink);
		sink.append(" ) ");
		return block.toString(sink);
	}

	public Node getInits() {
		return inits;
	}

	public Node getConditions() {
		return conditions;
	}

	public Node getOps() {
		return ops;
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
		if (this.inits != null)
			n++;
		if (this.conditions != null)
			n++;
		if (this.ops != null)
			n++;
		if (this.block != null)
			n++;
		return n;
	}

	@Override
	public Node getInnerNode(int n) {
		if (this.inits != null && n-- == 0)
			return this.inits;
		if (this.conditions != null && n-- == 0)
			return this.conditions;
		if (this.ops != null && n-- == 0)
			return this.ops;
		if (this.block != null && n-- == 0)
			return this.block;
		throw new IndexOutOfBoundsException();
	}
}
