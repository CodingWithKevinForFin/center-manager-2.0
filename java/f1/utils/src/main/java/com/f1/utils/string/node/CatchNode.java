package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class CatchNode implements Node {
	public static final byte CODE = Node.CATCH;

	final private VariableNode type;
	final private VariableNode var;
	final private BlockNode node;
	final private int position;

	public CatchNode(int position, VariableNode type, VariableNode var, BlockNode catchNode) {
		this.position = position;
		this.type = type;
		this.var = var;
		this.node = catchNode;
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
		sink.append("catch(");
		type.toString(sink);
		sink.append(' ');
		var.toString(sink);
		sink.append(')');
		node.toString(sink);
		return sink;
	}

	public VariableNode getType() {
		return type;
	}

	public VariableNode getVar() {
		return var;
	}

	public BlockNode getNode() {
		return node;
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return 3;
	}

	@Override
	public Node getInnerNode(int n) {
		switch (n) {
			case 0:
				return this.type;
			case 1:
				return this.var;
			default:
				return this.node;
		}
	}
}
