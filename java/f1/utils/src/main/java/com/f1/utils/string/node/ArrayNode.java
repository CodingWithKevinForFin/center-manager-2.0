package com.f1.utils.string.node;

import java.util.List;

import com.f1.utils.SH;
import com.f1.utils.string.Node;

final public class ArrayNode implements Node {

	public static final byte CODE = Node.ARRAY;
	final private int position;
	final private List<Node> params;

	public ArrayNode(int position, List<Node> params) {
		this.position = position;
		this.params = params;
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
		sink.append('{');
		SH.join(',', params, sink);
		sink.append('}');
		return sink;
	}

	public int getParamsCount() {
		return params.size();
	}
	public Node getParamAt(int n) {
		return params.get(n);
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
