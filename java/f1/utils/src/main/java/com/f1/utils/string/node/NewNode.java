package com.f1.utils.string.node;

import java.util.List;

import com.f1.utils.SH;
import com.f1.utils.string.Node;

final public class NewNode implements Node {

	public static final byte CODE = Node.NEW;
	private final String className;
	private final List<Node> params;
	private final ArrayNode arrayNode;
	private final List<Node> dimensions;

	public NewNode(int position, String className, ArrayNode arrayNode, List<Node> dimensions) {
		this.position = position;
		this.className = className;
		this.arrayNode = arrayNode;
		this.dimensions = dimensions;
		this.params = null;
	}

	public NewNode(int position, String className, List<Node> params) {
		this.position = position;
		this.className = className;
		this.params = params;
		this.arrayNode = null;
		this.dimensions = null;
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
		sink.append("new ").append(className);
		if (params == null) {
			sink.append('[');
			SH.join("][", dimensions, sink);
			sink.append(']');
		} else {
			sink.append('(');
			SH.join(",", params, sink);
			sink.append(')');
		}
		return sink;
	}

	public String getClassName() {
		return className;
	}

	public ArrayNode getArrayNode() {
		return arrayNode;
	}

	public int getParamsCount() {
		return params.size();
	}

	public Node getParamAt(int n) {
		return this.params.get(n);
	}
	public int getDimensionsCount() {
		return dimensions == null ? 0 : dimensions.size();
	}

	public Node getDimensionAt(int n) {
		return this.dimensions.get(n);
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		int n = 0;
		if (params != null)
			n = params.size();
		if (arrayNode != null)
			n++;
		if (dimensions != null)
			n += dimensions.size();
		return n;
	}

	@Override
	public Node getInnerNode(int n) {
		if (params != null) {
			if (n < params.size())
				return params.get(n);
			n -= params.size();
		}
		if (arrayNode != null && n-- == 0)
			return arrayNode;
		return this.dimensions.get(n);
	}
}
