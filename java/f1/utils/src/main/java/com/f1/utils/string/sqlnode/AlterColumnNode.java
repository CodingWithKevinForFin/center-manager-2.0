package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.VariableNode;

final public class AlterColumnNode implements Node {

	private static final byte CODE = Node.ALTER;
	private final int position;
	private final int type;
	private final VariableNode colName;
	private final VariableNode newType;
	private final VariableNode newName;
	private final UseNode useOptions;
	private final VariableNode before;
	private final Node expression;

	public AlterColumnNode(int position, int type, VariableNode colName, VariableNode newName, VariableNode newType, VariableNode before, UseNode options, Node expression) {
		this.position = position;
		this.type = type;
		this.colName = colName;
		this.before = before;
		this.newName = newName;
		this.newType = newType;
		this.useOptions = options;
		this.expression = expression;

	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(SqlExpressionParser.toOperationString(type)).append(' ');
		switch (type) {
			case SqlExpressionParser.ID_ADD:
				sink.append(newName.getVarname());
				if (newType != null)
					sink.append(' ').append(newType.getVarname());
				if (expression != null)
					expression.toString(sink.append(' '));
				break;
			case SqlExpressionParser.ID_RENAME:
				sink.append(colName.getVarname()).append(" TO ").append(newName.getVarname());
				break;
			case SqlExpressionParser.ID_DROP:
				sink.append(colName.getVarname());
				break;
			case SqlExpressionParser.ID_MODIFY:
				sink.append(colName.getVarname()).append(" AS ").append(newName.getVarname());
				if (newType != null)
					return sink.append(' ').append(newType);
				break;
		}
		if (before != null)
			before.toString(sink.append(" BEFORE"));
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
	public VariableNode getColName() {
		return colName;
	}
	public VariableNode getNewType() {
		return newType;
	}
	public VariableNode getNewName() {
		return newName;
	}
	public UseNode getUseOptions() {
		return useOptions;
	}
	public VariableNode getBefore() {
		return before;
	}
	public Node getExpression() {
		return expression;
	}
	public int getType() {
		return type;
	}
	@Override
	public byte getNodeCode() {
		return CODE;
	}
	@Override
	public int getInnerNodesCount() {
		int n = 0;
		if (colName != null)
			n++;
		if (newType != null)
			n++;
		if (newName != null)
			n++;
		if (useOptions != null)
			n++;
		if (before != null)
			n++;
		if (expression != null)
			n++;
		return n;
	}
	@Override
	public Node getInnerNode(int n) {
		if (colName != null && n-- == 0)
			return colName;
		if (newType != null && n-- == 0)
			return newType;
		if (newName != null && n-- == 0)
			return newName;
		if (useOptions != null && n-- == 0)
			return useOptions;
		if (before != null && n-- == 0)
			return before;
		if (expression != null && n-- == 0)
			return expression;
		throw new IndexOutOfBoundsException();
	}
}
