package com.f1.utils.string.node;

import com.f1.utils.SH;
import com.f1.utils.string.Node;

final public class MapNode implements Node {

	public static final byte CODE = Node.MAP;
	final private int position;
	final private Node[] params;

	public MapNode(int position, Node[] params) {
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

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return this.params.length;
	}

	@Override
	public Node getInnerNode(int n) {
		return this.params[n];
	}
}
