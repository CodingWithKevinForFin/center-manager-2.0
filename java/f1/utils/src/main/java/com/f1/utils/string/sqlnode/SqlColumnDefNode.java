package com.f1.utils.string.sqlnode;

import com.f1.utils.CH;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.VariableNode;

final public class SqlColumnDefNode extends SqlNode {

	final private VariableNode name;
	final private VariableNode type;
	final private UseNode use;

	public SqlColumnDefNode(int position, Node next, VariableNode name, VariableNode type, UseNode use) {
		super(position, next, SqlExpressionParser.ID_INVALID);
		this.name = name;
		this.type = type;
		this.use = use;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		this.name.toString(sink);
		sink.append(' ');
		this.type.toString(sink);
		if (use != null && CH.isntEmpty(use.getOptionsMap()))
			this.use.toString(sink);
		return sink;
	}

	public VariableNode getName() {
		return name;
	}
	public VariableNode getType() {
		return type;
	}

	public UseNode getUse() {
		return this.use;
	}

	@Override
	public int getInnerNodesCount() {
		int n = 2;
		if (use != null)
			n++;
		if (getNext() != null)
			n++;
		return n;
	}

	@Override
	public Node getInnerNode(int n) {
		switch (n) {
			case 0:
				return name;
			case 1:
				return type;
			case 2:
				if (use != null)
					return use;
		}
		return getNext();
	}
}
