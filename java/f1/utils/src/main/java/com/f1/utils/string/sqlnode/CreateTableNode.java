package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;

public class CreateTableNode extends SqlNode {

	final private AdminNode[] tableDefs;

	public CreateTableNode(int position, AdminNode[] createTables, Node next) {
		super(position, next, SqlExpressionParser.ID_CREATE);
		this.tableDefs = createTables;
	}

	public AdminNode[] getTableDefs() {
		return this.tableDefs;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		boolean first = true;
		for (AdminNode i : tableDefs) {
			if (first)
				first = false;
			else
				sink.append(", ");
			i.toString(sink);
		}
		return sink;
	}

	@Override
	public int getInnerNodesCount() {
		return getNext() == null ? tableDefs.length : (tableDefs.length + 1);
	}

	@Override
	public Node getInnerNode(int n) {
		if (n < tableDefs.length)
			return tableDefs[n];
		return getNext();
	}
}
