package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class AppendNode implements Node {

	public static final byte CODE = Node.APPEND;
	final private String text;
	final private int position;
	final private char quoteChar;

	public AppendNode(int position, String text, char quoteChar) {
		this.position = position;
		this.text = text;
		this.quoteChar = quoteChar;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (quoteChar != StringTemplateNode.NO_ESCAPE)
			sink.append(quoteChar);
		sink.append(text);
		if (quoteChar != StringTemplateNode.NO_ESCAPE)
			sink.append(quoteChar);
		return sink;
	}

	@Override
	public String toString() {
		return this.toString(new StringBuilder()).toString();
	}

	@Override
	public int getPosition() {
		return this.position;
	}

	public String getText() {
		return text;
	}

	public char getQuoteChar() {
		return this.quoteChar;
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
