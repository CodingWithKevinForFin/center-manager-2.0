package com.f1.utils.string.sqlnode;

import com.f1.utils.AH;
import com.f1.utils.SH;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;

public class SqlColumnsNode extends SqlNode {
	private final Node[] columns;

	public SqlColumnsNode(int position, Node[] columns, Node next, int operation) {
		super(position, next, operation);
		this.columns = columns;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(SqlExpressionParser.toOperationString(getOperation()));
		if (columns != null)
			SH.join(',', columns, sink.append(' '));
		if (getNext() != null)
			getNext().toString(sink.append(' '));
		return sink;
	}

	public int getEndPosition() {
		if (AH.isntEmpty(columns)) {
			Node last = AH.last(columns);
			if (last instanceof SqlColumnsNode)
				return ((SqlColumnsNode) last).getEndPosition();
			else if (last != null)
				return last.getPosition();
		}
		if (getOperation() > 0)
			return this.getPosition() + SqlExpressionParser.toOperationString(this.getOperation()).length();
		else
			return this.getPosition();
	}

	final public int getColumnsCount() {
		return this.columns == null ? 0 : columns.length;
	}
	final public Node getColumnAt(int n) {
		return this.columns[n];
	}

	public void setColumnAt(int i, Node n) {
		this.columns[i] = n;
	}

	public Node[] getColumns() {
		return columns;
	}

	@Override
	public int getInnerNodesCount() {
		int n = 0;
		if (columns != null)
			n += columns.length;
		if (getNext() != null)
			n++;
		return n;
	}

	@Override
	public Node getInnerNode(int n) {
		if (columns != null && n < columns.length)
			return columns[n];
		return getNext();
	}

}
