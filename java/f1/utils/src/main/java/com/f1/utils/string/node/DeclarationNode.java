package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class DeclarationNode implements Node {

	public static final byte CODE = Node.DECLARATION;
	private final String vartype;
	private final String varname;
	private final Node param;
	private final int position;
	private DeclarationNode next;

	public DeclarationNode(int position, String vartype, String varname, Node param) {
		this.position = position;
		this.vartype = vartype;
		this.varname = varname;
		this.param = param;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (param != null) {
			sink.append(vartype).append(' ');
			param.toString(sink);
		} else {
			sink.append(vartype).append(' ').append(varname);
		}
		for (DeclarationNode n = next; n != null; n = n.next) {
			sink.append(',');
			if (n.param != null) {
				n.param.toString(sink);
			} else {
				sink.append(n.varname);
			}
		}
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

	public String getVartype() {
		return vartype;
	}

	public String getVarname() {
		return varname;
	}

	public Node getParam() {
		return param;
	}

	public DeclarationNode getNext() {
		return next;
	}

	public void setNext(DeclarationNode next) {
		this.next = next;
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return this.next == null ? 1 : 2;
	}

	@Override
	public Node getInnerNode(int n) {
		return n == 0 ? this.param : this.next;
	}
}
