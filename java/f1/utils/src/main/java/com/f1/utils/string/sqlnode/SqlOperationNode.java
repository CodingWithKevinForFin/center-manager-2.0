package com.f1.utils.string.sqlnode;

import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.VariableNode;

final public class SqlOperationNode extends SqlNode {
	private final Node name;

	public SqlOperationNode(int position, Node name, Node next, int operation) {
		super(position, next, operation);
		this.name = name;
	}
	public String getNameAsString() {
		if (name instanceof VariableNode)
			return ((VariableNode) name).getVarname();
		if (name instanceof ConstNode)
			return ((ConstNode) name).toString();
		if (name instanceof MethodNode)
			return ((MethodNode) name).toString();
		else
			throw new ExpressionParserException(getPosition(), "Expecting Const or Variable not: " + name.getClass().getSimpleName());
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (getOperation() != SqlExpressionParser.ID_INVALID)
			sink.append(SqlExpressionParser.toOperationString(getOperation())).append(' ');
		if (name != null)
			sink.append(name).append(' ');
		sink.append(' ');
		if (getNext() != null)
			getNext().toString(sink);
		return sink;
	}
	public Node getName() {
		return name;
	}
	@Override
	public int getInnerNodesCount() {
		return getNext() != null ? 2 : 1;
	}
	@Override
	public Node getInnerNode(int n) {
		return n == 0 ? name : getNext();
	}
}
