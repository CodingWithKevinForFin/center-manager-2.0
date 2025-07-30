package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;

final public class WhereNode extends SqlNode {
	private final Node condition;

	public WhereNode(int position, Node condition, Node next) {
		super(position, next, SqlExpressionParser.ID_WHERE);
		this.condition = condition;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(SqlExpressionParser.toOperationString(getOperation()));
		if (condition != null)
			condition.toString(sink.append(' '));
		if (getNext() != null)
			getNext().toString(sink.append(' '));
		return sink;
	}

	public Node getCondition() {
		return condition;
	}

	@Override
	public int getInnerNodesCount() {
		return getNext() == null ? 1 : 2;
	}

	@Override
	public Node getInnerNode(int n) {
		return n == 0 ? condition : getNext();
	}
}
