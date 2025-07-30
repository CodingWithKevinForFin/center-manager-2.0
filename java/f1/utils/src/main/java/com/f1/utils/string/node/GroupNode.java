package com.f1.utils.string.node;

import java.util.List;

import com.f1.utils.SH;
import com.f1.utils.string.Node;

final public class GroupNode implements Node {

	private static final byte CODE = Node.GROUP;
	private final List<Node> params;

	public GroupNode(int position, List<Node> params) {
		this.position = position;
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
		sink.append('(');
		SH.join(',', params, sink);
		return sink.append(')');
	}

	public int getParamsCount() {
		return this.params.size();
	}

	public Node getParamAt(int n) {
		return this.params.get(n);
	}
	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return this.params.size();
	}

	@Override
	public Node getInnerNode(int n) {
		return this.params.get(n);
	}
}
