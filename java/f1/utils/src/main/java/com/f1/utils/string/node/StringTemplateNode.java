package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class StringTemplateNode implements Node {

	public static final char NO_ESCAPE = 0;
	public static final byte CODE = Node.STRING_TEMPLATE;
	private StringTemplateNode next;
	final private int position;
	final private Node injectionNode;
	final private char quoteChar;
	final private boolean isNested;

	public StringTemplateNode(int position, AppendNode node, boolean isNested) {
		this.position = position;
		this.injectionNode = node;
		this.quoteChar = NO_ESCAPE;
		this.isNested = isNested;
	}

	public StringTemplateNode(int position, Node node, char quoteChar, boolean isNested) {
		this.position = position;
		this.quoteChar = quoteChar;
		this.injectionNode = node;
		this.isNested = isNested;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (quoteChar != NO_ESCAPE)
			sink.append(quoteChar);
		this.injectionNode.toString(sink);
		if (quoteChar != NO_ESCAPE)
			sink.append(quoteChar);
		if (next != null)
			next.toString(sink);
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

	public StringTemplateNode getNext() {
		return this.next;
	}

	public void setNext(StringTemplateNode next) {
		this.next = next;
	}
	public boolean isInjectionVar() {
		return injectionNode != null;
	}
	public Node getInjectionNode() {
		return injectionNode;
	}

	public char getQuoteChar() {
		return this.quoteChar;
	}

	public boolean getIsNested() {
		return this.isNested;
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		int n = 0;
		if (this.next != null)
			n++;
		if (this.injectionNode != null)
			n++;
		return n;
	}

	@Override
	public Node getInnerNode(int n) {
		if (this.next != null && n == 0)
			return this.next;
		return this.injectionNode;
	}
}
