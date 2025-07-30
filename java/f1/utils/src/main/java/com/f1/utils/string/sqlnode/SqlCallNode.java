package com.f1.utils.string.sqlnode;

import com.f1.utils.SH;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;

final public class SqlCallNode extends SqlNode {
	private final String methodName;
	private final Node[] params;

	public SqlCallNode(int position, String methodName, Node[] nodes, Node next) {
		super(position, next, SqlExpressionParser.ID_CALL);
		this.methodName = methodName;
		this.params = nodes;
	}
	public String getMethodName() {
		return this.methodName;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (params == null)
			return sink.append(methodName).append("()");
		sink.append(methodName).append('(');
		SH.join(',', params, sink);
		sink.append(')');
		if (getNext() != null)
			getNext().toString(sink.append(' '));
		return sink;
	}

	public int getParamsCount() {
		return this.params == null ? 0 : this.params.length;
	}

	public Node getParamAt(int n) {
		return this.params[n];
	}
	@Override
	public int getInnerNodesCount() {
		return getNext() == null ? params.length : params.length + 1;
	}
	@Override
	public Node getInnerNode(int n) {
		if (n < params.length)
			return params[n];
		return getNext();
	}
}
