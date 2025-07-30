package com.f1.utils.string.sqlnode;

import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;

final public class AdminNode extends SqlNode {
	public static final byte NO_IF = 0;
	public static final byte IF_EXISTS = 1;
	public static final byte IF_NOT_EXISTS = 2;
	private final int targetType;
	private final UseNode useNode;
	private final SqlNode options;
	private byte ifCondition;

	public AdminNode(int position, int type, int targetType, Node next, UseNode useNode) {
		this(position, type, targetType, next, useNode, null);
	}
	public AdminNode(int position, int type, int targetType, Node next, UseNode useNode, SqlNode options) {
		super(position, next, type);
		this.useNode = useNode;
		this.targetType = targetType;
		this.options = options;
		if (useNode != null && useNode.getNext() != null)
			throw new ExpressionParserException(useNode.getNext().getPosition(), "Trailing text after USE clause");
	}

	public SqlNode getOptions() {
		return options;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(SqlExpressionParser.toOperationString(getOperation()));
		sink.append(' ');
		for (SqlNode n = options; n != null; n = (SqlNode) n.getNext()) {
			n.toString(sink);
			sink.append(' ');
		}
		sink.append(SqlExpressionParser.toOperationString(targetType));
		if (this.ifCondition == IF_EXISTS)
			sink.append(" IF EXISTS");
		else if (this.ifCondition == IF_NOT_EXISTS)
			sink.append(" IF NOT EXISTS");
		if (getNext() != null)
			getNext().toString(sink.append(' '));
		if (useNode != null)
			useNode.toString(sink.append(' '));
		return sink;
	}
	public void setIfCondition(byte ifExists) {
		this.ifCondition = ifExists;
	}

	public byte getIfType() {
		return this.ifCondition;
	}
	public boolean getIfNotExistsOrThrow() {
		switch (this.ifCondition) {
			case NO_IF:
				return false;
			case IF_NOT_EXISTS:
				return true;
			default:
				throw new ExpressionParserException(getPosition(), "OPERATION NOT SUPPORTED: 'IF EXISTS'");
		}
	}
	public boolean getIfExistsOrThrow() {
		switch (this.ifCondition) {
			case NO_IF:
				return false;
			case IF_EXISTS:
				return true;
			default:
				throw new ExpressionParserException(getPosition(), "OPERATION NOT SUPPORTED: 'IF NOT EXISTS'");
		}
	}
	public int getTargetType() {
		return targetType;
	}
	public UseNode getUseNode() {
		return useNode;
	}
	@Override
	public int getInnerNodesCount() {
		int n = 0;
		if (useNode != null)
			n++;
		if (options != null)
			n++;
		if (getNext() != null)
			n++;
		return n;
	}
	@Override
	public Node getInnerNode(int n) {
		if (useNode != null && n-- == 0)
			return useNode;
		if (options != null && n-- == 0)
			return options;
		return getNext();
	}
}
