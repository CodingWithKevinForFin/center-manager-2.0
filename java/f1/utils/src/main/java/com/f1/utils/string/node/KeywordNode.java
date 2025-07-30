package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class KeywordNode implements Node {

	private static final byte CODE = Node.KEYWORD;
	private final String keyword;
	final private int position;

	public KeywordNode(String keyword) {
		this.keyword = keyword;
		this.position = -1;
	}

	public KeywordNode(int position, String keyword) {
		this.keyword = keyword;
		this.position = position;
	}

	@Override
	public String toString() {
		return keyword;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(keyword);
	}

	public String getKeyword() {
		return keyword;
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
