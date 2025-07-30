package com.f1.utils.string.node;

import com.f1.utils.SH;
import com.f1.utils.string.Node;

final public class ConstNode implements Node {

	public static final byte CODE = Node.CONST;
	private final Object value;

	public ConstNode(Object value) {
		this(-1, value);
	}

	public ConstNode(int position, Object value) {
		this.position = position;
		this.value = value;
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
		if (value == null) {
			return sink.append("null");
		} else if (value instanceof Long) {
			return sink.append(value).append('L');
		} else if (value instanceof Float) {
			return sink.append(value).append('D');
		} else if (value instanceof String) {
			sink.append('\"');
			return SH.escape(value.toString(), '"', '\\', sink).append('"');
		} else
			return sink.append(value);
	}

	public Object getValue() {
		return value;
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return 0;
	}

	@Override
	public Node getInnerNode(int n) {
		return null;
	}
}
