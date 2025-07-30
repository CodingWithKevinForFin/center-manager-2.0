package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;

public class ValuesNode implements Node {

	private static final byte CODE = Node.VALUES;
	private final int position;
	private final Node[] values;
	private final int colCount;

	public ValuesNode(int position, int colCount, Node[] nodes) {
		this.position = position;
		this.values = nodes;
		this.colCount = colCount;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("(");
		for (int i = 0; i < values.length; i++) {
			if (i > 0)
				sink.append(i % colCount == 0 ? "), (" : ", ");
			values[i].toString(sink);
		}
		sink.append(")");
		return sink;
	}

	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public int getColCount() {
		return colCount;
	}

	public int getValuesCount() {
		return this.values == null ? 0 : this.values.length;
	}

	public Node getValueAt(int n) {
		return this.values[n];
	}
	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return values.length;
	}

	@Override
	public Node getInnerNode(int n) {
		return values[n];
	}
}
