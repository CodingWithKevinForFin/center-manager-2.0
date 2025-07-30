package com.f1.utils.string.node;

import com.f1.utils.SH;
import com.f1.utils.string.Node;

final public class ExternNode implements Node {

	public static final byte CODE = Node.EXTERN;
	final private int position;
	final private VariableNode languageName;
	final private int codePosition;
	final private String code;
	final private int bracketsCount;

	public ExternNode(int position, VariableNode name, int bracketsCount, int codePosition, String code) {
		this.position = position;
		this.languageName = name;
		this.bracketsCount = bracketsCount;
		this.codePosition = codePosition;
		this.code = code;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("extern ");
		languageName.toString(sink);
		SH.repeat('{', bracketsCount, sink);
		sink.append(code);
		SH.repeat('}', bracketsCount, sink);
		return sink;
	}

	public VariableNode getLanguageName() {
		return languageName;
	}

	public int getCodePosition() {
		return codePosition;
	}

	public String getCode() {
		return code;
	}

	public int getBracketsCount() {
		return bracketsCount;
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return 1;
	}

	@Override
	public Node getInnerNode(int n) {
		return this.languageName;
	}

}
