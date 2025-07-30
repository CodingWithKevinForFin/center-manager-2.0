package com.f1.utils.string.sqlnode;

import com.f1.utils.string.SqlExpressionParser;

final public class ExecuteNode extends SqlNode {
	private final String value;

	public ExecuteNode(int pos, String string) {
		super(pos, null, SqlExpressionParser.ID_EXECUTE);
		this.value = string;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(" EXECUTE ").append(value);
	}

	public String getValue() {
		return value;
	}
}
