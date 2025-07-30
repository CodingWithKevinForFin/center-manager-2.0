package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;

public class SqlUnionNode extends SqlNode {

	private final boolean byName;

	public SqlUnionNode(int position, Node next, boolean byName) {
		super(position, next, SqlExpressionParser.ID_UNION);
		this.byName = byName;
	}

	public boolean isByName() {
		return byName;
	}

}
