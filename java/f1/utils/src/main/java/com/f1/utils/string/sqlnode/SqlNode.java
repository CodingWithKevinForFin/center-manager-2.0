package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;

public class SqlNode implements Node {
	public static final byte CODE = Node.SQL;
	private Node next;
	private final int operation;
	final private int position;

	public SqlNode(int position, Node next, int operation) {
		this.next = next;
		this.operation = operation;
		this.position = position;
	}
	@Override
	final public int getPosition() {
		return position;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(SqlExpressionParser.toOperationString(operation));
		if (next != null)
			next.toString(sink.append(' '));
		return sink;
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	final public Node getNext() {
		return next;
	}
	final public void setNext(Node next) {
		this.next = next;
	}
	final public int getOperation() {
		return operation;
	}
	@Override
	public byte getNodeCode() {
		return CODE;
	}
	@Override
	public int getInnerNodesCount() {
		return next == null ? 0 : 1;
	}
	@Override
	public Node getInnerNode(int n) {
		return this.next;
	}
}
