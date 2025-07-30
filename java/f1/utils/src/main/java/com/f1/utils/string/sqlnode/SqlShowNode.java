package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.VariableNode;

public class SqlShowNode extends SqlNode {

	private final int scope;
	private final String target;
	private final VariableNode name;
	private final VariableNode from;
	private final boolean isFull;
	private final MethodNode methodSignature;//add field methodSignature for the case of "SHOW METHOD func(arg,..)"

	public SqlShowNode(int position, String target, int scope, boolean isFull, VariableNode name, VariableNode from, Node next) {
		super(position, next, SqlExpressionParser.ID_SHOW);
		this.target = target;
		this.scope = scope;
		this.name = name;
		this.from = from;
		this.setNext(next);
		this.isFull = isFull;
		this.methodSignature = null; //add init field
	}

	//new constructor for init methodSignature
	public SqlShowNode(int position, String target, int scope, boolean isFull, MethodNode methodSignature, VariableNode from, Node next) {
		super(position, next, SqlExpressionParser.ID_SHOW);
		this.target = target;
		this.scope = scope;
		this.name = null;
		this.from = from;
		this.setNext(next);
		this.isFull = isFull;
		this.methodSignature = methodSignature; //add init field
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(SqlExpressionParser.toOperationString(getOperation()));
		if (isFull)
			sink.append(" FULL");
		sink.append(' ').append(target);
		if (name != null)
			sink.append(' ').append(name);
		if (from != null)
			sink.append(" FROM ").append(from);
		if (getNext() != null)
			getNext().toString(sink.append(' '));
		return sink;
	}

	public MethodNode getMethodSignature() {
		return methodSignature;
	}

	public int getScope() {
		return scope;
	}

	public String getTarget() {
		return target;
	}

	public VariableNode getName() {
		return name;
	}

	public VariableNode getFrom() {
		return from;
	}

	public boolean isFull() {
		return isFull;
	}

	@Override
	public int getInnerNodesCount() {
		int n = 0;
		if (name != null)
			n++;
		if (from != null)
			n++;
		if (methodSignature != null)
			n++;
		if (getNext() != null)
			n++;
		return n;
	}

	@Override
	public Node getInnerNode(int n) {
		if (name != null && n-- == 0)
			return name;
		if (from != null && n-- == 0)
			return from;
		if (methodSignature != null && n-- == 0)
			return methodSignature;
		return getNext();
	}
}
