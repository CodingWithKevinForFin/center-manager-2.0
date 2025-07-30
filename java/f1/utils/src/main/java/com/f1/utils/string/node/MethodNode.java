package com.f1.utils.string.node;

import java.util.List;

import com.f1.utils.SH;
import com.f1.utils.string.Node;

final public class MethodNode implements Node {
	public static final byte CODE = Node.METHOD;

	private static final Node[] EMPTY = new Node[0];
	private final String methodName;
	private final List<Node> params;

	public MethodNode(int position, String methodName, List<Node> params) {
		this.position = position;
		this.methodName = methodName;
		this.params = params;
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
		if (params == null)
			return sink.append(methodName).append("()");
		sink.append(methodName).append('(');
		SH.join(',', params, sink);
		sink.append(')');
		return sink;
	}

	public String getMethodName() {
		return methodName;
	}

	public int getParamsCount() {
		return params == null ? 0 : params.size();
	}

	public Node getParamAt(int n) {
		return this.params.get(n);
	}

	public Node[] getParamsToArray() {
		if (this.params == null || this.params.size() == 0)
			return EMPTY;
		return this.params.toArray(new Node[this.params.size()]);
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return getParamsCount();
	}

	@Override
	public Node getInnerNode(int n) {
		return this.params.get(n);
	}
}
